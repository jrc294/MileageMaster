package com.aspiration.mileagemaster;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ClientActivity extends AppCompatActivity implements DeleteDialogFragment.NoticeDialogListener {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.client_container, new ClientActivityFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        BackFragment back_fragment = (BackFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        back_fragment.backPressed();
        super.onBackPressed();
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
                BackFragment back_fragment = (BackFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                if (back_fragment.getItemId() > 1) {
                    DeleteDialogFragment confirmFragment = new DeleteDialogFragment();
                    confirmFragment.show(getSupportFragmentManager(), "confirm");
                } else {
                    Toast.makeText(this, getString(R.string.cannot_delete), Toast.LENGTH_SHORT).show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        BackFragment back_fragment = (BackFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        back_fragment.deleteItem();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

}
