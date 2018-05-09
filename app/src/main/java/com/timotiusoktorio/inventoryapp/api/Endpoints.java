package com.timotiusoktorio.inventoryapp.api;

import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;
import com.timotiusoktorio.inventoryapp.dom.objects.Transaction;
import com.timotiusoktorio.inventoryapp.dom.objects.Unit;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;


public class Endpoints {

    private String HFUUID = "";

    public interface LoginService {

        @POST("security/authenticate/")
        Call<UsersInfo> basicLogin();

    }

    public interface CategoriesService {
        @GET("units")
        Call<List<Unit>> getUnits();

        @GET("subcategories")
        Call<List<SubCategory>> getSubCategories();

        @GET("categories")
        Call<List<Category>> getCategories();

    }

    public interface TransactionServices{
        @GET("transactions")
        Call<List<Transaction>> getTransactions();

//        @POST("transactions")
//        Call<EncounterResponse> postEncounter(@Header("From") String serviceProviderUUID, @Body RequestBody e);

    }

    public interface NotificationServices{
//        @POST("save-push-notification-token")
//        Call<String> registerDevice(@Body RequestBody u);
    }

}
