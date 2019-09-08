package com.softmed.stockapp_staging.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.softmed.stockapp_staging.activities.LoginActivity;
import com.softmed.stockapp_staging.api.Endpoints;
import com.softmed.stockapp_staging.database.AppDatabase;
import com.softmed.stockapp_staging.dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp_staging.utils.ServiceGenerator;
import com.softmed.stockapp_staging.utils.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class GetSchedulesWorker extends Worker {
    private static final String TAG = GetSchedulesWorker.class.getSimpleName();


    public GetSchedulesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return getPostingSchedule();
    }


    private Result getPostingSchedule() {
        Log.d(TAG, "Get Schedule Worker working");
        SessionManager session = new SessionManager(this.getApplicationContext());
        Endpoints.TransactionServices transactionServices = ServiceGenerator.createService(Endpoints.TransactionServices.class,
                session.getUserName(),
                session.getUserPass());

        AppDatabase database = AppDatabase.getDatabase(this.getApplicationContext());
        Call<List<ProductReportingScheduleResponse>> call = transactionServices.getSchedule();

        Response<List<ProductReportingScheduleResponse>> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {
                Log.d(TAG, "Successful Posting schedule received " + new Gson().toJson(response.body()));
                for (ProductReportingScheduleResponse reportingSchedule : response.body()) {
                    try {
                        if (database.productReportingScheduleModelDao().getProductReportingScheduleById(reportingSchedule.getId()).getStatus().equalsIgnoreCase("posted")) {
                            reportingSchedule.setStatus("posted");
                        }

                        database.productReportingScheduleModelDao().addProductSchedule(LoginActivity.getProductReportingSchedule(reportingSchedule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return Result.success();
            } else {
                Log.d(TAG, "Getting posting schedule failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.retry();
        }


    }
}