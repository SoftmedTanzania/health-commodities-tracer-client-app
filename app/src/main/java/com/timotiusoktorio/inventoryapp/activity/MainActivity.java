package com.timotiusoktorio.inventoryapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.adapter.ProductAdapter;
import com.timotiusoktorio.inventoryapp.database.ProductDbHelper;
import com.timotiusoktorio.inventoryapp.fragment.ConfirmationDialogFragment;
import com.timotiusoktorio.inventoryapp.helper.PhotoHelper;
import com.timotiusoktorio.inventoryapp.model.Product;
import com.timotiusoktorio.inventoryapp.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ProductAdapter.OnItemClickListener, ProductAdapter.OnItemSaleListener, ProductAdapter.OnItemDeleteListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";

    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private ProductDbHelper mDbHelper;
    private ProductAdapter mAdapter;

    // This is a DialogInterface listener that is used to communicate between the DialogFragment and
    // this activity. The method gets invoked when the user presses the 'OK' button on the dialog.
    private DialogInterface.OnClickListener mOnPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // Delete all products in the database and clear the adapter.
            mDbHelper.deleteAllProducts();
            mAdapter.emptyData();
            checkEmptyData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);

        mDbHelper = ProductDbHelper.getInstance(getApplicationContext());
        mAdapter = new ProductAdapter(this, new ArrayList<Product>());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemSaleListener(this);
        mAdapter.setOnItemDeleteListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_empty_products) {
            ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                    R.string.title_dialog_confirm_empty_products, R.string.message_dialog_confirm_empty_products);
            dialogFragment.setOnPositiveClickListener(mOnPositiveClickListener);
            dialogFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Product> products = mDbHelper.queryProducts();
        mAdapter.refreshData(products);
        checkEmptyData();
    }

    /**
     * Interface method of ProductAdapter that gets invoked when the user presses one of the product
     * list item. This will navigate to DetailActivity to let user view the selected product details.
     * @param product - The selected product.
     */
    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(INTENT_EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    /**
     * Interface method of ProductAdapter that gets invoked when the user presses the 'Sale' button
     * on the popup menu. This will show a dialog to ask for the user's confirmation to track a sale
     * for this product. If the user confirmed, the selected product quantity will be decreased by 1.
     * @param position - The ArrayList position of the selected product.
     */
    @Override
    public void onItemSale(final int position) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Product product = mAdapter.decreaseProductQuantity(position);
                if (product != null) mDbHelper.updateProductQuantity(product.getId(), product.getQuantity());
            }
        };
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                R.string.title_dialog_confirm_sale, R.string.message_dialog_confirm_sale);
        dialogFragment.setOnPositiveClickListener(onClickListener);
        dialogFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
    }

    /**
     * Interface method of ProductAdapter that gets invoked when the user presses the 'Delete' button
     * on the popup menu. This will show a dialog to ask for the user's confirmation to delete this product.
     * If the user confirmed, the selected product will be deleted permanently from the database.
     * @param product - The selected product object.
     * @param position - The ArrayList position of the selected product.
     */
    @Override
    public void onItemDelete(final Product product, final int position) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.deleteProduct(position);
                mDbHelper.deleteProduct(product.getId());
                PhotoHelper.deleteCapturedPhotoFile(product.getPhotoPath());
                checkEmptyData();
            }
        };
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                R.string.title_dialog_confirm_delete, R.string.message_dialog_confirm_delete);
        dialogFragment.setOnPositiveClickListener(onClickListener);
        dialogFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
    }

    /**
     * Method that gets invoked when the user presses the 'Add Product' button or the FAB.
     * This will navigate to CreateActivity to let user add a new product to the database.
     * @param view - 'Add Product' button or the floating action button.
     */
    public void navigateToCreateActivity(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    /**
     * Method to toggle the recycler view or the empty view visibility based on the adapter data.
     * If the data is empty, the recycler view will be hidden and the empty view will be visible.
     * If the data is not empty, vice versa.
     */
    private void checkEmptyData() {
        boolean isDataEmpty = (mAdapter.getItemCount() == 0);
        mRecyclerView.setVisibility(isDataEmpty ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(isDataEmpty ? View.VISIBLE : View.GONE);
    }

}