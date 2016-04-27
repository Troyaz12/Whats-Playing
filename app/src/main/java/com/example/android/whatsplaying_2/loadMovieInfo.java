package com.example.android.whatsplaying_2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.whatsplaying_2.data.MovieContract;
import com.example.android.whatsplaying_2.data.MovieDbHelper;

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
 * Created by TroysMacBook on 3/30/16.
 */
public class loadMovieInfo extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MovieAdapter movieAdapter;
    private final Context mContext;
    String sortOrder;

    private static final String[] getTrailers = new String[] {
            MovieContract.MostPopularTrailers.TRAILER_KEY,
    };

    public loadMovieInfo(Context context, MovieAdapter movieAdapterImport) {
        mContext = context;
        movieAdapter = movieAdapterImport;
    }


    public long addMovie(String queryTable, String movieID, String title, String moviePost, String releaseDate, String voteAverage, String OverView) {
        long movieTableId;
        Uri insertedUri;
        ContentValues movieValues = new ContentValues();


        switch (queryTable) {
            case "Most Popular": {
                movieValues.put(MovieContract.MostPopularEntry.MOVIE_ID, movieID);
                movieValues.put(MovieContract.MostPopularEntry.MOVIE_TITLE, title);
                movieValues.put(MovieContract.MostPopularEntry.IMAGE, moviePost);
                movieValues.put(MovieContract.MostPopularEntry.RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, voteAverage);
                movieValues.put(MovieContract.MostPopularEntry.OVERVIEW, OverView);
                // insert movie data into the database.
                insertedUri = mContext.getContentResolver().insert(
                        MovieContract.MostPopularEntry.CONTENT_URI,
                        movieValues
                );
                break;
            }
            case "Highest Rated": {
                movieValues.put(MovieContract.HighestRatedEntry.MOVIE_ID, movieID);
                movieValues.put(MovieContract.HighestRatedEntry.MOVIE_TITLE, title);
                movieValues.put(MovieContract.HighestRatedEntry.IMAGE, moviePost);
                movieValues.put(MovieContract.HighestRatedEntry.RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.HighestRatedEntry.VOTE_AVERAGE, voteAverage);
                movieValues.put(MovieContract.HighestRatedEntry.OVERVIEW, OverView);
                // insert movie into highest rated database
                insertedUri = mContext.getContentResolver().insert(
                        MovieContract.HighestRatedEntry.CONTENT_URI,
                        movieValues
                );
                break;
            }
            case "Favorites": {
                movieValues.put(MovieContract.MostPopularEntry.MOVIE_ID, movieID);
                movieValues.put(MovieContract.MostPopularEntry.MOVIE_TITLE, title);
                movieValues.put(MovieContract.MostPopularEntry.IMAGE, moviePost);
                movieValues.put(MovieContract.MostPopularEntry.RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, voteAverage);
                movieValues.put(MovieContract.MostPopularEntry.OVERVIEW, OverView);
                //insert movie into trailer database
                insertedUri = mContext.getContentResolver().insert(
                        MovieContract.MostPopularEntry.CONTENT_URI,
                        movieValues
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown table: " + queryTable);

        }


            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieTableId = ContentUris.parseId(insertedUri);


        // Wait, that worked?  Yes!
        return movieTableId;
    }

    protected Movie[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        if (params.length == 0) {
            return null;
        }
        String queryTable = params[0];


        // Will contain the raw JSON response as a string.
        String movieString = null;

        Uri.Builder builder;

      //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
      //  sortOrder = prefs.getString(mContext.getString(R.string.sort_key), mContext.getString(R.string.default_sort_value));

        try {
            //building path to website to pull data
            if (queryTable.equalsIgnoreCase("Most Popular")) {
                builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("api_key", BuildConfig.Movie_db_api_key);
            } else {
                builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", "vote_average.desc")
                        .appendQueryParameter("api_key", BuildConfig.Movie_db_api_key);
            }
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
                return null;
            }
            //buffer reader to improve performance
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream is empty.
                return null;
            }
            movieString = buffer.toString();
            System.out.println("Movies" + movieString);


        } catch (
                IOException e


                )

        {
            Log.e("loadMovieInfo", "Error ", e);
            return null;
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

        try {
            return getMovieData(movieString,queryTable);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }
        //if there is an error parsing the forecast
        return null;

    }

    private void getMovieTrailer(long movieTableID, String movieID) throws JSONException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieString = null;

        Uri.Builder builder;

        //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        //  sortOrder = prefs.getString(mContext.getString(R.string.sort_key), mContext.getString(R.string.default_sort_value));

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

        for (int i = 0; i < movieArray.length(); i++) {


            //get individual movie info
            JSONObject movie = movieArray.getJSONObject(i);

            //create strings to pass to object
            String trailerKey = movie.getString(getKey);

            //add trailer into the database
     //       addMovieTrailer(movieTableID, trailerKey);

            ContentValues MovieTrailer = new ContentValues();


            if(sortOrder.equals("Most Popular")){
                MovieTrailer.put(MovieContract.MostPopularTrailers.MOVIE_SELECTED_TRAILER, movieTableID);
                MovieTrailer.put(MovieContract.MostPopularTrailers.TRAILER_KEY, trailerKey);
            }else if(sortOrder.equals("Highest Rated")){
                MovieTrailer.put(MovieContract.HighestRatedTrailers.MOVIE_SELECTED_TRAILER, movieTableID);
                MovieTrailer.put(MovieContract.HighestRatedTrailers.TRAILER_KEY, trailerKey);
            }

            cVVector.add(MovieTrailer);

        }
        if (cVVector.size() > 0) {


            //insert data into the table
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);


            if(sortOrder.equals("Most Popular")) {
                mContext.getContentResolver().bulkInsert(MovieContract.MostPopularTrailers.CONTENT_URI, cvArray);
            }else if(sortOrder.equals("Highest Rated")){
                mContext.getContentResolver().bulkInsert(MovieContract.HighestRatedTrailers.CONTENT_URI, cvArray);
            }




        }
    }
    public String[] getMovieReview(long movieTableID, String movieID) throws JSONException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri movieDetailsURI=null;
        // Will contain the raw JSON response as a string.
        String movieString = null;
        Cursor curReview=null;  //cursor to hold reviews
        int cursorRecords2 =0;  //how many records in the cursor

        Uri.Builder builder;

        //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        //  sortOrder = prefs.getString(mContext.getString(R.string.sort_key), mContext.getString(R.string.default_sort_value));

        try {
            //building path to website to pull data
            builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieID)
                    .appendPath("reviews")
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
        final String getReview = "content";
        final String getAuthor = "author";

        //get movieTrailer info and put it into a JSONArray
        JSONObject movieJSON = new JSONObject(movieString);
        JSONArray movieArray = movieJSON.getJSONArray(getResults);

        //this will hold movie objects
        // Movie[] moviesPlaying = new Movie[movieArray.length()];
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {


            //get individual movie info
            JSONObject movie = movieArray.getJSONObject(i);

            //create strings to pass to object
            String review = movie.getString(getReview);
            String author = movie.getString(getAuthor);

            //add trailer into the database
           // addMovieReview(movieTableID, review, author);

            ContentValues MovieReview = new ContentValues();

            if(sortOrder.equals("Most Popular")) {
                MovieReview.put(MovieContract.MostPopularReviews.MOVIE_SELECTED_REVIEWS, movieTableID);
                MovieReview.put(MovieContract.MostPopularReviews.AUTHOR, author);
                MovieReview.put(MovieContract.MostPopularReviews.REVIEWS, review);
            }else if(sortOrder.equals("Highest Rated")){
                MovieReview.put(MovieContract.HighestRatedReviews.MOVIE_SELECTED_REVIEWS, movieTableID);
                MovieReview.put(MovieContract.HighestRatedReviews.AUTHOR, author);
                MovieReview.put(MovieContract.HighestRatedReviews.REVIEWS, review);
            }



            cVVector.add(MovieReview);

        }
        if (cVVector.size() > 0) {

            //insert data into the table
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            System.out.println("Sort Order: "+sortOrder);
            if(sortOrder.equals("Most Popular")) {
                mContext.getContentResolver().bulkInsert(MovieContract.MostPopularReviews.CONTENT_URI, cvArray);

                //get movie reviews for movie
                movieDetailsURI = MovieContract.MostPopularReviews.buildMostPopularReviewUri(movieTableID);

            }else if(sortOrder.equals("Highest Rated")){
                mContext.getContentResolver().bulkInsert(MovieContract.HighestRatedReviews.CONTENT_URI, cvArray);
                System.out.println("movie review exe");
                //get movie reviews for movie
                movieDetailsURI = MovieContract.HighestRatedReviews.buildHighestRatedReviewUri(movieTableID);

            }else if(sortOrder.equals("Favorites")) {
                movieDetailsURI = MovieContract.FavoriteReviews.buildFavoriteReviewUri(movieTableID);
            }
               curReview = mContext.getContentResolver().query(movieDetailsURI,
                    null, null, null, null);
               cursorRecords2 = curReview.getCount();
        }

        String [] reviews = new String[cursorRecords2];    //create a string array to put the data in
        String insertReviewAuthor;
        String insertReview;
        String [] oneReview = new String[1];

        if(curReview!=null) {
            if (curReview.moveToFirst() && curReview.getString(curReview.getColumnIndex("reviews")) != null) {
                //insert data from cursor
                for (int x = 0; x < cursorRecords2; x++) {
                    insertReviewAuthor = curReview.getString(curReview.getColumnIndex("author"));
                    insertReview = curReview.getString(curReview.getColumnIndex("reviews"));


                    reviews[x] = mContext.getString(R.string.Author) + insertReviewAuthor + "\n" + "\n" + insertReview;

                    curReview.moveToNext();
                }

                curReview.close();

            }
        }else {
            oneReview[0] = "No Reviews Available.";
            return oneReview;
        }
        return reviews;             //return string array



    }
    private Movie[] getMovieData(String movieJsonStr, String queryTable) throws JSONException {
        System.out.println("JSON: " + movieJsonStr);

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
          //  long movieTableID = addMovie(queryTable, movieid, title, moviePoster, releaseDate, voteAverage,plot);




            ContentValues movies = new ContentValues();

            if(sortOrder.equals("Most Popular")) {
                movies.put(MovieContract.MostPopularEntry.MOVIE_ID, movieid);
                movies.put(MovieContract.MostPopularEntry.MOVIE_TITLE, title);
                movies.put(MovieContract.MostPopularEntry.IMAGE, moviePoster);
                movies.put(MovieContract.MostPopularEntry.RELEASE_DATE, releaseDate);
                movies.put(MovieContract.MostPopularEntry.VOTE_AVERAGE, voteAverage);
                movies.put(MovieContract.MostPopularEntry.OVERVIEW, plot);
                //   movies.put(MovieContract.MostPopularEntry.SORT_ORDER, sortOrder);
            }else if(sortOrder.equals("Highest Rated")){
                movies.put(MovieContract.HighestRatedEntry.MOVIE_ID, movieid);
                movies.put(MovieContract.HighestRatedEntry.MOVIE_TITLE, title);
                movies.put(MovieContract.HighestRatedEntry.IMAGE, moviePoster);
                movies.put(MovieContract.HighestRatedEntry.RELEASE_DATE, releaseDate);
                movies.put(MovieContract.HighestRatedEntry.VOTE_AVERAGE, voteAverage);
                movies.put(MovieContract.HighestRatedEntry.OVERVIEW, plot);
            }



            cVVector.add(movies);
        }

        if (cVVector.size() > 0) {
             MovieDbHelper mOpenHelper;
            mOpenHelper = new MovieDbHelper(mContext);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
/*
        if(queryTable.equals("Highest Rated"))
            db.delete(MovieContract.MostPopularEntry.TABLE_NAME, MovieContract.MostPopularEntry.SORT_ORDER + " = ?", new String[] {"Highest Rated"});

            if(queryTable.equals("Most Popular"))
                db.delete(MovieContract.MostPopularEntry.TABLE_NAME, MovieContract.MostPopularEntry.SORT_ORDER + " = ?", new String[] {"Most Popular"});
*/

            if(sortOrder.equals("Most Popular")) {
                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MovieContract.MostPopularEntry.CONTENT_URI,
                        null,
                        null);

                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MovieContract.MostPopularTrailers.CONTENT_URI,
                        null,
                        null);

                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MovieContract.MostPopularReviews.CONTENT_URI,
                        null,
                        null);
            }else if(sortOrder.equals("Highest Rated")){
                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MovieContract.HighestRatedEntry.CONTENT_URI,
                        null,
                        null);

                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MovieContract.HighestRatedTrailers.CONTENT_URI,
                        null,
                        null);

                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MovieContract.HighestRatedReviews.CONTENT_URI,
                        null,
                        null);

            }


            //insert data into the table

            if(sortOrder.equals("Most Popular")) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MovieContract.MostPopularEntry.CONTENT_URI, cvArray);
            }else if(sortOrder.equals("Highest Rated")){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MovieContract.HighestRatedEntry.CONTENT_URI, cvArray);

            }


        }

        //get URI that the user wants to see, ie Most Popular or Highest Rated
        Uri movieDetailsURI=null;
        if(sortOrder.equals("Most Popular")) {
            movieDetailsURI = MovieContract.MostPopularEntry.CONTENT_URI;
        }else if(sortOrder.equals("Highest Rated")){
            movieDetailsURI = MovieContract.HighestRatedEntry.CONTENT_URI;
        }else if(sortOrder.equals("Favorites")){
            movieDetailsURI = MovieContract.FavoriteEntry.CONTENT_URI;
        }

        Cursor cur = mContext.getContentResolver().query(movieDetailsURI, //pull only movies that user wants to see, either most popular or highest rated
                null, null,null, null);

        System.out.println("URI: "+movieDetailsURI);

        //get cursor count, set up movie array to store values in.
        int cursorRecords = cur.getCount();
        System.out.println("records" +cursorRecords);


        int cursorRecords2;
        Movie[] moviesPlaying = new Movie[cursorRecords];


        String insertMovie_id;
        String insertMovieTitle;
        String insertMoviePoster;
        String insertMoviePlotSynopsis;
        String insertMovieVoteAverage;
        String insertMovieReleaseDate;
        Long insertTableID;


        Long insertMovie_Selected_trailer_id;
        String insertTrailer=null;

      //  String[] trailers = new String[cursorRecords];
        String[] trailers;
        //move through cursor, store data in movie array
        if (cur.moveToFirst()) {
            for(int i=0; i<cursorRecords; i++){
                //load movie data into a movie array
                insertTableID =  cur.getLong(cur.getColumnIndex("_id"));
                insertMovie_id = cur.getString(cur.getColumnIndex("movie_id"));

                System.out.println("movie id: " +insertMovie_id + " "+ insertTableID);

                //load movie trailers
                getMovieTrailer(insertTableID, insertMovie_id);


                if(sortOrder.equals("Most Popular")) {
                    //put trailers in a cursor
                     movieTrailersURI = MovieContract.MostPopularTrailers.buildTrailer(insertTableID, insertTableID);
                }else if(sortOrder.equals("Highest Rated")){
                    movieTrailersURI = MovieContract.HighestRatedTrailers.buildTrailer(insertTableID, insertTableID);
                } else if(sortOrder.equals("Favorites")) {
                    movieTrailersURI = MovieContract.FavoriteTrailers.buildTrailer(insertTableID, insertTableID);
                }

                Cursor curTrailer = mContext.getContentResolver().query(movieTrailersURI,
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
                moviesPlaying[i] = new Movie(insertMovieTitle, insertMovieReleaseDate, insertMoviePoster, insertMovieVoteAverage, insertMoviePlotSynopsis, insertMovie_id, insertTableID,trailers,sortOrder);

                //movie to next row in cursor
                cur.moveToNext();
                curTrailer.close();
            }


            int trailerLength;
            int movielength = moviesPlaying.length;
            for(int z=0; z<movielength; z++) {
                trailerLength = moviesPlaying[z].trailers.length;

                for(int y=0;y<trailerLength; y++){

                    System.out.println("Trailers in object: " +moviesPlaying[z].trailers[y]);

                }

            }

            cur.close();


        }


        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
//end of new code just added
        return moviesPlaying;
    }

    protected void onPostExecute(Movie[] result) {

        if (result != null) {
            movieAdapter.clear();
            for (Movie movieStr : result) {
                movieAdapter.add(movieStr);
            }
        }

    }
}