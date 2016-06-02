package com.aspiration.mileagemaster;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements TabFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final String LOG_TAG = HomeFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String DATE_FORMAT_MONTH = "MMM";
    private static final String DATE_FORMAT_YEAR_MONTH = "yyyy-MM";

    private OnFragmentInteractionListener mListener;

    HorizontalBarChart myChart;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);

        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        myChart = (HorizontalBarChart) rootview.findViewById(R.id.chart);

        ArrayList<BarEntry> vals = new ArrayList<>();

        /*BarEntry one = new BarEntry(100.000f, 0); // 0 == quarter 1
        vals.add(one);
        BarEntry two = new BarEntry(50.000f, 1); // 1 == quarter 2 ...
        vals.add(two);
        BarEntry three = new BarEntry(75.000f, 2); // 0 == quarter 1
        vals.add(three);
        BarEntry four = new BarEntry(25.000f, 3); // 1 == quarter 2 ...
        vals.add(four);
        BarEntry five = new BarEntry(2.000f, 4); // 0 == quarter 1
        vals.add(five);
        BarEntry six = new BarEntry(120.000f, 5); // 1 == quarter 2 ...
        vals.add(six);
        BarEntry seven = new BarEntry(80.000f, 6); // 0 == quarter 1
        vals.add(seven);
        BarEntry eight = new BarEntry(8.000f, 7); // 1 == quarter 2 ...
        vals.add(eight);
        BarEntry nine = new BarEntry(22.000f, 8); // 0 == quarter 1
        vals.add(nine);
        BarEntry ten = new BarEntry(33.000f, 9); // 1 == quarter 2 ...
        vals.add(ten);
        BarEntry eleven = new BarEntry(66.000f, 10); // 0 == quarter 1
        vals.add(eleven);
        BarEntry twelve = new BarEntry(76.000f, 11); // 1 == quarter 2 ...
        vals.add(twelve);*/

        BarDataSet setComp1 = new BarDataSet(vals, "miles");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);

        ArrayList<String> xVals = new ArrayList<>();




        // Need to construct a start date from a year ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -11);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YEAR_MONTH);
        String from_date = dateFormat.format(cal.getTime()) + "-01";

        // Let's query the content provider to retrieve some totals
        Map<Integer,Integer> monthly_summary = new HashMap<>();
        Cursor c = getActivity().getContentResolver().query(TripContract.TripEntry.buildTripSummaryByDate(from_date),null,null,new String[]{from_date},null);
        if (c.moveToFirst()) {
            do {
                Log.d(LOG_TAG, c.getString(0) + "|" + String.valueOf(c.getInt(1)));
                monthly_summary.put(Integer.parseInt(c.getString(0)) -1, c.getInt(1));
            } while (c.moveToNext());
        }
        c.close();

        // Load up the previous 12 months starting with this month.
        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_MONTH);
        for (int i = 0; i < 12; i++) {
            Date date = cal.getTime();
            int numeric_month = cal.get(Calendar.MONTH);
            BarEntry bar_entry = new BarEntry(monthly_summary.get(numeric_month) == null ? 0 : monthly_summary.get(numeric_month), i);
            vals.add(bar_entry);
            String month = format.format(date);
            xVals.add(month);
            cal.add(Calendar.MONTH, 1);
        }


        BarData data = new BarData(xVals,dataSets);
        myChart.setData(data);
        Legend legend = myChart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        myChart.invalidate(); // refresh

        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void refresh(Intent data) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
