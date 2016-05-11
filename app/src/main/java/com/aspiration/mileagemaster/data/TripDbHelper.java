package com.aspiration.mileagemaster.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aspiration.mileagemaster.MainActivityFragment;
import com.aspiration.mileagemaster.R;

import static com.aspiration.mileagemaster.data.TripContract.ClientEntry;
import static com.aspiration.mileagemaster.data.TripContract.StandardChargeEntry;
import static com.aspiration.mileagemaster.data.TripContract.TripChargeEntry;
import static com.aspiration.mileagemaster.data.TripContract.TripEntry;

/**
 * Created by jonathan.cook on 3/24/2016.
 */
public class TripDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION =15;

    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    //
    //
    public static final String DATABASE_NAME = "mileagemaster.db";
    //public static final String DATABASE_NAME = "/storage/emulated/legacy/Android/data/com.aspiration.mileagemaster/mileagemaster.db";

    Context mContext;

    public TripDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRIP_TABLE = "CREATE TABLE " + TripEntry.TABLE_NAME + " ( " +
                TripEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TripEntry.COLUMN_STARTING_PLACE + " TEXT NOT NULL, " +
                TripEntry.COLUMN_ENDING_PLACE + " TEXT NOT NULL, " +
                TripEntry.COLUMN_DISTANCE + " TEXT NOT NULL, " +
                TripEntry.COLUMN_CLIENT_ID + " INTEGER NOT NULL, " +
                TripEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                TripEntry.COLUMN_DATE_TIME + " TEXT NOT NULL," +
                TripEntry.COLUMN_COST + " REAL NOT NULL," +
                TripEntry.COLUMN_COMPLETE + " BOOLEAN NOT NULL DEFAULT 0," +
                TripEntry.COLUMN_NOTES + " TEXT," +
                "FOREIGN KEY(" + TripEntry.COLUMN_CLIENT_ID + ") REFERENCES " + ClientEntry.TABLE_NAME + "(" + ClientEntry._ID + ")" +
                " );";
        final String SQL_CREATE_CLIENT_TABLE = "CREATE TABLE " + ClientEntry.TABLE_NAME + " ( " +
                ClientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ClientEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ClientEntry.COLUMN_PRICE_PER_MILE + " REAL NOT NULL, " +
                ClientEntry.COLUMN_TAX_RATE + " REAL NOT NULL, " +
                ClientEntry.COLUMN_STANDARD_CHARGE_1_ID + " INTEGER, " +
                ClientEntry.COLUMN_STANDARD_CHARGE_2_ID + " INTEGER, " +
                ClientEntry.COLUMN_STANDARD_CHARGE_3_ID + " INTEGER, " +
                "FOREIGN KEY(" + ClientEntry.COLUMN_STANDARD_CHARGE_1_ID + ") REFERENCES " + StandardChargeEntry.TABLE_NAME + "(" + StandardChargeEntry._ID + ")," +
                "FOREIGN KEY(" + ClientEntry.COLUMN_STANDARD_CHARGE_2_ID + ") REFERENCES " + StandardChargeEntry.TABLE_NAME + "(" + StandardChargeEntry._ID + ")," +
                "FOREIGN KEY(" + ClientEntry.COLUMN_STANDARD_CHARGE_3_ID + ") REFERENCES " + StandardChargeEntry.TABLE_NAME + "(" + StandardChargeEntry._ID + ")" +
                " );";
        final String SQL_CREATE_STANDARD_CHARGE_TABLE = "CREATE TABLE " + StandardChargeEntry.TABLE_NAME + " ( " +
                StandardChargeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StandardChargeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                StandardChargeEntry.COLUMN_COST + " REAL NOT NULL" +
                " );";
        final String SQL_CREATE_TRIP_CHARGE_TABLE = "CREATE TABLE " + TripChargeEntry.TABLE_NAME + " ( " +
                TripChargeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TripChargeEntry.COLUMN_TRIP_ID + " INTEGER NOT NULL, " +
                TripChargeEntry.COLUMN_STANDARD_CHARGE_ID + " INTEGER, " +
                TripChargeEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                TripChargeEntry.COLUMN_COST + " REAL NOT NULL, " +
                TripChargeEntry.COLUMN_IS_TAX + " BOOLEAN NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(" + TripChargeEntry.COLUMN_TRIP_ID + ") REFERENCES " + TripEntry.TABLE_NAME + "(" + TripEntry._ID + ")," +
                "FOREIGN KEY(" + TripChargeEntry.COLUMN_STANDARD_CHARGE_ID + ") REFERENCES " + StandardChargeEntry.TABLE_NAME + "(" + StandardChargeEntry._ID + ") ON DELETE CASCADE" +
                " );";

        Log.d(LOG_TAG, "Create database");
        db.execSQL(SQL_CREATE_STANDARD_CHARGE_TABLE);
        db.execSQL(SQL_CREATE_CLIENT_TABLE);
        db.execSQL(SQL_CREATE_TRIP_TABLE);
        db.execSQL(SQL_CREATE_TRIP_CHARGE_TABLE);
        db.execSQL("insert into " + StandardChargeEntry.TABLE_NAME + " (" + StandardChargeEntry.COLUMN_NAME + ", " + StandardChargeEntry.COLUMN_COST + ") values ('" + mContext.getString(R.string.notset) + "',0)");
        db.execSQL("insert into " + ClientEntry.TABLE_NAME + " (" + ClientEntry.COLUMN_NAME + ", " + ClientEntry.COLUMN_PRICE_PER_MILE + ", " + ClientEntry.COLUMN_TAX_RATE + ") values ('" + mContext.getString(R.string.myself) + "',0,0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Drop database");
        db.execSQL("DROP TABLE IF EXISTS " + TripChargeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TripEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StandardChargeEntry.TABLE_NAME);
        onCreate(db);
    }
}
