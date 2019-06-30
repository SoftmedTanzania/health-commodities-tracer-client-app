package com.softmed.stockapp.Activities;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductList;
import com.softmed.stockapp.Fragments.DashboardFragment;
import com.softmed.stockapp.Fragments.ProductsListFragment;
import com.softmed.stockapp.Fragments.UpdateStockFragment;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Utils.AlarmReceiver;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.Utils.VerticalArrow;
import com.softmed.stockapp.Utils.ZoomOutPageTransformer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import tarek360.animated.icons.AnimatedIconView;
import tarek360.animated.icons.IconFactory;
import tarek360.animated.icons.drawables.NotificationAlert;

import static android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES;
import static android.view.View.GONE;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // Session Manager Class
    private SessionManager session;
    private ViewPager updateStockViewPager;
    private int managedProductsCount;
    private InkPageIndicator inkPageIndicator;
    private AnimatedIconView arrowUp,reportingAlert,alarmCounterIcon;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    public static int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Session Manager
        session = new SessionManager(getApplicationContext());

        final AppDatabase baseDatabase = AppDatabase.getDatabase(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrowUp = findViewById(R.id.animated_arrow_up);
        arrowUp.setAnimatedIcon(new VerticalArrow());


        reportingAlert = findViewById(R.id.alarm_counter);
        alarmCounterIcon = findViewById(R.id.alarm_counter_icon);


        NotificationAlert notificationAlert = new NotificationAlert();
        notificationAlert.setNotificationCount(3);

        NotificationAlert notificationAlert2 = new NotificationAlert();
        notificationAlert2.setNotificationCount(3);
        reportingAlert.setAnimatedIcon(notificationAlert);
        alarmCounterIcon.setAnimatedIcon(notificationAlert2);
        alarmCounterIcon.startAnimation();



        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            private SlidingUpPanelLayout.PanelState prevState = COLLAPSED;
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == EXPANDED && prevState==COLLAPSED) {
                    arrowUp.startAnimation();

                    reportingAlert.setVisibility(View.VISIBLE);
                    reportingAlert.startAnimation();
                    alarmCounterIcon.setVisibility(GONE);
                    prevState = EXPANDED;
                } else if (newState == COLLAPSED && prevState==EXPANDED) {
                    arrowUp.startAnimation();
                    alarmCounterIcon.setVisibility(View.VISIBLE);
                    alarmCounterIcon.startAnimation();

                    reportingAlert.setVisibility(GONE);
                    prevState = COLLAPSED;
                }
            }
        });

        alarmCounterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(EXPANDED);
            }
        });

        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        DashboardFragment dashboardFragment = new DashboardFragment();
        ProductsListFragment productsListFragment = new ProductsListFragment();

        viewPagerAdapter.addFragment(dashboardFragment, "Dashboard");
        viewPagerAdapter.addFragment(productsListFragment, "Products List");

        viewPager.setAdapter(viewPagerAdapter);

        ColorDrawable cd = new ColorDrawable(0xFF666666);
        viewPager.setPageMargin(convertDip2Pixels(MainActivity.this, 1));
        viewPager.setPageMarginDrawable(cd);


        tabLayout = findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons();
            }
        });


        inkPageIndicator = findViewById(R.id.indicator);

        updateStockViewPager = findViewById(R.id.view_pager);
        updateStockViewPager.setOffscreenPageLimit(20);


        new AsyncTask<Void, Void, List<ProductList>>() {

            @Override
            protected List<ProductList> doInBackground(Void... voids) {
                return baseDatabase.productsModelDao().getAvailableProductsCheck();
            }

            @Override
            protected void onPostExecute(List<ProductList> productLists) {
                super.onPostExecute(productLists);
                Log.d(TAG, "products list size = " + productLists.size());

                managedProductsCount = productLists.size();
                DotIndicatorPagerAdapter adapter = new DotIndicatorPagerAdapter(getSupportFragmentManager(), productLists);
                updateStockViewPager.setAdapter(adapter);
                inkPageIndicator.setViewPager(viewPager);
            }
        }.execute();


        updateStockViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        scheduleAlarm();

        boolean initializeStock = getIntent().getBooleanExtra("reportStock",false);

        Log.d(TAG, "isFirstLogin = " + session.getIsFirstLogin());
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            sessionManager.checkLogin();
            finish();
        } else if (session.getIsFirstLogin()) {
            Intent i = new Intent(MainActivity.this, ManagedProductsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(i);
            finish();
        }else if(initializeStock){
            slidingUpPanelLayout.setPanelState(EXPANDED);
        }
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
            } catch (Exception e) {
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

    public void moveToNextProduct() {
        int nextItem = updateStockViewPager.getCurrentItem() + 1;

        if (nextItem < managedProductsCount) {
            updateStockViewPager.setCurrentItem(nextItem, true);
        } else {
            slidingUpPanelLayout.setPanelState(COLLAPSED);
        }
    }

    public static class DotIndicatorPagerAdapter extends FragmentPagerAdapter {
        private List<ProductList> productLists;

        DotIndicatorPagerAdapter(FragmentManager fm, List<ProductList> productLists) {
            super(fm);
            this.productLists = productLists;
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public float getPageWidth(int position) {

            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
            if (tabletSize) {
                return 0.5f;
            }else{
                return 1f;
            }


        }
    }

}