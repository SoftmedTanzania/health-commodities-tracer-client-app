package com.softmed.stockapp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputLayout;
import com.softmed.stockapp.R;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.Balances;
import com.softmed.stockapp.dom.entities.PostingFrequencies;
import com.softmed.stockapp.dom.entities.Product;
import com.softmed.stockapp.dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.dom.entities.Transactions;
import com.softmed.stockapp.dom.entities.Unit;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.workers.GetSchedulesWorker;
import com.softmed.stockapp.workers.SendTransactionsWorker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;


/**
 * Dialog allowing users to select a date.
 */
public class AddTransactionDialogue extends DialogFragment {
    public static AppDatabase baseDatabase;
    private View dialogueLayout;
    private TextInputLayout stockAdjustmentQuantity, numberOfClientsOnRegimeInputLayout, wastageInputLayout, quantityExpiredInputLayout, stockOutDaysInputLayout;
    private MaterialSpinner availabilityOfClientsOnRegimeSpinner, reportingPeriod;
    private List<ProductReportingSchedule> productReportingSchedules;
    private int productId;
    private Boolean hasClients = null;
    private Product product;
    private PostingFrequencies postingFrequency;
    private TextView productName;
    private Unit unit;
    private int reportingScheduleId = 0;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Session Manager Class
    private SessionManager session;

