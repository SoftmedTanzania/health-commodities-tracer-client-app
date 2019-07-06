package com.softmed.stockapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.softmed.stockapp.Activities.MainActivity;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.dto.ProducToBeReportedtList;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.Dom.entities.TransactionType;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.Dom.entities.Unit;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.softmed.stockapp.Utils.Calendars.toBeginningOfTheDay;


/**
 * Dialog allowing users to select a date.
 */
public class UpdateStockFragment extends Fragment {
    private static final String TAG = UpdateStockFragment.class.getSimpleName();
    public static AppDatabase baseDatabase;
    private View dialogueLayout;
    private MaterialSpinner stockAdjustmentReasonSpinner, availabilityOfClientsOnRegimeSpinner;
    private TextInputLayout stockAdjustmentQuantity, numberOfClientsOnRegimeInputLayout, wastageInputLayout, quantityExpiredInputLayout, stockOutDaysInputLayout;
    private int productId;
    private String productName, scheduledDate;
    private int scheduledId;
    private boolean hasClients = false;
    private int stockQuantity;
    private Product product;
    private Unit unit;
    // Session Manager Class
    private SessionManager session;

    public UpdateStockFragment() {
    }


    public static UpdateStockFragment newInstance(ProducToBeReportedtList product) {
        UpdateStockFragment f = new UpdateStockFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("productId", product.getId());
        args.putString("productName", product.getName());
        args.putString("scheduledDate", product.getScheduledDate());
        args.putInt("scheduledId", product.getScheduleId());
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
        scheduledDate = getArguments().getString("scheduledDate");
        scheduledId = getArguments().getInt("scheduledId");
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

        TextView scheduledDateTextView = dialogueLayout.findViewById(R.id.date);

        Date d = new Date();
        d.setTime(Long.parseLong(scheduledDate));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        scheduledDateTextView.setText("STOCK STATUS FOR " + dateFormat.format(d));

        stockAdjustmentQuantity = dialogueLayout.findViewById(R.id.stock_adjustment_quantity);
        numberOfClientsOnRegimeInputLayout = dialogueLayout.findViewById(R.id.number_of_clients_on_regime);
        quantityExpiredInputLayout = dialogueLayout.findViewById(R.id.quantity_expired);
        stockOutDaysInputLayout = dialogueLayout.findViewById(R.id.stock_out_days);
        wastageInputLayout = dialogueLayout.findViewById(R.id.wastage);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                product = baseDatabase.productsModelDao().getProductByName(productId);

                Log.d(TAG,"Product = "+new Gson().toJson(product));
                unit = baseDatabase.unitsDao().getUnit(product.getUnit_id());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                stockAdjustmentQuantity.setHint("Stock On Hand in (" + unit.getName() + ")");

                if (product.isTrack_wastage()) {
                    wastageInputLayout.setVisibility(View.VISIBLE);
                }

                if (product.isTrack_quantity_expired()) {
                    quantityExpiredInputLayout.setVisibility(View.VISIBLE);
                }

                if (product.isTrack_number_of_patients()) {
                    numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                }

            }
        }.execute();


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
                        hasClients = true;

                        if (product.isTrack_number_of_patients()) {
                            numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        hasClients = false;
                        if (product.isTrack_number_of_patients()) {
                            numberOfClientsOnRegimeInputLayout.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        dialogueLayout.findViewById(R.id.add_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInputs()) {
                    stockQuantity = 0;
                    try {
                        stockQuantity = Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new AsyncTask<Void, Void, Void>() {
                        private int stockOutDays;
                        private String noOfClients, QuantityExpired, wastage;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            stockOutDays = Integer.parseInt(stockOutDaysInputLayout.getEditText().getText().toString());
                            noOfClients = numberOfClientsOnRegimeInputLayout.getEditText().getText().toString();
                            QuantityExpired = quantityExpiredInputLayout.getEditText().getText().toString();
                            wastage = wastageInputLayout.getEditText().getText().toString();

                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            Transactions transactions = new Transactions();

                            transactions.setId(UUID.randomUUID().toString());
                            transactions.setProduct_id(productId);
                            transactions.setUser_id(Integer.valueOf(session.getUserUUID()));
                            transactions.setTransactiontype_id(1);
                            transactions.setScheduleId(scheduledId);
                            transactions.setAmount(stockQuantity);
                            transactions.setHasClients(hasClients);
                            transactions.setSyncStatus(0);
                            transactions.setCreated_at(Calendar.getInstance().getTimeInMillis());
                            transactions.setStockOutDays(stockOutDays);

                            if (hasClients && product.isTrack_number_of_patients()) {
                                transactions.setClientsOnRegime(noOfClients);
                            }

                            if (product.isTrack_quantity_expired()) {
                                transactions.setQuantityExpired(QuantityExpired);
                            }

                            if (product.isTrack_wastage()) {
                                transactions.setWastage(wastage);
                            }

                            transactions.setStatus_id(1);

                            Calendar c = Calendar.getInstance();
                            toBeginningOfTheDay(c);

                            transactions.setCreated_at(c.getTimeInMillis());

                            baseDatabase.transactionsDao().addTransactions(transactions);

                            Balances balances = baseDatabase.balanceModelDao().getBalance(productId);
                            balances.setBalance(stockQuantity);
                            baseDatabase.balanceModelDao().addBalance(balances);

                            try {
                                Log.d(TAG, "Updating product schedule");
                                ProductReportingSchedule reportingSchedule = baseDatabase.productReportingScheduleModelDao().getProductReportingScheduleById(scheduledId);
                                reportingSchedule.setStatus("posted");
                                baseDatabase.productReportingScheduleModelDao().addProductSchedule(reportingSchedule);

                                Log.d(TAG, "updated product schedule = " + new Gson().toJson(baseDatabase.productReportingScheduleModelDao().getProductReportingScheduleById(scheduledId)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void v) {
                            super.onPostExecute(v);
                            ((MainActivity) getActivity()).moveToNextProduct();

                        }
                    }.execute();
                }

            }
        });


        return dialogueLayout;
    }

    public boolean checkInputs() {
        if (stockAdjustmentQuantity.getEditText().getText().toString().equals("")) {
            stockAdjustmentQuantity.getEditText().setError("Please fill the stock on hand quantity");
            return false;
        } else if (numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals("") && product.isTrack_number_of_patients() && hasClients) {
            numberOfClientsOnRegimeInputLayout.getEditText().setError("Please fill the number of clients on regime");
            return false;
        } else if (stockOutDaysInputLayout.getEditText().getText().toString().equals("")) {
            stockOutDaysInputLayout.getEditText().setError("Please fill the stockout days quantity");
            return false;
        } else if (wastageInputLayout.getEditText().getText().toString().equals("") && product.isTrack_wastage()) {
            wastageInputLayout.getEditText().setError("Please fill wastage quantity");
            return false;
        } else if (quantityExpiredInputLayout.getEditText().getText().toString().equals("") && product.isTrack_quantity_expired()) {
            quantityExpiredInputLayout.getEditText().setError("Please fill the quantity expired");
            return false;
        }
        return true;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
