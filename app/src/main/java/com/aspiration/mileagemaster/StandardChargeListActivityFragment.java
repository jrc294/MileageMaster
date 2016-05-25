package com.aspiration.mileagemaster;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspiration.mileagemaster.data.StandardListAdapter;
import com.aspiration.mileagemaster.data.TripContract;

/**
 * Created by jonathan.cook on 4/18/2016.
 */
public class StandardChargeListActivityFragment extends Fragment implements TabFragment, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TRIP_LOADER = 0;
    RecyclerView mRecyclerView;
    public static final String KEY_ID = "id";

    public StandardChargeListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_standard_charge_list, container, false);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.standard_charge_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getActivity().getSupportLoaderManager().initLoader(TRIP_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TripContract.StandardChargeEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                new String[]{TripContract.StandardChargeEntry._ID,
                        TripContract.StandardChargeEntry.COLUMN_NAME,
                        TripContract.StandardChargeEntry.COLUMN_COST},TripContract.StandardChargeEntry._ID + " > ?",new String[]{"1"},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            mRecyclerView.setAdapter(new StandardListAdapter(data));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void refresh(Intent data) {


    }
}
