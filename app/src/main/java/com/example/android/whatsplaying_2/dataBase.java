package com.example.android.whatsplaying_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.whatsplaying_2.data.MovieContract;
import com.example.android.whatsplaying_2.data.MovieDbHelper;

import java.util.Vector;

/**
 * Created by TroysMacBook on 4/22/16.
 */
public class dataBase extends AsyncTask<Movie, Void, Void> {

    Movie addFavorite;
    private final Context mContext;
    Boolean togglePosition;

    String title;
    String releaseDate;
    String moviePoster;
    String voteAverage;
    String plot;
    String movieid;
    String[] trailers;
    String[] reviews;
    String sortOrder;

    public dataBase(Context context, Movie movie){
        addFavorite = movie;
        mContext = context;

    }

    @Override
    protected Void doInBackground(Movie... params) {

        //get values needed from movie object
        title = addFavorite.getMovieTitle();
        releaseDate = addFavorite.releaseDate;
        moviePoster = addFavorite.getMoviePoster();
        voteAverage = addFavorite.getVoteAverage();
        plot = addFavorite.getPlotSynopsis();
        movieid = addFavorite.getId();
        trailers = addFavorite.getTrailers();
        reviews = addFavorite.getReviews();
        sortOrder = addFavorite.getSortOrder();


        if(togglePosition.equals(true))
            AddMovieToTable();
        else
            removeMovieFromTable();


        return null;
    }
    public void setTogglePosition(Boolean toggle){

        togglePosition=toggle;
    }
    public Boolean getTogglePosition(String movieid){       //should the favorites button be checked
        //get database
        MovieDbHelper mOpenHelper;
        mOpenHelper = new MovieDbHelper(mContext);

        Cursor cursorMovie = mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,        // see if movie is in the favorites table
                null,
                MovieContract.FavoriteEntry.MOVIE_ID + " = ? ",
                new String[]{movieid},
                null,
                null,
                null);

        if(cursorMovie.getCount()>0)
            togglePosition = true;
        else
            togglePosition = false;

        return togglePosition;
    }

    private void AddMovieToTable(){
        //uris needed
        Uri movieURI=null;
        Uri insertedUri;

        //put data the goes into favorite movie table into a content values object
        ContentValues movies = new ContentValues();

        movies.put(MovieContract.FavoriteEntry.MOVIE_ID, movieid);
        movies.put(MovieContract.FavoriteEntry.MOVIE_TITLE, title);
        movies.put(MovieContract.FavoriteEntry.IMAGE, moviePoster);
        movies.put(MovieContract.FavoriteEntry.RELEASE_DATE, releaseDate);
        movies.put(MovieContract.FavoriteEntry.VOTE_AVERAGE, voteAverage);
        movies.put(MovieContract.FavoriteEntry.OVERVIEW, plot);

        movieURI = MovieContract.FavoriteEntry.CONTENT_URI;       //uri to table that data needs to be inserted in

        insertedUri = mContext.getContentResolver().insert(movieURI,movies);


        Vector<ContentValues> cVVectorTrailers = new Vector<ContentValues>(trailers.length);

        for (int i = 0; i < trailers.length; i++) {

            ContentValues MovieTrailer = new ContentValues();

            MovieTrailer.put(MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER,movieid);
            MovieTrailer.put(MovieContract.FavoriteTrailers.TRAILER_KEY, trailers[i]);

            cVVectorTrailers.add(MovieTrailer);

        }
        if (cVVectorTrailers.size() > 0) {
            //insert data into the table
            ContentValues[] cvArray = new ContentValues[cVVectorTrailers.size()];
            cVVectorTrailers.toArray(cvArray);

            mContext.getContentResolver().bulkInsert(MovieContract.FavoriteTrailers.CONTENT_URI, cvArray);

        }

        Vector<ContentValues> cVVectorReview = new Vector<ContentValues>(reviews.length);


        for (int i = 0; i < reviews.length; i++) {
            ContentValues MovieReview = new ContentValues();
            MovieReview.put(MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS,movieid);
            MovieReview.put(MovieContract.FavoriteReviews.REVIEWS, reviews[i]);

            cVVectorReview.add(MovieReview);

        }
        if (cVVectorReview.size() > 0) {
            //insert data into the table
            ContentValues[] cvArray = new ContentValues[cVVectorReview.size()];
            cVVectorReview.toArray(cvArray);

            mContext.getContentResolver().bulkInsert(MovieContract.FavoriteReviews.CONTENT_URI, cvArray);

        }
    }

    private void removeMovieFromTable(){
        mContext.getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI, MovieContract.FavoriteEntry.MOVIE_ID+" = ? ", new String[]{movieid});
        mContext.getContentResolver().delete(MovieContract.FavoriteTrailers.CONTENT_URI, MovieContract.FavoriteTrailers.MOVIE_SELECTED_TRAILER+"=?",new String[]{movieid});
        mContext.getContentResolver().delete(MovieContract.FavoriteReviews.CONTENT_URI, MovieContract.FavoriteReviews.MOVIE_SELECTED_REVIEWS+"=?",new String[]{movieid});
    }
}