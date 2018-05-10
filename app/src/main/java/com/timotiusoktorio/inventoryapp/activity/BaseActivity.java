package com.timotiusoktorio.inventoryapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.timotiusoktorio.inventoryapp.api.Endpoints;
import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Retrofit;

/**
 * Created by issy on 11/14/17.
 */

public class BaseActivity extends AppCompatActivity {

    public static Typeface  Avenir_Light;

    public static final String LOCALE_KEY = "localekey";
    public static final String LOCALE_PREF_KEY = "localePref";
    public static final String ENGLISH_LOCALE = "en";
    public static final String SWAHILI_LOCALE = "sw";
    public Retrofit retrofit;
    public Endpoints apiEndpoints;
    public static AppDatabase baseDatabase;
    public Locale locale;
    public static SharedPreferences localeSp;
    // Session Manager Class
    public static SessionManager session;
    final public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static String localeString = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localeSp = getSharedPreferences(LOCALE_PREF_KEY, MODE_PRIVATE);

        localeString = localeSp.getString(LOCALE_KEY, SWAHILI_LOCALE);
        Log.d("language", "From SP : "+localeString);
        Configuration config = getBaseContext().getResources().getConfiguration();
        if (! "".equals(localeString) && ! config.locale.getLanguage().equals(localeString)) {
            Locale locale = new Locale(localeString);
            Locale.setDefault(locale);
            config.locale = locale;
            Log.d("language", "Setting Swahili locale");
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        // Session class instance
        session = new SessionManager(getApplicationContext());
        baseDatabase = AppDatabase.getDatabase(this);

    }

    public static String getLocaleString(){
        return localeString;
    }



}
