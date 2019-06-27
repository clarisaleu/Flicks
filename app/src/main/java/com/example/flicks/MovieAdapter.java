package com.example.flicks;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    // instance fields
    ArrayList<Movie> movies;  // list of movies
    Config config;  // config needed for movie image urls
    Context context;  // context for rendering

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;  // initialize movie list

    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    // creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();  // get the context and create the inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        // create view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        // return a new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds an inflated view to a new view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the movie data at the specified position
        Movie movie = movies.get(position);
        // populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        // build url for poster image
        String imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        int radius = 23; // corner radius, higher value = more rounded
        int margin = 0; // crop margin, set to 0 for corners with no crop
        // load image using Glide
        Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, radius, margin)).placeholder(R.drawable.flicks_movie_placeholder).error(R.drawable.flicks_movie_placeholder).into(holder.ivPosterImage);
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create viewholder as static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // track view objs
        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // look up view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
