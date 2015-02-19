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
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;

import android.content.Context;

/**
 * This class provides several methods for creating, parsing and uploading
 * changssets.
 * 
 * @author Richard (first implementations, + added the closeChangeset)
 * @author tbrose (sourcecode rearrangement)
 *
 */
public final class ChangesetUtil {

    /**
     * Private Constructor, prevents instantiation.
     */
    private ChangesetUtil() {
    }

    private static String getChangesetRequest(String comment) {
        return "<osm>\n<changeset>\n"
                + "<tag k=\"created_by\" v=\"Data4All\"/>\n"
                + "<tag k=\"comment\" v=\"" + comment + "\"/>\n"
                + "</changeset>\n</osm>";
    }

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
     *            The user to request a id for
     * @param comment
     *            The changeset-comment
     * @return the changeset-id
     * @throws OsmException
     *             If the changeset-id cannot be grabbed
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
     * 
     * @param context
     * @param changesetId
     * @return
     * @throws OsmException
     */
    public static String getChangesetXml(Context context, int changesetId)
            throws OsmException {
        try {
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
        } catch (JSONException e) {
            throw new OsmException(e);
        }
    }

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
     * 
     * @param user
     * @param changesetId
     * @param changesetXml
     * @param callback
     * @throws OsmException
     */
    public static CloseableUpload upload(User user, int changesetId,
            String changesetXml, Callback<Integer> callback)
            throws OsmException {
        try {
            final HttpPost request = getUploadPost(user, changesetId);

            // Setting the Entity
            final HttpEntity entity = new MyStringEntry(changesetXml, callback);
            request.setEntity(entity);

            return new CloseableUpload(request);
        } catch (UnsupportedEncodingException e) {
            throw new OsmException(e);
        }
    }

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
}
