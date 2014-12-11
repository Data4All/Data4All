package io.github.data4all;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.Resources;
import android.util.Log;

/**
 * @author Based on Server.java of vespucci (sb)
 * @source https://code.google.com/p/osmeditor4android/
 * 
 */
public class Server {

    /**
     * Timeout for connections in milliseconds.
     */
    private static final int     TIMEOUT = 45 * 1000;

    /**
     * Location of OSM API
     */
    private final String         serverURL;

    /**
     * username for write-access on the server.
     */
    private final String         username;

    /**
     * oauth access token
     */
    private String               accesstoken;

    /**
     * oauth access token secret
     */
    private String               accesstokensecret;

    /**
     * display name of the user.
     */
    private UserDetails          userDetails;

    /**
     * <a href="http://wiki.openstreetmap.org/wiki/API">API</a>-Version.
     */
    private static final String  version = "0.6";

    private XmlPullParserFactory xmlParserFactory;

    /**
     * Constructor. Sets {@link #rootOpen} and {@link #createdByTag}.
     * 
     * @param apiurl
     *            The OSM API URL to use (e.g.
     *            "http://api.openstreetmap.org/api/0.6/").
     * @param username
     * @param password
     * @param oauth
     * @param generator
     *            the name of the editor.
     */
    public Server(final String apiurl, final String username,
            final String password, boolean oauth, String accesstoken,
            String accesstokensecret, final String generator) {
        Log.d("Server", "constructor");
        if (apiurl != null && !apiurl.equals("")) {
            this.serverURL = apiurl;
        } else {
            this.serverURL = "http://api.openstreetmap.org/api/" + version
                    + "/"; // probably not needed anymore
        }
        
        this.username = username;
        
        this.accesstoken = accesstoken;
        this.accesstokensecret = accesstokensecret;

        userDetails = null;
        Log.d("Server", "oAuth token " + this.accesstoken
                + "oAuth secret " + this.accesstokensecret);

        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e("Data4All", "Problem creating parser factory", e);
        }
        xmlParserFactory = factory;

    }

    /**
     * display name and message counts is the only thing that is interesting
     * 
     * @author simon
     *
     */
    public class UserDetails {
        public String display_name = "unknown";
        public int    received     = 0;
        public int    unread       = 0;
        public int    sent         = 0;
    }

    /**
     * Get the details for the user.
     * 
     * @return The display name for the user, or null if it couldn't be
     *         determined.
     */
    public UserDetails getUserDetails() {
        UserDetails result = null;
        if (userDetails == null) {
            // Haven't retrieved the details from OSM - try to
            try {
                HttpURLConnection connection = openConnectionForWriteAccess(
                        getUserDetailsUrl(), "GET");
                try {
                    // connection.getOutputStream().close(); GET doesn't have an
                    // outputstream
                    // checkResponseCode(connection);
                    XmlPullParser parser = xmlParserFactory.newPullParser();
                    parser.setInput(connection.getInputStream(), null);
                    int eventType;
                    result = new UserDetails();
                    boolean messages = false;
                    while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                        String tagName = parser.getName();
                        if (eventType == XmlPullParser.START_TAG
                                && "user".equals(tagName)) {
                            result.display_name = parser.getAttributeValue(
                                    null, "display_name");
                            Log.d("Server", "getUserDetails display name "
                                    + result.display_name);
                        }
                        if (eventType == XmlPullParser.START_TAG
                                && "messages".equals(tagName)) {
                            messages = true;
                        }
                        if (eventType == XmlPullParser.END_TAG
                                && "messages".equals(tagName)) {
                            messages = false;
                        }
                        if (messages) {
                            if (eventType == XmlPullParser.START_TAG
                                    && "received".equals(tagName)) {
                                result.received = Integer.parseInt(parser
                                        .getAttributeValue(null, "count"));
                                Log.d("Server", "getUserDetails received "
                                        + result.received);
                                result.unread = Integer.parseInt(parser
                                        .getAttributeValue(null, "unread"));
                                Log.d("Server", "getUserDetails unread "
                                        + result.unread);
                            }
                            if (eventType == XmlPullParser.START_TAG
                                    && "sent".equals(tagName)) {
                                result.sent = Integer.parseInt(parser
                                        .getAttributeValue(null, "count"));
                                Log.d("Server", "getUserDetails sent "
                                        + result.sent);
                            }
                        }
                    }
                } finally {
                    disconnect(connection);
                }
                // } catch (OsmException e) {
                // Log.e("Data4All", "Problem accessing user details", e);
            } catch (XmlPullParserException e) {
                Log.e("Data4All", "Problem accessing user details", e);
            } catch (MalformedURLException e) {
                Log.e("Data4All", "Problem accessing user details", e);
            } catch (ProtocolException e) {
                Log.e("Data4All", "Problem accessing user details", e);
            } catch (IOException e) {
                Log.e("Data4All", "Problem accessing user details", e);
            } catch (NumberFormatException e) {
                Log.e("Data4All", "Problem accessing user details", e);
            }
            return result;
        }
        return userDetails; // might not make sense
    }

    /**
     * return the username for this server, may be null
     * 
     * @return
     */
    public String getDisplayName() {
        return username;
    }

    /**
     * @param connection
     */
    private static void disconnect(final HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private URL getUserDetailsUrl() throws MalformedURLException {
        return new URL(serverURL + "user/details");
    }

    /**
     * @param elem
     * @param xml
     * @return
     * @throws IOException
     * @throws MalformedURLException
     * @throws ProtocolException
     */
    private HttpURLConnection openConnectionForWriteAccess(final URL url,
            final String requestMethod) throws IOException,
            MalformedURLException, ProtocolException {
        return openConnectionForWriteAccess(url, requestMethod, "text/xml");
    }

    private HttpURLConnection openConnectionForWriteAccess(final URL url,
            final String requestMethod, final String contentType)
            throws IOException, MalformedURLException, ProtocolException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "" + contentType
                + "; charset=utf-8");
        connection.setRequestProperty("User-Agent", Resources.getSystem().getString(R.string.app_name));
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setRequestMethod(requestMethod);

            OAuthHelper oa = new OAuthHelper();
            OAuthConsumer consumer = oa.getConsumer();
            consumer.setTokenWithSecret(accesstoken, accesstokensecret);
            // sign the request
            try {
                consumer.sign(connection);
                // HttpParameters h = consumer.getRequestParameters();

            } catch (OAuthMessageSignerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        connection.setDoOutput(!"GET".equals(requestMethod));
        connection.setDoInput(true);
        return connection;
    }
}
