package com.example.flicks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.flicks.models.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.flicks.models.Movie;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends Activity {

    // Constants:
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";  // the parameter name for the API key
    public final static String TAG = "MovieListActivity";  // tag for logging from this activity

    // Instance fields:
    AsyncHttpClient client;   // AsyncHttpClient() to call API
    ArrayList<Movie> movies;  // list of currently playing movies
    RecyclerView rvMovies;    // the RecylerView
    MovieAdapter adapter;     // the adapter wired to the RecyclerView
    Config config;            // image config

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        client = new AsyncHttpClient();  // initialize the client
        movies = new ArrayList<>();  // initialize list of movies
        // initialize the adapter  -- movie array list can't be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // Resolve the recycler view and connect a layout manager and the adapter
        rvMovies = ButterKnife.findById(this, R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // Get the configuration on app creation
        getConfiguration();
    }

    // Get the list of currently playing movies from API
    private void getNowPlaying(){
        String url = API_BASE_URL + "/movie/now_playing";  // create URL

        // Set request params to be passed
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));  // API key, always required

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into movie list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // Iterate through result set and create movie objects
                    for(int i = 0 ; i < results.length(); i ++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // Notify adapter that row was changed
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    // Get the configuration from the API
    private void getConfiguration(){
        String url = API_BASE_URL + "/configuration";  // create URL

        // Set request params to be passed
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));  // API key, always required

        // Execute a GET request, expecting a JSON object resp
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try {
                    config = new Config(response);
                    // Pass the config to adapter
                    adapter.setConfig(config);
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and poster size %s", config.getImageBaseURL(), config.getPosterSize()));
                } catch (JSONException e){
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });

        // Get the now playing movie list from API
        getNowPlaying();
    }

    // Helper method to handle errors, log them, and alert user
    private void logError(String message, Throwable error, boolean alertUser){
        Log.e(TAG, message, error);  // Always log the error
        // alert the user to avoid silent errors
        if(alertUser){
            // Show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
