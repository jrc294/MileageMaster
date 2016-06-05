package com.aspiration.mileagemaster;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.aspiration.mileagemaster.data.TripContract;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jonathan.cook on 6/5/2016.
 */
public class WidgetUpdateService extends IntentService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_today);

            Intent mainIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            Calendar cal = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            String date_from = dateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            String date_to = dateFormat.format(cal.getTime());

            int counter = 0;
            int distance_travelled = 0;
            double cost_of_miles = 0;
            double cost_of_charges = 0;
            Cursor c = this.getContentResolver().query(TripContract.TripEntry.buildTripSummaryForDaily(),
                    new String[]{TripContract.TripEntry._ID, TripContract.TripEntry.COLUMN_DISTANCE, TripContract.TripEntry.COLUMN_COST},
                    TripContract.TripEntry.COLUMN_DATE_TIME, new String[]{date_from,date_to}, null);
            if (c.moveToFirst()) {
                do {
                    counter++;
                    distance_travelled += Integer.parseInt(c.getString(c.getColumnIndex(TripContract.TripEntry.COLUMN_DISTANCE)));
                    cost_of_miles += c.getDouble(c.getColumnIndex(TripContract.TripEntry.COLUMN_COST));

                    Cursor c2 = this.getContentResolver().query(TripContract.TripChargeEntry.buildTripChargeById(c.getLong(c.getColumnIndex(TripContract.TripEntry._ID))),
                            new String[]{TripContract.TripChargeEntry.COLUMN_COST},null,null,null);
                    if (c2.moveToFirst()) {
                        do {
                            cost_of_charges += c2.getDouble(c2.getColumnIndex(TripContract.TripChargeEntry.COLUMN_COST));
                        } while (c2.moveToNext());
                    }
                    c2.close();

                } while (c.moveToNext());
            }
            c.close();

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            String charges = format.format(cost_of_miles + cost_of_charges);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            String distance_units = settings.getString(getString(R.string.pref_distance_unit_key), getString(R.string.pref_distance_unit_default));

            if (distance_units.equals(getString(R.string.pref_distance_unit_default))) {
                distance_units = getString(R.string.miles);
            } else {
                distance_units = getString(R.string.kms);
            }


            views.setTextViewText(R.id.tvTripsToday, String.format("%1$s = %2$s", getString(R.string.trips_today), String.valueOf(counter)));
            views.setTextViewText(R.id.tvMilesTravelledToday, String.format("%1$s = %2$s %3$s", getString(R.string.distance_today), String.valueOf(distance_travelled), distance_units));
            views.setTextViewText(R.id.tvTotalChargesToday, String.format("%1$s = %2$s", getString(R.string.charges_today), charges));


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
