package com.codepath.flicks.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.flicks.R;
import com.codepath.flicks.models.Movie;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import static android.R.attr.type;
import static com.codepath.flicks.R.id.tvOverview;
import static com.codepath.flicks.R.id.tvTitle;
import static com.codepath.flicks.models.Movie.MovieValues.UNPOPULAR;

/**
 * Created by culgnol on 9/3/17.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    // View Lookup cache
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivImage;
    }

    private static class ViewHolderPopular {
        ImageView ivImage;
    }

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    @Override
    public int getViewTypeCount() {
        return Movie.MovieValues.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getMovieValue().ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Movie movie = getItem(position);
        // Get the data item type for this position
        int type = getItemViewType(position);

        if (type == UNPOPULAR.ordinal()) {
            ViewHolder viewHolder; //view Lookup cache stored in tag

            if (convertView == null) {
                // Inflate xml layout based on the type
                convertView = getInflatedLayoutForType(type);

                viewHolder = new ViewHolder();

                viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivMovieImage);
                viewHolder.tvTitle = (TextView) convertView.findViewById(tvTitle);
                viewHolder.tvOverview = (TextView) convertView.findViewById(tvOverview);
                // cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // clear out the image from last time
            viewHolder.ivImage.setImageResource(0);

            int orientation = getContext().getResources().getConfiguration().orientation;

            // populate data
            viewHolder.tvTitle.setText(movie.getOriginalTitle());
            viewHolder.tvOverview.setText(movie.getOverview());

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Picasso.with(getContext()).load(movie.getPosterPath()).placeholder(R.drawable.ic_launcher).into(viewHolder.ivImage);
            } else {
                Picasso.with(getContext()).load(movie.getBackdropPath()).placeholder(R.drawable.ic_launcher).into(viewHolder.ivImage);
            }

            //return the view
            return convertView;

        } else if (type == Movie.MovieValues.POPULAR.ordinal()) {
            ViewHolderPopular  viewHolderPopular;

            if (convertView == null) {
                // Inflate xml layout based on the type
                convertView = getInflatedLayoutForType(type);

                viewHolderPopular = new ViewHolderPopular();

                viewHolderPopular.ivImage = (ImageView) convertView.findViewById(R.id.ivMovieImage);
                // cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolderPopular);
            } else {
                viewHolderPopular = (ViewHolderPopular) convertView.getTag();
            }

            // clear out the image from last time
            viewHolderPopular.ivImage.setImageResource(0);

            // populate data
            Picasso.with(getContext()).load(movie.getBackdropPath()).placeholder(R.drawable.ic_launcher).into(viewHolderPopular.ivImage);

            //return the view
            return convertView;
        }


        //Throw exception, unknown data type
        throw new IllegalArgumentException("Movie type not found.");
    }

    // Given the item type, responsible for returning the correct inflated XML layout file
    private View getInflatedLayoutForType(int type) {
        if (type == Movie.MovieValues.UNPOPULAR.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_movie, null);
        } else if (type == Movie.MovieValues.POPULAR.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_movie_popular, null);
        } else {
            return null;
        }
    }
}
