package com.softmed.stockapp.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.CategoryBalance;
import com.softmed.stockapp.Dom.entities.ProductBalance;
import com.softmed.stockapp.R;
import com.softmed.stockapp.Utils.SessionManager;
import com.softmed.stockapp.viewmodels.CategoryBalanceViewModel;
import com.softmed.stockapp.viewmodels.ProductsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.animation.Easing.EaseInOutQuad;

public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private PieChart mChart1;
    private CombinedChart mChart2;
    private List<String> categoryNames = new ArrayList<>();
    private List<Integer> sizes = new ArrayList<>();
    private AppDatabase appDatabase;
    private List<ProductBalance> mProductBalances;
    private LinearLayout productBalancesList;
    private CategoryBalanceViewModel categoryBalanceViewModel;
    private ProductsViewModel productsViewModel;
    private List<CategoryBalance> categoryBalances;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rowview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        appDatabase = AppDatabase.getDatabase(getActivity().getApplicationContext());

        productBalancesList = rowview.findViewById(R.id.product_balances_list);

        Typeface muliTypeface = ResourcesCompat.getFont(getActivity(), R.font.muli);

        //Pie chart configurations
        mChart1 = rowview.findViewById(R.id.chart1);
        mChart1.setUsePercentValues(true);
        mChart1.getDescription().setEnabled(false);
        mChart1.setExtraOffsets(5, 10, 5, 5);

        mChart1.setDragDecelerationFrictionCoef(0.95f);
        mChart1.setCenterText("Inventory Summary");

        mChart1.setDrawHoleEnabled(true);
        mChart1.setHoleColor(Color.WHITE);

        mChart1.setTransparentCircleColor(Color.WHITE);
        mChart1.setTransparentCircleAlpha(110);

        mChart1.setHoleRadius(58f);
        mChart1.setTransparentCircleRadius(61f);

        mChart1.setDrawCenterText(true);

        mChart1.setRotationAngle(0);
        mChart1.setRotationEnabled(true);
        mChart1.setHighlightPerTapEnabled(true);

        mChart1.animateY(1400, EaseInOutQuad);


        Legend l = mChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(7f);

        l.setTypeface(muliTypeface);

        // entry label styling
        mChart1.setEntryLabelColor(Color.BLACK);
        mChart1.setEntryLabelTextSize(12f);


        //Bar chart configurations
        mChart2 = rowview.findViewById(R.id.chart2);
        mChart2.getDescription().setEnabled(false);
        mChart2.setBackgroundColor(Color.WHITE);
        mChart2.setDrawGridBackground(false);
        mChart2.setDrawBarShadow(false);
        mChart2.setHighlightFullBarEnabled(false);
        mChart2.setDrawValueAboveBar(false);


        ValueFormatter xAxisFormatter = new XAxisValueFormatter();
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


        categoryBalanceViewModel = ViewModelProviders.of(this).get(CategoryBalanceViewModel.class);
        categoryBalanceViewModel.getCategoryBalances().observe(getActivity(), new Observer<List<CategoryBalance>>() {
            @Override
            public void onChanged(@Nullable List<CategoryBalance> categoryBalances) {
                DashboardFragment.this.categoryBalances = categoryBalances;
                try {
                    setData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        productsViewModel = ViewModelProviders.of(this).get(ProductsViewModel.class);
        productsViewModel.getProductBalances(new SessionManager(getActivity()).getFacilityId()).observe(getActivity(), new Observer<List<ProductBalance>>() {
            @Override
            public void onChanged(@Nullable List<ProductBalance> productBalances) {


                mProductBalances = productBalances;
                try {
                    if (mProductBalances != null && mProductBalances.size() > 0) {
                        productBalancesList.removeAllViews();
                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                        int i = 1;


                        final List<Integer> chart2Colors = new ArrayList<>();
                        ArrayList<Entry> lineEntries = new ArrayList<>();

                        for (ProductBalance productBalance : productBalances) {
                            Log.d(TAG, "coze = " + productBalance.getProductName() + "  -- " + productBalance.getConsumptionQuantity());

                            View v = getLayoutInflater().inflate(R.layout.view_inventory_balance_item, null);
                            ((TextView) v.findViewById(R.id.sn)).setText(String.valueOf(i));
                            ((TextView) v.findViewById(R.id.product_name)).setText(productBalance.getProductCategory() + " - " + productBalance.getProductName());

                            String balance = String.valueOf(productBalance.getBalance());
                            balance += " " + productBalance.getUnit();
                            ((TextView) v.findViewById(R.id.balance)).setText(balance);

                            if (productBalance.getBalance() > productBalance.getConsumptionQuantity()) {
                                chart2Colors.add(Color.rgb(30, 185, 128));
                            } else {
                                chart2Colors.add(Color.rgb(176, 0, 32));
                            }

                            lineEntries.add(new Entry(i, productBalance.getConsumptionQuantity()));

                            yVals1.add(new BarEntry(i, productBalance.getBalance()));
                            i++;
                            productBalancesList.addView(v);
                        }

                        yVals1.add(new BarEntry(i, 0));

                        LineDataSet set = new LineDataSet(lineEntries, "Product Consumption");
                        set.setColor(Color.rgb(240, 238, 70));
                        set.setLineWidth(2.5f);
                        set.setCircleColor(Color.rgb(240, 238, 70));
                        set.setCircleRadius(5f);
                        set.setFillColor(Color.rgb(240, 238, 70));
                        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        set.setDrawValues(true);
                        set.setValueTextSize(10f);
                        set.setValueTextColor(Color.rgb(240, 238, 70));

                        set.setAxisDependency(YAxis.AxisDependency.LEFT);
                        LineData lineData = new LineData();
                        lineData.addDataSet(set);


                        BarDataSet set1 = new BarDataSet(yVals1, "Inventory Balances");
                        set1.setDrawIcons(false);
                        set1.setColors(chart2Colors);

                        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                        dataSets.add(set1);

                        BarData barData = new BarData(dataSets);
                        barData.setValueTextSize(10f);
                        barData.setBarWidth(0.9f);


                        CombinedData data = new CombinedData();
                        data.setData(barData);
                        data.setData(lineData);


                        mChart2.setData(data);
                        mChart2.highlightValues(null);
                        mChart2.invalidate();

                        Log.d(TAG, "Invalidated the graph");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rowview;
    }

    private void setData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (CategoryBalance categoryBalance : categoryBalances) {
            entries.add(new PieEntry((float) (categoryBalance.getBalance()),
                    categoryBalance.getName(),
                    getResources().getDrawable(R.drawable.ic_content_paste_white_24dp)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        mChart1.setData(data);
        mChart1.invalidate();
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


}
