package com.softmed.rucodia.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.softmed.rucodia.Services.PostOfficeService;


public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PostOfficeService.class);
        //i.putExtra("foo", "bar");
        try {
            context.startService(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
