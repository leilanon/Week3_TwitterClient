package com.codepath.apps.week3_twitterclient.model;

import com.codepath.apps.week3_twitterclient.ParseRelativeDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Leila on 2017/3/1.
 */


// Parse the JSON + Store the data, encapsulate state logic or display logic

public class Tweet {
    // List out the attribute
    private String body;
    private long uid; // unique id for the tweet itself
    private User user; // store embedded user object
    private String createdAt;
    private String parseRelativeDate;

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        parseRelativeDate = new ParseRelativeDate().getRelativeTimeAgo(createdAt);
        return parseRelativeDate;
    }



    public User getUser() {
        return user;
    }
// Deserialize the JSON and build the Tweet object
    // Tweet.fromJSON("{ ... }") => <Tweet>

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        // Extract the value from the JSON, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return the tweet object
        return tweet;
    }

    // Tweet.fromJSONArray([{...}, {...}]) => List<Tweet>
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        // Iterate the json array and create the tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        // Return the finished list
        return tweets;
    }

}
