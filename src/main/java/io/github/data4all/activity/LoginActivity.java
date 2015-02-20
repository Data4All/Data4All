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
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.User;
import io.github.data4all.util.NetworkState;
import io.github.data4all.util.oauth.OsmOAuthAuthorizationClient;
import io.github.data4all.util.oauth.exception.OsmLoginFailedException;
import io.github.data4all.util.oauth.exception.OsmOAuthAuthorizationException;
import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.util.List;

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
public class LoginActivity extends AbstractActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private EditText osmName;
    private EditText osmPass;
    private View progress;

    /**
     * Sole Constructor.
     */
    public LoginActivity() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.isLoggedIn()) {
            this.nextActivity();
        } else {
            this.setContentView(R.layout.activity_login);
            this.osmName = (EditText) findViewById(R.id.osm_name);
            this.osmPass = (EditText) findViewById(R.id.osm_pass);
            this.progress = findViewById(R.id.progress);
        }
    }

    /**
     * @return If the user is currently logged in.
     */
    private boolean isLoggedIn() {
        final DataBaseHandler database = new DataBaseHandler(this);
        final List<User> users = database.getAllUser();
        Log.i(TAG, "" + users.size());
        database.close();
        return !users.isEmpty();
    }

    /**
     * Saves the user in the database.
     * 
     * @param user
     *            The user to save
     */
    private void saveUser(User user) {
        final DataBaseHandler database = new DataBaseHandler(this);
        database.createUser(user);
        database.close();
    }

    /**
     * Starts the next activity. Used when the user is logged in.
     */
    private void nextActivity() {
        // TODO: Open right activity
        this.startActivity(new Intent(this, MapViewActivity.class));
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
        if (v.getId() == R.id.osm_login) {
            osmName.setError(null);
            osmPass.setError(null);
            final String username = osmName.getText().toString().trim();
            final String password = osmPass.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                osmName.requestFocus();
                osmName.setError(getString(R.string.login_username_empty));
            } else if (TextUtils.isEmpty(password)) {
                osmPass.requestFocus();
                osmPass.setError(getString(R.string.login_password_empty));
            } else if (!NetworkState.isNetworkAvailable(this)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.no_network_title)
                        .setMessage(R.string.no_network_message)
                        .show();
            } else {
                this.showProgress(true);
                new Thread(new Authentisator(username, password)).start();
            }
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
                final User user = new OsmOAuthAuthorizationClient(
                        OAuthParameters.CURRENT).authorise(username, password);
                LoginActivity.this.saveUser(user);
                LoginActivity.this.nextActivity();
            } catch (OsmLoginFailedException e) {
                Log.e(TAG, "Login to osm failed:", e);
                this.showDialog(getString(R.string.login_access_dialog_title),
                        getString(R.string.login_access_dialog_mes));
            } catch (OsmOAuthAuthorizationException e) {
                Log.e(TAG, "Osm OAuth failed:", e);
                this.showDialog("Error", e.getLocalizedMessage());
            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoginActivity.this.showProgress(false);
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
