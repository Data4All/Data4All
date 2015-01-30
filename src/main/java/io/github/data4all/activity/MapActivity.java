/* 
 * Copyright (c) 2014, 2015 Data4All
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import io.github.data4all.model.map.MapLine;
import io.github.data4all.model.map.MapMarker;
import io.github.data4all.model.map.MapPolygon;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
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
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

    // Last known ZoomLevel
    protected int actualZoomLevel;

    // Last known Center Latitude
    protected double actualCenterLatitude;

    // Last known Center Longitude
    protected double actualCenterLongitude;

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

    // Default OpenStreetMap TileSource
    protected static final ITileSource OSM_TILESRC = TileSourceFactory.MAPNIK;

    // Default Satellite Map Tilesource
    protected static final OnlineTileSourceBase SAT_MAP = new MapBoxTileSource(
            "MapBoxSatelliteLabelled", ResourceProxy.string.mapquest_aerial, 1,
            19, 256, ".png");
    protected static final ITileSource DEF_TILESRC = TileSourceFactory.MAPNIK;

    /**
     * Default constructor
     */
    public MapActivity() {
        super();
    }

    protected void setUpMapView() {
        mapView = (MapView) this.findViewById(R.id.mapview);

        // Set Maptilesource
        Log.i(TAG, "Set Maptilesource to " + OSM_TILESRC.name());
        mapView.setTileSource(OSM_TILESRC);

        // Add Satellite Map TileSource
        MapBoxTileSource.retrieveMapBoxMapId(this);
        TileSourceFactory.addTileSource(SAT_MAP);

        // Set Maptilesource
        Log.i(TAG, "Set Maptilesource to " + DEF_TILESRC.name());
        mapView.setTileSource(DEF_TILESRC);

        // Activate Multi Touch Control
        Log.i(TAG, "Activate Multi Touch Controls");
        mapView.setMultiTouchControls(true);

        // Set Min/Max Zoom Level
        Log.i(TAG, "Set minimal Zoomlevel to " + MINIMAL_ZOOM_LEVEL);
        mapView.setMinZoomLevel(MINIMAL_ZOOM_LEVEL);

        Log.i(TAG, "Set maximal Zoomlevel to " + MAXIMAL_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAXIMAL_ZOOM_LEVEL);

        mapController = (MapController) this.mapView.getController();

        // Set Default Zoom Level
        Log.i(TAG, "Set default Zoomlevel to " + DEFAULT_ZOOM_LEVEL);
        actualZoomLevel = DEFAULT_ZOOM_LEVEL;

        // Set actual Center
        if (this.getMyLocation() != null) {
            Log.i(TAG, "Set actual Center to " + this.getMyLocation());
            actualCenter = this.getMyLocation();
        }

        myLocationOverlay = new MyLocationNewOverlay(this, mapView);
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
    protected void addOsmElementToMap(OsmElement element) {
        if (element != null) {
            // if the Element is a Node
            if (element instanceof Node) {
                final Node node = (Node) element;
                Log.i(TAG, "Add Node with Coordinates "
                        + node.toGeoPoint().toString());
                this.addNodeToMap(node);
                // if the Element is Way
            } else if (element instanceof Way) {
                final Way way = (Way) element;
                // if the Element is an Area
                if (way.isClosed()) {
                    Log.i(TAG, "Add Area with Coordinates " + way.toString());
                    this.addAreaToMap(way);
                    // if the Element is an Path
                } else {
                    Log.i(TAG, "Add Path with Coordinates " + way.toString());
                    this.addPathToMap(way);
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
     * @param way
     *            the area which should be added to the map
     **/
    protected void addAreaToMap(Way way) {
        final Polygon area = new MapPolygon(this);

        Log.i(TAG, "Set Area Points to " + way.toString());
        area.setPoints(way.getGeoPoints());

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
     * @param way
     *            the path which should be added to the map
     **/
    protected void addPathToMap(Way way) {
        final Polyline path = new MapLine(this);

        Log.i(TAG, "Set Path Points to " + way.toString());
        path.setPoints(way.getGeoPoints());

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
        state.putSerializable("actualZoomLevel", actualZoomLevel);

        Log.i(TAG, "Save actual Center Latitude: " + actualCenterLatitude);
        state.putSerializable("actualCenterLatitude", actualCenterLatitude);

        Log.i(TAG, "Save actual Center Longitude: " + actualCenterLongitude);
        state.putSerializable("actualCenterLongitude", actualCenterLongitude);

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "Set actual Center Latitude: "
                + mapView.getMapCenter().getLatitude());
        actualCenterLatitude = mapView.getMapCenter().getLatitude();

        Log.i(TAG, "Set actual Center Longitude: "
                + mapView.getMapCenter().getLongitude());
        actualCenterLongitude = mapView.getMapCenter().getLongitude();

        Log.i(TAG, "Set actual Zoom Level: " + mapView.getZoomLevel());
        actualZoomLevel = mapView.getZoomLevel();
    }

    /**
     * Returns the actual Position.
     *
     * @return the actual position
     **/
    protected IGeoPoint getMyLocation() {
        final LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        final Criteria criteria = new Criteria();
        final String provider = locationManager
                .getBestProvider(criteria, false);
        final Location currentLocation = locationManager
                .getLastKnownLocation(provider);
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
        String mvp = mapView.getTileProvider().getTileSource().name();
        if ("MapBoxSatelliteLabelled".equals(mvp)) {
            Log.i(TAG, "Set Maptilesource to " + mvp);
            mapView.setTileSource(OSM_TILESRC);
            final ImageButton button = (ImageButton) findViewById(R.id.switch_maps);
            button.setImageResource(R.drawable.ic_sat);
            mapView.postInvalidate();
            // switch to Satellite Map
        } else {
            Log.i(TAG, "Set Maptilesource to "
                    + mapView.getTileProvider().getTileSource().name());
            mapView.setTileSource(SAT_MAP);
            ImageButton button = (ImageButton) findViewById(R.id.switch_maps);
            button.setImageResource(R.drawable.ic_map);
            mapView.postInvalidate();
        }
    }

}
