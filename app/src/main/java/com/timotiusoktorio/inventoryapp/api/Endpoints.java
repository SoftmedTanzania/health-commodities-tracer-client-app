package com.timotiusoktorio.inventoryapp.api;

import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;
import com.timotiusoktorio.inventoryapp.dom.objects.TransactionType;
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
