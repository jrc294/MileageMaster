package com.aspiration.mileagemaster.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jonathan.cook on 3/24/2016.
 */
public class TripContract {

    public static final String CONTENT_AUTHORITY = "com.aspiration.mileagemaster";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRIP = "trip";


    public static final class TripEntry implements BaseColumns {
        public static final String TABLE_NAME = "trip";
        public static final String COLUMN_STARTING_PLACE = "starting_place";
        public static final String COLUMN_ENDING_PLACE = "ending_place";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_CLIENT_ID = "client_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_COMPLETE = "complete";
        public static final String COLUMN_NOTES = "notes";
    }

    public static final class ClientEntry implements BaseColumns {
        public static final String TABLE_NAME = "client";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE_PER_MILE = "price_per_mile";
        public static final String COLUMN_TAX_RATE = "tax_rate";
        public static final String COLUMN_STANDARD_CHARGE_1_ID = "standard_charge_1_id";
        public static final String COLUMN_STANDARD_CHARGE_2_ID = "standard_charge_2_id";
        public static final String COLUMN_STANDARD_CHARGE_3_ID = "standard_charge_3_id";

        public static final String PATH_CLIENT = "client";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLIENT).build();

        // Method to build a Uri for querying an individual standard charge back
        public static Uri buildClientById(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Method to return the ID of the movie from the Uri
        public static String getIDSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class StandardChargeEntry implements BaseColumns {
        public static final String TABLE_NAME = "standard_charge";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COST = "cost";

        public static final String PATH_STANDARD_CHARGE = "standard_charge";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STANDARD_CHARGE).build();

        // Method to build a Uri for querying an individual standard charge back
        public static Uri buildStandardChargeById(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Method to return the ID of the movie from the Uri
        public static String getIDSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TripChargeEntry implements BaseColumns {
        public static final String TABLE_NAME = "trip_charge";
        public static final String COLUMN_TRIP_ID = "trip_id";
        public static final String COLUMN_STANDARD_CHARGE_ID = "standard_charge_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_IS_TAX = "is_tax";
    }


}
