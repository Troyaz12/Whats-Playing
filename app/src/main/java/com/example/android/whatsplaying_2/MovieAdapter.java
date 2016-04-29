package com.example.android.whatsplaying_2;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by TroysMacBook on 1/18/16.
 */
public class MovieAdapter extends CursorAdapter {

    Movie[] movies;
    public MovieAdapter(Activity context, Cursor movies, int flags){
        super(context,movies,flags);

    }
    private String getMovie(Cursor cur){

        //get cursor count, set up movie array to store values in.
        int cursorRecords = cur.getCount();
        System.out.println("records" +cursorRecords);

        int cursorRecords2;
        Movie[] moviesPlaying = new Movie[cursorRecords];

        String insertMovie_id;
        String insertMovieTitle;
        String insertMoviePoster=null;
        String insertMoviePlotSynopsis;
        String insertMovieVoteAverage;
        String insertMovieReleaseDate;
        Long insertTableID;
        Long insertMovie_Selected_trailer_id;
        String insertTrailer=null;

        String trailers[]=null;
        String sortOrder=null;

    //    if (cur.moveToFirst()) {

     //       for(int i=0; i<cursorRecords; i++){

                insertTableID =  cur.getLong(cur.getColumnIndex("_id"));
                insertMovie_id = cur.getString(cur.getColumnIndex("movie_id"));
                insertMovieTitle= cur.getString(cur.getColumnIndex("title"));
                insertMoviePoster= cur.getString(cur.getColumnIndex("image"));
                insertMoviePlotSynopsis= cur.getString(cur.getColumnIndex("overview"));
                insertMovieVoteAverage= cur.getString(cur.getColumnIndex("vote_average"));
                insertMovieReleaseDate= cur.getString(cur.getColumnIndex("release_date"));

       //         moviesPlaying[i] = new Movie(insertMovieTitle, insertMovieReleaseDate, insertMoviePoster, insertMovieVoteAverage, insertMoviePlotSynopsis, insertMovie_id, insertTableID,trailers,sortOrder);
//
       //         cur.moveToNext();
       //     }


    //    }

        return insertMoviePoster;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // gets the movie object from the ArrayAdapter at position

           View convertView = LayoutInflater.from(context).inflate(R.layout.movieposter_item,parent,false);
         //  movies = getMovie(cursor);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
     //   int position = cursor.getPosition();
     //   Movie movies = (Movie) getItem(position);


        String Movieurl = "http://image.tmdb.org/t/p/w185" + getMovie(cursor);
        ImageView posterView = (ImageView) view.findViewById(R.id.moviePoster);


        Picasso.with(context)
                .load(Movieurl)
                .into(posterView);


    }
}
