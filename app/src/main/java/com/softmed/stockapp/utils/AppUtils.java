package com.softmed.stockapp.utils;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/*
 * Created by troy379 on 04.04.17.
 */
public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();

    public static void showToast(Context context, @StringRes int text, boolean isLong) {
        showToast(context, context.getString(text), isLong);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static RequestBody getRequestBody(Object object) {

        RequestBody body;
        String datastream = "";

        try {
            datastream = new Gson().toJson(object);

            Log.d(TAG, "Serialized Object = " + datastream);

            body = RequestBody.create(MediaType.parse("application/json"), datastream);

        } catch (Exception e) {
            e.printStackTrace();
            body = RequestBody.create(MediaType.parse("application/json"), datastream);
        }

        return body;

    }
}