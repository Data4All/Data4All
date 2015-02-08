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

/**
 * Holds session data for the authentication progress.
 * 
 * @author tbrose
 */
final class SessionId {
    private String id;
    private String token;
    private String userName;

    /**
     * Constructs a new SessionId with the given parameters.
     * 
     * @param id
     *            The session id
     * @param token
     *            The session token
     * @param userName
     *            The user of this session
     */
    private SessionId(String id, String token, String userName) {
        this.id = id;
        this.token = token;
        this.userName = userName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token
     *            the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Constructs a new SessionId with the given parameters.
     * 
     * @param id
     *            The session id
     * @param token
     *            The session token
     * @param username
     *            The user of this session
     * @return a new SessionId
     */
    public static SessionId create(String id, String token, String username) {
        return new SessionId(id, token, username);
    }
}
