package com.softmed.stockapp_staging.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.softmed.stockapp_staging.api.Endpoints;
import com.softmed.stockapp_staging.database.AppDatabase;
import com.softmed.stockapp_staging.dom.dto.DeleteMessegeDTO;
import com.softmed.stockapp_staging.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp_staging.dom.entities.Message;
import com.softmed.stockapp_staging.dom.entities.MessageRecipients;
import com.softmed.stockapp_staging.dom.responces.NewMessageResponce;
import com.softmed.stockapp_staging.utils.ServiceGenerator;
import com.softmed.stockapp_staging.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.softmed.stockapp_staging.utils.AppUtils.getRequestBody;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class DeleteMessagesWorker extends Worker {
    private static final String TAG = DeleteMessagesWorker.class.getSimpleName();


    public DeleteMessagesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return deleteMessage(getInputData().getString("messageId"),(getInputData().getBoolean("isTrashedByCreator",false)));
    }


    private Result deleteMessage(String messageId, boolean isTrashedByCreator) {
        Log.d(TAG, "Delete Message Worker working");
        SessionManager sess = new SessionManager(this.getApplicationContext());
        Endpoints.MessagesServices messagesServices = ServiceGenerator.createService(Endpoints.MessagesServices.class,
                sess.getUserName(),
                sess.getUserPass());

        Call deleteMessage;

        DeleteMessegeDTO deleteMessegeDTO = new DeleteMessegeDTO();

        deleteMessegeDTO.setId(messageId);


        if(isTrashedByCreator){
            deleteMessegeDTO.setTrashed_by_creator(true);
            deleteMessage = messagesServices.deleteMessageByCreator(getRequestBody(deleteMessegeDTO));
        }else{
            deleteMessegeDTO.setIs_trashed(true);
            deleteMessage = messagesServices.deleteMessageByRecipient(getRequestBody(deleteMessegeDTO));
        }




        Response response = null;
        try {
            response = deleteMessage.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {
                Log.d(TAG,"deleting message successful Code= "+response.code());
                return Result.success();

            } else {
                Log.d(TAG, "Deleting Message Failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.retry();
        }

    }
}