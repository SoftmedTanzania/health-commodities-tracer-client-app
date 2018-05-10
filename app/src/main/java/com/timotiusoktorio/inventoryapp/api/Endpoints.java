package com.timotiusoktorio.inventoryapp.api;

import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;
import com.timotiusoktorio.inventoryapp.dom.objects.Unit;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;
import com.timotiusoktorio.inventoryapp.dom.responces.CategoriesResponse;
import com.timotiusoktorio.inventoryapp.dom.responces.LoginResponse;
import com.timotiusoktorio.inventoryapp.dom.responces.ProductsResponse;
import com.timotiusoktorio.inventoryapp.dom.responces.SubCategoriesResponse;
import com.timotiusoktorio.inventoryapp.dom.responces.TransactionsResponse;
import com.timotiusoktorio.inventoryapp.dom.responces.UnitsResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public class Endpoints {

    private String HFUUID = "";

    public interface LoginService {

        @GET("users/1")
        Call<LoginResponse> basicLogin();

    }

    public interface CategoriesService {
        @GET("units")
        Call<UnitsResponse> getUnits();

        @GET("subcategories")
        Call<SubCategoriesResponse> getSubCategories();

        @GET("categories")
        Call<CategoriesResponse> getCategories();

    }

    public interface ProductsService {
        @GET("products")
        Call<ProductsResponse> getProducts();
    }

    public interface TransactionServices{
        @GET("transactions")
        Call<TransactionsResponse> getTransactions();

//        @POST("transactions")
//        Call<CategoriesResponse> postEncounter(@Header("From") String serviceProviderUUID, @Body RequestBody e);

    }

    public interface NotificationServices{
        @POST("save-push-notification-token")
        Call<String> registerDevice(@Body RequestBody u);
    }

}
