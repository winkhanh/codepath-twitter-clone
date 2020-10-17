package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public long id;

    public Tweet(){

    }

    public static Tweet fromJson(JSONObject jsonObj) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObj.getString("text");
        tweet.createdAt = jsonObj.getString("created_at");
        tweet.user = User.fromJson(jsonObj.getJSONObject("user"));
        tweet.id = jsonObj.getLong("id");

        return tweet;
    }

    public String getCreatedAt() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArr) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i=0;i<jsonArr.length();i++){
            tweets.add(fromJson(jsonArr.getJSONObject(i)));
        }
        return tweets;
    }
}
