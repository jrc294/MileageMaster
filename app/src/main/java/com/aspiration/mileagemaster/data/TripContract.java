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

        public static final String PATH_TRIP = "trip";

        public static final String PATH_TRIP_CLIENT = "client";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP).build();

        public static final Uri CONTENT_CLIENT_CHECK_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP).appendPath(PATH_TRIP_CLIENT).build();

        public static final Uri CONTENT_CLIENT_MONTHLY_SUMMARY = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP).build();

        // Method to build a Uri for querying an individual trip back
        public static Uri buildTripById(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTripSummaryByDate(String date) {
            return CONTENT_CLIENT_MONTHLY_SUMMARY.buildUpon().appendPath(date).build();
        }

        public static Uri buildTripClientCheckById(long id) {
            return ContentUris.withAppendedId(CONTENT_CLIENT_CHECK_URI, id);
        }

        // Method to return the ID of the trip from the Uri
        public static String getIDSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // Method to return the ID of a standard charge being used by a client
        public static String getClientIdForTrip(Uri uri) {
            return uri.getPathSegments().get(2);
        }
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

        public static final String PATH_CLIENT_STANDARD_CHARGE = "client_standard_charge";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLIENT).build();

        public static final Uri CONTENT_STANDARD_CHARGE_CHECK_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLIENT).appendPath(PATH_CLIENT_STANDARD_CHARGE).build();

        // Method to build a Uri for querying an individual standard charge back
        public static Uri buildClientById(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildClientStandardChargeCheckById(long id) {
            return ContentUris.withAppendedId(CONTENT_STANDARD_CHARGE_CHECK_URI, id);
        }

        // Method to return the ID of the movie from the Uri
        public static String getIDSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // Method to return the ID of a standard charge being used by a client
        public static String getStandardChargeFromClient(Uri uri) {
            return uri.getPathSegments().get(2);
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

        public static final String PATH_TRIP_CHARGE_ENTRY = "trip_charge_entry";

        public static final String PATH_TRIP_CHARGES_FOR_CLIENT_RANGE = "trip_charges_for_client_range";

        public static final String PATH_TRIP_CHARGES_FOR_TRIPS = "trip_charges_for_trips";

        public static final Uri CONTENT_CHARGES_FOR_CLIENT_RANGE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP_CHARGE_ENTRY).appendPath(PATH_TRIP_CHARGES_FOR_CLIENT_RANGE).build();

        public static final Uri CONTENT_CHARGES_FOR_TRIPS = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP_CHARGE_ENTRY).appendPath(PATH_TRIP_CHARGES_FOR_TRIPS).build();

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP_CHARGE_ENTRY).build();

        public static Uri buildTripChargeById(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIDSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // Method to return the ID of a standard charge being used by a client
        public static String getStandardChargeIdForTrip(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildTripChargeTripById(long id) {
            return ContentUris.withAppendedId(CONTENT_CHARGES_FOR_TRIPS, id);
        }
    }


}
