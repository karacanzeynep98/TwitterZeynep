package com.codepath.apps.restclienttemplate.models;

import android.net.ParseException;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {

    //define all the attributes
    public String body;
    public long uid; //database id for the tweet
    //declare user class/user object in each tweet
    public User user;
    public String createdAt;
    public boolean likedByUser;
    public int numFavorites;
    public Entity entity;
    public boolean hasEntities; //do we have entities in the first place

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {

        //constructor
        Tweet tweet = new Tweet();
        //extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.likedByUser = jsonObject.getBoolean("favorited");
        tweet.numFavorites = jsonObject.getInt("favorite_count");

        JSONObject entityObject= jsonObject.getJSONObject("entities");
        if(entityObject.has("media")) {
            JSONArray mediaEndpoint = entityObject.getJSONArray("media");
            if(mediaEndpoint != null && mediaEndpoint.length() != 0) {
                tweet.entity = Entity.fromJSON(jsonObject.getJSONObject("entities"));
                tweet.hasEntities = true;
            } else {
                tweet.hasEntities = false;
            }
        }

        return tweet;

    }

    public Tweet() {}

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean getLiked() {return likedByUser;}

    public int getNumLikes() {return numFavorites;}


}
