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
import io.github.data4all.util.oauth.exception.OsmOAuthAuthorizationException;
import io.github.data4all.util.oauth.parameters.DevelopOAuthParameters;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * Activity to start authentication process
 * 
 * @author sbollen, tbrose
 *
 */
public class LoginActivity extends BasicActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    
    private EditText osmName;
    private EditText osmPass;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoggegIn()) {
            startActivity(nextActivity());
            this.finish();
        } else {
            setContentView(R.layout.activity_login);
            osmName = (EditText) findViewById(R.id.osm_name);
            osmPass = (EditText) findViewById(R.id.osm_pass);
        }
    }

    /**
     * @return If the user is currently logged in.
     */
    private boolean isLoggegIn() {
        return false;
    }

    /**
     * The intent for the next activity to open if the user is logged in.
     * 
     * @return The intent for the next activity
     */
    private Intent nextActivity() {
        return new Intent(this, MapActivity.class);
    }

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
            new Thread(new Authentisator(password, username)).start();
        }
    }

    /**
     * @author tbrose
     *
     */
    private final class Authentisator implements Runnable {
        /**
         * 
         */
        private final String password;
        /**
         * 
         */
        private final String username;

        /**
         * @param password
         * @param username
         */
        private Authentisator(String password, String username) {
            this.password = password;
            this.username = username;
        }

        @Override
        public void run() {
            try {
                User user =
                        new OsmOAuthAuthorizationClient(
                                DevelopOAuthParameters.THIS).authorise(
                                username, password);
                showDialog("Success", "Token: " + user.getOAuthToken()
                        + "\nSecret: " + user.getOauthTokenSecret());
            } catch (OsmOAuthAuthorizationException e) {
                showDialog("Error", e.getLocalizedMessage());
            }
        }

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
