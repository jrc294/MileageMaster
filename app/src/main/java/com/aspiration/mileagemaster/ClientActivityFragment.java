package com.aspiration.mileagemaster;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.aspiration.mileagemaster.data.TripContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ClientActivityFragment extends Fragment {

    public ClientActivityFragment() {
    }

    Spinner mCharge1;
    Spinner mCharge2;
    Spinner mCharge3;
    Long mId;
    EditText mName;
    EditText mCostPerMile;
    EditText mTaxRate;

    String[] clientColumns = new String[]
            {TripContract.ClientEntry.COLUMN_NAME,
            TripContract.ClientEntry.COLUMN_PRICE_PER_MILE,
            TripContract.ClientEntry.COLUMN_TAX_RATE};

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
            //mCost.formatCharge(true);
            client_cursor.close();

            /*if (savedInstanceState != null) {
                mInitName = savedInstanceState.getString(INITIAL_NAME);
                mInitCost = savedInstanceState.getString(INITIAL_COST);
                mId = savedInstanceState.getLong(INITIAL_ID);
            } else {
                mInitName = mName.getText().toString();
                mInitCost = mCost.getText().toString();
            }*/
        }

        return viewroot;
    }


}
