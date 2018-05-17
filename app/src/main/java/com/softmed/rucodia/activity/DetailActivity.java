package com.softmed.rucodia.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softmed.rucodia.LoadProductPhotoAsync;
import com.softmed.rucodia.R;
import com.softmed.rucodia.database.AppDatabase;
import com.softmed.rucodia.dom.objects.Balances;
import com.softmed.rucodia.dom.objects.ProductBalance;
import com.softmed.rucodia.dom.objects.ProductList;
import com.softmed.rucodia.dom.objects.TransactionType;
import com.softmed.rucodia.dom.objects.Transactions;
import com.softmed.rucodia.fragment.AddTransactionDialogue;
import com.softmed.rucodia.fragment.ConfirmationDialogFragment;
import com.softmed.rucodia.fragment.CreateOrderDialogue;
import com.softmed.rucodia.helper.PhotoHelper;
import com.softmed.rucodia.utils.SessionManager;
import com.softmed.rucodia.viewmodels.ProductsViewModel;
import com.softmed.rucodia.viewmodels.TransactionsListViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.softmed.rucodia.utils.Calendars.toBeginningOfTheDay;

/**
 * Created by Coze on 2016-08-08.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";

    private ImageView mProductPhotoImageView;
    private TextView mProductQuantityTextView;
    private AppDatabase database;
    private ProductBalance mProduct;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;

    private FloatingActionButton floatingActionButton;
    private String stockAdjustmentReason="";
    private TransactionsListViewModel transactionsListViewModel;
    private ProductsViewModel productsViewModel;
    private TableLayout transactionsTable;


    // Session Manager Class
    private SessionManager session;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    /**
     * Permissions required to read and write contacts. Used by the {@link AddProductActivity}.
     */
    private static String[] PERMISSIONS_EXTERNAL_STORAGE= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // This is a DialogInterface listener that is used to communicate between the DialogFragment and
    // this activity. The method gets invoked when the user presses the 'OK' button on the dialog.
    private DialogInterface.OnClickListener mOnPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            //TODO IMPLEMENT DELETION OF PRODUCT
            // Delete the current product from the database and return to MainActivity.
//            mDbHelper.deleteProduct(mProduct.getmId());
            PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
            finish();
        }
    };

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProductPhotoImageView = (ImageView) findViewById(R.id.product_photo_image_view);
        mProductQuantityTextView = (TextView) findViewById(R.id.product_quantity_text_view);
        transactionsTable = (TableLayout)findViewById(R.id.transactions_table);


        // Get an instance of the database helper.
        database =  AppDatabase.getDatabase(this);
        session = new SessionManager(this);


        // Get the product object which was sent from MainActivity.
        final ProductList product = (ProductList) getIntent().getSerializableExtra(INTENT_EXTRA_PRODUCT);
        // The product object currently doesn't have the complete product information.
        // Get the rest of the product information from the database.


        productsViewModel = ViewModelProviders.of(DetailActivity.this).get(ProductsViewModel.class);

        productsViewModel.getProdictById(product.getId()).observe(DetailActivity.this, new Observer<ProductBalance>() {
            @Override
            public void onChanged(@Nullable ProductBalance mProd) {
                mProduct = mProd;

                // Populate views with the product details data.
                populateViewsWithProductData();

                transactionsListViewModel = ViewModelProviders.of(DetailActivity.this).get(TransactionsListViewModel.class);

                transactionsListViewModel.getTransactionsListByProductId(mProduct.getProductId()).observe(DetailActivity.this, new Observer<List<Transactions>>() {
                    @Override
                    public void onChanged(@Nullable List<Transactions> transactions) {
                        transactionsTable.removeAllViews();

                        int i=0;
                        for(final Transactions transactions1:transactions){
                            i++;
                            final View v = LayoutInflater.from(DetailActivity.this).inflate(R.layout.view_transaction_item,null);
                            ((TextView)v.findViewById(R.id.sn)).setText(String.valueOf(i));

                            new AsyncTask<Void, Void, String>(){
                                @Override
                                protected String doInBackground(Void... voids) {
                                    return database.transactionTypeModelDao().getTransactionTypeName(transactions1.getTransactiontype_id());
                                }

                                @Override
                                protected void onPostExecute(String name) {
                                    super.onPostExecute(name);
                                    ((TextView)v.findViewById(R.id.transaction_type)).setText(name);
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);




                            Log.d(TAG,"timestamp Date = "+transactions1.getCreated_at());


                            Date date = new Date(transactions1.getCreated_at());
                            DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
                            String dateFormatted = formatter.format(date);


                            Log.d(TAG,"formated Date = "+dateFormatted);

                            ((TextView)v.findViewById(R.id.date)).setText(dateFormatted);



                            ((TextView)v.findViewById(R.id.price_per_item)).setText(String.valueOf(transactions1.getPrice()));
                            ((TextView)v.findViewById(R.id.total)).setText(String.valueOf(transactions1.getAmount() * transactions1.getPrice()));
                            ((TextView)v.findViewById(R.id.quantity)).setText(String.valueOf(transactions1.getAmount()));

                            transactionsTable.addView(v);
                        }
                    }
                });
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTransactionDialogue Dialogue = AddTransactionDialogue.newInstance(mProduct.getProductId(),mProduct.getPrice());
                Dialogue.show(getSupportFragmentManager(), "Adding Transaction");
            }
        });



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
                // Navigate to AddProductActivity and pass the current product object as an argument.
                Intent intent = new Intent(this, AddProductActivity.class);
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
     * Method for populating the views with the product data. As the code for populating views can
     * be quite long, a separate method is preferable for better readability.
     */
    private void populateViewsWithProductData() {
        String photoPath = mProduct.getImagePath();

        Log.d(TAG,"product path = "+photoPath);
        mProductPhotoImageView.setTag(photoPath);
        if (!TextUtils.isEmpty(photoPath)) {
            new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(photoPath);
        }

        TextView productNameTextView = (TextView) findViewById(R.id.product_name_text_view);
        productNameTextView.setText(mProduct.getSubCategoryName()+" - "+mProduct.getProductName());

        TextView productPriceTextView = (TextView) findViewById(R.id.product_price_text_view);
        double roundedPrice = Math.round(mProduct.getPrice() * 10000.0) / 10000.0;
        productPriceTextView.setText(getString(R.string.string_format_product_price_details, String.valueOf(roundedPrice)));

        mProductQuantityTextView = (TextView) findViewById(R.id.product_quantity_text_view);
        mProductQuantityTextView.setText(getString(R.string.string_format_product_quantity_details, String.valueOf(mProduct.getBalance())));
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