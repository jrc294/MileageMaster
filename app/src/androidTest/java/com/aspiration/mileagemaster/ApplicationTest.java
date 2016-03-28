package com.aspiration.mileagemaster;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.aspiration.mileagemaster.data.TripContract;
import com.aspiration.mileagemaster.data.TripDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    static final String LOG_TAG = ApplicationTest.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(TripDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        //deleteTheDatabase();
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateDb() throws Throwable {
        SQLiteDatabase db = new TripDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());
        if (c.moveToFirst()) {
            do {
                Log.d(LOG_TAG, c.getString(c.getColumnIndex(TripContract.ClientEntry.COLUMN_NAME)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    public void testStandardChargeInsert() throws Throwable {

        ContentValues cv = new ContentValues();
        cv.put(TripContract.StandardChargeEntry.COLUMN_NAME, "My Charge");
        cv.put(TripContract.StandardChargeEntry.COLUMN_COST, 5.25);

       // How do we create the content resolver
        Uri uri = getContext().getContentResolver().insert(TripContract.StandardChargeEntry.CONTENT_URI, cv);

        // Ok. Use the uri to get a cursor back
        Cursor cursor = getContext().getContentResolver().query(uri,
                new String[]{TripContract.StandardChargeEntry.COLUMN_NAME, TripContract.StandardChargeEntry.COLUMN_COST},
                TripContract.StandardChargeEntry._ID + " = ?", null, null, null);
        assertTrue("Error: This means that the standard charges has not been created correctly", cursor.moveToFirst());
        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_NAME)));
            Log.d(LOG_TAG, String.valueOf(cursor.getFloat(cursor.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_COST))));
        }
        cursor.close();

    }

    public void testClientInsert() throws Throwable {

        ContentValues cv = new ContentValues();
        cv.put(TripContract.StandardChargeEntry.COLUMN_NAME, "Client charge");
        cv.put(TripContract.StandardChargeEntry.COLUMN_COST, 6.25);

        // How do we create the content resolver
        getContext().getContentResolver().insert(TripContract.StandardChargeEntry.CONTENT_URI, cv);

        cv = new ContentValues();
        cv.put(TripContract.ClientEntry.COLUMN_NAME, "Myself");
        cv.put(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE, 0.50);
        cv.put(TripContract.ClientEntry.COLUMN_TAX_RATE, 6.00);
        cv.put(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID, 1);

        // How do we create the content resolver
        Uri uri = getContext().getContentResolver().insert(TripContract.ClientEntry.CONTENT_URI, cv);

        // Ok. Use the uri to get a cursor back
        Cursor cursor = getContext().getContentResolver().query(uri,
                new String[] {TripContract.ClientEntry.COLUMN_NAME, TripContract.ClientEntry.COLUMN_PRICE_PER_MILE, TripContract.ClientEntry.COLUMN_TAX_RATE, TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID},
                TripContract.ClientEntry._ID + " = ?",null,null,null);
        assertTrue("Error: This means that the client has not been created correctly", cursor.moveToFirst());
        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_NAME)));
            Log.d(LOG_TAG, String.valueOf(cursor.getFloat(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_PRICE_PER_MILE))));
            Log.d(LOG_TAG, String.valueOf(cursor.getFloat(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_TAX_RATE))));
            Log.d(LOG_TAG, String.valueOf(cursor.getInt(cursor.getColumnIndex(TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID))));
        }

    }
}