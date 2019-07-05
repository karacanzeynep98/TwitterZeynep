package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.media.Image;

import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;


import android.text.Layout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;

    public static int REPLY_REQUEST_CODE = 200;

    //pass in the tweets array in the constructor
    public TweetAdapter(List<Tweet> Tweets) {
        mTweets = Tweets;
    }

    //for each row, inflate the layout and cache references in Viewholder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //get the data according to the position
        Tweet tweet = mTweets.get(position);

        //populate the views according to this data
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvUserScreenName.setText("@" + tweet.user.screenName);


        String relativeTime = getRelativeTimeAgo(tweet.createdAt);
        viewHolder.timeStamp.setText(relativeTime);

        Glide.with(context).load(tweet.user.profileImageUrl).into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvUserScreenName;
        public TextView tvBody;
        public TextView timeStamp;
        public ImageView ivReplyImage;

        //constructor of the viewHolder will take in an inflated layout
        public ViewHolder(View itemView) {
            super(itemView);

            //perform findViewbyId lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            timeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            tvUserScreenName = (TextView) itemView.findViewById(R.id.tvUserId);
            ivReplyImage = (ImageView) itemView.findViewById(R.id.ivReplyButton);

            ivReplyImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);

                Intent replyTweet = new Intent(context, ReplyActivity.class);
                replyTweet.putExtra("username", "@" + tweet.user.screenName);
                ((Activity) context).startActivityForResult(replyTweet, REPLY_REQUEST_CODE);
            }
        }

    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

}
