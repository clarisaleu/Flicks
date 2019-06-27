package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    String imageBaseURL;  // the base URL for loading images
    String posterSize;    // the poster size when fetching images, part of the URL
    String backdropSize;  // the backdrop size to use when fetching images

    public Config(JSONObject object) throws JSONException {
        // parse images object
        JSONObject images = object.getJSONObject("images");
        // get the image base url
        imageBaseURL = images.getString("secure_base_url");
        // get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        posterSize = posterSizeOptions.optString(3, "w342");  // use "w342" as default

        // get the backdrop size
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_size");
        backdropSize = backdropSizeOptions.optString(1, "w780");  // use "w780" as default
    }

    // helper method for creating urls
    public String getImageUrl(String size, String path){
        return String.format("%s%s%s",imageBaseURL,size,path);  // concatenate all three
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
