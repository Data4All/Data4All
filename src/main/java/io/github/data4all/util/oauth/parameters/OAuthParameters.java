package io.github.data4all.util.oauth.parameters;

/**
 * An interface for providing the parameters to the osm oauth authentication
 * process.
 * 
 * @author tbrose
 *
 */
public interface OAuthParameters {

    /**
     * Gets the consumer key.
     * 
     * @return The consumer key
     */
    public String getConsumerKey();

    /**
     * Gets the consumer secret.
     * 
     * @return The consumer secret
     */
    public String getConsumerSecret();

    /**
     * Gets the user login URL.
     * 
     * @return The user login URL
     */
    public String getUserLoginUrl();

    /**
     * Gets the request token URL.
     * 
     * @return The request token URL
     */
    public String getRequestTokenUrl();

    /**
     * Gets the access token URL.
     * 
     * @return The access token URL
     */
    public String getAccessTokenUrl();

    /**
     * Gets the authorise URL.
     * 
     * @return The authorise URL
     */
    public String getAuthoriseUrl();
}
