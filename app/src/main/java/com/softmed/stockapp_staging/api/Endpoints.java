package com.softmed.stockapp_staging.api;

import com.softmed.stockapp_staging.dom.dto.MessageRecipientsDTO;
import com.softmed.stockapp_staging.dom.entities.Balances;
import com.softmed.stockapp_staging.dom.entities.Location;
import com.softmed.stockapp_staging.dom.entities.MessageRecipients;
import com.softmed.stockapp_staging.dom.entities.PostingFrequencies;
import com.softmed.stockapp_staging.dom.entities.Product;
import com.softmed.stockapp_staging.dom.entities.TransactionType;
import com.softmed.stockapp_staging.dom.entities.Transactions;
import com.softmed.stockapp_staging.dom.responces.CategoriesResponse;
import com.softmed.stockapp_staging.dom.responces.LoginResponse;
import com.softmed.stockapp_staging.dom.responces.NewMessageResponce;
import com.softmed.stockapp_staging.dom.responces.ProductReportingScheduleResponse;
import com.softmed.stockapp_staging.dom.responces.ProductsPostResponse;
import com.softmed.stockapp_staging.dom.responces.UnitsResponse;
import com.softmed.stockapp_staging.dom.responces.UserResponse;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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


        @PUT("update_password")
        Call<ResponseBody> updatePassword(@Body RequestBody e);

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

        @GET("api_posting_frequency")
        Call<List<PostingFrequencies>> getPostingFrequencies();

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
        Call<List<MessageRecipientsDTO>> getMessages();

        @GET("api_message_recipients")
        Call<List<MessageRecipients>> getMessageRecipients();


        @POST("create_new_message")
        Call<NewMessageResponce> postMessages(@Body RequestBody e);

        @PUT("update_is_trashed_status")
        Call<ResponseBody> deleteMessageByRecipient(@Body RequestBody e);

        @PUT("update_parent_message_status")
        Call<ResponseBody> deleteMessageByCreator(@Body RequestBody e);

        @PUT("update_read_message_status")
        Call<String> updateMessageReadStatus(@Body RequestBody e);
    }

    public interface NotificationServices {
        @PUT("update_google_token")
        Call<String> registerDevice(@Body RequestBody u);
    }


}
