package com.softmed.stockapp.api;

import com.softmed.stockapp.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp.dom.entities.Balances;
import com.softmed.stockapp.dom.entities.Location;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.Product;
import com.softmed.stockapp.dom.entities.TransactionType;
import com.softmed.stockapp.dom.entities.Transactions;
import com.softmed.stockapp.dom.responces.CategoriesResponse;
import com.softmed.stockapp.dom.responces.LoginResponse;
import com.softmed.stockapp.dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp.dom.responces.ProductsPostResponse;
import com.softmed.stockapp.dom.responces.UnitsResponse;
import com.softmed.stockapp.dom.responces.UserResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;


public class Endpoints {

    private String HFUUID = "";

    public interface LoginService {
        @POST("rest-auth/login/")
        Call<LoginResponse> basicLogin(@Body RequestBody e);


        @GET("api_locations/")
        Call<List<Location>> getLocations();

        @GET("api_user_profile/")
        Call<List<UserResponse>> getAllUsers();

    }

    public interface CategoriesService {
        @GET("api_health_commodity_category")
        Call<List<CategoriesResponse>> getCategories();

    }

    public interface ProductsService {
        @GET("api_health_commodity")
        Call<List<Product>> getProducts();

        @GET("api_unit")
        Call<List<UnitsResponse>> getUnits();

        @POST("products")
        Call<ProductsPostResponse> postProducts(@Body RequestBody e);
    }

    public interface TransactionServices {
        @GET("api_health_commodity_transactions")
        Call<List<Transactions>> getTransactions();

        @GET
        Call<List<Balances>> getBalances(@Url String url);

        @GET("api_posting_schedule")
        Call<List<ProductReportingScheduleResponse>> getSchedule();

        @GET("transactiontypes")
        Call<List<TransactionType>> getTransactionTypes();

        @POST("api_health_commodity_transactions/")
        Call<List<Transactions>> postTransaction(@Body RequestBody e);


        @POST("api_health_commodity_mapping/")
        Call<List<ProductReportingScheduleResponse>> postBalances(@Body RequestBody e);
    }

    public interface MessagesServices {
        @GET("api_messages")
        Call<List<Message>> getMessages();

        @GET("api_message_recipients")
        Call<List<MessageRecipients>> getMessageRecipients();


        @POST("api_sent_messages/")
        Call<MessageRecipientsDTO> postMessages(@Body RequestBody e);

        @PUT("update_read_message_status")
        Call<MessageRecipients> updateMessageReadStatus(@Body RequestBody e);
    }

    public interface NotificationServices {
        @PUT("update_google_token")
        Call<String> registerDevice(@Body RequestBody u);
    }


}
