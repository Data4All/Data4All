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

import io.github.data4all.network.OscUploadHelper;
import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to request a changeSetID from OpenStreetMap.
 * 
 * @author Richard
 *
 */

public class RequestChangesetIDFromOsmTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = RequestChangesetIDFromOsmTask.class
            .getSimpleName();
    private Context context;

    private HttpPut request;

    private OAuthConsumer consumer;
    private File changeSetXML;
    private OscUploadHelper helper;

    /**
     * HTTP result code or -1 in case of internal error.
     */
    private int statusCode = -1;

    /**
     * Constructor of the RequestChangesetIDFromOpenStreetMapTask.
     * 
     * Sets the Parameter values to the local attributes.
     * 
     * @param context
     *            the Context of the Application
     * @param consumer
     *            the OAuthConusmer used for authentication
     * @param comment
     *            the Comment for this ChangeSet, should include what is
     *            uploaded
     * @param helper
     *            the OscUploadHelper which started the task
     */
    public RequestChangesetIDFromOsmTask(Context context,
            OAuthConsumer consumer, String comment, OscUploadHelper helper) {

        this.helper = helper;
        this.context = context;
        this.consumer = consumer;
        changeSetXML =
                new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/getChangesetID.xml");

        try {
            final PrintWriter writer =
                    new PrintWriter(new BufferedWriter(new FileWriter(
                            changeSetXML)));

            writer.println("<osm>");
            writer.println("<changeset>");
            writer.println("<tag k=\"created_by\" v=\"Data4All\"/>");
            writer.println("<tag k=\"comment\" v=\"" + comment + "\"/>");
            writer.println("</changeset>");
            writer.println("</osm>");

            writer.flush();
            writer.close();

        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }

    }

    /**
     * Forms the HttpRequest.
     */
    @Override
    protected void onPreExecute() {

        try {
            // Prepare request
            request =
                    new HttpPut(OAuthParameters.CURRENT.getScopeUrl()
                            + "api/0.6/changeset/create");

            // Sign the request with oAuth
            consumer.sign(request);

            // Adding the Entity to the Request
            final HttpEntity entity =
                    new FileEntity(changeSetXML, "text/plain");
            request.setEntity(entity);

        } catch (OAuthMessageSignerException e) {
            Log.e(TAG, "OAuthMessageSignerException", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(TAG, "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e) {
            Log.e(TAG, "OAuthCommunicationException", e);
        }
    }

    /**
     * Displays what happened.
     */
    @Override
    protected void onPostExecute(Void result) {
        switch (statusCode) {
        case -1:
            // Internal error, the request didn't start at all
            Log.d(TAG, "Internal error, the request did not start at all");
            break;
        case HttpStatus.SC_OK:
            // Success ! Update database and close activity
            Log.d(TAG, "Success");
            break;
        case HttpStatus.SC_UNAUTHORIZED:
            // Does not have authorization
            Log.d(TAG, "Does not have authorization");
            break;
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            Toast.makeText(context, "INTERNAL SERVER ERROR", Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "INTERNAL SERVER ERROR");
            break;
        default:
            // unknown error
            Log.d(TAG, "Unknown error");
        }
    }

    /**
     * Sending the Request and analyzes the response and starts the Uploading.
     * Task through the helper if the response was positive.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            final DefaultHttpClient httpClient = new DefaultHttpClient();

            // Sending the Request
            final HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            final HttpEntity responseEntity = response.getEntity();
            Integer changesetId = 0;

            // Looking if the Request was successful and then starting the
            // Upload Task through the helper
            if (statusCode == HttpStatus.SC_OK) {
                final InputStream is = responseEntity.getContent();
                final BufferedReader in =
                        new BufferedReader(new InputStreamReader(is));
                final String line = in.readLine();
                changesetId = Integer.parseInt(line);
                helper.parseAndUpload(changesetId);
            }
            Log.d(TAG, "OSM ChangeSetID response Entity: " + responseEntity);
            Log.d(TAG, "OSM ChangeSetID response: " + changesetId);
            Log.d(TAG, "OSM ChangeSetID statusCode: " + statusCode);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "doInBackground failed", e);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground failed", e);
        }

        return null;
    }

}
