package com.softmed.stockapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.softmed.stockapp.R;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.ProductList;
import com.softmed.stockapp.dom.entities.Product;
import com.softmed.stockapp.dom.entities.ProductBalance;
import com.softmed.stockapp.dom.entities.Transactions;
import com.softmed.stockapp.fragments.AddTransactionDialogue;
import com.softmed.stockapp.customViews.CustomScrollView;
import com.softmed.stockapp.utils.LoadProductPhotoAsync;
import com.softmed.stockapp.utils.PhotoHelper;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.ProductsViewModel;
import com.softmed.stockapp.viewmodels.TransactionsListViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

/**
 * Created by Coze on 2016-08-08.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE_AND_CAMERA = 1;

    private static String[] PERMISSIONS_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private ImageView mProductPhotoImageView;
    private TextView mProductQuantityTextView;
    private AppDatabase database;
    private ProductBalance mProduct;
    private Product myProduct;
    private ExtendedFloatingActionButton floatingActionButton;
    private String stockAdjustmentReason = "";
    private TransactionsListViewModel transactionsListViewModel;
    private ProductsViewModel productsViewModel;
    private TableLayout transactionsTable;
    private EasyImage easyImage;
    // Session Manager Class
    private SessionManager session;
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

        easyImage = new EasyImage.Builder(DetailActivity.this)
                .setChooserTitle("Pick media").setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setCopyImagesToPublicGalleryFolder(false)
                .setFolderName("EasyImage sample")
                .allowMultiple(false)
                .build();

        mProductPhotoImageView = findViewById(R.id.product_photo_image_view);
        mProductPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Contacts permissions have not been granted.
                    Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
                    setRequestExternalStoragePermissions();

                } else {
                    easyImage.openCameraForImage(DetailActivity.this);

                }
            }
        });


        mProductQuantityTextView = findViewById(R.id.product_quantity_text_view);
        transactionsTable = findViewById(R.id.transactions_table);

        database = AppDatabase.getDatabase(this);
        session = new SessionManager(this);


        final ProductList product = (ProductList) getIntent().getSerializableExtra(INTENT_EXTRA_PRODUCT);

        productsViewModel = ViewModelProviders.of(DetailActivity.this).get(ProductsViewModel.class);

        productsViewModel.getProdictById(product.getId(), session.getFacilityId()).observe(DetailActivity.this, new Observer<ProductBalance>() {
            @Override
            public void onChanged(@Nullable final ProductBalance mProd) {
                mProduct = mProd;

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        myProduct = database.productsModelDao().getProductById(mProd.getProductId());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        // Populate views with the product details data.
                        populateViewsWithProductData();

                        transactionsListViewModel = ViewModelProviders.of(DetailActivity.this).get(TransactionsListViewModel.class);

                        transactionsListViewModel.getLastTransactionByProductId(mProduct.getProductId(), session.getFacilityId()).observe(DetailActivity.this, new Observer<Transactions>() {
                            @Override
                            public void onChanged(@Nullable final Transactions transactions) {

                                new AsyncTask<Void, Void, Void>() {
                                    private Product product;

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        product = database.productsModelDao().getProductById(mProduct.getProductId());
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void name) {
                                        super.onPostExecute(name);

                                        if (product.isTrack_number_of_patients()) {
                                            View numberOfClientsLayout = findViewById(R.id.number_of_clients_layout);
                                            numberOfClientsLayout.setVisibility(View.VISIBLE);

                                            TextView numberOfClientsOnRegime = findViewById(R.id.number_of_clients_on_regime);

                                            Log.d(TAG, "number of clients on regime = " + transactions.getClientsOnRegime());

                                            numberOfClientsOnRegime.setText(String.format("%s %s", getResources().getString(R.string.string_format_product_clients_on_regime), String.valueOf(transactions.getClientsOnRegime())));

                                        }
                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });

                        transactionsListViewModel.getTransactionsListByProductId(mProduct.getProductId(), session.getFacilityId()).observe(DetailActivity.this, new Observer<List<Transactions>>() {
                            @Override
                            public void onChanged(@Nullable List<Transactions> transactions) {
                                transactionsTable.removeAllViews();

                                if (myProduct.isTrack_number_of_patients()) {
                                    findViewById(R.id.clients_on_regime_title).setVisibility(View.VISIBLE);
                                }
                                if (myProduct.isTrack_number_of_patients()) {
                                    findViewById(R.id.wastage_title).setVisibility(View.VISIBLE);
                                }
                                if (myProduct.isTrack_number_of_patients()) {
                                    findViewById(R.id.expired_stock_title).setVisibility(View.VISIBLE);
                                }

                                int i = 0;
                                for (final Transactions transactions1 : transactions) {
                                    i++;
                                    final View v = LayoutInflater.from(DetailActivity.this).inflate(R.layout.view_transaction_item, null);
                                    ((TextView) v.findViewById(R.id.sn)).setText(String.valueOf(i));


                                    Log.d(TAG, "timestamp Date = " + transactions1.getCreated_at());


                                    Date date = new Date(transactions1.getCreated_at());
                                    DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
                                    String dateFormatted = formatter.format(date);


                                    Log.d(TAG, "formated Date = " + dateFormatted);

                                    ((TextView) v.findViewById(R.id.date)).setText(dateFormatted);


                                    if (myProduct.isTrack_number_of_patients()) {
                                        v.findViewById(R.id.number_of_clients_on_regime).setVisibility(View.VISIBLE);
                                        ((TextView) v.findViewById(R.id.number_of_clients_on_regime)).setText(String.valueOf(transactions1.getClientsOnRegime()));
                                    }
                                    ((TextView) v.findViewById(R.id.quantity)).setText(String.valueOf(transactions1.getAmount()));
                                    ((TextView) v.findViewById(R.id.stock_out_days)).setText(String.valueOf(transactions1.getStockOutDays()));

                                    if (myProduct.isTrack_number_of_patients()) {
                                        v.findViewById(R.id.wastage).setVisibility(View.VISIBLE);
                                        ((TextView) v.findViewById(R.id.wastage)).setText(String.valueOf(transactions1.getWastage()));
                                    }

                                    if (myProduct.isTrack_number_of_patients()) {
                                        v.findViewById(R.id.expired_stock).setVisibility(View.VISIBLE);
                                        ((TextView) v.findViewById(R.id.expired_stock)).setText(String.valueOf(transactions1.getQuantityExpired()));
                                    }

                                    transactionsTable.addView(v);
                                }
                            }
                        });
                    }
                }.execute();
            }
        });

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTransactionDialogue Dialogue = AddTransactionDialogue.newInstance(mProduct.getProductId());
                Dialogue.show(getSupportFragmentManager(), "Adding Transaction");
            }
        });


        ((CustomScrollView) findViewById(R.id.scrollable)).setMyScrollChangeListener(new CustomScrollView.OnMyScrollChangeListener() {
            @Override
            public void onScrollUp() {
                //Toast.makeText(getActivity(), "Scrolling up", Toast.LENGTH_SHORT).show();
                Log.d("scroll", "up");
                floatingActionButton.extend(true);

            }

            @Override
            public void onScrollDown() {
                // Toast.makeText(getActivity(), "Scrolling down", Toast.LENGTH_SHORT).show();
                Log.d("scroll", "down");

                floatingActionButton.shrink(true);

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
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that gets invoked when the user presses the product photo image view.
     * This method will dispatch a view intent to open the product photo in full screen.
     *
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
        String photoPath = "";
        if (myProduct.getLocal_image_path() != null) {
            photoPath = myProduct.getLocal_image_path();
        }

        Log.d(TAG, "product path = " + photoPath);
        mProductPhotoImageView.setTag(photoPath);
        if (!TextUtils.isEmpty(photoPath)) {
            new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(photoPath);
        }

        TextView productNameTextView = findViewById(R.id.product_name_text_view);
        productNameTextView.setText(mProduct.getProductCategory() + " - " + mProduct.getProductName());

        mProductQuantityTextView = findViewById(R.id.product_quantity_text_view);
        mProductQuantityTextView.setText(String.format("%s %s", String.valueOf(mProduct.getBalance()), String.valueOf(mProduct.getUnit())));


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

            Log.d(TAG, "show status");

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


            Log.i(TAG, "Displaying camera permission rationale to provide additional context.");

            Snackbar.make(findViewById(R.id.content), R.string.permission_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(DetailActivity.this, PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE_AND_CAMERA);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE_AND_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check the request code to determine which intent was dispatched.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                // Choose photo successful. Delete previously captured photo file if there's any.
                PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
                // Save the file uri as a tag and display the selected photo on the ImageView.
                String photoPath = data.getData().toString();
                mProductPhotoImageView.setTag(photoPath);
                showImage(mProductPhotoImageView, photoPath);
            }
        }

        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                final String photoPath = imageFiles[0].getFile().getAbsolutePath();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        myProduct.setLocal_image_path(photoPath);
                        database.productsModelDao().addProduct(myProduct);
                        return null;
                    }
                }.execute();
                mProductPhotoImageView.setTag(photoPath);
                showImage(mProductPhotoImageView, photoPath);
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });
    }
}