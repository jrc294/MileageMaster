package com.aspiration.mileagemaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;
import com.aspiration.mileagemaster.data.Util;

/**
 * A placeholder fragment containing a simple view.
 */
public class ClientActivityFragment extends Fragment implements BackFragment {

    public ClientActivityFragment() {
    }

    Spinner mCharge1;
    Spinner mCharge2;
    Spinner mCharge3;
    Long mId;
    EditText mName;
    EditText mCostPerMile;
    EditText mTaxRate;

    private static final String INITIAL_NAME = "initial_name";
    private static final String INITIAL_COST_PER_MILE = "initial_cost_per_mile";
    private static final String INITIAL_TAX_RATE = "initial_tax_rate";
    private static final String INITIAL_CHARGE1 = "initial_charge1";
    private static final String INITIAL_CHARGE2 = "initial_charge2";
    private static final String INITIAL_CHARGE3 = "initial_charge3";
    private static final String INITIAL_ID = "initial_id";

    String mInitName;
    String mInitCostPerMile;
    String mInitTaxRate;
    long mInitCharge1;
    long mInitCharge2;
    long mInitCharge3;

    String[] clientColumns = new String[]
            {TripContract.ClientEntry.COLUMN_NAME,
            TripContract.ClientEntry.COLUMN_PRICE_PER_MILE,
            TripContract.ClientEntry.COLUMN_TAX_RATE,
            TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID,
            TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID,
            TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View viewroot =  inflater.inflate(R.layout.fragment_client, container, false);
        mCharge1 = (Spinner) viewroot.findViewById(R.id.spStandardCharge1);
        mCharge2 = (Spinner) viewroot.findViewById(R.id.spStandardCharge2);
        mCharge3 = (Spinner) viewroot.findViewById(R.id.spStandardCharge3);
        mName = (EditText) viewroot.findViewById(R.id.etClientName);
        mCostPerMile = (EditText) viewroot.findViewById(R.id.etClientMileageCost);
        mTaxRate = (EditText) viewroot.findViewById(R.id.etTaxRate);

        String[] fromColumns = new String[]{TripContract.StandardChargeEntry.COLUMN_NAME};
        int[] toViews = new int[]{android.R.id.text1};

        Cursor cursor = getActivity().getContentResolver().query(TripContract.StandardChargeEntry.CONTENT_URI,
                new String[]{TripContract.StandardChargeEntry._ID, TripContract.StandardChargeEntry.COLUMN_NAME},
                null,null,null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, cursor, fromColumns, toViews,0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCharge1.setAdapter(adapter);
        mCharge2.setAdapter(adapter);
        mCharge3.setAdapter(adapter);

        mId = getActivity().getIntent().getExtras() != null ? getActivity().getIntent().getExtras().getLong(StandardChargeListActivity.KEY_ID) : null;
        if (mId != null) {
            Cursor client_cursor = getActivity().getContentResolver().query(TripContract.ClientEntry.buildClientById(mId),
                    clientColumns,
                    TripContract.ClientEntry._ID + " = ?",
                    new String[]{String.valueOf(mId)},
                    null,
                    null);
            client_cursor.moveToFirst();
            mName.setText(client_cursor.getString(client_cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_NAME)));
            mCostPerMile.setText(String.valueOf(client_cursor.getFloat(client_cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE))));
            mTaxRate.setText(String.valueOf(client_cursor.getFloat(client_cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_TAX_RATE))));
            mCharge1.setSelection(Util.setSpinnerSelection(mCharge1, client_cursor.getInt(client_cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID))));
            mCharge2.setSelection(Util.setSpinnerSelection(mCharge2, client_cursor.getInt(client_cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID))));
            mCharge3.setSelection(Util.setSpinnerSelection(mCharge3, client_cursor.getInt(client_cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID))));
            //mCost.formatCharge(true);
            client_cursor.close();

            if (savedInstanceState != null) {
                mInitName = savedInstanceState.getString(INITIAL_NAME);
                mInitCostPerMile = savedInstanceState.getString(INITIAL_COST_PER_MILE);
                mInitTaxRate = savedInstanceState.getString(INITIAL_TAX_RATE);
                mInitCharge1 = savedInstanceState.getLong(INITIAL_CHARGE1);
                mInitCharge2 = savedInstanceState.getLong(INITIAL_CHARGE2);
                mInitCharge3 = savedInstanceState.getLong(INITIAL_CHARGE3);
                mId = savedInstanceState.getLong(INITIAL_ID);
            } else {
                mInitName = mName.getText().toString();
                mInitCostPerMile = mCostPerMile.getText().toString();
                mInitCharge1 = mCharge1.getSelectedItemId();
                mInitCharge2 = mCharge2.getSelectedItemId();
                mInitCharge3 = mCharge3.getSelectedItemId();
                mInitTaxRate = mTaxRate.getText().toString();
            }
        }

        return viewroot;
    }


    @Override
    public void backPressed() {
        // Insert on back press
        if (!mName.getText().toString().equals(mInitName) || !mCostPerMile.getText().toString().equals(mInitCostPerMile) || !mTaxRate.getText().toString().equals(mInitTaxRate) || mCharge1.getSelectedItemId() != mInitCharge1  || mCharge2.getSelectedItemId() != mInitCharge2  || mCharge3.getSelectedItemId() != mInitCharge3) {
            if (mId == null && !mName.getText().toString().equals("") && !mCostPerMile.getText().toString().equals("")  && !mTaxRate.getText().toString().equals("")) {
                // Save new charge
                //mCostPerMile.formatCharge(true);
                ContentValues cv = new ContentValues();
                cv.put(TripContract.ClientEntry.COLUMN_NAME, mName.getText().toString());
                cv.put(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE, Float.parseFloat(mCostPerMile.getText().toString()));
                cv.put(TripContract.ClientEntry.COLUMN_TAX_RATE, Float.parseFloat(mTaxRate.getText().toString()));
                Uri uri = getActivity().getContentResolver().insert(TripContract.ClientEntry.CONTENT_URI, cv);
                if (!TripContract.ClientEntry.getIDSettingFromUri(uri).equals(-1)) {
                    Toast.makeText(getActivity(), getString(R.string.client_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } else if (mId != null && !mName.getText().toString().equals("") && !mCostPerMile.getText().toString().equals("")  && !mTaxRate.getText().toString().equals("")) {
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
            }
        }
    }

    @Override
    public void deleteItem() {
        int rows_deleted = getActivity().getContentResolver().delete(TripContract.ClientEntry.CONTENT_URI, TripContract.ClientEntry._ID + " = ?", new String[]{String.valueOf(mId)});
        if (rows_deleted > 0) {
            Toast.makeText(getActivity(), getString(R.string.client_deleted), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the movie stores
        outState.putString(INITIAL_NAME, mInitName);
        outState.putString(INITIAL_COST_PER_MILE, mInitCostPerMile);
        outState.putString(INITIAL_TAX_RATE, mInitTaxRate);
        outState.putLong(INITIAL_CHARGE1, mInitCharge1);
        outState.putLong(INITIAL_CHARGE2, mInitCharge2);
        outState.putLong(INITIAL_CHARGE3, mInitCharge3);
        if (mId != null) {
            outState.putLong(INITIAL_ID, mId);
        }
    }
}
