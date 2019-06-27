package com.softmed.stockapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.softmed.stockapp.Activities.MainActivity;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.TransactionType;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.softmed.stockapp.Utils.Calendars.toBeginningOfTheDay;


/**
 * Dialog allowing users to select a date.
 */
public class UpdateStockFragment extends android.support.v4.app.Fragment {
    private static final String TAG = UpdateStockFragment.class.getSimpleName();
    public static AppDatabase baseDatabase;
    private View dialogueLayout;
    private MaterialSpinner stockAdjustmentReasonSpinner, availabilityOfClientsOnRegimeSpinner;
    private List<TransactionType> transactionTypes;
    private TextInputLayout stockAdjustmentQuantity, numberOfClientsOnRegimeInputLayout;
    private int productId, numberOfClientsOnRegime;
    private String productName;
    private boolean availabilityOfClients = false;
    private int stockQuantity;

    // Session Manager Class
    private SessionManager session;

    public UpdateStockFragment() {
    }


    public static UpdateStockFragment newInstance(Product product) {
        UpdateStockFragment f = new UpdateStockFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("productId", product.getId());
        args.putString("productName", product.getName());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        final Activity activity = getActivity();
        baseDatabase = AppDatabase.getDatabase(getActivity());
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        productId = getArguments().getInt("productId");
        productName = getArguments().getString("productName");
        session = new SessionManager(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dialogueLayout = inflater.inflate(R.layout.fragment_update_stock, container, false);


        TextView productTitle = dialogueLayout.findViewById(R.id.product_name);
        productTitle.setText(productName);

        stockAdjustmentQuantity = dialogueLayout.findViewById(R.id.stock_adjustment_quantity);
        numberOfClientsOnRegimeInputLayout = dialogueLayout.findViewById(R.id.number_of_clients_on_regime);

        stockAdjustmentReasonSpinner = dialogueLayout.findViewById(R.id.stock_adjustment_reason);
        availabilityOfClientsOnRegimeSpinner = dialogueLayout.findViewById(R.id.do_you_have_any_clients_on_regime);

        final String[] availabilityOfClientsOnRegime = {"Yes", "No"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, availabilityOfClientsOnRegime);
        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
        availabilityOfClientsOnRegimeSpinner.setAdapter(spinAdapter);
        availabilityOfClientsOnRegimeSpinner.setSelection(1);
        availabilityOfClientsOnRegimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (availabilityOfClientsOnRegime[i].equalsIgnoreCase("yes")) {
                        availabilityOfClients = true;
                        numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        availabilityOfClients = false;
                        numberOfClientsOnRegimeInputLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                transactionTypes = baseDatabase.transactionTypeModelDao().getTransactionTypes();
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
                List<String> transactionTypesNames = new ArrayList<>();
                for (TransactionType tType : transactionTypes) {
                    transactionTypesNames.add(tType.getName());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, transactionTypesNames);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                stockAdjustmentReasonSpinner.setAdapter(spinAdapter);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        dialogueLayout.findViewById(R.id.add_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((!availabilityOfClients || !numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals(""))) {
                    final Integer numberOfClientsOnRegime = Integer.valueOf(numberOfClientsOnRegimeInputLayout.getEditText().getText().toString());
                    stockQuantity = 0;
                    try {
                        stockQuantity = Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            Transactions transactions = new Transactions();
                            transactions.setUuid(UUID.randomUUID().toString());
                            transactions.setProduct_id(productId);
                            transactions.setUser_id(Integer.valueOf(session.getUserUUID()));
                            transactions.setTransactiontype_id(1);
                            transactions.setAmount(stockQuantity);

                            if (availabilityOfClients)
                                transactions.setClientsOnRegime(numberOfClientsOnRegime);
                            else
                                transactions.setClientsOnRegime(0);
                            transactions.setStatus_id(1);

                            Calendar c = Calendar.getInstance();
                            toBeginningOfTheDay(c);

                            transactions.setCreated_at(c.getTimeInMillis());
                            baseDatabase.transactionsDao().addTransactions(transactions);

                            Balances balances = baseDatabase.balanceModelDao().getBalance(productId);

                            balances.setBalance(stockQuantity);
                            baseDatabase.balanceModelDao().addBalance(balances);


                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void v) {
                            super.onPostExecute(v);
                            ((MainActivity) getActivity()).moveToNextProduct();

                        }
                    }.execute();


                } else if (stockAdjustmentQuantity.getEditText().getText().toString().equals("")) {
                    stockAdjustmentQuantity.getEditText().setError("Please fill the stock on hand quantity");
                } else if (numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals("")) {
                    numberOfClientsOnRegimeInputLayout.getEditText().setError("Please fill the number of clients on regime");
                }

            }
        });


        return dialogueLayout;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
