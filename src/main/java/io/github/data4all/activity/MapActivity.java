package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import io.github.data4all.service.GPSservice;
import io.github.data4all.service.GPSservice.LocalBinder;
import io.github.data4all.util.Optimizer;

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

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Super Class for all Map Activitys
 * 
 * @author Oliver Schwartz
 *
 */
public abstract class MapActivity extends BasicActivity {

    GPSservice mService;
    boolean mBound = false;

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

    // Last known ZoomLevel;
    protected int actualZoomLevel;

    // Last known Center Latitude
    protected double actualCenterLatitude;

    // Last known Center Longitude
    protected double actualCenterLongitude;

    // Last known Center Geopoint
    protected IGeoPoint actualCenter;

    // Default Zoom Level
    protected final int DEFAULT_ZOOM_LEVEL = 18;

    // Minimal Zoom Level
    protected final int MINIMAL_ZOOM_LEVEL = 10;

    // Maximal Zoom Level
    protected final int MAXIMAL_ZOOM_LEVEL = 20;

    // Default Stroke width
    protected final float DEFAULT_STROKE_WIDTH = 3.0f;

    // Default Stroke Color
    protected final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);

    // Default OpenStreetMap TileSource
    protected final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;

    // Default Satellite Map Tilesource
    protected final OnlineTileSourceBase MAPBOX_SATELLITE_LABELLED = new MapBoxTileSource(
            "MapBoxSatelliteLabelled", ResourceProxy.string.mapquest_aerial, 1,
            19, 256, ".png");
    protected final ITileSource DEFAULT_TILESOURCE = TileSourceFactory.MAPNIK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setUpMapView() {
        mapView = (MapView) this.findViewById(R.id.mapview);

        // Set Maptilesource
        Log.i(TAG, "Set Maptilesource to " + OSM_TILESOURCE.name());
        mapView.setTileSource(OSM_TILESOURCE);

        // Add Satellite Map TileSource
        MapBoxTileSource.retrieveMapBoxMapId(this);
        TileSourceFactory.addTileSource(MAPBOX_SATELLITE_LABELLED);

        // Set Maptilesource
        Log.i(TAG, "Set Maptilesource to " + DEFAULT_TILESOURCE.name());
        mapView.setTileSource(DEFAULT_TILESOURCE);

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
        if (getMyLocation() != null) {
            Log.i(TAG, "Set actual Center to " + getMyLocation());
            actualCenter = getMyLocation();
        }

        myLocationOverlay = new MyLocationNewOverlay(this, mapView);
    }

    protected void removeOverlayFromMap(Overlay overlay) {
        if (mapView.getOverlays().contains(overlay)) {
            mapView.getOverlays().remove(overlay);
            mapView.postInvalidate();
        }

    }

    /**
     * Adds an OsmElement as an Overlay to the Map
     *
     * @param element
     *            the OsmElement which should be added to the map
     **/
    protected void addOsmElementToMap(OsmElement element) {
        if (element != null) {
            // if the Element is a Node
            if (element instanceof Node) {
                Node node = (Node) element;
                Log.i(TAG, "Add Node with Coordinates "
                        + node.toGeoPoint().toString());
                addNodeToMap(node);
                // if the Element is Way
            } else if (element instanceof Way) {
                Way way = (Way) element;
                // if the Element is an Area
                if (way.isClosed()) {
                    Log.i(TAG, "Add Area with Coordinates " + way.toString());
                    addAreaToMap(way);
                    // if the Element is an Path
                } else {
                    Log.i(TAG, "Add Path with Coordinates " + way.toString());
                    addPathToMap(way);
                }
            }
        }
    }

    /**
     * Adds an Node as an Overlay to the Map
     *
     * @param node
     *            the node which should be added to the map
     **/
    protected void addNodeToMap(Node node) {
        Marker poi = new Marker(mapView) {
            @Override
            public boolean onLongPress(final MotionEvent e,
                    final MapView mapView) {
                final Overlay overlay = this;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            removeOverlayFromMap(overlay);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        mapView.getContext());
                builder.setMessage(getString(R.string.deleteDialog))
                        .setPositiveButton(getString(R.string.yes),
                                dialogClickListener)
                        .setNegativeButton(getString(R.string.no),
                                dialogClickListener).show();

                return true;

            }
        };
        Log.i(TAG, "Set Node Points to " + node.toString());

        // disable InfoWindow
        poi.setInfoWindow(null);
        poi.setPosition(node.toGeoPoint());
        mapView.getOverlays().add(poi);
        mapView.postInvalidate();
    }

    /**
     * Adds an area as an Overlay to the Map
     *
     * @param way
     *            the area which should be added to the map
     **/
    protected void addAreaToMap(Way way) {
        Polygon area = new Polygon(this) {
            @Override
            public boolean onLongPress(final MotionEvent e,
                    final MapView mapView) {
                final Overlay overlay = this;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            removeOverlayFromMap(overlay);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        mapView.getContext());
                builder.setMessage(getString(R.string.deleteDialog))
                        .setPositiveButton(getString(R.string.yes),
                                dialogClickListener)
                        .setNegativeButton(getString(R.string.no),
                                dialogClickListener).show();

                return true;

            }
        };

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
     * Adds an Path as an Overlay to the Map
     *
     * @param way
     *            the path which should be added to the map
     **/
    protected void addPathToMap(Way way) {
        Polyline path = new Polyline(this) {
            @Override
            public boolean onLongPress(final MotionEvent e,
                    final MapView mapView) {
                final Overlay overlay = this;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            removeOverlayFromMap(overlay);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        mapView.getContext());
                builder.setMessage(getString(R.string.deleteDialog))
                        .setPositiveButton(getString(R.string.yes),
                                dialogClickListener)
                        .setNegativeButton(getString(R.string.no),
                                dialogClickListener).show();

                return true;

            }
        };
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
     * Returns the actual Position
     *
     * @return the actual position
     **/
    protected IGeoPoint getMyLocation() {
        Location loc = Optimizer.currentBestLoc();

        if (loc != null) {
            return new GeoPoint(loc.getLatitude(), loc.getLongitude());
        }
        return null;
    }
}
