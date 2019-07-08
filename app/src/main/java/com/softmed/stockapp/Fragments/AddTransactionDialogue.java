package com.softmed.stockapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
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

import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.Dom.entities.Unit;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.softmed.stockapp.Utils.Calendars.toBeginningOfTheDay;


/**
 * Dialog allowing users to select a date.
 */
public class AddTransactionDialogue extends DialogFragment {
    private static final String TAG = AddTransactionDialogue.class.getSimpleName();
    public static AppDatabase baseDatabase;
    private View dialogueLayout;
    private TextInputLayout stockAdjustmentQuantity, numberOfClientsOnRegimeInputLayout,wastageInputLayout,quantityExpiredInputLayout,stockOutDaysInputLayout;
    private MaterialSpinner  availabilityOfClientsOnRegimeSpinner,reportingPeriod;
    private List<ProductReportingSchedule> productReportingSchedules;
    private int productId, numberOfClientsOnRegime;
    private Boolean hasClients = null;
    private Product product;
    private TextView productName;
    private Unit unit;
    private int reportingScheduleId = 0;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Session Manager Class
    private SessionManager session;

    public AddTransactionDialogue() {
    }


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

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                product = baseDatabase.productsModelDao().getProductByName(productId);
                unit = baseDatabase.unitsDao().getUnit(product.getUnit_id());
                productReportingSchedules =  baseDatabase.productReportingScheduleModelDao().getMissedProductReportings(productId,Calendar.getInstance().getTimeInMillis());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                productName.setText(product.getName());
                stockAdjustmentQuantity.setHint("Stock On Hand in ("+unit.getName()+")");
                if(product.isTrack_wastage()){
                    wastageInputLayout.setVisibility(View.VISIBLE);
                }

                if(product.isTrack_quantity_expired()){
                    quantityExpiredInputLayout.setVisibility(View.VISIBLE);
                }

                if(product.isTrack_number_of_patients()){
                    numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                }

                List<String> reportingDate = new ArrayList<>();
                for(ProductReportingSchedule productReportingSchedules:productReportingSchedules){
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
                }catch (Exception e){
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
                    if (availabilityOfClientsOnRegime[i].equalsIgnoreCase("yes") && product.isTrack_number_of_patients() ) {
                        hasClients = true;
                        numberOfClientsOnRegimeInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        hasClients = false;
                        numberOfClientsOnRegimeInputLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hasClients=null;
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
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            Transactions transactions = new Transactions();

                            transactions.setId(UUID.randomUUID().toString());
                            transactions.setProduct_id(productId);
                            transactions.setUser_id(Integer.valueOf(session.getUserUUID()));
                            transactions.setTransactiontype_id(1);
                            transactions.setScheduleId(reportingScheduleId);
                            transactions.setAmount(Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString()));

                            transactions.setStockOutDays(Integer.parseInt(stockOutDaysInputLayout.getEditText().getText().toString()));

                            if (hasClients && product.isTrack_number_of_patients()) {
                                transactions.setClientsOnRegime(numberOfClientsOnRegimeInputLayout.getEditText().getText().toString());
                            }

                            if (product.isTrack_quantity_expired()) {
                                transactions.setClientsOnRegime(quantityExpiredInputLayout.getEditText().getText().toString());
                            }

                            if (product.isTrack_wastage()) {
                                transactions.setWastage(wastageInputLayout.getEditText().getText().toString());
                            }

                            transactions.setStatus_id(1);

                            transactions.setStatus_id(1);

                            Calendar c = Calendar.getInstance();

                            transactions.setCreated_at(c.getTimeInMillis());
                            baseDatabase.transactionsDao().addTransactions(transactions);

                            Balances balances = baseDatabase.balanceModelDao().getBalance(productId,session.getFacilityId());

                            balances.setBalance(Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString()));

                            baseDatabase.balanceModelDao().addBalance(balances);


                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void v) {
                            super.onPostExecute(v);
                            stockAdjustmentQuantity.getEditText().setText("");
                            Toast.makeText(getActivity(), "Transaction Added successfully", Toast.LENGTH_LONG).show();
                            dismiss();

                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                } else if(stockAdjustmentQuantity.getEditText().getText().toString().equals("")) {
                    stockAdjustmentQuantity.getEditText().setError("Please fill the stock on hand quantity");
                }else if(numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals("")) {
                    numberOfClientsOnRegimeInputLayout.getEditText().setError("Please fill the number of clients on regime");
                }

            }
        });


        return dialogueLayout;
    }

    public boolean checkInputs(){
        if(stockAdjustmentQuantity.getEditText().getText().toString().equals("")){
            stockAdjustmentQuantity.getEditText().setError("Please fill the stock on hand quantity");
            return false;
        } else if(reportingScheduleId==0){
            reportingPeriod.setError("Please select the reporting period");
            return false;
        } else if(hasClients==null){
            availabilityOfClientsOnRegimeSpinner.setError("Please select this");
            return false;
        }else if (numberOfClientsOnRegimeInputLayout.getEditText().getText().toString().equals("") && product.isTrack_number_of_patients() && hasClients) {
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
