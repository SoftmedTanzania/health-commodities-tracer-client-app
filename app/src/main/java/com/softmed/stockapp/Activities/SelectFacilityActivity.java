package com.softmed.stockapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.dto.CategoryProducts;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Location;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Services.PostOfficeService;
import com.softmed.stockapp.Utils.ServiceGenerator;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.api.Endpoints;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class SelectFacilityActivity extends BaseActivity {

    private static final String TAG = SelectFacilityActivity.class.getSimpleName();
    private AppDatabase baseDatabase;
    private LinearLayout productsLayout;
    private List<Product> managedProductIds = new ArrayList<>();
    private List<Balances> currentBalances = new ArrayList<>();
    private SessionManager session;
    private Endpoints.TransactionServices transactionServices;
    private List<Location> facilityLocations;
    private int selectedLocationId = 0;

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


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saving the managed products", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                session.setFacilityId(selectedLocationId);

            }
        });

        productsLayout = findViewById(R.id.products_categories);
        baseDatabase = AppDatabase.getDatabase(this);
        getlocations();
    }

    private void getlocations() {
        new getLocationsAsyncTask().executeOnExecutor(THREAD_POOL_EXECUTOR);
    }


    @SuppressLint("StaticFieldLeak")
    class getLocationsAsyncTask extends AsyncTask<Void, Void, List<Location>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Location> doInBackground(Void... voids) {

            List<Location> locations = new ArrayList<>();
            facilityLocations = baseDatabase.locationModelDao().getLocationsByParentId(session.getFacilityId());


            for (Location location : facilityLocations) {
                Log.d(TAG, "facility name = " + location.getName());
                locations.add(location);
            }

            return locations;
        }

        @Override
        protected void onPostExecute(List<Location> locations) {
            super.onPostExecute(locations);

            View v = getLayoutInflater().inflate(R.layout.view_select_facility, null);


            List<String> locationNames = new ArrayList<>();
            for(Location location:locations){
                locationNames.add(location.getName());
            }


            MaterialSpinner locationSpinner = v.findViewById(R.id.select_health_facility);

            ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(SelectFacilityActivity.this, R.layout.simple_spinner_item_black, locationNames);
            spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
            locationSpinner.setAdapter(spinAdapter);

            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedLocationId = facilityLocations.get(i).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            productsLayout.addView(v);


        }
    }


}
