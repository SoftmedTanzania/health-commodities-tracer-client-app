package com.softmed.stockapp.Activities;

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
import com.softmed.stockapp.R;
import com.softmed.stockapp.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ManagedProductsActivity extends AppCompatActivity {

    private static final String TAG = ManagedProductsActivity.class.getSimpleName();
    private AppDatabase baseDatabase;
    private LinearLayout productsLayout;
    private List<Product> managedProductIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managed_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Choose Products Managed by the Facility");

        Typeface rosarioRegular = ResourcesCompat.getFont(this, R.font.rosario_regular);
        Typeface robotoRegular = ResourcesCompat.getFont(this, R.font.roboto_regular);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);
        collapsingToolbar.setCollapsedTitleTypeface(robotoRegular);
        collapsingToolbar.setExpandedTitleTypeface(rosarioRegular);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {

                        Log.d(TAG, "balances = " + new Gson().toJson(baseDatabase.balanceModelDao().getBalances()));


                        for (Product product : managedProductIds) {

                            Log.d(TAG, "Adding balance = " + product.getName());
                            Balances balance = new Balances();

                            balance.setProduct_id(product.getId());
                            balance.setBalance(0);

                            Log.d(TAG, "Saving balance = " + new Gson().toJson(balance));
                            baseDatabase.balanceModelDao().addBalance(balance);
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
                        startActivity(intent);
                        ManagedProductsActivity.this.finish();
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

    class getProductsAsyncTask extends AsyncTask<Void, Void, List<CategoryProducts>> {

        List<Balances> balances = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<CategoryProducts> doInBackground(Void... voids) {

            List<CategoryProducts> categoryProductsList = new ArrayList<>();
            List<Category> categories = baseDatabase.categoriesModel().getAllCategories();
            Log.d(TAG, "all categories = " + new Gson().toJson(categories));

            List<Product> ps = baseDatabase.productsModelDao().getAllProducts();

            Log.d(TAG, "categories size = " + categories.size());
            for (Category category : categories) {
                Log.d(TAG, "category name = " + category.getName());

                CategoryProducts categoryProducts = new CategoryProducts();
                categoryProducts.setCategory(category);

                List<Product> products = baseDatabase.productsModelDao().getProductsByCategoryId(category.getId());
                categoryProducts.setProducts(products);

                categoryProductsList.add(categoryProducts);
            }

            balances = baseDatabase.balanceModelDao().getAllBalances();
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
                    final CheckBox managedProduct = ((CheckBox) v.findViewById(R.id.checkbox));
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

                    for(Balances b:balances){
                        if(b.getProduct_id()==product.getId()){
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
