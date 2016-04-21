package com.aspiration.mileagemaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class StandardChargeListActivity extends AppCompatActivity {

    public static final String KEY_ID = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_charge_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            StandardChargeListActivityFragment fragment = new StandardChargeListActivityFragment();
            fragmentTransaction.add(R.id.standard_charge_list_container, fragment);
            fragmentTransaction.commit();
        }

    }

    public void startActivity(View view) {
        Intent i = new Intent(this, StandardChargeActivity.class);
        if (view.getTag() != null) {
            i.putExtra(KEY_ID, (Long) view.getTag());
        }
        startActivity(i);
    }

}
