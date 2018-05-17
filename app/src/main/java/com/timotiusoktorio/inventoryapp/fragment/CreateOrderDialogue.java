package com.timotiusoktorio.inventoryapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.timotiusoktorio.inventoryapp.LoadProductPhotoAsync;
import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.activity.AddProductActivity;
import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.Product;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;


/**
 * Dialog allowing users to select a date.
 */
public class CreateOrderDialogue extends android.support.v4.app.DialogFragment {
    private static final String TAG=CreateOrderDialogue.class.getSimpleName();
    private boolean sentSucessfully=false;
    double longitude;
    double latitude;
    private RelativeLayout dialogueLayout;
    private MaterialSpinner categorySpinner,subCategorySpinner, productsSpinner;
    private List<SubCategory> subCategories;
    private List<Product> products;
    private TextView description;
    private long typeId;
    public static AppDatabase baseDatabase;
    private  List<Category> categories;
    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    /**
     * Permissions required to read and write contacts. Used by the {@link AddProductActivity}.
     */
    private static String[] PERMISSIONS_EXTERNAL_STORAGE= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    public CreateOrderDialogue() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        final Activity activity = getActivity();
        baseDatabase = AppDatabase.getDatabase(getActivity());
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);



        dialogueLayout =(RelativeLayout)inflater.inflate(R.layout.dialogue_create_order, container, false);
        final ViewPager viewPager = (ViewPager) dialogueLayout.findViewById(R.id.viewPagerVertical);



        description = (TextView) dialogueLayout.findViewById(R.id.product_description);
        categorySpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.spin_category_spinner);
        subCategorySpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.sub_category_spinner);
        productsSpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.products_spinner);




//
//        new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... voids) {
//                categories = baseDatabase.categoriesModel().getAllCategories();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                for(Category category:categories){
//                    categoryStrings.add(category.getName());
//                }
//
//                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.simple_spinner_item_black, categoryStrings);
//                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
//                categorySpinner.setAdapter(spinAdapter);
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        final List<String> categoryStrings= new ArrayList<>();
        for(Category category:categories){
            categoryStrings.add(category.getName());
        }

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, categoryStrings);
        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
        categorySpinner.setAdapter(spinAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subCategories = baseDatabase.subCategoriesModel().getSubCategoryByCategoryId(i+1);

                final List<String> subCategoryStrings= new ArrayList<>();
                for(SubCategory subCategory:subCategories){
                    subCategoryStrings.add(subCategory.getName());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, subCategoryStrings);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                subCategorySpinner.setAdapter(spinAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                products = new ArrayList<>();
                try {
                    products = baseDatabase.productsModelDao().getProductsBySubCategoryId(subCategories.get(i).getId());
                }catch (Exception e){
                    e.printStackTrace();
                }

                final List<String> productsNames= new ArrayList<>();
                for(Product product:products){
                    productsNames.add(product.getName());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, productsNames);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                productsSpinner.setAdapter(spinAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    description.setText(products.get(i).getDescription());
                    typeId = products.get(i).getId();
                }catch (Exception e){
                    typeId = -1;
                    description.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        viewPager.setAdapter(new ViewPagerAdapter());


        dialogueLayout.findViewById(R.id.another_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        dialogueLayout.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        dialogueLayout.findViewById(R.id.product_photo_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductPhotoDialog(view);
            }
        });


        return dialogueLayout;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onActivityCreated(Bundle arg0){
        super.onActivityCreated(arg0);
//        getDialog().getWindow().getAttributes().windowAnimations= R.style.dialogue_animation;
    }


    /**
     * Method that gets invoked when the user presses the 'photo camera' floating action button.
     * This method will inflate the product photo dialog using the product photo dialog fragment.
     * @param view - 'photo camera' floating action button.
     */
    public void showProductPhotoDialog(View view) {
        ProductPhotoDialogFragment dialogFragment = new ProductPhotoDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), PRODUCT_PHOTO_DIALOG_TAG);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.add_product_layout;
                    break;
                case 1:
                    resId = R.id.add_product_with_image;
                    break;
            }
            return dialogueLayout.findViewById(resId);
        }
    }

    public void showImage(ImageView v, String photoPath) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            setRequestExternalStoragePermissions();

        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
//            Glide.with(getApplicationContext()).load(photoPath).into(mProductPhotoImageView);
            new LoadProductPhotoAsync(getActivity(), v).execute(photoPath);
            Log.d(TAG,"show status");

        }
    }

    /**
     * Requests the EXTERNAL STORAGE permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void setRequestExternalStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG, "Displaying external storage  permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(dialogueLayout.findViewById(R.id.content), R.string.permission_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(getActivity(), PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "requesting external storage permission.");
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }


}
