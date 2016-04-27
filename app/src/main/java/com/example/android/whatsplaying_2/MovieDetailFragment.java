package com.example.android.whatsplaying_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    private ArrayAdapter<String> trailerAdapter;
    private String[] movieTrailer = {
                    "Select Trailer"};
    public Movie movie;
    Bundle b;
    private String[] reviews;
    List<String> trailerList;
    private String[] spinnerLabels;
    private ArrayAdapter<String> reviewAdapter;
    List<String> reviewList;
    dataBase movieTaskDetail;
    Boolean togglePosition;      //toggle position of add favorite movie toggle button
    ToggleButton toggle;
    Boolean movieInFavorites;

    public MovieDetailFragment() {
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

// Restore state members from saved instance
        if(savedInstanceState == null) {
            // add movie objects to the array adapter
            //grab intent
            Intent i = getActivity().getIntent();
            //get bundle from intent
            b = i.getExtras();

            if (b != null) {
                //get movie object from bundle
                movie = b.getParcelable("selectedMovie");
                reviews = b.getStringArray("selectedMovieReview");
                System.out.println("exe2");
                System.out.println("Movie Title: "+movie.movieTitle);
            }


        }
        else {
            movie = savedInstanceState.getParcelable("movieSaved");
            reviews = savedInstanceState.getStringArray("movieReviewSaved");

            System.out.println("exe");

        }
        movieTaskDetail = new dataBase(getActivity(),movie);   //create a instance of the database class
        movieInFavorites = movieTaskDetail.getTogglePosition(movie.getId());

    }


      @Override
  public void onSaveInstanceState(Bundle outState) {
          super.onSaveInstanceState(outState);
          outState.putParcelable("movieSaved", movie);
          outState.putStringArray("movieReviewSaved", reviews);

      }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container,false);

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


        toggle = (ToggleButton) rootView.findViewById(R.id.addToFavorites);

        if(movieInFavorites.equals(true)){
            toggle.setChecked(true);
        }

        //set onclick listener for toggle button
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    togglePosition=true;
                    movieTaskDetail = new dataBase(getActivity(),movie);   //create a instance of the database class
                    movieTaskDetail.setTogglePosition(togglePosition);
                    movieTaskDetail.execute(movie);
                } else {
                    // The toggle is disabled
                    togglePosition=false;
                    movieTaskDetail = new dataBase(getActivity(),movie);   //create a instance of the database class
                    movieTaskDetail.setTogglePosition(togglePosition);
                    movieTaskDetail.execute(movie);
                }
            }
        });




        return rootView;

    }

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {       //listener for the trailer spinner
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = trailerList.get(pos);

            if(selected!="Select Trailer"){     //do not attend to load the default text
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + selected)));
            }

        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
