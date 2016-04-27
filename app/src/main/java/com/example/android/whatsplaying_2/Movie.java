package com.example.android.whatsplaying_2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TroysMacBook on 1/18/16.
 */
public class Movie implements Parcelable{
    String movieTitle;
    String releaseDate;
    String moviePoster; //drawable reference id
    String voteAverage;
    String plotSynopsis;
    String id;                  //movie id
    Long tableID;               //id in database table
    String[] trailers;
    String[] reviews;
    String sortOrder;

    public Movie (String lmovieTitle, String lreleaseDate, String lmoviePoster, String lvoteAverage,
                  String lplotSynopsis, String movieId, Long movieTableID, String[] ltrailers, String sort){
        this.movieTitle = lmovieTitle;
        this.releaseDate=lreleaseDate;
        this.moviePoster = lmoviePoster;
        this.voteAverage = lvoteAverage;
        this.plotSynopsis= lplotSynopsis;
        this.id = movieId;
        this.tableID = movieTableID;

        int trailerSize = ltrailers.length;
        trailers = new String[trailerSize];

        for (int x=0; x<trailerSize; x++) {
            this.trailers[x] = ltrailers[x];
            System.out.println("trailers in object: " + trailers[x]);
        }

        this.sortOrder = sort;

    }

    private Movie(Parcel in){
        movieTitle = in.readString();
        releaseDate = in.readString();
        moviePoster=in.readString();
        voteAverage=in.readString();
        plotSynopsis=in.readString();
        id = in.readString();
        tableID = in.readLong();

        trailers= in.createStringArray();
        sortOrder = in.readString();

    }
    private void addReviews(String[] reviewList){
        int reviewSize = reviewList.length;
        reviews = new String[reviewSize];

        for (int x=0; x<reviewSize; x++) {
            this.reviews[x] = reviewList[x];
            System.out.println("reviews parced");
        }


    }

    @Override
    public int describeContents(){
        return 0;
    }
    public String toString() { return movieTitle + "--" + releaseDate + "--" + moviePoster + "--"+voteAverage+"--"+
            plotSynopsis +"--"+id +"--"+tableID+sortOrder; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieTitle);
        parcel.writeString(releaseDate);
        parcel.writeString(moviePoster);
        parcel.writeString(voteAverage);
        parcel.writeString(plotSynopsis);
        parcel.writeString(id);
        parcel.writeLong(tableID);

        parcel.writeInt(trailers.length);
        for( int x=0; x<trailers.length; x++ ){
            parcel.writeString(trailers[x]);
        }
        parcel.writeString(sortOrder);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }
        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public String getMovieTitle(){
        return movieTitle;

    }
    public void setMovieTitle(String title){
        movieTitle=title;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
        public void setReleaseDate(String date){
            releaseDate=date;
        }
    public String getMoviePoster(){
        return moviePoster;
    }

    public void setMoviePoster(String poster){
            moviePoster = poster;
    }
    public String getVoteAverage(){
        return voteAverage;
    }
    public void setVoteAverage(String votes){
        voteAverage=votes;
    }

    public String getPlotSynopsis(){
        return plotSynopsis;
    }
    public void setPlotSynopsis(String plot){
        plotSynopsis = plot;
    }

    public String getId (){
        return id;
    }
    public void setId(String setId){
        id=setId;
    }

    public Long getTableID(){
        return tableID;
    }

    public void setTableID(Long setTableID){
        tableID=setTableID;
    }
    public String[] getTrailers(){

        return trailers;
    }
    public void setTrailers(String[] ltrailers){
        int trailerSize = ltrailers.length;
        trailers = new String[trailerSize];

        for (int x=0; x<trailerSize; x++) {
            this.trailers[x] = ltrailers[x];
            System.out.println("trailers parced");
        }
    }
    public String getSortOrder(){


        return sortOrder;
    }
    public void setSortOrder(String sort){
        sortOrder=sort;
    }

    public String[] getReviews(){

        return reviews;
    }

    public void setReviews(String[] reviewList){
        int reviewSize = reviewList.length;
        reviews = new String[reviewSize];

        for (int x=0; x<reviewSize; x++) {
            this.reviews[x] = reviewList[x];
            System.out.println("reviews parced");
        }
    }

}

