package io.github.data4all.util.oauth;

// License: GPL. For details, see LICENSE file.

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.User;
import io.github.data4all.util.oauth.exception.OsmLoginFailedException;
import io.github.data4all.util.oauth.exception.OsmOAuthAuthorizationException;
import io.github.data4all.util.oauth.parameters.DevelopOAuthParameters;
import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.io.BufferedReader;
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
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthException;

/**
 * An OAuth 1.0 authorization client.
 * 
 * @since 2746
 */
public class OsmOAuthAuthorizationClient {
    private static final String LOG_TAG = OsmOAuthAuthorizationClient.class.getSimpleName();

    private final OAuthConsumer consumer;
    private final OAuthProvider provider;
    private HttpURLConnection connection;
    private final OAuthParameters parameters;

    private static class SessionId {
        String id;
        String token;
        String userName;
    }

    /**
     * Creates a new authorisation client with the parameters
     * <code>parameters</code>.
     *
     * @param parameters
     *            the OAuth parameters. Must not be null.
     */
    public OsmOAuthAuthorizationClient(OAuthParameters parameters) {
        this.parameters = parameters;
        consumer = new DefaultOAuthConsumer(parameters.getConsumerKey(),
                        parameters.getConsumerSecret());
        provider = new DefaultOAuthProvider(parameters.getRequestTokenUrl(),
                        parameters.getAccessTokenUrl(),
                        parameters.getAuthoriseUrl());
    }

