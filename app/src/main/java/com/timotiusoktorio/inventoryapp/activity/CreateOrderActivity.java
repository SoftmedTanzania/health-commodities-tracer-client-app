package com.timotiusoktorio.inventoryapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.timotiusoktorio.inventoryapp.R;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class CreateOrderActivity extends AppCompatActivity {
    private MaterialSpinner supplierSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getActionBar().setTitle("Create an Order");


        //TODO obtain supplier list from the database
        List<String> suppliers = new ArrayList<>();
        suppliers.add("Supplier 1");

        supplierSpinner = (MaterialSpinner)findViewById(R.id.spin_suppliers);
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, suppliers);
        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
        supplierSpinner.setAdapter(spinAdapter);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
