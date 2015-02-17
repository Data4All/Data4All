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
import android.widget.ProgressBar;

/**
 * 
 * 
 * @author tbrose
 *
 */
public class UploadActivity extends BasicActivity {

    public static final String TAG = UploadActivity.class.getSimpleName();
    ProgressBar progress;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        progress = (ProgressBar) findViewById(R.id.upload_progress);
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
            progress.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Starts the login process if there is a network connection and the
     * username and password fields are not empty.
     * 
     * @param v
     *            The view which was clicked
     */
    public void onClickUpload(View v) {
        if (v.getId() == R.id.upload_upload_button) {
            
        }
    }
}
