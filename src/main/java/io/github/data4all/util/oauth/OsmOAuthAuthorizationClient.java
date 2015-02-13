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
package io.github.data4all.util.oauth;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.User;
import io.github.data4all.util.oauth.exception.OsmLoginFailedException;
import io.github.data4all.util.oauth.exception.OsmOAuthAuthorizationException;
import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthException;

/**
 * An OAuth 1.0 authorization client.
 * 
 * @author tbrose (inspired by JOSM)
 */
public class OsmOAuthAuthorizationClient {
    private static final String OSM_SESSION = "_osm_session=";
    private static final String COOKIE = "Cookie";
    private static final String LOG_TAG = "OsmOAuthAuthorizationClient";

    private final OAuthConsumer consumer;
    private final OAuthProvider provider;
    private HttpURLConnection connection;
    private final OAuthParameters parameters;

    /**
     * Creates a new authorization client with the parameters
     * <code>parameters</code>.
     *
     * @param parameters
     *            the OAuth parameters. Must not be null.
     */
    public OsmOAuthAuthorizationClient(OAuthParameters parameters) {
        this.parameters = parameters;
        consumer = parameters.createConsumer();
        provider = parameters.createProvider();
    }

    /**
     * Automatically authorizes a osmUser.
     *
     * @param osmUserName
     *            the OSM user name. Must not be null.
     * @param osmPassword
     *            the OSM password. Must not be null.
     * @return A valid user with name and tokens.
     * @throws OsmOAuthAuthorizationException
     *             if the authorization fails
     */
    public User authorise(String osmUserName, String osmPassword)
            throws OsmOAuthAuthorizationException {
        final SessionId sessionId = this.fetchOsmWebsiteSessionId(osmUserName);
        this.authenticateOsmSession(sessionId, osmUserName, osmPassword);
        this.sendAuthorisationRequest(sessionId, this.getRequestToken());
        this.logoutOsmSession();
        try {
            provider.retrieveAccessToken(consumer, null);
            return new User(sessionId.getUserName(), consumer.getToken(),
                    consumer.getTokenSecret());
        } catch (OAuthException e) {
            throw new OsmOAuthAuthorizationException(e);
        }
    }

    /**
     * Submits a request for a Request Token to the Request Token Endpoint Url
     * of the OAuth Service Provider and replies the request token.
     *
     * @return the OAuth Request Token
     * @throws OsmOAuthAuthorizationException
     *             if something goes wrong when retrieving the request token
     * @throws OsmTransferCanceledException
     *             if the user canceled the request
     */
    private User getRequestToken() throws OsmOAuthAuthorizationException {
        try {
            provider.retrieveRequestToken(consumer, "");
            return new User(consumer.getToken(), consumer.getTokenSecret());
        } catch (OAuthException e) {
            throw new OsmOAuthAuthorizationException(e);
        }
    }

    /**
     * Builds the authorise URL for a given Request Token. Users can be
     * redirected to this URL. There they can login to OSM and authorise the
     * request.
     *
     * @param user
     *            the request token
     * @return the authorise URL for this request
     */
    private String getAuthoriseUrl(User user) {
        final StringBuilder sb = new StringBuilder();

        // OSM is an OAuth 1.0 provider. We just add
        // the oauth request token to
        // the authorisation request, no callback parameter.
        //
        sb.append(parameters.getAuthoriseUrl()).append("?")
                .append(OAuth.OAUTH_TOKEN).append("=")
                .append(user.getOAuthToken());
        return sb.toString();
    }

    private static String extractToken(HttpURLConnection connection) {
        InputStream is = null;
        BufferedReader r = null;
        try {
            is = connection.getInputStream();
            r = new BufferedReader(new InputStreamReader(is, "UTF_8"));
            String c;
            final Pattern p =
                    Pattern.compile(".*authenticity_token.*"
                            + "value=\"([^\"]+)\".*");
            while ((c = r.readLine()) != null) {
                final Matcher m = p.matcher(c);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "extractToken failed", e);
            return null;
        } finally {
            close(r);
            close(is);
        }
        return null;
    }

    /**
     * Extracts the OSM session into a {@link SessionId} object.
     * 
     * @param connection
     *            The connection to read from
     * @param username
     *            The username for the Session
     * @return The session object or {@code null} if the session cannot be
     *         extracted
     */
    private static SessionId extractOsmSession(HttpURLConnection connection,
            String username) {
        final List<String> setCookies =
                connection.getHeaderFields().get("Set-Cookie");
        if (setCookies == null) {
            // no cookies set
            return null;
        }
        for (String setCookie : setCookies) {
            final String[] kvPairs = setCookie.split(";");
            for (String kvPair : kvPairs) {
                final String[] kv = kvPair.trim().split("=");
                if (kv != null && kv.length == 1 + 1
                        && "_osm_session".equals(kv[0])) {
                    // osm session cookie found
                    return buildSessionId(connection, kv[1], username);
                }
            }
        }
        return null;
    }

