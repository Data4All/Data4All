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

import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import io.github.data4all.logger.Log;
import android.widget.Toast;

/**
 * Task to upload a gpx file to OpenStreetMap INFO: Request is getting status
 * code 500 if using dev api!
 * 
 * @author sb
 *
 */

public class UploadToOpenStreetMapTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = UploadToOpenStreetMapTask.class
            .getSimpleName();
    private Context context;

    private HttpPost request;

    private OAuthConsumer consumer;
    private File gpxFile;
    private String description;
    private String tags;
    private String visibility;

    /**
     * HTTP result code or -1 in case of internal error.
     */
    private int statusCode = -1;

    /**
     * Uploads the GPX Files to OSM.
     * 
     * @param context
     *            the Context of the Application
     * @param consumer
     *            the OAuth Consumer for Authentication
     * @param gpxFile
     *            the gpx File which should be uploaded
     * @param description
     *            description what the gpx tracks are
     * @param tags
     *            tags for the tracks
     * @param visibility
     *            visibility of the tracks
     */
    public UploadToOpenStreetMapTask(Context context, OAuthConsumer consumer,
            File gpxFile, String description, String tags, String visibility) {

        this.context = context;
        this.consumer = consumer;
        this.gpxFile = gpxFile;
        this.description = description;
        this.tags = tags;
        this.visibility = visibility;
    }

    /**
     * Forming the Request.
     */
    @Override
    protected void onPreExecute() {

        try {
            // Prepare request
            request =
                    new HttpPost(OAuthParameters.CURRENT.getScopeUrl()
                            + "api/0.6/gpx/create");

            // Sign the request with oAuth
            consumer.sign(request);

            // Prepare entity
            final MultipartEntity entity =
                    new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Add different parts to entity
            final FileBody gpxBody = new FileBody(gpxFile);
            entity.addPart("file", gpxBody);

            if (description == null || description.length() <= 0) {
                description = "Data4All GPX-Upload";
            }
            entity.addPart("description", new StringBody(description));
            entity.addPart("tags", new StringBody(tags));
            entity.addPart("visibility", new StringBody(visibility));

            // Hand the entity to the request
            request.setEntity(entity);

        } catch (OAuthMessageSignerException e) {
            Log.e(TAG, "OAuthMessageSignerException", e);
        } catch (OAuthExpectationFailedException e) {
            Log.e(TAG, "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e) {
            Log.e(TAG, "OAuthCommunicationException", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException", e);
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
     * Sending the Request.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            final DefaultHttpClient httpClient = new DefaultHttpClient();

            final HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();

            Log.d(TAG, "OSM Gpx Upload: " + statusCode);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "doInBackground failed", e);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground failed", e);
        }

        return null;
    }

}
