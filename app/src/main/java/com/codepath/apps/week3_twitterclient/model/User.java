package com.codepath.apps.week3_twitterclient.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leila on 2017/3/1.
 */

public class User {
    // list attribute
    private String name;
    private long uid;
    private String screenName;
    private String profileImgUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    // deserialize the user json => User
    public static User fromJSON(JSONObject json) {
        User u = new User();
        // Extract and fill the value
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImgUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return an user
        return u;
    }
}
