package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.media.Image;

import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;


import android.text.Layout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

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

        if (tweet.hasEntities == true) {
            String entityUrl = tweet.entity.loadURL;
            viewHolder.ivTweetImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(entityUrl).into(viewHolder.ivTweetImage);
        } else {
            viewHolder.ivTweetImage.setVisibility(View.GONE);
        }
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
        public ImageView ivRetweetButton;
        public ImageView ivLikeButton;
        public ImageView ivTweetImage;

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
            ivRetweetButton = (ImageView) itemView.findViewById(R.id.ivRetweetButton);
            ivLikeButton = (ImageView) itemView.findViewById(R.id.ivLikeButton);
            ivTweetImage = (ImageView) itemView.findViewById(R.id.ivTweetImage);

            ivReplyImage.setOnClickListener(this);
            ivRetweetButton.setOnClickListener(this);
            ivLikeButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            switch(v.getId()) {


                case R.id.ivReplyButton:
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

                    return;


                case R.id.ivRetweetButton:

                    position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        ; //DO STUFF


                    }

                    return;

                case R.id.ivLikeButton:

                    int position2 = getAdapterPosition();

                    Tweet tweet = mTweets.get(position2);

                    // make sure the position is valid, i.e. actually exists in the view
                    if (position2 != RecyclerView.NO_POSITION) {

                        if(mTweets.get(position2).likedByUser) {

                            TwitterApp.getRestClient().favoriteTweet(tweet.uid, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    DrawableCompat.setTint(ivLikeButton.getDrawable(), ContextCompat.getColor(context, R.color.medium_red));

                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                    Log.d("TwitterClient", response.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("TwitterClient", errorResponse.toString());
                                    throwable.printStackTrace();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    Log.d("TwitterClient", errorResponse.toString());
                                    throwable.printStackTrace();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d("TwitterClient", responseString);
                                    throwable.printStackTrace();
                                }
                            });

                        } else {

                            TwitterApp.getRestClient().unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    DrawableCompat.setTint(ivLikeButton.getDrawable(), ContextCompat.getColor(context, R.color.twitter_blue));

                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                    Log.d("TwitterClient", response.toString());

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("TwitterClient", errorResponse.toString());
                                    throwable.printStackTrace();

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    Log.d("TwitterClient", errorResponse.toString());
                                    throwable.printStackTrace();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d("TwitterClient", responseString);
                                    throwable.printStackTrace();

                                }
                            });
                        }
                    }

                    return;
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
