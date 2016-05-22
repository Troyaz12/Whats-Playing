package com.example.android.whatsplaying_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TroysMacBook on 5/3/16.
 */
public class Utility {

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_RELEASE_DATE = 2;
    private static final int COL_VOTE_AVERAGE = 3;
    private static final int COL_IMAGE = 4;
    private static final int COL_OVERVIEW = 5;
    private static final int COL_TRAILER_KEY = 6;
    private static final int COL_REVIEW_REVIEW_INFO = 7;
    private static final int COL_REVIEW_AUTHOR = 8;


    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_key),
                context.getString(R.string.default_sort_value));
    }
    public static String[] getTrailers(Cursor data){
        List<String> lista = new ArrayList<String>();
        lista.add("Select Trailer");

        data.moveToFirst();
        if(!data.getString(COL_TRAILER_KEY).equalsIgnoreCase("No Trailer Available")) {
            //loop through cursor to get all trailers
            for (int i = 1; i < data.getCount() + 1; i++) {
                if(!lista.contains(data.getString(COL_TRAILER_KEY))){
                    lista.add(data.getString(COL_TRAILER_KEY));
                    data.moveToNext();
                }else{
                    data.moveToNext();
                }
            }
        }

        String[] ltrailer;
        if(lista.size()<1){
            ltrailer = new String[1];
            ltrailer[0] = "No Trailer Available";
        }else{
            ltrailer = lista.toArray(new String[lista.size()]);
        }

        return ltrailer;
    }

    public static String[] getReviews(Cursor data, Context context){
        List<String> lista = new ArrayList<String>();

        data.moveToFirst();

        if(!Utility.getSortOrder(context).equalsIgnoreCase("Favorites")) {
            if (!data.getString(COL_REVIEW_AUTHOR).equalsIgnoreCase("No Reviews Available")) {
                //loop through cursor to get all reviews
                for (int i = 0; i < data.getCount(); i++) {
                    //    boolean isInArray=false;

                    String insertReviewAuthor = data.getString(COL_REVIEW_AUTHOR);
                    String insertReview = data.getString(COL_REVIEW_REVIEW_INFO);
                    String totalReview = context.getString(R.string.Author) + insertReviewAuthor + "\n" + "\n" + insertReview + "\n" + "\n";

                    if (!lista.contains(totalReview)) {
                        lista.add(totalReview);
                        data.moveToNext();
                    } else {
                        data.moveToNext();
                    }
                }
            }
        }else{
            if (!data.getString(COL_REVIEW_REVIEW_INFO).equalsIgnoreCase("No Reviews Available")) {
                //loop through cursor to get all trailers
                for (int i = 0; i < data.getCount(); i++) {
                    String insertReview = data.getString(COL_REVIEW_REVIEW_INFO);
                    String totalReview = insertReview + "\n" + "\n";
                    if (!lista.contains(totalReview)) {
                        System.out.println(totalReview);
                        lista.add(totalReview);
                        data.moveToNext();
                    } else {
                        data.moveToNext();
                    }
                }
            }
        }
        String[] reviews;
        if(lista.size()<1){
            reviews = new String[1];
            reviews[0] = "No Review Available";
        }else{
            reviews = lista.toArray(new String[lista.size()]);
        }

        return reviews;
    }


}
