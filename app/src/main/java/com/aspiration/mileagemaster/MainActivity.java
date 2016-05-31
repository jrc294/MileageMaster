package com.aspiration.mileagemaster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aspiration.mileagemaster.data.TripListAdapter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, TripSearchFragment.Callback,
        TripActivityFragment.DatePickerFragment.OnDateSelected, DeleteDialogFragment.NoticeDialogListener, TripListAdapter.Callback {

    private TabLayout tabLayout;
    private ViewPager mViewPager;
    Map<Integer, TabFragment> mPageReferenceMap = new HashMap<>();

    boolean mSearchFilterOnClient = false;
    long mClientId;
    Calendar mSearchFromDate;
    Calendar mSearchToDate;
    String mCompletedStatus;
    String mSearchClientName;

    public static final String KEY_ID = "id";
    private static final int ACTIVITY_SEARCH = 1;
    private static final String SEARCH_FRAGMENT = "search_fragment";
    public boolean mTwoPane;




    public enum Tab {CLIENTS, CHARGES, HOME, MY_TRIPS};

    Tab mCurrent_tab = Tab.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mSearchFromDate = Calendar.getInstance();
            mSearchFromDate.add(Calendar.DATE, -30);
            mSearchToDate = Calendar.getInstance();
        } else {
            mSearchFromDate = (Calendar) savedInstanceState.getSerializable(Search.DATE_FROM);
            mSearchToDate = (Calendar) savedInstanceState.getSerializable(Search.DATE_TO);
            mClientId = savedInstanceState.getLong(Search.CLIENT_ID);
            mCompletedStatus = savedInstanceState.getString(Search.COMPLETED_STATUS);
            mSearchClientName = savedInstanceState.getString(Search.CLIENT_NAME);
            mSearchFilterOnClient = savedInstanceState.getBoolean(Search.INCLUDE_CLIENT);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startactivity(view);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.home)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.trips)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.clients)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.charges)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Search.DATE_FROM, mSearchFromDate);
        outState.putSerializable(Search.DATE_TO, mSearchToDate);
        outState.putLong(Search.CLIENT_ID, mClientId);
        outState.putString(Search.COMPLETED_STATUS, mCompletedStatus);
        outState.putString(Search.CLIENT_NAME, mSearchClientName);
        outState.putBoolean(Search.INCLUDE_CLIENT, mSearchFilterOnClient);
        super.onSaveInstanceState(outState);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment tab = null;

            switch (position) {
                case 0:
                    tab = HomeFragment.newInstance(null,null); break;
                case 1:
                    tab = new TripSearchFragment();
                    break;
                case 2:
                    tab = new ClientListActivityFragment(); break;
                case 3:
                    tab = new StandardChargeListActivityFragment(); break;
                default:
                    tab = null;
            }
            mPageReferenceMap.put(position, (TabFragment) tab);
            return tab;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        public TabFragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabFragment fragment = (TabFragment) super.instantiateItem(container, position);
            mPageReferenceMap.put(position, fragment);
            return fragment;
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void startactivity(View view) {
        Intent i = null;
        switch (mCurrent_tab) {
            case HOME : i = new Intent(this, TripActivity.class);break;
            case MY_TRIPS: i = new Intent(this, TripActivity.class); break;
            case CLIENTS : i = new Intent(this, ClientActivity.class); break;
            case CHARGES : i = new Intent(this, StandardChargeActivity.class); break;
        }

        if (mCurrent_tab == Tab.MY_TRIPS) {
            if (view.getTag() != null && view.getTag().toString().equals("filter")) {
                startSearchActivity();
            } else {
                if (view.getTag() != null) {
                    i.putExtra(KEY_ID, (Long) view.getTag());
                }
                if (mTwoPane && view.getTag() != null) {
                    ((MasterDetailFragment) mPageReferenceMap.get(1)).reloadDetail((Long) view.getTag());
                } else {
                    startActivity(i);
                }
            }
        } else {
            if (view.getTag() != null) {
                i.putExtra(KEY_ID, (Long) view.getTag());
            }
            startActivity(i);
        }
    }


    private void setCurrentTab(int current_tab) {
        switch (current_tab) {
            case 0 : mCurrent_tab = Tab.HOME; break;
            case 1 : mCurrent_tab = Tab.MY_TRIPS; break;
            case 2 : mCurrent_tab = Tab.CLIENTS; break;
            case 3 : mCurrent_tab = Tab.CHARGES; break;
        }
    }

    public void startSearchActivity() {
        Bundle args = new Bundle();
        args.putBoolean(Search.INCLUDE_CLIENT, mSearchFilterOnClient);
        args.putSerializable(Search.DATE_FROM, mSearchFromDate);
        args.putSerializable(Search.DATE_TO, mSearchToDate);
        args.putLong(Search.CLIENT_ID, mClientId);
        args.putString(Search.COMPLETED_STATUS, mCompletedStatus);
        Intent i = new Intent(this, Search.class);
        i.putExtras(args);
        startActivityForResult(i, ACTIVITY_SEARCH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_SEARCH) {
                refreshSearch(data);
            }
        }
    }

    private void refreshSearch(Intent data) {
        TabFragment myFragment = ((PagerAdapter) mViewPager.getAdapter()).getFragment(mViewPager.getCurrentItem());
        mSearchFilterOnClient = data.getExtras().getBoolean(Search.INCLUDE_CLIENT);
        mSearchFromDate = (Calendar) data.getExtras().getSerializable(Search.DATE_FROM);
        mClientId = data.getExtras().getLong(Search.CLIENT_ID);
        mSearchToDate = (Calendar) data.getExtras().getSerializable(Search.DATE_TO);
        mCompletedStatus = data.getExtras().getString(Search.COMPLETED_STATUS);
        mSearchClientName = data.getExtras().getString(Search.CLIENT_NAME);
        myFragment.refresh(data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    @Override
    public boolean isTwoPane() {
        return mTwoPane;
    }

    @Override
    public void onDateSelected(Calendar calendar, String tag) {
        ((MasterDetailFragment) mPageReferenceMap.get(1)).updateCalendar(calendar);
    }

    @Override
    public Calendar getCurrentlySelectedDate(String tag) {
        return null;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        MasterDetailFragment fragment = ((MasterDetailFragment)mPageReferenceMap.get(1));
        fragment.deleteItem();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
