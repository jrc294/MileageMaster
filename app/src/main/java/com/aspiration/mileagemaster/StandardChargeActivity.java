package com.aspiration.mileagemaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;

public class StandardChargeActivity extends AppCompatActivity implements DeleteDialogFragment.NoticeDialogListener {

    EditText mName;
    EditText mCost;
    Long mId;

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
        mCost = (EditText) findViewById(R.id.etChargeCost);

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
            cursor.close();
        }

        mCost.addTextChangedListener(new MyTextWatcher(mCost));
        mName.addTextChangedListener(new MyTextWatcher(mCost));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {


        // Insert on back press
        if (mId == null && !mName.getText().equals("") && !mCost.getText().equals("")) {
            // Save new charge
            ContentValues cv = new ContentValues();
            cv.put(TripContract.StandardChargeEntry.COLUMN_NAME, mName.getText().toString());
            cv.put(TripContract.StandardChargeEntry.COLUMN_COST, mCost.getText().toString());
            Uri uri = getContentResolver().insert(TripContract.StandardChargeEntry.CONTENT_URI,cv);
            if (!TripContract.StandardChargeEntry.getIDSettingFromUri(uri).equals(-1)) {
                Toast.makeText(this, getString(R.string.standard_charge_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        } else if (mId != null) {
            boolean dirty = (((String) mCost.getTag()).equals("dirty") || ((String) mName.getTag()).equals("dirty"));
            if (dirty) {
                // Update on back press
                ContentValues cv = new ContentValues();
                cv.put(TripContract.StandardChargeEntry.COLUMN_NAME, mName.getText().toString());
                cv.put(TripContract.StandardChargeEntry.COLUMN_COST, mCost.getText().toString());
                int rows_updated = getContentResolver().update(TripContract.StandardChargeEntry.CONTENT_URI, cv, TripContract.StandardChargeEntry._ID + " = ?", new String[]{String.valueOf(mId)});
                if (rows_updated > 0) {
                    Toast.makeText(this, getString(R.string.standard_charge_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
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

    public class MyTextWatcher implements TextWatcher {

        View mView;

        public MyTextWatcher(View view) {
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mView.setTag("dirty");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
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
                DeleteDialogFragment confirmFragment = new DeleteDialogFragment();
                confirmFragment.show(getSupportFragmentManager(),"confirm");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
