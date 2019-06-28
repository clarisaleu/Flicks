package com.example.flicks;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;  // movie to display
    // Constants:
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";  // the parameter name for the API key
    public final static String TAG = "MovieListActivity";  // tag for logging from this activity

    AsyncHttpClient client;   // AsyncHttpClient() to call API

    // The view objects
    TextView tvTitle;  // Title
    TextView tvOverview;  // Description
    ImageView movieImage;
    RatingBar rbVoteAverage;  // Average vote
    Integer movieId;  // movie id for trailer
    String key;
    Context context;  // context for rendering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbRating);
        movieImage = (ImageView) findViewById(R.id.movieImg);
        client = new AsyncHttpClient();  // initialize the client

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        String imageUrl = getIntent().getStringExtra("video_id");
        Glide.with(this).load(imageUrl).into(movieImage);

        // get movie id
        movieId = movie.getId();
        //set a boolean to check if func run i.e. returns trailer.
        getMovieTrailer();

        movieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent for the new activity
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                intent.putExtra("video_key", key);
                startActivity(intent);
            }
        });

    }

    private void getMovieTrailer(){
        String url = API_BASE_URL + "/movie/"+movieId+"/video";  // create URL
        // Set request params to be passed
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));  // API key, always required
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              //  boolean ret = false;
                // load the result of movie
                try {
                    // parse results and get the key
                    JSONArray results = response.getJSONArray("results");
                    JSONObject obj = results.getJSONObject(0);
                    key = obj.getString("key");
                } catch (JSONException e) {
                    logError("Failed to parse movie trailer", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from movie video endpoint", throwable, true);
            }
        });

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

