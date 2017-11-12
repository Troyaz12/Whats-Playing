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

            Bundle args = new Bundle();
            args.putParcelable(MovieDetail.MovieDetailFragment.DETAIL_URI, getIntent().getData());          //get uri for one pane mode

            MovieDetail.MovieDetailFragment fragment = new MovieDetail.MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                 .add(R.id.movie_detail_container,fragment)
                    .commit();
        }
    }

    public static class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private ArrayAdapter<String> trailerAdapter;
        public Movie movie;
        private String[] spinnerLabels;
        dataBase movieTaskDetail;
        Boolean togglePosition;      //toggle position of add favorite movie toggle button
        ToggleButton toggle;
        Boolean movieInFavorites;
        public String[] trailer;
        public String[] reviews;
        static final String DETAIL_URI = "URI";
        private static final int DETAIL_LOADER = 0;
        private Uri mUri;

        TextView detailTextView;
        TextView releaseDateTextView;
        TextView voteAverageTextView;
        ImageView PosterDetail;
        TextView overviewTextView;
        TextView review;


        private static final String[] MOST_POPULAR_MOVIE_COLUMNS = {
                MovieContract.MostPopularEntry.TABLE_NAME + "." + MovieContract.MostPopularEntry.MOVIE_ID,
                MovieContract.MostPopularEntry.MOVIE_TITLE,
                MovieContract.MostPopularEntry.RELEASE_DATE,
                MovieContract.MostPopularEntry.VOTE_AVERAGE,
                MovieContract.MostPopularEntry.IMAGE,
                MovieContract.MostPopularEntry.OVERVIEW,
                MovieContract.MostPopularTrailers.TRAILER_KEY,
                MovieContract.MostPopularReviews.REVIEWS,
                MovieContract.MostPopularReviews.AUTHOR

        };

        private static final String[] HIGHEST_RATED_MOVIE_COLUMNS = {
                MovieContract.HighestRatedEntry.TABLE_NAME + "." + MovieContract.HighestRatedEntry.MOVIE_ID,
                MovieContract.HighestRatedEntry.MOVIE_TITLE,
                MovieContract.HighestRatedEntry.RELEASE_DATE,
                MovieContract.HighestRatedEntry.VOTE_AVERAGE,
                MovieContract.HighestRatedEntry.IMAGE,
                MovieContract.HighestRatedEntry.OVERVIEW,
                MovieContract.HighestRatedTrailers.TRAILER_KEY,
                MovieContract.HighestRatedReviews.REVIEWS,
                MovieContract.HighestRatedReviews.AUTHOR

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
        private static final int COL_REVIEW_REVIEW_INFO = 7;
        private static final int COL_REVIEW_AUTHOR = 8;



        public MovieDetailFragment() {
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(MovieDetail.MovieDetailFragment.DETAIL_URI);
            }

            View rootView = inflater.inflate(R.layout.fragment_movie_detail,container,false);


            //add to layouts
            detailTextView = (TextView)rootView.findViewById(R.id.textTitle);
            releaseDateTextView = (TextView)rootView.findViewById(R.id.textReleaseDate);
            voteAverageTextView = (TextView)rootView.findViewById(R.id.textVoteAve);
            PosterDetail = (ImageView) rootView.findViewById(R.id.moviePosterDetail);
            overviewTextView = (TextView)rootView.findViewById(R.id.textPlot);
            review = (TextView) rootView.findViewById(R.id.textReviews);

            return rootView;

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
            if ( null != mUri ) {

                //find out what table to pull the movie data from
                Uri breakupUri = Uri.parse(mUri.toString());
                String[] path = breakupUri.getPath().split("/");
                String table = path[ path.length - 2 ];


                if (table.equals("most_popular_trailers")) {
                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            MOST_POPULAR_MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                } else if (table.equals("highest_rated_trailers")) {
                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            HIGHEST_RATED_MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                } else if (table.equals("favorite_trailers")) {

                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            FAVORITE_MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
            }
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return null;

        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, final Cursor data) {
            if (!data.moveToFirst()) {
                String title="Data is downloading. Please wait.";
                detailTextView.setText(title);


                return; }

            List<String> lista = new ArrayList<String>();
            lista.add("Select Trailer");

                //get information from the cursor
                String movie_ID = data.getString(COL_MOVIE_ID);
                String title = data.getString(COL_MOVIE_TITLE);
                String releaseDate = data.getString(COL_RELEASE_DATE);
                String voteAverage = data.getString(COL_VOTE_AVERAGE);
                String image = data.getString(COL_IMAGE);
                String overView = data.getString(COL_OVERVIEW);

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
            movie = new Movie(title,releaseDate,image,voteAverage,overView,movie_ID,trailer,"null", reviews);

            movieTaskDetail = new dataBase(getActivity(),movie);   //create a instance of the database class
            movieInFavorites = movieTaskDetail.getTogglePosition(movie.getId());

            // Get a reference to the ListView, and attach this adapter to it.
            Spinner trailerSpinner = (Spinner) getView().findViewById(R.id.listview_Trailers);
            trailerSpinner.setAdapter(trailerAdapter);

            trailerSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

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

        }
        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {


        }

        public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {       //listener for the trailer spinner
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = trailer[pos];
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
