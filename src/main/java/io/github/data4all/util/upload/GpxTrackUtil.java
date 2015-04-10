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
package io.github.data4all.util.upload;

import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.User;
import io.github.data4all.task.NewUploadTracksTask;
import io.github.data4all.util.oauth.exception.OsmException;
import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.os.ResultReceiver;

/**
 * 
 * @author fkirchge
 *
 */
public final class GpxTrackUtil {

    /**
     * Private Constructor, prevents instantiation.
     */
    private GpxTrackUtil() {
    }

    /**
     * Returns a {@link CloseableUpload} containing all nessesary informations
     * for the upload to the OSM API.
     * 
     * @param user
     *            The {@link User} who uploads the Changeset.
     * @param callback
     *            The callback object for interaction with the
     *            {@link ResultReceiver}.
     * @return {@link CloseableUpload} object
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    public static CloseableUpload upload(Context context, User user,
            String track, String description, String tags, String visibility,
            Callback<Integer> callback) throws OsmException {
        final HttpPost request = getUploadPost(user);

        MultipartEntity reqEntity = new MultipartEntity();
        FileBody fileBody =
                new FileBody(createGpxFile(context,
                        NewUploadTracksTask.testTrack(), "file.gpx"));

        try {
            reqEntity.addPart("file", fileBody);
            reqEntity.addPart("description", new StringBody(description));
            reqEntity.addPart("tags", new StringBody(tags));
            reqEntity.addPart("visibility", new StringBody(visibility));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        request.setEntity(reqEntity);
        request.addHeader("ContentType", "multipart/form-data");
        // Setting the Entity
//        final HttpEntity entity = new MultiCallbackEntry(callback);
//        request.setEntity(entity);

        return new CloseableUpload(request);
    }

    /**
     * Returns a {@link HttpPost} containing the Changeset ID which is signed by
     * the OAuth {@link User}.
     * 
     * @param user
     *            The OAuth {@link User}.
     * @return The signed {@link HttpPost}.
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    private static HttpPost getUploadPost(User user) throws OsmException {
        final OAuthParameters params = OAuthParameters.CURRENT;
        final OAuthConsumer consumer =
                new CommonsHttpOAuthConsumer(params.getConsumerKey(),
                        params.getConsumerSecret());
        consumer.setTokenWithSecret(user.getOAuthToken(),
                user.getOauthTokenSecret());
        final HttpPost httpPost =
                new HttpPost("http://www.openstreetmap.org/api/0.6/gpx/create");
        try {
            consumer.sign(httpPost);
        } catch (OAuthMessageSignerException e) {
            throw new OsmException(e);
        } catch (OAuthExpectationFailedException e) {
            throw new OsmException(e);
        } catch (OAuthCommunicationException e) {
            throw new OsmException(e);
        }
        return httpPost;
    }

    /**
     * Method for reading a track from memory. Return a string representation of
     * a saved track.
     * 
     * @param context
     *            the context
     * @param trackXml
     *            the xml file of the gpx track
     * @param fileName
     *            the filename of the gpx track
     * @return a file object containing the gpx tracks
     */
    private static File createGpxFile(Context context, String trackXml,
            String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(trackXml);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Checks the number of elements in the database and returns true if is a
     * upload necessary.
     * 
     * @param context
     * @return true, if upload is necessary
     */
    public static boolean needToUpload(Context context) {
        final DataBaseHandler db = new DataBaseHandler(context);
        final List<Track> elems = db.getAllGPSTracks();
        if (!elems.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
