package com.aspiration.mileagemaster.data;

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
}
