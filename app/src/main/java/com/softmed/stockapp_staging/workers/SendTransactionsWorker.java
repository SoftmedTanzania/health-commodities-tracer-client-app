package com.softmed.stockapp_staging.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.softmed.stockapp_staging.api.Endpoints;
import com.softmed.stockapp_staging.database.AppDatabase;
import com.softmed.stockapp_staging.dom.entities.Transactions;
import com.softmed.stockapp_staging.utils.ServiceGenerator;
import com.softmed.stockapp_staging.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.softmed.stockapp_staging.utils.AppUtils.getRequestBody;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class SendTransactionsWorker extends Worker {
    private static final String TAG = SendTransactionsWorker.class.getSimpleName();


    public SendTransactionsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return sendTransaction(getInputData().getString("transactionId"));
    }


    private Result sendTransaction(String transactionId) {
        Log.d(TAG, "Send Transaction Worker working");
        SessionManager sess = new SessionManager(this.getApplicationContext());
        Endpoints.TransactionServices transactionServices = ServiceGenerator.createService(Endpoints.TransactionServices.class,
                sess.getUserName(),
                sess.getUserPass());

        AppDatabase database = AppDatabase.getDatabase(this.getApplicationContext());


        Transactions transaction = database.transactionsDao().getTransactionById(transactionId);

        List<Transactions> transactions = new ArrayList<>();
        transactions.add(transaction);

        Call transactionCall = transactionServices.postTransaction(getRequestBody(transactions));

        Response response = null;
        try {
            response = transactionCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {
                transaction.setSyncStatus(1);
                database.transactionsDao().addTransactions(transaction);

                return Result.success();

            } else {
                Log.d(TAG, "Sending Transaction Failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.failure();
        }

    }
}