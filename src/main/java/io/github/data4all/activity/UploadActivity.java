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
import io.github.data4all.service.UploadService;
import io.github.data4all.util.MapUtil;
import io.github.data4all.view.D4AMapView;

import java.util.List;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapController;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
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
    private ProgressBar progress;
    private View indetermineProgress;
    private TextView countText;
    private View uploadButton;
    private View cancleButton;
    private D4AMapView mapView;
    private MapController mapController;
    private MapBoxTileSourceV4 osmMap;

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
        indetermineProgress = findViewById(R.id.upload_indetermine_progress);
        countText = (TextView) findViewById(R.id.upload_count_text);
        uploadButton = findViewById(R.id.upload_upload_button);
        cancleButton = findViewById(R.id.upload_cancle_button);
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

    /**
     * Reads the number of Objects to Upload.
     */
    private void readObjectCount() {
        final DataBaseHandler db = new DataBaseHandler(this);
        final int count = db.getDataElementCount();
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
            this.indetermineProgress.setVisibility(View.VISIBLE);

            this.uploadButton.setEnabled(false);
            this.uploadButton.setVisibility(View.GONE);
            this.cancleButton.setEnabled(true);
            this.cancleButton.setVisibility(View.VISIBLE);
        } else {
            this.progress.setVisibility(View.INVISIBLE);
            this.indetermineProgress.setVisibility(View.INVISIBLE);

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
            final Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION, UploadService.UPLOAD);
            intent.putExtra(UploadService.HANDLER, new MyReceiver());

            startService(intent);
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
            this.showProgress(false);
        }
    }

    /**
     * Deletes all DataElements
     */
    private void deleteAllElements() {
        final DataBaseHandler db = new DataBaseHandler(this);
        db.deleteAllDataElements();
        db.close();
        this.readObjectCount();
    }

    /**
     * Shows all DataElements on the Map.
     */
    private void showAllElementsOnMap() {
        final DataBaseHandler db = new DataBaseHandler(this);
        List<AbstractDataElement> list = db.getAllDataElements();

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
            if (resultCode == UploadService.CURRENT_PROGRESS) {
                progress.setProgress(resultData.getInt(UploadService.MESSAGE));
            } else if (resultCode == UploadService.MAX_PROGRESS) {
                progress.setMax(resultData.getInt(UploadService.MESSAGE));
                progress.setProgress(0);
                progress.setVisibility(View.VISIBLE);
                indetermineProgress.setVisibility(View.INVISIBLE);
            } else if (resultCode == UploadService.ERROR) {
                final String msg = resultData.getString(UploadService.MESSAGE);
                UploadActivity.this.onError(msg);
            } else if (resultCode == UploadService.SUCCESS) {
                UploadActivity.this.onSuccess();
            }
        }
    }
}
