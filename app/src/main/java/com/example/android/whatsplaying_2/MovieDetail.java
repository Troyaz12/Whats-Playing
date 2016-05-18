package com.example.android.whatsplaying_2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.whatsplaying_2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                   // .add(R.id.container, new MovieDetailFragment())
                    .add(R.id.movie_detail_container, new MovieDetailFragment())
                    .commit();
        }
    }

    public static class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private ArrayAdapter<String> trailerAdapter;
        private String[] movieTrailer = {
                "Select Trailer"};
        public Movie movie;
        Bundle b;
        List<String> trailerList;
        private String[] spinnerLabels;
        private ArrayAdapter<String> reviewAdapter;
        List<String> reviewList;
        dataBase movieTaskDetail;
        Boolean togglePosition;      //toggle position of add favorite movie toggle button
        ToggleButton toggle;
        Boolean movieInFavorites;
        public String[] trailer;
        public String[] reviews;
        public String sortOrder;

        private static final int DETAIL_LOADER = 0;

        private static final String[] MOST_POPULAR_MOVIE_COLUMNS = {
                MovieContract.MostPopularEntry.TABLE_NAME + "." + MovieContract.MostPopularEntry.MOVIE_ID,
                MovieContract.MostPopularEntry.MOVIE_TITLE,
                MovieContract.MostPopularEntry.RELEASE_DATE,
                MovieContract.MostPopularEntry.VOTE_AVERAGE,
                MovieContract.MostPopularEntry.IMAGE,
                MovieContract.MostPopularEntry.OVERVIEW,
                MovieContract.MostPopularTrailers.TRAILER_KEY,
                MovieContract.MostPopularReviews.AUTHOR,
                MovieContract.MostPopularReviews.REVIEWS

        };

        private static final String[] HIGHEST_RATED_MOVIE_COLUMNS = {
                MovieContract.HighestRatedEntry.TABLE_NAME + "." + MovieContract.HighestRatedEntry.MOVIE_ID,
                MovieContract.HighestRatedEntry.MOVIE_TITLE,
                MovieContract.HighestRatedEntry.RELEASE_DATE,
                MovieContract.HighestRatedEntry.VOTE_AVERAGE,
                MovieContract.HighestRatedEntry.IMAGE,
                MovieContract.HighestRatedEntry.OVERVIEW,
                MovieContract.HighestRatedTrailers.TRAILER_KEY,
                MovieContract.HighestRatedReviews.AUTHOR,
                MovieContract.HighestRatedReviews.REVIEWS
        };

        private static final String[] FAVORITE_MOVIE_COLUMNS = {
                MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry.MOVIE_ID,
                MovieContract.FavoriteEntry.MOVIE_TITLE,
                MovieContract.FavoriteEntry.RELEASE_DATE,
                MovieContract.FavoriteEntry.VOTE_AVERAGE,
                MovieContract.FavoriteEntry.IMAGE,
                MovieContract.FavoriteEntry.OVERVIEW,
                MovieContract.FavoriteTrailers.TRAILER_KEY,
                MovieContract.FavoriteReviews.REVIEWS

        };


        // these constants correspond to the projection defined above, and must change if the
        // projection changes. this is for the popular movies table
        private static final int COL_MOVIE_ID = 0;
        private static final int COL_MOVIE_TITLE = 1;
        private static final int COL_RELEASE_DATE = 2;
        private static final int COL_VOTE_AVERAGE = 3;
        private static final int COL_IMAGE = 4;
        private static final int COL_OVERVIEW = 5;
        private static final int COL_TRAILER_KEY = 6;
        private static final int COL_REVIEW_AUTHOR = 7;
        private static final int COL_REVIEW_REVIEW_INFO = 8;


        public MovieDetailFragment() {
        }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

// Restore state members from saved instance
   //     if(savedInstanceState == null) {
            // add movie objects to the array adapter
            //grab intent
  //          Intent i = getActivity().getIntent();
            //get bundle from intent
   //         b = i.getExtras();

  //          if (b != null) {
                //get movie object from bundle
    //            movie = b.getParcelable("selectedMovie");
      //          reviews = b.getStringArray("selectedMovieReview");
        //        System.out.println("exe2");
          //      System.out.println("Movie Title: "+movie.movieTitle);
        //    }


  //      }
   //     else {
   //         movie = savedInstanceState.getParcelable("movieSaved");
   //         reviews = savedInstanceState.getStringArray("movieReviewSaved");

   //     }

    }
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
      //              outState.putParcelable("movieSaved", movie);
            //      outState.putStringArray("movieReviewSaved", reviews);

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

