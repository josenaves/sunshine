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

            // Fantastic. Now that we have a location, add some weather !
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
                // get the value in each column by finding the appropriate column index.

                int dateIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
                String date = weatherCursor.getString(dateIndex);

                int descIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
                String description = weatherCursor.getString(descIndex);

                int locationKeyIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_LOC_KEY);
                int locationKey = weatherCursor.getInt(locationKeyIndex);

                int maxIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
                int max = weatherCursor.getInt(maxIndex);

                int minIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
                int min = weatherCursor.getInt(minIndex);

                int wIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
                int weatherId = weatherCursor.getInt(wIndex);

                int degreesIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES);
                double degrees = weatherCursor.getDouble(degreesIndex);

                int humidityIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
                double humidity = weatherCursor.getDouble(humidityIndex);

                int pressureIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
                double pressure = weatherCursor.getDouble(pressureIndex);

                int windIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);
                double wind = weatherCursor.getDouble(windIndex);

                // Ok, data is returned ! Assert that it's the right data, and the database
                // creation code is working as intended.
                // Then take a break. We both know that wasn't easy.
                assertEquals(locationKey, locationRowId);

                assertEquals(weatherId, 321);
                assertEquals(min, 65);
                assertEquals(max, 75);

                assertEquals(date, "20141205");
                assertEquals(description, "Asteroids");

                assertEquals(degrees, 1.1);
                assertEquals(humidity, 1.2);
                assertEquals(pressure, 1.3);
                assertEquals(wind, 5.5);

            }
            else {
                // That's weirds, it works on MY machine :)
                fail("No values returned :(");
            }

            weatherCursor.close();
        }
        else {
            // That's weirds, it works on MY machine :)
            fail("No values returned :(");
        }

        cursor.close();


    }

}

