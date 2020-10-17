package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final int TWEET_MAX_LENGTH=280;
    EditText etCompose;
    Button btnTweet;
    String composingTweet;
    TwitterClient client;
    Button btnClear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client=TwitterApp.getRestClient(this);
        etCompose=findViewById(R.id.etCompose);
        btnTweet=findViewById(R.id.btnTweet);
        composingTweet=getIntent().getExtras().getString("composing_tweet");
        etCompose.setText(composingTweet);
        btnClear=findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                etCompose.setText("");
            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this,"Tweet can't be left empty",Toast.LENGTH_LONG).show();
                    return;
                }else if (tweetContent.length()>TWEET_MAX_LENGTH){
                    Toast.makeText(ComposeActivity.this,"Tweet cannot be too long",Toast.LENGTH_LONG).show();
                    return;
                }
                client.tweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d("wack","onSuccess");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("wack","onFailure");
                    }
                },tweetContent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose_cancel){
            Intent i = new Intent();
            i.putExtra("composing_tweet",etCompose.getText().toString());
            setResult(RESULT_CANCELED,i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}