    /**
     * Automatically authorises a request token for a set of privileges.
     *
     * @param requestToken
     *            the request token. Must not be null.
     * @param osmUserName
     *            the OSM user name. Must not be null.
     * @param osmPassword
     *            the OSM password. Must not be null.
     * @param privileges
     *            the set of privileges. Must not be null.
     * @throws OsmOAuthAuthorizationException
     *             if the authorisation fails
     */
    public User authorise(String osmUserName,
            String osmPassword) throws OsmOAuthAuthorizationException {
        SessionId sessionId = fetchOsmWebsiteSessionId();
        sessionId.userName = osmUserName;
        authenticateOsmSession(sessionId, osmUserName, osmPassword);
        sendAuthorisationRequest(sessionId, getRequestToken());
        logoutOsmSession(sessionId);
        try {
            provider.retrieveAccessToken(consumer, null);
            return new User(sessionId.userName, consumer.getToken(), consumer.getTokenSecret());
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
        StringBuilder sb = new StringBuilder();

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
            r = new BufferedReader(new InputStreamReader(is,"UTF_8"));
            String c;
            Pattern p =
                    Pattern.compile(".*authenticity_token.*value=\"([^\"]+)\".*");
            while ((c = r.readLine()) != null) {
                Matcher m = p.matcher(c);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignore) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }

    private static SessionId extractOsmSession(HttpURLConnection connection) {
        List<String> setCookies =
                connection.getHeaderFields().get("Set-Cookie");
        if (setCookies == null)
            // no cookies set
            return null;

        for (String setCookie : setCookies) {
            String[] kvPairs = setCookie.split(";");
            if (kvPairs == null || kvPairs.length == 0) {
                continue;
            }
            for (String kvPair : kvPairs) {
                kvPair = kvPair.trim();
                String[] kv = kvPair.split("=");
                if (kv == null || kv.length != 2) {
                    continue;
                }
                if ("_osm_session".equals(kv[0])) {
                    // osm session cookie found
                    String token = extractToken(connection);
                    if (token == null)
                        return null;
                    SessionId si = new SessionId();
                    si.id = kv[1];
                    si.token = token;
                    return si;
                }
            }
        }
        return null;
    }

    private static String buildPostRequest(Map<String, String> parameters)
            throws OsmOAuthAuthorizationException {
        try {
            StringBuilder sb = new StringBuilder();

            for (Iterator<Entry<String, String>> it =
                    parameters.entrySet().iterator(); it.hasNext();) {
                Entry<String, String> entry = it.next();
                String value = entry.getValue();
                value = (value == null) ? "" : value;
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
     * Derives the OSM login URL from the OAuth Authorization Website URL
     *
     * @return the OSM login URL
     * @throws OsmOAuthAuthorizationException
     *             if something went wrong, in particular if the URLs are
     *             malformed
     */
    private String buildOsmLoginUrl() throws OsmOAuthAuthorizationException {
        try {
            URL autUrl = new URL(parameters.getAuthoriseUrl());
            URL url =
                    new URL(autUrl.getProtocol(), autUrl.getHost(),
                            autUrl.getPort(), "/login");
            return url.toString();
        } catch (MalformedURLException e) {
            throw new OsmOAuthAuthorizationException(e);
        }
    }

    /**
     * Derives the OSM logout URL from the OAuth Authorization Website URL
     *
     * @return the OSM logout URL
     * @throws OsmOAuthAuthorizationException
     *             if something went wrong, in particular if the URLs are
     *             malformed
     */
    private String buildOsmLogoutUrl() throws OsmOAuthAuthorizationException {
        try {
            URL autUrl = new URL(parameters.getAuthoriseUrl());
            URL url =
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
    private SessionId fetchOsmWebsiteSessionId()
            throws OsmOAuthAuthorizationException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(buildOsmLoginUrl()).append("?cookie_test=true");
            URL url = new URL(sb.toString());
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            SessionId sessionId = extractOsmSession(connection);
            if (sessionId == null)
                throw new OsmOAuthAuthorizationException(
                        String.format(
                                "OSM website did not return a session cookie in response to ''%s'',",
                                url.toString()));
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
            URL url = new URL(getAuthoriseUrl(requestToken));
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("Cookie", "_osm_session="
                    + sessionId.id + "; _osm_username=" + sessionId.userName);
            connection.connect();
            sessionId.token = extractToken(connection);
            if (sessionId.token == null)
                throw new OsmOAuthAuthorizationException(
                        String.format(
                                "OSM website did not return a session cookie in response to ''%s'',",
                                url.toString()));
        } catch (IOException e) {
            throw new OsmOAuthAuthorizationException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    private void authenticateOsmSession(SessionId sessionId, String userName,
            String password) throws OsmLoginFailedException {
        try {
            URL url = new URL(buildOsmLoginUrl());
            synchronized (this) {
                connection = openHttpConnection(url);
            }
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("username", userName);
            parameters.put("password", password);
            parameters.put("referer", "/");
            parameters.put("commit", "Login");
            parameters.put("authenticity_token", sessionId.token);

            String request = buildPostRequest(parameters);

            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    Integer.toString(request.length()));
            connection.setRequestProperty("Cookie", "_osm_session="
                    + sessionId.id);
            // make sure we can catch 302 Moved Temporarily below
            connection.setInstanceFollowRedirects(false);

            connection.connect();
            
            DataOutputStream dout = null;
            try {
                dout = new DataOutputStream(connection.getOutputStream());
                dout.writeBytes(request);
                dout.flush();
            } finally {
                if (dout != null) {
                    try {
                        dout.close();
                    } catch (Exception ignore) {
                    }
                }
            }

            // after a successful login the OSM website sends a redirect to a
            // follow up page. Everything
            // else, including a 200 OK, is a failed login. A 200 OK is replied
            // if the login form with
            // an error page is sent to back to the user.
            //
            int retCode = connection.getResponseCode();
            if (retCode != HttpURLConnection.HTTP_MOVED_TEMP)
                throw new OsmOAuthAuthorizationException(
                        String.format(
                                "Failed to authenticate user ''%s'' with password ''***'' as OAuth user",
                                userName));
        } catch (OsmOAuthAuthorizationException e) {
            throw new OsmLoginFailedException(e.getCause());
        } catch (IOException e) {
            throw new OsmLoginFailedException(e);
        } finally { 
            synchronized (this) {
                connection = null;
            }
        }
    }

    private void logoutOsmSession(SessionId sessionId)
            throws OsmOAuthAuthorizationException {
        try {
            URL url = new URL(buildOsmLogoutUrl());
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

    private void sendAuthorisationRequest(SessionId sessionId,
            User user) throws OsmOAuthAuthorizationException {
        Map<String, String> parameters = new HashMap<String, String>();
        fetchOAuthToken(sessionId, user);
        parameters.put("oauth_token", user.getOAuthToken());
        parameters.put("oauth_callback", "");
        parameters.put("authenticity_token", sessionId.token);

        // Write all privileges
        parameters.put("allow_write_api", "yes");
        parameters.put("allow_write_gpx", "yes");
        parameters.put("allow_read_gpx", "yes");
        parameters.put("allow_write_prefs", "yes");
        parameters.put("allow_read_prefs", "yes");
        parameters.put("allow_write_notes", "yes");

        parameters.put("commit", "Save changes");

        String request = buildPostRequest(parameters);
        try {
            URL url = new URL(this.parameters.getAuthoriseUrl());
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
            connection.setRequestProperty("Cookie", "_osm_session="
                    + sessionId.id + "; _osm_username=" + sessionId.userName);
            connection.setInstanceFollowRedirects(false);

            connection.connect();
            
            DataOutputStream dout = null;
            try {
                dout = new DataOutputStream(connection.getOutputStream());
                dout.writeBytes(request);
                dout.flush();
            } finally {
                if (dout != null) {
                    try {
                        dout.close();
                    } catch (Exception ignore) {
                    }
                }
            }

            int retCode = connection.getResponseCode();
            if (retCode != HttpURLConnection.HTTP_OK)
                throw new OsmOAuthAuthorizationException(String.format(
                        "Failed to authorize OAuth request  ''%s'', code: %s",
                        user.getOAuthToken(), retCode));
        } catch (IOException e) {
            throw new OsmOAuthAuthorizationException(e);
        } finally {
            synchronized (this) {
                connection = null;
            }
        }
    }

    /**
     * Opens a HTTP connection to the given URL
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
        HttpURLConnection connection =
                (HttpURLConnection) httpURL.openConnection();
        connection.setUseCaches(false);
        return connection;
    }
}
