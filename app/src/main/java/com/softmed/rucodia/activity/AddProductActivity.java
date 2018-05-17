package com.softmed.rucodia.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softmed.rucodia.LoadProductPhotoAsync;
import com.softmed.rucodia.R;
import com.softmed.rucodia.database.AppDatabase;
import com.softmed.rucodia.dom.objects.Balances;
import com.softmed.rucodia.dom.objects.Category;
import com.softmed.rucodia.dom.objects.Product;
import com.softmed.rucodia.dom.objects.SubCategory;
import com.softmed.rucodia.dom.objects.Transactions;
import com.softmed.rucodia.dom.objects.Unit;
import com.softmed.rucodia.fragment.ProductPhotoDialogFragment;
import com.softmed.rucodia.helper.PhotoHelper;
import com.softmed.rucodia.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;


public class AddProductActivity extends AppCompatActivity implements DialogInterface.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = AddProductActivity.class.getSimpleName();
    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    /**
     * Permissions required to read and write contacts. Used by the {@link AddProductActivity}.
     */
    private static String[] PERMISSIONS_EXTERNAL_STORAGE= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    private ImageView mProductPhotoImageView;
    private TextInputLayout mProductPriceTIL;
    private TextInputLayout mProductQuantityTIL;
    private TextView description;
    private Product mPassedProduct;
    // Global variable to hold the path of the photo file which gets created each time the user
    // capture a photo using camera intent. This is stored globally so the file can be accessed
    // later on. If the user cancel taking a picture via the camera intent or decided to choose
    // a photo from the gallery instead, the file needs to be deleted as it's no longer needed.
    private String mTempPhotoFilePath;

    private MaterialSpinner categorySpinner,subCategorySpinner, productsSpinner;
    private List<SubCategory> subCategories;
    private List<Product> products;
    private int productId;
    private String subCategoryName, productName;
    public static AppDatabase baseDatabase;
    private List<Category> categories;
    private  List<String> categoryStrings= new ArrayList<>();

    // Session Manager Class
    private SessionManager session;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        baseDatabase = AppDatabase.getDatabase(this);

        mProductPhotoImageView = (ImageView) findViewById(R.id.product_photo_image_view);
        mProductPriceTIL = (TextInputLayout) findViewById(R.id.product_price_text_input_layout);
        mProductQuantityTIL = (TextInputLayout) findViewById(R.id.product_quantity_text_input_layout);
        description = (TextView) findViewById(R.id.product_description);


        categorySpinner = (MaterialSpinner) findViewById(R.id.spin_category_spinner);
        subCategorySpinner = (MaterialSpinner) findViewById(R.id.sub_category_spinner);
        productsSpinner = (MaterialSpinner) findViewById(R.id.products_spinner);


        mPassedProduct = getIntent().getParcelableExtra(INTENT_EXTRA_PRODUCT);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle((mPassedProduct == null) ? R.string.title_action_bar_add_product : R.string.title_action_bar_edit_product);

        // If there is a product object passed from the intent, populate the views with the product data.
        //TODO handle this
