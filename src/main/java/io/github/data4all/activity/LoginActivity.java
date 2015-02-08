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

import io.github.data4all.R;
import io.github.data4all.model.data.User;
import io.github.data4all.util.NetworkState;
import io.github.data4all.util.oauth.OsmOAuthAuthorizationClient;
import io.github.data4all.util.oauth.exception.OsmLoginFailedException;
import io.github.data4all.util.oauth.exception.OsmOAuthAuthorizationException;
import io.github.data4all.util.oauth.parameters.DevelopOAuthParameters;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * Activity to start authentication process.
 * 
 * @author tbrose
 *
 */
public class LoginActivity extends BasicActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private EditText osmName;
    private EditText osmPass;
    private View progress;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoggedIn()) {
            nextActivity();
        } else {
            setContentView(R.layout.activity_login);
            osmName = (EditText) findViewById(R.id.osm_name);
            osmPass = (EditText) findViewById(R.id.osm_pass);
            progress = findViewById(R.id.progress);
        }
    }

    /**
     * @return If the user is currently logged in.
     */
    private boolean isLoggedIn() {
        // TODO: Ask the database if there is a user
        return false;
    }

    /**
     * Saves the user in the database.
     * 
     * @param user
     *            The user to save
     */
    private void saveUser(User user) {
        // TODO: Save user in database
    }

    /**
     * Starts the next activity. Used when the user is logged in.
     */
    private void nextActivity() {
        // TODO: Open right activity
        this.startActivity(new Intent(this, MapActivity.class));
        this.finish();
    }

    /**
     * Show or hide the progress bar.
     * 
     * @param show
     *            Whether or not the progress bar should be shown
     */
    private void showProgress(boolean show) {
        if (show) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    /**
     * Starts the login process if there is a network connection and the
     * username and password fields are not empty.
     * 
     * @param v
     *            The view which was clicked
     */
    public void onClickStart(View v) {
        final String username = osmName.getText().toString().trim();
        final String password = osmPass.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            osmName.requestFocus();
            osmName.setError("Username is empty");
        } else if (TextUtils.isEmpty(password)) {
            osmPass.requestFocus();
            osmPass.setError("Password is empty");
        } else if (!NetworkState.isNetworkAvailable(this)) {
            new AlertDialog.Builder(this).setTitle("Network unavailable")
                    .setMessage("Please connect your device to the internet")
                    .show();
        } else {
            showProgress(true);
            new Thread(new Authentisator(username, password)).start();
        }
    }

    /**
     * Starts an authorization-attempt via {@link OsmOAuthAuthorizationClient}.
     * 
     * @author tbrose
     *
     */
    private final class Authentisator implements Runnable {
        /**
         * The username to login with.
         */
        private final String username;

        /**
         * The password to login with.
         */
        private final String password;

        /**
         * Constructs a new Authentisator with the given credentials.
         * 
         * @param username
         *            The username to login with.
         * @param password
         *            The password to login with.
         */
        private Authentisator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Make an attempt to get a token for the given credentials.
         */
        @Override
        public void run() {
            try {
                User user =
                        new OsmOAuthAuthorizationClient(
                                DevelopOAuthParameters.THIS).authorise(
                                username, password);
                saveUser(user);
                nextActivity();
            } catch (OsmOAuthAuthorizationException e) {
                if (e instanceof OsmLoginFailedException) {
                    showDialog("Access denied",
                            "Username or Password may be incorrect");
                } else {
                    showDialog("Error", e.getLocalizedMessage());
                }
            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }
        }

        /**
         * Shows a {@link AlertDialog} via the UiThread.
         * 
         * @param title
         *            The title of the dialog
         * @param content
         *            The message of the dialog
         */
        public void showDialog(final String title, final String content) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(LoginActivity.this).setTitle(title)
                            .setMessage(content).show();
                }
            });
        }
    }
}
