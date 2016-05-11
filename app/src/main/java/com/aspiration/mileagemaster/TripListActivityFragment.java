package com.aspiration.mileagemaster;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.aspiration.mileagemaster.data.TripListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class TripListActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    RecyclerView mRecyclerView;
    private static final int TRIP_LOADER = 2;
    public static final String KEY_ID = "id";

    public TripListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trip_list_recycler_view);
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
        Uri uri = TripContract.TripEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                new String[]{TripContract.TripEntry._ID,
                        TripContract.TripEntry.COLUMN_DESCRIPTION},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            mRecyclerView.setAdapter(new TripListAdapter(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
