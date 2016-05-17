package com.example.android.whatsplaying_2.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by TroysMacBook on 3/13/16.
 */
public class MovieProvider extends ContentProvider{

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOST_POPULAR_MOVIES = 100;
    static final int MOST_POPULAR_MOVIES_TRAILER = 101;
    static final int MOST_POPULAR_MOVIES_REVIEWS = 102;
    static final int MOST_POPULAR_MOVIES_DETAIL = 300;

    static final int MOST_POPULAR_MOVIES_TRAILER_TABLE = 103;
    static final int MOST_POPULAR_MOVIES_REVIEWS_TABLE = 104;


    static final int HIGHEST_RATED_MOVIES = 105;
    static final int HIGHEST_RATED_TRAILERS = 106;
    static final int HIGHEST_RATED_REVIEWS = 107;
    static final int HIGHEST_RATED_REVIEWS_TABLE = 111;
    static final int HIGHEST_RATED_TRAILERS_TABLE = 112;
    static final int HIGHEST_RATED_MOVIES_TABLE = 113;

    static final int FAVORITE_MOVIES = 108;
    static final int FAVORITE_TRAILERS = 109;
    static final int FAVORITE_REVIEWS = 110;
    static final int FAVORITE_MOVIES_TABLE = 114;
    static final int FAVORITE_TRAILERS_TABLE = 115;
    static final int FAVORITE_REVIEWS_TABLE = 116;


    private static final SQLiteQueryBuilder MovieQueryBuilder;

    static{
        MovieQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //weather LEFT OUTER JOIN MostPopularReviews and trailers ON MostPopularTrailers.MOVIESELECTED = MostPopularTrailers._id

        MovieQueryBuilder.setTables(
                MovieContract.MostPopularEntry.TABLE_NAME +
                        " LEFT OUTER JOIN " + MovieContract.MostPopularTrailers.TABLE_NAME + " ON "
                        + MovieContract.MostPopularEntry.TABLE_NAME +
                        "." + MovieContract.MostPopularEntry._ID +
                        " = " + MovieContract.MostPopularTrailers.TABLE_NAME +
                        "." + MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER +
                        " LEFT OUTER JOIN " + MovieContract.MostPopularReviews.TABLE_NAME + " ON " +
                                MovieContract.MostPopularEntry.TABLE_NAME +
                        "." + MovieContract.MostPopularEntry._ID +
                        " = " + MovieContract.MostPopularReviews.TABLE_NAME +
                        "." + MovieContract.MostPopularReviews.MOVIE_SELECTED_REVIEWS);

    }
    private static final SQLiteQueryBuilder MovieQueryHighestRatedBuilder;

    static{
        MovieQueryHighestRatedBuilder = new SQLiteQueryBuilder();
        MovieQueryHighestRatedBuilder.setTables(
        MovieContract.HighestRatedEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + MovieContract.HighestRatedTrailers.TABLE_NAME + " ON "
                + MovieContract.HighestRatedEntry.TABLE_NAME +
                "." + MovieContract.HighestRatedEntry._ID +
                " = " + MovieContract.HighestRatedTrailers.TABLE_NAME +
                "." + MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER +
                " LEFT OUTER JOIN " + MovieContract.HighestRatedReviews.TABLE_NAME + " ON " +
                       MovieContract.HighestRatedEntry.TABLE_NAME +
                "." + MovieContract.HighestRatedEntry._ID +
                " = " + MovieContract.HighestRatedReviews.TABLE_NAME +
                "." + MovieContract.HighestRatedReviews.MOVIE_SELECTED_REVIEWS);

    }

    private static final SQLiteQueryBuilder MovieQueryFavoriteBuilder;

    static{
        MovieQueryFavoriteBuilder = new SQLiteQueryBuilder();
        MovieQueryFavoriteBuilder.setTables(
                MovieContract.FavoriteEntry.TABLE_NAME +
                        " LEFT OUTER JOIN " + MovieContract.FavoriteTrailers.TABLE_NAME + " ON "
                        + MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.MOVIE_ID +
                        " = " + MovieContract.FavoriteTrailers.TABLE_NAME +
                        "." + MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER +
                        " LEFT OUTER JOIN " + MovieContract.FavoriteReviews.TABLE_NAME + " ON " +
                        MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.MOVIE_ID +
                        " = " + MovieContract.FavoriteReviews.TABLE_NAME +
                        "." + MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS);
    }


    private static final String MovieTrailerSelection =
            MovieContract.MostPopularEntry.TABLE_NAME +
                    "." + MovieContract.MostPopularEntry._ID + " = ? AND "+
            MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER + " = ? AND "+
            MovieContract.MostPopularReviews.MOVIE_SELECTED_REVIEWS + " = ? ";