//        if (mPassedProduct != null) populateViewsWithPassedProductData();

        // Add text changed listener to all text fields that needs to be validated.
        mProductPriceTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i >= 0 && mProductPriceTIL.isErrorEnabled())
                    mProductPriceTIL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mProductQuantityTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i >= 0 && mProductQuantityTIL.isErrorEnabled())
                    mProductQuantityTIL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                categories = baseDatabase.categoriesModel().getAllCategories();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                for(Category category:categories){
                    categoryStrings.add(category.getName());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.simple_spinner_item_black, categoryStrings);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                categorySpinner.setAdapter(spinAdapter);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        subCategories = baseDatabase.subCategoriesModel().getSubCategoryByCategoryId(i+1);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        final List<String> subCategoryStrings= new ArrayList<>();
                        for(SubCategory subCategory:subCategories){
                            subCategoryStrings.add(subCategory.getName());
                        }

                        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.simple_spinner_item_black, subCategoryStrings);
                        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                        subCategorySpinner.setAdapter(spinAdapter);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {

                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        products = new ArrayList<>();
                        try {
                            subCategoryName = subCategories.get(i).getName();
                            products = baseDatabase.productsModelDao().getProductsBySubCategoryId(subCategories.get(i).getId());
                        }catch (Exception e){
                            e.printStackTrace();
                            subCategoryName="";
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        final List<String> ProductsNames= new ArrayList<>();
                        for(Product product:products){
                            ProductsNames.add(product.getName());
                        }

                        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.simple_spinner_item_black, ProductsNames);
                        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                        productsSpinner.setAdapter(spinAdapter);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,final int i, long l) {
                try {
                    productName = products.get(i).getName();
                    description.setText(products.get(i).getDescription());
                    productId = products.get(i).getId();

                    new AsyncTask<Void, Void, Unit>(){
                        @Override
                        protected Unit doInBackground(Void... voids) {
                            Unit unit = baseDatabase.unitsDao().getUnit(products.get(i).getUnitId());
                            return unit;
                        }

                        @Override
                        protected void onPostExecute(Unit unit) {
                            super.onPostExecute(unit);

                            String priceHint= "Product Price / "+unit.getName();
                            mProductPriceTIL.getEditText().setHint(priceHint);

                            String hint="Product Quantity / "+unit.getName();
                            mProductQuantityTIL.getEditText().setHint(hint);

                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                }catch (Exception e){
                    productId = -1;
                    productName = "";
                    description.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check the request code to determine which intent was dispatched.
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                // Take photo successful. If the user has previously set a captured photo on the
                // ImageView, that photo file needs to be deleted since it will be replaced now.
                PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
                // Save the file uri as a tag and display the captured photo on the ImageView.
                mProductPhotoImageView.setTag(mTempPhotoFilePath);
                showImage(mProductPhotoImageView,mTempPhotoFilePath);
            } else if (resultCode == RESULT_CANCELED) {
                // The user cancelled taking a photo. The photo file created from the camera intent
                // is just an empty file so delete it since we don't need it anymore.
                File photoFile = new File(mTempPhotoFilePath);
                photoFile.delete();
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                // Choose photo successful. Delete previously captured photo file if there's any.
                PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
                // Save the file uri as a tag and display the selected photo on the ImageView.
                String photoPath = data.getData().toString();
                mProductPhotoImageView.setTag(photoPath);
                showImage(mProductPhotoImageView,photoPath);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            // The user presses the 'Done' action button. Validate user inputs before proceeding.
            if (validateUserInput()) {

                // If passed product object is present, update the product with the new data.
                // Otherwise, create a new product object with the data and save it to the database.
                if (mPassedProduct != null) {
                    //TODO handle updating of product information
//                    buildProductWithUserInputData(mPassedProduct);
//                    mDbHelper.updateProduct(mPassedProduct);

                    // Set previousActivityIntent to DetailActivity and pass back the updated product object.
                    Intent previousActivityIntent = new Intent(this, DetailActivity.class);
                    previousActivityIntent.putExtra(INTENT_EXTRA_PRODUCT, mPassedProduct);

                    // Return to the previous activity which called this activity.
                    NavUtils.navigateUpTo(this, previousActivityIntent);
                } else {

                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            Balances b = baseDatabase.balanceModelDao().getBalance(productId);
                            String uuid=null;
                            int balance = 0;
                            if(b!=null){
                                uuid = b.getUuid();
                                balance = b.getBalance();
                            }

                            Balances balances =  buildProductWithUserInputData(uuid,balance);

                            Transactions transaction = new Transactions();


                            transaction.setProduct_id(productId);
                            transaction.setAmount(Integer.valueOf(mProductQuantityTIL.getEditText().getText().toString()));
                            transaction.setPrice(Integer.valueOf(mProductPriceTIL.getEditText().getText().toString()));
                            transaction.setUser_id(Integer.valueOf(session.getUserUUID()));
                            transaction.setUuid(UUID.randomUUID().toString());

                            //TODO remove hardcoding of ids
                            transaction.setTransactiontype_id(1);
                            transaction.setStatus_id(1);

                            baseDatabase.balanceModelDao().addBalance(balances);
                            baseDatabase.transactionsDao().addTransactions(transaction);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void v) {
                            super.onPostExecute(v);

                            Intent previousActivityIntent = new Intent(AddProductActivity.this, MainActivity.class);

                            // Return to the previous activity which called this activity.
                            NavUtils.navigateUpTo(AddProductActivity.this, previousActivityIntent);

                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                }
            }
        } else if (item.getItemId() == android.R.id.home) {
            // The user presses the 'Back' action button. Before returning to MainActivity, check
            // if the user has set a captured photo to the ImageView because it needs to be deleted.
            if (mPassedProduct == null) PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * AlertDialog interface callback method which gets invoked when the user selects one of the
     * available options on the product photo dialog.
     * @param dialogInterface - the dialog interface.
     * @param i - position of the selected option.
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            // The user selects 'Take photo' option. Dispatch the camera intent.
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create a File where the photo will be saved to.
                File photoFile = null;
                try {
                    photoFile = PhotoHelper.createPhotoFile(this);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                if (photoFile != null) {
                    // Save the photo file path globally.
                    mTempPhotoFilePath = photoFile.getAbsolutePath();
                    // Get the file content URI using FileProvider to avoid FileUriExposedException.
                    Uri photoUri = FileProvider.getUriForFile(this, getString(R.string.authority), photoFile);
                    // Set the file content URI as an intent extra and dispatch the camera intent.
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PHOTO);
                }
            }
        } else {
            // The user selects 'Choose photo' option. Dispatch the choose photo intent.
            Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (choosePhotoIntent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(choosePhotoIntent, REQUEST_CODE_CHOOSE_PHOTO);
        }
    }

    /**
     * Method that gets invoked when the user presses the product photo image view.
     * This method will dispatch a view intent to open the product photo in full screen.
     * @param view - FrameLayout (ViewGroup that contains the product photo image view).
     */
    public void viewProductPhoto(View view) {
        PhotoHelper.dispatchViewImageIntent(this, mProductPhotoImageView.getTag());
    }

    /**
     * Method that gets invoked when the user presses the 'photo camera' floating action button.
     * This method will inflate the product photo dialog using the product photo dialog fragment.
     * @param view - 'photo camera' floating action button.
     */
    public void showProductPhotoDialog(View view) {
        ProductPhotoDialogFragment dialogFragment = new ProductPhotoDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), PRODUCT_PHOTO_DIALOG_TAG);
    }

    /**
     * Method for populating the views with the passed product data. Since the user might come to
     * this activity to edit an existing product object, all text fields and ImageView must be
     * populated by the product object.
     */
    private void populateViewsWithPassedProductData() {
        //TODO handle displaying of product photo
        String photoPath = "";
//        String photoPath = mPassedProduct.getmPhotoPath();
//        mProductPhotoImageView.setTag(photoPath);
        if (!TextUtils.isEmpty(photoPath))
            showImage(mProductPhotoImageView,photoPath);

//        mProductNameTIL.getEditText().setText(mPassedProduct.getmName());
//        mProductSupplierTIL.getEditText().setText(mPassedProduct.getmSupplier());
//        mProductSupplierEmailTIL.getEditText().setText(mPassedProduct.getSupplierEmail());
//        mProductPriceTIL.getEditText().setText(String.valueOf(mPassedProduct.getmPrice()));
//        mProductQuantityTIL.getEditText().setText(String.valueOf(mPassedProduct.getmQuantity()));
    }

    /**
     * Method for validating user inputs when the user presses the 'Done' action button.
     * The inputs that needs to be validated are: product name, supplier, supplier email, price, and quantity.
     * @return true if all required inputs are present, false if not present.
     */
    private boolean validateUserInput() {
       boolean isTypeIdSet = productId != -1;
        boolean isProductPriceSet = !TextUtils.isEmpty(mProductPriceTIL.getEditText().getText());
        boolean isProductQtySet = !TextUtils.isEmpty(mProductQuantityTIL.getEditText().getText());

        if (!isTypeIdSet) {
            productsSpinner.setError(getString(R.string.error_msg_product_type_empty));
        }


        if (!isProductPriceSet) {
            mProductPriceTIL.setError(getString(R.string.error_msg_product_price_empty));
            mProductPriceTIL.setErrorEnabled(true);
        }
        if (!isProductQtySet) {
            mProductQuantityTIL.setError(getString(R.string.error_msg_product_quantity_empty));
            mProductQuantityTIL.setErrorEnabled(true);
        }
        return  isTypeIdSet && isProductPriceSet && isProductQtySet;
    }

    /**
     * Method for extracting user inputs from the text fields to a balance object.
     */
    private Balances buildProductWithUserInputData(String uuid, int balance) {
        Log.d(TAG,"Product Id = "+productId);
        Balances balances = new Balances();

        balances.setProduct_id(productId);
        if(uuid==null) {
            balances.setUuid(UUID.randomUUID().toString());
        }else{
            balances.setUuid(uuid);
        }
        // Get the product photo path from the ImageView tag. The tag might contains null data, so
        // it needs to be checked. If it's null, set the photo path to an empty string.
        Object imageViewTag = mProductPhotoImageView.getTag();

        balances.setImage_path( (imageViewTag != null) ? imageViewTag.toString() : "" );
        balances.setPrice(Integer.valueOf(mProductPriceTIL.getEditText().getText().toString()));
        balances.setBalance(Integer.valueOf(mProductQuantityTIL.getEditText().getText().toString())+balance);

        return balances;
    }

    public void showImage(ImageView v, String photoPath) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            setRequestExternalStoragePermissions();

        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
//            Glide.with(getApplicationContext()).load(photoPath).into(mProductPhotoImageView);
            new LoadProductPhotoAsync(this, v).execute(photoPath);
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG, "Displaying external storage  permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(findViewById(R.id.content), R.string.permission_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(AddProductActivity.this, PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "requesting external storage permission.");
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

}