package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    RecyclerView rvTweet;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        rvTweet=findViewById(R.id.rvTweet);
        swipeContainer=findViewById(R.id.swipeContainer);
        tweets = new ArrayList<>();

        adapter = new TweetsAdapter(this,tweets);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        rvTweet.setLayoutManager(layoutManager);

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light
        );

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });

        rvTweet.setAdapter((adapter));
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreData();

            }
        };
        rvTweet.addOnScrollListener(scrollListener);
        populateHomeTimeline();
    }

    private void loadMoreData() {
        client.getNextPage(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArr = json.jsonArray;
                try {
                    adapter.addAll(Tweet.fromJsonArray(jsonArr));
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e("TimeLineActivity","On Failure",e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        },tweets.get(tweets.size()-1).id);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArr = json.jsonArray;
                try {
                    Log.d("TimeLineActivity","On Sucess");
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArr));
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e("TimeLineActivity","On Failure",e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }
}