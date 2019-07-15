package com.softmed.stockapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.softmed.stockapp.R;
import com.softmed.stockapp.fragments.ContactChooserFragment;

public class ContactChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_chooser_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ContactChooserFragment.newInstance())
                    .commitNow();
        }
    }
}
