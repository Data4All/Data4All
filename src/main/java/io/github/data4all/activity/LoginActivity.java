/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.activity;

import io.github.data4all.Constants;
import io.github.data4all.R;
import io.github.data4all.task.OAuthRequestTokenTask;

import java.net.URLEncoder;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;

/**
 * Activity to start authentication process
 * 
 * @author sbollen, tbrose
 *
 */
public class LoginActivity extends Activity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private OAuthConsumer consumer;
    private OAuthProvider provider;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_login);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        try {
            this.consumer =
                    new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY,
                            Constants.CONSUMER_SECRET);
            this.provider =
                    new CommonsHttpOAuthProvider(Constants.REQUEST_URL
                            + "?scope="
                            + URLEncoder.encode(Constants.SCOPE,
                                    Constants.ENCODING), Constants.ACCESS_URL,
                            Constants.AUTHORIZE_URL);
            new OAuthRequestTokenTask(this, consumer, provider).execute(webView);
        } catch (Exception e) {
            Log.e(TAG, "Error creating consumer / provider", e);
        }
    }
}
