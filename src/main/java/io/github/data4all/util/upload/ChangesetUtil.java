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
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.User;
import io.github.data4all.util.OsmChangeParser;
import io.github.data4all.util.oauth.exception.OsmException;
import io.github.data4all.util.oauth.parameters.OAuthParameters;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.ResultReceiver;

/**
 * This class provides several methods for creating, parsing and uploading
 * changssets.
 * 
 * @author Richard (first implementations, + added the closeChangeset)
 * @author tbrose (sourcecode rearrangement)
 *
 */
@SuppressLint("SimpleDateFormat")
public final class ChangesetUtil {

    /**
     * Private Constructor, prevents instantiation.
     */
    private ChangesetUtil() {
    }

    /**
     * Returns a Changeset-Request to send to the OSM API.
     * 
     * @param comment
     *            The Comment specified for by the User.
     * @return The Changeset-Request.
     */
    private static String getChangesetRequest(String comment) {
        return "<osm>\n<changeset>\n"
                + "<tag k=\"created_by\" v=\"Data4All\"/>\n"
                + "<tag k=\"comment\" v=\"" + comment + "\"/>\n"
                + "</changeset>\n</osm>";
    }

    /**
     * Creates a {@link HttpPut} request and signs it by writing an OAuth
     * signature to it.
     * 
     * @param user
     *            The {@link User} account to sign the {@link HttpPut} with.
     * @return A {@link HttpPut} Request.
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    private static HttpPut getChangeSetPut(User user) throws OsmException {
        final OAuthParameters params = OAuthParameters.CURRENT;
        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                params.getConsumerKey(), params.getConsumerSecret());
        consumer.setTokenWithSecret(user.getOAuthToken(),
                user.getOauthTokenSecret());
        final HttpPut httpPut = new HttpPut(params.getScopeUrl()
                + "api/0.6/changeset/create");
        try {
            consumer.sign(httpPut);
        } catch (OAuthMessageSignerException e) {
            throw new OsmException(e);
        } catch (OAuthExpectationFailedException e) {
            throw new OsmException(e);
        } catch (OAuthCommunicationException e) {
            throw new OsmException(e);
        }
        return httpPut;
    }

    /**
     * Requests a changeset-id from the OSM-API.
     * 
     * @param user
     *            The {@link User} to request a id for.
     * @param comment
     *            The Comment for the Changeset.
     * @return The Changeset ID.
     * @throws OsmException
     *             If the Changeset ID cannot be grabbed.
     */
    public static CloseableRequest requestId(final User user,
            final String comment) throws OsmException {
        try {
            final HttpPut request = getChangeSetPut(user);
            // Add the xml-request-string
            request.setEntity(new StringEntity(getChangesetRequest(comment)));

            return new CloseableRequest(request);

        } catch (UnsupportedEncodingException e) {
            throw new OsmException(e);
        }
    }

    /**
     * Parses all objects from the database into an xml structure to create a
     * new Changeset.
     * 
     * @param context
     *            The application context for the {@link DataBaseHandler}.
     * @param changesetId
     *            The Changeset ID from the OSM API.
     * @return The created Changeset.
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    public static String getChangesetXml(Context context, int changesetId)
            throws OsmException {
        final DataBaseHandler db = new DataBaseHandler(context);
        final List<AbstractDataElement> elems = db.getAllDataElements();
        db.close();

        final StringBuilder builder = new StringBuilder();

        final PrintWriter writer = new PrintWriter(new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                builder.append((char) oneByte);
            }
        });

        OsmChangeParser.parseElements(elems, changesetId, writer);
        writer.close();
        return builder.toString();
    }

    /**
     * Checks the number of elements in the database and returns true if is a
     * upload necessary.
     * 
     * @param context
     *            the context
     * @return true, if upload is necessary
     */
    public static boolean needToUpload(Context context) {
        final DataBaseHandler db = new DataBaseHandler(context);
        final List<AbstractDataElement> elems = db.getAllDataElements();
        return !elems.isEmpty();
    }

