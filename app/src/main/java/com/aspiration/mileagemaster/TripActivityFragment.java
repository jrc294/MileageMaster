package com.aspiration.mileagemaster;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class TripActivityFragment extends Fragment implements BackFragment{

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
    private static final String DATE = "date";
    private static final String CHARGE_DATA = "charge_data";
    private static final String TIME = "time";
    private static final String DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
    private static final String TIME_FORMAT = "HH:mm";
    public Calendar mCalendar = Calendar.getInstance();
    private static final int DATE_PICKER_DIALOG = 0;
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
    HashMap<Long, Double> mStandardChargeCosts;
    EditTextCurrency mTripChargeAmount1;
    EditTextCurrency mTripChargeAmount2;
    EditTextCurrency mTripChargeAmount3;
    EditText mTripCustomerCharge1;
    EditText mTripCustomerCharge2;
    EditText mTripCustomerCharge3;
    EditTextCurrency mTripCustomerChargeAmount1;
    EditTextCurrency mTripCustomerChargeAmount2;
    EditTextCurrency mTripCustomerChargeAmount3;
    EditTextCurrency mTotalCostAmount;
    EditTextCurrency mTaxAmount;
    Button mTaxButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip, container, false);

        mDateField = (Spinner) rootView.findViewById(R.id.spTripDate);
        mTimeField = (Spinner) rootView.findViewById(R.id.spTripTime);
        mClient = (Spinner) rootView.findViewById(R.id.spClient);
        mCharge1 = (Spinner) rootView.findViewById(R.id.spTripCharge1);
        mCharge2 = (Spinner) rootView.findViewById(R.id.spTripCharge2);
        mCharge3 = (Spinner) rootView.findViewById(R.id.spTripCharge3);
        mFrom = (EditText) rootView.findViewById(R.id.etFrom);
        mTo = (EditText) rootView.findViewById(R.id.etTo);
        mMilesTravelled = (EditText) rootView.findViewById(R.id.etMilesTravelled);
        mMilesCost = (EditTextCurrency) rootView.findViewById(R.id.etMileageCost);
        mIsComplete = (CheckBox) rootView.findViewById(R.id.cbTripComplete);
        mNotes = (EditText) rootView.findViewById(R.id.etNotes);
        mTripChargeAmount1 = (EditTextCurrency) rootView.findViewById(R.id.edTripChargeAmount1);
        mTripChargeAmount2 = (EditTextCurrency) rootView.findViewById(R.id.edTripChargeAmount2);
        mTripChargeAmount3 = (EditTextCurrency) rootView.findViewById(R.id.edTripChargeAmount3);
        mTripCustomerCharge1 = (EditText) rootView.findViewById(R.id.etCustomerCharge1);
        mTripCustomerCharge2 = (EditText) rootView.findViewById(R.id.etCustomerCharge2);
        mTripCustomerCharge3 = (EditText) rootView.findViewById(R.id.etCustomerCharge3);
        mTripCustomerChargeAmount1 = (EditTextCurrency) rootView.findViewById(R.id.etCustomerChargeAmount1);
        mTripCustomerChargeAmount2 = (EditTextCurrency) rootView.findViewById(R.id.etCustomerChargeAmount2);
        mTripCustomerChargeAmount3 = (EditTextCurrency) rootView.findViewById(R.id.etCustomerChargeAmount3);
        mTotalCostAmount = (EditTextCurrency) rootView.findViewById(R.id.etTotalCostAmount);
        mTaxAmount = (EditTextCurrency) rootView.findViewById(R.id.etTaxAmount);
        mTaxButton = (Button) rootView.findViewById(R.id.btCalcTax);
        mNotes = (EditText) rootView.findViewById(R.id.etNotes);

        mTripCustomerChargeAmount1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
            }
        });

        mTripCustomerChargeAmount2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
            }
        });

        mTripCustomerChargeAmount3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcTotals();
                }
            }
        });

        mTaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaxAmount.setText(String.valueOf(calcTax()));
                mTaxAmount.formatAmount();
                calcTotals();
            }
        });

        String[] fromColumns = new String[]{TripContract.ClientEntry.COLUMN_NAME};
        int[] toViews = new int[]{android.R.id.text1};

        Cursor clientCursor = getActivity().getContentResolver().query(TripContract.ClientEntry.CONTENT_URI,
                new String[]{TripContract.ClientEntry._ID, TripContract.ClientEntry.COLUMN_NAME},null,null,null,null);

        SimpleCursorAdapter clientCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, clientCursor, fromColumns, toViews, 0);
        clientCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClient.setAdapter(clientCursorAdapter);

        Cursor standardChargeCursor = getActivity().getContentResolver().query(TripContract.StandardChargeEntry.CONTENT_URI,
                new String[]{TripContract.StandardChargeEntry._ID, TripContract.StandardChargeEntry.COLUMN_NAME, TripContract.StandardChargeEntry.COLUMN_COST},
                null,null,null);

        SimpleCursorAdapter standardChargeCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, standardChargeCursor, fromColumns, toViews,0);
        standardChargeCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (savedInstanceState != null) {
            mCalendar = (Calendar) savedInstanceState.getSerializable(DATE);
            mStandardChargeCosts = (HashMap<Long, Double>) savedInstanceState.getSerializable(CHARGE_DATA);
        } else {
            // Load the standard charge ID's into a hash map for future use
            mStandardChargeCosts = new HashMap<>();
            if (standardChargeCursor.moveToFirst()) {
                do {
                    mStandardChargeCosts.put(standardChargeCursor.getLong(standardChargeCursor.getColumnIndex(TripContract.StandardChargeEntry._ID)), standardChargeCursor.getDouble(standardChargeCursor.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_COST)));
                } while (standardChargeCursor.moveToNext());
            }
        }

        mCharge1.setAdapter(standardChargeCursorAdapter);
        mCharge1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTripChargeAmount1.setText(String.valueOf(mStandardChargeCosts.get(mCharge1.getSelectedItemId())));
                mTripChargeAmount1.formatAmount();
                calcTotals();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCharge2.setAdapter(standardChargeCursorAdapter);
        mCharge2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTripChargeAmount2.setText(String.valueOf(mStandardChargeCosts.get(mCharge2.getSelectedItemId())));
                mTripChargeAmount2.formatAmount();
                calcTotals();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCharge3.setAdapter(standardChargeCursorAdapter);
        mCharge3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTripChargeAmount3.setText(String.valueOf(mStandardChargeCosts.get(mCharge3.getSelectedItemId())));
                mTripChargeAmount3.formatAmount();
                calcTotals();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTripChargeAmount1.setText(String.valueOf(mStandardChargeCosts.get(mCharge1.getSelectedItemId())));
        mTripChargeAmount2.setText(String.valueOf(mStandardChargeCosts.get(mCharge2.getSelectedItemId())));
        mTripChargeAmount3.setText(String.valueOf(mStandardChargeCosts.get(mCharge3.getSelectedItemId())));



        mMilesTravelled.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((hasFocus == false) && (mMilesCost.getText().toString().equals(""))) {
                    if (mMilesTravelled.getText().toString().equals("") == false) {
                        Integer miles = Integer.parseInt(mMilesTravelled.getText().toString());
                        mMilesCost.setText(calcCost(miles));
                        mMilesCost.formatAmount();
                        calcTotals();
                    }
                }
            }
        });

        mId = getActivity().getIntent().getExtras() != null ? getActivity().getIntent().getExtras().getLong(TripListActivity.KEY_ID) : null;
        if (mId != null) {
            Cursor trip_cursor = getActivity().getContentResolver().query(TripContract.TripEntry.buildTripById(mId),
                    tripColumns,
                    TripContract.ClientEntry._ID + " = ?",
                    new String[]{String.valueOf(mId)},
                    null,
                    null);
            trip_cursor.moveToFirst();

            // Retrieve the date
            Date date = new Date();
            String dateString = trip_cursor.getString(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_DATE_TIME));
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            try {
                date = dateTimeFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mCalendar.setTime(date);
            UpdateDate(mCalendar);

            // Retrieve client
            int client_id = trip_cursor.getInt(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_CLIENT_ID));
            mClient.setSelection(Util.setSpinnerSelection(mClient, client_id));

            // Starting place
            mFrom.setText(trip_cursor.getString(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_STARTING_PLACE)));

            // Ending place
            mTo.setText(trip_cursor.getString(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_ENDING_PLACE)));

            // Distance
            mMilesTravelled.setText(trip_cursor.getString(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_DISTANCE)));

            // Cost
            mMilesCost.setText(String.valueOf(trip_cursor.getFloat(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_COST))));

            // Complete
            mIsComplete.setChecked(trip_cursor.getInt(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_COMPLETE)) == 1);

            // Notes
            mNotes.setText(trip_cursor.getString(trip_cursor.getColumnIndex(TripContract.TripEntry.COLUMN_NOTES)));

            trip_cursor.close();

            // Get any standard charges
            Cursor standard_charge_cursor = getActivity().getContentResolver().query(TripContract.TripChargeEntry.buildTripChargeById(mId),
                    chargeColumns,
                    null,
                    null,
                    null,
                    null);
            if (standard_charge_cursor.moveToFirst()) {
                int standard_charge_count = 0;
                int customer_charge_count = 0;
                do {
                    int spinner_id = standard_charge_cursor.getInt(standard_charge_cursor.getColumnIndex(TripContract.TripChargeEntry.COLUMN_STANDARD_CHARGE_ID));
                    String charge_cost = String.valueOf(standard_charge_cursor.getFloat(standard_charge_cursor.getColumnIndex(TripContract.TripChargeEntry.COLUMN_COST)));
                    String description = standard_charge_cursor.getString(standard_charge_cursor.getColumnIndex(TripContract.TripChargeEntry.COLUMN_DESCRIPTION));
                    boolean is_tax = standard_charge_cursor.getInt(standard_charge_cursor.getColumnIndex(TripContract.TripChargeEntry.COLUMN_IS_TAX)) == 1 ? true : false;
                    if (is_tax) {
                        mTaxAmount.setText(charge_cost);
                        mTaxAmount.formatAmount();
                    } else if (spinner_id > 0) {
                            switch (standard_charge_count) {
                                case 0:
                                    mCharge1.setSelection(Util.setSpinnerSelection(mCharge1, spinner_id));
                                    mTripChargeAmount1.setText(charge_cost);
                                    break;
                                case 1:
                                    mCharge2.setSelection(Util.setSpinnerSelection(mCharge2, spinner_id));
                                    mTripChargeAmount2.setText(charge_cost);
                                    break;
                                case 2:
                                    mCharge3.setSelection(Util.setSpinnerSelection(mCharge3, spinner_id));
                                    mTripChargeAmount3.setText(charge_cost);
                                    break;
                            }
                            standard_charge_count++;
                        } else {
                            switch (customer_charge_count) {
                                case 0:
                                    mTripCustomerCharge1.setText(description);
                                    mTripCustomerChargeAmount1.setText(charge_cost);
                                    mTripCustomerChargeAmount1.formatAmount();
                                    break;
                                case 1:
                                    mTripCustomerCharge2.setText(description);
                                    mTripCustomerChargeAmount2.setText(charge_cost);
                                    mTripCustomerChargeAmount2.formatAmount();
                                    break;
                                case 2:
                                    mTripCustomerCharge3.setText(description);
                                    mTripCustomerChargeAmount3.setText(charge_cost);
                                    mTripCustomerChargeAmount3.formatAmount();
                                    break;
                            }
                            customer_charge_count++;
                        }
                } while (standard_charge_cursor.moveToNext());
            }
            standard_charge_cursor.close();
        }

        // Set the date

        mDateField.setClickable(false);
        // Load the spinner with a the date string

        mDateField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickerDialog();
                }
                return true;
            }
        });

        mTimeField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showTimePickerDialog();
                }
                return true;
            }
        });

        UpdateDate(mCalendar);
        //calcTotals();



        return rootView;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATE, mCalendar);
        outState.putSerializable(CHARGE_DATA, mStandardChargeCosts);
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

    @Override
    public void backPressed() {
        // Insert on back press{
        if (!mFrom.getText().toString().equals("") && !mTo.getText().toString().equals("")) {

            SimpleDateFormat fmt = new SimpleDateFormat(DATE_TIME_FORMAT);
            String trip_date_time = fmt.format(mCalendar.getTime());

            String sJourneyDateTime = String.format(
                    "%tb %1$te, %1$tl:%1$tM %1$Tp", mCalendar);

            String sDescription = sJourneyDateTime + " - " + ((TextView)mClient.getSelectedView()).getText().toString()
                    + "\n" + mFrom.getText().toString() + " to " + mTo.getText().toString()
                    + "\n" + mMilesTravelled.getText().toString() + " miles" + " - " + mMilesCost.getText().toString();

            ContentValues cvTrip = new ContentValues();
            cvTrip.put(TripContract.TripEntry.COLUMN_STARTING_PLACE, mFrom.getText().toString());
            cvTrip.put(TripContract.TripEntry.COLUMN_ENDING_PLACE, mTo.getText().toString());
            cvTrip.put(TripContract.TripEntry.COLUMN_DATE_TIME, trip_date_time);
            cvTrip.put(TripContract.TripEntry.COLUMN_CLIENT_ID, mClient.getSelectedItemId());
            cvTrip.put(TripContract.TripEntry.COLUMN_DISTANCE, mMilesTravelled.getText().toString());
            cvTrip.put(TripContract.TripEntry.COLUMN_COST, mMilesCost.getText().toString());
            cvTrip.put(TripContract.TripEntry.COLUMN_COMPLETE, mIsComplete.isChecked());
            cvTrip.put(TripContract.TripEntry.COLUMN_NOTES, mNotes.getText().toString());
            cvTrip.put(TripContract.TripEntry.COLUMN_DESCRIPTION, sDescription);

            if (mId == null) {
                Uri uri = getActivity().getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, cvTrip);
                String trip_id = TripContract.TripEntry.getIDSettingFromUri(uri);
                if (!trip_id.equals(-1) ) {
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
                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void deleteItem() {
        int rows_deleted = getActivity().getContentResolver().delete(TripContract.TripEntry.CONTENT_URI, TripContract.TripEntry._ID + " = ?", new String[]{String.valueOf(mId)});
        if (rows_deleted > 0) {
            Toast.makeText(getActivity(), getString(R.string.trip_deleted), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
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

    private String calcCost(Integer miles) {

        float tripCost = 0;
        if ((miles <= 0) || (miles == null)) {
            miles = 0;
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        Cursor cursor = getActivity().getContentResolver().query(TripContract.ClientEntry.buildClientById(mClient.getSelectedItemId()),
                new String[]{TripContract.ClientEntry.COLUMN_PRICE_PER_MILE},null,null,null,null);
        if (cursor.moveToFirst()) {
            float price = cursor.getFloat(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE));
            tripCost = (price * miles);
        }
        cursor.close();
        return nf.format(tripCost);
    }

    private double calcTax() {

        double tax = 0;
        Cursor cursor = getActivity().getContentResolver().query(TripContract.ClientEntry.buildClientById(mClient.getSelectedItemId()),
                new String[]{TripContract.ClientEntry.COLUMN_TAX_RATE},null,null,null,null);
        if (cursor.moveToFirst()) {
            double tax_rate = cursor.getDouble(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_TAX_RATE));
            tax = (mTotalCostAmount.getValue() * tax_rate);
        }
        cursor.close();
        return tax;
    }

    private void calcTotals() {

        float total_charges = 0;
        total_charges += mMilesCost.getValue();
        total_charges += mTripChargeAmount1.getValue();
        total_charges += mTripChargeAmount2.getValue();
        total_charges += mTripChargeAmount3.getValue();
        total_charges += mTripCustomerChargeAmount1.getValue();
        total_charges += mTripCustomerChargeAmount2.getValue();
        total_charges += mTripCustomerChargeAmount3.getValue();
        total_charges += mTaxAmount.getValue();
        mTotalCostAmount.setText(String.valueOf(total_charges));
        mTotalCostAmount.formatAmount();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        DatePickerFragment.OnDateSelected mCallback;
        Calendar mCalendar = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog tpd = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, hour, minute, false);
            tpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return tpd;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
            mCallback.onDateSelected(mCalendar);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (DatePickerFragment.OnDateSelected) activity;
                mCalendar = mCallback.getCurrentlySelectedDate();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnDateSelected");
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        OnDateSelected mCallback;
        Calendar mCalendar = Calendar.getInstance();

        public interface OnDateSelected {
            public void onDateSelected(Calendar calendar);
            public Calendar getCurrentlySelectedDate();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);
            dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year,monthOfYear,dayOfMonth);
            mCallback.onDateSelected(mCalendar);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (OnDateSelected) activity;
                mCalendar = mCallback.getCurrentlySelectedDate();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnDateSelected");
            }
        }
    }
}
