package com.softmed.stockapp.broadcastReceivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.softmed.stockapp.R;

import static com.softmed.stockapp.services.MyFirebaseMessagingService.KEY_TEXT_REPLY;
import static com.softmed.stockapp.services.MyFirebaseMessagingService.MESSAGE_ID;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        String messageString = getMessageText(intent).toString();
        int notificationId = intent.getIntExtra(MESSAGE_ID,-1);








        String channelId = context.getResources().getString(R.string.default_notification_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Build a new notification, which informs the user that the system
            // handled their interaction with the previous notification.
            Notification repliedNotification = new Notification.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_calendar)
                    .setContentText(context.getString(R.string.replied))
                    .build();

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
}