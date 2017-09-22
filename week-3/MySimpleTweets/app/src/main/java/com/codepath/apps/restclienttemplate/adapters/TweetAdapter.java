package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by culgnol on 9/21/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> mTweets;

    // pass in the tweets array into the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
    }

    // need this to have items show on the screen
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder Class

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileIMage;
        public TextView tvUsername;
        public TextView tvBody;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileIMage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
        }
    }
}
