package com.example.android.whatsplaying_2;

//import android.app.LoaderManager;
//import android.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.whatsplaying_2.data.MovieContract;


//import android.content.Loader;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int Popular_MOVIE_LOADER = 0;
    private static final int Highest_MOVIE_LOADER = 1;
    public Handler handler = new Handler();
    private MovieAdapter movieAdapter;
    public String mList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    public String sortOrder;

    private static final String[] MOVIE_COLUMNS_MOST_POPULAR = {
      MovieContract.MostPopularEntry.TABLE_NAME+"."+MovieContract.MostPopularEntry._ID,
            MovieContract.MostPopularEntry.MOVIE_ID,
            MovieContract.MostPopularEntry.IMAGE
    };


    private static final String[] MOVIE_COLUMNS_HIGHEST_RATED = {
            MovieContract.HighestRatedEntry.TABLE_NAME+"."+MovieContract.HighestRatedEntry._ID,
            MovieContract.HighestRatedEntry.MOVIE_ID,
            MovieContract.HighestRatedEntry.IMAGE
    };

    private static final String[] MOVIE_COLUMNS_FAVORITES = {
            MovieContract.FavoriteEntry.TABLE_NAME+"."+ MovieContract.FavoriteEntry.MOVIE_ID,
            MovieContract.FavoriteEntry.IMAGE
    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_MOVIE_ID = 1;
    static final int COL_MOVIE_IMAGE = 2;

    public MainActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sortOrder = Utility.getSortOrder(getActivity());

        if(savedInstanceState == null) {
            // add movie objects to the array adapter
      //      mList = sortOrder;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
      //  outState.putParcelableArrayList("movies", mList);
        super.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieAdapter = new MovieAdapter(getActivity(),null,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                if(sortOrder.equals("Most Popular")) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                        if (cursor != null) {
                            System.out.println("movie id is: "+cursor.getLong(COL_MOVIE_ID)+"URI"+MovieContract.MostPopularTrailers.buildTrailer(
                                    cursor.getLong(COL_MOVIE_ID), cursor.getLong(COL_MOVIE_ID)
                            ));
                            Intent intent = new Intent(getActivity(), MovieDetail.class)
                                    .setData(MovieContract.MostPopularTrailers.buildTrailer(
                                            cursor.getLong(COL_MOVIE_ID), cursor.getLong(COL_MOVIE_ID)
                                    ))
                                    .putExtra("sortOrder", sortOrder);
                            startActivity(intent);
                        }
                }else if(sortOrder.equals("Highest Rated")){
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

                    if (cursor != null) {
                        System.out.println("movie id is: " + cursor.getLong(COL_MOVIE_ID) + "URI" + MovieContract.HighestRatedTrailers.buildTrailer(
                                cursor.getLong(COL_MOVIE_ID), cursor.getLong(COL_MOVIE_ID)
                        ));
                        Intent intent = new Intent(getActivity(), MovieDetail.class)
                                .setData(MovieContract.HighestRatedTrailers.buildTrailer(
                                        cursor.getLong(COL_MOVIE_ID), cursor.getLong(COL_MOVIE_ID)
                                ))
                                .putExtra("sortOrder", sortOrder);
                        startActivity(intent);
                    }

                }else if(sortOrder.equals("Favorites")) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                    if (cursor != null) {
                        System.out.println("movie id is: "+cursor.getLong(COL_MOVIE_ID)+"URI"+MovieContract.FavoriteTrailers.buildTrailer(
                                cursor.getLong(COL_MOVIE_ID), cursor.getLong(COL_MOVIE_ID)
                        ));
                        Intent intent = new Intent(getActivity(), MovieDetail.class)
                                .setData(MovieContract.FavoriteTrailers.buildTrailer(
                                        cursor.getLong(COL_MOVIE_ID), cursor.getLong(COL_MOVIE_ID)
                                ));
                        startActivity(intent);
                    }
                }
            }
        });

        return rootView;
    }

    void onSortChanged(){
        Uri movieDetailsURI=null;
        Cursor checkTable=null;
        sortOrder = Utility.getSortOrder(getActivity());

        if(sortOrder.equals("Most Popular")) {
            movieDetailsURI = MovieContract.MostPopularEntry.CONTENT_URI;

            checkTable = getActivity().getContentResolver().query(movieDetailsURI,
                    null, null, null, sortOrder);
            System.out.println("sort order amount in cursor: " + checkTable.getCount());

        }else if(sortOrder.equals("Highest Rated")){
            movieDetailsURI = MovieContract.HighestRatedEntry.CONTENT_URI;
            checkTable = getActivity().getContentResolver().query(movieDetailsURI,
                    null, null, null, sortOrder);
            System.out.println("sort order amount in cursor: " + sortOrder);

        }
//movies will not reload if they are already in the database
    if(checkTable.getCount()<1&&sortOrder.equals("Highest Rated")) {
        Toast.makeText(getActivity(), "Data loading, please wait.",
                Toast.LENGTH_LONG).show();
        getLoaderManager().restartLoader(Popular_MOVIE_LOADER, null, this);
        System.out.println("loader manager executed: ");

    }else{
        getLoaderManager().restartLoader(Popular_MOVIE_LOADER, null, this);
    }


    }
  /*  public void loadMovies() {
        MovieSyncAdapter.syncImmediately(getActivity());
        System.out.println("loadMovies synced");
    }   */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        sortOrder = Utility.getSortOrder(getActivity());

        Uri movieDetailsURI=null;
        if(sortOrder.equals("Most Popular")) {
            movieDetailsURI = MovieContract.MostPopularEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),movieDetailsURI, MOVIE_COLUMNS_MOST_POPULAR,null,null,null);
        }else if(sortOrder.equals("Highest Rated")){
            movieDetailsURI = MovieContract.HighestRatedEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),movieDetailsURI, MOVIE_COLUMNS_HIGHEST_RATED,null,null,null);
        }else if(sortOrder.equals("Favorites")){
            movieDetailsURI = MovieContract.FavoriteEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),movieDetailsURI, MOVIE_COLUMNS_FAVORITES,null,null,null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(Popular_MOVIE_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

}



