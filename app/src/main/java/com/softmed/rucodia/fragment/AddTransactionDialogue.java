package com.softmed.rucodia.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softmed.rucodia.LoadProductPhotoAsync;
import com.softmed.rucodia.R;
import com.softmed.rucodia.activity.AddProductActivity;
import com.softmed.rucodia.activity.DetailActivity;
import com.softmed.rucodia.database.AppDatabase;
import com.softmed.rucodia.dom.objects.Balances;
import com.softmed.rucodia.dom.objects.Category;
import com.softmed.rucodia.dom.objects.Product;
import com.softmed.rucodia.dom.objects.SubCategory;
import com.softmed.rucodia.dom.objects.TransactionType;
import com.softmed.rucodia.dom.objects.Transactions;
import com.softmed.rucodia.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.softmed.rucodia.utils.Calendars.toBeginningOfTheDay;


/**
 * Dialog allowing users to select a date.
 */
public class AddTransactionDialogue extends android.support.v4.app.DialogFragment {
    private static final String TAG=AddTransactionDialogue.class.getSimpleName();
    private View dialogueLayout;
    public static AppDatabase baseDatabase;
    private String stockAdjustmentReason="";
    private MaterialSpinner stockAdjustmentReasonSpinner;
    private List<TransactionType> transactionTypes;
    private int stockAdjustmentReasonId;
    private TextInputLayout stockAdjustmentQuantity;
    private int productId,price;

    // Session Manager Class
    private SessionManager session;

    public AddTransactionDialogue() {
    }


    public static AddTransactionDialogue newInstance(int productId, int price) {
        AddTransactionDialogue f = new AddTransactionDialogue();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("productId", productId);
        args.putInt("price", price);
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
        price = getArguments().getInt("price");
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

        dialogueLayout =inflater.inflate(R.layout.dialogue_create_transaction, container, false);


        stockAdjustmentQuantity = (TextInputLayout) dialogueLayout.findViewById(R.id.stock_adjustment_quantity);

        stockAdjustmentReasonSpinner = (MaterialSpinner)dialogueLayout.findViewById(R.id.stock_adjustment_reason);

        stockAdjustmentReasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    stockAdjustmentReason = transactionTypes.get(i).getName();
                    stockAdjustmentReasonId = transactionTypes.get(i).getId();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                transactionTypes = baseDatabase.transactionTypeModelDao().getTransactionTypes();
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);

                List<String> transactionTypesNames = new ArrayList<>();

                for(TransactionType tType:transactionTypes){
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
                if(!stockAdjustmentReason.equals("")){

                    if(!stockAdjustmentQuantity.getEditText().getText().toString().equals("")){


                        new AsyncTask<Void, Void, Void>(){
                            @Override
                            protected Void doInBackground(Void... voids) {
                                Transactions transactions = new Transactions();
                                transactions.setUuid(UUID.randomUUID().toString());
                                transactions.setProduct_id(productId);
                                transactions.setUser_id(Integer.valueOf(session.getUserUUID()));
                                transactions.setTransactiontype_id(stockAdjustmentReasonId);
                                transactions.setAmount(Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString()));
                                transactions.setPrice(price);
                                transactions.setStatus_id(1);

                                Calendar c = Calendar.getInstance();
                                toBeginningOfTheDay(c);

                                transactions.setCreated_at(c.getTimeInMillis());
                                baseDatabase.transactionsDao().addTransactions(transactions);

                                Balances balances = baseDatabase.balanceModelDao().getBalance(productId);

                                if(stockAdjustmentReasonId==1){
                                    balances.setBalance(balances.getBalance()+Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString()));
                                }else if(stockAdjustmentReasonId==2){
                                    balances.setBalance(balances.getBalance()-Integer.valueOf(stockAdjustmentQuantity.getEditText().getText().toString()));
                                }

                                baseDatabase.balanceModelDao().addBalance(balances);


                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void v) {
                                super.onPostExecute(v);
                                stockAdjustmentQuantity.getEditText().setText("");
                                Toast.makeText(getActivity(),"Transaction Added successfully",Toast.LENGTH_LONG).show();
                                dismiss();

                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



                    }else{
                        stockAdjustmentQuantity.getEditText().setError("Please fill the adjustment quantity");
                    }
                }else{
                    stockAdjustmentReasonSpinner.setError("Please select the transaction type");
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