    public static AddTransactionDialogue newInstance(int productId) {
        AddTransactionDialogue f = new AddTransactionDialogue();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("productId", productId);
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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        dialogueLayout = inflater.inflate(R.layout.dialogue_create_transaction, container, false);

        stockAdjustmentQuantity = dialogueLayout.findViewById(R.id.stock_adjustment_quantity);
        productName = dialogueLayout.findViewById(R.id.product_name);
        numberOfClientsOnRegimeInputLayout = dialogueLayout.findViewById(R.id.number_of_clients_on_regime);
        quantityExpiredInputLayout = dialogueLayout.findViewById(R.id.quantity_expired);
        stockOutDaysInputLayout = dialogueLayout.findViewById(R.id.stock_out_days);
        wastageInputLayout = dialogueLayout.findViewById(R.id.wastage);
        reportingPeriod = dialogueLayout.findViewById(R.id.reporting_period);
        availabilityOfClientsOnRegimeSpinner = dialogueLayout.findViewById(R.id.do_you_have_any_clients_on_regime);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                product = baseDatabase.productsModelDao().getProductById(productId);
                postingFrequency = baseDatabase.postingFrequencyModelDao().getPostingFrequencyById(product.getPostingFrequency());
                unit = baseDatabase.unitsDao().getUnit(product.getUnitId());
                productReportingSchedules = baseDatabase.productReportingScheduleModelDao().getMissedProductReportings(productId, Calendar.getInstance().getTimeInMillis());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                productName.setText(product.getName());
                stockAdjustmentQuantity.setHint("Stock On Hand in (" + unit.getName() + ")");
                if (product.isTrackWastage()) {
                    wastageInputLayout.setVisibility(View.VISIBLE);
                }

                if (product.isTrackQuantityExpired()) {
                    quantityExpiredInputLayout.setVisibility(View.VISIBLE);
                }

                if (product.isTrackNumberOfPatients()) {
                    numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                }

                List<String> reportingDate = new ArrayList<>();
                for (ProductReportingSchedule productReportingSchedules : productReportingSchedules) {
                    reportingDate.add(simpleDateFormat.format(productReportingSchedules.getScheduledDate()));
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, reportingDate);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                reportingPeriod.setAdapter(spinAdapter);

            }
        }.execute();


        reportingPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    reportingScheduleId = productReportingSchedules.get(i).getId();
                } catch (Exception e) {
                    e.printStackTrace();
                    reportingScheduleId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final String[] availabilityOfClientsOnRegime = {"Yes", "No"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, availabilityOfClientsOnRegime);
        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
        availabilityOfClientsOnRegimeSpinner.setAdapter(spinAdapter);


        availabilityOfClientsOnRegimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (availabilityOfClientsOnRegime[i].equalsIgnoreCase("yes") && product.isTrackNumberOfPatients()) {
                        hasClients = true;
                        numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        hasClients = false;
                        numberOfClientsOnRegimeInputLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hasClients = null;
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
                    new AsyncTask<Void, Void, String>() {

                        private String stockQuantity, numberOfClientsOnRegime, quantityExpired, wastage, stockOutDays;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            stockQuantity = stockAdjustmentQuantity.getEditText().getText().toString();
                            stockOutDays = stockOutDaysInputLayout.getEditText().getText().toString();
                            numberOfClientsOnRegime = numberOfClientsOnRegimeInputLayout.getEditText().getText().toString();
                            quantityExpired = quantityExpiredInputLayout.getEditText().getText().toString();
                            wastage = wastageInputLayout.getEditText().getText().toString();

                        }

                        @Override
                        protected String doInBackground(Void... voids) {
                            Transactions transactions = new Transactions();

                            transactions.setId(UUID.randomUUID().toString());
                            transactions.setProductId(productId);
                            transactions.setUserId(Integer.valueOf(session.getUserUUID()));
                            transactions.setTransactionTypeId(1);
                            transactions.setScheduleId(reportingScheduleId);
                            transactions.setAmount(Integer.valueOf(stockQuantity));

                            transactions.setStockOutDays(Integer.parseInt(stockOutDays));

                            if (hasClients && product.isTrackNumberOfPatients()) {
                                transactions.setClientsOnRegime(numberOfClientsOnRegime);
                            }

                            if (product.isTrackQuantityExpired()) {
                                transactions.setClientsOnRegime(quantityExpired);
                            }

                            if (product.isTrackWastage()) {
                                transactions.setWastage(wastage);
                            }

                            transactions.setStatusId(1);
                            transactions.setStatusId(1);
                            Calendar c = Calendar.getInstance();
                            transactions.setCreated_at(c.getTimeInMillis());

                            Balances balances = baseDatabase.balanceModelDao().getBalance(productId, session.getFacilityId());

                            transactions.setConsumptionQuantity(balances.getConsumptionQuantity());
                            baseDatabase.transactionsDao().addTransactions(transactions);

                            balances.setBalance(Integer.valueOf(stockQuantity));

                            baseDatabase.balanceModelDao().addBalance(balances);


                            return transactions.getId();
                        }

                        @Override
                        protected void onPostExecute(String transactionId) {
                            super.onPostExecute(transactionId);
                            stockAdjustmentQuantity.getEditText().setText("");
                            Toast.makeText(getActivity(), "Transaction Added successfully", Toast.LENGTH_LONG).show();


                            // Create a Constraints object that defines when the task should run
                            Constraints networkConstraints = new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build();


                            OneTimeWorkRequest sendTransactionWorker = new OneTimeWorkRequest.Builder(SendTransactionsWorker.class)
                                    .setConstraints(networkConstraints)
                                    .setInputData(
                                            new Data.Builder()
                                                    .putString("transactionId", transactionId)
                                                    .build()
                                    )
                                    .build();

                            OneTimeWorkRequest getPostingSchedule = new OneTimeWorkRequest.Builder(GetSchedulesWorker.class)
                                    .setConstraints(networkConstraints)
                                    .build();


                            WorkManager.getInstance()
                                    .beginWith(sendTransactionWorker)
                                    // Note: WorkManager.beginWith() returns a
                                    // WorkContinuation object; the following calls are
                                    // to WorkContinuation methods
                                    .then(getPostingSchedule)
                                    .enqueue();

                            dismiss();

                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                } else if (stockAdjustmentQuantity.getEditText().getText().toString().equals("")) {
                    stockAdjustmentQuantity.getEditText().setError("Please fill the stock on hand quantity");
                } else if (numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals("")) {
                    numberOfClientsOnRegimeInputLayout.getEditText().setError("Please fill the number of clients on regime");
                }

            }
        });


        return dialogueLayout;
    }

    public boolean checkInputs() {
        if (stockAdjustmentQuantity.getEditText().getText().toString().equals("")) {
            stockAdjustmentQuantity.getEditText().setError("Please fill the stock on hand quantity");
            return false;
        } else if (reportingScheduleId == 0) {
            reportingPeriod.setError("Please select the reporting period");
            return false;
        } else if (hasClients == null) {
            availabilityOfClientsOnRegimeSpinner.setError("Please select this");
            return false;
        } else if (numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals("") && product.isTrackNumberOfPatients() && hasClients) {
            numberOfClientsOnRegimeInputLayout.getEditText().setError("Please fill the number of clients on regime");
            return false;
        } else if (stockOutDaysInputLayout.getEditText().getText().toString().equals("")) {
            stockOutDaysInputLayout.getEditText().setError("Please fill the stockout days quantity");
            return false;
        } else if (Integer.parseInt(stockOutDaysInputLayout.getEditText().getText().toString()) > postingFrequency.getNumber_of_days()) {
            stockOutDaysInputLayout.getEditText().setError("Stock out days cannot be greater than " + postingFrequency.getNumber_of_days());
            return false;
        } else if (wastageInputLayout.getEditText().getText().toString().equals("") && product.isTrackWastage()) {
            wastageInputLayout.getEditText().setError("Please fill wastage quantity");
            return false;
        } else if (quantityExpiredInputLayout.getEditText().getText().toString().equals("") && product.isTrackQuantityExpired()) {
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
