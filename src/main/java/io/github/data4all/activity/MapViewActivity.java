/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.service.GPSservice;

import org.osmdroid.util.GeoPoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

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

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     *            is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        setUpMapView();

        // Set Overlay for the actual Position
        Log.i(TAG, "Added User Location Overlay to the map");
        mapView.getOverlays().add(myLocationOverlay);
        // Set ImageView for Loading Screen
        view = (ImageView) findViewById(R.id.imageView1);

        // for setting the actualZoomLevel and Center Position on Orientation
        // Change
        if (savedInstanceState != null) {
            loadState(savedInstanceState);
            view.setVisibility(View.GONE);
        } else {
            // fading out the loading screen
            view.animate().alpha(0.0F).setDuration(1000).setStartDelay(1500)
                    .withEndAction(new Runnable() {
                        public void run() {
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        }

        // Set Zoomlevel
        setZoomLevel(actualZoomLevel);

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
            startActivity(new Intent(this, CameraActivity.class));
            break;
        // Add new POI to the Map
        case R.id.new_point:
            this.createNewPOI();
            break;
        default:
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Enable User Position display
        Log.i(TAG, "Enable User Position Display");
        myLocationOverlay.enableMyLocation();

        if (actualCenter != null) {
            setCenter(actualCenter);
        } else if (myLocationOverlay.getMyLocation() != null) {
            actualCenter = myLocationOverlay.getMyLocation();
            setCenter(actualCenter);
        }
        // Start the GPS tracking
        Log.i(TAG, "Start GPSService");
        startService(new Intent(this, GPSservice.class));
    }

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
        final Intent intent = new Intent(this, MapPreviewActivity.class);
        final Node poi =
                new Node(-1, myPosition.getLatitude(),
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
        startActivity(intent);
    }

    /**
     * Set the Center to the User Position.
     **/
    private void returnToActualPosition() {
        if (myLocationOverlay.getMyLocation() != null) {
            setCenter(myLocationOverlay.getMyLocation());
        }
    }
}
