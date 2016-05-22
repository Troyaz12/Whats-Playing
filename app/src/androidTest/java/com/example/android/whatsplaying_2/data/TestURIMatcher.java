package com.example.android.whatsplaying_2.data;


import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestURIMatcher extends AndroidTestCase {
    private static final long Movie_QUERY = 1;
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final long TEST_LOCATION_ID = 10L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_WEATHER_DIR = MovieContract.MostPopularTrailers.CONTENT_URI;
    private static final Uri TEST_Movie_WITH_allTraliers_DIR = MovieContract.MostPopularTrailers.buildMostPopularTrailersUri(Movie_QUERY);
    private static final Uri TEST_Movie_WITH_MovieTrailers_DIR = MovieContract.MostPopularTrailers.buildTrailer(Movie_QUERY, Movie_QUERY);
    // content://com.example.android.sunshine.app/location"
    private static final Uri TEST_LOCATION_DIR = MovieContract.MostPopularEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MovieTrailer URI was matched incorrectly.",
                testMatcher.match(TEST_WEATHER_DIR), MovieProvider.MOST_POPULAR_MOVIES_TRAILER_TABLE);
        assertEquals("Error: The Movie WITH all Trailers URI was matched incorrectly.",
                testMatcher.match(TEST_Movie_WITH_allTraliers_DIR), MovieProvider.MOST_POPULAR_MOVIES_TRAILER_AND_REVIEWS);

        assertEquals("Error: The LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_DIR), MovieProvider.MOST_POPULAR_MOVIES);
    }
}
