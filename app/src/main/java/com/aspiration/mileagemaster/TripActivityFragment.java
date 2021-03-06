package com.aspiration.mileagemaster;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;
import com.aspiration.mileagemaster.data.Util;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class TripActivityFragment extends Fragment implements BackFragment, LoaderManager.LoaderCallbacks<Cursor>{

    public TripActivityFragment() {
    }

    String[] tripColumns = new String[]
            {TripContract.TripEntry.COLUMN_DATE_TIME,
                    TripContract.TripEntry.COLUMN_CLIENT_ID,
                    TripContract.TripEntry.COLUMN_STARTING_PLACE,
                    TripContract.TripEntry.COLUMN_ENDING_PLACE,
                    TripContract.TripEntry.COLUMN_DISTANCE,
                    TripContract.TripEntry.COLUMN_COST,
                    TripContract.TripEntry.COLUMN_COMPLETE,
                    TripContract.TripEntry.COLUMN_NOTES};

    String[] chargeColumns = new String[]
            {TripContract.TripChargeEntry.COLUMN_STANDARD_CHARGE_ID,
                    TripContract.TripChargeEntry.COLUMN_COST,
                    TripContract.TripChargeEntry.COLUMN_DESCRIPTION,
                    TripContract.TripChargeEntry.COLUMN_IS_TAX};

    Long mId;



    public static final String ACTION_DATA_UPDATED = "com.aspiration.mileagemaster.ACTION_DATA_UPDATED";
    private static final int TRIP_CLIENT_LOADER = 10;
    private static final int TRIP_CHARGE_LOADER = 11;
    private static final int TRIP_LOADER = 12;
    private static final int TRIP_CHARGES_LOADER = 13;
    private static final String DATE = "date";
    private static final String TAX_RATE = "tax_rate";
    private static final String CHARGE_DATA = "charge_data";
    private static final String CLIENT_ID = "client_id";
    private static final String CHARGE_1_ID = "charge_1_id";
    private static final String CHARGE_2_ID = "charge_2_id";
    private static final String CHARGE_3_ID = "charge_3_id";
    private static final String MILES_COST = "miles_cost";
    private static final String CHARGE_1 = "charge_1";
    private static final String CHARGE_2 = "charge_2";
    private static final String CHARGE_3 = "charge_3";
    private static final String ADHOC_CHARGE_1 = "adhoc_charge_1";
    private static final String ADHOC_CHARGE_2 = "adhoc_charge_2";
    private static final String ADHOC_CHARGE_3 = "adhoc_charge_3";
    private static final String TAX = "tax";
    private static final String DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
    private static final String TIME_FORMAT = "HH:mm";
    public Calendar mCalendar = Calendar.getInstance();
    Spinner mDateField;
    Spinner mTimeField;
    Spinner mClient;
    Spinner mCharge1;
    Spinner mCharge2;
    Spinner mCharge3;
    EditText mFrom;
    EditText mTo;
    EditText mMilesTravelled;
    EditTextCurrency mMilesCost;
    CheckBox mIsComplete;
    EditText mNotes;
    Double mTaxRate;
    HashMap<Long, Double> mStandardChargeCosts;
    EditTextCurrency mTripChargeAmount1;
    EditTextCurrency mTripChargeAmount2;
    EditTextCurrency mTripChargeAmount3;
    EditTextAdHocCharge mTripCustomerCharge1;
    EditTextAdHocCharge mTripCustomerCharge2;
    EditTextAdHocCharge mTripCustomerCharge3;
    EditTextCurrency mTripCustomerChargeAmount1;
    EditTextCurrency mTripCustomerChargeAmount2;
    EditTextCurrency mTripCustomerChargeAmount3;
    EditTextCurrency mTotalCostAmount;
    EditTextCurrency mTaxAmount;
    Button mTaxButton;
    Boolean okToSelect1 = false;
    Boolean okToSelect2 = false;
    Boolean okToSelect3 = false;
    boolean isTripClientLoaderComplete = false;
    boolean isTripChargeLoaderComplete = false;
    boolean isTripLoaderComplete = false;
    boolean isTripChargesComplete = false;
    Trip mTrip = new Trip();
    long mCharge1Id;
    long mCharge2Id;
    long mCharge3Id;
    long mClientId;
    Button mCalcTotal;
    Map<Long, ArrayList<Long>> mClientStandardCharges = new HashMap<>();
    boolean bTripChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip, container, false);

        elementConfig(rootView);


        Long id = getArguments() != null ? getArguments().getLong(MainActivity.KEY_ID) : 0;

        //getActivity().getSupportLoaderManager().destroyLoader(TRIP_CHARGES_LOADER);
        //getActivity().getSupportLoaderManager().destroyLoader(TRIP_LOADER);

        mId = id == 0 ? null : id;

        if (savedInstanceState != null) {
            mCalendar = (Calendar) savedInstanceState.getSerializable(DATE);
            mStandardChargeCosts = (HashMap<Long, Double>) savedInstanceState.getSerializable(CHARGE_DATA);
            mTaxRate = savedInstanceState.getDouble(TAX_RATE);
            mCharge1Id = savedInstanceState.getLong(CHARGE_1_ID);
            mCharge2Id = savedInstanceState.getLong(CHARGE_2_ID);
            mCharge3Id = savedInstanceState.getLong(CHARGE_3_ID);
            mClientId = savedInstanceState.getLong(CLIENT_ID);

            mMilesCost.setValue(savedInstanceState.getDouble(MILES_COST));
            mTripChargeAmount1.setValue(savedInstanceState.getDouble(CHARGE_1));
            mTripChargeAmount2.setValue(savedInstanceState.getDouble(CHARGE_2));
            mTripChargeAmount3.setValue(savedInstanceState.getDouble(CHARGE_3));
            mTripCustomerChargeAmount1.setValue(savedInstanceState.getDouble(ADHOC_CHARGE_1));
            mTripCustomerChargeAmount2.setValue(savedInstanceState.getDouble(ADHOC_CHARGE_2));
            mTripCustomerChargeAmount3.setValue(savedInstanceState.getDouble(ADHOC_CHARGE_3));
            mTaxAmount.setValue(savedInstanceState.getDouble(TAX));
        } else {
            mCalendar.setTime(new Date());
        }

        UpdateDate(mCalendar);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        getActivity().getSupportLoaderManager().initLoader(TRIP_CHARGE_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(TRIP_CLIENT_LOADER, null, this);

        if (savedInstanceState == null) {
            if (mId != null) {
                getActivity().getSupportLoaderManager().destroyLoader(TRIP_CHARGES_LOADER);
                getActivity().getSupportLoaderManager().destroyLoader(TRIP_LOADER);
                getActivity().getSupportLoaderManager().initLoader(TRIP_CHARGES_LOADER, null, this);
                getActivity().getSupportLoaderManager().initLoader(TRIP_LOADER, null, this);
            }
        }
        super.onActivityCreated(savedInstanceState);
    }


    public void UpdateDate(Calendar calendar) {
        mCalendar = calendar;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        ArrayList<String> date_for_spinner = new ArrayList<>();
        date_for_spinner.add(dateFormat.format(mCalendar.getTime()));

        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        ArrayList<String> time_for_spinner = new ArrayList<>();
        time_for_spinner.add(timeFormat.format(mCalendar.getTime()));

        ArrayAdapter<String> date_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, date_for_spinner);
        mDateField.setAdapter(date_adapter);

        ArrayAdapter<String> time_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, time_for_spinner);
        mTimeField.setAdapter(time_adapter);

    }

    @Override
    public void onDestroyView() {
        save();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATE, mCalendar);
        outState.putSerializable(CHARGE_DATA, mStandardChargeCosts);
        outState.putDouble(TAX_RATE, mTaxRate);
        outState.putLong(CHARGE_1_ID, mCharge1Id);
        outState.putLong(CHARGE_2_ID, mCharge2Id);
        outState.putLong(CHARGE_3_ID, mCharge3Id);
        outState.putLong(CLIENT_ID, mClientId);
        outState.putDouble(MILES_COST, mMilesCost.getValue());
        outState.putDouble(CHARGE_1, mTripChargeAmount1.getValue());
        outState.putDouble(CHARGE_2, mTripChargeAmount2.getValue());
        outState.putDouble(CHARGE_3, mTripChargeAmount3.getValue());
        outState.putDouble(ADHOC_CHARGE_1, mTripCustomerChargeAmount1.getValue());
        outState.putDouble(ADHOC_CHARGE_2, mTripCustomerChargeAmount2.getValue());
        outState.putDouble(ADHOC_CHARGE_3, mTripCustomerChargeAmount3.getValue());
        outState.putDouble(TAX, mTaxAmount.getValue());
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }


    public Calendar getCurrentlySelectedDate() {
        return mCalendar;
    }

    public void save() {
        if (bTripChanged) {
            getActivity().getSupportLoaderManager().destroyLoader(TRIP_CHARGES_LOADER);
            getActivity().getSupportLoaderManager().destroyLoader(TRIP_LOADER);
            isTripClientLoaderComplete = false;
            isTripLoaderComplete = false;
            isTripChargeLoaderComplete = false;
            isTripChargesComplete = false;
            // Insert on back press{
            mTripCustomerChargeAmount1.formatAmount();
            mTripCustomerChargeAmount2.formatAmount();
            mTripCustomerChargeAmount3.formatAmount();
            mTripChargeAmount1.formatAmount();
            mTripChargeAmount2.formatAmount();
            mTripChargeAmount3.formatAmount();
            mTaxAmount.formatAmount();
            mMilesCost.formatAmount();
            calcTotals();

            if (mMilesTravelled.getText().length() == 0) {
                mMilesTravelled.setText("0");
            }

            if (!mFrom.getText().toString().equals("") && !mTo.getText().toString().equals("")) {

                SimpleDateFormat fmt = new SimpleDateFormat(DATE_TIME_FORMAT);
                String trip_date_time = fmt.format(mCalendar.getTime());

                String sJourneyDateTime = String.format(
                        "%tb %1$te, %1$tl:%1$tM %1$Tp", mCalendar);

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String distance_units = settings.getString(getString(R.string.pref_distance_unit_key), getString(R.string.pref_distance_unit_default));

                String sDescription = sJourneyDateTime + " - " + ((TextView) mClient.getSelectedView()).getText().toString()
                        + "\n" + mFrom.getText().toString() + " to " + mTo.getText().toString()
                        + "\n" + mMilesTravelled.getText().toString() + " "
                        + (distance_units.toLowerCase().equals(getString(R.string.pref_distance_unit_default)) ? getString(R.string.miles) : getString(R.string.kms)) +
                        " - " + mTotalCostAmount.getText().toString();

                ContentValues cvTrip = new ContentValues();
                cvTrip.put(TripContract.TripEntry.COLUMN_STARTING_PLACE, mFrom.getText().toString());
                cvTrip.put(TripContract.TripEntry.COLUMN_ENDING_PLACE, mTo.getText().toString());
                cvTrip.put(TripContract.TripEntry.COLUMN_DATE_TIME, trip_date_time);
                cvTrip.put(TripContract.TripEntry.COLUMN_CLIENT_ID, mClient.getSelectedItemId());
                cvTrip.put(TripContract.TripEntry.COLUMN_DISTANCE, mMilesTravelled.getText().toString());
                cvTrip.put(TripContract.TripEntry.COLUMN_COST, mMilesCost.getValue());
                cvTrip.put(TripContract.TripEntry.COLUMN_COMPLETE, mIsComplete.isChecked());
                cvTrip.put(TripContract.TripEntry.COLUMN_NOTES, mNotes.getText().toString());
                cvTrip.put(TripContract.TripEntry.COLUMN_DESCRIPTION, sDescription);

                if (mId == null) {
                    Uri uri = getActivity().getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, cvTrip);
                    String trip_id = TripContract.TripEntry.getIDSettingFromUri(uri);
                    if (!trip_id.equals(-1)) {
                        if (saveCharges(trip_id)) {
                            Toast.makeText(getActivity(), getString(R.string.trip_saved), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int rows_updated = getActivity().getContentResolver().update(TripContract.TripEntry.CONTENT_URI, cvTrip, TripContract.TripEntry._ID + " = ?", new String[]{String.valueOf(mId)});
                    if (rows_updated > 0) {
                        // Now save the charges
                        //
                        // 1 - Delete all existing charges
                        int rows_deleted = getActivity().getContentResolver().delete(TripContract.TripChargeEntry.CONTENT_URI, TripContract.TripChargeEntry.COLUMN_TRIP_ID + " = ?", new String[]{String.valueOf(mId)});
                        // 2 - Save the charges
                        if (saveCharges(String.valueOf(mId))) {
                            Toast.makeText(getActivity(), getString(R.string.trip_saved), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(), getString(R.string.trip_saved), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.trip_not_saved), Toast.LENGTH_SHORT).show();
            }
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            getContext().sendBroadcast(dataUpdatedIntent);
        }
    }

    @Override
    public void backPressed() {

    }

    @Override
    public void deleteItem() {
        if (mId != null) {
            getActivity().getSupportLoaderManager().destroyLoader(TRIP_CHARGES_LOADER);
            getActivity().getSupportLoaderManager().destroyLoader(TRIP_LOADER);
            int rows_deleted = getActivity().getContentResolver().delete(TripContract.TripEntry.CONTENT_URI, TripContract.TripEntry._ID + " = ?", new String[]{String.valueOf(mId)});
            if (rows_deleted > 0) {
                Toast.makeText(getActivity(), getString(R.string.trip_deleted), Toast.LENGTH_SHORT).show();
                if (!((TripSearchFragment.Callback) getActivity()).isTwoPane()) {
                    getActivity().finish();
                } else {
                    FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                    trans.remove(this).commitAllowingStateLoss();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.select_a_trip_to_delete), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveItem() {
        save();
    }

    @Override
    public Long getItemId() {
        return null;
    }

    private boolean saveCharges(String trip_id) {

        boolean ret = true;
        String trip_charge_id;

        // Now save the charges
        if (mCharge1.getSelectedItemPosition() > 0) {
            trip_charge_id = saveCharge(trip_id, mCharge1, mTripChargeAmount1);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        if (ret && mCharge2.getSelectedItemPosition() > 0) {
            trip_charge_id = saveCharge(trip_id, mCharge2, mTripChargeAmount2);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        if (ret && mCharge3.getSelectedItemPosition() > 0) {
            trip_charge_id = saveCharge(trip_id, mCharge3, mTripChargeAmount3);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        if (ret && !mTripCustomerCharge1.getText().toString().equals("")) {
            trip_charge_id = saveCharge(trip_id, mTripCustomerCharge1, mTripCustomerChargeAmount1);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        if (ret && !mTripCustomerCharge2.getText().toString().equals("")) {
            trip_charge_id = saveCharge(trip_id, mTripCustomerCharge2, mTripCustomerChargeAmount2);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        if (ret && !mTripCustomerCharge3.getText().toString().equals("")) {
            trip_charge_id = saveCharge(trip_id, mTripCustomerCharge3, mTripCustomerChargeAmount3);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        if (ret && !mTaxAmount.getText().toString().equals("")) {
            trip_charge_id = saveTax(trip_id, mTaxAmount);
            ret = trip_charge_id.equals("-1") ? false : true;
        }
        return ret;

    }

    private String saveCharge(String trip_id, Spinner charge, EditTextCurrency cost) {

        // Now save the charges
        ContentValues cvTripCharges = new ContentValues();
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_TRIP_ID, Long.valueOf(trip_id));
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_DESCRIPTION, charge.getSelectedItem().toString());
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_STANDARD_CHARGE_ID, charge.getSelectedItemId());
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_COST, cost.getValue());
        Uri uri = getActivity().getContentResolver().insert(TripContract.TripChargeEntry.CONTENT_URI, cvTripCharges);
        return TripContract.TripChargeEntry.getIDSettingFromUri(uri);
    }

    private String saveCharge(String trip_id, EditText charge, EditTextCurrency cost) {

        // Now save the charges
        ContentValues cvTripCharges = new ContentValues();
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_TRIP_ID, Long.valueOf(trip_id));
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_DESCRIPTION, charge.getText().toString());
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_COST, cost.getValue());
        Uri uri = getActivity().getContentResolver().insert(TripContract.TripChargeEntry.CONTENT_URI, cvTripCharges);
        return TripContract.TripChargeEntry.getIDSettingFromUri(uri);
    }

    private String saveTax(String trip_id, EditTextCurrency cost) {

        // Now save the charges
        ContentValues cvTripCharges = new ContentValues();
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_TRIP_ID, Long.valueOf(trip_id));
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_DESCRIPTION, getResources().getString(R.string.tax));
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_COST, cost.getValue());
        cvTripCharges.put(TripContract.TripChargeEntry.COLUMN_IS_TAX, true);
        Uri uri = getActivity().getContentResolver().insert(TripContract.TripChargeEntry.CONTENT_URI, cvTripCharges);
        return TripContract.TripChargeEntry.getIDSettingFromUri(uri);
    }

    private double calcCost(Double miles) {

        double tripCost = 0;
        if ((miles <= 0) || (miles == null)) {
            miles = 0.0;
        }
        Cursor cursor = getActivity().getContentResolver().query(TripContract.ClientEntry.buildClientById(mClient.getSelectedItemId()),
                new String[]{TripContract.ClientEntry.COLUMN_PRICE_PER_MILE},null,null,null,null);
        if (cursor.moveToFirst()) {
            double price = cursor.getDouble(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE));
            tripCost = (price * miles);
        }
        cursor.close();
        return tripCost;
    }

    private double getTaxRate() {

        double tax_rate = 0;
        Cursor cursor = getActivity().getContentResolver().query(TripContract.ClientEntry.buildClientById(mClient.getSelectedItemId()),
                new String[]{TripContract.ClientEntry.COLUMN_TAX_RATE},null,null,null,null);
        if (cursor.moveToFirst()) {
            tax_rate = cursor.getDouble(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_TAX_RATE));
        }
        cursor.close();
        return tax_rate;
    }

    private double calcChargesWithoutTax() {
        double total_charges = 0;
        total_charges += mMilesCost.getValue();
        total_charges += mTripChargeAmount1.getValue();
        total_charges += mTripChargeAmount2.getValue();
        total_charges += mTripChargeAmount3.getValue();
        total_charges += mTripCustomerChargeAmount1.getValue();
        total_charges += mTripCustomerChargeAmount2.getValue();
        total_charges += mTripCustomerChargeAmount3.getValue();
        return total_charges;
    }

    private void calcTotals() {

        double total_charges = calcChargesWithoutTax();
        total_charges += mTaxAmount.getValue();
        mTotalCostAmount.setText(String.valueOf(total_charges));
        mTotalCostAmount.formatAmount();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Loader<Cursor> ret = null;

        if (id == TRIP_CLIENT_LOADER) {
            Uri uri = TripContract.ClientEntry.CONTENT_URI;
            ret = new CursorLoader(getActivity(),
                    uri,
                    new String[]{TripContract.ClientEntry._ID,
                            TripContract.ClientEntry.COLUMN_NAME,
                            TripContract.ClientEntry.COLUMN_PRICE_PER_MILE,
                            TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID,
                            TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID,
                            TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID}, null, null, null);
        }

        if (id == TRIP_CHARGE_LOADER) {
            Uri uri = TripContract.StandardChargeEntry.CONTENT_URI;
            ret = new CursorLoader(getActivity(),
                    uri,
                    new String[]{TripContract.StandardChargeEntry._ID,
                            TripContract.StandardChargeEntry.COLUMN_NAME,
                            TripContract.StandardChargeEntry.COLUMN_COST}, null, null, null);
        }

        if (id == TRIP_LOADER) {
            Uri uri = TripContract.TripEntry.buildTripById(mId);
            ret = new CursorLoader(getActivity(),
                    uri,
                    tripColumns, null, null, null);
        }

        if (id == TRIP_CHARGES_LOADER) {
            Uri uri = TripContract.TripChargeEntry.buildTripChargeById(mId);
            ret = new CursorLoader(getActivity(),
                    uri,
                    chargeColumns, null, null, null);
        }

        return ret;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        String[] fromColumns = new String[]{TripContract.ClientEntry.COLUMN_NAME};
        int[] toViews = new int[]{android.R.id.text1};

        if (loader.getId() == TRIP_CLIENT_LOADER) {
            if (data.getCount() > 0) {
                SimpleCursorAdapter clientCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data, fromColumns, toViews, 0);
                clientCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mClient.setAdapter(clientCursorAdapter);
                if (data.moveToFirst()) {
                    do {
                        ArrayList<Long> standard_charges_for_client = new ArrayList<>();
                        long id = data.getLong(data.getColumnIndex(TripContract.ClientEntry._ID));
                        long standard_charge_1 = data.getInt(data.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID));
                        long standard_charge_2 = data.getInt(data.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID));
                        long standard_charge_3 = data.getInt(data.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID));
                        standard_charges_for_client.add(standard_charge_1);
                        standard_charges_for_client.add(standard_charge_2);
                        standard_charges_for_client.add(standard_charge_3);
                        mClientStandardCharges.put(id, standard_charges_for_client);
                    } while (data.moveToNext());
                }
            }
            isTripClientLoaderComplete = true;
            mClient.setSelection(Util.setSpinnerSelection(mClient, mClientId));
            populateTrip();
        }

        if (loader.getId() == TRIP_CHARGE_LOADER) {
            if (data.getCount() > 0) {
                SimpleCursorAdapter standardChargeCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data, fromColumns, toViews, 0);
                standardChargeCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Load the standard charge ID's into a hash map for future use
                mStandardChargeCosts = new HashMap<>();
                if (data.moveToFirst()) {
                    do {
                        mStandardChargeCosts.put(data.getLong(data.getColumnIndex(TripContract.StandardChargeEntry._ID)), data.getDouble(data.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_COST)));
                    } while (data.moveToNext());
                }
                mCharge1.setAdapter(standardChargeCursorAdapter);
                mCharge2.setAdapter(standardChargeCursorAdapter);
                mCharge3.setAdapter(standardChargeCursorAdapter);

                mCharge1.setSelection(Util.setSpinnerSelection(mCharge1, mCharge1Id));
                mCharge2.setSelection(Util.setSpinnerSelection(mCharge2, mCharge2Id));
                mCharge3.setSelection(Util.setSpinnerSelection(mCharge3, mCharge3Id));


            }
            isTripChargeLoaderComplete = true;
            populateTrip();
        }

        if (loader.getId() == TRIP_LOADER) {
            if (data.getCount() > 0) {
                data.moveToFirst();

                mTrip.setDate(data.getString(data.getColumnIndex(TripContract.TripEntry.COLUMN_DATE_TIME)));
                mTrip.setClient(data.getInt(data.getColumnIndex(TripContract.TripEntry.COLUMN_CLIENT_ID)));
                mTrip.setFrom(data.getString(data.getColumnIndex(TripContract.TripEntry.COLUMN_STARTING_PLACE)));
                mTrip.setTo(data.getString(data.getColumnIndex(TripContract.TripEntry.COLUMN_ENDING_PLACE)));
                mTrip.setDistance(data.getString(data.getColumnIndex(TripContract.TripEntry.COLUMN_DISTANCE)));
                mTrip.setDistanceCost(data.getDouble(data.getColumnIndex(TripContract.TripEntry.COLUMN_COST)));
                mTrip.setComplete(data.getInt(data.getColumnIndex(TripContract.TripEntry.COLUMN_COMPLETE)));
                mTrip.setNotes(data.getString(data.getColumnIndex(TripContract.TripEntry.COLUMN_NOTES)));
            }
            isTripLoaderComplete = true;
            populateTrip();
        }

        if (loader.getId() == TRIP_CHARGES_LOADER) {
            if (data.getCount() > 0) {
                data.moveToFirst();
                int standard_charge_count = 0;
                int customer_charge_count = 0;
                do {
                    long spinner_id = data.getInt(data.getColumnIndex(TripContract.TripChargeEntry.COLUMN_STANDARD_CHARGE_ID));
                    double charge_cost = data.getDouble(data.getColumnIndex(TripContract.TripChargeEntry.COLUMN_COST));
                    String description = data.getString(data.getColumnIndex(TripContract.TripChargeEntry.COLUMN_DESCRIPTION));
                    boolean is_tax = data.getInt(data.getColumnIndex(TripContract.TripChargeEntry.COLUMN_IS_TAX)) == 1 ? true : false;
                    if (is_tax) {
                        mTrip.setTaxCost(charge_cost);
                    } else if (spinner_id > 0) {
                        switch (standard_charge_count) {
                            case 0:
                                mTrip.setCharge1(spinner_id);
                                mTrip.setChargeCost1(charge_cost);
                                break;
                            case 1:
                                mTrip.setCharge2(spinner_id);
                                mTrip.setChargeCost2(charge_cost);
                                break;
                            case 2:
                                mTrip.setCharge3(spinner_id);
                                mTrip.setChargeCost3(charge_cost);
                                break;
                        }
                        standard_charge_count++;
                    } else {
                        switch (customer_charge_count) {
                            case 0:
                                mTrip.setAdHocCharge1(description);
                                mTrip.setAdHocChargeCost1(charge_cost);
                                break;
                            case 1:
                                mTrip.setAdHocCharge2(description);
                                mTrip.setAdHocChargeCost2(charge_cost);
                                break;
                            case 2:
                                mTrip.setAdHocCharge3(description);
                                mTrip.setAdHocChargeCost3(charge_cost);
                                break;
                        }
                        customer_charge_count++;
                    }
                } while (data.moveToNext());
            }
            isTripChargesComplete = true;
            populateTrip();
        }
    }

    private void populateTrip() {

        if (isTripChargesComplete && isTripChargeLoaderComplete && isTripLoaderComplete && isTripClientLoaderComplete) {

            Date date = new Date();
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            try {
                date = dateTimeFormat.parse(mTrip.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mCalendar.setTime(date);
            UpdateDate(mCalendar);

            mClient.setSelection(Util.setSpinnerSelection(mClient, mTrip.getClient()));
            mFrom.setText(mTrip.getFrom());
            mTo.setText(mTrip.getTo());
            mMilesTravelled.setText(mTrip.getDistance());
            mMilesCost.setValue(mTrip.getDistanceCost());
            mIsComplete.setChecked(mTrip.getComplete() == 1);
            mCharge1.setSelection(Util.setSpinnerSelection(mCharge1, mTrip.getCharge1()));
            mCharge2.setSelection(Util.setSpinnerSelection(mCharge2, mTrip.getCharge2()));
            mCharge3.setSelection(Util.setSpinnerSelection(mCharge3, mTrip.getCharge3()));
            mTripChargeAmount1.setValue(mTrip.getChargeCost1());
            mTripChargeAmount2.setValue(mTrip.getChargeCost2());
            mTripChargeAmount3.setValue(mTrip.getChargeCost3());
            mTripCustomerCharge1.setText(mTrip.getAdHocCharge1());
            mTripCustomerCharge2.setText(mTrip.getAdHocCharge2());
            mTripCustomerCharge3.setText(mTrip.getAdHocCharge3());
            mTripCustomerChargeAmount1.setValue(mTrip.getAdHocChargeCost1());
            if (mTripCustomerCharge1.getText().length() > 0) {
                mTripCustomerChargeAmount1.setEnabled(true);
            }
            if (mTripCustomerCharge2.getText().length() > 0) {
                mTripCustomerChargeAmount2.setEnabled(true);
            }
            if (mTripCustomerCharge3.getText().length() > 0) {
                mTripCustomerChargeAmount3.setEnabled(true);
            }
            mTripCustomerChargeAmount2.setValue(mTrip.getAdHocChargeCost2());
            mTripCustomerChargeAmount3.setValue(mTrip.getAdHocChargeCost3());
            mTaxAmount.setValue(mTrip.getTaxCost());
            mNotes.setText(mTrip.getNotes());

            calcTotals();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (loader.getId() == TRIP_CLIENT_LOADER) {
            mClient.setAdapter(null);
        }

        if (loader.getId() == TRIP_CHARGE_LOADER) {
            mCharge1.setAdapter(null);
            mCharge2.setAdapter(null);
            mCharge3.setAdapter(null);
        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        DatePickerFragment.OnDateSelected mCallback;
        Calendar mCal;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            mCal = Calendar.getInstance();
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog tpd = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, hour, minute, false);
            tpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return tpd;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCal.set(mCal.get(Calendar.YEAR),mCal.get(Calendar.MONTH),mCal.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
            mCallback.onDateSelected(mCal, this.getTag());
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (DatePickerFragment.OnDateSelected) activity;
                mCal = mCallback.getCurrentlySelectedDate(this.getTag());
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnDateSelected");
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        OnDateSelected mCallback;
        Calendar mCal;

        public interface OnDateSelected {
            public void onDateSelected(Calendar calendar, String tag);
            public Calendar getCurrentlySelectedDate(String tag);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            mCal = Calendar.getInstance();
            int year = mCal.get(Calendar.YEAR);
            int month = mCal.get(Calendar.MONTH);
            int day = mCal.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);
            dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCal.set(year,monthOfYear,dayOfMonth);
            mCallback.onDateSelected(mCal, this.getTag());
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (OnDateSelected) activity;
                mCal = mCallback.getCurrentlySelectedDate(this.getTag());
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnDateSelected");
            }
        }
    }

    private void setClientStandardCharges(long id) {

        ArrayList<Long> standard_charges = mClientStandardCharges.get(id);
        int index = 0;
        for (Long item : standard_charges) {
            switch (index) {
                case 0 : mCharge1.setSelection(Util.setSpinnerSelection(mCharge1, item)); break;
                case 1 : mCharge2.setSelection(Util.setSpinnerSelection(mCharge2, item)); break;
                case 2 : mCharge3.setSelection(Util.setSpinnerSelection(mCharge3, item)); break;
            }
            index++;
        }

    }

    private void elementConfig(View rootView) {
        // 1 -Date spinner config
        mDateField = (Spinner) rootView.findViewById(R.id.spTripDate);
        mDateField.setClickable(false);
        // Load the spinner with a the date string
        mDateField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickerDialog();
                }
                bTripChanged = true;
                return true;
            }
        });

        // 2 - Time spinner config
        mTimeField = (Spinner) rootView.findViewById(R.id.spTripTime);
        mTimeField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showTimePickerDialog();
                }
                bTripChanged = true;
                return true;
            }
        });

        // 3 - Client spinner config
        mClient = (Spinner) rootView.findViewById(R.id.spClient);
        mClient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTaxRate = getTaxRate();
                mTaxButton.setText(String.format("%s @ %.2f%s", getResources().getString(R.string.calc_tax), mTaxRate, "%"));
                if (mId == null && (mClientId == 0 || mClientId != id)) {
                    okToSelect1 = true;
                    okToSelect2 = true;
                    okToSelect3 = true;
                    setClientStandardCharges(id);
                }
                mClientId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mClient.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });

        // 4 - Standard charges 1 config
        mCharge1 = (Spinner) rootView.findViewById(R.id.spTripCharge1);
        mCharge1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    okToSelect1 = true;
                }
                return false;
            }
        });
        mCharge1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If the mId != null, ignore the first time this fires to allow any standard charges to load
                if (okToSelect1) {
                    mTripChargeAmount1.setValue(mStandardChargeCosts.get(mCharge1.getSelectedItemId()));
                    calcTotals();
                    bTripChanged = true;
                }

                mCharge1Id = id;

                okToSelect1 = false;

                if (mCharge1.getSelectedItemPosition() == 0) {
                    mTripChargeAmount1.setEnabled(false);
                } else {
                    mTripChargeAmount1.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 5 - Standard charges 2 config
        mCharge2 = (Spinner) rootView.findViewById(R.id.spTripCharge2);
        mCharge2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    okToSelect2 = true;
                }
                return false;
            }
        });
        mCharge2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (okToSelect2) {
                    mTripChargeAmount2.setValue(mStandardChargeCosts.get(mCharge2.getSelectedItemId()));
                    calcTotals();
                    bTripChanged = true;
                }

                mCharge2Id = id;

                okToSelect2 = false;

                if (mCharge2.getSelectedItemPosition() == 0) {
                    mTripChargeAmount2.setEnabled(false);
                } else {
                    mTripChargeAmount2.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 6 - Standard charges 3 config
        mCharge3 = (Spinner) rootView.findViewById(R.id.spTripCharge3);
        mCharge3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    okToSelect3 = true;
                }
                return false;
            }
        });
        mCharge3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (okToSelect3) {
                    mTripChargeAmount3.setValue(mStandardChargeCosts.get(mCharge3.getSelectedItemId()));
                    calcTotals();
                    bTripChanged = true;
                }
                okToSelect3 = false;

                mCharge3Id = id;

                if (mCharge3.getSelectedItemPosition() == 0) {
                    mTripChargeAmount3.setEnabled(false);
                } else {
                    mTripChargeAmount3.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 7 - From
        mFrom = (EditText) rootView.findViewById(R.id.etFrom);
        mFrom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });

        // 8 - To
        mTo = (EditText) rootView.findViewById(R.id.etTo);
        mTo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });

        // 9 - Miles travelled config
        mMilesTravelled = (EditText) rootView.findViewById(R.id.etMilesTravelled);
        mMilesTravelled.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mMilesTravelled.getText().length() == 0) {
                        mMilesTravelled.setText("0");
                    }
                    Double miles = Double.parseDouble(mMilesTravelled.getText().toString());
                    mMilesCost.setValue(calcCost(miles));
                    calcTotals();
                }
            }
        });
        mMilesTravelled.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });

        // 10 - Miles cost config
        mMilesCost = (EditTextCurrency) rootView.findViewById(R.id.etMileageCost);
        mMilesCost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });

        // 11 - Is Complete
        mIsComplete = (CheckBox) rootView.findViewById(R.id.cbTripComplete);
        mIsComplete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });


        // 12 - Notes
        mNotes = (EditText) rootView.findViewById(R.id.etNotes);
        mNotes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });


        // 13 - Standard charge amount 1
        mTripChargeAmount1 = (EditTextCurrency) rootView.findViewById(R.id.edTripChargeAmount1);
        mTripChargeAmount1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
                bTripChanged = true;
            }
        });

        // 14 - Standard charge amount 2
        mTripChargeAmount2 = (EditTextCurrency) rootView.findViewById(R.id.edTripChargeAmount2);
        mTripChargeAmount2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
                bTripChanged = true;
            }
        });

        // 15 - Standard charge amount 3
        mTripChargeAmount3 = (EditTextCurrency) rootView.findViewById(R.id.edTripChargeAmount3);
        mTripChargeAmount3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
                bTripChanged = true;
            }
        });

        // 16 - adhoc charge description 1
        mTripCustomerCharge1 = (EditTextAdHocCharge) rootView.findViewById(R.id.etCustomerCharge1);
        mTripCustomerCharge1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });

        // 17 - adhoc charge description 2
        mTripCustomerCharge2 = (EditTextAdHocCharge) rootView.findViewById(R.id.etCustomerCharge2);
        mTripCustomerCharge2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });


        // 18 - adhoc charge description 3
        mTripCustomerCharge3 = (EditTextAdHocCharge) rootView.findViewById(R.id.etCustomerCharge3);
        mTripCustomerCharge3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });


        // 19 - adhoc charge amount 1
        mTripCustomerChargeAmount1 = (EditTextCurrency) rootView.findViewById(R.id.etCustomerChargeAmount1);
        mTripCustomerChargeAmount1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
                bTripChanged = true;
            }
        });
        mTripCustomerChargeAmount1.setEnabled(false);
        mTripCustomerCharge1.setCurrencyField(mTripCustomerChargeAmount1);

        // 20 - adhoc charge amount 2
        mTripCustomerChargeAmount2 = (EditTextCurrency) rootView.findViewById(R.id.etCustomerChargeAmount2);
        mTripCustomerChargeAmount2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
                bTripChanged = true;
            }
        });
        mTripCustomerChargeAmount2.setEnabled(false);
        mTripCustomerCharge2.setCurrencyField(mTripCustomerChargeAmount2);

        // 21 - adhoc charge amount 3
        mTripCustomerChargeAmount3 = (EditTextCurrency) rootView.findViewById(R.id.etCustomerChargeAmount3);
        mTripCustomerChargeAmount3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
                bTripChanged = true;
            }
        });
        mTripCustomerCharge3.setCurrencyField(mTripCustomerChargeAmount3);
        mTripCustomerChargeAmount3.setEnabled(false);

        // 22 - tax button config
        mTaxButton = (Button) rootView.findViewById(R.id.btCalcTax);
        mTaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double tax_amount = (mTaxRate / 100) * calcChargesWithoutTax();
                DecimalFormat df = new DecimalFormat("#########.##");
                tax_amount = Double.valueOf(df.format(tax_amount));
                mTaxAmount.setText(String.valueOf(tax_amount));
                mTaxAmount.formatAmount();
                calcTotals();
                bTripChanged = true;
            }
        });

        // 23 - total cost amount
        mTotalCostAmount = (EditTextCurrency) rootView.findViewById(R.id.etTotalCostAmount);

        // 24 - tax amount config
        mTaxAmount = (EditTextCurrency) rootView.findViewById(R.id.etTaxAmount);
        mTaxAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bTripChanged = true;
                return false;
            }
        });


        mCalcTotal = (Button) rootView.findViewById(R.id.btnTotalCostLabel);
        mCalcTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTotalCostAmount.requestFocus();
                calcTotals();
                bTripChanged = true;
            }
        });
    }
}
