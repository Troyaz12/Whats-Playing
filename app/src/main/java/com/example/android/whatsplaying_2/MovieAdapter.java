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

    public MovieAdapter(Activity context, Cursor movies, int flags){

        super(context,movies,flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // gets the movie object from the ArrayAdapter at position

           View convertView = LayoutInflater.from(context).inflate(R.layout.movieposter_item,parent,false);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

            String Movieurl = "http://image.tmdb.org/t/p/w185" + cursor.getString(MainActivityFragment.COL_MOVIE_IMAGE);
            ImageView posterView = (ImageView) view.findViewById(R.id.moviePoster);

            Picasso.with(context)
                    .load(Movieurl)
                    .error(R.drawable.noposter)
                    .into(posterView);
    }
}
