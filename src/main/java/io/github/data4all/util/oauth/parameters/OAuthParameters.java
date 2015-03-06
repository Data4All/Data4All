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
package io.github.data4all.util.oauth.parameters;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

/**
 * An interface for providing the parameters to the osm oauth authentication
 * process.
 * 
 * @author tbrose (inspired by JOSM)
 */
public abstract class OAuthParameters {
    /**
     * The OAuth parameters which are currently in use.
     */
    public static final OAuthParameters CURRENT = StableOAuthParameters.INSTANCE;

    /**
     * Gets the consumer key.
     * 
     * @return The consumer key
     */
    public abstract String getConsumerKey();

    /**
     * Gets the consumer secret.
     * 
     * @return The consumer secret
     */
    public abstract String getConsumerSecret();

    /**
     * Gets the scope URL for all URLs that needs to be build to address this
     * API endpoint.
     * 
     * @return The the scope of the endpoint
     */
    public abstract String getScopeUrl();

    /**
     * Gets the user login URL.
     * 
     * @return The user login URL
     */
    public abstract String getUserLoginUrl();

    /**
     * Gets the request token URL.
     * 
     * @return The request token URL
     */
    public abstract String getRequestTokenUrl();

    /**
     * Gets the access token URL.
     * 
     * @return The access token URL
     */
    public abstract String getAccessTokenUrl();

    /**
     * Gets the authorise URL.
     * 
     * @return The authorise URL
     */
    public abstract String getAuthoriseUrl();

    /**
     * Creates a OAuthConsumer with this parameters.
     * 
     * @return The created consumer
     */
    public OAuthConsumer createConsumer() {
        return new DefaultOAuthConsumer(this.getConsumerKey(),
                this.getConsumerSecret());
    }

    /**
     * Creates a OAuthProvider with this parameters.
     * 
     * @return The created provider
     */
    public OAuthProvider createProvider() {
        return new DefaultOAuthProvider(this.getRequestTokenUrl(),
                this.getAccessTokenUrl(), this.getAuthoriseUrl());
    }
}
