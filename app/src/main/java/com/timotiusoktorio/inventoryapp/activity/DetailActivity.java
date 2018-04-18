package com.timotiusoktorio.inventoryapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.timotiusoktorio.inventoryapp.LoadProductPhotoAsync;
import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.database.ProductDbHelper;
import com.timotiusoktorio.inventoryapp.fragment.ConfirmationDialogFragment;
import com.timotiusoktorio.inventoryapp.helper.PhotoHelper;
import com.timotiusoktorio.inventoryapp.model.Product;

import java.io.File;

/**
 * Created by Coze on 2016-08-08.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";

    private ImageView mProductPhotoImageView;
    private TextView mProductQuantityTextView;
    private ProductDbHelper mDbHelper;
    private Product mProduct;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    /**
     * Permissions required to read and write contacts. Used by the {@link CreateActivity}.
     */
    private static String[] PERMISSIONS_EXTERNAL_STORAGE= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // This is a DialogInterface listener that is used to communicate between the DialogFragment and
    // this activity. The method gets invoked when the user presses the 'OK' button on the dialog.
    private DialogInterface.OnClickListener mOnPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // Delete the current product from the database and return to MainActivity.
            mDbHelper.deleteProduct(mProduct.getmId());
            PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProductPhotoImageView = (ImageView) findViewById(R.id.product_photo_image_view);
        mProductQuantityTextView = (TextView) findViewById(R.id.product_quantity_text_view);

        // Get an instance of the database helper.
        mDbHelper = ProductDbHelper.getInstance(getApplicationContext());

        // Get the product object which was sent from MainActivity.
        Product product = getIntent().getParcelableExtra(INTENT_EXTRA_PRODUCT);
        // The product object currently doesn't have the complete product information.
        // Get the rest of the product information from the database.
        mProduct = mDbHelper.queryProductDetails(product);

        // Populate views with the product details data.
        populateViewsWithProductData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Navigate to CreateActivity and pass the current product object as an argument.
                Intent intent = new Intent(this, CreateActivity.class);
                intent.putExtra(INTENT_EXTRA_PRODUCT, mProduct);
                startActivity(intent);
                break;
            case R.id.action_delete:
                // Show a confirmation dialog before deleting the product from the database.
                ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                        R.string.title_dialog_confirm_delete, R.string.message_dialog_confirm_delete);
                dialogFragment.setOnPositiveClickListener(mOnPositiveClickListener);
                dialogFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
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
     * Method that gets invoked when the user presses either the '+ QTY' or '- QTY' button.
     * This method will increase or decrease the product quantity by 1 for each click.
     * Each time the quantity is changed, it will be updated in the database.
     * @param view - Button ('+ QTY' or '- QTY' button).
     */
    public void modifyProductQuantity(View view) {
        int productQty = mProduct.getmQuantity();
        if (view.getId() == R.id.increase_qty_button) {
            // Increase the quantity of the product by 1.
            productQty = productQty + 1;
            updateProductQuantity(productQty);
        } else {
            // Decrease the quantity of the product by 1 only if it wouldn't result a negative qty.
            if (productQty > 0) {
                productQty = productQty - 1;
                updateProductQuantity(productQty);
            }
        }
    }

    /**
     * Method that gets invoked when the user presses the 'Contact Supplier' button.
     * This method will dispatch an email intent to the supplier using the supplier email address
     * stored in the product object.
     * @param view - Button ('Contact Supplier' button)
     */
    public void contactSupplier(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));

//        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mProduct.getSupplierEmail() });

        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_order_request, mProduct.getmName()));
        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
    }

    /**
     * Method for populating the views with the product data. As the code for populating views can
     * be quite long, a separate method is preferable for better readability.
     */
    private void populateViewsWithProductData() {
        String photoPath = mProduct.getmPhotoPath();
//        mProductPhotoImageView.setTag(photoPath);
        if (!TextUtils.isEmpty(photoPath)) {
            Glide.with(getApplicationContext()).load(photoPath).into(mProductPhotoImageView);
        }

        TextView productNameTextView = (TextView) findViewById(R.id.product_name_text_view);
        productNameTextView.setText(mProduct.getmName());

        TextView productCodeTextView = (TextView) findViewById(R.id.product_code_text_view);
//        productCodeTextView.setText(getString(R.string.string_format_product_code, mProduct.getCode()));

        TextView productSupplierTextView = (TextView) findViewById(R.id.product_supplier_text_view);
        productSupplierTextView.setText(getString(R.string.string_format_product_supplier, mProduct.getmSupplier()));

        TextView productPriceTextView = (TextView) findViewById(R.id.product_price_text_view);
        double roundedPrice = Math.round(mProduct.getmPrice() * 10000.0) / 10000.0;
        productPriceTextView.setText(getString(R.string.string_format_product_price_details, roundedPrice));

        mProductQuantityTextView = (TextView) findViewById(R.id.product_quantity_text_view);
        mProductQuantityTextView.setText(getString(R.string.string_format_product_quantity_details, mProduct.getmQuantity()));
    }

    /**
     * Method for updating the product quantity TextView and the product in the database with the
     * new product quantity after it has been modified when the user presses '+ QTY' or '- QTY' button.
     * This method is created because the code is called twice in a separate statements.
     * @param newQuantity - The new product quantity.
     */
    private void updateProductQuantity(int newQuantity) {
        mProductQuantityTextView.setText(getString(R.string.string_format_product_quantity_details, newQuantity));
        mProduct.setmQuantity(newQuantity);
        mDbHelper.updateProductQuantity(mProduct.getmId(), mProduct.getmQuantity());
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
            Log.i(TAG, "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(findViewById(R.id.content), R.string.permission_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(DetailActivity.this, PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check the request code to determine which intent was dispatched.
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
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
}