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
import io.github.data4all.R;

import java.io.File;
import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to upload a OsmChange File to OpenStreetMap.
 * 
 * @author Richard
 *
 */

public class UploadingOsmChangeFileToOpenStreetMapTask extends
		AsyncTask<Void, Void, Void> {

	private final String TAG = getClass().getSimpleName();
	private Context context;

	private HttpPost request;

	private OAuthConsumer consumer;
	private File oscFile;
	private long id;

	/**
	 * HTTP result code or -1 in case of internal error.
	 */
	private int statusCode = -1;

	/**
	 * Constructor of the UploadingOsmChangeFileToOpenStreetMapTask.
	 * 
	 * Sets the Parameter values to the local attributes
	 * 
	 * @param context
	 *            The context of the Application
	 * @param consumer
	 *            The OAuthconsumer used for authentication
	 * @param id
	 *            The ChangeSetID required for uploading
	 */
	public UploadingOsmChangeFileToOpenStreetMapTask(Context context,
			OAuthConsumer consumer, long id) {

		this.context = context;
		this.consumer = consumer;
		oscFile = new File(context.getFilesDir().getAbsolutePath()
				+ "/OsmChangeUpload.osc");
		this.id = id;
	}

	/**
	 * Forms the HttpRequest.
	 */
	@Override
	protected void onPreExecute() {

		try {
			// Prepare request
			request = new HttpPost(Constants.SCOPE + "api/0.6/changeset/" + id
					+ "/upload");
			// Sign the request with oAuth
			this.consumer.sign(request);

			// Setting the Parameter
			final BasicHttpParams params = new BasicHttpParams();
			params.setParameter("id", id);
			request.setParams(params);

			// Setting the Entity
			final HttpEntity entity = new FileEntity(oscFile, "text/plain");
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
			// Toast.makeText(context,
			// R.string.uploadToOsmRequestInternalTaskError,
			// Toast.LENGTH_LONG).show();
			Log.d(TAG, "Internal error, the request did not start at all");
			break;
		case HttpStatus.SC_OK:
			// Success ! Update database and close activity
			// Toast.makeText(context, R.string.success,
			// Toast.LENGTH_LONG).show();
			Log.d(TAG, "Success");
			break;
		case HttpStatus.SC_UNAUTHORIZED:
			// Does not have authorization
			// Toast.makeText(context, R.string.uploadToOsmRequestTaskAuthError,
			// Toast.LENGTH_LONG).show();
			Log.d(TAG, "Does not have authorization");
			break;
		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			Toast.makeText(context, "INTERNAL SERVER ERROR", Toast.LENGTH_LONG)
					.show();
			Log.d(TAG, "INTERNAL SERVER ERROR");
			break;

		default:
			// unknown error
			// Toast.makeText(context, R.string.unkownError, Toast.LENGTH_LONG)
			// .show();
			Log.d(TAG, "Unknown error");
		}
	}

	/**
	 * Sending the Request and analyzes the response.
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			final DefaultHttpClient httpClient = new DefaultHttpClient();

			// Sending the Request
			final HttpResponse response = httpClient.execute(request);
			statusCode = response.getStatusLine().getStatusCode();
			final HttpEntity responseEntity = response.getEntity();

			Log.d(TAG, "OSM ChangeSetID response Entity: " + responseEntity);
			Log.d(TAG, "OSM ChangeSetID statusCode: " + statusCode);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "doInBackground failed", e);
		} catch (IOException e) {
            Log.e(TAG, "doInBackground failed", e);
        }

		return null;
	}

}