    /**
     * Returns a {@link HttpPost} containing the Changeset ID which is signed by
     * the OAuth {@link User}.
     * 
     * @param user
     *            The OAuth {@link User}.
     * @param id
     *            The Changeset ID.
     * @return The signed {@link HttpPost}.
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    private static HttpPost getUploadPost(User user, int id)
            throws OsmException {
        final OAuthParameters params = OAuthParameters.CURRENT;
        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                params.getConsumerKey(), params.getConsumerSecret());
        consumer.setTokenWithSecret(user.getOAuthToken(),
                user.getOauthTokenSecret());
        final HttpPost httpPost = new HttpPost(params.getScopeUrl()
                + "api/0.6/changeset/" + id + "/upload");
        try {
            consumer.sign(httpPost);
        } catch (OAuthMessageSignerException e) {
            throw new OsmException(e);
        } catch (OAuthExpectationFailedException e) {
            throw new OsmException(e);
        } catch (OAuthCommunicationException e) {
            throw new OsmException(e);
        }

        final BasicHttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("id", id);
        httpPost.setParams(httpParams);
        return httpPost;
    }

    /**
     * Returns a {@link CloseableUpload} containing all nessesary informations
     * for the upload to the OSM API.
     * 
     * @param user
     *            The {@link User} who uploads the Changeset.
     * @param changesetId
     *            The Changeset ID.
     * @param changesetXml
     *            The Changeset which should be uploaded.
     * @param callback
     *            The callback object for interaction with the
     *            {@link ResultReceiver}.
     * @return {@link CloseableUpload} object
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    public static CloseableUpload upload(User user, int changesetId,
            String changesetXml, Callback<Integer> callback)
            throws OsmException {
        try {
            final HttpPost request = getUploadPost(user, changesetId);

            // Setting the Entity
            final HttpEntity entity = new CallbackStringEntry(changesetXml,
                    callback);
            request.setEntity(entity);

            return new CloseableUpload(request);
        } catch (UnsupportedEncodingException e) {
            throw new OsmException(e);
        }
    }

    /**
     * Returns a {@link HttpPut} which closes the Changeset with the given ID.
     * 
     * @param user
     *            The {@link User} account to sign the {@link HttpPut}.
     * @param changeSetId
     *            The Changeset ID.
     * @return The signed {@link HttpPut}.
     * @throws OsmException
     *             Indicates an failure in an osm progess.
     */
    private static HttpPut getChangeSetClose(User user, long changeSetId)
            throws OsmException {
        final OAuthParameters params = OAuthParameters.CURRENT;
        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                params.getConsumerKey(), params.getConsumerSecret());
        consumer.setTokenWithSecret(user.getOAuthToken(),
                user.getOauthTokenSecret());
        final HttpPut httpPut = new HttpPut(params.getScopeUrl()
                + "api/0.6/changeset/" + changeSetId + "/close");
        try {
            consumer.sign(httpPut);
        } catch (OAuthMessageSignerException e) {
            throw new OsmException(e);
        } catch (OAuthExpectationFailedException e) {
            throw new OsmException(e);
        } catch (OAuthCommunicationException e) {
            throw new OsmException(e);
        }
        final BasicHttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("id", changeSetId);
        httpPut.setParams(httpParams);
        return httpPut;
    }

    /**
     * Requests a Closure of a changesetId from the OSM-API.
     * 
     * @param user
     *            The user to request the Closure of the id
     * @param changesetId
     *            The Id of the changeSet which should be closed
     * @return The ClosableCloseRequest which sends the Request
     * @throws OsmException
     *             If the changesetId cannot be grabbed
     */
    public static CloseableCloseRequest closeId(final User user,
            final long changesetId) throws OsmException {
        final HttpPut request = getChangeSetClose(user, changesetId);
        return new CloseableCloseRequest(request);
    }

    /**
     * Returns a {@link HttpGet} containing the boundingbox and time.
     * 
     * @param time
     *            the time from where we are looking for new changeSets
     * @param min_lon
     *            lowest Longitude of the BoundingBox
     * @param min_lat
     *            lowest Latitude of the BoundingBox
     * @param max_lon
     *            Highest Longitude of the BoundingBox
     * @param max_lat
     *            highest Latitude of the BoundingBox
     * @return HttpGet with Params
     */
    private static HttpGet getChangesetGet(long time, double min_lon,
            double min_lat, double max_lon, double max_lat)  {
        final OAuthParameters params = OAuthParameters.CURRENT;
        final HttpGet httpGet = new HttpGet(params.getScopeUrl()
                + "api/0.6/changesets");

        final BasicHttpParams httpParams = new BasicHttpParams();
        List<Double> bbox = new LinkedList<Double>();
        bbox.add(min_lon);
        bbox.add(min_lat);
        bbox.add(max_lon);
        bbox.add(max_lat);

        String timeformat = "yyyy-MM-dd'T'HH:mm:ss.SZ";
        final SimpleDateFormat dateformat = new SimpleDateFormat(timeformat);
        httpParams.setParameter("bbox", bbox);
        httpParams.setParameter("time", dateformat.format(new Date(time)));
        httpParams.setParameter("closed", true);

        httpGet.setParams(httpParams);
        return httpGet;
    }

    /**
     * Requests a list of closed changesets from the OSM-API.
     * 
     * @param time
     *            the time from where we are looking for new changeSets
     * @param min_lon
     *            lowest Longitude of the BoundingBox
     * @param min_lat
     *            lowest Latitude of the BoundingBox
     * @param max_lon
     *            Highest Longitude of the BoundingBox
     * @param max_lat
     *            highest Latitude of the BoundingBox
     * @return The ClosableCloseRequest which sends the Request
     * @throws OsmException
     *             If the changesetId cannot be grabbed
     */
    public static CloseableGetChangeSets getChangeSet(long time,
            double min_lon, double min_lat, double max_lon, double max_lat)
            throws OsmException {
        final HttpGet request = getChangesetGet(time, min_lon, min_lat,
                max_lon, max_lat);
        return new CloseableGetChangeSets(request);
    }

}
