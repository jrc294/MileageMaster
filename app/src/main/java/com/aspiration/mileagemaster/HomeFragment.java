package com.aspiration.mileagemaster;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspiration.mileagemaster.data.TripContract;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements TabFragment {


    static final String LOG_TAG = HomeFragment.class.getSimpleName();

    private static final String DATE_FORMAT_MONTH = "MMM";
    private static final String DATE_FORMAT_YEAR_MONTH = "yyyy-MM";

    HorizontalBarChart myChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);

        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        myChart = (HorizontalBarChart) rootview.findViewById(R.id.chart);

        // Create an asyncTask to do this. Would normally use a content loader
        // but the rubric requires that I use an asyncTask somewhere.

        refresh(null);
        return rootview;
    }

    public void updateChart(Map<Integer,Integer> monthly_summary) {

        ArrayList<BarEntry> barVals = new ArrayList<>();

        BarDataSet setDistance = new BarDataSet(barVals, getString(R.string.total_miles_traveled));
        setDistance.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(setDistance);

        ArrayList<String> labelVals = new ArrayList<>();

        // Load up the previous 12 months starting with this month.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_MONTH);
        for (int i = 0; i < 12; i++) {
            Date date = cal.getTime();
            int numeric_month = cal.get(Calendar.MONTH);
            BarEntry bar_entry = new BarEntry(monthly_summary.get(numeric_month) == null ? 0 : monthly_summary.get(numeric_month), i);
            barVals.add(bar_entry);
            String month = format.format(date);
            labelVals.add(month);
            cal.add(Calendar.MONTH, 1);
        }

        BarData data = new BarData(labelVals,dataSets);
        data.setValueFormatter(new IntegerValueFormatter());
        myChart.setData(data);
        Legend legend = myChart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        XAxis xAxis = myChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawAxisLine(false);
        YAxis left = myChart.getAxisLeft();
        left.setEnabled(false);
        left = myChart.getAxisRight();
        left.setDrawGridLines(false);
        myChart.setDrawMarkerViews(false);
        myChart.setScaleEnabled(false);
        myChart.setHighlightPerTapEnabled(false);
        myChart.setHighlightPerDragEnabled(false);

        myChart.invalidate(); // refresh
    }

    @Override
    public void refresh(Intent data) {
        FetchData fetchData = new FetchData();
        fetchData.execute();
    }

    public class IntegerValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public IntegerValueFormatter() {
            mFormat = new DecimalFormat("########0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

    public class FetchData extends AsyncTask<Void, Void, Map<Integer,Integer>> {

        @Override
        protected Map<Integer,Integer> doInBackground(Void... params) {
            // Let's query the content provider to retrieve some totals

            // Need to construct a start date from a year ago
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -11);

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YEAR_MONTH);
            String from_date = dateFormat.format(cal.getTime()) + "-01";

            Map<Integer,Integer> monthly_summary = new HashMap<>();
            Cursor c = getActivity().getContentResolver().query(TripContract.TripEntry.buildTripSummaryByDate(from_date),null,null,new String[]{from_date},null);
            if (c.moveToFirst()) {
                do {
                    //Log.d(LOG_TAG, c.getString(0) + "|" + String.valueOf(c.getInt(1)));
                    monthly_summary.put(Integer.parseInt(c.getString(0)) -1, c.getInt(1));
                } while (c.moveToNext());
            }
            c.close();
            return monthly_summary;
        }

        @Override
        protected void onPostExecute(Map<Integer,Integer> monthly_summary) {
            HomeFragment.this.updateChart(monthly_summary);
        }
    }
}
