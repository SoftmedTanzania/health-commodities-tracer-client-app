package com.softmed.stockapp.workers;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.softmed.stockapp.api.Endpoints;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp.dom.dto.UpdatePasswordDTO;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.responces.NewMessageResponce;
import com.softmed.stockapp.utils.ServiceGenerator;
import com.softmed.stockapp.utils.SessionManager;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.softmed.stockapp.utils.AppUtils.getRequestBody;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class UpdatePasswordWorker extends Worker {
    private static final String TAG = UpdatePasswordWorker.class.getSimpleName();


    public UpdatePasswordWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return updatePassword(getInputData().getString("oldPassword"),getInputData().getString("newPassword"));
    }


    private Result updatePassword(String oldPassword, String newPassword) {
        Log.d(TAG, "Update Password Worker working");
        SessionManager sess = new SessionManager(this.getApplicationContext());
        Endpoints.LoginService loginService = ServiceGenerator.createService(Endpoints.LoginService.class,
                sess.getUserName(),
                sess.getUserPass());

        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setNew_password(newPassword);
        updatePasswordDTO.setOld_password(oldPassword);



        Call updatePasswordCall = loginService.updatePassword(getRequestBody(updatePasswordDTO));

        Response<String> response = null;
        try {
            response = updatePasswordCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            if (response.code() == 200 || response.code() == 201) {

                return Result.success();

            } else {
                Log.d(TAG, "Updating password Failed with code " + response.code());
                return Result.retry();
            }
        } else {
            return Result.retry();
        }

    }
}