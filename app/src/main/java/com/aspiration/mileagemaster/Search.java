package com.aspiration.mileagemaster;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Intent;
import android.database.Cursor;

import com.aspiration.mileagemaster.data.TripContract;
import com.aspiration.mileagemaster.data.Util;

public class Search extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TripActivityFragment.DatePickerFragment.OnDateSelected{

	private Spinner mDateFrom;
	private Spinner mDateTo;
	private ImageButton btnRevert;
	private ImageButton btnSearch;
	private CheckBox chkCheck;
	private TextView lblAgencies;
	private static final String DATE_FROM_PICKER = "date_from_picker";
    private static final String DATE_TO_PICKER = "date_to_picker";
    private static final int LOADER_CLIENT = 20;
	Calendar mCalendarFrom;
	Calendar mCalendarTo;
	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private static final String DATE_FORMAT_INT = "yyyy-MM-dd";
	private Spinner mAgencies;
	private Spinner mCompleted;
	private long AgencySelected;
	private String CompleteStateSelected;
    SimpleCursorAdapter mClientCursorAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

        if (savedInstanceState == null) {
            mCalendarFrom = Calendar.getInstance();
            mCalendarTo = Calendar.getInstance();
        } else {
            mCalendarFrom = (Calendar) savedInstanceState.getSerializable(DATE_FROM_PICKER);
            mCalendarTo = (Calendar) savedInstanceState.getSerializable(DATE_TO_PICKER);
        }

