package com.aspiration.mileagemaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String KEY_ID = "id";

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public enum Tab {CLIENTS, CHARGES, HOME, MY_TRIPS};

    Tab mCurrent_tab = Tab.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(view);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("HOME"));
        tabLayout.addTab(tabLayout.newTab().setText("TRIPS"));
        tabLayout.addTab(tabLayout.newTab().setText("CLIENTS"));
        tabLayout.addTab(tabLayout.newTab().setText("CHARGES"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setCurrentTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    HomeFragment tab1 = HomeFragment.newInstance(null,null);
                    return tab1;
                case 1:
                    TripListActivityFragment tab2 = new TripListActivityFragment();
                    return tab2;
                case 2:
                    ClientListActivityFragment tab3 = new ClientListActivityFragment();
                    return tab3;
                case 3:
                    StandardChargeListActivityFragment tab4 = new StandardChargeListActivityFragment();
                    return tab4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startActivity(View view) {
        Intent i = null;
        switch (mCurrent_tab) {
            case HOME : i = new Intent(this, TripActivity.class);break;
            case MY_TRIPS: i = new Intent(this, TripActivity.class);break;
            case CLIENTS : i = new Intent(this, ClientActivity.class); break;
            case CHARGES : i = new Intent(this, StandardChargeActivity.class); break;
        }
        if (view.getTag() != null) {
            i.putExtra(KEY_ID, (Long) view.getTag());
        }
        startActivity(i);
    }

    private void setCurrentTab(int current_tab) {
        switch (current_tab) {
            case 0 : mCurrent_tab = Tab.HOME; break;
            case 1 : mCurrent_tab = Tab.MY_TRIPS; break;
            case 2 : mCurrent_tab = Tab.CLIENTS; break;
            case 3 : mCurrent_tab = Tab.CHARGES; break;
        }
    }



}
