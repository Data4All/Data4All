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
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.network.MapBoxTileSourceV4;
import io.github.data4all.service.UploadElementsService;
import io.github.data4all.service.UploadTracksService;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.upload.ChangesetUtil;
import io.github.data4all.util.upload.GpxTrackUtil;
import io.github.data4all.view.D4AMapView;

import java.util.List;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapController;

import android.R.menu;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity to upload objects to the OSM API.
 * 
 * @author tbrose
 */
public class UploadActivity extends AbstractActivity {

    public static final String TAG = UploadActivity.class.getSimpleName();
    private ProgressBar progressElements;
    private ProgressBar progressTracks;
    private View indetermineProgressElements;
    private View indetermineProgressTracks;
    private TextView countDataElementsText;
    private TextView countGPSTracksText;
    private View uploadButton;
    private View cancleButton;
    private D4AMapView mapView;
    private MapController mapController;
    private MapBoxTileSourceV4 osmMap;
	private EditText uploadComment;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        progressElements = (ProgressBar) findViewById(R.id.upload_progress_elements);
        progressTracks = (ProgressBar) findViewById(R.id.upload_progress_tracks);
        indetermineProgressElements = findViewById(R.id.upload_indetermine_progress_elements);
        indetermineProgressTracks = findViewById(R.id.upload_indetermine_progress_tracks);
        countDataElementsText = (TextView) findViewById(R.id.upload_count_text);
        countGPSTracksText =
                (TextView) findViewById(R.id.upload_gpstracks_count);
        uploadButton = findViewById(R.id.upload_upload_button);
        cancleButton = findViewById(R.id.upload_cancle_button);
        uploadComment = (EditText) findViewById(R.id.uploadComment);
        this.readObjectCount();
        mapView = (D4AMapView) this.findViewById(R.id.mapviewupload);

        MapBoxTileSourceV4.retrieveMapBoxAuthKey(this);

