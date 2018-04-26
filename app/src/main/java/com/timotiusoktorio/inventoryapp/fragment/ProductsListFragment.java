package com.timotiusoktorio.inventoryapp.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.activity.AddProductActivity;
import com.timotiusoktorio.inventoryapp.activity.DetailActivity;
import com.timotiusoktorio.inventoryapp.adapter.ProductAdapter;
import com.timotiusoktorio.inventoryapp.database.ProductDbHelper;
import com.timotiusoktorio.inventoryapp.helper.PhotoHelper;
import com.timotiusoktorio.inventoryapp.model.Product;
import com.timotiusoktorio.inventoryapp.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ProductsListFragment extends Fragment implements
        ProductAdapter.OnItemClickListener, ProductAdapter.OnItemSaleListener, ProductAdapter.OnItemDeleteListener  {
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private ProductDbHelper mDbHelper;
    private ProductAdapter mAdapter;

    public ProductsListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_products_list, container, false);

        v.findViewById(R.id.add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCreateActivity();
            }
        });

        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCreateActivity();
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mEmptyView = (LinearLayout) v.findViewById(R.id.empty_view);

        mDbHelper = ProductDbHelper.getInstance(getActivity().getApplicationContext());
        mAdapter = new ProductAdapter(getActivity(), new ArrayList<Product>());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemSaleListener(this);
        mAdapter.setOnItemDeleteListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        return v;
    }

    /**
     * Interface method of ProductAdapter that gets invoked when the user presses one of the product
     * list item. This will navigate to DetailActivity to let user view the selected product details.
     * @param product - The selected product.
     */
    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
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
                if (product != null) mDbHelper.updateProductQuantity(product.getmId(), product.getmQuantity());
            }
        };
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                R.string.title_dialog_confirm_sale, R.string.message_dialog_confirm_sale);
        dialogFragment.setOnPositiveClickListener(onClickListener);
        dialogFragment.show(getActivity().getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
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
                mDbHelper.deleteProduct(product.getmId());
                PhotoHelper.deleteCapturedPhotoFile(product.getmPhotoPath());
                checkEmptyData();
            }
        };
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                R.string.title_dialog_confirm_delete, R.string.message_dialog_confirm_delete);
        dialogFragment.setOnPositiveClickListener(onClickListener);
        dialogFragment.show(getActivity().getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
    }

    /**
     * Method that gets invoked when the user presses the 'Add Product' button or the FAB.
     * This will navigate to AddProductActivity to let user add a new product to the database.
     * @param - 'Add Product' button or the floating action button.
     */
    public void navigateToCreateActivity() {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
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

    @Override
    public void onResume() {
        super.onResume();
        List<Product> products = mDbHelper.queryProducts();
        mAdapter.refreshData(products);
        checkEmptyData();
    }
}
