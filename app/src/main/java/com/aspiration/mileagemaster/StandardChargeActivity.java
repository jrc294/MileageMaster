package com.aspiration.mileagemaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;

public class StandardChargeActivity extends AppCompatActivity implements DeleteDialogFragment.NoticeDialogListener {

    EditText mName;
    EditTextCurrency mCost;
    String mInitName;
    String mInitCost;
    Long mId;

    private static final String INITIAL_NAME = "initial_name";
    private static final String INITIAL_COST = "initial_cost";
    private static final String INITIAL_ID = "initial_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_charge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mName = (EditText) findViewById(R.id.etChargeName);
        mCost = (EditTextCurrency) findViewById(R.id.etChargeCost);

        mId = getIntent().getExtras() != null ? getIntent().getExtras().getLong(StandardChargeListActivity.KEY_ID) : null;
        if (mId != null) {
            Cursor cursor = getContentResolver().query(TripContract.StandardChargeEntry.buildStandardChargeById(mId) ,
                    new String[]{TripContract.StandardChargeEntry.COLUMN_NAME, TripContract.StandardChargeEntry.COLUMN_COST},
                    TripContract.StandardChargeEntry._ID + " = ?",
                    new String[]{String.valueOf(mId)},
                    null,
                    null);
            cursor.moveToFirst();
            mName.setText(cursor.getString(cursor.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_NAME)));
            mCost.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_COST))));
            mCost.formatAmount();
            cursor.close();

            if (savedInstanceState != null) {
                mInitName = savedInstanceState.getString(INITIAL_NAME);
                mInitCost = savedInstanceState.getString(INITIAL_COST);
                mId = savedInstanceState.getLong(INITIAL_ID);
            } else {
                mInitName = mName.getText().toString();
                mInitCost = mCost.getText().toString();
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // Insert on back press
        mCost.formatAmount();
        if (!mName.getText().toString().equals(mInitName) || !mCost.getText().toString().equals(mInitCost)) {
            if (mId == null && !mName.getText().toString().equals("") && !mCost.getText().toString().equals("")) {
                // Save new charge
                ContentValues cv = new ContentValues();
                cv.put(TripContract.StandardChargeEntry.COLUMN_NAME, mName.getText().toString());
                cv.put(TripContract.StandardChargeEntry.COLUMN_COST, mCost.getValue());
                Uri uri = getContentResolver().insert(TripContract.StandardChargeEntry.CONTENT_URI, cv);
                if (!TripContract.StandardChargeEntry.getIDSettingFromUri(uri).equals(-1)) {
                    Toast.makeText(this, getString(R.string.standard_charge_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } else if (mId != null && !mName.getText().toString().equals("") && !mCost.getText().toString().equals("")) {
                // Update on back press

                ContentValues cv = new ContentValues();
                cv.put(TripContract.StandardChargeEntry.COLUMN_NAME, mName.getText().toString());
                cv.put(TripContract.StandardChargeEntry.COLUMN_COST, mCost.getValue());
                int rows_updated = getContentResolver().update(TripContract.StandardChargeEntry.CONTENT_URI, cv, TripContract.StandardChargeEntry._ID + " = ?", new String[]{String.valueOf(mId)});
                if (rows_updated > 0) {
                    Toast.makeText(this, getString(R.string.standard_charge_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } else if (mName.getText().toString().equals("") || mCost.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.standard_charge_not_saved), Toast.LENGTH_SHORT).show();
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        int rows_deleted = getContentResolver().delete(TripContract.StandardChargeEntry.CONTENT_URI, TripContract.StandardChargeEntry._ID + " = ?", new String[]{String.valueOf(mId)});
        if (rows_deleted > 0) {
            Toast.makeText(this, getString(R.string.standard_charge_deleted), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_standard_charge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_item:
                if (deleteOk()) {
                    DeleteDialogFragment confirmFragment = new DeleteDialogFragment();
                    confirmFragment.show(getSupportFragmentManager(), "confirm");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the movie stores
        outState.putString(INITIAL_NAME, mInitName);
        outState.putString(INITIAL_COST, mInitCost);
        if (mId != null) {
            outState.putLong(INITIAL_ID, mId);
        }
    }

    private boolean deleteOk() {
        Cursor c = getContentResolver().query(TripContract.ClientEntry.buildClientStandardChargeCheckById(mId),
                new String[] {TripContract.ClientEntry.COLUMN_NAME},
                null,null,null,null);
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(TripContract.ClientEntry.COLUMN_NAME));
            Toast.makeText(this, String.format(getResources().getString(R.string.standard_charge_is_in_use), name), Toast.LENGTH_LONG).show();
            return false;
        }
        c.close();

        c = getContentResolver().query(TripContract.TripChargeEntry.buildTripChargeTripById(mId),
                new String[] {TripContract.TripChargeEntry._ID},
                null,null,null,null);
        if (c.moveToFirst()) {
            Toast.makeText(this, getString(R.string.standard_charge_is_in_use_by_trip), Toast.LENGTH_LONG).show();
            return false;
        }
        c.close();
        return true;
    }

}
