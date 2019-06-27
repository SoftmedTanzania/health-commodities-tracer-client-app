package com.softmed.stockapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.softmed.stockapp.R;
import com.softmed.stockapp.Activities.AddProductActivity;
import com.softmed.stockapp.Activities.CreateProductActivity;
import com.softmed.stockapp.Activities.DetailActivity;
import com.softmed.stockapp.Adapters.ProductAdapter;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.ProductList;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.Utils.DividerItemDecoration;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.viewmodels.ProductsViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.softmed.stockapp.Utils.Calendars.toBeginningOfTheDay;

public class ProductsListFragment extends Fragment implements
        ProductAdapter.OnItemClickListener, ProductAdapter.OnItemSaleListener, ProductAdapter.OnItemDeleteListener  {
    private static final String TAG = ProductsListFragment.class.getSimpleName();
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private AppDatabase database;
    private ProductAdapter mAdapter;
    private ProductsViewModel productsViewModel;
    // Session Manager Class
    private SessionManager session;

    public ProductsListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity());
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

        database = AppDatabase.getDatabase(getActivity().getApplicationContext());

        mAdapter = new ProductAdapter(getActivity(), new ArrayList<ProductList>());

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemSaleListener(this);
        mAdapter.setOnItemDeleteListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);


        productsViewModel = ViewModelProviders.of(this).get(ProductsViewModel.class);
        productsViewModel.getAvailableProducts().observe(getActivity(), new Observer<List<ProductList>>() {
            @Override
            public void onChanged(@Nullable List<ProductList> productLists) {
                Log.d(TAG,"products list size = "+productLists.size());
                mAdapter.refreshData(productLists);
                checkEmptyData();

            }
        });

        return v;
    }

    /**
     * Interface method of ProductAdapter that gets invoked when the user presses one of the product
     * list item. This will navigate to DetailActivity to let user view the selected product details.
     * @param product - The selected product.
     */
    @Override
    public void onItemClick(ProductList product) {

        Log.d(TAG,"Clicked Product Id = "+product.getId());

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
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProductList product = mAdapter.decreaseProductQuantity(position);
                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (product != null){
                            Transactions transactions = new Transactions();
                            transactions.setUuid(UUID.randomUUID().toString());
                            transactions.setStatus_id(1);
                            transactions.setClientsOnRegime(product.getNumberOfClientsOnRegime());
                            transactions.setAmount(1);
                            transactions.setProduct_id(product.getId());
                            //TODO remove hardcoding of ids
                            transactions.setTransactiontype_id(2);
                            transactions.setUser_id(Integer.valueOf(session.getUserUUID()));

                            Calendar c = Calendar.getInstance();
                            toBeginningOfTheDay(c);

                            transactions.setCreated_at(c.getTimeInMillis());


                            Log.d(TAG,"Saving transactions");
                            database.transactionsDao().addTransactions(transactions);

                            Balances balances = database.balanceModelDao().getBalance(product.getId());
                            balances.setBalance(balances.getBalance()-1);
                            database.balanceModelDao().addBalance(balances);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v) {
                        super.onPostExecute(v);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
    public void onItemDelete(final ProductList product, final int position) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.deleteProduct(position);

                //TODO handle deletion of product photo
//                database.productsModelDao().deleteProduct(product);
//                PhotoHelper.deleteCapturedPhotoFile(product.getPhotoPath());
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
        SessionManager session = new SessionManager(getContext());

//        Log.d(TAG,"USER LEVEL ID = "+session.getUserLevel());
//        if(session.getUserLevel()==2){
//            Intent intent = new Intent(getActivity(), CreateProductActivity.class);
//            startActivity(intent);
//        }else {
//            Intent intent = new Intent(getActivity(), AddProductActivity.class);
//            startActivity(intent);
//        }
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
