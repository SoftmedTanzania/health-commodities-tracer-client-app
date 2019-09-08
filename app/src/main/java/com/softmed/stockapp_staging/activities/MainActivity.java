package com.softmed.stockapp_staging.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.softmed.stockapp_staging.R;
import com.softmed.stockapp_staging.customViews.VerticalArrow;
import com.softmed.stockapp_staging.database.AppDatabase;
import com.softmed.stockapp_staging.dom.dto.ProducToBeReportedtList;
import com.softmed.stockapp_staging.dom.entities.Location;
import com.softmed.stockapp_staging.dom.entities.UsersInfo;
import com.softmed.stockapp_staging.fragments.DashboardFragment;
import com.softmed.stockapp_staging.fragments.ProductsListFragment;
import com.softmed.stockapp_staging.fragments.UpcomingReportingScheduleFragment;
import com.softmed.stockapp_staging.fragments.UpdateStockFragment;
import com.softmed.stockapp_staging.utils.SessionManager;
import com.softmed.stockapp_staging.utils.ZoomOutPageTransformer;
import com.softmed.stockapp_staging.viewmodels.MessageListViewModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tarek360.animated.icons.AnimatedIconView;
import tarek360.animated.icons.drawables.NotificationAlert;

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
    private AnimatedIconView arrowUp, reportingAlert, alarmCounterIcon;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private NotificationAlert notificationAlert, notificationAlert1;
    private AppDatabase baseDatabase;
    private DotIndicatorPagerAdapter adapter;
    private List<UsersInfo> usersInfos;
    private SessionManager sessionManager;
    private String districtFacility = "";

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

        baseDatabase = AppDatabase.getDatabase(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrowUp = findViewById(R.id.animated_arrow_up);
        arrowUp.setAnimatedIcon(new VerticalArrow());

        reportingAlert = findViewById(R.id.alarm_counter);
        alarmCounterIcon = findViewById(R.id.alarm_counter_icon);

        notificationAlert = new NotificationAlert();
        notificationAlert1 = new NotificationAlert();

        reportingAlert.setAnimatedIcon(notificationAlert);
        alarmCounterIcon.setAnimatedIcon(notificationAlert1);

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            private SlidingUpPanelLayout.PanelState prevState = COLLAPSED;

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == EXPANDED && prevState == COLLAPSED) {
                    arrowUp.startAnimation();

                    reportingAlert.setVisibility(View.VISIBLE);
                    reportingAlert.startAnimation();
                    alarmCounterIcon.setVisibility(GONE);
                    prevState = EXPANDED;
                } else if (newState == COLLAPSED && prevState == EXPANDED) {
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


        findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagesDialogsActivity.open(MainActivity.this);
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
        updateStockViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        updateStockViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == managedProductsCount) {
                    // Check if no view has focus:
                    View view = MainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        final boolean initializeStock = getIntent().getBooleanExtra("reportInitialStock", false);
        districtFacility = getIntent().getStringExtra("districtFacility");

        if (districtFacility == null) {
            districtFacility = "";
        }

        adapter = new DotIndicatorPagerAdapter(getSupportFragmentManager());
        updateStockViewPager.setAdapter(adapter);
//      inkPageIndicator.setViewPager(updateStockViewPager);

        new AsyncTask<Void, Void, Location>() {
            @Override
            protected Location doInBackground(Void... voids) {
                return baseDatabase.locationModelDao().getLocationById(session.getFacilityId());
            }

            @Override
            protected void onPostExecute(Location location) {
                super.onPostExecute(location);
                try {
                    Log.d(TAG, "Location Name = " + location.getName());
                    ((TextView) findViewById(R.id.title)).setText(location.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();


        Log.d(TAG, "isFirstLogin = " + session.getIsFirstLogin());
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            sessionManager.checkLogin();
            finish();
        } else if (session.getAssignedFacilityType().equals("DST") && districtFacility.equals("")) {
            Intent i = new Intent(MainActivity.this, SelectFacilityActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(i);
            finish();

        } else if (session.getIsFirstLogin()) {
            Intent i = new Intent(MainActivity.this, ManagedProductsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(i);
            finish();
        } else if (initializeStock) {
            updateStockViewpager();

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    slidingUpPanelLayout.setPanelState(EXPANDED);
                }
            }.execute();


        } else {
            updateStockViewpager();
        }


        MessageListViewModel messageListViewModel = ViewModelProviders.of(this).get(MessageListViewModel.class);
        try {
            messageListViewModel.getUnreadMessageCountUserId(Integer.parseInt(session.getUserUUID())).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    ((TextView) findViewById(R.id.badge_notification_1)).setText(String.valueOf(integer));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @SuppressLint("StaticFieldLeak")
    public void updateStockViewpager() {
        new AsyncTask<Void, Void, List<ProducToBeReportedtList>>() {

            @Override
            protected List<ProducToBeReportedtList> doInBackground(Void... voids) {
                Log.d(TAG, "All schedules = " + new Gson().toJson(baseDatabase.productReportingScheduleModelDao().getAllProductReportingSchedule()));
                usersInfos = baseDatabase.userInfoDao().loggeInUser(session.getUserName());
                return baseDatabase.productsModelDao().getUnreportedProductStocks(Calendar.getInstance().getTimeInMillis(), session.getFacilityId());
            }

            @Override
            protected void onPostExecute(List<ProducToBeReportedtList> productLists) {
                super.onPostExecute(productLists);


                Log.d(TAG, "Unposted schedules = " + new Gson().toJson(productLists));

                if (usersInfos.size() == 0) {
                    session.logoutUser();
                    finish();
                }

                adapter.removeAllFragments();
                managedProductsCount = productLists.size();

                for (ProducToBeReportedtList products : productLists) {
                    adapter.addFragment(UpdateStockFragment.newInstance(products));
                }
                adapter.addFragment(new UpcomingReportingScheduleFragment());

                adapter.notifyChangeInPosition(50);
                adapter.notifyDataSetChanged();
                updateStockViewPager.setCurrentItem(0);

                notificationAlert = new NotificationAlert();
                notificationAlert.setNotificationCount(managedProductsCount);

                notificationAlert1 = new NotificationAlert();
                notificationAlert1.setNotificationCount(managedProductsCount);
                reportingAlert.setAnimatedIcon(notificationAlert);
                alarmCounterIcon.setAnimatedIcon(notificationAlert1);
                alarmCounterIcon.startAnimation();

            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            if (session.getAssignedFacilityType().equals("DST")) {
                getMenuInflater().inflate(R.menu.menu_district, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_main, menu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.logoutUser();
            finish();
        } else if (item.getItemId() == R.id.action_change_facility) {
            Intent i = new Intent(MainActivity.this, SelectFacilityActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(i);
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

    }


    @SuppressLint("StaticFieldLeak")
    public void moveToNextProduct() {
        int nextItem = updateStockViewPager.getCurrentItem() + 1;

        if (nextItem <= managedProductsCount) {
            updateStockViewPager.setCurrentItem(nextItem, true);
        } else {
            Log.d(TAG, "Updating viewpager");
            slidingUpPanelLayout.setPanelState(COLLAPSED);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    updateStockViewpager();
                }
            }.execute();


        }
    }

    public static class DotIndicatorPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        private long baseId = 0;

        DotIndicatorPagerAdapter(FragmentManager fm) {
            super(fm);
            this.mFragmentList = new ArrayList<>();
            Log.d(TAG, "initialized adapter");
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        void removeAllFragments() {
            mFragmentList.clear();
        }

        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }


        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         *
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
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
            } else {
                return 1f;
            }


        }
    }

}