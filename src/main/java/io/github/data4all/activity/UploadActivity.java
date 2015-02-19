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

import java.util.List;

import io.github.data4all.R;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.service.UploadService;
import io.github.data4all.util.Optimizer;

import org.json.JSONException;

import android.content.Intent;
import android.location.Location;
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

            this.uploadButton.setEnabled(false);
            this.uploadButton.setVisibility(View.GONE);
            this.cancleButton.setEnabled(true);
            this.cancleButton.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.INVISIBLE);
            progress.setIndeterminate(true);

            this.cancleButton.setEnabled(false);
            this.cancleButton.setVisibility(View.GONE);
            this.uploadButton.setEnabled(true);
            this.uploadButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 
     */
    public void onSuccess() {
        showProgress(false);
        String msg = "Successfully uploaded";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 
     */
    public void onError(String msg) {
        showProgress(false);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Starts the upload process.
     * 
     * @param v
     *            The view which was clicked
     */
    public void onClickUpload(View v) {
        if (v.getId() == R.id.upload_upload_button) {
            final Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION, UploadService.UPLOAD);
            intent.putExtra(UploadService.HANDLER, new MyReceiver());

            startService(intent);
            showProgress(true);
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
            final Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION, UploadService.CANCLE);
            intent.putExtra(UploadService.HANDLER, new MyReceiver());

            startService(intent);
            showProgress(false);
        }
    }

    /**
     * @throws JSONException
     */
    public void onClickAdd(View v) throws JSONException {
        final DataBaseHandler db = new DataBaseHandler(this);
        db.createDataElement(new Node(-db.getDataElementCount() - 1,
                53.55095672607422, 9.997346878051758));
        db.close();
        readObjectCount();
    }

    /**
     * @throws JSONException
     */
    public void onClickDelete(View v) throws JSONException {
        final DataBaseHandler db = new DataBaseHandler(this);
        final List<AbstractDataElement> allDataElements =
                db.getAllDataElements();
        for (AbstractDataElement elem : allDataElements) {
            db.deleteDataElement(elem);
        }
        db.close();
        readObjectCount();
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
            if (resultCode == UploadService.CURRENT_PROGRESS) {
                progress.setProgress(resultData.getInt(UploadService.MESSAGE));
            } else if (resultCode == UploadService.MAX_PROGRESS) {
                progress.setMax(resultData.getInt(UploadService.MESSAGE));
                progress.setIndeterminate(false);
            } else if (resultCode == UploadService.ERROR) {
                final String msg = resultData.getString(UploadService.MESSAGE);
                UploadActivity.this.onError(msg);
            } else if (resultCode == UploadService.SUCCESS) {
                UploadActivity.this.onSuccess();
            }
        }
    }
}
