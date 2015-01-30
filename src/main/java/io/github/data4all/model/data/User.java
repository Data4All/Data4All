/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.model.data;

/**
 * Simple model for the osm user.
 * 
 * @author fkirchge
 *
 */
public class User {

    /**
     * User details for the login procedure
     */
    private String username;
    private String oauthToken;
    private boolean isLoggedIn;
    private String oauthTokenSecret;

    /**
     * Default constructor
     * 
     * @param username
     * @param loginToken
     */
    public User(String oauthToken, String oauthTokenSecret) {
        this.oauthToken = oauthToken;
        this.setOauthTokenSecret(oauthTokenSecret);
    }

 
    public User(String username, String oauthToken, String oauthTokenSecret) {
        this.username = username;
        this.oauthToken = oauthToken;
        this.setOauthTokenSecret(oauthTokenSecret);
    }

    /**
     * Default constructor
     * 
     * @param username
     * @param loginToken
     * @param status
     */
    public User(String username, String loginToken, boolean status) {
        this.username = username;
        this.oauthToken = loginToken;
        this.isLoggedIn = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginToken() {
        return oauthToken;
    }

    public void setLoginToken(String loginToken) {
        this.oauthToken = loginToken;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
     
    public String getOAuthToken() {
        return oauthToken;
    }

    public void setOAuthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthTokenSecret() {
        return oauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        this.oauthTokenSecret = oauthTokenSecret;
    }

}