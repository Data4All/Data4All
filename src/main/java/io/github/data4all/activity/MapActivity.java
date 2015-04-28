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
import io.github.data4all.listener.ButtonRotationListener;
import io.github.data4all.logger.Log;
import io.github.data4all.network.MapBoxTileSourceV4;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Super Class for all Map Activities.
 * 
 * @author Oliver Schwartz
 *
 */
public abstract class MapActivity extends AbstractActivity {

    // Node Type Definition Number
    protected static final int NODE_TYPE_DEF = 1;

    // Type Definition Key
    protected static final String TYPE = "TYPE_DEF";

    // OSMElement Key
    protected static final String OSM = "OSM_ELEMENT";

    // Logger Tag
    private static final String TAG = "MapActivity";

    // OsmDroid Mapview
    protected D4AMapView mapView;

    // ImageView
    protected ImageView view;

    // OsmDroid Mapcontroller
    protected MapController mapController;

    // Overlay for actual Position
    protected MyLocationNewOverlay myLocationOverlay;

    // Last known MapTileSource
    protected ITileSource mapTileSource;

    // Last known MapTileProvider Variable name
    protected static final String M_T_P = "mapTileProvider";

    // Last known ZoomLevel
    protected int actualZoomLevel;

    // Last known ZoomLevel Variable name
    protected static final String ZOOM_LEVEL_NAME = "actualZoomLevel";

    // Last known Center Latitude
    protected double actCentLat;

    // Last known Center Latitude Variable Name
    protected static final String CENT_LAT = "actCentLat";

    // Last known Center Longitude
    protected double actCentLong;

    // Last known Center Longitude Variable Name
    protected static final String CENT_LON = "actCentLon";

    // Last known Center Geopoint
    protected IGeoPoint actualCenter;

    // Default Zoom Level
    protected static final int DEFAULT_ZOOM_LEVEL = 18;

    // Minimal Zoom Level
    protected static final int MINIMAL_ZOOM_LEVEL = 10;

    // Maximal Zoom Level
    protected static final int MAXIMAL_OSM_ZOOM_LEVEL = 22;

    protected static final int MAXIMAL_SATELLITE_ZOOM_LEVEL = 18;

