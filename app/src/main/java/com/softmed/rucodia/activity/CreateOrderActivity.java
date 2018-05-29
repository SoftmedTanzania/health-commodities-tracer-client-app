package com.softmed.rucodia.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softmed.rucodia.R;
import com.softmed.rucodia.database.AppDatabase;
import com.softmed.rucodia.dom.objects.Orders;
import com.softmed.rucodia.dom.objects.OrdersItems;
import com.softmed.rucodia.dom.objects.UsersInfo;
import com.softmed.rucodia.fragment.CreateOrderDialogue;
import com.softmed.rucodia.utils.SessionManager;
import com.softmed.rucodia.viewmodels.OrderViewModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateOrderActivity extends AppCompatActivity {

    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    private LinearLayout orderItemsList;
    private OrderViewModel orderViewModel;
    private String batchNo;
    private int totalCOst = 0;
    private AppDatabase baseDatabase;
    private SessionManager sessionManager;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        batchNo = UUID.randomUUID().toString();

        orderItemsList = findViewById(R.id.order_items_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateOrderActivity.this,"Available in upcoming nightly builds with supplier app",Toast.LENGTH_SHORT).show();
                CreateOrderDialogue Dialogue = CreateOrderDialogue.newInstance(batchNo);
                Dialogue.show(getSupportFragmentManager(), "Adding Order");
            }
        });


        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        String dateFormatted = formatter.format(date);

        ((TextView)findViewById(R.id.todays_date)).setText(dateFormatted);



        baseDatabase = AppDatabase.getDatabase(this);

        sessionManager =  new SessionManager(getApplicationContext());

        new AsyncTask<Void, Void, UsersInfo>(){

            List<UsersInfo> usersInfos = new ArrayList<>();

            @Override
            protected UsersInfo doInBackground(Void... voids) {

                Log.d("Coze","username = "+sessionManager.getUserName());

                List<UsersInfo> usersInfos = baseDatabase.userInfoDao().loggeInUser(sessionManager.getUserName());

                if(usersInfos.size()>0) {
                    return usersInfos.get(0);
                }else{
                    return  null;
                }
            }

            @Override
            protected void onPostExecute(UsersInfo aVoid) {
                super.onPostExecute(aVoid);

                try {
                    ((TextView) findViewById(R.id.agro_dealer_name)).setText(aVoid.getFirstName() + " " + aVoid.getSurname());
                    ((TextView) findViewById(R.id.ward)).setText(aVoid.getEmail());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }.execute();



        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        orderViewModel.getOrdersByBatchNo(batchNo).observe(this, new Observer<List<OrdersItems>>() {
            @Override
            public void onChanged(@Nullable List<OrdersItems> orders) {
                orderItemsList.removeAllViews();
                totalCOst = 0;
                int i=0;

                Locale tanzaniaLocale = new Locale("sw", "TZ");
                NumberFormat format = NumberFormat.getCurrencyInstance(tanzaniaLocale);

                for(final OrdersItems order:orders){
                    i++;
                    final View v = LayoutInflater.from(CreateOrderActivity.this).inflate(R.layout.view_order_item,null);

                    ((TextView)v.findViewById(R.id.sn)).setText(String.valueOf(i));
                    ((TextView)v.findViewById(R.id.product_names_title)).setText(order.getName());
                    ((TextView)v.findViewById(R.id.order_quantity)).setText(String.valueOf(order.getOrdered()));

                    totalCOst+=order.getOrdered()*order.getPrices();


                    String currency = format.format(order.getOrdered()*order.getPrices());

                    ((TextView)v.findViewById(R.id.total_cost)).setText(currency);

                    orderItemsList.addView(v);
                }

                String currency = format.format(totalCOst);
                ((TextView)findViewById(R.id.estimated_cost)).setText(currency);


            }
        });

    }



}
