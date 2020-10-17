package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    RecyclerView rvTweet;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    FloatingActionButton fabCompose;
    SwipeRefreshLayout swipeContainer;
    String composingTweet;
    public static final int REQUEST_CODE = 18;

    EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        composingTweet="";
        client = TwitterApp.getRestClient(this);
        rvTweet=findViewById(R.id.rvTweet);
        fabCompose=findViewById(R.id.fabCompose);
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
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCompose();
            }
        });
        rvTweet.addOnScrollListener(scrollListener);
        populateHomeTimeline();
    }
    public void startCompose(){
        Log.d("Compose","Compose");
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("composing_tweet",composingTweet);
        startActivityForResult(i,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode== REQUEST_CODE && resultCode == RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0,tweet);
            composingTweet="";
            adapter.notifyItemInserted(0);
            rvTweet.smoothScrollToPosition(0);
        }else if (requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED){
            composingTweet=data.getStringExtra("composing_tweet");
        }
        super.onActivityResult(requestCode, resultCode, data);
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