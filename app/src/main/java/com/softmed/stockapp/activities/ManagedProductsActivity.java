package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.api.Endpoints;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.CategoryProducts;
import com.softmed.stockapp.dom.entities.Balances;
import com.softmed.stockapp.dom.entities.Category;
import com.softmed.stockapp.dom.entities.Product;
import com.softmed.stockapp.dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp.utils.ServiceGenerator;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.workers.GetSchedulesWorker;
import com.softmed.stockapp.workers.SendBalancesWorker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ManagedProductsActivity extends BaseActivity {

    private static final String TAG = ManagedProductsActivity.class.getSimpleName();
    private AppDatabase baseDatabase;
    private LinearLayout productsLayout;
    private List<Product> managedProductIds = new ArrayList<>();
    private List<Balances> currentBalances = new ArrayList<>();
    private SessionManager session;
    private Endpoints.TransactionServices transactionServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managed_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Choose Products Managed by the Facility");

        // Session Manager
        session = new SessionManager(getApplicationContext());

        transactionServices = ServiceGenerator.createService(Endpoints.TransactionServices.class,
                session.getUserName(),
                session.getUserPass());

        Typeface rosarioRegular = ResourcesCompat.getFont(this, R.font.rosario_regular);
        Typeface robotoRegular = ResourcesCompat.getFont(this, R.font.roboto_regular);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);
        collapsingToolbar.setCollapsedTitleTypeface(robotoRegular);
        collapsingToolbar.setExpandedTitleTypeface(rosarioRegular);


        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saving the managed products", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                final List<Balances> newMappings = new ArrayList<>();
                for (Product product : managedProductIds) {
                    boolean productAlreadyMapped = false;
                    for (Balances b : currentBalances) {
                        if (b.getProductId() == product.getId()) {
                            productAlreadyMapped = true;
                        }
                    }
                    if (!productAlreadyMapped) {
                        Balances balance = new Balances();

                        balance.setProductId(product.getId());
                        balance.setBalance(0);
                        balance.setUserId(Integer.parseInt(session.getUserUUID()));
                        balance.setHealthFacilityId(session.getFacilityId());
                        balance.setSyncStatus(0);

                        newMappings.add(balance);

                    }
                }

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        for (Balances balance : newMappings) {
                            balance.setSyncStatus(0);
                            baseDatabase.balanceModelDao().addBalance(balance);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        if (newMappings.size() > 0) {
                            Log.d(TAG, "sending product mappings responses ");
                            // Create a Constraints object that defines when the task should run
                            Constraints networkConstraints = new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build();

                            OneTimeWorkRequest sendBalancesWork = new OneTimeWorkRequest.Builder(SendBalancesWorker.class)
                                    .setConstraints(networkConstraints)
                                    .build();


                            OneTimeWorkRequest getPostingSchedule = new OneTimeWorkRequest.Builder(GetSchedulesWorker.class)
                                    .setConstraints(networkConstraints)
                                    .build();


                            WorkManager.getInstance()
                                    .beginWith(sendBalancesWork)
                                    // Note: WorkManager.beginWith() returns a
                                    // WorkContinuation object; the following calls are
                                    // to WorkContinuation methods
                                    .then(getPostingSchedule)
                                    .enqueue();

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.setIsFirstLogin(false);

                            Intent intent = new Intent(ManagedProductsActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("reportInitialStock", true);
                            startActivity(intent);
                            ManagedProductsActivity.this.finish();

                        } else {
                            getReportingSchedules();
                        }
                    }
                }.execute();
            }
        });

        productsLayout = findViewById(R.id.products_categories);
        baseDatabase = AppDatabase.getDatabase(this);
        getCategories();
    }

    private void getCategories() {
        new getProductsAsyncTask().executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    private void getReportingSchedules() {
        Log.d(TAG, "Calling product schedules ");
        transactionServices.getSchedule().enqueue(new Callback<List<ProductReportingScheduleResponse>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<List<ProductReportingScheduleResponse>> call, final Response<List<ProductReportingScheduleResponse>> response) {
                Log.d(TAG, "Received schedule = " + new Gson().toJson(response.body()));
                if (response.body() != null) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            for (ProductReportingScheduleResponse reportingSchedule : response.body()) {
                                baseDatabase.productReportingScheduleModelDao().addProductSchedule(getProductReportingSchedule(reportingSchedule));
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.setIsFirstLogin(false);
                            //Call HomeActivity to log in user
                            Intent intent = new Intent(ManagedProductsActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("reportInitialStock", true);
                            startActivity(intent);
                            ManagedProductsActivity.this.finish();

                        }
                    }.execute();
                } else {
                    Log.d(TAG, "Error obtaining product reporting schedule " + call.request().url());
                }

            }

            @Override
            public void onFailure(Call<List<ProductReportingScheduleResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class getProductsAsyncTask extends AsyncTask<Void, Void, List<CategoryProducts>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<CategoryProducts> doInBackground(Void... voids) {

            List<CategoryProducts> categoryProductsList = new ArrayList<>();
            List<Category> categories = baseDatabase.categoriesModel().getAllCategories();
            Log.d(TAG, "all categories = " + new Gson().toJson(categories));
            Log.d(TAG, "categories size = " + categories.size());

            for (Category category : categories) {
                Log.d(TAG, "category name = " + category.getName());

                CategoryProducts categoryProducts = new CategoryProducts();
                categoryProducts.setCategory(category);

                List<Product> products = baseDatabase.productsModelDao().getProductsByCategoryId(category.getId());
                categoryProducts.setProducts(products);

                categoryProductsList.add(categoryProducts);
            }

            currentBalances = baseDatabase.balanceModelDao().getAllBalancesByFacility(session.getFacilityId());
            return categoryProductsList;
        }

        @Override
        protected void onPostExecute(List<CategoryProducts> categoryProductsList) {
            super.onPostExecute(categoryProductsList);


            for (CategoryProducts categoryProducts : categoryProductsList) {
                View productCategories = getLayoutInflater().inflate(R.layout.view_products_categories, null);
                TextView categoryName = productCategories.findViewById(R.id.category_name);
                categoryName.setText(categoryProducts.getCategory().getName());
                LinearLayout productsCheckBoxLayout = productCategories.findViewById(R.id.products);


                for (final Product product : categoryProducts.getProducts()) {
                    View v = getLayoutInflater().inflate(R.layout.view_checkbox, null);
                    final CheckBox managedProduct = v.findViewById(R.id.checkbox);
                    managedProduct.setText(product.getName());
                    managedProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                managedProductIds.add(product);
                            } else {
                                managedProductIds.remove(product);
                            }
                        }
                    });

                    for (Balances b : currentBalances) {
                        if (b.getProductId() == product.getId()) {
                            managedProduct.setChecked(true);
                            break;
                        }
                    }

                    Log.d(TAG, "Product Name = " + product.getName());
                    productsCheckBoxLayout.addView(v);
                }

                productsLayout.addView(productCategories);
            }


        }
    }


}
