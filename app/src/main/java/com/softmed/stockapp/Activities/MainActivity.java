package com.softmed.stockapp.Activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductList;
import com.softmed.stockapp.Fragments.UpdateStockFragment;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Fragments.DashboardFragment;
import com.softmed.stockapp.Fragments.OrdersFragment;
import com.softmed.stockapp.Fragments.ProductsListFragment;
import com.softmed.stockapp.Utils.AlarmReceiver;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.Utils.ZoomOutPageTransformer;
import com.softmed.stockapp.viewmodels.ProductsViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES;
import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProductsViewModel productsViewModel;

    // Session Manager Class
    private SessionManager session;

    private ViewPager updateStockViewPager;
    private int managedProductsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Session Manager
        session = new SessionManager(getApplicationContext());

        final AppDatabase baseDatabase=AppDatabase.getDatabase(this);


        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            sessionManager.checkLogin();
            finish();
        }else{
            new AsyncTask<Void,Void,List<ProductList>>(){

                @Override
                protected List<ProductList> doInBackground(Void... voids) {
                    return baseDatabase.productsModelDao().getAvailableProductsCheck();
                }

                @Override
                protected void onPostExecute(List<ProductList> productLists) {
                    super.onPostExecute(productLists);
                    Log.d(TAG,"available products = "+new Gson().toJson(productLists));
                    if(productLists.size()==0){
                        Intent i = new Intent(MainActivity.this, ManagedProductsActivity.class);

                        // Closing all the Activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Add new Flag to start new Activity
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Staring Manage Products Activity
                        MainActivity.this.startActivity(i);
                        finish();
                    }
                }
            }.execute();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        DashboardFragment dashboardFragment = new DashboardFragment();
        ProductsListFragment productsListFragment = new ProductsListFragment();
        OrdersFragment ordersFragment = new OrdersFragment();

        viewPagerAdapter.addFragment(dashboardFragment, "Dashboard");
        viewPagerAdapter.addFragment(productsListFragment, "Products List");
//        viewPagerAdapter.addFragment(ordersFragment,"Order Fragment");

        viewPager.setAdapter(viewPagerAdapter);

        ColorDrawable cd = new ColorDrawable(0xFF666666);
        viewPager.setPageMargin(convertDip2Pixels(MainActivity.this,1));
        viewPager.setPageMarginDrawable(cd);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons();
            }
        });



        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        SpringDotsIndicator springDotsIndicator = findViewById(R.id.spring_dots_indicator);
        WormDotsIndicator wormDotsIndicator = findViewById(R.id.worm_dots_indicator);

        updateStockViewPager = findViewById(R.id.view_pager);
        updateStockViewPager.setOffscreenPageLimit(20);


        new AsyncTask<Void,Void,List<ProductList>>(){

            @Override
            protected List<ProductList> doInBackground(Void... voids) {
                return baseDatabase.productsModelDao().getAvailableProductsCheck();
            }

            @Override
            protected void onPostExecute(List<ProductList> productLists) {
                super.onPostExecute(productLists);
                Log.d(TAG,"products list size = "+productLists.size());

                managedProductsCount = productLists.size();
                DotIndicatorPagerAdapter adapter = new DotIndicatorPagerAdapter(getSupportFragmentManager(),productLists);
                updateStockViewPager.setAdapter(adapter);
            }
        }.execute();





        updateStockViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        dotsIndicator.setViewPager(updateStockViewPager);
        springDotsIndicator.setViewPager(updateStockViewPager);
        wormDotsIndicator.setViewPager(updateStockViewPager);



        scheduleAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.logoutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
            return null;
        }

        @Override
        public float getPageWidth(int position) {
            return 0.5f;
        }
    }

    public static class DotIndicatorPagerAdapter extends FragmentPagerAdapter {
        private List<ProductList> productLists;
        public DotIndicatorPagerAdapter(FragmentManager fm, List<ProductList> productLists) {
            super(fm);
            this.productLists=productLists;
        }

        @Override
        public int getCount() {
            return productLists.size();
        }

        @Override
        public Fragment getItem(int position) {
            ProductList productList = productLists.get(position);

            Product product = new Product();
            product.setId(productList.getId());
            product.setName(productList.getName());

            return UpdateStockFragment.newInstance(product);
        }
    }

    public void setupTabIcons() {


        View dashboard = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView dashboardTitle = dashboard.findViewById(R.id.title_text);
        dashboardTitle.setText("Dashboard");
        ImageView dashboardIcon = dashboard.findViewById(R.id.icon);
        dashboardIcon.setColorFilter(this.getResources().getColor(R.color.white));
        Glide.with(this).load(R.drawable.ic_dashboard_white_24dp).into(dashboardIcon);
        tabLayout.getTabAt(0).setCustomView(dashboard);


        View myInventory = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView opdTabTitle = myInventory.findViewById(R.id.title_text);
        opdTabTitle.setText("My Inventory");
        ImageView inventoryIcon = myInventory.findViewById(R.id.icon);
        inventoryIcon.setColorFilter(this.getResources().getColor(R.color.white));
        Glide.with(this).load(R.drawable.ic_content_paste_white_24dp).into(inventoryIcon);
        tabLayout.getTabAt(1).setCustomView(myInventory);


        try {

            View orderView = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView orderTitle = orderView.findViewById(R.id.title_text);
            orderTitle.setText("Orders");
            ImageView orderIcon = orderView.findViewById(R.id.icon);
            orderIcon.setColorFilter(this.getResources().getColor(R.color.white));
            Glide.with(this).load(R.drawable.ic_local_shipping_white_24dp).into(orderIcon);
            try {
                tabLayout.getTabAt(2).setCustomView(orderView);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                INTERVAL_FIFTEEN_MINUTES, pIntent);
    }

    public void moveToNextProduct(){
        int nextItem = updateStockViewPager.getCurrentItem()+1;

        if(nextItem<managedProductsCount) {
            updateStockViewPager.setCurrentItem(nextItem, true);
        }else {
            findViewById(R.id.products_stock_count).setVisibility(GONE);
        }
    }

    public static int convertDip2Pixels(Context context, int dip) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

}