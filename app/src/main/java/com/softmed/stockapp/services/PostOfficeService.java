package com.softmed.stockapp.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.google.gson.Gson;
import com.softmed.stockapp.activities.LoginActivity;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp.dom.entities.Balances;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.Transactions;
import com.softmed.stockapp.dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp.utils.ServiceGenerator;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.api.Endpoints;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostOfficeService extends IntentService {
    private static final String TAG = PostOfficeService.class.getSimpleName();

    AppDatabase database;
    SessionManager sess;
    Endpoints.TransactionServices transactionServices;
    Endpoints.MessagesServices messagesServices;

    public PostOfficeService() {
        super("PostOfficeService");
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

    @Override
    protected void onHandleIntent(Intent intent) {

        database = AppDatabase.getDatabase(this.getApplicationContext());
        sess = new SessionManager(this.getApplicationContext());


        Log.d(TAG, "username = " + sess.getUserName());
        Log.d(TAG, "password = " + sess.getUserPass());


        transactionServices = ServiceGenerator.createService(Endpoints.TransactionServices.class,
                sess.getUserName(),
                sess.getUserPass());

        messagesServices = ServiceGenerator.createService(Endpoints.MessagesServices.class,
                sess.getUserName(),
                sess.getUserPass());


        final List<Balances> balances = database.balanceModelDao().getUnPostedBalances();

        Log.d(TAG, " unposted balances = " + new Gson().toJson(balances));

        Call postBalancescall = transactionServices.postBalances(getRequestBody(balances));
        postBalancescall.enqueue(new Callback() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call call, Response response) {
                //Store Received Patient Information, TbPatient as well as PatientAppointments
                if (response.code() == 200 || response.code() == 201) {
                    Log.d(TAG, "Successful Balance responce " + new Gson().toJson(response.body()));

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            for (Balances balance : balances) {
                                balance.setSyncStatus(1);
                                database.balanceModelDao().addBalance(balance);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                        }
                    }.execute();


                } else {
                    Log.d(TAG, "Balance Responce Call URL " + call.request().url());
                    Log.d(TAG, "Balance Responce Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, "PostOfficeService Error = " + t.getMessage());
                Log.d(TAG, "PostOfficeService CALL URL = " + call.request().url());
                Log.d(TAG, "PostOfficeService CALL Header = " + call.request().header("Authorization"));
            }
        });


        final List<Transactions> transactions = database.transactionsDao().getUnPostedTransactions();

        Call transactionCall = transactionServices.postTransaction(getRequestBody(transactions));
        transactionCall.enqueue(new Callback() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call call, Response response) {
                //Store Received Patient Information, TbPatient as well as PatientAppointments
                if (response.code() == 200 || response.code() == 201) {
                    Log.d(TAG, "Successful Transaction responce " + new Gson().toJson(response.body()));
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            for (Transactions transaction : transactions) {
                                transaction.setSyncStatus(1);
                                database.transactionsDao().addTransactions(transaction);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                        }
                    }.execute();
                } else {
                    Log.d(TAG, "Transaction Responce Call URL " + call.request().url());
                    Log.d(TAG, "Transaction Responce Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, "PostOfficeService Error = " + t.getMessage());
                Log.d(TAG, "PostOfficeService CALL URL = " + call.request().url());
                Log.d(TAG, "PostOfficeService CALL Header = " + call.request().header("Authorization"));
            }
        });

        Call<List<ProductReportingScheduleResponse>> call = transactionServices.getSchedule();
        call.enqueue(new Callback<List<ProductReportingScheduleResponse>>() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<List<ProductReportingScheduleResponse>> call, final Response<List<ProductReportingScheduleResponse>> response) {
                Log.d(TAG, "Received schedules" + response.body() + "");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (response.body() != null) {
                            for (ProductReportingScheduleResponse reportingSchedule : response.body()) {
                                try {
                                    if (database.productReportingScheduleModelDao().getProductReportingScheduleById(reportingSchedule.getId()).getStatus().equalsIgnoreCase("posted")) {
                                        reportingSchedule.setStatus("posted");
                                    }


                                    database.productReportingScheduleModelDao().addProductSchedule(LoginActivity.getProductReportingSchedule(reportingSchedule));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute();
            }

            @Override
            public void onFailure(Call<List<ProductReportingScheduleResponse>> call, Throwable t) {
                Log.e("", "An error encountered!");
                Log.d("ScheduleCheck", "failed with " + t.getMessage() + " " + t.toString());
            }
        });

        final List<Message> unpostedMessages = database.messagesModelDao().getUnpostedMessages();
        for( Message message:unpostedMessages){

            MessageRecipientsDTO messageRecipientsDTO = new MessageRecipientsDTO();

            messageRecipientsDTO.setId(message.getId());
            messageRecipientsDTO.setCreateDate(message.getCreateDate());
            messageRecipientsDTO.setCreatorId(message.getCreatorId());
            messageRecipientsDTO.setMessageBody(message.getMessageBody());
            messageRecipientsDTO.setParentMessageId(message.getParentMessageId());
            messageRecipientsDTO.setSubject(message.getSubject());

            messageRecipientsDTO.setMessageRecipients(database.messageRecipientsModelDao().getAllMessageRecipientsByMessageId(message.getId()));

            Call<Message> callMessages = messagesServices.postMessages(getRequestBody(messageRecipientsDTO));
            callMessages.enqueue(new Callback<Message>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (response.body() != null) {
                                database.messagesModelDao().updateMessageIds(message.getId(), response.body().getId());
                                database.messageRecipientsModelDao().updateMessageRecipientsIds(message.getId(), response.body().getId());
                            }
                            return null;
                        }
                    }.execute();
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {

                }
            });

        }





        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
