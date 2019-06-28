package com.example.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel  // annotation indicate class is Parcelable
public class Movie {

    // Values from API (required public for parceler):
    String title;
    String overview;
    String posterPath;  // only the path
    String backdropPath;
    Integer id;  // for playing movie trailer
    Double voteAverage;

    // no-arg, empty constructor required for parceler
    public Movie() {}

    // Initialize from JSON data:
    public  Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
        id = object.getInt("id");
    }

    // Return the average vote
    public Double getVoteAverage() { return voteAverage; }

    // Return backdrop path
    public String getBackdropPath() { return backdropPath; }

    // Return movie title
    public String getTitle() { return title; }

    // Return movie id
    public Integer getId() { return id; }

    // Return movie synopsis
    public String getOverview() {
        return overview;
    }

    // Return movie poster path
    public String getPosterPath() {
        return posterPath;
    }
}
