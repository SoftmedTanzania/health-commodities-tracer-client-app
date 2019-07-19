package com.softmed.stockapp.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.softmed.stockapp.api.Endpoints;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp.dom.entities.Message;
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
public class SendMessagesWorker extends Worker {
    private static final String TAG = SendMessagesWorker.class.getSimpleName();


    public SendMessagesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return sendMessage(getInputData().getString("messageId"));
    }


    private Result sendMessage(String messageId) {
        Log.d(TAG, "Send Message Worker working");
        SessionManager sess = new SessionManager(this.getApplicationContext());
        Endpoints.MessagesServices messagesServices = ServiceGenerator.createService(Endpoints.MessagesServices.class,
                sess.getUserName(),
                sess.getUserPass());

        AppDatabase database = AppDatabase.getDatabase(this.getApplicationContext());


        Message message = database.messagesModelDao().getMessageById(messageId);

        MessageRecipientsDTO sentMessageRecipientsDTO = new MessageRecipientsDTO();
        sentMessageRecipientsDTO.setId(message.getId());
        sentMessageRecipientsDTO.setCreateDate(message.getCreateDate());
        sentMessageRecipientsDTO.setCreatorId(message.getCreatorId());
        sentMessageRecipientsDTO.setMessageBody(message.getMessageBody());
        sentMessageRecipientsDTO.setParentMessageId(message.getParentMessageId());
        sentMessageRecipientsDTO.setSubject(message.getSubject());
        sentMessageRecipientsDTO.setMessageRecipients(database.messageRecipientsModelDao().getAllMessageRecipientsByMessageId(message.getId()));

        Call<MessageRecipientsDTO> messageCall = messagesServices.postMessages(getRequestBody(sentMessageRecipientsDTO));

        Response<MessageRecipientsDTO> response = null;
        try {
            response = messageCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {

                MessageRecipientsDTO receivedMessageRecipientsDTO = response.body();
                database.messagesModelDao().updateMessageIds(messageId, receivedMessageRecipientsDTO.getId());
                database.messageRecipientsModelDao().updateMessageRecipientsIds(message.getId(), response.body().getId());

                for (int i = 0; i < receivedMessageRecipientsDTO.getMessageRecipients().size(); i++) {
                    MessageRecipients messageRecipients = receivedMessageRecipientsDTO.getMessageRecipients().get(i);
                    database.messageRecipientsModelDao().updateIds(sentMessageRecipientsDTO.getMessageRecipients().get(i).getId(), messageRecipients.getId());
                }

                return Result.success();

            } else {
                Log.d(TAG, "Sending Message Failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.failure();
        }

    }
}