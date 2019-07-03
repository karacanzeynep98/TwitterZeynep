package com.codepath.apps.restclienttemplate.models;

import android.net.ParseException;
import android.os.Parcelable;
import android.text.format.DateUtils;

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

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {

        //constructor
        Tweet tweet = new Tweet();
        //extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

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


}
