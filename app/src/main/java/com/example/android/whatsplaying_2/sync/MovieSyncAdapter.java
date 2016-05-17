package com.example.android.whatsplaying_2.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.android.whatsplaying_2.BuildConfig;
import com.example.android.whatsplaying_2.LoadHighestRated;
import com.example.android.whatsplaying_2.LoadMovieReviews;
import com.example.android.whatsplaying_2.R;
import com.example.android.whatsplaying_2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by TroysMacBook on 5/6/16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter{

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    String sortOrder;
    // Interval at which to sync with the weather, in milliseconds.
// 60 seconds (1 minute)  180 = 3 hours
    public static final int SYNC_INTERVAL = 60*720;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public Handler handlerPopularReviews = new Handler();
    public Handler handlerHighestRated = new Handler();

    public String insertMovie_id;

    public Long insertTableID;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

       sortOrder = "Most Popular";
        System.out.println("sort order is: "+sortOrder);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieString = null;

        Uri.Builder builder;

        //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //  sortOrder = prefs.getString(this.getString(R.string.sort_key), this.getString(R.string.default_sort_value));

        try {
            //building path to website to pull data
                builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("api_key", BuildConfig.Movie_db_api_key);

            String myUrl = builder.build().toString();
            System.out.println("URL: " + myUrl);
            //constructing URL for MovieDB
            URL url = new URL(myUrl);

            // Create the request to MovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();

            System.out.println("got input stream");

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                System.out.println("input stream is null");
                return;
            }
            //buffer reader to improve performance
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream is empty.
                return;
            }
            movieString = buffer.toString();
            System.out.println("Movies" + movieString);
            getMovieData(movieString, sortOrder);

        } catch (IOException e){
            Log.e("loadMovieInfo", "Error ", e);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error getting movie data");
        } finally

        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("loadMovieInfo", "Error closing stream", e);

                }
            }
        }

        return;

    }

    private void getMovieTrailer(long movieTableID, String movieID) throws JSONException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieString = null;

        Uri.Builder builder;

        //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //  sortOrder = prefs.getString(this.getString(R.string.sort_key), this.getString(R.string.default_sort_value));

        try {
            //building path to website to pull data
            builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieID)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", BuildConfig.Movie_db_api_key);

            String myUrl = builder.build().toString();
            System.out.println("URL: " + myUrl);
            //constructing URL for MovieDB
            URL url = new URL(myUrl);

            // Create the request to MovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
            }
            //buffer reader to improve performance
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream is empty.
            }
            movieString = buffer.toString();
            System.out.println("Movies" + movieString);


        } catch (
                IOException e


                )

        {
            Log.e("loadMovieInfo", "Error ", e);
        } finally

        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("loadMovieInfo", "Error closing stream", e);

                }
            }
        }

        final String getResults = "results";
        final String getKey = "key";

        //get movieTrailer info and put it into a JSONArray
        JSONObject movieJSON = new JSONObject(movieString);
        JSONArray movieArray = movieJSON.getJSONArray(getResults);

        //this will hold movie objects
        // Movie[] moviesPlaying = new Movie[movieArray.length()];
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
        String trailerKey;
        //if movie has no trailers
        if(movieArray.length()==0){
            trailerKey = "No Trailer Available";

            //add trailer into the database
            //       addMovieTrailer(movieTableID, trailerKey);

            ContentValues MovieTrailer = new ContentValues();

                MovieTrailer.put(MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER, movieTableID);
                MovieTrailer.put(MovieContract.MostPopularTrailers.TRAILER_KEY, trailerKey);

            cVVector.add(MovieTrailer);
        }

        for (int i = 0; i < movieArray.length(); i++) {


            //get individual movie info
            JSONObject movie = movieArray.getJSONObject(i);


            //create strings to pass to object
            trailerKey = movie.getString(getKey);

            if(trailerKey.equalsIgnoreCase("null")){
                trailerKey = "No Trailer Available";
            }

            //add trailer into the database
            //       addMovieTrailer(movieTableID, trailerKey);

            ContentValues MovieTrailer = new ContentValues();


                MovieTrailer.put(MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER, movieTableID);
                MovieTrailer.put(MovieContract.MostPopularTrailers.TRAILER_KEY, trailerKey);

            cVVector.add(MovieTrailer);

        }
        if (cVVector.size() > 0) {


            //insert data into the table
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            getContext().getContentResolver().bulkInsert(MovieContract.MostPopularTrailers.CONTENT_URI, cvArray);


        }
    }
    private void getMovieData(String movieJsonStr, String queryTable) throws JSONException {
        System.out.println("JSON: " + movieJsonStr);

        final Long[] movieTableID;
        final String[] movieID;

        final String getResults = "results";
        final String lmovieTitle = "title";
        final String lreleaseDate = "release_date";
        final String lmoviePoster = "poster_path";
        final String lvoteAverage = "vote_average";
        final String lplotSynopsis = "overview";
        final String id = "id";

        //get movie info and put it into a JSONArray
        JSONObject movieJSON = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJSON.getJSONArray(getResults);
        Uri movieTrailersURI=null;
        //this will hold movie objects
        // Movie[] moviesPlaying = new Movie[movieArray.length()];
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {


            //get individual movie info
            JSONObject movie = movieArray.getJSONObject(i);

            //create strings to pass to object
            String title = movie.getString(lmovieTitle);
            String releaseDate = movie.getString(lreleaseDate);
            String moviePoster = movie.getString(lmoviePoster);
            String voteAverage = movie.getString(lvoteAverage);
            String plot = movie.getString(lplotSynopsis);
            String movieid = movie.getString(id);
            sortOrder = queryTable;

            ContentValues movies = new ContentValues();

                movies.put(MovieContract.MostPopularEntry.MOVIE_ID, movieid);
                movies.put(MovieContract.MostPopularEntry.MOVIE_TITLE, title);
                movies.put(MovieContract.MostPopularEntry.IMAGE, moviePoster);
                movies.put(MovieContract.MostPopularEntry.RELEASE_DATE, releaseDate);
                movies.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, voteAverage);
                movies.put(MovieContract.MostPopularEntry.OVERVIEW, plot);
                //   movies.put(MovieContract.MostPopularEntry.SORT_ORDER, sortOrder);


            cVVector.add(movies);
        }

        if (cVVector.size() > 0) {

/*
        if(queryTable.equals("Highest Rated"))
            db.delete(MovieContract.MostPopularEntry.TABLE_NAME, MovieContract.MostPopularEntry.SORT_ORDER + " = ?", new String[] {"Highest Rated"});

            if(queryTable.equals("Most Popular"))
                db.delete(MovieContract.MostPopularEntry.TABLE_NAME, MovieContract.MostPopularEntry.SORT_ORDER + " = ?", new String[] {"Most Popular"});
*/

                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(MovieContract.MostPopularEntry.CONTENT_URI,
                        null,
                        null);

                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(MovieContract.MostPopularTrailers.CONTENT_URI,
                        null,
                        null);

                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(MovieContract.MostPopularReviews.CONTENT_URI,
                        null,
                        null);

            //insert data into the table

                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieContract.MostPopularEntry.CONTENT_URI, cvArray);
        }

        //get URI that the user wants to see, ie Most Popular or Highest Rated
        Uri movieDetailsURI=null;
            movieDetailsURI = MovieContract.MostPopularEntry.CONTENT_URI;

        Cursor cur = getContext().getContentResolver().query(movieDetailsURI, //pull only movies that user wants to see, either most popular or highest rated
                null, null,null, null);

        System.out.println("URI: "+movieDetailsURI);

        //get cursor count, set up movie array to store values in.
        int cursorRecords = cur.getCount();
        System.out.println("records" +cursorRecords);


        int cursorRecords2;
        //    Movie[] moviesPlaying = new Movie[cursorRecords];


        String findTitle;

        Long insertMovie_Selected_trailer_id;
        String insertTrailer=null;

        //  String[] trailers = new String[cursorRecords];
        String[] trailers;

        movieTableID = new Long[cursorRecords];
        movieID = new String[cursorRecords];

        //move through cursor, store data in movie array
        if (cur.moveToFirst()) {
            for(int i=0; i<cursorRecords; i++){
                //load movie data into a movie array
                insertTableID =  cur.getLong(cur.getColumnIndex("_id"));
                insertMovie_id = cur.getString(cur.getColumnIndex("movie_id"));

                movieTableID[i] = insertTableID;
                movieID[i] = insertMovie_id;

                System.out.println("movie id: " +insertMovie_id + " "+ insertTableID);

                //load movie trailers
                getMovieTrailer(insertTableID, insertMovie_id);

/*
                if(sortOrder.equals("Most Popular")) {
                    //put trailers in a cursor
                    movieTrailersURI = MovieContract.MostPopularTrailers.buildTrailer(insertTableID, insertTableID);
                }else if(sortOrder.equals("Highest Rated")){
                    movieTrailersURI = MovieContract.HighestRatedTrailers.buildTrailer(insertTableID, insertTableID);
                } else if(sortOrder.equals("Favorites")) {
                    movieTrailersURI = MovieContract.FavoriteTrailers.buildTrailer(insertTableID, insertTableID);
                }

                Cursor curTrailer = this.getContentResolver().query(movieTrailersURI,
                        null, null, null, null);
                cursorRecords2 = curTrailer.getCount();
                trailers = new String[cursorRecords2];
                System.out.println("Trailer uri: "+movieTrailersURI+ " number of records: "+cursorRecords2);
//put trailers in a string array so they can be passed into a movie object
                if (curTrailer.moveToFirst()) {
                    for (int x = 0; x < cursorRecords2; x++) {
                        insertMovie_Selected_trailer_id = curTrailer.getLong(curTrailer.getColumnIndex("movie_selected_Trailer"));
                        if(insertMovie_Selected_trailer_id.equals(insertTableID)) {
                            insertTrailer = curTrailer.getString(curTrailer.getColumnIndex("trailer_key"));
                            findTitle = curTrailer.getString(curTrailer.getColumnIndex("title"));
                            System.out.println("Its in the curserTrailer: " +findTitle);
                            trailers[x] = insertTrailer;
                        }
                        curTrailer.moveToNext();
                    }
                }

                int numtrailers = trailers.length;
                for (int x = 0; x < numtrailers; x++) {
                    System.out.println("trailers in array: " +trailers[x]);
                }

                insertMovieTitle= cur.getString(cur.getColumnIndex("title"));
                insertMoviePoster= cur.getString(cur.getColumnIndex("image"));
                insertMoviePlotSynopsis= cur.getString(cur.getColumnIndex("overview"));
                insertMovieVoteAverage= cur.getString(cur.getColumnIndex("vote_average"));
                insertMovieReleaseDate= cur.getString(cur.getColumnIndex("release_date"));

                System.out.println("Movie Info: "+insertMovieTitle+insertMoviePoster);

//pass data into the movie object
       //         moviesPlaying[i] = new Movie(insertMovieTitle, insertMovieReleaseDate, insertMoviePoster, insertMovieVoteAverage, insertMoviePlotSynopsis, insertMovie_id, trailers,sortOrder);
*/
                //movie to next row in cursor
                cur.moveToNext();
                //               curTrailer.close();
            }
            cur.close();
        }
        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        Thread tPopularReviews = new Thread( new Runnable(){
            public void run() {
                handlerPopularReviews.postDelayed(new Runnable() {
                    public void run() {

                        LoadMovieReviews newRevs = new LoadMovieReviews(getContext(), movieTableID, movieID, sortOrder);
                        newRevs.execute();
                        // getMovieReview(insertTableID,insertMovie_id);

                    }
                }, 32000);
            }
        });
        tPopularReviews.start();



        Thread tHighestRated = new Thread( new Runnable(){
            public void run() {
                handlerHighestRated.postDelayed(new Runnable() {
                    public void run() {

                        LoadHighestRated highestRatedExe = new LoadHighestRated(getContext());
                        highestRatedExe.execute();
                        // getMovieReview(insertTableID,insertMovie_id);

                    }
                }, 64000);
            }
        });
        tHighestRated.start();

    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);

        System.out.print("SyncImmediately is being called");
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            System.out.println("new account created");
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }



}
