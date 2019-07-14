package com.softmed.stockapp.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.softmed.stockapp.dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp.api.Endpoints;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Retrofit;

/**
 * Created by issy on 11/14/17.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

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

    public static ProductReportingSchedule getProductReportingSchedule(ProductReportingScheduleResponse productReportingScheduleResponse){
        ProductReportingSchedule reportingSchedule = new ProductReportingSchedule();
        reportingSchedule.setId(productReportingScheduleResponse.getId());
        reportingSchedule.setFacilityId(productReportingScheduleResponse.getFacilityId());
        reportingSchedule.setProductId(productReportingScheduleResponse.getProductId());
        reportingSchedule.setStatus(productReportingScheduleResponse.getStatus());


        Date scheduledDate= null;
        try {
            Log.d(TAG,"received schedule = "+productReportingScheduleResponse.getScheduledDate());
            scheduledDate = new SimpleDateFormat("yyyy-MM-dd").parse(productReportingScheduleResponse.getScheduledDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        reportingSchedule.setScheduledDate(scheduledDate);

        return reportingSchedule;
    }


}
