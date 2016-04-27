package com.example.android.whatsplaying_2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by TroysMacBook on 3/8/16.
 */
public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.example.android.whatsplaying_2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOST_POPULAR = "popular";
    public static final String PATH_MOST_POPULAR_TRAILERS = "most_popular_trailers";
    public static final String PATH_MOST_POPULAR_REVIEWS = "most_popular_reviews";

    public static final String PATH_HIGHEST_RATED = "ratings";
    public static final String PATH_HIGHEST_RATED_REVIEWS = "highest_rated_reviews";
    public static final String PATH_HIGHEST_RATED_TRAILERS = "highest_rated_trailers";

    public static final String PATH_FAVORITE_TRAILERS = "favorite_trailers";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_FAVORITE_REVIEWS = "favorite_reviews";

    public static final class MostPopularEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;

        //table name
        public static final String TABLE_NAME = "popular";

        //holds the id of the movie
        public static final String MOVIE_ID = "movie_id";

        //holds the image
        public static final String IMAGE = "image";

        //movie title
        public static final String MOVIE_TITLE = "title";

        //release date
        public static final String RELEASE_DATE = "release_date";

        //vote average
        public static final String VOTE_AVERAGE = "vote_average";

        //overview
        public static final String OVERVIEW = "overview";

        //what type of sort,  favorite, popular movies or highest rated
   //     public static final String SORT_ORDER = "sort_order";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri sortOrder(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMostPopularMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(0);
        }

    }

    public static final class MostPopularTrailers implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR_TRAILERS;

        //table name
        public static final String TABLE_NAME = "most_popular_trailers";

        //holds the id of the movie
        public static final String MOVIE_SELECTED_TRAILER = "movie_selected_Trailer";

        //trailers
        public static final String TRAILER_KEY = "trailer_key";

        public static Uri buildMostPopularTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildTrailer(long movieSelected, long movieSelected2) {

            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieSelected))
                    .appendQueryParameter(MOVIE_SELECTED_TRAILER, Long.toString(movieSelected2)).build();
        }
        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getMovieTrailerFromUri(Uri uri) {
            String movieID = uri.getQueryParameter(MOVIE_SELECTED_TRAILER);
            if (null != movieID && movieID.length() > 0)
                return movieID;
            else
                return null;
        }


    }
    public static final class MostPopularReviews implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR_REVIEWS;

        //table name
        public static final String TABLE_NAME = "most_popular_reviews";

        //holds the id of the movie
        public static final String MOVIE_SELECTED_REVIEWS = "movie_selected_reviews";

        //author
        public static final String AUTHOR = "author";

        //review
        public static final String REVIEWS = "reviews";

        public static Uri buildMostPopularReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildReview(long movieSelected, long movieSelected2) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieSelected))
                    .appendQueryParameter(MOVIE_SELECTED_REVIEWS, Long.toString(movieSelected2)).build();
        }
        public static String getMovieReviewsFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }



    public static final class HighestRatedEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HIGHEST_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED;

        //table name
        public static final String TABLE_NAME = "ratings";

        //holds the id of the movie
        public static final String MOVIE_ID = "movie_id";

        //holds the image
        public static final String IMAGE = "image";

        //movie title
        public static final String MOVIE_TITLE = "title";

        //release date
        public static final String RELEASE_DATE = "release_date";

        //vote average
        public static final String VOTE_AVERAGE = "vote_average";

        //overview
        public static final String OVERVIEW = "overview";


        public static Uri buildHighestRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getHighestRatedMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(0);
        }

    }

    public static final class FavoriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        //table name
        public static final String TABLE_NAME = "favorite";

        //holds the id of the movie
        public static final String MOVIE_ID = "movie_id";

        //holds the image
        public static final String IMAGE = "image";

        //movie title
        public static final String MOVIE_TITLE = "title";

        //results
        public static final String RESULTS = "results";

        //release date
        public static final String RELEASE_DATE = "release_date";

        //vote average
        public static final String VOTE_AVERAGE = "vote_average";

        //overview
        public static final String OVERVIEW = "overview";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getFavoriteUri(Uri uri) {
            return uri.getPathSegments().get(0);
        }


    }


    public static final class HighestRatedTrailers implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HIGHEST_RATED_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED_TRAILERS;

        //table name
        public static final String TABLE_NAME = "highest_rated_trailers";

        //holds the id of the movie
        public static final String MOVIE_SELECTED_TRAILER = "movie_selected_Trailer";

        //trailers
        public static final String TRAILER_KEY = "trailer_key";

        public static Uri buildhighestRatedTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildTrailer(long movieSelected, long movieSelected2) {

            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieSelected))
                    .appendQueryParameter(MOVIE_SELECTED_TRAILER, Long.toString(movieSelected2)).build();
        }
        public static String getMovieFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getMovieTrailerFromUri(Uri uri) {
            String movieID = uri.getQueryParameter(MOVIE_SELECTED_TRAILER);
            if (null != movieID && movieID.length() > 0)
                return movieID;
            else
                return null;
        }
    }

    public static final class HighestRatedReviews implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HIGHEST_RATED_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED_REVIEWS;

        //table name
        public static final String TABLE_NAME = "highest_rated_reviews";

        //holds the id of the movie
        public static final String MOVIE_SELECTED_REVIEWS = "movie_selected_reviews";

        //author
        public static final String AUTHOR = "author";

        //review
        public static final String REVIEWS = "reviews";

        public static Uri buildHighestRatedReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildReview(long movieSelected, long movieSelected2) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieSelected))
                    .appendQueryParameter(MOVIE_SELECTED_REVIEWS, Long.toString(movieSelected2)).build();
        }
        public static String getMovieHighestReviewsFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    public static final class FavoriteReviews implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_REVIEWS;

        //table name
        public static final String TABLE_NAME = "favorite_reviews";

        //holds the id of the movie
        public static final String MOVIE_SELECTED_REVIEWS = "movie_selected_reviews";

        //author
        public static final String AUTHOR = "author";

        //review
        public static final String REVIEWS = "reviews";

        public static Uri buildFavoriteReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildReview(long movieSelected, long movieSelected2) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieSelected))
                    .appendQueryParameter(MOVIE_SELECTED_REVIEWS, Long.toString(movieSelected2)).build();
        }
        public static String getMovieFavoriteReviewsFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    public static final class FavoriteTrailers implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_TRAILERS;

        //table name
        public static final String TABLE_NAME = "favorite_trailers";

        //holds the id of the movie
        public static final String MOVIE_SELECTED_TRAILER = "movie_selected_Trailer";

        //trailers
        public static final String TRAILER_KEY = "trailer_key";

        public static Uri FavoriteTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoriteTrailersDetail(String movieID){
            return CONTENT_URI.buildUpon().appendPath(movieID).build();

        }
        public static Uri buildTrailer(long movieSelected, long movieSelected2) {

            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieSelected))
                    .appendQueryParameter(MOVIE_SELECTED_TRAILER, Long.toString(movieSelected2)).build();
        }
        public static String getFavoriteFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getFavoriteTrailerFromUri(Uri uri) {
            String movieID = uri.getQueryParameter(MOVIE_SELECTED_TRAILER);
            if (null != movieID && movieID.length() > 0)
                return movieID;
            else
                return null;
        }


    }
}
