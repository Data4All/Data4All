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
import io.github.data4all.service.UploadService;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 
 * @author tbrose
 *
 */
public class UploadActivity extends BasicActivity {

    public static final String TAG = UploadActivity.class.getSimpleName();
    private ProgressBar progress;
    private TextView countText;
    private View uploadButton;
    private View cancleButton;

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
        countText = (TextView) findViewById(R.id.upload_count_text);
        uploadButton = findViewById(R.id.upload_upload_button);
        cancleButton = findViewById(R.id.upload_cancle_button);
        this.readObjectCount();
    }

    /**
     * Reads the number of Objects to Upload.
     */
    private void readObjectCount() {
        final DataBaseHandler db = new DataBaseHandler(this);
        int count = 0;
        try {
            count = db.getAllDataElements().size();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();

        countText.setText(Integer.toString(count));
        if (count > 0) {
            uploadButton.setEnabled(true);
        } else {
            uploadButton.setEnabled(false);
        }
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
            progress.setIndeterminate(true);
        }
    }

    /**
     * Starts the upload process.
     * 
     * @param v
     *            The view which was clicked
     */
    public void onClickUpload(View v) {
        if (v.getId() == R.id.upload_upload_button) {
            showProgress(true);

            final Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION, UploadService.UPLOAD);
            intent.putExtra(UploadService.HANDLER, new MyReceiver());

            startService(intent);

            v.setVisibility(View.GONE);
            this.cancleButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Stops the upload process.
     * 
     * @param v
     *            The view which was clicked
     */
    public void onClickCancle(View v) {
        if (v.getId() == R.id.upload_cancle_button) {
            showProgress(true);

            final Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION, UploadService.CANCLE);
            intent.putExtra(UploadService.HANDLER, new MyReceiver());

            startService(intent);

            v.setVisibility(View.GONE);
            this.uploadButton.setVisibility(View.VISIBLE);
            showProgress(false);
        }
    }

    /**
     * 
     * @author tbrose
     */
    private class MyReceiver extends ResultReceiver {
        public MyReceiver() {
            super(new Handler());
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.ResultReceiver#onReceiveResult(int,
         * android.os.Bundle)
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.v("UploadActivity$MyHandler", resultData.toString());
            if (resultCode == UploadService.MAX_PROGRESS) {
                progress.setMax(resultData.getInt(UploadService.MESSAGE));
                progress.setIndeterminate(false);
            } else if (resultCode == UploadService.CURRENT_PROGRESS) {
                progress.setProgress(resultData.getInt(UploadService.MESSAGE));
            } else if (resultCode == UploadService.ERROR) {
                showProgress(false);
                final String msg = resultData.getString(UploadService.MESSAGE);
                Toast.makeText(UploadActivity.this, msg, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
