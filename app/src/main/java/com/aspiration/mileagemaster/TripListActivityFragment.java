package com.aspiration.mileagemaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspiration.mileagemaster.data.StandardListAdapter;
import com.aspiration.mileagemaster.data.TripContract;
import com.aspiration.mileagemaster.data.TripListAdapter;
import com.aspiration.mileagemaster.data.Util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class TripListActivityFragment extends Fragment implements MasterDetailFragment, LoaderManager.LoaderCallbacks<Cursor>{

    RecyclerView mRecyclerView;
    private static final int TRIP_LOADER = 2;
    private static final int CHARGES_LOADER = 3;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_SHORT = "MMM-dd";
    private static final String SELECTED_TRIP = "selected_trip";
    long mClientId;
    boolean mFilterOnClient;
    String mCompletedStatus;
    Calendar mCalendarFrom;
    Calendar mCalendarTo;
    TextView mTextViewFilter;
    String mClientName;
    int mMilesTraveled;
    double mMileageCharge;
    double mChargesCharge;
    int mSelectedTrip = -1;

    public TripListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);

        mCompletedStatus = getString(R.string.all_trips);
        mCalendarFrom = Calendar.getInstance();
        mCalendarFrom.add(Calendar.DATE, -30);

        mCalendarTo = Calendar.getInstance();

        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trip_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTextViewFilter = (TextView) rootView.findViewById(R.id.tvFilterDetails);

        if (savedInstanceState != null) {
            mCompletedStatus = savedInstanceState.getString(Search.COMPLETED_STATUS);
            mFilterOnClient = savedInstanceState.getBoolean(Search.INCLUDE_CLIENT);
            mCalendarFrom = (Calendar) savedInstanceState.getSerializable(Search.DATE_FROM);
            mCalendarTo = (Calendar) savedInstanceState.getSerializable(Search.DATE_TO);
            mClientName = savedInstanceState.getString(Search.CLIENT_NAME);
            mSelectedTrip = savedInstanceState.getInt(SELECTED_TRIP);
        }

        updateFilterValues();

        return rootView;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getActivity().getSupportLoaderManager().initLoader(TRIP_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(CHARGES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Search.COMPLETED_STATUS, mCompletedStatus);
        outState.putBoolean(Search.INCLUDE_CLIENT, mFilterOnClient);
        outState.putSerializable(Search.DATE_FROM, mCalendarFrom);
        outState.putSerializable(Search.DATE_TO, mCalendarTo);
        outState.putString(Search.CLIENT_NAME, mClientName);
        int selected_trip = ((TripListAdapter) mRecyclerView.getAdapter()).getTrip();
        outState.putInt(SELECTED_TRIP, selected_trip);
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        // Build selection3
        Loader<Cursor> ret = null;
        String selection = null;
        ArrayList<String> args_list = new ArrayList<>();
        String[] args = null;
        Calendar calendarTo = Calendar.getInstance();

        calendarTo.setTime(mCalendarTo.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String from_date = dateFormat.format(mCalendarFrom.getTime());
        calendarTo.add(Calendar.DATE, 1);
        String to_date = dateFormat.format(calendarTo.getTime());



        selection = TripContract.TripEntry.COLUMN_DATE_TIME + " > ? and ";
        args_list.add(from_date);

        selection += TripContract.TripEntry.COLUMN_DATE_TIME + " < ?";
        args_list.add(to_date);

        if (mFilterOnClient) {
            selection += " and " + TripContract.TripEntry.COLUMN_CLIENT_ID + " = ?";
            args_list.add(String.valueOf(mClientId));
        }

        if (!mCompletedStatus.equals(getString(R.string.all_trips))) {
            selection += " and " + TripContract.TripEntry.COLUMN_COMPLETE + " = ?";
            if (mCompletedStatus.equals(getString(R.string.completed_only))) {
                args_list.add("1");
            } else {
                args_list.add("0");
            }
        }

        if (args_list.size() > 0) {
            args = args_list.toArray(new String[args_list.size()]);
        }

        if (id == TRIP_LOADER) {
            Uri uri = TripContract.TripEntry.CONTENT_URI;
            ret = new CursorLoader(getActivity(),
                    uri,
                    new String[]{TripContract.TripEntry._ID,
                            TripContract.TripEntry.COLUMN_DESCRIPTION, TripContract.TripEntry.COLUMN_DISTANCE, TripContract.TripEntry.COLUMN_COST}, selection, args, null);
        }

        if (id == CHARGES_LOADER) {
            Uri uri = TripContract.TripChargeEntry.CONTENT_CHARGES_FOR_CLIENT_RANGE_URI;
            ret = new CursorLoader(getActivity(),
                    uri,
                    null, selection, args, null);
        }
        return ret;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == TRIP_LOADER) {
            mMileageCharge = 0;
            mMilesTraveled = 0;
            mRecyclerView.setAdapter(new TripListAdapter(data, (TripListAdapter.Callback) getActivity()));
            ((TripListAdapter) mRecyclerView.getAdapter()).setTrip(mSelectedTrip);

            if (data.moveToFirst()) {
                do {
                    mMilesTraveled += Integer.parseInt(data.getString(data.getColumnIndex(TripContract.TripEntry.COLUMN_DISTANCE)));
                    mMileageCharge += data.getDouble(data.getColumnIndex(TripContract.TripEntry.COLUMN_COST));
                } while (data.moveToNext());
            }
        }

        if (loader.getId() == CHARGES_LOADER) {
            mChargesCharge = 0;
            if (data.moveToFirst()) {
                mChargesCharge = data.getDouble(0);
            }
        }
        updateFilterValues();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void refresh(Intent data) {
        mClientId = data.getExtras().getLong(Search.CLIENT_ID);
        mFilterOnClient = data.getExtras().getBoolean(Search.INCLUDE_CLIENT);
        mCompletedStatus = data.getExtras().getString(Search.COMPLETED_STATUS);
        mCalendarFrom = (Calendar) data.getExtras().getSerializable(Search.DATE_FROM);
        mCalendarTo = (Calendar) data.getExtras().getSerializable(Search.DATE_TO);
        mClientName = data.getExtras().getString(Search.CLIENT_NAME);
        mSelectedTrip = -1;
        updateFilterValues();
        getActivity().getSupportLoaderManager().restartLoader(TRIP_LOADER, null, this);
        getActivity().getSupportLoaderManager().restartLoader(CHARGES_LOADER, null, this);
    }

    private void updateFilterValues() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_SHORT);
        String from_date = dateFormat.format(mCalendarFrom.getTime());
        String to_date = dateFormat.format(mCalendarTo.getTime());

        String filter_details = String.format("%1$s, %2$s %3$s %4$s",mCompletedStatus,
                from_date, getString(R.string.to).toLowerCase(), to_date);
        if (mFilterOnClient) {
            filter_details += String.format(" %1$s %2$s",getString(R.string.For), mClientName);
        }
        filter_details += String.format("\n%1$d %2$ss", mMilesTraveled, Util.getDistanceUnit(getActivity()));
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        filter_details += String.format(", %1$s %2$s", fmt.format(mMileageCharge + mChargesCharge), "charged");
        mTextViewFilter.setText(filter_details);
    }

    @Override
    public void reloadDetail(long id) {
        mSelectedTrip = ((TripListAdapter) mRecyclerView.getAdapter()).getTrip();
    }

    @Override
    public void updateCalendar(Calendar calendar) {

    }

    @Override
    public void deleteItem() {

    }

}
