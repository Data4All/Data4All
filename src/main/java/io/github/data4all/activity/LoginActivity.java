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
package io.github.data4all.activity;

import io.github.data4all.Constants;
import io.github.data4all.R;
import io.github.data4all.model.data.User;
import io.github.data4all.task.RetrieveUsernameTask;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Activity to start authentication process.
 * 
 * @author sb
 *
 */
public class LoginActivity extends BasicActivity {

    final String TAG = getClass().getSimpleName();
    private SharedPreferences prefs;
    protected String xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG,
                "SharedPreferences:"
                        + PreferenceManager.getDefaultSharedPreferences(
                                getBaseContext()).getAll());

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // By checking oauth tokens will be saved permanently
                // Otherwise they will be destroyed on by calling onDestroy()
                final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);

                // Already got token?
                if (!prefs.contains(OAuth.OAUTH_TOKEN)
                        && !prefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {

                    // Setting flag to remember tokens
                    setTemporaryFlag(!checkBox.isChecked());

                    // Starting oAuth process
                    startActivity(new Intent().setClass(v.getContext(),
                            PrepareRequestTokenActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.alreadyLoggedIn, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // Delete tokens
        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                deleteTokenFromSharedPreferences();
            }
        });

        final Button getUsernameButton =
                (Button) findViewById(R.id.getUsernameButton);
        getUsernameButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (!prefs.contains("USERNAME")) {
                    final OAuthConsumer consumer =
                            new CommonsHttpOAuthConsumer(
                                    Constants.CONSUMER_KEY,
                                    Constants.CONSUMER_SECRET);
                    consumer.setTokenWithSecret(
                            prefs.getString(OAuth.OAUTH_TOKEN, null),
                            prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null));

                    new RetrieveUsernameTask(Constants.API_USERDETAILS,
                            consumer, prefs).execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            prefs.getString("USERNAME", "NULL"),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        // Delete OAuthToken when onDestroy() is called
        if (isTokenTemporary()) {
            deleteTokenFromSharedPreferences();
        }
    }

    protected User returnUser() {
        return new User(prefs.getString("USERNAME", null), prefs.getString(
                OAuth.OAUTH_TOKEN, null), prefs.getString(
                OAuth.OAUTH_TOKEN_SECRET, null));

    }

    private void deleteTokenFromSharedPreferences() {
        if (prefs.contains(OAuth.OAUTH_TOKEN)
                && prefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {
            final Editor ed = prefs.edit();
            ed.remove(OAuth.OAUTH_TOKEN);
            ed.remove(OAuth.OAUTH_TOKEN_SECRET);
            ed.remove("USERNAME");
            ed.remove("IS_TEMPORARY");
            ed.commit();
        }
        Log.d(TAG, "SharedPreferences:" + prefs.getAll());
    }

    private boolean isTokenTemporary() {
        return prefs.getBoolean("IS_TEMPORARY", false);
    }

    private void setTemporaryFlag(boolean b) {
        final Editor ed = prefs.edit();
        ed.putBoolean("IS_TEMPORARY", b);
        ed.commit();
        Log.i(TAG, "SharedPreferences:" + prefs.getAll());
    }
}
