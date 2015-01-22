package io.github.data4all.task;

import io.github.data4all.Constants;
import io.github.data4all.R;

import java.io.File;
import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to upload a gpx file to OpenStreetMap 
 * INFO: Request is getting status code 500 if using dev api!
 * 
 * @author sb
 *
 */

public class UploadToOpenStreetMapTask extends AsyncTask<Void, Void, Void> {

    private final String  TAG        = getClass().getSimpleName();
    private Context       context;

    private HttpPost      request;

    private OAuthConsumer consumer;
    private File          gpxFile;
    private String        description;
    private String        tags;
    private String        visibility;

    /**
     * HTTP result code or -1 in case of internal error
     */
    private int           statusCode = -1;

    public UploadToOpenStreetMapTask(Context context, OAuthConsumer consumer,
            File gpxFile, String description, String tags, String visibility) {

        this.context = context;
        this.consumer = consumer;
        this.gpxFile = gpxFile;
        this.description = description;
        this.tags = tags;
        this.visibility = visibility;
    }

    @Override
    protected void onPreExecute() {

        try {
            // Prepare request
            request = new HttpPost(Constants.SCOPE + "/api/0.6/gpx/create");

            // Sign the request with oAuth
            consumer.sign(request);

            // Prepare entity
            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            // Add different parts to entity
            FileBody gpxBody = new FileBody(gpxFile);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        switch (statusCode) {
        case -1:
            // Internal error, the request didn't start at all
            Toast.makeText(context,
                    R.string.uploadToOsmRequestInternalTaskError,
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Internal error, the request did not start at all");
            break;
        case HttpStatus.SC_OK:
            // Success ! Update database and close activity
            Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Success");
            break;
        case HttpStatus.SC_UNAUTHORIZED:
            // Does not have authorization
            Toast.makeText(context, R.string.uploadToOsmRequestTaskAuthError,
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Does not have authorization");
            break;
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            Toast.makeText(context, "INTERNAL SERVER ERROR", Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "INTERNAL SERVER ERROR");
            break;

        default:
            // unknown error
            Toast.makeText(context, R.string.unkownError, Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "Unknown error");
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();

            Log.d(TAG, "OSM Gpx Upload: " + statusCode);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground failed", e);
            e.printStackTrace();
        }

        return null;
    }

}
