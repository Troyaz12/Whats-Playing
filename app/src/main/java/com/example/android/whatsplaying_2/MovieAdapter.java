package com.example.android.whatsplaying_2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by TroysMacBook on 1/18/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity context, List<Movie> movies){
        super(context,0,movies);

    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // gets the movie object from the ArrayAdapter at position
        Movie movies = getItem(position);
        String Movieurl = "http://image.tmdb.org/t/p/w185" + movies.moviePoster;
        System.out.println("MoviePoster url: " + Movieurl);


        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movieposter_item,parent,false);
        }
        ImageView posterView = (ImageView) convertView.findViewById(R.id.moviePoster);


        Picasso.with(getContext())
                .load(Movieurl)
                .into(posterView);

        return convertView;


    }



}
