package com.softmed.stockapp_staging.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.softmed.stockapp_staging.api.Endpoints;
import com.softmed.stockapp_staging.utils.ServiceGenerator;
import com.softmed.stockapp_staging.utils.SessionManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class SendFCMTokenWorker extends Worker {
    private static final String TAG = SendFCMTokenWorker.class.getSimpleName();


    public SendFCMTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return sendFCM(getInputData().getString("token"));
    }


    private Result sendFCM(String token) {
        Log.d(TAG, "Send FCM Token Worker working");
        SessionManager sess = new SessionManager(this.getApplicationContext());
        Endpoints.NotificationServices notificationServices = ServiceGenerator.createService(Endpoints.NotificationServices.class, sess.getUserName(), sess.getUserPass());


        String datastream = "";
        JSONObject object = new JSONObject();
        RequestBody body;

        try {
            object.put("user", sess.getUserUUID());
            object.put("reg_id", token);

            datastream = object.toString();
            Log.d("FCMService", "data " + datastream);

            body = RequestBody.create(MediaType.parse("application/json"), datastream);

        } catch (Exception e) {
            e.printStackTrace();
            body = RequestBody.create(MediaType.parse("application/json"), datastream);
        }
        Call updateFCMCall = notificationServices.registerDevice(body);
        Response response = null;
        try {
            response = updateFCMCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {

                Log.d(TAG, "updated FCM Successful ");

                return Result.success();

            } else {
                Log.d(TAG, "Sending FCM Failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.retry();
        }

    }
}