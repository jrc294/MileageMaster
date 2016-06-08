package com.aspiration.mileagemaster;

import android.app.Application;
import android.os.Trace;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by jonathan.cook on 6/7/2016.
 */
public class MileageMasterApplication extends Application {

    Tracker mTracker;

    public void startTracking() {

        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker() {

        startTracking();
        return mTracker;
    }

}
