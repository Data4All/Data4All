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

/**
 * @author tbrose
 *
 */
public final class DevelopOAuthParameters extends OAuthParameters {
    /**
     * The only instance of this class.
     */
    public static final OAuthParameters INSTANCE = new DevelopOAuthParameters();

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
     * io.github.data4all.util.oauth.parameters.OAuthParameters#getScopeUrl()
     */
    @Override
    public String getScopeUrl() {
        return "http://master.apis.dev.openstreetmap.org/";
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
