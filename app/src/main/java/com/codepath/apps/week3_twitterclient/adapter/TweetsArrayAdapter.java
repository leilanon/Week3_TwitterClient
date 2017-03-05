package com.codepath.apps.week3_twitterclient.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.week3_twitterclient.R;
import com.codepath.apps.week3_twitterclient.model.Tweet;

import java.util.List;

/**
 * Created by Leila on 2017/3/2.
 */

// Taking the Tweet objects and turning into Views displayed in List
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    // Override and setup custom template
    // ViewHolder pattern
    // View lookup cache

    private static class ViewHolder {
        ImageView ivProfileImg;
        TextView tvUsername;
        TextView tvBody;
        TextView tvCreatedAt;
        TextView tvName;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the tweet
        Tweet tweet = getItem(position);
        // 2. Find or inflate the template
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);

            // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 3. Find the subview to fill with data in template

        viewHolder.tvUsername.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvCreatedAt.setText(tweet.getCreatedAt());
        viewHolder.tvName.setText(tweet.getUser().getName());
        Glide.with(getContext()).load(tweet.getUser().getProfileImgUrl()).into(viewHolder.ivProfileImg);

        // ImageView ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
        // TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        // TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        // 4. Populate data into the subview
        // tvUsername.setText(tweet.getUser().getScreenName());
        // tvBody.setText(tweet.getBody());
        // ivProfileImg.setImageResource(android.R.color.transparent); // clear out the the old image for a recycled view
        // Picasso.with(getContext()).load(tweet.getUser().getProfileImgUrl()).into(ivProfileImg);

        // Return the view to the List
        return convertView;
    }
}
