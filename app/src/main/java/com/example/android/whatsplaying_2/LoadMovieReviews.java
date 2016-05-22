package com.example.android.whatsplaying_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
 * Created by TroysMacBook on 5/10/16.
 */
public class LoadMovieReviews extends AsyncTask<Void, Void, Void>{

    public Long[] tableID;
    public String[] movieID;
    public Context context;
    public String sortOrder;

    public LoadMovieReviews(Context context, Long[] tableID, String[] movieID, String sortOrder){
        this.tableID = tableID;
        this.movieID = movieID;
        this.context = context;
        this.sortOrder = sortOrder;
    }


    @Override
    protected Void doInBackground(Void... params) {

        System.out.println("records in cursor for loadMovieRevs: " + tableID.length);

        for(int i=0;i<tableID.length;i++) {

            System.out.println("records in cursor for loadMovieRevs: " + tableID.length+ "i is: "+i+ " movieID: "+ tableID[i]);


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Uri movieDetailsURI = null;
            // Will contain the raw JSON response as a string.
            String movieString = null;
            Cursor curReview = null;  //cursor to hold reviews
            int cursorRecords2 = 0;  //how many records in the cursor

            Uri.Builder builder;

            try {
                //building path to website to pull data
                builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(movieID[i])
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
            JSONObject movieJSON = null;
            try {
                movieJSON = new JSONObject(movieString);
                JSONArray movieArray = movieJSON.getJSONArray(getResults);

               //holds reviews if there are them
                Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());



                for (int x = 0; x < movieArray.length(); x++) {


                    //get individual movie info
                    JSONObject movie = movieArray.getJSONObject(x);

                    //create strings to pass to object
                    String review = movie.getString(getReview);
                    String author = movie.getString(getAuthor);

                    //add trailer into the database

                    ContentValues MovieReview = new ContentValues();

                    if (sortOrder.equals("Most Popular")) {
                        MovieReview.put(MovieContract.MostPopularReviews.MOVIE_SELECTED_REVIEWS, tableID[i]);
                        MovieReview.put(MovieContract.MostPopularReviews.AUTHOR, author);
                        MovieReview.put(MovieContract.MostPopularReviews.REVIEWS, review);
                    } else if (sortOrder.equals("Highest Rated")) {
                        MovieReview.put(MovieContract.HighestRatedReviews.MOVIE_SELECTED_REVIEWS, tableID[i]);
                        MovieReview.put(MovieContract.HighestRatedReviews.AUTHOR, author);
                        MovieReview.put(MovieContract.HighestRatedReviews.REVIEWS, review);
                    }

                    cVVector.add(MovieReview);

                }
                //labels movie as not having a review
                if(movieArray.length()==0) {
                    ContentValues MovieReview = new ContentValues();
                    String noReviews = "No Reviews Available";
                    MovieReview.put(MovieContract.MostPopularReviews.MOVIE_SELECTED_REVIEWS, tableID[i]);
                    MovieReview.put(MovieContract.MostPopularReviews.AUTHOR, noReviews);
                    MovieReview.put(MovieContract.MostPopularReviews.REVIEWS, " ");

                    cVVector.add(MovieReview);
                }

                if (cVVector.size() > 0) {

                    //insert data into the table
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    System.out.println("Sort Order: " + sortOrder);
                    if (sortOrder.equals("Most Popular")) {
                        context.getContentResolver().bulkInsert(MovieContract.MostPopularReviews.CONTENT_URI, cvArray);

                        //get movie reviews for movie
                        movieDetailsURI = MovieContract.MostPopularReviews.buildMostPopularReviewUri(tableID[i]);

                    } else if (sortOrder.equals("Highest Rated")) {
                        context.getContentResolver().bulkInsert(MovieContract.HighestRatedReviews.CONTENT_URI, cvArray);
                        System.out.println("movie review exe");
                        //get movie reviews for movie
                        movieDetailsURI = MovieContract.HighestRatedReviews.buildHighestRatedReviewUri(tableID[i]);

                    } else if (sortOrder.equals("Favorites")) {
                        movieDetailsURI = MovieContract.FavoriteReviews.buildFavoriteReviewUri(tableID[i]);
                    }
                    curReview = context.getContentResolver().query(movieDetailsURI,
                            null, null, null, null);
                    cursorRecords2 = curReview.getCount();
                }

                String[] reviews = new String[cursorRecords2];    //create a string array to put the data in
                String insertReviewAuthor;
                String insertReview;
                String[] oneReview = new String[1];

                if (curReview != null) {
                    if (curReview.moveToFirst() && curReview.getString(curReview.getColumnIndex("reviews")) != null) {
                        //insert data from cursor
                        for (int x = 0; x < cursorRecords2; x++) {
                            insertReviewAuthor = curReview.getString(curReview.getColumnIndex("author"));
                            insertReview = curReview.getString(curReview.getColumnIndex("reviews"));

                            reviews[x] = context.getString(R.string.Author) + insertReviewAuthor + "\n" + "\n" + insertReview;

                            curReview.moveToNext();
                        }

                        curReview.close();

                    }
                } else {
                    oneReview[0] = "No Reviews Available.";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return null;
    }
}
