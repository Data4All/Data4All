/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.task;

import io.github.data4all.Constants;
import io.github.data4all.logger.Log;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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
public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {

    final String TAG = getClass().getName();
    private Context context;
    private OAuthProvider provider;
    private OAuthConsumer consumer;

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
     * Retrieve the OAuth Request Token and present a browser to the user to
     * authorize the token.
     * 
     */
    @Override
    protected Void doInBackground(Void... params) {

        try {
            Log.i(TAG, "Retrieving request token from OSM servers");
            final String url = provider.retrieveRequestToken(consumer,
                    Constants.OAUTH_CALLBACK_URL);
            Log.i(TAG, "Popping a browser with the authorize URL : " + url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_NO_HISTORY
                            | Intent.FLAG_FROM_BACKGROUND);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error during OAUth retrieve request token", e);
        }

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Log.d(TAG, "SharedPreferences: " + prefs.getAll().toString());
        return null;
    }

}
