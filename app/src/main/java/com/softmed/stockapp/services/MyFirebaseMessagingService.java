/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softmed.stockapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.stockapp.R;
import com.softmed.stockapp.activities.MessagesActivity;
import com.softmed.stockapp.broadcastreceivers.MyBroadcastReceiver;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.workers.NotificationWorker;
import com.softmed.stockapp.workers.SendFCMTokenWorker;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String REPLY_ACTION =
            "com.softmed.stockapp.messagingservice.ACTION_MESSAGE_REPLY";
    public static final String MESSAGE_ID = "message_id";
    public static final String MESSAGE_SENDER_ID = "message_sender_id";
    public static final String PARENT_MESSAGE_ID = "parent_message_id";
    public static final String MESSAGE_SUBJECT = "message_subject";
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {

                Gson gson = new Gson();
                String jsonString = gson.toJson(remoteMessage.getData());

                JSONObject data = new JSONObject(jsonString);

                Log.d(TAG, "DATA OBJECT = " + data.toString());
                JSONObject jsonData = new JSONObject(data.getString("data"));

                String notificationBody = data.getString("type");
                switch (notificationBody) {
                    case "NEW_MESSAGE": {
                        MessageRecipientsDTO messageRecipientsDTO = new Gson().fromJson(jsonData.toString(), MessageRecipientsDTO.class);

                        Log.d(TAG, "GENERATED MessageRecipientsDTO = " + new Gson().toJson(messageRecipientsDTO));
                        saveNewMessage(messageRecipientsDTO);
                    }
                    break;

                    case "UPDATE_READ_STATUS": {
                        Type listType = new TypeToken<List<MessageRecipients>>() {
                        }.getType();
                        List<MessageRecipients> messageRecipients = new Gson().fromJson(jsonData.toString(), listType);
                        saveMessageRecipients(messageRecipients);
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void saveNewMessage(MessageRecipientsDTO messageRecipientsDTO) {

        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());

                Message m = new Message();
                m.setId(messageRecipientsDTO.getId());
                m.setUuid(messageRecipientsDTO.getId());
                m.setSubject(messageRecipientsDTO.getSubject());
                m.setMessageBody(messageRecipientsDTO.getMessageBody());
                m.setCreatorId(messageRecipientsDTO.getCreatorId());

                //checking if the timestamp is in seconds or milliseconds.
                //android supports milliseconds timestamps
                int length = String.valueOf(messageRecipientsDTO.getCreateDate()).length();

                if (length == 10) {
                    messageRecipientsDTO.setCreateDate(messageRecipientsDTO.getCreateDate() * 1000);
                }

                m.setCreateDate(messageRecipientsDTO.getCreateDate());
                m.setParentMessageId(messageRecipientsDTO.getParentMessageId());
                m.setSyncStatus(1);
                appDatabase.messagesModelDao().addMessage(m);

                ArrayList<Integer> userIds = new ArrayList<>();
                ArrayList<String> userNames = new ArrayList<>();

                for (MessageRecipients messageRecipient : messageRecipientsDTO.getMessageRecipients()) {
                    userIds.add(messageRecipient.getRecipientId());

                    OtherUsers user = appDatabase.usersModelDao().getUser(messageRecipient.getRecipientId());
                    userNames.add(user.getFirstName() + " " + user.getSurname());
                    appDatabase.messageRecipientsModelDao().addRecipient(messageRecipient);
                }

                OtherUsers sender = appDatabase.usersModelDao().getUser(m.getCreatorId());

                sendNotification(sender.getFirstName() + " " + sender.getSurname(), messageRecipientsDTO.getMessageBody(), m, userIds, userNames);
            }
        });

    }

    private void saveMessageRecipients(List<MessageRecipients> recipientsList) {

        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                for (MessageRecipients messageRecipient : recipientsList) {
                    appDatabase.messageRecipientsModelDao().addRecipient(messageRecipient);
                }
            }
        });

    }


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        Constraints networkConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest sendMessage = new OneTimeWorkRequest.Builder(SendFCMTokenWorker.class)
                .setConstraints(networkConstraints)
                .setInputData(
                        new Data.Builder()
                                .putString("token", token)
                                .build()
                )
                .build();

        WorkManager.getInstance().enqueue(sendMessage);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title       FCM sender .
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, Message receivedMessage, ArrayList<Integer> userIds, ArrayList<String> userNames) {

        Intent messageIntent = new Intent(this, MessagesActivity.class);

        messageIntent.putExtra("parentMessageId", receivedMessage.getParentMessageId());
        messageIntent.putIntegerArrayListExtra("userIds", userIds);
        messageIntent.putStringArrayListExtra("userNames", userNames);


        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, messageIntent,
                PendingIntent.FLAG_ONE_SHOT);


        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();


        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        Integer.parseInt(receivedMessage.getId()),
                        getMessageReplyIntent(receivedMessage),
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the reply action and add the remote input.
        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24px,
                        getString(R.string.reply_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


        // Create the UnreadConversation and populate it with the participant name,
        // read and reply intents.
//        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConvBuilder =
//                new UnreadConversation.Builder(conversation.getParticipantName())
//                        .setLatestTimestamp(conversation.getTimestamp())
//                        .setReadPendingIntent(readPendingIntent)
//                        .setReplyAction(replyIntent, remoteInput);
//
//        // Note: Add messages from oldest to newest to the UnreadConversation.Builder
//        StringBuilder messageForNotification = new StringBuilder();
//        for (Iterator<String> messages = conversation.getMessages().iterator();
//             messages.hasNext(); ) {
//            String message = messages.next();
//            unreadConvBuilder.addMessage(message);
//            messageForNotification.append(message);
//            if (messages.hasNext()) {
//                messageForNotification.append(EOL);
//            }
//        }


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_calendar)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .addAction(replyAction)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Health Facility Tracer Application Messages",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(Integer.parseInt(receivedMessage.getId()), notificationBuilder.build());
    }

    // Creates an Intent that will be triggered when a text reply is received.
    private Intent getMessageReplyIntent(Message receivedMessage) {
        int messageId = Integer.parseInt(receivedMessage.getId());
        return new Intent(this, MyBroadcastReceiver.class)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(REPLY_ACTION)
                .putExtra(MESSAGE_ID, messageId)
                .putExtra(MESSAGE_SUBJECT, receivedMessage.getSubject())
                .putExtra(PARENT_MESSAGE_ID, receivedMessage.getParentMessageId().equals("0") ? receivedMessage.getId() : receivedMessage.getParentMessageId())
                .putExtra(MESSAGE_SENDER_ID, receivedMessage.getCreatorId());
    }
}