    private static final String MovieReviewSelection =
            MovieContract.MostPopularEntry.TABLE_NAME +
                    "." + MovieContract.MostPopularEntry._ID + " = ? ";


    private static final String MovieDetails =
            MovieContract.MostPopularEntry.TABLE_NAME +
                    "." + MovieContract.MostPopularEntry._ID+ " AND "+
            MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER;

    private static final String HighestRatedMovieDetails =
            MovieContract.HighestRatedEntry.TABLE_NAME +
                    "." + MovieContract.HighestRatedEntry._ID+ " = ? ";

    private static final String HighestRatedTrailerSelection =
            MovieContract.HighestRatedEntry.TABLE_NAME +
                    "." + MovieContract.HighestRatedEntry._ID + " = ? AND "+
                    MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER + " = ? AND "+
                    MovieContract.HighestRatedReviews.MOVIE_SELECTED_REVIEWS + " = ? ";

    private static final String HighestRatedReviewSelection =
            MovieContract.HighestRatedEntry.TABLE_NAME +
                    "." + MovieContract.HighestRatedEntry._ID + " = ? ";


    private static final String FavoriteMovieDetails =
            MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry._ID+ " = ? ";

    private static final String FavoriteTrailerSelection =
            MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry._ID + " = ? AND "+
                    MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER + " = ? ";

    private static final String FavoriteReviewSelection =
            MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry._ID + " = ? AND "+
                    MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS + " = ? ";

    private static final String RemoveFavoriteMovieDetails =
            MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry.MOVIE_ID+ " = ? ";

    private static final String RemoveFavoriteTrailerSelection =
            MovieContract.FavoriteTrailers.TABLE_NAME +
                    "." + MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER + " = ? ";

    private static final String RemoveFavoriteReviewSelection =
            MovieContract.FavoriteReviews.TABLE_NAME +
                    "." + MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS + " = ? ";




    private Cursor deleteMovieData(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.MostPopularEntry.getMostPopularMovieFromUri(uri);


        return MovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                MovieDetails,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }

