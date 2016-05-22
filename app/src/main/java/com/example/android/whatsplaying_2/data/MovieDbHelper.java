package com.example.android.whatsplaying_2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.whatsplaying_2.data.MovieContract.MostPopularEntry;

import com.example.android.whatsplaying_2.data.MovieContract.MostPopularTrailers;

import com.example.android.whatsplaying_2.data.MovieContract.MostPopularReviews;
/**
 * Created by TroysMacBook on 3/8/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 9;

    static final String DATABASE_NAME = "Movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOST_POPULAR_TABLE = "CREATE TABLE " + MostPopularEntry.TABLE_NAME + " (" +
                MostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MostPopularEntry.MOVIE_ID + " TEXT NOT NULL, " +
                MostPopularEntry.IMAGE + " TEXT, " +
                MostPopularEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MostPopularEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MostPopularEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                MostPopularEntry.OVERVIEW + " TEXT NOT NULL " +
           //     MostPopularEntry.SORT_ORDER + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_MOST_POPULAR_TABLE_TRAILERS = "CREATE TABLE " + MostPopularTrailers.TABLE_NAME + " (" +
                MostPopularTrailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MostPopularTrailers.MOVIE_SELECTED_TRAILER + " INTEGER NOT NULL, " +
                MostPopularTrailers.TRAILER_KEY + " TEXT NOT NULL, " +
                // Set up foreign key from mostpopularentry table to mostpopulartrailers.
                " FOREIGN KEY (" + MostPopularTrailers.MOVIE_SELECTED_TRAILER + ") REFERENCES " +
                MostPopularEntry.TABLE_NAME + " (" + MostPopularEntry._ID + ") " +

                " );";

        final String SQL_CREATE_MOST_POPULAR_REVIEWS = "CREATE TABLE " + MostPopularReviews.TABLE_NAME + " (" +
                MostPopularReviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MostPopularReviews.MOVIE_SELECTED_REVIEWS + " INTEGER NOT NULL, " +
                MostPopularReviews.AUTHOR + " TEXT NOT NULL, " +
                MostPopularReviews.REVIEWS + " TEXT NOT NULL, " +
                // Set up foreign key from mostpopularentry table to mostpopularmoviereviews.
                " FOREIGN KEY (" + MostPopularReviews.MOVIE_SELECTED_REVIEWS + ") REFERENCES " +
                MostPopularEntry.TABLE_NAME + " (" + MostPopularEntry._ID + ") " +
                " );";

       //         sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE);
       //         sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE_TRAILERS);
       //         sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_REVIEWS);

        final String SQL_CREATE_HIGHEST_RATED_ENTRY = "CREATE TABLE " + MovieContract.HighestRatedEntry.TABLE_NAME + " (" +
                MovieContract.HighestRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.HighestRatedEntry.MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.HighestRatedEntry.IMAGE + " TEXT, " +
                MovieContract.HighestRatedEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.HighestRatedEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.HighestRatedEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.HighestRatedEntry.OVERVIEW + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_HIGHEST_RATED_TRAILERS = "CREATE TABLE " + MovieContract.HighestRatedTrailers.TABLE_NAME + " (" +
                MovieContract.HighestRatedTrailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER + " INTEGER NOT NULL, " +
                MovieContract.HighestRatedTrailers.TRAILER_KEY + " TEXT NOT NULL, " +
                // Set up foreign key from mostpopularentry table to mostpopulartrailers.
                " FOREIGN KEY (" + MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER + ") REFERENCES " +
                MovieContract.HighestRatedEntry.TABLE_NAME + " (" + MovieContract.HighestRatedEntry._ID + ") " +

                " );";

        final String SQL_CREATE_HIGHEST_RATED_REVIEWS = "CREATE TABLE " + MovieContract.HighestRatedReviews.TABLE_NAME + " (" +
                MovieContract.HighestRatedReviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.HighestRatedReviews.MOVIE_SELECTED_REVIEWS + " INTEGER NOT NULL, " +
                MovieContract.HighestRatedReviews.AUTHOR + " TEXT NOT NULL, " +
                MovieContract.HighestRatedReviews.REVIEWS + " TEXT NOT NULL, " +
                // Set up foreign key from mostpopularentry table to mostpopularmoviereviews.
                " FOREIGN KEY (" + MovieContract.HighestRatedReviews.MOVIE_SELECTED_REVIEWS + ") REFERENCES " +
                MovieContract.HighestRatedEntry.TABLE_NAME + " (" + MovieContract.HighestRatedEntry._ID + ") " +
                " );";

        final String SQL_CREATE_FAVORITE_ENTRY = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.FavoriteEntry.MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.IMAGE + " TEXT, " +
                MovieContract.FavoriteEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.OVERVIEW + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_FAVORITE_TRAILERS = "CREATE TABLE " + MovieContract.FavoriteTrailers.TABLE_NAME + " (" +
                MovieContract.FavoriteTrailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER + " INTEGER NOT NULL, " +
                MovieContract.FavoriteTrailers.TRAILER_KEY + " TEXT NOT NULL, " +
                // Set up foreign key from mostpopularentry table to mostpopulartrailers.
                " FOREIGN KEY (" + MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER + ") REFERENCES " +
                MovieContract.FavoriteEntry.TABLE_NAME + " (" +  MovieContract.FavoriteEntry.MOVIE_ID + ") " +

                " );";

        final String SQL_CREATE_FAVORITE_REVIEWS = "CREATE TABLE " + MovieContract.FavoriteReviews.TABLE_NAME + " (" +
                MovieContract.FavoriteReviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS + " INTEGER NOT NULL, " +
                MovieContract.FavoriteReviews.REVIEWS + " TEXT NOT NULL, " +
                // Set up foreign key from mostpopularentry table to mostpopularmoviereviews.
                " FOREIGN KEY (" + MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS + ") REFERENCES " +
                MovieContract.FavoriteEntry.TABLE_NAME + " (" + MovieContract.FavoriteEntry.MOVIE_ID + ") " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE_TRAILERS);
        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_REVIEWS);

        sqLiteDatabase.execSQL(SQL_CREATE_HIGHEST_RATED_ENTRY);
        sqLiteDatabase.execSQL(SQL_CREATE_HIGHEST_RATED_REVIEWS);
        sqLiteDatabase.execSQL(SQL_CREATE_HIGHEST_RATED_TRAILERS);

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_ENTRY);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_REVIEWS);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TRAILERS);


    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MostPopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MostPopularTrailers.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MostPopularReviews.TABLE_NAME);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.HighestRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.HighestRatedReviews.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.HighestRatedTrailers.TABLE_NAME);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteTrailers.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteReviews.TABLE_NAME);



        onCreate(sqLiteDatabase);
    }

}
