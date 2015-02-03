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
package io.github.data4all;

import io.github.data4all.exceptions.OsmException;

import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.util.Log;

/**
 * Helper class for signpost oAuth. More or less based on this tutorial
 * 
 * @source http://nilvec.com/implementing-client-side-oauth-on-android.html
 * @author sb
 *
 */
public class OAuthHelper {
    private static OAuthConsumer mConsumer;
    private static OAuthProvider mProvider;
    private static String mCallbackUrl;

    final String TAG = getClass().getSimpleName();

    public OAuthHelper(String osmBaseUrl) throws OsmException {

        String key = Constants.CONSUMER_KEY;
        String secret = Constants.CONSUMER_SECRET;

        setmConsumer(new CommonsHttpOAuthConsumer(key, secret));
        Log.d("OAuthHelper", "Using " + Constants.REQUEST_URL + " "
                + Constants.ACCESS_URL + " " + Constants.AUTHORIZE_URL);
        Log.d("OAuthHelper", "With key " + key + " secret " + secret);
        mProvider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL,
                Constants.ACCESS_URL, Constants.AUTHORIZE_URL);
        mProvider.setOAuth10a(true);
        setmCallbackUrl(OAuth.OUT_OF_BAND);

        Log.d("OAuthHelper", "No matching API for " + osmBaseUrl + "found");
        throw new OsmException(
                "No matching OAuth configuration found for this API");
    }

    public OAuthHelper(String osmBaseUrl, String consumerKey,
            String consumerSecret, String callbackUrl)
            throws UnsupportedEncodingException {
        setmConsumer(new CommonsHttpOAuthConsumer(consumerKey, consumerSecret));
        mProvider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL,
                Constants.ACCESS_URL, Constants.AUTHORIZE_URL);
        mProvider.setOAuth10a(true);
        setmCallbackUrl((callbackUrl == null ? OAuth.OUT_OF_BAND : callbackUrl));
    }

    /**
     * this constructor is for access to the singletons
     */
    public OAuthHelper() {

    }

    /**
     * Returns an OAuthConsumer initialized with the consumer keys for the API
     * in question
     * 
     * @param osmBaseUrl
     * @return
     */
    public OAuthConsumer getConsumer() {
        String key = Constants.CONSUMER_KEY;
        String secret = Constants.CONSUMER_SECRET;
        return new CommonsHttpOAuthConsumer(key, secret);
    }

    public static OAuthConsumer getmConsumer() {
        return mConsumer;
    }

    public static void setmConsumer(OAuthConsumer mConsumer) {
        OAuthHelper.mConsumer = mConsumer;
    }

    public static String getmCallbackUrl() {
        return mCallbackUrl;
    }

    public static void setmCallbackUrl(String mCallbackUrl) {
        OAuthHelper.mCallbackUrl = mCallbackUrl;
    }
}
