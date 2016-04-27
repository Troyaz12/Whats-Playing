package com.example.android.whatsplaying_2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import com.example.android.whatsplaying_2.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by TroysMacBook on 3/9/16.
 */
public class TestUtilities extends AndroidTestCase {

    static final String key = "trailer path";

    static ContentValues createMovieEntry() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();

        //insert values into MostPopularEntry table
        testValues.put(MovieContract.MostPopularEntry.MOVIE_ID, "1");
        testValues.put(MovieContract.MostPopularEntry.IMAGE, "image");
        testValues.put(MovieContract.MostPopularEntry.MOVIE_TITLE, "title");
        testValues.put(MovieContract.MostPopularEntry.RELEASE_DATE, "release date");
        testValues.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, "Vote Average");
        testValues.put(MovieContract.MostPopularEntry.OVERVIEW, "Overview");

        return testValues;
    }
    static ContentValues createMovieTrailers(long row) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();

        //insert values into MostPopularEntry table
        testValues.put(MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER,row);
        testValues.put(MovieContract.MostPopularTrailers.TRAILER_KEY, "TRAILER PATH GOES HERE");

        return testValues;
    }

    static ContentValues createMovieReviews(long row) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();

        //insert values into MostPopularEntry table
        testValues.put(MovieContract.MostPopularReviews.MOVIE_SELECTED_REVIEWS,row);
        testValues.put(MovieContract.MostPopularReviews.AUTHOR,"Author");
        testValues.put(MovieContract.MostPopularReviews.REVIEWS, "a review goes here");

        return testValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();



        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long createPopularMovieValues(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues MovieValues = new ContentValues();
        MovieValues.put(MovieContract.MostPopularEntry.MOVIE_ID, "1");
        MovieValues.put(MovieContract.MostPopularEntry.IMAGE, "image");
        MovieValues.put(MovieContract.MostPopularEntry.MOVIE_TITLE, "title");
        MovieValues.put(MovieContract.MostPopularEntry.RELEASE_DATE, "release date");
        MovieValues.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, "Vote Average");
        MovieValues.put(MovieContract.MostPopularEntry.OVERVIEW, "Overview");

        long locationRowId;
        locationRowId = db.insert(MovieContract.MostPopularEntry.TABLE_NAME, null, MovieValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", locationRowId != -1);


        return locationRowId;
    }

    static ContentValues returnPopularMoviesValues (){
        ContentValues MovieValues = new ContentValues();
        MovieValues.put(MovieContract.MostPopularEntry.MOVIE_ID, "1");
        MovieValues.put(MovieContract.MostPopularEntry.IMAGE, "image");
        MovieValues.put(MovieContract.MostPopularEntry.MOVIE_TITLE, "title");
        MovieValues.put(MovieContract.MostPopularEntry.RELEASE_DATE, "release date");
        MovieValues.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, "Vote Average");
        MovieValues.put(MovieContract.MostPopularEntry.OVERVIEW, "Overview");


        return MovieValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */

    /*
    static ContentValues createTrailers(long row) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MostPopularTrailers.MOVIE_ID,row);
        testValues.put(MovieContract.MostPopularTrailers.TRAILERS, "trailer path");


        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    /*
    static ContentValues insertTrailersValues(long locationRowId) {
        // insert our test records into the database


      //  long locationRowId;
      //  locationRowId = db.insert(MovieContract.MostPopularTrailers.TABLE_NAME, null, testValues);
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MostPopularTrailers.MOVIE_ID,locationRowId);
        testValues.put(MovieContract.MostPopularTrailers.TRAILERS, "TRAILER PATH GOES HERE");



        return testValues;
    }


    static ContentValues insertReviewValues(long locationRowId) {
        // insert our test records into the database


        //  long locationRowId;
        //  locationRowId = db.insert(MovieContract.MostPopularTrailers.TABLE_NAME, null, testValues);
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MostPopularReviews.MOVIE_ID,locationRowId);
        testValues.put(MovieContract.MostPopularReviews.REVIEWS, "review path");



        return testValues;
    }
*/
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }









}
