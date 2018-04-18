package com.timotiusoktorio.inventoryapp.activity;


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
import com.timotiusoktorio.inventoryapp.database.ProductDbHelper;
import com.timotiusoktorio.inventoryapp.fragment.DashboardFragment;
import com.timotiusoktorio.inventoryapp.fragment.OrdersFragment;
import com.timotiusoktorio.inventoryapp.fragment.ProductsListFragment;
import com.timotiusoktorio.inventoryapp.model.CategorySubCategory;
import com.timotiusoktorio.inventoryapp.model.Model;
import com.timotiusoktorio.inventoryapp.model.Type;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProductDbHelper productDbHelper = new ProductDbHelper(this);
        if(productDbHelper.getCategories().size()>0){
            List<Model> categories = new ArrayList<>();
            Model seeds = new Model(1,"Mbegu Bora",true);
            Model viuatilifu = new Model(1,"Viuatilifu",true);

            categories.add(seeds);
            categories.add(viuatilifu);
            productDbHelper.insertCategories(categories);



            Model subMahindi = new Model(1,"Mahindi",true);
            Model subMaharage = new Model(2,"Maharage",true);
            Model subMihogo = new Model(3,"Mihogo",true);
            Model submagugu = new Model(4,"Dawa za kuua magugu",true);
            Model subWadudu = new Model(5,"Dawa za kuuwa wadudu shambani",true);
            Model subUkungu = new Model(6,"Dawa za kuzuia ukungu shamba",true);

            List<Model> subCategories = new ArrayList<>();
            subCategories.add(subMahindi);
            subCategories.add(subMaharage);
            subCategories.add(subMihogo);
            subCategories.add(submagugu);
            subCategories.add(subWadudu);
            subCategories.add(subUkungu);
            productDbHelper.insertSubCategories(subCategories);


            CategorySubCategory seeds1 = new CategorySubCategory();
            seeds1.setmId(1);
            seeds1.setCategoryId(1);
            seeds1.setSubCategoryId(1);

            CategorySubCategory seeds2 = new CategorySubCategory();
            seeds2.setmId(2);
            seeds2.setCategoryId(1);
            seeds2.setSubCategoryId(2);

            CategorySubCategory seeds3 = new CategorySubCategory();
            seeds3.setmId(3);
            seeds3.setCategoryId(1);
            seeds3.setSubCategoryId(3);


            CategorySubCategory viuatilifu1 = new CategorySubCategory();
            viuatilifu1.setmId(4);
            viuatilifu1.setCategoryId(2);
            viuatilifu1.setSubCategoryId(4);

            CategorySubCategory viuatilifu2 = new CategorySubCategory();
            viuatilifu2.setmId(5);
            viuatilifu2.setCategoryId(2);
            viuatilifu2.setSubCategoryId(5);


            CategorySubCategory viuatilifu3 = new CategorySubCategory();
            viuatilifu3.setmId(6);
            viuatilifu3.setCategoryId(2);
            viuatilifu3.setSubCategoryId(6);

            List<CategorySubCategory> categorySubCategories = new ArrayList<>();
            categorySubCategories.add(seeds1);
            categorySubCategories.add(seeds2);
            categorySubCategories.add(seeds3);
            categorySubCategories.add(viuatilifu1);
            categorySubCategories.add(viuatilifu2);
            categorySubCategories.add(viuatilifu3);
            productDbHelper.insertCategoriesSubCategories(categorySubCategories);

            List<Type> typeList = new ArrayList<>();


            Type typeMaize1 = new Type(1,1,"TZH -538 (FARU)","Kukomaa siku 110-120",true);
            Type typeMaize2 = new Type(2,1,"TZH -536 (FARU)","Kukomaa siku 110-120",true);
            Type typeMaize3 = new Type(3,1,"TZH -523 (FARU)","Kukomaa siku 90-110",true);
            Type typeMaize4 = new Type(4,1,"MERU HB  513","Kukomaa siku 75-90",true);


            Type typeBeans1 = new Type(5,2,"Njano Uyole","Kukomaa siku 90",true);
            Type typeBeans2 = new Type(6,2,"Lyamungo 90 ","Kukomaa siku 90",true);
            Type typeBeans3 = new Type(7,2,"Lyamungo 85 ","Kukomaa siku 90",true);
            Type typeBeans4 = new Type(8,2,"Jesca  ","Kukomaa siku 70",true);
            Type typeBeans5 = new Type(9,2,"Lyamungo ","Kukomaa siku 70",true);


            Type typeCassava1 = new Type(10,3,"Mkombozi","",true);
            Type typeCassava2 = new Type(11,3,"Mkumba ","",true);
            Type typeCassava3 = new Type(12,3,"Kipusa ","",true);
            Type typeCassava4 = new Type(13,3,"Chereko ","",true);
            Type typeCassava5 = new Type(14,3,"Mkuranga-1 ","",true);


            Type typeViatilifu1 = new Type(15,4,"Ontil ","Isiyochagua magugu kwa ajili ya maandalizi ya shamba",true);
            Type typeViatilifu2 = new Type(16,4,"Primagram ","Inatumika kabla ya mazao kuota kwenye mahindi tu",true);
            Type typeViatilifu3 = new Type(17,4,"Bromos ","Inatumika baada ya mazao kuota kwenye mahindi tu",true);
            Type typeViatilifu4 = new Type(18,4,"Sateca ","Inatumika baada ya mazao kuota kwenye maharage tu",true);



            typeList.add(typeMaize1);
            typeList.add(typeMaize2);
            typeList.add(typeMaize3);
            typeList.add(typeMaize4);
            typeList.add(typeBeans1);
            typeList.add(typeBeans2);
            typeList.add(typeBeans3);
            typeList.add(typeBeans4);
            typeList.add(typeBeans5);
            typeList.add(typeCassava1);
            typeList.add(typeCassava2);
            typeList.add(typeCassava3);
            typeList.add(typeCassava4);
            typeList.add(typeCassava5);
            typeList.add(typeViatilifu1);
            typeList.add(typeViatilifu2);
            typeList.add(typeViatilifu3);
            typeList.add(typeViatilifu4);


            productDbHelper.insertTypes(typeList);

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
        viewPagerAdapter.addFragment(ordersFragment,"Order Fragment");

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



        View orderView = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView orderTitle = orderView.findViewById(R.id.title_text);
        orderTitle.setText("Orders");
        ImageView orderIcon    = orderView.findViewById(R.id.icon);
        orderIcon.setColorFilter(this.getResources().getColor(R.color.white));
        Glide.with(this).load(R.drawable.ic_local_shipping_white_24dp).into(orderIcon);
        tabLayout.getTabAt(2).setCustomView(orderView);


    }

}