package com.codepath.apps.week3_twitterclient.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.week3_twitterclient.EndlessScrollListener;
import com.codepath.apps.week3_twitterclient.R;
import com.codepath.apps.week3_twitterclient.TwitterApplication;
import com.codepath.apps.week3_twitterclient.TwitterClient;
import com.codepath.apps.week3_twitterclient.adapter.TweetsArrayAdapter;
import com.codepath.apps.week3_twitterclient.model.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 200;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private Long retMaxId, retSinceId;

    public SharedPreferences spref;
    public SharedPreferences.Editor editor;

    public static final String KEY = "lin.leila.week3_twitterclient";

//    private int leftReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvTweets = (ListView) findViewById(R.id.lvTweets);

        // Create the ArrayList (data source)
        tweets = new ArrayList<>();
        // Construct  the adapter from the data source
        aTweets = new TweetsArrayAdapter(this, tweets);
        // Connect Adapter to list view
        lvTweets.setAdapter(aTweets);

        // Get the client
        client = TwitterApplication.getRestClient();


        spref = getSharedPreferences(KEY, Context.MODE_PRIVATE);
        editor = spref.edit();
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    editor.putString("auth_screen_name", response.getString("screen_name"));
                    editor.putString("auth_user_id", response.getString("id"));
                    editor.putString("auth_name", response.getString("name"));
                    editor.putString("auth_profile_image_url", response.getString("profile_image_url"));
                    editor.apply();
                    setTitle("@"+spref.getString("auth_screen_name", "Timeline"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

        populateTimeline(0);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or loadNextDataFromApi(totalItemsCount)
                if (retSinceId.equals(retMaxId)) {
                    return false;
                } else if (retMaxId < retSinceId) {
                    populateTimeline(page);
                    retSinceId = retMaxId;
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                } else {
                    return false;
                }
            }
        });
    }

    // Send an API request to the timeline JSON
    // Fill the ListView by creating the tweet objects from the JSON
    private void populateTimeline(int page) {
        if (page == 0) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    // Deserialize JSON
                    // Create Model and Add to the Adapter
                    // Load Model data into ListView
                    aTweets.addAll(Tweet.fromJSONArray(json));
                    retMaxId = tweets.get(tweets.size()-1).getUid();
                    retSinceId = tweets.get(0).getUid();
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    try {
                        Snackbar.make(getCurrentFocus(),
                                errorResponse.getJSONArray("errors")
                                        .getJSONObject(0)
                                        .get("message").toString(),
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            client.getMoreHomeTimeline(retMaxId, new JsonHttpResponseHandler() {
                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    // Deserialize JSON
                    // Create Model and Add to the Adapter
                    // Load Model data into ListView
                    if (Tweet.fromJSONArray(json).size() > 1) {
                        aTweets.addAll(Tweet.fromJSONArray(json));
                        retMaxId = tweets.get(tweets.size()-1).getUid();
                    }
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    try {
                        Snackbar.make(getCurrentFocus(),
                                errorResponse.getJSONArray("errors")
                                        .getJSONObject(0)
                                        .get("message").toString(),
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

//    public void getLimitLeft() {
//        client.getAPIRateLimit(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
//                try {
//                    String tmp = jsonObject
//                            .getJSONObject("resources")
//                            .getJSONObject("statuses")
//                            .getJSONObject("/statuses/home_timeline")
//                            .getString("remaining");
//                    leftReq = Integer.valueOf(tmp);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            // FAILURE
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("DEBUG", errorResponse.toString());
//            }
//        });
//    }

    // ActionBar


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            client.getSingleTweet(data.getExtras().getString("tweet_id"), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    aTweets.insert(Tweet.fromJSON(json), 0);
                    aTweets.notifyDataSetChanged();
                    Snackbar.make(getCurrentFocus(), "New Tweet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Snackbar.make(getCurrentFocus(), "Cannot get new tweet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        } else {
            Log.d("DEBUG", "resultCode/requestCode: " + resultCode + "/" + requestCode);
        }

        editor.putString("etBody", "").apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            i.putExtra("tweet_id", "");
            startActivityForResult(i, REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

}
