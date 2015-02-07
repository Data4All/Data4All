/**
 * 
 */
package io.github.data4all.util.oauth.parameters;

/**
 * @author tbrose
 *
 */
public class DevelopOAuthParameters implements OAuthParameters {
    /**
     * The only instance of this class
     */
    public static OAuthParameters THIS = new DevelopOAuthParameters();

    private DevelopOAuthParameters() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openstreetmap.josm.data.oauth.OAuthParameters#getConsumerKey()
     */
    @Override
    public String getConsumerKey() {
        return "pXTyQeCIqpXN3FrHc3lsDQoZbmaH3wnhMANrRNyH";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getConsumerSecret()
     */
    @Override
    public String getConsumerSecret() {
        return "4vN5TU43n7JlGRjBKxZ18VZZXj8XLy9kKvlAkcPl";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getConsumerSecret()
     */
    @Override
    public String getUserLoginUrl() {
        return "http://master.apis.dev.openstreetmap.org/login";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getRequestTokenUrl()
     */
    @Override
    public String getRequestTokenUrl() {
        return "http://master.apis.dev.openstreetmap.org/oauth/request_token";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.data.oauth.OAuthParameters#getAccessTokenUrl()
     */
    @Override
    public String getAccessTokenUrl() {
        return "http://master.apis.dev.openstreetmap.org/oauth/access_token";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openstreetmap.josm.data.oauth.OAuthParameters#getAuthoriseUrl()
     */
    @Override
    public String getAuthoriseUrl() {
        return "http://master.apis.dev.openstreetmap.org/oauth/authorize";
    }

}
