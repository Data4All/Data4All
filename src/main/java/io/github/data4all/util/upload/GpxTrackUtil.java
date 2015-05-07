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
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.User;
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
 * This class provides several methods for creating, parsing and uploading gpx
 * tracks.
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
     * @param context
     *            the application context
     * @param user
     *            The {@link User} who uploads the Changeset.
     * @param track
     *            the gpx xml string
     * @param description
     *            The trace description.
     * @param tags
     *            A string containing tags for the trace. (comma seperated)
     * @param visibility
     *            One of the following: private, public, trackable,
     *            identifiable.
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

        final MultipartEntity reqEntity = new MultiCallbackEntry(callback);
        final FileBody fileBody =
                new FileBody(createGpxFile(context, track, "file.gpx"));

        try {
            reqEntity.addPart("file", fileBody);
            reqEntity.addPart("description", new StringBody(description));
            reqEntity.addPart("tags", new StringBody(tags));
            reqEntity.addPart("visibility", new StringBody(visibility));
        } catch (UnsupportedEncodingException e) {
            Log.e(GpxTrackUtil.class.getSimpleName(), "Exception: ", e);
        }

        request.setEntity(reqEntity);
        request.addHeader("ContentType", "multipart/form-data");
        // Setting the Entity

        // final HttpEntity entity = new MultiCallbackEntry(callback);
        // request.setEntity(entity);

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
     * Test method to debug the upload.
     * 
     * @return xml file with tracks
     */
    public static String testTrack() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"Data4All - https://data4all.github.io/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\" >");
        sb.append("<trk>");
        sb.append("<name>file.gpx</name>");
        sb.append("<trkseg>");
        sb.append("<trkpt lat=\"53.09714087006391\" lon=\"8.835233152546115\">");
        sb.append("<time>2015-03-05T02:57:42Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"53.097029161158886\" lon=\"8.835149370872804\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:43Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"53.096913698361774\" lon=\"8.835062773771332\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:44Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"53.0967978943368\" lon=\"8.834975920752603\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.123434\" lon=\"4.12345644\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"52.12324343\" lon=\"4.123123432\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324324\" lon=\"5.123123123\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.324435345324\" lon=\"5.345345\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.4535435\" lon=\"5.35435453\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.546456456546\" lon=\"5.45645435\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"51.3242343\" lon=\"5.3222332\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");

        sb.append("</trkseg>");
        sb.append("</trk>");
        sb.append("</gpx>");
        return sb.toString();
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
        final File file = new File(context.getFilesDir(), fileName);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(trackXml);
            writer.close();
        } catch (IOException e) {
            Log.e(GpxTrackUtil.class.getSimpleName(), "IOException: ", e);
        }
        return file;
    }

    /**
     * Checks the number of elements in the database and returns true if is a
     * upload necessary.
     * 
     * @param context
     *            the application context
     * @return true, if upload is necessary
     */
    public static boolean needToUpload(Context context) {
        final DataBaseHandler db = new DataBaseHandler(context);
        final List<Track> elems = db.getAllGPSTracks();
        return !elems.isEmpty();
    }

}
