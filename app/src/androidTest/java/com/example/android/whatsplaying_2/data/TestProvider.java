package com.example.android.whatsplaying_2.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.whatsplaying_2.data.MovieContract.MostPopularEntry;
import com.example.android.whatsplaying_2.data.MovieContract.MostPopularTrailers;




/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.

    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {


        mContext.getContentResolver().delete(
                MovieContract.MostPopularEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.MostPopularReviews.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.MostPopularTrailers.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                MostPopularTrailers.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Most popular Trailers table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.MostPopularReviews.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from most popular reviews table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MostPopularEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from most popular entry table during delete", 0, cursor.getCount());
        cursor.close();



    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();


        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
      public void testGetType() {


        // content://com.example.android.sunshine.app/popular/
        String type = mContext.getContentResolver().getType(MostPopularEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the MostPopularEntry CONTENT_URI should return MostPopularEntry.CONTENT_TYPE",
                MostPopularEntry.CONTENT_TYPE, type);



        int testMovie = 1;
        // content://com.example.android.sunshine.app/popular/1
        String type1 = mContext.getContentResolver().getType(
                MostPopularEntry.buildPopularUri(testMovie));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/popular
        assertEquals("Error: the PopularMovieEntry CONTENT_URI should return PopularMovieEntry.CONTENT_Type",
                MostPopularEntry.CONTENT_ITEM_TYPE, type1);


        // content://com.example.android.sunshine.app/MostPopularTrailers/
        String type2 = mContext.getContentResolver().getType(MostPopularTrailers.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the Most Popular trailers CONTENT_URI should return MostPopularTrailers.CONTENT_TYPE",
                MostPopularTrailers.CONTENT_TYPE, type2);

          // content://com.example.android.sunshine.app/MostPopularReviews/
         String type3 = mContext.getContentResolver().getType(MovieContract.MostPopularReviews.CONTENT_URI);
          // vnd.android.cursor.dir/com.example.android.sunshine.app/location
          assertEquals("Error: the Most popular reviews CONTENT_URI should return MostPopularReviews.CONTENT_TYPE",
                  MovieContract.MostPopularReviews.CONTENT_TYPE, type3);


    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */


    public void testBasicMovieTrailerAndReviewQuery() {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //ContentValues testValues = TestUtilities.createTrailers();

        //long locationRowId = TestUtilities.insertTrailersValues(mContext);

        long locationRowId = TestUtilities.createPopularMovieValues(mContext);

      //  ContentValues testMovieValues = TestUtilities.returnPopularMoviesValues();

        //add trailer values
        ContentValues testValues = TestUtilities.createMovieTrailers(locationRowId);

        System.out.println("ContentValues: "+testValues.toString());



        long MovieRowId = db.insert(MostPopularTrailers.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert Most Popular Trailers into the Database", MovieRowId != -1);

        //add trailer values
        ContentValues testValuesreviews = TestUtilities.createMovieReviews(locationRowId);
        long MovieRowIdreviews = db.insert(MovieContract.MostPopularReviews.TABLE_NAME, null, testValuesreviews);

        assertTrue("Unable to Insert Most Popular Reviews into the Database", MovieRowIdreviews != -1);



        db.close();

        // Test the basic content provider query
        Cursor MovieCursor = mContext.getContentResolver().query(
                MostPopularTrailers.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", MovieCursor, testValues);



        Cursor MovieCursorreview = mContext.getContentResolver().query(
                MovieContract.MostPopularReviews.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", MovieCursorreview, testValuesreviews);


    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */

    public void testBasicMovieQueries() {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //ContentValues testValues = TestUtilities.createTrailers();

        //long locationRowId = TestUtilities.insertTrailersValues(mContext);

        long locationRowId = TestUtilities.createPopularMovieValues(mContext);

        ContentValues testValues = TestUtilities.createMovieEntry();

        // Test the basic content provider query
        Cursor locationCursor = mContext.getContentResolver().query(
                MostPopularEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLocationQueries, Popular movie query", locationCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    locationCursor.getNotificationUri(), MostPopularEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */

    public void testUpdatePopularMovieEntry() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieEntry();

        Uri locationUri = mContext.getContentResolver().
                insert(MostPopularEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MostPopularEntry._ID, locationRowId);
        updatedValues.put(MostPopularEntry.IMAGE, "santa movie poster");
        updatedValues.put(MostPopularEntry.MOVIE_TITLE, "Santa's Village");
        updatedValues.put(MostPopularEntry.OVERVIEW, "Santa's Village overview");


        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MostPopularEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.TestContentObserver.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MostPopularEntry.CONTENT_URI, updatedValues, MostPopularEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MostPopularEntry.CONTENT_URI,
                null,   // projection
                MostPopularEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testPopularMovieEntry.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }
    /*
    public void testUpdatePopularMovieTrailers() {
        // Create a new map of values, where column names are the keys
        ContentValues valuesTrailers = TestUtilities.createTrailers();

        Uri locationUri = mContext.getContentResolver().
                insert(MostPopularTrailers.CONTENT_URI, valuesTrailers);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(valuesTrailers);
        updatedValues.put(MostPopularTrailers.MOVIE_ID, locationRowId);
        updatedValues.put(MostPopularTrailers.TRAILERS, "santa movie trailer");



        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MostPopularTrailers.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.TestContentObserver.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MostPopularTrailers.CONTENT_URI, updatedValues, MostPopularTrailers.MOVIE_ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursorTrailers = mContext.getContentResolver().query(
                MostPopularTrailers.CONTENT_URI,
                null,   // projection
                MostPopularTrailers.MOVIE_ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testMovieTrailer.  Error validating location entry update.",
                cursorTrailers, updatedValues);

        cursorTrailers.close();
    }   */
    /*
    public void testUpdatePopularMovieReviews() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieReviews();

        Uri locationUri = mContext.getContentResolver().
                insert(MovieContract.MostPopularReviews.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MostPopularReviews.MOVIE_ID, locationRowId);
        updatedValues.put(MovieContract.MostPopularReviews.REVIEWS, "santa movie trailer");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MovieContract.MostPopularReviews.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.TestContentObserver.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MostPopularReviews.CONTENT_URI, updatedValues, MovieContract.MostPopularReviews.MOVIE_ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MostPopularReviews.CONTENT_URI,
                null,   // projection
                MovieContract.MostPopularReviews.MOVIE_ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testMovieReview.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }  */

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.



        public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieEntry();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MostPopularEntry.CONTENT_URI, testValues);

         System.out.println("URI: new"+locationUri.toString());
        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);


        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MostPopularEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntryEntry.",
                cursor, testValues);

        // create movie trailer
        ContentValues weatherValues = TestUtilities.createMovieTrailers(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.TestContentObserver.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MostPopularTrailers.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(MostPopularTrailers.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);

        System.out.println("URI: "+weatherInsertUri.toString());

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor MovieTrailerCursor = mContext.getContentResolver().query(
                MostPopularTrailers.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MostPopularMovieTrailer insert.",
                MovieTrailerCursor, weatherValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        weatherValues.putAll(testValues);
        System.out.println("Movie Values: " + weatherValues.toString());
        System.out.println("Query: "+ MostPopularTrailers.buildTrailer(locationRowId,locationRowId));

        // Get the joined MostPopularEntry and MostPopularTrailer data
         Cursor MovieEntryAndMovieTrailerCursor = mContext.getContentResolver().query(
                MostPopularTrailers.buildTrailer(locationRowId,locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Movie Entry and Movie Trailer Data.",
                MovieEntryAndMovieTrailerCursor, weatherValues);

                //test movie review

            // create movie Review
            ContentValues MovieReviews = TestUtilities.createMovieReviews(locationRowId);
            // The TestContentObserver is a one-shot class
            tco = TestUtilities.TestContentObserver.getTestContentObserver();

            mContext.getContentResolver().registerContentObserver(MovieContract.MostPopularReviews.CONTENT_URI, true, tco);

            Uri MovieReviewInsertUri = mContext.getContentResolver()
                    .insert(MovieContract.MostPopularReviews.CONTENT_URI, MovieReviews);
            assertTrue(MovieReviewInsertUri != null);

            // Did our content observer get called?  Students:  If this fails, your insert weather
            // in your ContentProvider isn't calling
            // getContext().getContentResolver().notifyChange(uri, null);
            tco.waitForNotificationOrFail();
            mContext.getContentResolver().unregisterContentObserver(tco);

            // A cursor is your primary interface to the query results.
            Cursor MovieReviewCursor = mContext.getContentResolver().query(
                    MovieContract.MostPopularReviews.CONTENT_URI,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null // columns to group by
            );

            TestUtilities.validateCursor("testInsertReadProvider. Error validating MostPopularMovieTrailer insert.",
                    MovieReviewCursor, MovieReviews);

            // Add the location values in with the weather data so that we can make
            // sure that the join worked and we actually get all the values back
            MovieReviews.putAll(testValues);
            System.out.println("Movie Values: " + weatherValues.toString());
            System.out.println("Query: "+ MovieContract.MostPopularReviews.buildReview(locationRowId, locationRowId));

            // Get the joined MostPopularEntry and MostPopularTrailer data
            Cursor MovieEntryAndMovieReviewCursor = mContext.getContentResolver().query(
                    MovieContract.MostPopularReviews.buildReview(locationRowId, locationRowId),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Movie Entry and Movie Trailer Data.",
                    MovieEntryAndMovieReviewCursor, MovieReviews);








    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our popular movies delete.
        TestUtilities.TestContentObserver MovieEntryObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularEntry.CONTENT_URI, true, MovieEntryObserver);

        // Register a content observer for our trailers delete.
        TestUtilities.TestContentObserver MovieTrailerObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularTrailers.CONTENT_URI, true, MovieTrailerObserver);

        // Register a content observer for our trailers delete.
        TestUtilities.TestContentObserver MovieReviewObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularTrailers.CONTENT_URI, true, MovieReviewObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        MovieEntryObserver.waitForNotificationOrFail();
        MovieTrailerObserver.waitForNotificationOrFail();
        MovieReviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(MovieEntryObserver);
        mContext.getContentResolver().unregisterContentObserver(MovieTrailerObserver);
        mContext.getContentResolver().unregisterContentObserver(MovieReviewObserver);


    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieValues(long locationRowId) {
        String testPath = TestUtilities.key;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(MostPopularTrailers.MOVIE_SELECTED_TRAILER, locationRowId);
            weatherValues.put(MostPopularTrailers.TRAILER_KEY, testPath);


            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createMovieEntry();
        Uri locationUri = mContext.getContentResolver().insert(MostPopularEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MostPopularEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues(locationRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MostPopularTrailers.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MostPopularTrailers.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MostPopularTrailers.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
