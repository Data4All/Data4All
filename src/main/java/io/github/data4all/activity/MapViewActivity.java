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
import io.github.data4all.model.data.Node;
import io.github.data4all.service.GPSservice;
import io.github.data4all.util.Optimizer;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Main Activity that shows the default MapView.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapViewActivity extends MapActivity implements OnClickListener {

    // Logger Tag
    private static final String TAG = "MapViewActivity";

    /**
     * Default constructor.
     */
    public MapViewActivity() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        setUpMapView(savedInstanceState);
        setUpLoadingScreen();

        // Set Overlay for the actual Position
        Log.i(TAG, "Added User Location Overlay to the map");
        mapView.getOverlays().add(myLocationOverlay);

        // Set Listener for Buttons
        int id = R.id.return_to_actual_Position;
        final ImageButton returnToPosition = (ImageButton) findViewById(id);
        returnToPosition.setOnClickListener(this);

        id = R.id.switch_maps;
        final ImageButton satelliteMap = (ImageButton) findViewById(id);
        satelliteMap.setOnClickListener(this);

        id = R.id.to_camera;
        final ImageButton camera = (ImageButton) findViewById(id);
        camera.setOnClickListener(this);

        id = R.id.new_point;
        final ImageButton newPoint = (ImageButton) findViewById(id);
        newPoint.setOnClickListener(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        // Set center to user Location
        case R.id.return_to_actual_Position:
            this.returnToActualPosition();
            break;
        // Upload new Data
        case R.id.upload_data:
            startActivity(new Intent(this, LoginActivity.class));
            break;
        // switch between Maps
        case R.id.switch_maps:
            switchMaps();
            break;
        // Make Photo
        case R.id.to_camera:
            startCamera();
            break;
        // Add new POI to the Map
        case R.id.new_point:
            this.createNewPOI();
            break;
        default:
            break;
        }
    }

    private void startCamera() {
        if (Optimizer.currentLocation() == null) {
            final String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text,
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent camera = new Intent(this, CameraActivity.class);
            startActivity(camera);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        // clear all Overlays
        mapView.getOverlays().clear();
        //add osmElements from the database to the map
        DataBaseHandler db = new DataBaseHandler(this);
        List<AbstractDataElement> list = db.getAllDataElements();
        mapView.addOsmElementsToMap(this, list);

        db.close();

        // Set Overlay for the actual Position
        Log.i(TAG, "Added User Location Overlay to the map");
        mapView.getOverlays().add(myLocationOverlay);


        // Enable User Position display
        Log.i(TAG, "Enable User Position Display");
        myLocationOverlay.enableMyLocation();

        // Start the GPS tracking
        Log.i(TAG, "Start GPSService");
        startService(new Intent(this, GPSservice.class));
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.activity.MapActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();

        // Disable Actual Location Overlay
        Log.i(TAG, "Disable Actual Location Overlay");
        myLocationOverlay.disableMyLocation();
        // Pause the GPS tracking
        Log.i(TAG, "Stop GPSService");
        stopService(new Intent(this, GPSservice.class));
    }

    /**
     * Creates new POI on the actual Position.
     **/
    private void createNewPOI() {
        final GeoPoint myPosition = myLocationOverlay.getMyLocation();
        if (myPosition == null) {
            final String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                    .show();
        } else {
            final Intent intent = new Intent(this, MapPreviewActivity.class);
            final Node poi = new Node(-1, myPosition.getLatitude(),
                    myPosition.getLongitude());

            // Set Type Definition for Intent to Node
            Log.i(TAG, "Set intent extra " + TYPE + " to " + NODE_TYPE_DEF);
            intent.putExtra(TYPE, NODE_TYPE_DEF);

            // Set OsmElement for Intent to POI
            Log.i(TAG, "Set Intent Extra " + OSM + " to Node with Coordinates "
                    + poi.toString());
            intent.putExtra(OSM, poi);

            // Start MapPreview Activity
            Log.i(TAG, "Start MapPreview Activity");
            startActivityForResult(intent, 0);
        }
    }

    /* (non-Javadoc)
     * @see io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android.content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        final DataBaseHandler db = new DataBaseHandler(this);
        final List<AbstractDataElement> list = db.getAllDataElements();
        mapView.addOsmElementsToMap(this, list);
        db.close();
        mapView.postInvalidate();
    }

    /**
     * Set the Center to the User Position.
     **/
    private void returnToActualPosition() {
        if (myLocationOverlay.getMyLocation() != null) {
            setCenter(myLocationOverlay.getMyLocation());
        } else {
            String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
