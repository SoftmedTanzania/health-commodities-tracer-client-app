package com.softmed.rucodia.api;

import com.softmed.rucodia.dom.objects.TransactionType;
import com.softmed.rucodia.dom.objects.Transactions;
import com.softmed.rucodia.dom.responces.CategoriesResponse;
import com.softmed.rucodia.dom.responces.LoginResponse;
import com.softmed.rucodia.dom.responces.ProductsResponse;
import com.softmed.rucodia.dom.responces.TransactionsResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public class Endpoints {

    private String HFUUID = "";

    public interface LoginService {

        @GET("auth")
        Call<LoginResponse> basicLogin();

    }

    public interface CategoriesService {
        @GET("categories")
        Call<List<CategoriesResponse>> getCategories();

    }

    public interface ProductsService {
        @GET("products")
        Call<List<ProductsResponse>> getProducts();
    }

    public interface TransactionServices{
        @GET("transactions")
        Call<List<Transactions>> getTransactions();

        @GET("transactiontypes")
        Call<List<TransactionType>> getTransactionTypes();

        @POST("transactions")
        Call<TransactionsResponse> postTransaction(@Body RequestBody e);

    }

    public interface NotificationServices{
        @POST("save-push-notification-token")
        Call<String> registerDevice(@Body RequestBody u);
    }

}
