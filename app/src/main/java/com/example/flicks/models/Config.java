package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    String imageBaseURL;  // the base URL for loading images
    String posterSize;    // the poster size when fetching images, part of the URL
    String backdropSize;  // the backdrop size to use when fetching images


    public Config(JSONObject object) throws JSONException {
        // Parse images object
        JSONObject images = object.getJSONObject("images");
        imageBaseURL = images.getString("secure_base_url");  // get the image base url
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");  // get the poster size
        posterSize = posterSizeOptions.optString(3, "w342");  // use "w342" as default

        // Get the backdrop size
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");  // use "w780" as default
    }

    // Helper method for creating urls
    public String getImageUrl(String size, String path){
        return String.format("%s%s%s",imageBaseURL,size,path);  // concatenate all three
    }

    // Return image base url
    public String getImageBaseURL() {
        return imageBaseURL;
    }

    // Return poster size
    public String getPosterSize() {
        return posterSize;
    }

    // Return backdrop size
    public String getBackdropSize() { return backdropSize; }
}
