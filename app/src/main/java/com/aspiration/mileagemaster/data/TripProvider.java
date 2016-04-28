package com.aspiration.mileagemaster.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.UnknownFormatConversionException;

/**
 * Created by jonathan.cook on 3/27/2016.
 */
public class TripProvider extends ContentProvider {

    TripDbHelper mOpenHelper;

    static final String LOG_TAG = TripProvider.class.getSimpleName();


    private static final int STANDARD_CHARGE = 100;
    private static final int STANDARD_CHARGE_BY_ID = 101;
    private static final int CLIENT = 110;
    private static final int CLIENT_BY_ID = 111;
    private static final int CLIENT_USING_STANDARD_CHARGE_ID = 112;
    private static final int TRIP = 120;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // _id = ?
    private static final String sStandardChargeID = TripContract.StandardChargeEntry._ID + " = ? ";

    private static final String sClientId = TripContract.ClientEntry._ID + " = ? ";

    private static final String sStandardChargeIDs = TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_1_ID + " = ? OR " +
                                                    TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_2_ID + " = ? OR " +
                                                    TripContract.ClientEntry.COLUMN_STANDARD_CHARGE_3_ID + " = ? ";


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TripContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TripContract.StandardChargeEntry.PATH_STANDARD_CHARGE, STANDARD_CHARGE);
        matcher.addURI(authority, TripContract.StandardChargeEntry.PATH_STANDARD_CHARGE + "/#", STANDARD_CHARGE_BY_ID);
        matcher.addURI(authority, TripContract.ClientEntry.PATH_CLIENT, CLIENT);
        matcher.addURI(authority, TripContract.ClientEntry.PATH_CLIENT + "/#", CLIENT_BY_ID);
        matcher.addURI(authority, TripContract.ClientEntry.PATH_CLIENT + "/*" + "/#", CLIENT_USING_STANDARD_CHARGE_ID);
        matcher.addURI(authority, TripContract.TripEntry.PATH_TRIP, TRIP);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/*", MOVIE_WITH_CATEGORY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TripDbHelper(getContext());
        mOpenHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortorder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case STANDARD_CHARGE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TripContract.StandardChargeEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            }
            case STANDARD_CHARGE_BY_ID: {
                String id = TripContract.StandardChargeEntry.getIDSettingFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TripContract.StandardChargeEntry.TABLE_NAME,
                        projection,
                        sStandardChargeID,
                        new String[]{id},
                        null,
                        null,
                        null);
                break;
            }
            case CLIENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TripContract.ClientEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            }
            case CLIENT_BY_ID: {
                String id = TripContract.ClientEntry.getIDSettingFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TripContract.ClientEntry.TABLE_NAME,
                        projection,
                        sClientId,
                        new String[]{id},
                        null,
                        null,
                        null);
                break;
            }
            case CLIENT_USING_STANDARD_CHARGE_ID: {
                String id = TripContract.ClientEntry.getZZZ(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TripContract.ClientEntry.TABLE_NAME,
                        projection,
                        sStandardChargeIDs,
                        new String[]{id,id,id},
                        null,
                        null,
                        null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STANDARD_CHARGE: {
                long _id = db.insert(TripContract.StandardChargeEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = TripContract.StandardChargeEntry.buildStandardChargeById(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case CLIENT: {
                long _id = db.insert(TripContract.ClientEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = TripContract.ClientEntry.buildClientById(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRIP: {
                long _id = db.insert(TripContract.TripEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = TripContract.TripEntry.buildTripById(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnknownFormatConversionException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (match) {
            case STANDARD_CHARGE: {
                rowsDeleted = db.delete(TripContract.StandardChargeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CLIENT: {
                rowsDeleted = db.delete(TripContract.ClientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnknownFormatConversionException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case STANDARD_CHARGE:
                try {
                    rowsUpdated = db.update(TripContract.StandardChargeEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CLIENT:
                try {
                    rowsUpdated = db.update(TripContract.ClientEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
}
