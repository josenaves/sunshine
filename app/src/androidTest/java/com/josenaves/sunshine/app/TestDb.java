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

import java.util.Map;
import java.util.Set;

/**
 * Created by jnaves on 15/09/14.
 */
public class TestDb extends AndroidTestCase {

    private String LOG_TAG = TestDb.class.getSimpleName();

    private static final String TEST_CITY_NAME = "North Pole";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        WeatherDbHelper dbHelper  = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create a new map of values where column names are the keys
        ContentValues values = createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // a cursor is your primary interface to query results
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                null,  // columns projection (all)
                null,  // columns for the "where" clause
                null,  // values for the "where" clause
                null,  // columns to group by
                null,  // columns to filter by row groups
                null   // sort order
        );

        if (cursor.moveToFirst()) {

            ContentValues weatherValues = createWeatherValues(locationRowId);

            long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            // verify we got a row back
            assertTrue(weatherRowId != -1);
            Log.d(LOG_TAG, "New row id: " + weatherRowId);

            // a cursor is your primary interface to query results
            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    null,  // leaving columns null just return all the columns
                    null,  // columns for the "where" clause
                    null,  // values for the "where" clause
                    null,  // columns to group by
                    null,  // columns to filter by row groups
                    null   // sort order
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);
            }
            else {
                // That's weirds, it works on MY machine :)
                fail("No weather data returned :(");
            }

            weatherCursor.close();
        }
        else {
            // That's weirds, it works on MY machine :)
            fail("No values returned :(");
        }

        cursor.close();
    }

    public static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String,Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }


    public static ContentValues createNorthPoleLocationValues() {
        // test data we're going to insert into the DB
        String testLocationSetting = "99705";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;
    }

    public static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

}

