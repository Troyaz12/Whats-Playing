package com.example.android.whatsplaying_2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.whatsplaying_2.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class HighestRated extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int HIGHEST_RATED_MOVIE_LOADER = 0;
    private MovieAdapter movieHighestRatedAdapter;
    Uri moviehighestRatedDetailsURI =null;

    private static final String[] MOVIE_COLUMNS_HIGHEST_RATED = {
            MovieContract.HighestRatedEntry.TABLE_NAME+"."+MovieContract.HighestRatedEntry._ID,
            MovieContract.HighestRatedEntry.MOVIE_ID,
            MovieContract.HighestRatedEntry.IMAGE
    };


    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_MOVIE_ID = 1;
    static final int COL_MOVIE_IMAGE = 2;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */

        void onItemSelected(Uri movieUri);
    }

    public HighestRated() {
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieHighestRatedAdapter = new MovieAdapter(getActivity(),null,0);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        GridView gridViewHighestRated = (GridView) rootView.findViewById(R.id.gridview);
        gridViewHighestRated.setAdapter(movieHighestRatedAdapter);

        gridViewHighestRated.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.


                   final Cursor cursorHighestRated = (Cursor) adapterView.getItemAtPosition(i);
                        if (cursorHighestRated != null) {

                                ((HighestRated.Callback) getActivity())
                                        .onItemSelected(MovieContract.HighestRatedTrailers.buildTrailer(
                                                cursorHighestRated.getLong(COL_MOVIE_ID), cursorHighestRated.getLong(COL_MOVIE_ID)
                                        ));



                        }






    }
        });

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


            moviehighestRatedDetailsURI = MovieContract.HighestRatedEntry.CONTENT_URI;
            return new CursorLoader(getActivity(), moviehighestRatedDetailsURI, MOVIE_COLUMNS_HIGHEST_RATED,null,null,null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieHighestRatedAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieHighestRatedAdapter.swapCursor(null);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(HIGHEST_RATED_MOVIE_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

}