        // Add Satellite Map TileSource
        osmMap =
                new MapBoxTileSourceV4(MapActivity.OSM_MAP_NAME,
                        MapActivity.MINIMAL_ZOOM_LEVEL,
                        MapActivity.MAXIMAL_OSM_ZOOM_LEVEL);
        TileSourceFactory.addTileSource(osmMap);
        mapView.setTileSource(osmMap);
        mapController = (MapController) this.mapView.getController();
        this.showAllElementsOnMap();
    }
    
    /*
    * (non-Javadoc)
    * 
    * @see android.app.Activity#onPrepareOptionsMenu(android.os.Bundle)
    */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.upload_data);
        item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Reads the number of Objects to Upload.
     */
    private void readObjectCount() {
        final DataBaseHandler db = new DataBaseHandler(this);
        final int countDataElements = db.getDataElementCount();
        final int countGPSTracks = db.getGPSTrackCount();
        db.close();

        countDataElementsText.setText(Integer.toString(countDataElements));
        countGPSTracksText.setText(Integer.toString(countGPSTracks));
        if (countDataElements > 0 || countGPSTracks > 0) {
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
            this.indetermineProgressElements.setVisibility(View.VISIBLE);
            this.indetermineProgressTracks.setVisibility(View.VISIBLE);
            this.uploadButton.setEnabled(false);
            this.uploadButton.setVisibility(View.GONE);
            this.cancleButton.setEnabled(true);
            this.cancleButton.setVisibility(View.VISIBLE);
        } else {
            this.progressElements.setVisibility(View.INVISIBLE);
            this.progressTracks.setVisibility(View.INVISIBLE);
            this.indetermineProgressElements.setVisibility(View.INVISIBLE);
            this.indetermineProgressTracks.setVisibility(View.INVISIBLE);
            this.cancleButton.setEnabled(false);
            this.cancleButton.setVisibility(View.GONE);
            this.uploadButton.setEnabled(true);
            this.uploadButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the upload succeeds.
     */
    public void onSuccess() {
        this.showProgress(false);
        this.deleteAllElements();
        final String msg = getString(R.string.upload_success);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Called when the upload fails.
     * 
     * @param msg
     *            The description of the error
     */
    public void onError(String msg) {
        this.showProgress(false);
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
            this.showProgress(true);
            if (ChangesetUtil.needToUpload(this)) {
                final Intent intentUploadElements =
                        new Intent(this, UploadElementsService.class);
                intentUploadElements.putExtra(UploadElementsService.ACTION,
                        UploadElementsService.UPLOAD);
                intentUploadElements.putExtra(UploadElementsService.HANDLER,
                        new MyReceiver());
                Log.d(TAG, "trying to start service to upload elements");
                startService(intentUploadElements);
            }
            if (GpxTrackUtil.needToUpload(this)) {
                final Intent intentUploadTracks =
                        new Intent(this, UploadTracksService.class);
                intentUploadTracks.putExtra(UploadTracksService.ACTION,
                        UploadTracksService.UPLOAD);
                intentUploadTracks.putExtra(UploadTracksService.HANDLER,
                        new MyTracksReceiver());
                Log.d(TAG, "trying to start service to upload tracks");
                startService(intentUploadTracks);
            }
        }
    }

    /**
     * Stops the upload process.
     * 
     * @param v
     *            The view which was clicked
     */
    public void onClickCancel(View v) {
        if (v.getId() == R.id.upload_cancle_button) {
            final Intent intentUploadElements =
                    new Intent(this, UploadElementsService.class);

            intentUploadElements.putExtra(UploadElementsService.ACTION,
                    UploadElementsService.CANCLE);
            intentUploadElements.putExtra(UploadElementsService.HANDLER,
                    new MyReceiver());

            startService(intentUploadElements);

            final Intent intentUploadTracks =
                    new Intent(this, UploadTracksService.class);

            intentUploadTracks.putExtra(UploadTracksService.ACTION,
                    UploadTracksService.CANCLE);
            intentUploadTracks.putExtra(UploadTracksService.HANDLER,
                    new MyReceiver());

            startService(intentUploadTracks);

            this.showProgress(false);
        }
    }

    /**
     * Deletes all DataElements.
     */
    private void deleteAllElements() {
        final DataBaseHandler db = new DataBaseHandler(this);
        db.deleteAllDataElements();
        db.deleteAllGPSTracks();
        db.close();
        this.readObjectCount();
    }

    /**
     * Shows all DataElements on the Map.
     */
    private void showAllElementsOnMap() {
        final DataBaseHandler db = new DataBaseHandler(this);
        final List<AbstractDataElement> list = db.getAllDataElements();

        if (list != null && !list.isEmpty()) {
            mapController.setCenter(MapUtil.getCenterFromOsmElements(list));
            final BoundingBoxE6 boundingBox =
                    MapUtil.getBoundingBoxForOsmElements(list);
            mapView.setBoundingBox(boundingBox);
            mapView.setScrollable(false);
            mapView.getOverlays().clear();
            mapView.addOsmElementsToMap(this, list);
            mapView.postInvalidate();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android
     * .content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        // finishWorkflow to get back to main activity
        finishWorkflow(data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#finish()
     */
    @Override
    public void finish() {
        finishWorkflow(null);
    }

    /**
     * IPC to receive a callback result from the UploadService.
     * 
     * @author tbrose
     */
    private class MyReceiver extends ResultReceiver {
        /**
         * Constructs a new MyReceiver with an new Handler.
         * 
         * @see Handler
         */
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
            if (resultData == null) {
                Log.v("UploadActivity$MyHandler", "data=null");
            } else {
                Log.v("UploadActivity$MyHandler",
                        "data=" + resultData.toString());
            }
            if (resultCode == UploadElementsService.CURRENT_PROGRESS) {
                progressElements.setProgress(resultData
                        .getInt(UploadElementsService.MESSAGE));
            } else if (resultCode == UploadElementsService.MAX_PROGRESS) {
                progressElements.setMax(resultData
                        .getInt(UploadElementsService.MESSAGE));
                progressElements.setProgress(0);
                progressElements.setVisibility(View.VISIBLE);
                indetermineProgressElements.setVisibility(View.INVISIBLE);
            } else if (resultCode == UploadElementsService.ERROR) {
                final String msg =
                        resultData.getString(UploadElementsService.MESSAGE);
                UploadActivity.this.onError(msg);
            } else if (resultCode == UploadElementsService.SUCCESS) {
                UploadActivity.this.onSuccess();
            }
        }
    }

    /**
     * IPC to receive a callback result from the UploadService.
     * 
     * @author tbrose, fkirchge
     */
    private class MyTracksReceiver extends ResultReceiver {
        /**
         * Constructs a new MyReceiver with an new Handler.
         * 
         * @see Handler
         */
        public MyTracksReceiver() {
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
            if (resultData == null) {
                Log.v("UploadActivity$MyHandler", "data=null");
            } else {
                Log.v("UploadActivity$MyHandler",
                        "data=" + resultData.toString());
            }
            if (resultCode == UploadTracksService.CURRENT_PROGRESS) {
                progressTracks.setProgress(resultData
                        .getInt(UploadTracksService.MESSAGE));
            } else if (resultCode == UploadElementsService.MAX_PROGRESS) {
                progressTracks.setMax(resultData
                        .getInt(UploadTracksService.MESSAGE));
                progressTracks.setProgress(0);
                progressTracks.setVisibility(View.VISIBLE);
                indetermineProgressTracks.setVisibility(View.INVISIBLE);
            } else if (resultCode == UploadTracksService.ERROR) {
                final String msg =
                        resultData.getString(UploadTracksService.MESSAGE);
                UploadActivity.this.onError(msg);
            } else if (resultCode == UploadTracksService.SUCCESS) {
                UploadActivity.this.onSuccess();
            }
        }
    }
}
