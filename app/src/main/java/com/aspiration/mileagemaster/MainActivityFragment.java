package com.aspiration.mileagemaster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private AdView mAdView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewroot = inflater.inflate(R.layout.fragment_main, container, false);

        mAdView = (AdView) viewroot.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return viewroot;
    }

    public void hideAd() {
        mAdView.setVisibility(View.INVISIBLE);
    }

    public void showAd() {
        mAdView.setVisibility(View.VISIBLE);
    }


}
