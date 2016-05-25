package com.aspiration.mileagemaster.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Spinner;

import com.aspiration.mileagemaster.R;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by jonathan.cook on 4/6/2016.
 */
public class Util {

    public static String getCurrency() {
        Currency currency = Currency.getInstance(Locale.getDefault());
        return currency.getSymbol();
    }

    public static int setSpinnerSelection(Spinner spinner, long value) {

        int ret = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            Long spinner_value = spinner.getItemIdAtPosition(i);
            if (value == spinner_value) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    public static int setSpinnerSelection(Spinner spinner, String value) {

        int ret = 0;
        if (value != null) {
            for (int i = 0; i < spinner.getCount(); i++) {
                String spinner_value = (String) spinner.getItemAtPosition(i);
                if (value.equals(spinner_value)) {
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    public static String getDistanceUnit(Context c) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        return  settings.getString(c.getString(R.string.pref_distance_unit_key),c.getString(R.string.pref_distance_unit_default));
    }
}
