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

import io.github.data4all.logger.Log;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.CookieManager;

/**
 * 
 * @author Based on this tutorial:
 *         http://blog.doityourselfandroid.com/2010/11/10
 *         /oauth-flow-in-android-app/
 *
 */

public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

    final String TAG = getClass().getName();

    private OAuthProvider provider;
    private OAuthConsumer consumer;
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

        final String oauth_verifier =
                uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

        try {
            provider.retrieveAccessToken(consumer, oauth_verifier);

            // Store tokens in SharedPreferences
            final Editor edit = prefs.edit();
            edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
            edit.putString(OAuth.OAUTH_TOKEN_SECRET,
                    consumer.getTokenSecret());
            edit.commit();

            final String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
            final String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

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
        //startActivity(new Intent(LoginActivity.this,
         //       MapViewActivity.class));
        // Remove sessioncookie before moving on
        CookieManager.getInstance().removeSessionCookie();
        //finish();
    }
}
