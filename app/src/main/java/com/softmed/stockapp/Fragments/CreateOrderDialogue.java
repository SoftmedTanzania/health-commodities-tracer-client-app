package com.softmed.stockapp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
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

import com.softmed.stockapp.Utils.LoadProductPhotoAsync;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Activities.AddProductActivity;
import com.softmed.stockapp.api.Endpoints;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Orders;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.responces.LoginResponse;
import com.softmed.stockapp.Utils.ServiceGenerator;
import com.softmed.stockapp.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Dialog allowing users to select a date.
 */
public class CreateOrderDialogue extends android.support.v4.app.DialogFragment {
    private static final String TAG=CreateOrderDialogue.class.getSimpleName();
    private boolean sentSucessfully=false;
    double longitude;
    double latitude;
    private RelativeLayout dialogueLayout;
    private MaterialSpinner categorySpinner,subCategorySpinner, productsSpinner,suppliersSpinner;
    private List<Product> products;
    private TextView description,price;
    private long typeId;
    public static AppDatabase baseDatabase;
    private  List<Category> categories = new ArrayList<>();
    SessionManager sess;
    private int supplierId;
    private  String batchId;
    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    /**
     * Permissions required to read and write contacts. Used by the {@link AddProductActivity}.
     */
    private static String[] PERMISSIONS_EXTERNAL_STORAGE= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private  List<String> suppliersNames = new ArrayList<>();
    private  List<LoginResponse> suppliers = new ArrayList<>();

    private List<String> categoryStrings = new ArrayList<>();


    public CreateOrderDialogue() {
    }


    public static CreateOrderDialogue newInstance(String batchId) {
        CreateOrderDialogue f = new CreateOrderDialogue();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("batchId", batchId);
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
        batchId = getArguments().getString("batchId");
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);


        sess = new SessionManager(getActivity().getApplicationContext());
        dialogueLayout =(RelativeLayout)inflater.inflate(R.layout.dialogue_create_order, container, false);
        description = (TextView) dialogueLayout.findViewById(R.id.product_description);
        price = (TextView) dialogueLayout.findViewById(R.id.price);
        categorySpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.spin_category_spinner);
        subCategorySpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.sub_category_spinner);
        productsSpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.products_spinner);
        suppliersSpinner = (MaterialSpinner) dialogueLayout.findViewById(R.id.suppliers_spinner);


        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                categories = baseDatabase.categoriesModel().getAllCategories();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                for(Category category:categories){
                    categoryStrings.add(category.getName());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, categoryStrings);
                spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                categorySpinner.setAdapter(spinAdapter);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        for(Category category:categories){
            categoryStrings.add(category.getName());
        }

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, categoryStrings);
        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
        categorySpinner.setAdapter(spinAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {

                products = new ArrayList<>();

                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            products = baseDatabase.productsModelDao().getProductsSummaryByCategoryId(categories.get(i).getId());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v) {
                        super.onPostExecute(v);

                        final List<String> productsNames= new ArrayList<>();
                        for(Product product:products){
                            productsNames.add(product.getName());
                        }

                        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, productsNames);
                        spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
                        productsSpinner.setAdapter(spinAdapter);

                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    description.setText(products.get(i).getDescription());
                    price.setText(String.valueOf(products.get(i).getPrice()));
                    typeId = products.get(i).getId();
                }catch (Exception e){
                    typeId = -1;
                    description.setText("");
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        Endpoints.LoginService loginService = ServiceGenerator.createService(Endpoints.LoginService.class, sess.getUserName(), sess.getUserPass());
        Call<List<LoginResponse>> call = loginService.getAllUsers();
        call.enqueue(new Callback<List<LoginResponse>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<List<LoginResponse>> call, Response<List<LoginResponse>> response) {

//                Log.d(TAG,"response = "+response.toString());
//                if (response.isSuccessful()) {
//
//                    suppliersNames = new ArrayList<>();
//                    suppliers = new ArrayList<>();
//                   for(LoginResponse r : response.body()){
//                       try {
//                           if (r.getLevelResponses().get(0).getId() == 2) {
//                               suppliers.add(r);
//                               suppliersNames.add(r.getFirstName() + " " + r.getSurname());
//                           }
//                       }catch (Exception e){
//                           e.printStackTrace();
//                       }
//                   }
//
//                    ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item_black, suppliersNames);
//                    spinAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_black);
//                    suppliersSpinner.setAdapter(spinAdapter);
//
//
//                }
            }

            @Override
            public void onFailure(Call<List<LoginResponse>> call, Throwable t) {
                // something went completely south (like no internet connection)
                try {
                    Log.d("Error", t.getMessage());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

        });


        suppliersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    supplierId = suppliers.get(i).getUser().getId();
                }catch (Exception e){
                    supplierId = -1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        dialogueLayout.findViewById(R.id.add_product).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Orders order = new Orders();
                        order.setBatch(batchId);
                        order.setProduct_id((int)typeId);
                        order.setDealer_id(Integer.valueOf(sess.getUserUUID()));
                        order.setSupplier_id(supplierId);
                        order.setStatus_id(0);
                        order.setStatus(0);
                        order.setUuid(UUID.randomUUID().toString());
                        order.setOrdered(Integer.valueOf(((TextInputLayout)dialogueLayout.findViewById(R.id.product_stock_on_hand_input_layout)).getEditText().getText().toString()));

                        baseDatabase.orderModelDao().addOrder(order);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v) {
                        super.onPostExecute(v);
                        Toast.makeText(getActivity(),"Product Added Successfully",Toast.LENGTH_LONG).show();
                        dismiss();

                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



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
    @Override
    public void onActivityCreated(Bundle arg0){
        super.onActivityCreated(arg0);
//        getDialog().getWindow().getAttributes().windowAnimations= R.style.dialogue_animation;
    }





    public void showImage(ImageView v, String photoPath) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            setRequestExternalStoragePermissions();

        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
//            Glide.with(getApplicationContext()).load(photoPath).into(mProductPhotoImageView);
            new LoadProductPhotoAsync(getActivity(), v).execute(photoPath);
            Log.d(TAG,"show status");

        }
    }

    /**
     * Requests the EXTERNAL STORAGE permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void setRequestExternalStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG, "Displaying external storage  permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(dialogueLayout.findViewById(R.id.content), R.string.permission_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(getActivity(), PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "requesting external storage permission.");
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }


}
