package me.appfolio.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

class Movie implements Parcelable {

    private String title;
    private String original_title;
    private String overview;
    private String poster_path;
    private String release_date;
    private double popularity;
    private double vote_average;
    private int vote_count;

    Movie() {}

    private Movie(Parcel in) {
        title = in.readString();
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        vote_count = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getOriginalTitle() {
        return original_title;
    }

    void setOriginalTitle(String original_title) {
        this.original_title = original_title;
    }

    String getOverview() {
        return overview;
    }

    void setOverview(String overview) {
        this.overview = overview;
    }

    String getPosterPath() {
        return poster_path;
    }

    void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    String getReleaseDate() {
        return release_date;
    }

    void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    double getPopularity() {
        return popularity;
    }

    void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    double getVoteAverage() {
        return vote_average;
    }

    void setVoteAverage(double vote_average) {
        this.vote_average = vote_average;
    }

    int getVoteCount() {
        return vote_count;
    }

    void setVoteCount(int vote_count) {
        this.vote_count = vote_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeInt(vote_count);
    }
}
