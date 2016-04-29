package com.example.android.whatsplaying_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.whatsplaying_2.data.MovieContract;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MovieAdapter movieAdapter;
    public ArrayList<Movie> mList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    public loadMovieInfo movieTask;
    public String sortOrder;
    String[] reviews;

    public MainActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            // add movie objects to the array adapter
            mList = new ArrayList<Movie>();
        }
        else {
            mList = savedInstanceState.getParcelableArrayList("movies");
            System.out.println("exe1");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mList);
        super.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = prefs.getString(getString(R.string.sort_key),
                getString(R.string.default_sort_value));

        Uri movieDetailsURI=null;
        if(sortOrder.equals("Most Popular")) {
            movieDetailsURI = MovieContract.MostPopularEntry.CONTENT_URI;
        }else if(sortOrder.equals("Highest Rated")){
            movieDetailsURI = MovieContract.HighestRatedEntry.CONTENT_URI;
        }else if(sortOrder.equals("Favorites")){
            movieDetailsURI = MovieContract.FavoriteEntry.CONTENT_URI;
        }

        Cursor cur = getActivity().getContentResolver().query(movieDetailsURI, //pull only movies that user wants to see, either most popular or highest rated
                null, null,null, null);

        movieAdapter = new MovieAdapter(getActivity(),cur,0);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie item = (Movie) movieAdapter.getItem(i);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //after movie is selected, get movie reviews
                try {
                    reviews = movieTask.getMovieReview(item.tableID,item.id);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //create intent for placeholder fragment
                Intent intent = new Intent();

                intent.setClass(getActivity(), MovieDetail.class)
                        .putExtra("selectedMovie",item)
                        .putExtra("selectedMovieReview", reviews);
                startActivity(intent);

            }
        });

        return rootView;
    }
    
    public void onStart(){
        //pull Json information
        loadMovies();
        super.onStart();

    }

    public void loadMovies(){

        movieTask = new loadMovieInfo(getActivity());
        movieTask.execute(sortOrder);

    }



}


