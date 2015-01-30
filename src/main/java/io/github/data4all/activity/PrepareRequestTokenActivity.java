/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.activity;

import io.github.data4all.Constants;
import io.github.data4all.logger.Log;
import java.net.URLEncoder;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Prepares a OAuthConsumer and OAuthProvider
 * 
 * OAuthConsumer is configured with the consumer key & consumer secret.
 * OAuthProvider is configured with the 3 OAuth endpoints.
 * 
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the
 * request.
 * 
 * @author sb
 * @source Based on this tutorial:
 *         http://blog.doityourselfandroid.com/2010/11/10
 *         /oauth-flow-in-android-app/
 * 
 */
public class PrepareRequestTokenActivity extends BasicActivity {

    final String TAG = getClass().getName();

    private OAuthConsumer consumer;
    private OAuthProvider provider;
    private WebView       webView;
    private boolean       handled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            System.setProperty("debug", "true");
            this.consumer = new CommonsHttpOAuthConsumer(
                    Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
            this.provider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL
                    + "?scope="
                    + URLEncoder.encode(Constants.SCOPE, Constants.ENCODING),
                    Constants.ACCESS_URL, Constants.AUTHORIZE_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error creating consumer / provider", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView = new WebView(this);        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);
        setContentView(webView);

        handled = false;

        Log.i(TAG, "Starting task to request a token");
        new OAuthRequestTokenTask(this, consumer, provider).execute();

    }

    
    /**
     * Do the oAuth dance to get request token
     *
     */
    private class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {

        final String          TAG = getClass().getName();
        private Context       context;
        private OAuthProvider provider;
        private OAuthConsumer consumer;
        private String        url;

        /**
         * 
         * We pass the OAuth consumer and provider.
         * 
         * @param context
         *            Required to be able to start the intent to launch the
         *            browser.
         * @param provider
         *            The OAuthProvider object
         * @param consumer
         *            The OAuthConsumer object
         */
        public OAuthRequestTokenTask(Context context, OAuthConsumer consumer,
                OAuthProvider provider) {
            this.context = context;
            this.consumer = consumer;
            this.provider = provider;
        }

        /**
         * 
         * Retrieve the OAuth Request Token and present a webview to the user to
         * authorize the token.
         * 
         */
        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.i(TAG, "Retrieving request token from OSM servers");
                url = provider.retrieveRequestToken(consumer,
                        Constants.OAUTH_CALLBACK_URL);

                handled = false;
            } catch (Exception e) {
                Log.e(TAG, "Error during OAUth retrieve request token", e);
            }

            Log.d(TAG, "SharedPreferences: "
                    + PreferenceManager.getDefaultSharedPreferences(context)
                            .getAll().toString());
            return null;
        }

        
        @Override
        protected void onPostExecute(Void result) {

            Log.i(TAG, "Retrieving request token from OSM servers");

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url,
                        Bitmap bitmap) {
                    Log.i(TAG, "onPageStarted : " + url + " handled = "
                            + handled);
                }

                @Override
                public void onPageFinished(final WebView view, final String url) {
                    Log.i(TAG, "onPageFinished : " + url + " handled = "
                            + handled);

                    if (url.startsWith(Constants.OAUTH_CALLBACK_URL)) {
                        if (url.indexOf("oauth_token=") != -1) {
                            webView.setVisibility(View.INVISIBLE);

                            if (!handled) {

                                new RetrieveAccessTokenTask(
                                        consumer,
                                        provider,
                                        PreferenceManager
                                                .getDefaultSharedPreferences(getApplicationContext()))
                                        .execute(Uri.parse(url));
                            }
                        } else {
                            webView.setVisibility(View.VISIBLE);
                        }
                    }
                }

            });

            webView.loadUrl(url);

        }

    }

    /**
     * Do the oAuth dance to get access token
     *
     */
    private class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

        final String              TAG = getClass().getName();

        private OAuthProvider     provider;
        private OAuthConsumer     consumer;
        private SharedPreferences prefs;

        public RetrieveAccessTokenTask(OAuthConsumer consumer,
                OAuthProvider provider, SharedPreferences prefs) {
            this.consumer = consumer;
            this.provider = provider;
            this.prefs = prefs;
        }

        /**
         * Retrieve the oauth_verifier, and store the oauth and
         * oauth_token_secret for future API calls in SharedPreferences.
         */
        @Override
        protected Void doInBackground(Uri... params) {
            final Uri uri = params[0];

            final String oauth_verifier = uri
                    .getQueryParameter(OAuth.OAUTH_VERIFIER);

            try {
                provider.retrieveAccessToken(consumer, oauth_verifier);

                //Store tokens in SharedPreferences
                final Editor edit = prefs.edit();
                edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
                edit.putString(OAuth.OAUTH_TOKEN_SECRET,
                        consumer.getTokenSecret());
                edit.commit();

                String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
                String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

                consumer.setTokenWithSecret(token, secret);

                Log.i(TAG, "OAuth - Access Token Retrieved");

            } catch (Exception e) {
                Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
            }

            return null;
        }

        /**
         * When we're done and we've retrieved either a valid token or an error
         * from the server, we'll return to our original activity
         */
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "Starting Loginscreen again");
            startActivity(new Intent(PrepareRequestTokenActivity.this,
                    MapViewActivity.class));
            //Remove sessioncookie before moving on
            CookieManager.getInstance().removeSessionCookie();
            finish();
        }
    }
}