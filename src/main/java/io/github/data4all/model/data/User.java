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
package io.github.data4all.model.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple model for the osm user.
 * 
 * @author fkirchge
 *
 */
public class User implements Parcelable {

    /**
     * User details for the login procedure.
     */
    private String username;
    private String oauthToken;
    private String oauthTokenSecret;

    /**
     * Default constructor.
     * 
     * @param oauthToken
     * @param oauthTokenSecret
     */
    public User(String oauthToken, String oauthTokenSecret) {
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
    }

    /**
     * Default constructor.
     * 
     * @param username
     * @param oauthToken
     * @param oauthTokenSecret
     */
    public User(String username, String oauthToken, String oauthTokenSecret) {
        this.username = username;
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
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

    /**
     * Constructor to create a User from a parcel.
     * 
     * @param in
     */
    private User(Parcel in) {
        this.username = in.readString();
        this.oauthToken = in.readString();
        this.oauthTokenSecret = in.readString();
    }

    /**
     * Methods to write and restore a Parcel.
     */
    public static final Parcelable.Creator<User> CREATOR =
            new Parcelable.Creator<User>() {

                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                public User[] newArray(int size) {
                    return new User[size];
                }
            };

    /**
     * Writes the nodes to the given parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(oauthToken);
        dest.writeString(oauthTokenSecret);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}