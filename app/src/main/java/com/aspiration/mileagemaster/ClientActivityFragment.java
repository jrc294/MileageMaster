package com.aspiration.mileagemaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aspiration.mileagemaster.data.TripContract;
import com.aspiration.mileagemaster.data.Util;

import java.text.NumberFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class ClientActivityFragment extends Fragment implements BackFragment, LoaderManager.LoaderCallbacks<Cursor> {

    public ClientActivityFragment() {
    }

    Spinner mCharge1;
    Spinner mCharge2;
    Spinner mCharge3;
    Long mId;
    EditText mName;
    EditTextCurrency mCostPerMile;
    EditText mTaxRate;
    Client mClient;

    private static final String INITIAL_NAME = "initial_name";
    private static final String INITIAL_COST_PER_MILE = "initial_cost_per_mile";
    private static final String INITIAL_TAX_RATE = "initial_tax_rate";
    private static final String INITIAL_CHARGE1 = "initial_charge1";
    private static final String INITIAL_CHARGE2 = "initial_charge2";
    private static final String INITIAL_CHARGE3 = "initial_charge3";
    private static final String INITIAL_ID = "initial_id";

    private static final int CLIENT_FRAGMENT_CHARGES_LOADER = 200;
    private static final int CLIENT_FRAGMENT_LOADER = 201;

    SimpleCursorAdapter mAdapter;
    boolean mIsClientFragmentChargesLoaded = false;
    boolean mIsClientFragmentLoaded = false;

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
        mCostPerMile = (EditTextCurrency) viewroot.findViewById(R.id.etClientMileageCost);
        mTaxRate = (EditText) viewroot.findViewById(R.id.etTaxRate);

        mTaxRate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    formatTax();
                }
            }
        });

        String[] fromColumns = new String[]{TripContract.StandardChargeEntry.COLUMN_NAME};
        int[] toViews = new int[]{android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, null, fromColumns, toViews,0);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCharge1.setAdapter(mAdapter);
        mCharge2.setAdapter(mAdapter);
        mCharge3.setAdapter(mAdapter);

        return viewroot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mId = getActivity().getIntent().getExtras() != null ? getActivity().getIntent().getExtras().getLong(StandardChargeListActivity.KEY_ID) : null;
        getLoaderManager().initLoader(CLIENT_FRAGMENT_CHARGES_LOADER, null, this);
        // Only load the client detail if this is an edit and the client data isn't already in memory
        if (mId != null && savedInstanceState == null) {
            getLoaderManager().initLoader(CLIENT_FRAGMENT_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void backPressed() {
        formatTax();
        mCostPerMile.formatAmount();
        ContentValues cv = new ContentValues();
        cv.put(TripContract.ClientEntry.COLUMN_NAME, mName.getText().toString());
        cv.put(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE, mCostPerMile.getValue());
        cv.put(TripContract.ClientEntry.COLUMN_TAX_RATE, Float.parseFloat(mTaxRate.getText().toString().replace("%","")));
        cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID, mCharge1.getSelectedItemId());
        cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID, mCharge2.getSelectedItemId());
        cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID, mCharge3.getSelectedItemId());
        // Insert on back press
        if (mId == null && !mName.getText().toString().equals("")) {
            // Save new charge
            Uri uri = getActivity().getContentResolver().insert(TripContract.ClientEntry.CONTENT_URI, cv);
            if (!TripContract.ClientEntry.getIDSettingFromUri(uri).equals(-1)) {
                Toast.makeText(getActivity(), getString(R.string.client_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        } else if (mId != null && !mName.getText().toString().equals("") && !mCostPerMile.getText().toString().equals("")  && !mTaxRate.getText().toString().equals("")) {
            // Update on back press
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
    public Long getItemId() {
        return mId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mId != null) {
            outState.putLong(INITIAL_ID, mId);
        }
    }

    private void formatTax() {
        if (mTaxRate.getText().toString().length() == 0) {
            mTaxRate.setText("0");
        }
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        mTaxRate.setText(fmt.format(Float.parseFloat(mTaxRate.getText().toString().replace("%","")) / 100).toString());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = null;
        if (id == CLIENT_FRAGMENT_CHARGES_LOADER) {
            loader = new CursorLoader(getActivity(), TripContract.StandardChargeEntry.CONTENT_URI,
                    new String[]{TripContract.StandardChargeEntry._ID, TripContract.StandardChargeEntry.COLUMN_NAME},
                    null, null, null);
        } else if (id == CLIENT_FRAGMENT_LOADER) {
            loader = new CursorLoader(getActivity(), TripContract.ClientEntry.buildClientById(mId),
                    clientColumns,
                    TripContract.ClientEntry._ID + " = ?", new String[]{String.valueOf(mId)}, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            if (loader.getId() == CLIENT_FRAGMENT_CHARGES_LOADER) {
                mAdapter.swapCursor(data);
                mIsClientFragmentChargesLoaded = true;
                populateData();
            } else if (loader.getId() == CLIENT_FRAGMENT_LOADER) {
                data.moveToFirst();
                mClient = new Client(data.getString(data.getColumnIndex(TripContract.ClientEntry.COLUMN_NAME)),
                        data.getDouble(data.getColumnIndex(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE)),
                        data.getDouble(data.getColumnIndex(TripContract.ClientEntry.COLUMN_TAX_RATE)),
                        data.getLong(data.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID)),
                        data.getLong(data.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID)),
                        data.getLong(data.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID)));
                mIsClientFragmentLoaded = true;
                populateData();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CLIENT_FRAGMENT_CHARGES_LOADER) {
            mAdapter.swapCursor(null);
        }
    }

    private void populateData() {
        if (mIsClientFragmentLoaded && mIsClientFragmentChargesLoaded) {
            mName.setText(mClient.getName());
            mCostPerMile.setText(String.valueOf(mClient.getCostPerMile()));
            mTaxRate.setText(String.valueOf(String.valueOf(mClient.getTaxRate())));
            mCharge1.setSelection(Util.setSpinnerSelection(mCharge1, mClient.getCharge1id()));
            mCharge2.setSelection(Util.setSpinnerSelection(mCharge2, mClient.getCharge2id()));
            mCharge3.setSelection(Util.setSpinnerSelection(mCharge3, mClient.getCharge3id()));
            formatTax();
            mCostPerMile.formatAmount();
        }
    }
}
