package com.softmed.stockapp.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.softmed.stockapp.R;
import com.softmed.stockapp.dom.entities.CategoryBalance;
import com.softmed.stockapp.dom.entities.ProductBalance;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.CategoryBalanceViewModel;
import com.softmed.stockapp.viewmodels.ProductsViewModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private BarChart mChart1;
    private CombinedChart mChart2;
    private List<String> categoryNames = new ArrayList<>();
    private List<Integer> sizes = new ArrayList<>();
    private List<ProductBalance> mProductBalances;
    private List<ProductBalance> monthOfStockCategoryProductBalances;
    private LinearLayout productBalancesList;
    private Spinner categoriesSpinner;
    private ProductsViewModel productsViewModel;
    private CategoryBalance selectedMonthOfStockCategory;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rowview = inflater.inflate(R.layout.fragment_dashboard, container, false);


        productBalancesList = rowview.findViewById(R.id.product_balances_list);

        categoriesSpinner = rowview.findViewById(R.id.category_spinner);

        Typeface muliTypeface = ResourcesCompat.getFont(getActivity(), R.font.muli);

        //Bar chart configurations
        mChart1 = rowview.findViewById(R.id.chart1);
        mChart1.getDescription().setEnabled(false);
        mChart1.setBackgroundColor(Color.WHITE);
        mChart1.setDrawGridBackground(false);
        mChart1.setDrawBarShadow(false);
        mChart1.setHighlightFullBarEnabled(false);
        mChart1.setDrawValueAboveBar(false);


        ValueFormatter xAxisStockMonthValueFormatter = new XAxisValueFormatter();
        XAxis chart1XAxis = mChart1.getXAxis();
        chart1XAxis.setDrawGridLines(false);
        chart1XAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart1XAxis.setGranularityEnabled(true);
        chart1XAxis.setValueFormatter(xAxisStockMonthValueFormatter);
        chart1XAxis.setAxisMinimum(0f);
        chart1XAxis.setLabelRotationAngle(-90f);

        ValueFormatter customValueFormatter = new MyAxisValueFormatter();
        final YAxis chart1LeftAxis = mChart1.getAxisLeft();
        chart1LeftAxis.setLabelCount(8, false);
        chart1LeftAxis.setValueFormatter(customValueFormatter);
        chart1LeftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        chart1LeftAxis.setSpaceTop(15f);
        chart1LeftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis chart1RightAxis = mChart1.getAxisRight();
        chart1RightAxis.setDrawGridLines(false);
        chart1RightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setTypeface(muliTypeface);
        l.setWordWrapEnabled(true);


        //Bar chart configurations
        mChart2 = rowview.findViewById(R.id.chart2);
        mChart2.getDescription().setEnabled(false);
        mChart2.setBackgroundColor(Color.WHITE);
        mChart2.setDrawGridBackground(false);
        mChart2.setDrawBarShadow(false);
        mChart2.setHighlightFullBarEnabled(false);
        mChart2.setDrawValueAboveBar(false);


        ValueFormatter xAxisFormatter = new MonthOfStockXAxisValueFormatter();
        XAxis xAxis = mChart2.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelRotationAngle(-90f);


        // draw bars behind lines
        mChart2.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        ValueFormatter custom = new MyAxisValueFormatter();
        final YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart2.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        Legend l2 = mChart2.getLegend();
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setForm(Legend.LegendForm.SQUARE);
        l2.setFormSize(9f);
        l2.setTextSize(11f);
        l2.setXEntrySpace(4f);
        l2.setTypeface(muliTypeface);
        l2.setWordWrapEnabled(true);


        CategoryBalanceViewModel categoryBalanceViewModel = ViewModelProviders.of(this).get(CategoryBalanceViewModel.class);
        categoryBalanceViewModel.getCategoryBalances().observe(getActivity(), categoryBalances -> {
            List<String> categoryNames = new ArrayList<>();
            for (CategoryBalance categoryBalance : categoryBalances) {
                categoryNames.add(categoryBalance.getName());
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            categoryNames); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            categoriesSpinner.setAdapter(spinnerArrayAdapter);
            categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedMonthOfStockCategory = categoryBalances.get(i);
                    setChartData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        });

        productsViewModel = ViewModelProviders.of(this).get(ProductsViewModel.class);
        productsViewModel.getProductBalances(new SessionManager(getActivity()).getFacilityId()).observe(getActivity(), productBalances -> {
            mProductBalances = productBalances;
            setChartData();
        });

        return rowview;
    }

    private void setChartData(){
        try {
            if (mProductBalances != null && mProductBalances.size() > 0) {
                monthOfStockCategoryProductBalances = new ArrayList<>();
                productBalancesList.removeAllViews();
                ArrayList<BarEntry> yVals1 = new ArrayList<>();
                ArrayList<BarEntry> yVals2 = new ArrayList<>();

                int productBalanceChartIndex = 1;
                int categoryProductBalanceChartIndex = 1;

                final List<Integer> chart2Colors = new ArrayList<>();
                ArrayList<Entry> lineEntries = new ArrayList<>();

                for (ProductBalance productBalance : mProductBalances) {
                    Log.d(TAG, "coze = " + productBalance.getProductName() + "  -- " + productBalance.getConsumptionQuantity());

                    View v = getLayoutInflater().inflate(R.layout.view_inventory_balance_item, null);
                    ((TextView) v.findViewById(R.id.sn)).setText(String.valueOf(productBalanceChartIndex));
                    ((TextView) v.findViewById(R.id.product_name)).setText(productBalance.getProductCategory() + " - " + productBalance.getProductName());

                    String balance = String.valueOf(productBalance.getBalance());
                    balance += " " + productBalance.getUnit();
                    ((TextView) v.findViewById(R.id.balance)).setText(balance);

                    float stockSeverity = (productBalance.getBalance() * 1f) / productBalance.getConsumptionQuantity();

                    Log.d(TAG, "severity value = " + stockSeverity);

                    if (stockSeverity >= 6) {
                        chart2Colors.add(Color.rgb(31, 36, 93));
                    } else if (stockSeverity < 6 && stockSeverity >= 3) {
                        chart2Colors.add(Color.rgb(30, 185, 128));
                    } else if (stockSeverity < 3 && stockSeverity >= 0.5) {
                        chart2Colors.add(Color.rgb(220, 220, 70));
                    } else {
                        chart2Colors.add(Color.rgb(176, 0, 32));
                    }

                    lineEntries.add(new Entry(productBalanceChartIndex, productBalance.getConsumptionQuantity()));

                    yVals1.add(new BarEntry(productBalanceChartIndex, productBalance.getBalance()));

                    Log.d(TAG, "balance = " + (productBalance.getBalance()));
                    Log.d(TAG, "consumption of stock = " + (productBalance.getConsumptionQuantity()));

                    float monthsOfStock = (productBalance.getBalance() * 1f / productBalance.getConsumptionQuantity());
                    Log.d(TAG, "months of stock = " + monthsOfStock);

                    if(selectedMonthOfStockCategory.getCategoryId()==productBalance.getCategoryId()) {
                        monthOfStockCategoryProductBalances.add(productBalance);
                        yVals2.add(new BarEntry(categoryProductBalanceChartIndex, monthsOfStock));
                        categoryProductBalanceChartIndex++;
                    }

                    ((TextView) v.findViewById(R.id.months_of_stock)).setText(String.valueOf(monthsOfStock));

                    productBalanceChartIndex++;
                    productBalancesList.addView(v);
                }

                LineDataSet set = new LineDataSet(lineEntries, "Product Consumption");
                set.setColor(Color.rgb(136, 180, 187));
                set.setLineWidth(2.5f);
                set.setCircleColor(Color.rgb(136, 180, 187));
                set.setCircleRadius(5f);
                set.setFillColor(Color.rgb(136, 180, 187));
                set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                set.setDrawValues(true);
                set.setValueTextSize(10f);
                set.setValueTextColor(Color.rgb(136, 180, 187));

                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                LineData lineData = new LineData();
                lineData.addDataSet(set);


                BarDataSet set1 = new BarDataSet(yVals1, "Inventory Balances");
                set1.setDrawIcons(false);
                set1.setColors(chart2Colors);

                BarDataSet set2 = new BarDataSet(yVals2, "Months of Stock");
                set2.setDrawIcons(false);
                set2.setColors(chart2Colors);

                ArrayList<IBarDataSet> dataSets1 = new ArrayList<IBarDataSet>();
                dataSets1.add(set1);

                ArrayList<IBarDataSet> dataSets2 = new ArrayList<IBarDataSet>();
                dataSets2.add(set2);

                BarData barData1 = new BarData(dataSets1);
                barData1.setValueTextSize(10f);
                barData1.setBarWidth(0.9f);

                BarData barData2 = new BarData(dataSets2);
                barData2.setValueTextSize(10f);
                barData2.setBarWidth(0.9f);


                CombinedData data = new CombinedData();
                data.setData(barData1);
                data.setData(lineData);

                try {
                    mChart1.clearValues();
                }catch (Exception e){
                    e.printStackTrace();
                }
                mChart1.setData(barData2);
                mChart1.highlightValues(null);
                mChart1.invalidate();

                mChart2.setData(data);
                mChart2.highlightValues(null);
                mChart2.invalidate();

                Log.d(TAG, "Invalidated the graph");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyAxisValueFormatter extends ValueFormatter {

        public MyAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int v = (int) value;
            return v + "";
        }
    }

    class XAxisValueFormatter extends ValueFormatter {
        public XAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value) {
            if (value == mProductBalances.size() + 1 || value == 0) {
                return "";
            } else {
                if (mProductBalances.get((int) value - 1).getProductName().length() < 25) {
                    return mProductBalances.get((int) value - 1).getProductName();
                } else {
                    try {
                        return mProductBalances.get((int) value - 1).getProductName().split(" ")[0] + " " + mProductBalances.get((int) value - 1).getProductName().split(" ")[1];
                    } catch (Exception e) {
                        return mProductBalances.get((int) value - 1).getProductName();
                    }
                }
            }
        }
    }

    class MonthOfStockXAxisValueFormatter extends ValueFormatter {
        public MonthOfStockXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value) {
            if (value == mProductBalances.size() + 1 || value == 0) {
                return "";
            } else {
                if (mProductBalances.get((int) value - 1).getProductName().length() < 25) {
                    return mProductBalances.get((int) value - 1).getProductName();
                } else {
                    try {
                        return mProductBalances.get((int) value - 1).getProductName().split(" ")[0] + " " + mProductBalances.get((int) value - 1).getProductName().split(" ")[1];
                    } catch (Exception e) {
                        return mProductBalances.get((int) value - 1).getProductName();
                    }
                }
            }
        }
    }


}
