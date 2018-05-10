package com.timotiusoktorio.inventoryapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.database.ProductDbHelper;
import com.timotiusoktorio.inventoryapp.fragment.DashboardFragment;
import com.timotiusoktorio.inventoryapp.fragment.OrdersFragment;
import com.timotiusoktorio.inventoryapp.fragment.ProductsListFragment;
import com.timotiusoktorio.inventoryapp.model.CategorySubCategory;
import com.timotiusoktorio.inventoryapp.model.Model;
import com.timotiusoktorio.inventoryapp.model.Type;
import com.timotiusoktorio.inventoryapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = AppDatabase.getDatabase(this);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()){
            sessionManager.checkLogin();
            finish();
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        DashboardFragment dashboardFragment = new DashboardFragment();
        ProductsListFragment productsListFragment = new ProductsListFragment();
        OrdersFragment ordersFragment = new OrdersFragment();

        viewPagerAdapter.addFragment(dashboardFragment,"Dashboard");
        viewPagerAdapter.addFragment(productsListFragment,"Products List");
//        viewPagerAdapter.addFragment(ordersFragment,"Order Fragment");

        viewPager.setAdapter(viewPagerAdapter);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_empty_products) {
//            ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
//                    R.string.title_dialog_confirm_empty_products, R.string.message_dialog_confirm_empty_products);
//            dialogFragment.setOnPositiveClickListener(mOnPositiveClickListener);
//            dialogFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
        }else  if (item.getItemId() == R.id.action_create_order) {
                Intent intent = new Intent(MainActivity.this,CreateOrderActivity.class);
                startActivity(intent);
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
    }

    public void setupTabIcons() {


        View dashboard = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView dashboardTitle = dashboard.findViewById(R.id.title_text);
        dashboardTitle.setText("Dashboard");
        ImageView dashboardIcon    = dashboard.findViewById(R.id.icon);
        dashboardIcon.setColorFilter(this.getResources().getColor(R.color.white));
        Glide.with(this).load(R.drawable.ic_dashboard_white_24dp).into(dashboardIcon);
        tabLayout.getTabAt(0).setCustomView(dashboard);


        View myInventory = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView opdTabTitle = myInventory.findViewById(R.id.title_text);
        opdTabTitle.setText("My Inventory");
        ImageView inventoryIcon    = myInventory.findViewById(R.id.icon);
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
            tabLayout.getTabAt(2).setCustomView(orderView);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}