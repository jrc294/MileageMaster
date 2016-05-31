package com.aspiration.mileagemaster;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class TripActivity extends AppCompatActivity implements TripActivityFragment.DatePickerFragment.OnDateSelected, DeleteDialogFragment.NoticeDialogListener, TripSearchFragment.Callback {

    private static final String TRIPFRAGMENT_TAG = "TFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            Long id = getIntent().getExtras() != null ? getIntent().getExtras().getLong(TripListActivity.KEY_ID) : null;
            Bundle arguments = new Bundle();

            if (id != null) {
                arguments.putLong(MainActivity.KEY_ID, id);
            }

            TripActivityFragment fragment = new TripActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.trip_container, fragment, TRIPFRAGMENT_TAG)
                    .commit();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_standard_charge, menu);
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        BackFragment back_fragment = (BackFragment) getSupportFragmentManager().findFragmentByTag(TRIPFRAGMENT_TAG);
        back_fragment.deleteItem();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDateSelected(Calendar calendar, String tag) {
        TripActivityFragment fragment = (TripActivityFragment) getSupportFragmentManager().findFragmentByTag(TRIPFRAGMENT_TAG);
        if (fragment != null) {
            fragment.UpdateDate(calendar);
        }
    }

    @Override
    public Calendar getCurrentlySelectedDate(String tag) {
        TripActivityFragment fragment = (TripActivityFragment) getSupportFragmentManager().findFragmentByTag(TRIPFRAGMENT_TAG);
        if (fragment != null) {
            return fragment.getCurrentlySelectedDate();
        }
        return Calendar.getInstance();
    }

    @Override
    public void onBackPressed() {
        BackFragment back_fragment = (BackFragment) getSupportFragmentManager().findFragmentByTag(TRIPFRAGMENT_TAG);
        back_fragment.backPressed();
        super.onBackPressed();
    }

    @Override
    public void setTwoPane(boolean twoPane) {

    }

    @Override
    public boolean isTwoPane() {
        return false;
    }
}
