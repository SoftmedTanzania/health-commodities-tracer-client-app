package com.softmed.stockapp.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.softmed.stockapp.api.Endpoints;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.utils.ServiceGenerator;
import com.softmed.stockapp.utils.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.softmed.stockapp.utils.AppUtils.getRequestBody;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class SendMessageRecipientWorker extends Worker {
    private static final String TAG = SendMessageRecipientWorker.class.getSimpleName();


    public SendMessageRecipientWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return sendMessage(getInputData().getString("messageId"));
    }


    private Result sendMessage(String messageId) {
        Log.d(TAG, "Updating Message Recipient Worker working");
        SessionManager sess = new SessionManager(this.getApplicationContext());
        Endpoints.MessagesServices messagesServices = ServiceGenerator.createService(Endpoints.MessagesServices.class,
                sess.getUserName(),
                sess.getUserPass());

        AppDatabase database = AppDatabase.getDatabase(this.getApplicationContext());


        MessageRecipients messageRecipients = database.messageRecipientsModelDao().getMessageRecipientsByMessageIdAndRecipientId(messageId, Integer.parseInt(sess.getUserUUID()));

        Call<MessageRecipients> messageCall = messagesServices.updateMessageReadStatus(getRequestBody(messageRecipients));

        Response<MessageRecipients> response = null;
        try {
            response = messageCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {

                MessageRecipients messageRecipient = response.body();

                Log.d(TAG, "Saved message Recipient = " + new Gson().toJson(messageRecipient));

                return Result.success();

            } else {
                Log.d(TAG, "Updating Message Recipient Failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.failure();
        }

    }
}