/*
        TextView title = (TextView) rootView.findViewById(R.id.textTitle);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.textReleaseDate);
        TextView VoteAve = (TextView) rootView.findViewById(R.id.textVoteAve);
        TextView Plot = (TextView) rootView.findViewById(R.id.textPlot);
        ImageView PosterDetail = (ImageView) rootView.findViewById(R.id.moviePosterDetail);
        TextView review = (TextView) rootView.findViewById(R.id.textReviews);

        //format reviews into a string to be passed to the review textview
        StringBuilder builder = new StringBuilder();
        for(String s : reviews) {
            builder.append(s);
        }

        //get movie information
        title.setText(movie.movieTitle);
        releaseDate.setText("Release Date: " + movie.releaseDate);
        VoteAve.setText("Rating: " + movie.voteAverage);
        String Movieurl = "http://image.tmdb.org/t/p/w185" + movie.moviePoster;
        Picasso.with(getContext()).load(Movieurl).into(PosterDetail);
        Plot.setText(movie.plotSynopsis);
        review.setText(builder.toString());

        trailerList = new ArrayList<String>(Arrays.asList(movieTrailer));  //load Select trailer string. this is the default for the spinner
        Collections.addAll(trailerList, movie.trailers);        //add trailer keys to list

        spinnerLabels = new String[trailerList.size()];

        for(int x=0; x<spinnerLabels.length; x++){

            if(x==0){
                spinnerLabels[x]="Select a Trailer";
            }else{
                spinnerLabels[x]="Select a Trailer " +x;
            }


        }

        //code for trailer spinner
        trailerAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_trailers, // The name of the layout ID.
                        spinnerLabels);

        // Get a reference to the ListView, and attach this adapter to it.
        Spinner trailerSpinner = (Spinner) rootView.findViewById(R.id.listview_Trailers);
        trailerSpinner.setAdapter(trailerAdapter);

        trailerSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
*/

            return inflater.inflate(R.layout.fragment_movie_detail,container,false);

        }

        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            sortOrder = Utility.getSortOrder(getActivity());

            if(sortOrder.equals("Most Popular")){
                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        MOST_POPULAR_MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
            }else if(sortOrder.equals("Highest Rated")){
                System.out.println("highest rated cursor exe");
                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        HIGHEST_RATED_MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
            }else if(sortOrder.equals("Favorites")){
                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        FAVORITE_MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
            }

            System.out.println("Oncreateloader executed");
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return null;

        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, final Cursor data) {
            System.out.println("On load finished "+data.getCount());
            if (!data.moveToFirst()) { return; }
/*
            if(!data.getString(COL_TRAILER_KEY).equalsIgnoreCase("No Trailer Available")) {
                trailer = new String[data.getCount() + 1];
                trailer[0] = "Select Trailer";
            }else{
                trailer = new String[1];
                trailer[0] = "No Trailer Available";
            }
*/
            String title="No Title Available";
            String releaseDate="No Release Date Available";
            String voteAverage="No Vote Average Available";
            String image=null;
            String overView="No Overview Available";
            sortOrder = Utility.getSortOrder(getActivity());
            System.out.println("Sort Order: "+ sortOrder);
            List<String> lista = new ArrayList<String>();
            lista.add("Select Trailer");

       //     if(sortOrder.equals("Most Popular")) {
                //get information from the cursor
                String movie_ID = data.getString(COL_MOVIE_ID);
                title = data.getString(COL_MOVIE_TITLE);
                releaseDate = data.getString(COL_RELEASE_DATE);
                voteAverage = data.getString(COL_VOTE_AVERAGE);
                image = data.getString(COL_IMAGE);
                overView = data.getString(COL_OVERVIEW);
                System.out.println("Most Popular: "+ title);




            //get trailers
 /*               data.moveToFirst();
                if(!data.getString(COL_TRAILER_KEY).equalsIgnoreCase("No Trailer Available")) {
                    //loop through cursor to get all trailers
                    for (int i = 1; i < data.getCount() + 1; i++) {
                    //    boolean isInArray=false;
                        if(!lista.contains(data.getString(COL_TRAILER_KEY))){
                            lista.add(data.getString(COL_TRAILER_KEY));
                         //   System.out.println("Array Trailer: " + trailer[i]);
                            data.moveToNext();
                        }else{
                            data.moveToNext();
                        }
                    }
                }

            data.moveToFirst();
            if(!data.getString(COL_TRAILER_KEY).equalsIgnoreCase("No Trailer Available")) {
                //loop through cursor to get all trailers
                for (int i = 1; i < data.getCount() + 1; i++) {
                    //    boolean isInArray=false;
                    if(!lista.contains(data.getString(COL_TRAILER_KEY))){
                        lista.add(data.getString(COL_TRAILER_KEY));
                        //   System.out.println("Array Trailer: " + trailer[i]);
                        data.moveToNext();
                    }else{
                        data.moveToNext();
                    }
                }
            }

*/


         //   }else if(sortOrder.equals("Highest Rated")) {
//get information from the cursor
 /*               title = data.getString(COL_MOVIE_TITLE);
                releaseDate = data.getString(COL_RELEASE_DATE);
                voteAverage = data.getString(COL_VOTE_AVERAGE);
                image = data.getString(COL_IMAGE);
                overView = data.getString(COL_OVERVIEW);
                System.out.println("Highest rated: "+ title);
                data.moveToFirst();
                if(!data.getString(COL_TRAILER_KEY).equalsIgnoreCase("No Trailer Available")) {
                    //loop through cursor to get all trailers
                    for (int i = 1; i < data.getCount() + 1; i++) {
                        //    boolean isInArray=false;
                        if(!lista.contains(data.getString(COL_TRAILER_KEY))){
                            lista.add(data.getString(COL_TRAILER_KEY));
                            //   System.out.println("Array Trailer: " + trailer[i]);
                            data.moveToNext();
                        }else{
                            data.moveToNext();
                        }
                    }
                }
            }else if(sortOrder.equals("Favorites")) {
                //get information from the cursor
                title = data.getString(COL_MOVIE_TITLE);
                releaseDate = data.getString(COL_RELEASE_DATE);
                voteAverage = data.getString(COL_VOTE_AVERAGE);
                image = data.getString(COL_IMAGE);
                overView = data.getString(COL_OVERVIEW);

                data.moveToFirst();
                if(!data.getString(COL_TRAILER_KEY).equalsIgnoreCase("No Trailer Available")) {

                    //loop through cursor to get all trailers
                    for (int i = 1; i < data.getCount() + 1; i++) {
                        trailer[i] = data.getString(COL_TRAILER_KEY);
                        System.out.println("Array Trailer: " + trailer[i]);
                        data.moveToNext();
                    }
                }
            }

*/
 /*           if(lista.size()<1){
                trailer = new String[1];
                trailer[0] = "No Trailer Available";
            }else{
                trailer = lista.toArray(new String[lista.size()]);
            }  */

            //get trailers and put into an array
            trailer = Utility.getTrailers(data);
            reviews = Utility.getReviews(data, getContext());

                //create labels in spinner
            spinnerLabels = new String[trailer.length];

            for(int x=0; x<spinnerLabels.length; x++){

                if(x==0&&spinnerLabels.length==1) {
                    spinnerLabels[x] = "No Trailer Available";
                }else if(x==0&&spinnerLabels.length>1){
                    spinnerLabels[x] = "Select A Trailer";
                }else if(spinnerLabels.length>1){
                    spinnerLabels[x]="Select Trailer " +x;
                }
            }

            //format reviews into a string to be passed to the review textview
            StringBuilder builder = new StringBuilder();
            for(String s : reviews) {
                builder.append(s);
            }

            //add to layouts
            TextView detailTextView = (TextView)getView().findViewById(R.id.textTitle);
            TextView releaseDateTextView = (TextView)getView().findViewById(R.id.textReleaseDate);
            TextView voteAverageTextView = (TextView)getView().findViewById(R.id.textVoteAve);
            ImageView PosterDetail = (ImageView) getView().findViewById(R.id.moviePosterDetail);
            TextView overviewTextView = (TextView)getView().findViewById(R.id.textPlot);
            TextView review = (TextView) getView().findViewById(R.id.textReviews);

            review.setText(builder.toString());


            //code for trailer spinner
            trailerAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_trailers, // The name of the layout ID.
                            spinnerLabels);


            detailTextView.setText(title);
            releaseDateTextView.setText("Release Date: " + releaseDate);
            voteAverageTextView.setText("Rating: " + voteAverage);
            String Movieurl = "http://image.tmdb.org/t/p/w185" + image;
            Picasso.with(getContext()).load(Movieurl).into(PosterDetail);
            overviewTextView.setText(overView);

            //put into movie object to be passed to favorites
            movie = new Movie(title,releaseDate,image,voteAverage,overView,movie_ID,trailer,sortOrder, reviews);

            movieTaskDetail = new dataBase(getActivity(),movie);   //create a instance of the database class
            movieInFavorites = movieTaskDetail.getTogglePosition(movie.getId());

            // Get a reference to the ListView, and attach this adapter to it.
            Spinner trailerSpinner = (Spinner) getView().findViewById(R.id.listview_Trailers);
            trailerSpinner.setAdapter(trailerAdapter);

            trailerSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());


            System.out.println("here is the movie title: " + trailer);

            //add favorites
            toggle = (ToggleButton) getView().findViewById(R.id.addToFavorites);

            if(movieInFavorites.equals(true)){
                toggle.setChecked(true);
            }

            //set onclick listener for toggle button
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        togglePosition = true;
                        movieTaskDetail = new dataBase(getActivity(), movie);   //create a instance of the database class
                        movieTaskDetail.setTogglePosition(togglePosition);
                        movieTaskDetail.execute(movie);
                    } else {
                        // The toggle is disabled
                        togglePosition = false;
                        movieTaskDetail = new dataBase(getActivity(), movie);   //create a instance of the database class
                        movieTaskDetail.setTogglePosition(togglePosition);
                        movieTaskDetail.execute(movie);
                    }
                }
            });






            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            //    if (mShareActionProvider != null) {
            //      mShareActionProvider.setShareIntent(createShareForecastIntent());
            //   }


        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

        }

        public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {       //listener for the trailer spinner
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = trailer[pos];
                System.out.println("Selected trailer " +selected);
                if(selected!="Select Trailer"&&!selected.equalsIgnoreCase("No Trailer Available")){     //do not attend to load the default text
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + selected)));
                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }

        }
    }



}
