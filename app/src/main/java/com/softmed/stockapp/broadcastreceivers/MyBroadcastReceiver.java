package com.softmed.stockapp.broadcastreceivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.workers.SendMessagesWorker;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.softmed.stockapp.services.MyFirebaseMessagingService.KEY_TEXT_REPLY;
import static com.softmed.stockapp.services.MyFirebaseMessagingService.MESSAGE_ID;
import static com.softmed.stockapp.services.MyFirebaseMessagingService.MESSAGE_SENDER_ID;
import static com.softmed.stockapp.services.MyFirebaseMessagingService.MESSAGE_SUBJECT;
import static com.softmed.stockapp.services.MyFirebaseMessagingService.PARENT_MESSAGE_ID;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        String messageString = getMessageText(intent).toString();
        int notificationId = intent.getIntExtra(MESSAGE_ID, -1);

        int senderId = intent.getIntExtra(MESSAGE_SENDER_ID, -1);
        String parentMessageId = intent.getStringExtra(PARENT_MESSAGE_ID);
        String subject = intent.getStringExtra(MESSAGE_SUBJECT);


        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CreateReply(messageString, parentMessageId, subject, senderId, context);
            }
        });


        String channelId = context.getResources().getString(R.string.default_notification_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Build a new notification, which informs the user that the system
            // handled their interaction with the previous notification.
            Notification repliedNotification = new Notification.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_calendar)
                    .setContentText(context.getString(R.string.replied))
                    .build();


            Log.d(TAG, "notificationId = " + notificationId);

            // Issue the new notification.
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, repliedNotification);
        }


    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

    private void CreateReply(String reply, String parentMessageId, String subject, int senderId, Context context) {

        SessionManager sessionManager = new SessionManager(context);
        AppDatabase appDatabase = AppDatabase.getDatabase(context);


        Message newMessage = new Message();
        newMessage.setId(UUID.randomUUID().toString());
        newMessage.setCreateDate(Calendar.getInstance().getTimeInMillis());
        newMessage.setCreatorId(Integer.parseInt(sessionManager.getUserUUID()));
        newMessage.setMessageBody(reply);
        newMessage.setUuid(newMessage.getId());
        newMessage.setParentMessageId(parentMessageId);
        newMessage.setSubject(subject);

        Log.d(TAG, "saving new message = " + new Gson().toJson(newMessage));
        appDatabase.messagesModelDao().addMessage(newMessage);


        List<MessageRecipients> recipients = appDatabase.messageRecipientsModelDao().getAllMessageRecipientsByMessageId(parentMessageId);
        for (MessageRecipients recipient : recipients) {
            if (recipient.getRecipientId() == Integer.parseInt(sessionManager.getUserUUID()))
                continue;
            MessageRecipients messageRecipients = new MessageRecipients();
            messageRecipients.setId(UUID.randomUUID().toString());
            messageRecipients.setMessageId(newMessage.getId());
            messageRecipients.setRead(false);
            messageRecipients.setRecipientId(recipient.getRecipientId());

            appDatabase.messageRecipientsModelDao().addRecipient(messageRecipients);
            Log.d(TAG, "saving new message recipients = " + new Gson().toJson(messageRecipients));
        }

        //Creating message recipient of the sender so as to also reply to him
        MessageRecipients messageRecipients = new MessageRecipients();
        messageRecipients.setId(UUID.randomUUID().toString());
        messageRecipients.setMessageId(newMessage.getId());
        messageRecipients.setRead(false);
        messageRecipients.setRecipientId(senderId);

        if (messageRecipients.getRecipientId() == Integer.parseInt(sessionManager.getUserUUID())) {
            appDatabase.messageRecipientsModelDao().addRecipient(messageRecipients);
        }

        //Creating a worker to handle sending of message once there is internet connectivity
        Constraints networkConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest sendMessage = new OneTimeWorkRequest.Builder(SendMessagesWorker.class)
                .setConstraints(networkConstraints)
                .setInputData(
                        new Data.Builder()
                                .putString("messageId", newMessage.getId())
                                .build())
                .build();

        WorkManager.getInstance().enqueue(sendMessage);

    }
}