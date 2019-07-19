package com.softmed.stockapp.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.softmed.stockapp.R;
import com.softmed.stockapp.activities.MainActivity;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.ProductScheduleDTO;
import com.softmed.stockapp.viewmodels.ProductReportingScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpcomingReportingScheduleFragment extends Fragment {
    private static final String TAG = UpcomingReportingScheduleFragment.class.getSimpleName();
    private ProductReportingScheduleViewModel productReportingScheduleViewModel;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private LinearLayout scheduleLayout;
    private AppDatabase appDatabase;

    public UpcomingReportingScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = AppDatabase.getDatabase(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_upcoming_reporting_schedule, container, false);

        itemView.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).moveToNextProduct();
            }
        });

        scheduleLayout = itemView.findViewById(R.id.schedule_layouts);
        productReportingScheduleViewModel = ViewModelProviders.of(getActivity()).get(ProductReportingScheduleViewModel.class);
        productReportingScheduleViewModel.getUpcomingReportingsDates(Calendar.getInstance().getTimeInMillis()).observe(getActivity(), new Observer<List<Long>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onChanged(@Nullable final List<Long> scheduledDates) {
                new AsyncTask<Void, Void, List<ProductScheduleDTO>>() {
                    @Override
                    protected List<ProductScheduleDTO> doInBackground(Void... voids) {
                        List<ProductScheduleDTO> productScheduleDTOS = new ArrayList<>();
                        for (final Long dateTime : scheduledDates) {
                            ProductScheduleDTO productScheduleDTO = new ProductScheduleDTO();
                            productScheduleDTO.setReportingDate(dateTime);
                            productScheduleDTO.setProductNames(appDatabase.productReportingScheduleModelDao().getUpcomingReportingsProductsByDate(dateTime));
                            productScheduleDTOS.add(productScheduleDTO);
                        }

                        return productScheduleDTOS;
                    }

                    @Override
                    protected void onPostExecute(List<ProductScheduleDTO> products) {
                        super.onPostExecute(products);
                        scheduleLayout.removeAllViews();
                        Log.d(TAG, "Dates size = " + scheduledDates.size());
                        for (ProductScheduleDTO productScheduleDTO : products) {
                            Date date = new Date();
                            date.setTime(productScheduleDTO.getReportingDate());

                            View scheduleItemView = inflater.inflate(R.layout.view_schedule_list_item, container, false);

                            LinearLayout linearLayout = scheduleItemView.findViewById(R.id.product_names_title);

                            for (String productName : productScheduleDTO.getProductNames()) {
                                Log.d(TAG, "Dates Product = " + productName);
                                TextView productNameTextView = (TextView) inflater.inflate(R.layout.view_scheduled_product_item, null);
                                productNameTextView.setText(productName);
                                linearLayout.addView(productNameTextView);
                            }

                            scheduleItemView.invalidate();


                            Log.d(TAG, "Dates  = " + simpleDateFormat.format(date));
                            TextView textView = scheduleItemView.findViewById(R.id.scheduled_date);
                            textView.setText(simpleDateFormat.format(date));
                            scheduleLayout.addView(scheduleItemView);
                        }


                    }
                }.execute();

            }
        });

        return itemView;

    }

}
