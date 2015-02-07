/**
 * 
 */
package io.github.data4all.util.oauth.parameters;


/**
 * @author tbrose
 *
 */
public class StableOAuthParameters implements OAuthParameters {
    /**
     * The only instance of this class
     */
    public static OAuthParameters THIS = new StableOAuthParameters();

    private StableOAuthParameters() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openstreetmap.josm.data.oauth.OAuthParameters#getConsumerKey()
     */
    @Override
    public String getConsumerKey() {
        return "5bSHgylPtS0mf3q3hDBG2BOz6PoHWolr2W8PMDCT";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getConsumerSecret()
     */
    @Override
    public String getConsumerSecret() {
        return "9ej0TvsLOQeJPb4xVKFoeoFSLA1iF9jHqll2Jovb";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openstreetmap.josm.data.oauth.OAuthParameters#getUserLoginUrl()
     */
    @Override
    public String getUserLoginUrl() {
        return "https://www.openstreetmap.org/login";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getRequestTokenUrl()
     */
    @Override
    public String getRequestTokenUrl() {
        // "http://api06.dev.openstreetmap.org/oauth/request_token"
        return "http://www.openstreetmap.org/oauth/request_token";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getAccessTokenUrl()
     */
    @Override
    public String getAccessTokenUrl() {
        // "http://api06.dev.openstreetmap.org/oauth/access_token"
        return "http://www.openstreetmap.org/oauth/access_token";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openstreetmap.josm.data.oauth.OAuthParameters#getAuthoriseUrl()
     */
    @Override
    public String getAuthoriseUrl() {
        // "http://api06.dev.openstreetmap.org/oauth/authorize"
        return "http://www.openstreetmap.org/oauth/authorize";
    }
}
