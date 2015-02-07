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
package io.github.data4all.task;

import io.github.data4all.Constants;
import io.github.data4all.logger.Log;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * An asynchronous task that communicates with OpenStreetMap to retrieve a
 * request token. (OAuthGetRequestToken)
 * 
 * After receiving the request token from OpenStreetMap, show a browser to the
 * user to authorize the Request Token. (OAuthAuthorizeToken)
 * 
 * @author Based on this tutorial:
 *         http://blog.doityourselfandroid.com/2010/11/10
 *         /oauth-flow-in-android-app/
 * 
 */
public class OAuthRequestTokenTask extends AsyncTask<WebView, Void, WebView> {

    final String TAG = getClass().getName();
    private Context context;
    private OAuthProvider provider;
    private OAuthConsumer consumer;
    private String url;

    /**
     * 
     * We pass the OAuth consumer and provider.
     * 
     * @param context
     *            Required to be able to start the intent to launch the browser.
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
    protected WebView doInBackground(final WebView... params) {
        if (params.length > 0) {
            final WebView webView = params[0];
            try {
                Log.i(TAG, "Retrieving request token from OSM servers");
                url =
                        provider.retrieveRequestToken(consumer,
                                Constants.OAUTH_CALLBACK_URL);
            } catch (Exception e) {
                Log.e(TAG, "Error during OAUth retrieve request token", e);
            }
            Log.d(TAG, "SharedPreferences: "
                    + PreferenceManager.getDefaultSharedPreferences(context)
                            .getAll().toString());
            return webView;
        } else {
            throw new IllegalArgumentException("params.length < 1");
        }
    }

    @Override
    protected void onPostExecute(final WebView webView) {

        Log.i(TAG, "Retrieving request token from OSM servers");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                Log.i(TAG, "onPageStarted : " + url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                Log.i(TAG, "onPageFinished : " + url);

                if (url.startsWith(Constants.OAUTH_CALLBACK_URL)) {
                    if (url.indexOf("oauth_token=") != -1) {
                        webView.setVisibility(View.INVISIBLE);

                        new RetrieveAccessTokenTask(consumer, provider,
                                PreferenceManager
                                        .getDefaultSharedPreferences(context))
                                .execute(Uri.parse(url));
                    } else {
                        webView.setVisibility(View.VISIBLE);
                    }
                }
            }

        });

        webView.loadUrl(url);

    }

}
