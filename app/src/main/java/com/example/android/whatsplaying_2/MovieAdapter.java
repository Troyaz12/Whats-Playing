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

 //   Movie[] movies;
 //   Movie[] moviesFromCursor;
    public MovieAdapter(Activity context, Cursor movies, int flags){

        super(context,movies,flags);

    }
 /*   private Movie[] getMovie(Cursor cur){

        //get cursor count, set up movie array to store values in.
        int cursorRecords = cur.getCount();
        System.out.println("records" +cursorRecords);

        int cursorRecords2;
        Movie[] moviesPlaying = new Movie[cursorRecords];

        String insertMovie_id=null;
        String insertMovieTitle=null;
        String insertMoviePoster=null;
        String insertMoviePlotSynopsis=null;
        String insertMovieVoteAverage=null;
        String insertMovieReleaseDate=null;
        String insertTrailerKey=null;
        Long insertTableID=null;
        Long insertMovie_Selected_trailer_id;
        String insertTrailer=null;
        List<String> trailerList = new ArrayList<String>();
        int MovieListIndex=0;                       //keeps track of number of movies in the movie array

        String trailers[]={"No Trailers"};
        String sortOrder=null;

        if (cur.moveToFirst()) {

          //  insertTableID = cur.getLong(cur.getColumnIndex("_id"));
            insertMovie_id = cur.getString(cur.getColumnIndex("movie_id"));
            insertMovieTitle = cur.getString(cur.getColumnIndex("title"));
            insertMoviePoster = cur.getString(cur.getColumnIndex("image"));
            insertMoviePlotSynopsis = cur.getString(cur.getColumnIndex("overview"));
            insertMovieVoteAverage = cur.getString(cur.getColumnIndex("vote_average"));
            insertMovieReleaseDate = cur.getString(cur.getColumnIndex("release_date"));

            for(int i=0; i<cursorRecords; i++){
                String newID = cur.getString(cur.getColumnIndex("movie_id"));
                if(!insertMovie_id.equals(newID)) {

                    if(trailerList!=null)
                        trailerList.toArray(trailers);
                    System.out.println("Index in movie array: "+MovieListIndex);
                    System.out.println("Movie Poster: "+ insertMoviePoster+ insertMovieTitle + insertMovie_id+ "movie ID: "+insertMovie_id+" "+insertMoviePlotSynopsis + " "+insertMovieReleaseDate+ " "+insertMovieVoteAverage);
                    moviesPlaying[MovieListIndex] = new Movie(insertMovieTitle, insertMovieReleaseDate, insertMoviePoster, insertMovieVoteAverage, insertMoviePlotSynopsis, insertMovie_id, trailers, sortOrder);
                    MovieListIndex++;
                    trailerList.clear();

                 //   insertTableID = cur.getLong(cur.getColumnIndex("_id"));
                    insertMovie_id = cur.getString(cur.getColumnIndex("movie_id"));
                    insertMovieTitle = cur.getString(cur.getColumnIndex("title"));
                    insertMoviePoster = cur.getString(cur.getColumnIndex("image"));
                    insertMoviePlotSynopsis = cur.getString(cur.getColumnIndex("overview"));
                    insertMovieVoteAverage = cur.getString(cur.getColumnIndex("vote_average"));
                    insertMovieReleaseDate = cur.getString(cur.getColumnIndex("release_date"));

                }
                    insertTrailerKey= cur.getString(cur.getColumnIndex("trailer_key"));
                    trailerList.add(insertTrailerKey);

                if(i==cursorRecords-1) {
                    moviesPlaying[MovieListIndex] = new Movie(insertMovieTitle, insertMovieReleaseDate, insertMoviePoster, insertMovieVoteAverage, insertMoviePlotSynopsis, insertMovie_id, trailers, sortOrder);
                    System.out.println("Movie Poster: "+ insertMoviePoster+ insertMovieTitle + insertMovie_id+ "movie ID: "+insertMovie_id+" "+insertMoviePlotSynopsis + " "+insertMovieReleaseDate+ " "+insertMovieVoteAverage);
                    System.out.println("Index in movie array: "+MovieListIndex);

                }
                cur.moveToNext();
            }
        }
                movies = new Movie[MovieListIndex];
                for(int i=0; i<MovieListIndex;i++){
                    movies[i] = moviesPlaying[i];
                    System.out.println(i+"Movie Title:"+movies[i].getMovieTitle());
                }

        return movies;
    }  */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // gets the movie object from the ArrayAdapter at position

           View convertView = LayoutInflater.from(context).inflate(R.layout.movieposter_item,parent,false);
         //  movies = getMovie(cursor);
    //    moviesFromCursor = getMovie(cursor);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();
       // System.out.println("Position of cursor: "+position);
       // Movie movies = (Movie) getItem(position);


   //     if(position<moviesFromCursor.length){
     //       System.out.println("Position of cursor: " + moviesFromCursor.length);
            String Movieurl = "http://image.tmdb.org/t/p/w185" + cursor.getString(MainActivityFragment.COL_MOVIE_IMAGE);
            ImageView posterView = (ImageView) view.findViewById(R.id.moviePoster);


            Picasso.with(context)
                    .load(Movieurl)
                    .error(R.drawable.noposter)
                    .into(posterView);
        // }

    }
}
