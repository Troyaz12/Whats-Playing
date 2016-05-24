package com.example.android.whatsplaying_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

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
 * Created by TroysMacBook on 5/13/16.
 */
public class LoadHighestRated extends AsyncTask<Void, Void, Void> {
    public Context context;
    public String sortOrder="Highest Rated";
    public Handler handler = new Handler();

    public String insertMovie_id;
    public Long insertTableID;



    public LoadHighestRated(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

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
                        .appendPath("top_rated")
                        .appendQueryParameter("api_key", BuildConfig.Movie_db_api_key);

            String myUrl = builder.build().toString();
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
            getMovieData(movieString);

        } catch (IOException e){
            Log.e("loadMovieInfo", "Error ", e);
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

            return null;
    }

    private void getMovieTrailer(long movieTableID, String movieID) throws JSONException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieString = null;

        Uri.Builder builder;

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
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
        String trailerKey;
        //if movie has no trailers
        if(movieArray.length()==0){
            trailerKey = "No Trailer Available";

            //add trailer into the database

            ContentValues MovieTrailer = new ContentValues();


                MovieTrailer.put(MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER, movieTableID);
                MovieTrailer.put(MovieContract.HighestRatedTrailers.TRAILER_KEY, trailerKey);


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

            ContentValues MovieTrailer = new ContentValues();



                MovieTrailer.put(MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER, movieTableID);
                MovieTrailer.put(MovieContract.HighestRatedTrailers.TRAILER_KEY, trailerKey);


            cVVector.add(MovieTrailer);

        }
        if (cVVector.size() > 0) {


            //insert data into the table
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            context.getContentResolver().bulkInsert(MovieContract.HighestRatedTrailers.CONTENT_URI, cvArray);

        }
    }

    private void getMovieData(String movieJsonStr) throws JSONException {
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

            ContentValues movies = new ContentValues();

                movies.put(MovieContract.HighestRatedEntry.MOVIE_ID, movieid);
                movies.put(MovieContract.HighestRatedEntry.MOVIE_TITLE, title);
                movies.put(MovieContract.HighestRatedEntry.IMAGE, moviePoster);
                movies.put(MovieContract.HighestRatedEntry.RELEASE_DATE, releaseDate);
                movies.put(MovieContract.HighestRatedEntry.VOTE_AVERAGE, voteAverage);
                movies.put(MovieContract.HighestRatedEntry.OVERVIEW, plot);

            cVVector.add(movies);
        }

        if (cVVector.size() > 0) {

            // delete old data so we don't build up an endless history
            context.getContentResolver().delete(MovieContract.HighestRatedEntry.CONTENT_URI,
                    null,
                    null);

            // delete old data so we don't build up an endless history
            context.getContentResolver().delete(MovieContract.HighestRatedTrailers.CONTENT_URI,
                    null,
                    null);

            // delete old data so we don't build up an endless history
            context.getContentResolver().delete(MovieContract.HighestRatedReviews.CONTENT_URI,
                    null,
                    null);

            //insert data into the table

            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(MovieContract.HighestRatedEntry.CONTENT_URI, cvArray);

        }

        //get URI that the user wants to see, ie Most Popular or Highest Rated
        Uri movieDetailsURI=null;

            movieDetailsURI = MovieContract.HighestRatedEntry.CONTENT_URI;

        Cursor cur = context.getContentResolver().query(movieDetailsURI, //pull only movies that user wants to see, either most popular or highest rated
                null, null,null, null);

        System.out.println("URI: "+movieDetailsURI);

        //get cursor count, set up movie array to store values in.
        int cursorRecords = cur.getCount();
        System.out.println("records" +cursorRecords);

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

                cur.moveToNext();
            }
            cur.close();
        }
        Thread t = new Thread( new Runnable(){
            public void run() {
                handler.postDelayed(new Runnable(){
                    public void run() {

                        LoadMovieReviews newRevs = new LoadMovieReviews(context,movieTableID,movieID,sortOrder);
                        newRevs.execute();
                    }
                }, 32000);
            }
        });
        t.start();
    }


}
