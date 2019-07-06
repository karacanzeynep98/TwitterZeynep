package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    public static final int COMPOSE_REQUEST_CODE = 100;
    public static final int REPLY_REQUEST_CODE = 800;

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    Tweet resultTweet;
    private SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;
    long maxId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        client = TwitterApp.getRestClient();

        //find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvItems);
        //init the arraylist (data source)
        tweets = new ArrayList<>();
        //construct the adapter from the data source
        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //RecyclerView setup
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        populateTimeline(maxId);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getSupportActionBar().setTitle("Twitter");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff1da1f2")));
        getSupportActionBar().setLogo(getDrawable(R.drawable.ic_launcher_twitter_round));
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    private void populateTimeline(long maxId){

        showProgressBar();
        //make the network request to get back data from twitter api
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                for (int i = 0; i < response.length(); i++){
                    //convert each object to a tweet model
                    //add that tweet model to our data source
                    //notify the adapter that we added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1); //pass the position as argument
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


    //filter by filter.getFilterId for clicking on MENU items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.composeButton:
                composeMessage();
                return true;
            //case R.id.miProfile:
                //showProfileView();
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   private void composeMessage() {
        //open ComposeActivity to create a new tweet
        Intent composeTweet = new Intent(this, ComposeActivity.class);
        startActivityForResult(composeTweet, COMPOSE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == COMPOSE_REQUEST_CODE && resultCode == RESULT_OK) {

            resultTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(ComposeActivity.RESULT_TWEET_KEY));

            tweets.add(0, resultTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);

        }
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REPLY_REQUEST_CODE && resultCode == RESULT_OK) {

            resultTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(ComposeActivity.RESULT_TWEET_KEY));

            tweets.add(0, resultTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    public void fetchTimelineAsync(int page) {
        tweetAdapter.clear();
        populateTimeline(maxId);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {

        maxId = tweets.get(tweets.size() - 1).uid;
        populateTimeline(maxId);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }



}
