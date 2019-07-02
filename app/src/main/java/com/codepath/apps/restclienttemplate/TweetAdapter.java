package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.media.Image;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;


import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;

    //pass in the tweets array in the constructor
    public TweetAdapter(List<Tweet> Tweets) {
        mTweets = Tweets;
    }

    //for each row, inflate the layout and cache references in Viewholder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //creates one tweet by inflating views
        View tweetView = inflater.inflate(R.layout.itemtweet, parent, false);
        //creates a viewHolder for one tweet. onCreateViewHolder() will be called each time you create a tweet
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    //bind the values based on the position of the element
    //fill out the tweet body, username and image using the onBindViewHolder method
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //get the data according to the position
        Tweet tweet = mTweets.get(position);

        //populate the views according to this data
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);

        Glide.with(context).load(tweet.user.profileImageUrl).into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;

        //constructor of the viewHolder will take in an inflated layout
        public ViewHolder(View itemView) {
            super(itemView);

            //perform findViewbyId lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        }
    }
}
