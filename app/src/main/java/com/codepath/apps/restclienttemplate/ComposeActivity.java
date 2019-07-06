package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static String RESULT_TWEET_KEY = "result_tweet";

    TwitterClient client;
    EditText etTweetInput;
    TextView tvScreenName;
    TextView tvCount;
    Button myButton;
    String my_username = "@Karacan_149";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImageCompose);
        ivProfileImage.setImageResource(R.drawable.image1);

        etTweetInput = (EditText) findViewById(R.id.etNewTweet);
        myButton = (Button) findViewById(R.id.myTweetButton);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvScreenName = (TextView) findViewById(R.id.tvScreenNameCompose);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendTweet();
            }
        });

        client = TwitterApp.getRestClient();
        tvScreenName.setText(my_username);

        etTweetInput.addTextChangedListener(mTextEditorWatcher);

    }

    private void sendTweet() {

        client.sendTweet(etTweetInput.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        //parsing response
                        JSONObject responseJson = new JSONObject(new String(responseBody));
                        Tweet resultTweet = Tweet.fromJSON(responseJson);

                        //return result to calling activity
                        Intent resultData = new Intent();
                        resultData.putExtra(RESULT_TWEET_KEY, Parcels.wrap(resultTweet));

                        setResult(RESULT_OK, resultData);
                        finish();

                    } catch (JSONException e){
                        Log.e("ComposeActivity", "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ;
            }
        });
    }

    private final TextWatcher mTextEditorWatcher=new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            tvCount.setText(String.valueOf(280-s.length()));
        }
    };



}
