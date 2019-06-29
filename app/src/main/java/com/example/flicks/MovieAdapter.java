package com.example.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;


import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    // For rounded corners on movie images
    private static int radius = 23;  // corner radius, higher value = more rounded
    private static int margin = 0;  // crop margin, set to 0 for corners with no crop

    // Instance fields:
    ArrayList<Movie> movies;  // list of movies
    Config config;            // config needed for movie image urls
    Context context;          // context for rendering

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;  // initialize movie list
    }

    // Set configuration
    public void setConfig(Config config) {
        this.config = config;
    }

    // Return configuration
    public Config getConfig() {
        return config;
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        // Create view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);  // return a new ViewHolder
    }

    // Binds an inflated view to a new view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);        // get the movie data at the specified position
        holder.tvTitle.setText(movie.getTitle());  // populate the view with the movie data
        holder.tvOverview.setText(movie.getOverview());

        // Determine the current orientation
        boolean isPortrait =  context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


        String imageUrl = null;  // Build url for poster image

        // If in portrait mode, laod the poster image else orientation in landscape mode
        if(isPortrait){
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // Get the correct placeholder and imageview for current orientation
        int placeHolderID = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        // Load image using Glide
        Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, radius, margin)).placeholder(placeHolderID).error(placeHolderID).into(imageView);
    }

    // Create view holder as static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Track view objects
        ImageView ivPosterImage;    // Portrait mode
        ImageView ivBackdropImage;  // Landscape mode
        TextView tvTitle;           // Movie title
        TextView tvOverview;        // Movie description

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Look up view objects by id
            ivPosterImage = ButterKnife.findById(itemView, R.id.ivPosterImage);
            ivBackdropImage = ButterKnife.findById(itemView, R.id.ivBackdropImage);
            tvOverview = ButterKnife.findById(itemView, R.id.tvOverview);
            tvTitle = ButterKnife.findById(itemView, R.id.tvTitle);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show MovieDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Movie movie = movies.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // Pass backdrop image
                String imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
                intent.putExtra("video_id", imageUrl);
                intent.putExtra("name_movie", movie.getTitle());

                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
