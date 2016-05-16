package com.aspiration.mileagemaster.data;

import android.widget.Spinner;

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
}
