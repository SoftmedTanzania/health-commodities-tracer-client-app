package com.softmed.stockapp.Activities;

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

import com.softmed.stockapp.Utils.LoadProductPhotoAsync;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.Dom.entities.Unit;
import com.softmed.stockapp.Fragments.ProductPhotoDialogFragment;
import com.softmed.stockapp.Utils.PhotoHelper;
import com.softmed.stockapp.Utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.softmed.stockapp.Utils.Calendars.toBeginningOfTheDay;


public class CreateProductActivity extends AppCompatActivity implements DialogInterface.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = CreateProductActivity.class.getSimpleName();
    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    /**
     * Permissions required to read and write contacts. Used by the {@link CreateProductActivity}.
     */
    private static String[] PERMISSIONS_EXTERNAL_STORAGE= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    private ImageView mProductPhotoImageView;
    private TextInputLayout mProductPriceTIL;
    private TextInputLayout mProductQuantityTIL;
    private TextInputLayout mProductNameTIL;
    private TextInputLayout mProductDescriptionTIL;
    private TextView description;
    private Product mPassedProduct;
    private String mTempPhotoFilePath;

    private MaterialSpinner categorySpinner,subCategorySpinner,unitsOfMeasureSpinner;
    private List<Product> products;
    private int categoryId,unitId,productId;
    private String subCategoryName, productName;
    public static AppDatabase baseDatabase;
    private List<Category> categories;
    private List<Unit> unitsOfMeasure;
    private  List<String> categoryStrings= new ArrayList<>();
    private  List<String> unitsStrings= new ArrayList<>();

    // Session Manager Class
    private SessionManager session;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        baseDatabase = AppDatabase.getDatabase(this);

        mProductPhotoImageView = (ImageView) findViewById(R.id.product_photo_image_view);
        mProductPriceTIL = (TextInputLayout) findViewById(R.id.clients_on_regime_input_layout);
        mProductQuantityTIL = (TextInputLayout) findViewById(R.id.product_stock_on_hand_input_layout);
        mProductDescriptionTIL = (TextInputLayout) findViewById(R.id.product_description_text_input_layout);
        mProductNameTIL = (TextInputLayout) findViewById(R.id.product_name_text_input_layout);
        description = (TextView) findViewById(R.id.product_description);

        categorySpinner = (MaterialSpinner) findViewById(R.id.spin_category_spinner);
        subCategorySpinner = (MaterialSpinner) findViewById(R.id.sub_category_spinner);
        unitsOfMeasureSpinner = (MaterialSpinner) findViewById(R.id.unit_of_measure_spinner);
        mPassedProduct = getIntent().getParcelableExtra(INTENT_EXTRA_PRODUCT);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle((mPassedProduct == null) ? R.string.title_action_bar_add_product : R.string.title_action_bar_edit_product);

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

        mProductNameTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i >= 0 && mProductNameTIL.isErrorEnabled())
                    mProductNameTIL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mProductDescriptionTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i >= 0 && mProductDescriptionTIL.isErrorEnabled())
                    mProductDescriptionTIL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                categories = baseDatabase.categoriesModel().getAllCategories();
                unitsOfMeasure = baseDatabase.unitsDao().getUnit();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                for(Category category:categories){
                    categoryStrings.add(category.getName());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(CreateProductActivity.this, R.layout.simple_spinner_item_black, categoryStrings);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                categorySpinner.setAdapter(spinAdapter);



                for(Unit unit:unitsOfMeasure){
                    unitsStrings.add(unit.getName());
                    Log.d(TAG,"Units Name = "+unit.getName());
                }
                ArrayAdapter<String> uAdapter = new ArrayAdapter<String>(CreateProductActivity.this, R.layout.simple_spinner_item_black, unitsStrings);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                unitsOfMeasureSpinner.setAdapter(uAdapter);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                try {
                    subCategoryName = categories.get(i).getName();
                    categoryId = categories.get(i).getId();
                }catch (Exception e){
                    e.printStackTrace();
                    subCategoryName="";
                    categoryId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        unitsOfMeasureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                try {
                    unitId = unitsOfMeasure.get(i).getId();
                }catch (Exception e){
                    unitId = -1;
                    e.printStackTrace();
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
                            Product product = new Product();
                            product.setDescription(mProductDescriptionTIL.getEditText().getText().toString());
                            product.setName(mProductNameTIL.getEditText().getText().toString());
                            product.setUnit_id(unitId);
                            product.setPrice(Integer.valueOf(mProductPriceTIL.getEditText().getText().toString()));
                            product.setUuid(UUID.randomUUID().toString());
                            product.setStatus(0);

                            Random rand = new Random();
                            productId = rand.nextInt(100000) + 5000;
                            product.setId(productId);

                            Balances b = null;
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
                            transaction.setClientsOnRegime(Integer.valueOf(mProductPriceTIL.getEditText().getText().toString()));
                            transaction.setUser_id(Integer.valueOf(session.getUserUUID()));
                            transaction.setUuid(UUID.randomUUID().toString());

                            transaction.setTransactiontype_id(1);
                            transaction.setStatus_id(1);

                            Calendar c = Calendar.getInstance();
                            toBeginningOfTheDay(c);
                            transaction.setCreated_at(c.getTimeInMillis());

                            baseDatabase.productsModelDao().addProduct(product);
                            baseDatabase.balanceModelDao().addBalance(balances);
                            baseDatabase.transactionsDao().addTransactions(transaction);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void v) {
                            super.onPostExecute(v);

                            Intent previousActivityIntent = new Intent(CreateProductActivity.this, MainActivity.class);

                            // Return to the previous activity which called this activity.
                            NavUtils.navigateUpTo(CreateProductActivity.this, previousActivityIntent);

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
     * Method for validating user inputs when the user presses the 'Done' action button.
     * The inputs that needs to be validated are: product name, supplier, supplier email, price, and quantity.
     * @return true if all required inputs are present, false if not present.
     */
    private boolean validateUserInput() {
       boolean isTypeIdSet = productId != -1;
        boolean isProductPriceSet = !TextUtils.isEmpty(mProductPriceTIL.getEditText().getText());
        boolean isProductQtySet = !TextUtils.isEmpty(mProductQuantityTIL.getEditText().getText());


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
        balances.setNumberOfClientsOnRegime(Integer.valueOf(mProductPriceTIL.getEditText().getText().toString()));
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
                                    .requestPermissions(CreateProductActivity.this, PERMISSIONS_EXTERNAL_STORAGE,
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