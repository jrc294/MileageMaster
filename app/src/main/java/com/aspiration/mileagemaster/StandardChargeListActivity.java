package com.aspiration.mileagemaster;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aspiration.mileagemaster.data.StandardChargeAdapter;
import com.aspiration.mileagemaster.data.TripContract;

public class StandardChargeListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private StandardChargeAdapter mAdapter;
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

        mDataset = getContentResolver().query(TripContract.StandardChargeEntry.CONTENT_URI,
                new String[]{TripContract.StandardChargeEntry.COLUMN_NAME, TripContract.StandardChargeEntry.COLUMN_COST},
                null,
                null,
                null);
        mAdapter = new StandardChargeAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

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

}