    // Default Stroke width
    protected static final float DEFAULT_STROKE_WIDTH = 3.0f;

    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);

    // Default Satellite Map Tilesource
    protected MapBoxTileSourceV4 satMap;

    protected static final String SAT_MAP_NAME = "mapbox.streets-satellite";

    // Default OpenStreetMap TileSource
    protected MapBoxTileSourceV4 osmMap;

    protected static final String OSM_MAP_NAME = "mapbox.streets";

    protected CacheManager cacheManager;

    protected ButtonRotationListener listener;

    /**
     * Default constructor.
     */
    public MapActivity() {
        super();
    }

    /**
     * Prepares the Mapview. Min/Max Zoomlevel, TileSources, etc.
     * 
     * 
     * @param savedInstanceState
     *            the old Preferences
     */
    protected void setUpMapView(Bundle savedInstanceState) {
        mapView = (D4AMapView) this.findViewById(R.id.mapview);
        // Set ImageView for Loading Screen
        view = (ImageView) findViewById(R.id.imageView1);

        MapBoxTileSourceV4.retrieveMapBoxAuthKey(this);

        // Add Satellite Map TileSource
        satMap =
                new MapBoxTileSourceV4(SAT_MAP_NAME, MINIMAL_ZOOM_LEVEL,
                        MAXIMAL_SATELLITE_ZOOM_LEVEL);
        TileSourceFactory.addTileSource(satMap);

        // Add OSM Map TileSource
        osmMap =
                new MapBoxTileSourceV4(OSM_MAP_NAME, MINIMAL_ZOOM_LEVEL,
                        MAXIMAL_OSM_ZOOM_LEVEL);
        TileSourceFactory.addTileSource(osmMap);
        mapTileSource = osmMap;

        // Activate Multi Touch Control
        Log.i(TAG, "Activate Multi Touch Controls");
        mapView.setMultiTouchControls(true);

        // Set Min/Max Zoom Level
        Log.i(TAG, "Set minimal Zoomlevel to " + MINIMAL_ZOOM_LEVEL);
        mapView.setMinZoomLevel(MINIMAL_ZOOM_LEVEL);

        Log.i(TAG, "Set maximal Zoomlevel to " + MAXIMAL_OSM_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAXIMAL_OSM_ZOOM_LEVEL);

        mapController = (MapController) this.mapView.getController();

        // for setting the actualZoomLevel and Center Position on Orientation
        // Change
        this.loadState(savedInstanceState);

        cacheManager = new CacheManager(mapView);

        // Set MapTileSource
        this.setMapTileSource(mapTileSource);

        // Set Zoomlevel
        this.setZoomLevel(actualZoomLevel);

        // Set Center
        this.setCenter(actualCenter);

        myLocationOverlay = new MyLocationNewOverlay(this, mapView);
    }

    /**
     * Shows Loading Screen.
     **/
    protected void setUpLoadingScreen() {

        // fading out the loading screen
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        }
        view.animate().alpha(0.0F).setDuration(1000).setStartDelay(1500)
                .withEndAction(new Runnable() {
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                }).start();
        // view.setVisibility(View.GONE);
    }

    /**
     * Downloades the Maptiles for the Actual Bounding Box.
     **/
    protected void downloadMapTiles() {
        cacheManager.downloadAreaAsync(this, mapView.getBoundingBox(),
                actualZoomLevel, actualZoomLevel);

    }

    protected boolean getAutoRotate() {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("auto_rotate", true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.i(TAG, "Save actual zoom level: " + actualZoomLevel);
        state.putSerializable(ZOOM_LEVEL_NAME, actualZoomLevel);

        Log.i(TAG, "Save actual Center Latitude: " + actCentLat);
        state.putSerializable(CENT_LAT, actCentLat);

        Log.i(TAG, "Save actual Center Longitude: " + actCentLong);
        state.putSerializable(CENT_LON, actCentLong);

        final String mTS = mapView.getTileProvider().getTileSource().name();
        Log.i(TAG, "Save actual Maptilesource: " + mTS);
        state.putSerializable(M_T_P, mTS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!this.getAutoRotate()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            listener.enable();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "Set actual Center Latitude: "
                + mapView.getMapCenter().getLatitude());
        actCentLat = mapView.getMapCenter().getLatitude();

        Log.i(TAG, "Set actual Center Longitude: "
                + mapView.getMapCenter().getLongitude());
        actCentLong = mapView.getMapCenter().getLongitude();

        Log.i(TAG, "Set actual Zoom Level: " + mapView.getZoomLevel());
        actualZoomLevel = mapView.getZoomLevel();

        final String mTS = mapView.getTileProvider().getTileSource().name();
        Log.i(TAG, "Set actual MapTileSource: " + mTS);
        mapTileSource = mapView.getTileProvider().getTileSource();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        listener.disable();
    }

    /**
     * Returns the actual Position.
     *
     * @return the actual position
     **/
    protected IGeoPoint getMyLocation() {
        final LocationManager locationManager =
                (LocationManager) this
                        .getSystemService(Context.LOCATION_SERVICE);
        final Location currentLocation =
                locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (currentLocation != null) {
            return new GeoPoint(currentLocation.getLatitude(),
                    currentLocation.getLongitude());
        }
        return null;

    }

    /**
     * Set the Zoom Level of the MapView.
     *
     * @param zoom
     *            the Zoomlevel which should be set
     **/
    protected void setZoomLevel(int zoom) {
        // Set Zoomlevel
        Log.i(TAG, "Set Zoomlevel to " + zoom);
        mapController.setZoom(zoom);
    }

    /**
     * Set the Center of the MapView.
     *
     * @param point
     *            the Center which should be set
     **/
    protected void setCenter(IGeoPoint point) {
        if (point == null) {
            final String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                    .show();
        } else {
            // Set ZoomCenter
            Log.i(TAG, "Set Mapcenter to " + point.toString());
            mapController.animateTo(point);
        }

    }

    /**
     * Switch between Satellite Map and OSM Map.
     **/
    protected void switchMaps() {
        // switch to OSM Map
        final String mts = mapView.getTileProvider().getTileSource().name();
        if (SAT_MAP_NAME.equals(mts)) {
            this.setMapTileSource(osmMap);
            mapView.setMaxZoomLevel(MAXIMAL_OSM_ZOOM_LEVEL);
            final ImageButton bt = (ImageButton) findViewById(R.id.switch_maps);
            bt.setImageResource(R.drawable.ic_sat);
            mapView.postInvalidate();
            // switch to Satellite Map
        } else {
            if (mapView.getZoomLevel() > MAXIMAL_SATELLITE_ZOOM_LEVEL) {
                setZoomLevel(MAXIMAL_SATELLITE_ZOOM_LEVEL);
            }
            mapView.setMaxZoomLevel(MAXIMAL_SATELLITE_ZOOM_LEVEL);
            this.setMapTileSource(satMap);
            final ImageButton bt = (ImageButton) findViewById(R.id.switch_maps);
            bt.setImageResource(R.drawable.ic_map);
            mapView.postInvalidate();
        }
    }

    /**
     * Set the Maptilesource for the mapview.
     * 
     * @param src
     *            the Maptilesource which should be set
     **/
    protected void setMapTileSource(ITileSource src) {
        if (src != null) {
            Log.i(TAG, "Set Maptilesource to " + src.name());
            mapTileSource = src;
            mapView.setTileSource(src);
            // this.downloadMapTiles();
            // this.setUpLoadingScreen();
        }

    }

    /**
     * Load the last known Zoomlevel, Center and Maptilesource.
     * 
     * @param savedInstanceState
     *            the last known State
     **/
    private void loadState(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.getSerializable(ZOOM_LEVEL_NAME) != null) {
            actualZoomLevel =
                    (Integer) savedInstanceState
                            .getSerializable(ZOOM_LEVEL_NAME);
        } else {
            actualZoomLevel = DEFAULT_ZOOM_LEVEL;
        }
        if (savedInstanceState != null
                && savedInstanceState.getSerializable(CENT_LON) != null
                && savedInstanceState.getSerializable(CENT_LAT) != null) {
            actCentLat = (Double) savedInstanceState.getSerializable(CENT_LAT);
            actCentLong = (Double) savedInstanceState.getSerializable(CENT_LON);
            actualCenter = new GeoPoint(actCentLat, actCentLong);
        } else {
            actualCenter = this.getMyLocation();
        }
        if (savedInstanceState != null
                && savedInstanceState.getSerializable(M_T_P) != null) {
            final String mTS =
                    (String) savedInstanceState.getSerializable(M_T_P);
            if (mTS.equals(SAT_MAP_NAME)) {
                mapTileSource = satMap;
                final ImageButton bt =
                        (ImageButton) findViewById(R.id.switch_maps);
                bt.setImageResource(R.drawable.ic_map);
            }
        } else {
            mapTileSource = osmMap;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.getTileProvider().clearTileCache();
    }

}
