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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class TripActivityFragment extends Fragment implements BackFragment{

    public TripActivityFragment() {
    }

    Long mId;
    private static final String DATE = "date";
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
    EditText mMilesCost;
    CheckBox mIsComplete;
    EditText mNotes;

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
        mMilesCost = (EditText) rootView.findViewById(R.id.etMileageCost);
        mIsComplete = (CheckBox) rootView.findViewById(R.id.cbTripComplete);
        mNotes = (EditText) rootView.findViewById(R.id.etNotes);

        String[] fromColumns = new String[]{TripContract.ClientEntry.COLUMN_NAME};
        int[] toViews = new int[]{android.R.id.text1};

        Cursor clientCursor = getActivity().getContentResolver().query(TripContract.ClientEntry.CONTENT_URI,
                new String[]{TripContract.ClientEntry._ID, TripContract.ClientEntry.COLUMN_NAME},null,null,null,null);

        SimpleCursorAdapter clientCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, clientCursor, fromColumns, toViews, 0);
        clientCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClient.setAdapter(clientCursorAdapter);

        Cursor standardChargeCursor = getActivity().getContentResolver().query(TripContract.StandardChargeEntry.CONTENT_URI,
                new String[]{TripContract.StandardChargeEntry._ID, TripContract.StandardChargeEntry.COLUMN_NAME},
                null,null,null);

        SimpleCursorAdapter standardChargeCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, standardChargeCursor, fromColumns, toViews,0);
        standardChargeCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCharge1.setAdapter(standardChargeCursorAdapter);
        mCharge2.setAdapter(standardChargeCursorAdapter);
        mCharge3.setAdapter(standardChargeCursorAdapter);

        if (savedInstanceState != null) {
            mCalendar = (Calendar) savedInstanceState.getSerializable(DATE);
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
        if (mId == null && !mFrom.getText().toString().equals("") && !mTo.getText().toString().equals("")) {
            // Save new charge
            //mCostPerMile.formatCharge(true);
            SimpleDateFormat fmt = new SimpleDateFormat(DATE_TIME_FORMAT);
            String trip_date_time = fmt.format(mCalendar.getTime());

            String sJourneyDateTime = String.format(
                    "%tb %1$te, %1$tl:%1$tM %1$Tp", mCalendar);

            String sDescription = sJourneyDateTime + " - " + "[Agency]"
                    + "\n" + mFrom.getText().toString() + " to " + mTo.getText().toString()
                    + "\n" + mMilesTravelled.getText().toString() + " miles" + " - " + mMilesCost.getText().toString();

            ContentValues cv = new ContentValues();
            cv.put(TripContract.TripEntry.COLUMN_STARTING_PLACE, mFrom.getText().toString());
            cv.put(TripContract.TripEntry.COLUMN_ENDING_PLACE, mTo.getText().toString());
            cv.put(TripContract.TripEntry.COLUMN_DATE_TIME, trip_date_time);
            cv.put(TripContract.TripEntry.COLUMN_CLIENT_ID, mClient.getId());
            cv.put(TripContract.TripEntry.COLUMN_DISTANCE, mMilesTravelled.getText().toString());
            cv.put(TripContract.TripEntry.COLUMN_COST, mMilesCost.getText().toString());
            cv.put(TripContract.TripEntry.COLUMN_COMPLETE, mIsComplete.isChecked());
            cv.put(TripContract.TripEntry.COLUMN_NOTES, mNotes.getText().toString());
            cv.put(TripContract.TripEntry.COLUMN_DESCRIPTION, sDescription);

            Uri uri = getActivity().getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, cv);
            if (!TripContract.TripEntry.getIDSettingFromUri(uri).equals(-1)) {
                Toast.makeText(getActivity(), getString(R.string.trip_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        } /*else if (mId != null && !mName.getText().toString().equals("") && !mCostPerMile.getText().toString().equals("")  && !mTaxRate.getText().toString().equals("")) {
            // Update on back press
            //mCost.formatCharge(true);
            ContentValues cv = new ContentValues();
            cv.put(TripContract.ClientEntry.COLUMN_NAME, mName.getText().toString());
            cv.put(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE, Float.parseFloat(mCostPerMile.getText().toString()));
            cv.put(TripContract.ClientEntry.COLUMN_TAX_RATE, Float.parseFloat(mTaxRate.getText().toString()));
            cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID, (int) mCharge1.getSelectedItemId());
            cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID, (int) mCharge2.getSelectedItemId());
            cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID, (int) mCharge3.getSelectedItemId());
            int rows_updated = getActivity().getContentResolver().update(TripContract.ClientEntry.CONTENT_URI, cv, TripContract.ClientEntry._ID + " = ?", new String[]{String.valueOf(mId)});
            if (rows_updated > 0) {
                Toast.makeText(getActivity(), getString(R.string.client_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        } else if (mName.getText().toString().equals("")) {
            Toast.makeText(getActivity(), getString(R.string.client_not_saved), Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void deleteItem() {

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
