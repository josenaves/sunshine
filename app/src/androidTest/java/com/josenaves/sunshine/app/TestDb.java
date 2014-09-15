package com.josenaves.sunshine.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;
import android.util.Log;

import com.josenaves.sunshine.app.data.WeatherContract;
import com.josenaves.sunshine.app.data.WeatherContract.LocationEntry;
import com.josenaves.sunshine.app.data.WeatherContract.WeatherEntry;
import com.josenaves.sunshine.app.data.WeatherDbHelper;

import junit.framework.Test;

/**
 * Created by jnaves on 15/09/14.
 */
public class TestDb extends AndroidTestCase {

    private String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        // test data we're going to insert into the DB
        String testCityName = "North Pole";
        String testLocationSetting = "99705";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        WeatherDbHelper dbHelper  = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create a new map of values where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testCityName);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // specify which columns you want
        String [] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LONG
        };

        // a cursor is your primary interface to query results
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                columns,
                null,  // columns for the "where" clause
                null,  // values for the "where" clause
                null,  // columns to group by
                null,  // columns to filter by row groups
                null   // sort order
        );

        if (cursor.moveToFirst()) {
            // get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex(LocationEntry.COLUMN_CITY_NAME);
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LAT);
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LONG);
            double longitude = cursor.getDouble(longIndex);

            // Ok, data is returned ! Assert that it's the right data, and the database
            // creation code is working as intended.
            // Then take a break. We both know that wasn't easy.
            assertEquals(testCityName, name);
            assertEquals(testLocationSetting, location);

            assertEquals(testLongitude, longitude);

            assertEquals(testLatitude, latitude);

        }
        else {
            // That's weirds, it works on MY machine :)
            fail("No values returned :(");
        }

        cursor.close();

    }

}
