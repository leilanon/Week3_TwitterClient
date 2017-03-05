package com.codepath.apps.week3_twitterclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.week3_twitterclient.R;
import com.codepath.apps.week3_twitterclient.TwitterApplication;
import com.codepath.apps.week3_twitterclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ComposeActivity extends AppCompatActivity {
    TwitterClient client;

    TextView tvUsername;
    ImageView ivProfileImg;
    EditText etBody;
    FloatingActionButton fabTweet;
    TextView tvRemain;

    SharedPreferences spref;
    SharedPreferences.Editor editor;
    public static final String KEY = "lin.leila.week3_twitterclient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Compose Tweet");

        client = TwitterApplication.getRestClient();
        spref = getSharedPreferences(KEY, MODE_PRIVATE);
        editor = spref.edit();
        findView();
        init();

        tvUsername.setText("@" + spref.getString("auth_screen_name", "???"));
        Glide.with(getContext()).load(spref.getString("auth_profile_image_url", "")).into(ivProfileImg);
    }


    public void findView() {
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
        etBody = (EditText) findViewById(R.id.etBody);
        fabTweet = (FloatingActionButton) findViewById(R.id.fabTweet);
        tvRemain = (TextView) findViewById(R.id.tvRemain);
    }

    public void init() {
        etBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (etBody.getText().toString().length() > 140) {
                    Snackbar.make(getCurrentFocus(), "Status is over 140 characters.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        etBody.addTextChangedListener(etWatcher);

        fabTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBody.getText().toString().length() > 140) {
                    Snackbar.make(v, "Status is over 140 characters.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    editor.putString("etBody", etBody.getText().toString());
                    editor.apply();
                    client.composeNewTweet(spref.getString("etBody", ""), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            getTweet(response);
                            ComposeActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", "POST_FAIL: "+errorResponse.toString());
                            setResult(RESULT_CANCELED);
//                          ComposeActivity.this.finish();
                        }
                    });
                }
            }
        });
    }

    public void getTweet(JSONObject response) {
        Intent intent = new Intent();
        String tweetId = "";

        try {
            tweetId = response.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (tweetId.length() > 0) {
            intent.putExtra("tweet_id", tweetId);
            intent.putExtra("resCode", 200);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
            Log.d("DEBUG", "SINGLE_RESULT: FAIL");
        }
    }

    private final TextWatcher etWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            tvRemain.setText(String.valueOf(140 - s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

}
