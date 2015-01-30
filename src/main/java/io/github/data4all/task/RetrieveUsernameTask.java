/*******************************************************************************
 * Copyright (c) 2014 Data4All
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
package io.github.data4all.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask to retrieve the username over http-connection Stores username in
 * SharedPreferences
 * 
 * @author sb
 *
 */
public class RetrieveUsernameTask extends AsyncTask<Void, Void, Void> {

    private String TAG = getClass().getSimpleName();

    private XmlPullParserFactory xmlParserFactory;

    private String url;
    private OAuthConsumer consumer;
    private SharedPreferences prefs;

    public RetrieveUsernameTask(String url, OAuthConsumer consumer,
            SharedPreferences prefs) {
        this.url = url;
        this.consumer = consumer;
        this.prefs = prefs;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e("Data4All", "Problem creating parser factory", e);
        }
        getUserXml(url, consumer);
        return null;
    }

    private void getUserXml(String url, OAuthConsumer consumer) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        try {
            // make a GET request
            HttpGet request = new HttpGet(url);
            Log.i(TAG, "Requesting URL : " + url);

            // sign the request with oAuth
            consumer.sign(request);

            // execute request and get the response
            HttpResponse response = httpclient.execute(request);
            Log.i(TAG, "Statusline : " + response.getStatusLine());

            // write content of response in InputStream
            InputStream data = response.getEntity().getContent();

            parseXmlUserInfo(data);
        } catch (OAuthMessageSignerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Parse a given InputStream to retrieve the username
     * 
     * @param inputStream
     *            an xml as InputStream
     */
    public void parseXmlUserInfo(InputStream inputStream) {
        XmlPullParser parser;
        try {
            parser = xmlParserFactory.newPullParser();
            parser.setInput(inputStream, null);
            int eventType;

            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (eventType == XmlPullParser.START_TAG
                        && "user".equals(tagName)) {
                    prefs.edit()
                            .putString(
                                    "USERNAME",
                                    parser.getAttributeValue(null,
                                            "display_name")).apply();
                    Log.d(TAG,
                            "getUserDetails display name "
                                    + prefs.getString("USERNAME", "NULL"));
                }
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
