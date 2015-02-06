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
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.map.MapLine;
import io.github.data4all.model.map.MapMarker;
import io.github.data4all.model.map.MapPolygon;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.cachemanager.CacheManager;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Super Class for all Map Activities.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapActivity extends BasicActivity {

    // Node Type Definition Number
    protected static final int NODE_TYPE_DEF = 1;

    // Type Definition Key
    protected static final String TYPE = "TYPE_DEF";

    // OSMElement Key
    protected static final String OSM = "OSM_ELEMENT";

    // Logger Tag
    private static final String TAG = "MapActivity";

    // OsmDroid Mapview
    protected MapView mapView;

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
    protected static final int MAXIMAL_ZOOM_LEVEL = 20;

    // Default Stroke width
    protected static final float DEFAULT_STROKE_WIDTH = 3.0f;

    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);

    // Default Satellite Map Tilesource
    protected OnlineTileSourceBase satMap;

    protected static final String SAT_MAP_NAME = "MapBoxSatelliteLabelled";

    // Default OpenStreetMap TileSource
    protected static final ITileSource OSM_TILESRC = TileSourceFactory.MAPQUESTOSM;

    CacheManager cacheManager;

    /**
     * Default constructor.
     */
    public MapActivity() {
        super();
    }

    protected void setUpMapView(Bundle savedInstanceState) {
        mapView = (MapView) this.findViewById(R.id.mapview);

        // Add Satellite Map TileSource
        MapBoxTileSource.retrieveMapBoxMapId(this);
        satMap = new MapBoxTileSource(SAT_MAP_NAME,
                ResourceProxy.string.mapquest_osm , 1, 19, 256, ".png");
        TileSourceFactory.addTileSource(satMap);

        // Activate Multi Touch Control
        Log.i(TAG, "Activate Multi Touch Controls");
        mapView.setMultiTouchControls(true);

        // Set Min/Max Zoom Level
        Log.i(TAG, "Set minimal Zoomlevel to " + MINIMAL_ZOOM_LEVEL);
        mapView.setMinZoomLevel(MINIMAL_ZOOM_LEVEL);

        Log.i(TAG, "Set maximal Zoomlevel to " + MAXIMAL_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAXIMAL_ZOOM_LEVEL);

        mapController = (MapController) this.mapView.getController();

        // for setting the actualZoomLevel and Center Position on Orientation
        // Change
        loadState(savedInstanceState);

        
        cacheManager = new CacheManager(mapView);

        // Set MapTileSource
        setMapTileSource(mapTileSource);

        // Set Zoomlevel
        setZoomLevel(actualZoomLevel);

        // Set Zoomlevel
        setCenter(actualCenter);

        myLocationOverlay = new MyLocationNewOverlay(this, mapView);
    }

    
    protected void setUpLoadingScreen() {
        // Set ImageView for Loading Screen
        view = (ImageView) findViewById(R.id.imageView1);

        // fading out the loading screen
        // view.animate().alpha(0.0F).setDuration(1000).setStartDelay(1500)
        // .withEndAction(new Runnable() {
        // public void run() {
        // view.setVisibility(View.GONE);
        // }
        // }).start();
        view.setVisibility(View.GONE);
    }

    /**
     * Downloades the Maptiles for the Actual Bounding Box.
     **/
    protected void downloadMapTiles(){
        cacheManager.downloadAreaAsync(this, mapView.getBoundingBox(),
                actualZoomLevel, actualZoomLevel+4);

    }
    
    /**
     * Removes an Overlay from the Map.
     *
     * @param overlay
     *            the Overlay which should be removed from the map
     **/
    public void removeOverlayFromMap(Overlay overlay) {
        if (mapView.getOverlays().contains(overlay)) {
            mapView.getOverlays().remove(overlay);
            mapView.postInvalidate();
        }

    }

    /**
     * Adds an OsmElement as an Overlay to the Map.
     *
     * @param element
     *            the OsmElement which should be added to the map
     **/
    protected void addOsmElementToMap(AbstractDataElement element) {
        if (element != null) {
            // if the Element is a Node
            if (element instanceof Node) {
                final Node node = (Node) element;
                Log.i(TAG, "Add Node with Coordinates "
                        + node.toGeoPoint().toString());
                this.addNodeToMap(node);
                // if the Element is Way
            } else if (element instanceof PolyElement) {
                final PolyElement polyElement = (PolyElement) element;

                // if the Element is an Path
                if (polyElement.getType() == PolyElementType.WAY) {
                    Log.i(TAG,
                            "Add Path with Coordinates "
                                    + polyElement.toString());
                    this.addPathToMap(polyElement);
                    // if the Element is an Area
                } else {
                    Log.i(TAG,
                            "Add Area with Coordinates "
                                    + polyElement.toString());
                    this.addAreaToMap(polyElement);
                }
            }
        }
    }

    /**
     * Adds an Node as an Overlay to the Map.
     *
     * @param node
     *            the node which should be added to the map
     **/
    protected void addNodeToMap(Node node) {
        final Marker poi = new MapMarker(this, mapView);
        Log.i(TAG, "Set Node Points to " + node.toString());
        // disable InfoWindow
        poi.setInfoWindow(null);
        poi.setPosition(node.toGeoPoint());
        mapView.getOverlays().add(poi);
        mapView.postInvalidate();
    }

    /**
     * Adds an area as an Overlay to the Map.
     *
     * @param polyElement
     *            the area which should be added to the map
     **/
    protected void addAreaToMap(PolyElement polyElement) {
        Polygon area = new MapPolygon(this);

        Log.i(TAG, "Set Area Points to " + polyElement.toString());
        area.setPoints(polyElement.getGeoPoints());

        Log.i(TAG, "Set Area Fill Color to " + DEFAULT_FILL_COLOR);
        area.setFillColor(DEFAULT_FILL_COLOR);

        Log.i(TAG, "Set Stroke Width to " + DEFAULT_STROKE_WIDTH);
        area.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        Log.i(TAG, "Set Stroke Color to " + DEFAULT_STROKE_COLOR);
        area.setStrokeColor(DEFAULT_STROKE_COLOR);

        mapView.getOverlays().add(area);
        mapView.postInvalidate();
    }

    /**
     * Adds an Path as an Overlay to the Map.
     *
     * @param polyElement
     *            the path which should be added to the map
     **/
    protected void addPathToMap(PolyElement polyElement) {
        Polyline path = new MapLine(this);

        Log.i(TAG, "Set Path Points to " + polyElement.toString());
        path.setPoints(polyElement.getGeoPoints());

        Log.i(TAG, "Set Path Color to " + DEFAULT_STROKE_COLOR);
        path.setColor(DEFAULT_STROKE_COLOR);

        Log.i(TAG, "Set Path Width to " + DEFAULT_STROKE_WIDTH);
        path.setWidth(DEFAULT_STROKE_WIDTH);
        mapView.getOverlays().add(path);
        mapView.postInvalidate();
    }

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
        final Criteria criteria = new Criteria();
        final String provider =
                locationManager.getBestProvider(criteria, false);
        final Location currentLocation =
                locationManager.getLastKnownLocation(provider);
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
        // Set ZoomCenter
        Log.i(TAG, "Set Mapcenter to " + point.toString());
        mapController.setCenter(point);
    }

    /**
     * Switch between Satellite Map and OSM Map.
     **/
    protected void switchMaps() {
        // switch to OSM Map
        final String mts = mapView.getTileProvider().getTileSource().name();
        if ("MapBoxSatelliteLabelled".equals(mts)) {
            setMapTileSource(OSM_TILESRC);
            final ImageButton bt = (ImageButton) findViewById(R.id.switch_maps);
            bt.setImageResource(R.drawable.ic_sat);
            mapView.postInvalidate();
            // switch to Satellite Map
        } else {
            setMapTileSource(satMap);
            final ImageButton bt = (ImageButton) findViewById(R.id.switch_maps);
            bt.setImageResource(R.drawable.ic_map);
            mapView.postInvalidate();
        }
    }

    /**
     * Set the Maptilesource for the mapview.
     * 
     * @param src
     *            the Maptilesource wich should be set
     **/
    protected void setMapTileSource(ITileSource src) {
        if (src != null) {
            Log.i(TAG, "Set Maptilesource to " + src.name());
            mapTileSource = src;
            mapView.setTileSource(src);
            downloadMapTiles();
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
            actualZoomLevel = (Integer) savedInstanceState
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
        } else if (this.getMyLocation() != null) {
            actualCenter = this.getMyLocation();
        }
        if (savedInstanceState != null
                && savedInstanceState.getSerializable(M_T_P) != null) {
            String mTS = (String) savedInstanceState.getSerializable(M_T_P);
            if (mTS.equals(SAT_MAP_NAME)) {
                mapTileSource = satMap;
                final ImageButton bt = (ImageButton) findViewById(R.id.switch_maps);
                bt.setImageResource(R.drawable.ic_map);
            }
        } else {
            mapTileSource = OSM_TILESRC;
        }
    }

}
