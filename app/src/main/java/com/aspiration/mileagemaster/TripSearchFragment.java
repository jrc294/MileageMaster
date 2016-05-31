package com.aspiration.mileagemaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.Calendar;


public class TripSearchFragment extends Fragment implements TabFragment, MasterDetailFragment {

    private static final String TRIP_FRAGMENT_MASTER_TAG = "mtag";
    private static final String TRIP_FRAGMENT_DETAIL_TAG = "dtag";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_trip_list, container, false);


        TabFragment master_fragment = (TabFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_MASTER_TAG);
        if (master_fragment == null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.trip_master_container, new TripListActivityFragment(), TRIP_FRAGMENT_MASTER_TAG)
                    .commit();
        }


        if (rootView.findViewById(R.id.trip_detail_container) != null) {
            ((Callback) getActivity()).setTwoPane(true);
        } else {
            ((Callback) getActivity()).setTwoPane(false);

        }



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        if (((Callback) getActivity()).isTwoPane()) {
            inflater.inflate(R.menu.menu_trip, menu);
        } else {
            inflater.inflate(R.menu.menu_trip_list_activity, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            Fragment detail_fragment = getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_DETAIL_TAG);
            if (detail_fragment != null) {
                DeleteDialogFragment confirmFragment = new DeleteDialogFragment();
                confirmFragment.show(getActivity().getSupportFragmentManager(), "confirm");
            } else {
                Toast.makeText(getActivity(), getString(R.string.select_a_trip_to_delete), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_search) {
            ((MainActivity) getActivity()).startSearchActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh(Intent data) {
        TabFragment master_fragment = (TabFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_MASTER_TAG);
        if (master_fragment != null) {
            master_fragment.refresh(data);
        }

        Fragment detail_fragment = getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_DETAIL_TAG);
        if (detail_fragment != null) {
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            trans.remove(detail_fragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void reloadDetail(long id) {
        Bundle arguments = new Bundle();
        arguments.putLong(MainActivity.KEY_ID, id);

        TripActivityFragment fragment = new TripActivityFragment();
        fragment.setArguments(arguments);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.trip_detail_container, fragment, TRIP_FRAGMENT_DETAIL_TAG)
                .commit();

        MasterDetailFragment master_fragment = (MasterDetailFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_MASTER_TAG);
        if (master_fragment != null) {
            master_fragment.reloadDetail(id);
        }
    }

    @Override
    public void updateCalendar(Calendar calendar) {
        ((TripActivityFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_DETAIL_TAG)).UpdateDate(calendar);
    }

    @Override
    public void deleteItem() {
        ((TripActivityFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_DETAIL_TAG)).deleteItem();
    }


    public interface Callback {

        public void setTwoPane(boolean twoPane);

        public boolean isTwoPane();
    }


}
