package com.softmed.rucodia.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.softmed.rucodia.R;
import com.softmed.rucodia.api.Endpoints;
import com.softmed.rucodia.database.AppDatabase;
import com.softmed.rucodia.dom.DomConverter;
import com.softmed.rucodia.dom.objects.SubCategory;
import com.softmed.rucodia.dom.objects.TransactionType;
import com.softmed.rucodia.dom.objects.Transactions;
import com.softmed.rucodia.dom.objects.UsersInfo;
import com.softmed.rucodia.dom.responces.CategoriesResponse;
import com.softmed.rucodia.dom.responces.LoginResponse;
import com.softmed.rucodia.dom.responces.ProductsResponse;
import com.softmed.rucodia.utils.Config;
import com.softmed.rucodia.utils.Constants;
import com.softmed.rucodia.utils.LargeDiagonalCutPathDrawable;
import com.softmed.rucodia.utils.ServiceGenerator;
import com.softmed.rucodia.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by issy on 11/23/17.
 */

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText usernameEt;
    private EditText passwordEt;
    private Button loginButton;
    private TextView loginMessages;
    private ProgressView loginProgress;
    private ImageView loginBgImage, background;
    private RelativeLayout credentialCard;
    private MaterialSpinner languageSpinner;

    public static AppDatabase baseDatabase;

    private String usernameValue="", passwordValue="";
    private String deviceRegistrationId = "";
    private Endpoints.CategoriesService categoriesService;
    private Endpoints.TransactionServices transactionServices;
    private Endpoints.ProductsService productsServices;
    private UsersInfo userInfo;

    // Session Manager Class
    private SessionManager session;
    int flag = 0;
    boolean justInitializing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String localeString = localeSp.getString(LOCALE_KEY, SWAHILI_LOCALE);
        Log.d("language", "From SP : "+localeString);
        Configuration config = getBaseContext().getResources().getConfiguration();
        if (localeString.equals(ENGLISH_LOCALE)){

            String language = ENGLISH_LOCALE;
            String country = "US";
            Locale locale = new Locale(language , country);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }else {

            String language = SWAHILI_LOCALE;
            String country = "TZ";
            Locale locale = new Locale(language , country);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        setContentView(R.layout.activity_login);
        setupview();

        // Session Manager
        session = new SessionManager(getApplicationContext());


        baseDatabase = AppDatabase.getDatabase(this);

        final String[] status = {"English", "Kiswahili"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, status);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        justInitializing = true;
        languageSpinner.setAdapter(spinAdapter);
        if (localeString.equals(ENGLISH_LOCALE)){
            justInitializing = true;
            languageSpinner.setSelection(1, false);
        }else {
            justInitializing = true;
            languageSpinner.setSelection(2, false);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getAuthenticationCredentials()){
                    loginProgress.setVisibility(View.VISIBLE);
                    loginUser();
                }
            }
        });

        final SharedPreferences.Editor editor = localeSp.edit();

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("language", "On listener");
                if (!justInitializing){
                    Log.d("language", "Inside Flag");
                    if (i == 0){
                        //English
                        Log.d("language", "Changing is SP to English");
                        editor.putString(LOCALE_KEY, ENGLISH_LOCALE);
                        editor.apply();
                        setLocale(new Locale(ENGLISH_LOCALE, "US"));
                    }else if (i == 1){
                        Log.d("language", "Changing is SP to Swahili");
                        //Swahili
                        editor.putString(LOCALE_KEY, SWAHILI_LOCALE);
                        editor.apply();
                        setLocale(new Locale(SWAHILI_LOCALE, "TZ"));
                    }else {

                    }
                }else{
                    justInitializing = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void setLocale(Locale locale){
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        Locale.setDefault(locale);
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        Log.d("language", "After changing the configuration");
        recreate();

    }

    private boolean getAuthenticationCredentials(){

//        if (!isDeviceRegistered()){
//            loginMessages.setText("Device is Not Registered for Notifications, please Register");
//            return false;
//        }
//        else
        if (usernameEt.getText().length() <= 0){
            Toast.makeText(this, getResources().getString(R.string.username_required), Toast.LENGTH_SHORT).show();
            return false;
        }else if (passwordEt.getText().length() <= 0){
            Toast.makeText(this, getResources().getString(R.string.password_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Log.d(TAG,"obtaining login credentials");
            usernameValue = usernameEt.getText().toString();
            passwordValue = passwordEt.getText().toString();

            return true;

        }
    }

    private boolean isDeviceRegistered(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        deviceRegistrationId = pref.getString("regId", null);
        if (deviceRegistrationId == null || deviceRegistrationId.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loginUser(){
        userInfo = null;
        if (!isNetworkAvailable()){
            //login locally

            Log.d("LoginActivity", "Inside no network");

            new AsyncTask<Void, Void, Void>(){

                List<UsersInfo> usersInfos = new ArrayList<>();

                @Override
                protected Void doInBackground(Void... voids) {
                    usersInfos = baseDatabase.userInfoDao().loggeInUser(usernameValue);
                    Log.d("LoginActivity", usersInfos.size()+"");
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    if (usersInfos.size() > 0){

                        Log.d("LoginActivity", "Session Found");

                        //User logged in, update the sessions
                        UsersInfo loggedInSessions = usersInfos.get(0);


                        session.createLoginSession(
                                loggedInSessions.getUsername(),
                                userInfo.getId(),
                                passwordValue,
                                loggedInSessions.getLocationId(),
                                loggedInSessions.getLevelId());

                        //Call HomeActivity to log in user
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        LoginActivity.this.finish();

                    }else {

                        Toast.makeText(LoginActivity.this,
                                "Internet required for this user to log in for the first time on this device",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }.execute();

        }else{
            loginButton.setText(getResources().getString(R.string.loading_data));
            loginMessages.setVisibility(View.VISIBLE);
            loginMessages.setText(getResources().getString(R.string.loging_in));


            Log.d(TAG, "username : "+usernameValue);
            Log.d(TAG, "password : "+passwordValue);

            //Use Retrofit to make http request calls
            Endpoints.LoginService loginService = ServiceGenerator.createService(Endpoints.LoginService.class, usernameValue, passwordValue);
            Call<LoginResponse> call = loginService.basicLogin();
            call.enqueue(new Callback<LoginResponse>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    Log.d(TAG,"response = "+response.toString());
                    if (response.isSuccessful()) {
                        // user object available
                        loginMessages.setTextColor(getResources().getColor(R.color.green_a700));
                        loginMessages.setText(getResources().getString(R.string.success));

                        Log.d(TAG,"response body = "+new Gson().toJson(response.body()).toString());
                        userInfo = DomConverter.getUserInfo(response.body());

                        String userUUID = userInfo.getUuid();
                        session.createLoginSession(
                                usernameValue,
                                userInfo.getId(),
                                passwordValue,
                                userInfo.getLocationId(),
                                userInfo.getLevelId());

                        categoriesService = ServiceGenerator.createService(Endpoints.CategoriesService.class, session.getUserName(), session.getUserPass());
                        transactionServices = ServiceGenerator.createService(Endpoints.TransactionServices.class, session.getUserName(), session.getUserPass());
                        productsServices = ServiceGenerator.createService(Endpoints.ProductsService.class, session.getUserName(), session.getUserPass());


//                        final Location location = DomConverter.getLocation(response.body());

                        new AsyncTask<Void, Void, Void>(){
                            @Override
                            protected Void doInBackground(Void... voids) {
                                Log.d(TAG,"userInfo : "+userInfo.toString());
                                baseDatabase.userInfoDao().addUserInfo(userInfo);
//                                baseDatabase.locationsModelDao().addLocation(location);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                sendRegistrationToServer(deviceRegistrationId,
                                        userInfo.getUuid());
                            }
                        }.execute();




                    } else {
                        loginMessages.setText(getResources().getString(R.string.error_logging_in));
                        loginMessages.setTextColor(getResources().getColor(R.color.red_a700));
                        loginProgress.setVisibility(View.GONE);
                        loginButton.setText(getResources().getString(R.string.login));
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    try {
                        Log.d("Error", t.getMessage());
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

            });
        }

    }

    private void sendRegistrationToServer(String token, String userUiid){

        SessionManager sess = new SessionManager(getApplicationContext());

        String datastream = "";
        JSONObject object   = new JSONObject();
        RequestBody body;

        try {
            object.put("userUiid", userUiid);
            object.put("googlePushNotificationToken", token);
            object.put("userType", 1);

            datastream = object.toString();
            Log.d("FCMService", "data "+datastream);

            body = RequestBody.create(MediaType.parse("application/json"), datastream);

        }catch (Exception e){
            e.printStackTrace();
            body = RequestBody.create(MediaType.parse("application/json"), datastream);
        }

        Endpoints.NotificationServices notificationServices = ServiceGenerator.createService(Endpoints.NotificationServices.class, session.getUserName(), session.getUserPass());
        Call call = notificationServices.registerDevice(body);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                new AddUserData(baseDatabase).execute(userInfo);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                loginMessages.setText(getResources().getString(R.string.device_registration_warning));
                loginMessages.setTextColor(getResources().getColor(R.color.red_600));
            }
        });

    }

    private void callCategories(){
        loginMessages.setText(getResources().getString(R.string.loading_categories));
        loginMessages.setTextColor(getResources().getColor(R.color.amber_a700));
        if (session.isLoggedIn()){
            Call<List<CategoriesResponse>> call = categoriesService.getCategories();
            call.enqueue(new Callback<List<CategoriesResponse>>() {

                @Override
                public void onResponse(Call<List<CategoriesResponse>> call, Response<List<CategoriesResponse>> response) {
                    //Here will handle the responce from the server
                    Log.d("CategoriesCheck", response.body()+"");

                    addCategoriesAsyncTask task = new addCategoriesAsyncTask(response.body());
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {
                    //Error!
                    //createDummyReferralData();
                    Log.e("", "An error encountered!");
                    Log.d("CategoriesCheck", "failed with "+t.getMessage()+" "+t.toString());


                    loginMessages.setText(getResources().getString(R.string.error_loading_categories));
                    loginMessages.setTextColor(getResources().getColor(R.color.red_500));
                }
            });
        }
    }

    private void callProducts(){
        loginMessages.setText(getResources().getString(R.string.loading_products));
        loginMessages.setTextColor(getResources().getColor(R.color.amber_a700));
        if (session.isLoggedIn()){
            Call<List<ProductsResponse>> call = productsServices.getProducts();
            call.enqueue(new Callback<List<ProductsResponse>>() {

                @Override
                public void onResponse(Call<List<ProductsResponse>> call, Response<List<ProductsResponse>> response) {
                    //Here will handle the responce from the server
                    Log.d("ProductsCheck", response.body()+"");

                    addProductsAsyncTask task = new addProductsAsyncTask(response.body());
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailure(Call<List<ProductsResponse>> call, Throwable t) {
                    //Error!
                    Log.e("", "An error encountered!");
                    Log.d("SubCategoriesCheck", "failed with "+t.getMessage()+" "+t.toString());


                    loginMessages.setText(getResources().getString(R.string.error_loading_products));
                    loginMessages.setTextColor(getResources().getColor(R.color.red_500));
                }
            });
        }
    }

    private void callTransactions(){
        loginMessages.setText(getResources().getString(R.string.loading_transactions));
        loginMessages.setTextColor(getResources().getColor(R.color.amber_a700));
        if (session.isLoggedIn()){
            Call<List<Transactions>> call = transactionServices.getTransactions("users/"+session.getUserUUID()+"/transactions");
            call.enqueue(new Callback<List<Transactions>>() {

                @Override
                public void onResponse(Call<List<Transactions>> call, Response<List<Transactions>> response) {


                    Log.d(TAG,"TransactionCheck Code = "+response.code());
                    //Here will handle the responce from the server
                    Log.d("transactionsCheck", response.body()+"");

                    addTransactionsAsyncTask task = new addTransactionsAsyncTask(response.body());
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailure(Call<List<Transactions>> call, Throwable t) {
                    //Error!
                    Log.e("", "An error encountered!");
                    Log.d("TransactionCheck", "failed with "+t.getMessage()+" "+t.toString());
                    loginMessages.setText(getResources().getString(R.string.error_loading_transactions));
                    loginMessages.setTextColor(getResources().getColor(R.color.red_500));
                }
            });
        }
    }

    private void callTransactionTypes(){
        loginMessages.setText(getResources().getString(R.string.loading_transactions_types));
        loginMessages.setTextColor(getResources().getColor(R.color.amber_a700));
        if (session.isLoggedIn()){
            Call<List<TransactionType>> call = transactionServices.getTransactionTypes();
            call.enqueue(new Callback<List<TransactionType>>() {

                @Override
                public void onResponse(Call<List<TransactionType>> call, Response<List<TransactionType>> response) {
                    //Here will handle the responce from the server
                    Log.d("transactionsCheck", response.body()+"");

                    addTransactionTypeAsyncTask task = new addTransactionTypeAsyncTask(response.body());
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailure(Call<List<TransactionType>> call, Throwable t) {
                    //Error!
                    Log.e("", "An error encountered!");
                    Log.d("TransactionCheck", "failed with "+t.getMessage()+" "+t.toString());
                    loginMessages.setText(getResources().getString(R.string.error_loading_transaction_types));
                    loginMessages.setTextColor(getResources().getColor(R.color.red_500));
                }
            });
        }
    }


    class addCategoriesAsyncTask extends AsyncTask<Void, Void, Void> {

        List<CategoriesResponse> results;

        addCategoriesAsyncTask(List<CategoriesResponse> responces){
            this.results = responces;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginMessages.setText("Finalizing Categories..");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("InitialSync", "Referal Response size : "+results.size());

            for (CategoriesResponse mList : results){
                baseDatabase.categoriesModel().addCategory(DomConverter.getCategory(mList));
                List<SubCategory> subCategories = DomConverter.getSubCategories(mList);
                for(SubCategory subCategory: subCategories){
                    baseDatabase.subCategoriesModel().addSubCategory(subCategory);
                }
                Log.d("InitialSync", "Category  : "+mList.getName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callProducts();
        }
    }

    class addProductsAsyncTask extends AsyncTask<Void, Void, Void> {

        List<ProductsResponse> results;

        addProductsAsyncTask(List<ProductsResponse> responces){
            this.results = responces;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginMessages.setText("Finalizing Categories..");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("InitialSync", "Products Response size : "+results.size());

            for (ProductsResponse mList : results){
                baseDatabase.productsModelDao().addProduct(DomConverter.getProduct(mList));
                baseDatabase.unitsDao().addUnit(DomConverter.getUnit(mList.getUnitResponses().get(0)));
                Log.d("InitialSync", "Product  : "+mList.getName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callTransactions();
        }
    }

    class addTransactionsAsyncTask extends AsyncTask<Void, Void, Void> {

        List<Transactions> results;

        addTransactionsAsyncTask(List<Transactions> responces){
            this.results = responces;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginMessages.setText("Finalizing Transactions..");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (Transactions mList : results){
                baseDatabase.transactionsDao().addTransactions(mList);
                Log.d("InitialSync", "Transactions type : "+mList.getTransactiontype_id());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callTransactionTypes();
        }
    }

    class addTransactionTypeAsyncTask extends AsyncTask<Void, Void, Void> {

        List<TransactionType> results;

        addTransactionTypeAsyncTask(List<TransactionType> responces){
            this.results = responces;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginMessages.setText("Finalizing Transaction Types..");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("InitialSync", "TransactionTypes Response size : "+results.size());

            for (TransactionType mList : results){
                baseDatabase.transactionTypeModelDao().addTransactionsTypes(mList);
                Log.d("InitialSync", "Transactions type : "+mList.getName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            loginMessages.setText(getResources().getString(R.string.success));
            loginMessages.setTextColor(getResources().getColor(R.color.green_500));

            //Call HomeActivity to log in user
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    class AddUserData extends AsyncTask<UsersInfo, Void, Void>{

        AppDatabase database;

        AddUserData(AppDatabase db){
            this.database = db;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            callCategories();

        }

        @Override
        protected Void doInBackground(UsersInfo... userData) {
            database.userInfoDao().addUserInfo(userData[0]);
            return null;
        }
    }


    private void setupview(){

        languageSpinner = (MaterialSpinner) findViewById(R.id.spin_language);

        credentialCard = (RelativeLayout) findViewById(R.id.credential_card);
        credentialCard.setBackground(new LargeDiagonalCutPathDrawable(50));


        background = (ImageView) findViewById(R.id.background);
//        Glide.with(this).load(R.drawable.bg2).into(background);

        loginMessages = (TextView) findViewById(R.id.login_messages);
        loginMessages.setVisibility(View.GONE);

        loginProgress = (ProgressView) findViewById(R.id.login_progress);
        loginProgress.setVisibility(View.GONE);

        loginButton = (Button) findViewById(R.id.login_button);

        usernameEt = (EditText) findViewById(R.id.user_username_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