    /**
     * Builds a {@link SessionId} for this connection with the given id and
     * username.
     * 
     * @param connection
     *            The connection to read the token from
     * @param id
     *            The id to use
     * @param username
     *            The username to use
     * @return The SessionId or {@code null} if the token cannot be extracted.
     */
    private static SessionId buildSessionId(HttpURLConnection connection,
            String id, String username) {
        final String token = extractToken(connection);
        if (token == null) {
            return null;
        } else {
            return SessionId.create(id, token, username);
        }
    }

    /**
     * Converts the parameter map into uri post parameters.
     * 
     * @param parameters
     *            The Parameters to post
     * @return The given parameters as a HTTP-POST string
     * @throws OsmOAuthAuthorizationException
     *             If UTF-8 is not supported
     */
    private static String buildPostRequest(Map<String, String> parameters)
            throws OsmOAuthAuthorizationException {
        try {
            final StringBuilder sb = new StringBuilder();

            for (final Iterator<Entry<String, String>> it =
                    parameters.entrySet().iterator(); it.hasNext();) {
                final Entry<String, String> entry = it.next();
                String value = entry.getValue();
                if (value == null) {
                    value = "";
                }
                sb.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(value, "UTF-8"));
                if (it.hasNext()) {
                    sb.append("&");
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new OsmOAuthAuthorizationException(e);
        }
    }

    /**
     * Derives the OSM logout URL from the OAuth Authorization Website URL.
     *
     * @return the OSM logout URL
     * @throws OsmOAuthAuthorizationException
     *             if something went wrong, in particular if the URLs are
     *             malformed
     */
    private String buildOsmLogoutUrl() throws OsmOAuthAuthorizationException {
        try {
            final URL autUrl = new URL(parameters.getAuthoriseUrl());
            final URL url =
                    new URL("http", autUrl.getHost(), autUrl.getPort(),
                            "/logout");
            return url.toString();
        } catch (MalformedURLException e) {
            throw new OsmOAuthAuthorizationException(e);
        }
    }

    /**
     * Submits a request to the OSM website for a login form. The OSM website
     * replies a session ID in a cookie.
     *
     * @return the session ID structure
     * @throws OsmOAuthAuthorizationException
     *             if something went wrong
     */
    private SessionId fetchOsmWebsiteSessionId(String username)
            throws OsmOAuthAuthorizationException {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(parameters.getUserLoginUrl()).append("?cookie_test=true");
            final URL url = new URL(sb.toString());
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            final SessionId sessionId = extractOsmSession(connection, username);
            if (sessionId == null) {
                throw new OsmOAuthAuthorizationException(String.format(
                        "OSM website did not return a session cookie"
                                + " in response to ''%s'',", url.toString()));
            }
            return sessionId;
        } catch (IOException e) {
            throw new OsmOAuthAuthorizationException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    /**
     * Submits a request to the OSM website for a OAuth form. The OSM website
     * replies a session token in a hidden parameter.
     *
     * @throws OsmOAuthAuthorizationException
     *             if something went wrong
     */
    private void fetchOAuthToken(SessionId sessionId, User requestToken)
            throws OsmOAuthAuthorizationException {
        try {
            final URL url = new URL(this.getAuthoriseUrl(requestToken));
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty(COOKIE,
                    OSM_SESSION + sessionId.getId() + "; _osm_username="
                            + sessionId.getUserName());
            connection.connect();
            sessionId.setToken(extractToken(connection));
            if (sessionId.getToken() == null) {
                throw new OsmOAuthAuthorizationException(String.format(
                        "OSM website did not return a session cookie "
                                + "in response to ''%s'',", url.toString()));
            }
        } catch (IOException e) {
            throw new OsmOAuthAuthorizationException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    /**
     * Send an authentication request for the given user, password and session
     * to the OSM server.
     * 
     * @param sessionId
     *            The session to use
     * @param userName
     *            The username to use
     * @param password
     *            The password to use
     * @throws OsmLoginFailedException
     *             If the given credentials
     */
    private void authenticateOsmSession(SessionId sessionId, String userName,
            String password) throws OsmLoginFailedException {
        try {
            final URL url = new URL(parameters.getUserLoginUrl());
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            final String request =
                    buildPostRequest(authenticateOsmSessionParams(userName,
                            password, sessionId.getToken()));

            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    Integer.toString(request.length()));
            connection.setRequestProperty(COOKIE,
                    OSM_SESSION + sessionId.getId());
            // make sure we can catch 302 Moved Temporarily below
            connection.setInstanceFollowRedirects(false);

            connection.connect();

            DataOutputStream dout = null;
            try {
                dout = new DataOutputStream(connection.getOutputStream());
                dout.writeBytes(request);
                dout.flush();
            } finally {
                close(dout);
            }

            // after a successful login the OSM website sends a redirect to a
            // follow up page. Everything
            // else, including a 200 OK, is a failed login. A 200 OK is replied
            // if the login form with
            // an error page is sent to back to the user.
            //
            final int retCode = connection.getResponseCode();
            if (retCode != HttpURLConnection.HTTP_MOVED_TEMP) {
                throw new OsmOAuthAuthorizationException();
            }
        } catch (OsmOAuthAuthorizationException e) {
            throw new OsmLoginFailedException(e);
        } catch (IOException e) {
            throw new OsmLoginFailedException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    /**
     * Builds the parameter map for the OsmSession authentication.
     * 
     * @param userName
     *            The userName to use
     * @param password
     *            The password to use
     * @param token
     *            The token to use
     * @return The constructed parameter map
     */
    private static Map<String, String> authenticateOsmSessionParams(
            String userName, String password, String token) {
        final Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("username", userName);
        postParams.put("password", password);
        postParams.put("referer", "/");
        postParams.put("commit", "Login");
        postParams.put("authenticity_token", token);
        return postParams;
    }

    private void logoutOsmSession() throws OsmOAuthAuthorizationException {
        try {
            final URL url = new URL(this.buildOsmLogoutUrl());
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
        } catch (IOException e) {
            throw new OsmOAuthAuthorizationException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    /**
     * Send an authorization request for the given user and session to the OSM
     * server.
     * 
     * @param sessionId
     *            The session to use
     * @param user
     *            The user to authorize
     * @throws OsmOAuthAuthorizationException
     *             If the authorization request fails
     */
    private void sendAuthorisationRequest(SessionId sessionId, User user)
            throws OsmOAuthAuthorizationException {
        this.fetchOAuthToken(sessionId, user);
        final String request =
                buildPostRequest(sendAuthorizationRequestParams(
                        sessionId.getToken(), user.getOAuthToken()));
        try {
            final URL url = new URL(this.parameters.getAuthoriseUrl());
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    Integer.toString(request.length()));
            connection.setRequestProperty(COOKIE,
                    OSM_SESSION + sessionId.getId() + "; _osm_username="
                            + sessionId.getUserName());
            connection.setInstanceFollowRedirects(false);

            connection.connect();

            DataOutputStream dout = null;
            try {
                dout = new DataOutputStream(connection.getOutputStream());
                dout.writeBytes(request);
                dout.flush();
            } finally {
                close(dout);
            }

            final int retCode = connection.getResponseCode();
            if (retCode != HttpURLConnection.HTTP_OK) {
                throw new OsmLoginFailedException();
            }
        } catch (IOException e) {
            throw new OsmOAuthAuthorizationException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    /**
     * Builds the parameter map for the authorization request.
     * 
     * @param sessionToken
     *            The sessionToken to use
     * @param userToken
     *            The userToken to use
     * @return The constructed parameter map
     */
    private static Map<String, String> sendAuthorizationRequestParams(
            String sessionToken, String userToken) {
        final Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("oauth_token", userToken);
        postParams.put("oauth_callback", "");
        postParams.put("authenticity_token", sessionToken);

        // Write all privileges
        postParams.put("allow_write_api", "yes");
        postParams.put("allow_write_gpx", "yes");
        postParams.put("allow_read_gpx", "yes");
        postParams.put("allow_write_prefs", "yes");
        postParams.put("allow_read_prefs", "yes");
        postParams.put("allow_write_notes", "yes");

        postParams.put("commit", "Save changes");
        return postParams;
    }

    /**
     * Closes the given {@link Closeable} and logs Exceptions that might be
     * thrown.
     * 
     * @param c
     *            The {@link Closeable} to close
     */
    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignore) {
                Log.e(LOG_TAG, "Exception is thown while closing stream",
                        ignore);
            }
        }
    }

    /**
     * Opens a HTTP connection to the given URL.
     * 
     * @param httpURL
     *            The HTTP url to open (must use http:// or https://)
     * @return An open HTTP connection to the given URL
     * @throws java.io.IOException
     *             if an I/O exception occurs.
     */
    private static HttpURLConnection openHttpConnection(URL httpURL)
            throws IOException {
        if (httpURL == null
                || !Pattern.compile("https?").matcher(httpURL.getProtocol())
                        .matches()) {
            throw new IllegalArgumentException("Invalid HTTP url");
        }
        Log.d(LOG_TAG, "Open connection to: " + httpURL.toString());
        final HttpURLConnection connection =
                (HttpURLConnection) httpURL.openConnection();
        connection.setUseCaches(false);
        return connection;
    }
}