    private Cursor getMostPopularMovieDetails(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.MostPopularEntry.getMostPopularMovieFromUri(uri);


        return MovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                MovieDetails,               //rows wanted
                null,               //selection args
                null,               //group by
                null,               //row included in cursor
                sortOrder                //order by
        );
    }

    private Cursor getMostPopularMovieTrailer(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.MostPopularTrailers.getMovieFromUri(uri);
        String MovieSelected2 = MovieContract.MostPopularTrailers.getMovieTrailerFromUri(uri);
        System.out.println("URI: "+uri.toString() +"MovieSelected: "+MovieSelected + "MovieSelectedTrailer: "+MovieSelected2);
        System.out.println("query: "+ MovieTrailerSelection);

        return MovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                MovieTrailerSelection,               //rows wanted
                new String[]{MovieSelected, MovieSelected2,MovieSelected2},               //selection args
                null,               //group by
                null,               //row included in cursor
                sortOrder                //order by
        );
    }
    private Cursor getMostPopularMovieReview(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.MostPopularReviews.getMovieReviewsFromUri(uri);


        return MovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                MovieReviewSelection,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }
    private Cursor getHighestRatedMovieDetails(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.HighestRatedEntry.getHighestRatedMovieFromUri(uri);


        return MovieQueryHighestRatedBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                HighestRatedMovieDetails,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }

    private Cursor getHighestRatedTrailer(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.HighestRatedTrailers.getMovieFromUri(uri);
        String MovieSelected2 = MovieContract.HighestRatedTrailers.getMovieTrailerFromUri(uri);
       // System.out.println("URI: "+uri.toString() +"MovieSelected: "+MovieSelected + "MovieSelectedTrailer: "+MovieSelected2);
        System.out.println("query: "+ MovieTrailerSelection);

        return MovieQueryHighestRatedBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                HighestRatedTrailerSelection,               //rows wanted
                new String[]{MovieSelected,MovieSelected2,MovieSelected2},               //selection args
                null,               //group by
                null,               //row included in cursor
                sortOrder                //order by
        );
    }
    private Cursor getHighestRatedReview(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.HighestRatedReviews.getMovieHighestReviewsFromUri(uri);


        return MovieQueryHighestRatedBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                HighestRatedReviewSelection,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }

    private Cursor getFavoriteMovieDetails(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.FavoriteEntry.getFavoriteUri(uri);


        return MovieQueryFavoriteBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                FavoriteMovieDetails,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }
    private Cursor getFavoriteTrailer(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.FavoriteTrailers.getFavoriteFromUri(uri);
        String MovieSelected2 = MovieContract.FavoriteTrailers.getFavoriteTrailerFromUri(uri);
        // System.out.println("URI: "+uri.toString() +"MovieSelected: "+MovieSelected + "MovieSelectedTrailer: "+MovieSelected2);
        System.out.println("query: "+ MovieTrailerSelection);

        return MovieQueryFavoriteBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                FavoriteTrailerSelection,               //rows wanted
                new String[]{MovieSelected,MovieSelected2},               //selection args
                null,               //group by
                null,               //row included in cursor
                sortOrder                //order by
        );
    }
    private Cursor getFavoriteReview(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.FavoriteReviews.getMovieFavoriteReviewsFromUri(uri);


        return MovieQueryFavoriteBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                FavoriteReviewSelection,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }
    private Cursor removeFavoriteMovieDetails(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.FavoriteEntry.getFavoriteUri(uri);


        return MovieQueryFavoriteBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                RemoveFavoriteMovieDetails,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }
    private Cursor removeFavoriteTrailer(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.FavoriteTrailers.getFavoriteFromUri(uri);
        String MovieSelected2 = MovieContract.FavoriteTrailers.getFavoriteTrailerFromUri(uri);
        // System.out.println("URI: "+uri.toString() +"MovieSelected: "+MovieSelected + "MovieSelectedTrailer: "+MovieSelected2);
        System.out.println("query: "+ MovieTrailerSelection);

        return MovieQueryFavoriteBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                RemoveFavoriteTrailerSelection,               //rows wanted
                new String[]{MovieSelected,MovieSelected2},               //selection args
                null,               //group by
                null,               //row included in cursor
                sortOrder                //order by
        );
    }
    private Cursor removeFavoriteReview(Uri uri, String[] projection, String sortOrder) {

        String MovieSelected = MovieContract.FavoriteReviews.getMovieFavoriteReviewsFromUri(uri);


        return MovieQueryFavoriteBuilder.query(mOpenHelper.getReadableDatabase(),  //table
                projection,         //columns to return
                RemoveFavoriteReviewSelection,               //rows wanted
                new String[]{MovieSelected},               //selection args
                null,               //group by
                null,               //row included in cursor
                null                //order by
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR, MOST_POPULAR_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR+"/*", MOST_POPULAR_MOVIES_DETAIL);

        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR_REVIEWS, MOST_POPULAR_MOVIES_REVIEWS_TABLE);
        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR_REVIEWS + "/*", MOST_POPULAR_MOVIES_REVIEWS );

        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR_TRAILERS, MOST_POPULAR_MOVIES_TRAILER_TABLE);
        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR_TRAILERS + "/*", MOST_POPULAR_MOVIES_TRAILER );


        matcher.addURI(authority, MovieContract.PATH_HIGHEST_RATED, HIGHEST_RATED_MOVIES_TABLE);
        matcher.addURI(authority, MovieContract.PATH_HIGHEST_RATED + "/*", HIGHEST_RATED_MOVIES );

        matcher.addURI(authority, MovieContract.PATH_HIGHEST_RATED_REVIEWS, HIGHEST_RATED_REVIEWS_TABLE);
        matcher.addURI(authority, MovieContract.PATH_HIGHEST_RATED_REVIEWS + "/*", HIGHEST_RATED_REVIEWS );

        matcher.addURI(authority, MovieContract.PATH_HIGHEST_RATED_TRAILERS, HIGHEST_RATED_TRAILERS_TABLE);
        matcher.addURI(authority, MovieContract.PATH_HIGHEST_RATED_TRAILERS + "/*", HIGHEST_RATED_TRAILERS );

        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE_MOVIES_TABLE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/*", FAVORITE_MOVIES );

        matcher.addURI(authority, MovieContract.PATH_FAVORITE_REVIEWS, FAVORITE_REVIEWS_TABLE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE_REVIEWS + "/*", FAVORITE_REVIEWS );

        matcher.addURI(authority, MovieContract.PATH_FAVORITE_TRAILERS, FAVORITE_TRAILERS_TABLE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE_TRAILERS + "/*", FAVORITE_TRAILERS );


        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        System.out.println("getType URI: " +uri + "match: "+ match);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOST_POPULAR_MOVIES:
                return MovieContract.MostPopularEntry.CONTENT_TYPE;
            case MOST_POPULAR_MOVIES_TRAILER:
                return MovieContract.MostPopularTrailers.CONTENT_TYPE;
            case MOST_POPULAR_MOVIES_REVIEWS:
                return MovieContract.MostPopularReviews.CONTENT_TYPE;
            case MOST_POPULAR_MOVIES_DETAIL:
                return MovieContract.MostPopularEntry.CONTENT_ITEM_TYPE;
            case MOST_POPULAR_MOVIES_TRAILER_TABLE:
                return MovieContract.MostPopularTrailers.CONTENT_TYPE;
            case MOST_POPULAR_MOVIES_REVIEWS_TABLE:
                return MovieContract.MostPopularReviews.CONTENT_TYPE;
            case HIGHEST_RATED_MOVIES:
                return MovieContract.HighestRatedEntry.CONTENT_ITEM_TYPE;
            case HIGHEST_RATED_REVIEWS:
                return MovieContract.HighestRatedReviews.CONTENT_TYPE;
            case HIGHEST_RATED_TRAILERS:
                return MovieContract.HighestRatedTrailers.CONTENT_TYPE;
            case HIGHEST_RATED_MOVIES_TABLE:
                return MovieContract.HighestRatedEntry.CONTENT_TYPE;
            case HIGHEST_RATED_REVIEWS_TABLE:
                return MovieContract.HighestRatedReviews.CONTENT_TYPE;
            case HIGHEST_RATED_TRAILERS_TABLE:
                return MovieContract.HighestRatedTrailers.CONTENT_TYPE;
            case FAVORITE_MOVIES:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            case FAVORITE_REVIEWS:
                return MovieContract.FavoriteReviews.CONTENT_TYPE;
            case FAVORITE_TRAILERS:
                return MovieContract.FavoriteTrailers.CONTENT_TYPE;
            case FAVORITE_MOVIES_TABLE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_REVIEWS_TABLE:
                return MovieContract.FavoriteReviews.CONTENT_TYPE;
            case FAVORITE_TRAILERS_TABLE:
                return MovieContract.FavoriteTrailers.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;

        System.out.println("Cursor Query: "+sUriMatcher.match(uri) + "   This is the URI: "+uri);

        switch (sUriMatcher.match(uri)) {

            case MOST_POPULAR_MOVIES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                System.out.println("Query" + "MOST_POPULAR_MOVIES");
                break;
            }
            // movie details
            case MOST_POPULAR_MOVIES_DETAIL: {
                retCursor = getMostPopularMovieDetails(uri, projection, sortOrder);
                System.out.println("Query"+ "MOST_POPULAR_MOVIES_DETAIL");

                break;
            }
            // "movie trailers"
            case MOST_POPULAR_MOVIES_TRAILER_TABLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MostPopularTrailers.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                System.out.println("Query"+ "MOST_POPULAR_MOVIES_TRAILER_TABLE");

                break;
            }
            case MOST_POPULAR_MOVIES_TRAILER: {
                retCursor = getMostPopularMovieTrailer(uri, projection, sortOrder);
                System.out.println("Query"+ "MOST_POPULAR_MOVIES_TRAILER");

                break;
            }
            // "reviews"
            case MOST_POPULAR_MOVIES_REVIEWS_TABLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MostPopularReviews.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                System.out.println("Query"+ "MOST_POPULAR_MOVIES_REVIEWS_TABLE");

                break;
            }
            case MOST_POPULAR_MOVIES_REVIEWS: {
                retCursor = getMostPopularMovieReview(uri, projection, sortOrder);
                System.out.println("Query"+ "MOST_POPULAR_MOVIES_REVIEWS");

                break;
            }
            case HIGHEST_RATED_MOVIES:
            {
                retCursor = getHighestRatedMovieDetails(uri, projection, sortOrder);
                System.out.println("Query"+ "HIGHEST_RATED_MOVIES");

                break;
            }

            case HIGHEST_RATED_MOVIES_TABLE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.HighestRatedEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                System.out.println("Query"+ "HIGHEST_RATED_MOVIES_TABLE");

                break;
            }
            case HIGHEST_RATED_REVIEWS: {
                retCursor = getHighestRatedReview(uri, projection, sortOrder);
                System.out.println("Query"+ "HIGHEST_RATED_REVIEWS");

                break;
            }
            case HIGHEST_RATED_REVIEWS_TABLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.HighestRatedReviews.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                System.out.println("Query"+ "HIGHEST_RATED_REVIEWS_TABLE");

                break;
            }
            case HIGHEST_RATED_TRAILERS: {
                retCursor = getHighestRatedTrailer(uri, projection, sortOrder);
                System.out.println("Query"+ "HIGHEST_RATED_TRAILERS");

                break;
            }
            case FAVORITE_MOVIES:
            {
                retCursor = getFavoriteMovieDetails(uri, projection, sortOrder);
                System.out.println("Query"+ "FAVORITE_MOVIES");

                break;
            }
            case FAVORITE_MOVIES_TABLE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                System.out.println("Query"+ "FAVORITE_MOVIES_TABLE");

                break;
            }
            case FAVORITE_REVIEWS: {
                retCursor = getFavoriteReview(uri, projection, sortOrder);
                System.out.println("Query"+ "FAVORITE_REVIEWS");

                break;
            }
            case FAVORITE_TRAILERS: {
                retCursor = getFavoriteTrailer(uri, projection, sortOrder);
                System.out.println("Query"+ "FAVORITE_TRAILERS");

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        insert movies
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {


        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOST_POPULAR_MOVIES: {
                long _id = db.insert(MovieContract.MostPopularEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MostPopularEntry.buildPopularUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOST_POPULAR_MOVIES_TRAILER_TABLE: {
                long _id = db.insert(MovieContract.MostPopularTrailers.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MostPopularTrailers.buildMostPopularTrailersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOST_POPULAR_MOVIES_REVIEWS_TABLE: {
                long _id = db.insert(MovieContract.MostPopularReviews.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MostPopularReviews.buildMostPopularReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case HIGHEST_RATED_MOVIES_TABLE: {
                long _id = db.insert(MovieContract.HighestRatedEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.HighestRatedEntry.buildHighestRatedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case HIGHEST_RATED_TRAILERS_TABLE: {
                long _id = db.insert(MovieContract.HighestRatedTrailers.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.HighestRatedTrailers.buildhighestRatedTrailersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case HIGHEST_RATED_REVIEWS_TABLE: {
                long _id = db.insert(MovieContract.HighestRatedReviews.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.HighestRatedReviews.buildHighestRatedReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_MOVIES_TABLE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_TRAILERS_TABLE: {
                long _id = db.insert(MovieContract.FavoriteTrailers.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteTrailers.FavoriteTrailersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_REVIEWS_TABLE: {
                long _id = db.insert(MovieContract.FavoriteReviews.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteReviews.buildFavoriteReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted=0;
        // this makes delete all rows return the number of rows deleted
     //   if ( null == selection ) selection = "1";
        switch (match) {
            case MOST_POPULAR_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MostPopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOST_POPULAR_MOVIES_TRAILER_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.MostPopularTrailers.TABLE_NAME, selection, selectionArgs);
                break;
            case MOST_POPULAR_MOVIES_REVIEWS_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.MostPopularReviews.TABLE_NAME, selection, selectionArgs);
                break;
            case HIGHEST_RATED_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.HighestRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HIGHEST_RATED_MOVIES_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.HighestRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HIGHEST_RATED_TRAILERS_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.HighestRatedTrailers.TABLE_NAME, selection, selectionArgs);
                break;
            case HIGHEST_RATED_REVIEWS_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.HighestRatedReviews.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIES_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                System.out.println("Delete executed: "+ selection+ " "+selectionArgs);

                break;
            case FAVORITE_TRAILERS_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteTrailers.TABLE_NAME, selection, selectionArgs);
                System.out.println("Delete executed: "+ selection+ " "+selectionArgs);

                break;
            case FAVORITE_REVIEWS_TABLE:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteReviews.TABLE_NAME, selection, selectionArgs);
                System.out.println("Delete executed: "+ selection+ " "+selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOST_POPULAR_MOVIES:
                rowsUpdated = db.update(MovieContract.MostPopularEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOST_POPULAR_MOVIES_TRAILER:
                rowsUpdated = db.update(MovieContract.MostPopularTrailers.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOST_POPULAR_MOVIES_REVIEWS:
                rowsUpdated = db.update(MovieContract.MostPopularReviews.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case HIGHEST_RATED_MOVIES:
                rowsUpdated = db.update(MovieContract.HighestRatedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int returnCount;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOST_POPULAR_MOVIES_TRAILER_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MostPopularTrailers.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOST_POPULAR_MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MostPopularEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOST_POPULAR_MOVIES_REVIEWS_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MostPopularReviews.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case HIGHEST_RATED_MOVIES_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.HighestRatedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case HIGHEST_RATED_REVIEWS_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.HighestRatedReviews.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case HIGHEST_RATED_TRAILERS_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.HighestRatedTrailers.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FAVORITE_MOVIES_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FAVORITE_REVIEWS_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavoriteReviews.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FAVORITE_TRAILERS_TABLE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavoriteTrailers.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
