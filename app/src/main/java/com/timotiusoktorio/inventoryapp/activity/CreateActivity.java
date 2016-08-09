package com.timotiusoktorio.inventoryapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.timotiusoktorio.inventoryapp.LoadProductPhotoAsync;
import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.database.ProductDbHelper;
import com.timotiusoktorio.inventoryapp.fragment.ProductPhotoDialogFragment;
import com.timotiusoktorio.inventoryapp.helper.PhotoHelper;
import com.timotiusoktorio.inventoryapp.model.Product;

import java.io.File;
import java.io.IOException;

/**
 * Created by Timotius on 2016-08-03.
 */

public class CreateActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    private static final String LOG_TAG = CreateActivity.class.getSimpleName();
    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    private ImageView mProductPhotoImageView;
    private TextInputLayout mProductNameTIL;
    private TextInputLayout mProductCodeTIL;
    private TextInputLayout mProductSupplierTIL;
    private TextInputLayout mProductSupplierEmailTIL;
    private TextInputLayout mProductPriceTIL;
    private TextInputLayout mProductQuantityTIL;
    private ProductDbHelper mDbHelper;
    private Product mPassedProduct;
    // Global variable to hold the path of the photo file which gets created each time the user
    // capture a photo using camera intent. This is stored globally so the file can be accessed
    // later on. If the user cancel taking a picture via the camera intent or decided to choose
    // a photo from the gallery instead, the file needs to be deleted as it's no longer needed.
    private String mTempPhotoFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mProductPhotoImageView = (ImageView) findViewById(R.id.product_photo_image_view);
        mProductNameTIL = (TextInputLayout) findViewById(R.id.product_name_text_input_layout);
        mProductCodeTIL = (TextInputLayout) findViewById(R.id.product_code_text_input_layout);
        mProductSupplierTIL = (TextInputLayout) findViewById(R.id.product_supplier_text_input_layout);
        mProductSupplierEmailTIL = (TextInputLayout) findViewById(R.id.product_supplier_email_text_input_layout);
        mProductPriceTIL = (TextInputLayout) findViewById(R.id.product_price_text_input_layout);
        mProductQuantityTIL = (TextInputLayout) findViewById(R.id.product_quantity_text_input_layout);

        mDbHelper = ProductDbHelper.getInstance(getApplicationContext());
        mPassedProduct = getIntent().getParcelableExtra(INTENT_EXTRA_PRODUCT);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle((mPassedProduct == null) ? R.string.title_action_bar_add_product : R.string.title_action_bar_edit_product);

        // If there is a product object passed from the intent, populate the views with the product data.
        if (mPassedProduct != null) populateViewsWithPassedProductData();

        // Add text changed listener to all text fields that needs to be validated.
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
        mProductSupplierTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i >= 0 && mProductSupplierTIL.isErrorEnabled())
                    mProductSupplierTIL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mProductSupplierEmailTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i >= 0 && mProductSupplierEmailTIL.isErrorEnabled())
                    mProductSupplierEmailTIL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(mTempPhotoFilePath);
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
                new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(photoPath);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            // The user presses the 'Done' action button. Validate user inputs before proceeding.
            if (validateUserInput()) {
                Intent previousActivityIntent;
                // If passed product object is present, update the product with the new data.
                // Otherwise, create a new product object with the data and save it to the database.
                if (mPassedProduct != null) {
                    buildProductWithUserInputData(mPassedProduct);
                    mDbHelper.updateProduct(mPassedProduct);
                    // Set previousActivityIntent to DetailActivity and pass back the updated product object.
                    previousActivityIntent = new Intent(this, DetailActivity.class);
                    previousActivityIntent.putExtra(INTENT_EXTRA_PRODUCT, mPassedProduct);
                } else {
                    Product product = new Product();
                    buildProductWithUserInputData(product);
                    mDbHelper.insertProduct(product);
                    // Set previousActivityIntent to MainActivity.
                    previousActivityIntent = new Intent(this, MainActivity.class);
                }
                // Return to the previous activity which called this activity.
                NavUtils.navigateUpTo(this, previousActivityIntent);
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
                    Log.e(LOG_TAG, e.getMessage(), e);
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
        String photoPath = mPassedProduct.getPhotoPath();
        mProductPhotoImageView.setTag(photoPath);
        if (!TextUtils.isEmpty(photoPath))
            new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(photoPath);

        mProductNameTIL.getEditText().setText(mPassedProduct.getName());
        mProductCodeTIL.getEditText().setText(mPassedProduct.getCode());
        mProductSupplierTIL.getEditText().setText(mPassedProduct.getSupplier());
        mProductSupplierEmailTIL.getEditText().setText(mPassedProduct.getSupplierEmail());
        mProductPriceTIL.getEditText().setText(String.valueOf(mPassedProduct.getPrice()));
        mProductQuantityTIL.getEditText().setText(String.valueOf(mPassedProduct.getQuantity()));
    }

    /**
     * Method for validating user inputs when the user presses the 'Done' action button.
     * The inputs that needs to be validated are: product name, supplier, supplier email, price, and quantity.
     * @return true if all required inputs are present, false if not present.
     */
    private boolean validateUserInput() {
        boolean isProductNameSet = !TextUtils.isEmpty(mProductNameTIL.getEditText().getText());
        boolean isProductSupplierSet = !TextUtils.isEmpty(mProductSupplierTIL.getEditText().getText());
        boolean isProductSupplierEmailSet = !TextUtils.isEmpty(mProductSupplierEmailTIL.getEditText().getText());
        boolean isProductPriceSet = !TextUtils.isEmpty(mProductPriceTIL.getEditText().getText());
        boolean isProductQtySet = !TextUtils.isEmpty(mProductQuantityTIL.getEditText().getText());
        if (!isProductNameSet) {
            mProductNameTIL.setError(getString(R.string.error_msg_product_name_empty));
            mProductNameTIL.setErrorEnabled(true);
        }
        if (!isProductSupplierSet) {
            mProductSupplierTIL.setError(getString(R.string.error_msg_product_supplier_empty));
            mProductSupplierTIL.setErrorEnabled(true);
        }
        if (!isProductSupplierEmailSet) {
            mProductSupplierEmailTIL.setError(getString(R.string.error_msg_product_supplier_email_empty));
            mProductSupplierEmailTIL.setErrorEnabled(true);
        }
        if (!isProductPriceSet) {
            mProductPriceTIL.setError(getString(R.string.error_msg_product_price_empty));
            mProductPriceTIL.setErrorEnabled(true);
        }
        if (!isProductQtySet) {
            mProductQuantityTIL.setError(getString(R.string.error_msg_product_quantity_empty));
            mProductQuantityTIL.setErrorEnabled(true);
        }
        return isProductNameSet && isProductSupplierSet && isProductSupplierEmailSet && isProductPriceSet && isProductQtySet;
    }

    /**
     * Method for extracting user inputs from the text fields to a product object.
     * @param product - The product object.
     */
    private void buildProductWithUserInputData(Product product) {
        product.setName(mProductNameTIL.getEditText().getText().toString());
        product.setCode(mProductCodeTIL.getEditText().getText().toString());
        product.setSupplier(mProductSupplierTIL.getEditText().getText().toString());
        product.setSupplierEmail(mProductSupplierEmailTIL.getEditText().getText().toString());
        // Get the product photo path from the ImageView tag. The tag might contains null data, so
        // it needs to be checked. If it's null, set the photo path to an empty string.
        Object imageViewTag = mProductPhotoImageView.getTag();
        product.setPhotoPath( (imageViewTag != null) ? imageViewTag.toString() : "" );
        product.setPrice(Double.valueOf(mProductPriceTIL.getEditText().getText().toString()));
        product.setQuantity(Integer.valueOf(mProductQuantityTIL.getEditText().getText().toString()));
    }

}