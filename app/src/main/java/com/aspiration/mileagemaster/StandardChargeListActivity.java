package com.aspiration.mileagemaster;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aspiration.mileagemaster.data.StandardChargeAdapter;
import com.aspiration.mileagemaster.data.TripContract;

public class StandardChargeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TRIP_LOADER = 0;
    private RecyclerView mRecyclerView;
    private StandardChargeAdapter mAdapter;
    public static final String KEY_ID = "id";
    Cursor mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_charge_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*mDataset = getContentResolver().query(TripContract.StandardChargeEntry.CONTENT_URI,
                new String[]{TripContract.StandardChargeEntry._ID, TripContract.StandardChargeEntry.COLUMN_NAME, TripContract.StandardChargeEntry.COLUMN_COST},
                null,
                null,
                null);
        mAdapter = new StandardChargeAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChargeActivity(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(TRIP_LOADER, null, this);
    }

    public void startChargeActivity(View view) {
        Intent i = new Intent(this, StandardChargeActivity.class);
        if (view.getTag() != null) {
            i.putExtra(KEY_ID, (Long) view.getTag());
        }
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Call the content resolver to get the images for all movies
        Uri uri = TripContract.StandardChargeEntry.CONTENT_URI;
        return new CursorLoader(this,
                uri,
                new String[]{TripContract.StandardChargeEntry._ID,
                        TripContract.StandardChargeEntry.COLUMN_NAME,
                        TripContract.StandardChargeEntry.COLUMN_COST},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            mRecyclerView.setAdapter(new StandardChargeAdapter(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
