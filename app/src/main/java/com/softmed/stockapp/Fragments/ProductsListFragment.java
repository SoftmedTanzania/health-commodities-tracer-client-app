package com.softmed.stockapp.Fragments;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.softmed.stockapp.R;
import com.softmed.stockapp.Activities.DetailActivity;
import com.softmed.stockapp.Adapters.ProductAdapter;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.dto.ProductList;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.Utils.DividerItemDecoration;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.viewmodels.ProductsViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.softmed.stockapp.Utils.Calendars.toBeginningOfTheDay;

public class ProductsListFragment extends Fragment implements
        ProductAdapter.OnItemClickListener, ProductAdapter.OnProductReportStockListener {
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

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mEmptyView = v.findViewById(R.id.empty_view);

        database = AppDatabase.getDatabase(getActivity().getApplicationContext());

        mAdapter = new ProductAdapter(getActivity(), new ArrayList<ProductList>());

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnProductReportStockListener(this);

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
    public void onReportStock(ProductList product, int position) {
        AddTransactionDialogue Dialogue = AddTransactionDialogue.newInstance(product.getId());

        try {
            Dialogue.show(getActivity().getSupportFragmentManager(), "Adding Transaction");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