		mDateFrom = (Spinner) findViewById(R.id.spDateFrom);
		mDateFrom.setClickable(false);
		mDateFrom.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					showDateFromPickerDialog();
				}
				return true;
			}
		});

		mDateTo = (Spinner) findViewById(R.id.spDateTo);
		mDateTo.setClickable(false);
		mDateTo.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					showDateToPickerDialog();
				}
				return true;
			}
		});

		UpdateDate(mDateFrom, mCalendarFrom);
		UpdateDate(mDateTo, mCalendarTo);

        mAgencies = (Spinner) findViewById(R.id.spnAgency);

        String[] fromColumns = new String[]{TripContract.ClientEntry.COLUMN_NAME};
        int[] toViews = new int[]{android.R.id.text1};

        mClientCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, null, fromColumns, toViews, 0);
        mClientCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAgencies.setAdapter(mClientCursorAdapter);

		//Intent i = this.getIntent();
		//mCalendarFrom.set(Calendar.YEAR, i.getExtras().getInt("DATEFROMYEAR"));
		//mCalendarFrom
		//		.set(Calendar.MONTH, i.getExtras().getInt("DATEFROMMONTH"));
		//mCalendarFrom.set(Calendar.DAY_OF_MONTH,
		//		i.getExtras().getInt("DATEFROMDAY"));

		//mCalendarTo.set(Calendar.YEAR, i.getExtras().getInt("DATETOYEAR"));
		//mCalendarTo.set(Calendar.MONTH, i.getExtras().getInt("DATETOMONTH"));
		//mCalendarTo.set(Calendar.DAY_OF_MONTH, i.getExtras()
		//		.getInt("DATETODAY"));


		mCompleted = (Spinner) findViewById(R.id.spnCompleted);
		btnRevert = (ImageButton) findViewById(R.id.SearchCancelButton);
		btnSearch = (ImageButton) findViewById(R.id.SearchOkButton);
		lblAgencies = (TextView) findViewById(R.id.lblAgencies);
		chkCheck = (CheckBox) findViewById(R.id.cbkIncludeAgency);

		//chkCheck.setChecked(i.getExtras().getBoolean("INCLUDEAGENCY"));

		/*mDateFromButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG_FROM);
			}
		});

		mDateToButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG_TO);
			}
		});

		btnRevert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				Bundle extras = new Bundle();
				extras.putInt("DATEFROMYEAR", mCalendarFrom.get(Calendar.YEAR));
				extras.putInt("DATEFROMMONTH",
						mCalendarFrom.get(Calendar.MONTH));
				extras.putInt("DATEFROMDAY",
						mCalendarFrom.get(Calendar.DAY_OF_MONTH));

				extras.putInt("DATETOYEAR", mCalendarTo.get(Calendar.YEAR));
				extras.putInt("DATETOMONTH", mCalendarTo.get(Calendar.MONTH));
				extras.putInt("DATETODAY",
						mCalendarTo.get(Calendar.DAY_OF_MONTH));
				extras.putBoolean("INCLUDEAGENCY", chkCheck.isChecked());
				extras.putLong("AGENCY", AgencySelected);
				extras.putString(
						"COMPLETEDSTATE",
						mCompleted.getItemAtPosition(
								mCompleted.getSelectedItemPosition())
								.toString());
				i.putExtras(extras);
				setResult(RESULT_OK, i);
				finish();

			}
		});

		*/

		chkCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DisableAgencies();
			}
		});

        /*

		mAgencies.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long id) {
				AgencySelected = id;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*objDbAdapter = new MileageBuddyDbAdapter(this);
		objDbAdapter.open();

		Cursor agenciesCursor = objDbAdapter.fetchAllAgencies();
		startManagingCursor(agenciesCursor);

		String[] from = new String[] { "short_name" };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter agencies = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, agenciesCursor, from, to);
		agencies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAgencies.setAdapter(agencies);
		objDbAdapter.close();
        */
		String completedOptionsList[] = { "Completed Only", "Incompleted Only",
				"All Trips" };
		ArrayAdapter<String> completedOptions = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, completedOptionsList);
		completedOptions
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCompleted.setAdapter(completedOptions);
        /*
		AgencySelected = i.getExtras().getLong("AGENCYID");

		if (AgencySelected > 0) {
			for (int x = 0; x < mAgencies.getCount(); x++) {
				Long value = mAgencies.getItemIdAtPosition(x);
				if (value == AgencySelected) {
					mAgencies.setSelection(x);
				}
			}
		}

		CompleteStateSelected = i.getExtras().getString("COMPLETEDSTATE");

		if ((CompleteStateSelected != null)
				&& (CompleteStateSelected.length() > 0)) {
			for (int x = 0; x < mCompleted.getCount(); x++) {
				if (mCompleted.getItemAtPosition(x).toString()
						.equals(CompleteStateSelected)) {
					mCompleted.setSelection(x);
				}
			}
		}*/

	}

	private void DisableAgencies() {
		if (chkCheck.isChecked()) {
			mAgencies.setEnabled(true);
			lblAgencies.setEnabled(true);
		} else {
			mAgencies.setEnabled(false);
			lblAgencies.setEnabled(false);
		}

	}

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().initLoader(LOADER_CLIENT, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(DATE_FROM_PICKER, mCalendarFrom);
        outState.putSerializable(DATE_TO_PICKER, mCalendarTo);
        super.onSaveInstanceState(outState);
    }

    /*protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_DIALOG_FROM:
			return showDatePickerFrom();
		case DATE_PICKER_DIALOG_TO:
			return showDatePickerTo();
		}
		return super.onCreateDialog(id);
	}

	/*private DatePickerDialog showDatePickerFrom() {
		DatePickerDialog datePicker = new DatePickerDialog(Search.this,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mCalendarFrom.set(Calendar.YEAR, year);
						mCalendarFrom.set(Calendar.MONTH, monthOfYear);
						mCalendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						updateDateButtonText();
					}
				}, mCalendarFrom.get(Calendar.YEAR),
				mCalendarFrom.get(Calendar.MONTH),
				mCalendarFrom.get(Calendar.DAY_OF_MONTH));
		return datePicker;
	}

	private DatePickerDialog showDatePickerTo() {
		DatePickerDialog datePicker = new DatePickerDialog(Search.this,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mCalendarTo.set(Calendar.YEAR, year);
						mCalendarTo.set(Calendar.MONTH, monthOfYear);
						mCalendarTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						updateDateButtonText();
					}
				}, mCalendarTo.get(Calendar.YEAR),
				mCalendarTo.get(Calendar.MONTH),
				mCalendarTo.get(Calendar.DAY_OF_MONTH));
		return datePicker;
	}*/

	private void UpdateDate(Spinner spinner, Calendar calendar) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		ArrayList<String> date_for_spinner = new ArrayList<>();
		date_for_spinner.add(dateFormat.format(calendar.getTime()));

		ArrayAdapter<String> date_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, date_for_spinner);
		spinner.setAdapter(date_adapter);
	}

	private void showDateFromPickerDialog() {
		DialogFragment newFragment = new TripActivityFragment.DatePickerFragment();
		newFragment.show(getSupportFragmentManager(),DATE_FROM_PICKER);
	}

    private void showDateToPickerDialog() {
        DialogFragment newFragment = new TripActivityFragment.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),DATE_TO_PICKER);
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//updateDateButtonText();
		DisableAgencies();
	}

    @Override
    public void onDateSelected(Calendar calendar, String tag) {

        if (tag.equals(DATE_FROM_PICKER)) {
            mCalendarFrom = calendar;
            UpdateDate(mDateFrom, calendar);
        } else if (tag.equals(DATE_TO_PICKER)) {
            mCalendarTo = calendar;
            UpdateDate(mDateTo, calendar);
        }
    }

    @Override
    public Calendar getCurrentlySelectedDate(String tag) {
        if (tag.equals(DATE_FROM_PICKER)) {
            return mCalendarFrom;
        } else  {
            return mCalendarTo;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Loader<Cursor> ret = null;

        Uri uri = TripContract.ClientEntry.CONTENT_URI;
        ret = new CursorLoader(this,
                uri,
                new String[]{TripContract.ClientEntry._ID,
                        TripContract.ClientEntry.COLUMN_NAME}, null, null, null);
        return ret;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {

            String[] fromColumns = new String[]{TripContract.ClientEntry.COLUMN_NAME};
            int[] toViews = new int[]{android.R.id.text1};

            SimpleCursorAdapter clientCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, data, fromColumns, toViews, 0);
            clientCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mClientCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mClientCursorAdapter.swapCursor(null);
    }
}