package com.softmed.stockapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.dto.CategoryProducts;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Services.PostOfficeService;
import com.softmed.stockapp.Utils.ServiceGenerator;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.api.Endpoints;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ManagedProductsActivity extends AppCompatActivity {

    private static final String TAG = ManagedProductsActivity.class.getSimpleName();
    private AppDatabase baseDatabase;
    private LinearLayout productsLayout;
    private List<Product> managedProductIds = new ArrayList<>();
    private List<Balances> currentBalances = new ArrayList<>();
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managed_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Choose Products Managed by the Facility");

        // Session Manager
        session = new SessionManager(getApplicationContext());

        Typeface rosarioRegular = ResourcesCompat.getFont(this, R.font.rosario_regular);
        Typeface robotoRegular = ResourcesCompat.getFont(this, R.font.roboto_regular);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);
        collapsingToolbar.setCollapsedTitleTypeface(robotoRegular);
        collapsingToolbar.setExpandedTitleTypeface(rosarioRegular);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                final Endpoints.TransactionServices transactionServices = ServiceGenerator.createService(Endpoints.TransactionServices.class,
                        session.getUserName(),
                        session.getUserPass());

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
                        for(Balances balance:newMappings){
                            balance.setSyncStatus(1);
                            baseDatabase.balanceModelDao().addBalance(balance);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                    }
                }.execute();


                Call postBalanceCall = transactionServices.postBalances(PostOfficeService.getRequestBody(newMappings));
                postBalanceCall.enqueue(new Callback() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onResponse(Call call, Response response) {
                        //Store Received Patient Information, TbPatient as well as PatientAppointments
                        if (response.code() == 200 || response.code() == 201) {
                            Log.d(TAG, "Successful saved product mappings responses " + response.body());

                            transactionServices.getSchedule().enqueue(new Callback<List<ProductReportingSchedule>>() {
                                @Override
                                public void onResponse(Call<List<ProductReportingSchedule>> call, final Response<List<ProductReportingSchedule>> response) {

                                    if (response.body() != null) {
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... voids) {
                                                for (ProductReportingSchedule reportingSchedule : response.body()) {
                                                    baseDatabase.productReportingScheduleModelDao().addProductSchedule(reportingSchedule);
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
                                    }else{
                                        Log.d(TAG, "Error obtaining product reporting schedule " + call.request().url());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<ProductReportingSchedule>> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        } else {
                            Log.d(TAG, "Balance Responce Call URL " + call.request().url());
                            Log.d(TAG, "Balance Responce Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        t.printStackTrace();
                        Log.d(TAG, "PostOfficeService Error = " + t.getMessage());
                        Log.d(TAG, "PostOfficeService CALL URL = " + call.request().url());
                        Log.d(TAG, "PostOfficeService CALL Header = " + call.request().header("Authorization"));
                    }
                });

            }
        });

        productsLayout = findViewById(R.id.products_categories);
        baseDatabase = AppDatabase.getDatabase(this);
        getCategories();
    }

    private void getCategories() {
        new getProductsAsyncTask().executeOnExecutor(THREAD_POOL_EXECUTOR);
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

            currentBalances = baseDatabase.balanceModelDao().getAllBalances();
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
