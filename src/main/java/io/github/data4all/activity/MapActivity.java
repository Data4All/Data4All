package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

public abstract class MapActivity extends Activity {
	
	// Logger Tag
	private static final String TAG = "MapActivity";
	
	protected MapView mapView;
	protected ImageView view;
	protected MapController mapController;
	protected MyLocationNewOverlay myLocationOverlay;
	
	// Default Zoom Level
	protected final int DEFAULT_ZOOM_LEVEL = 18;

	// Minimal Zoom Level
	protected final int MINIMAL_ZOOM_LEVEL = 10;

	// Maximal Zoom Level
	protected final int MAXIMAL_ZOOM_LEVEL = 20;

	// Default OpenStreetMap TileSource
	protected final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;

	// BaseURL For SatelliteMap download.
	// TODO Create Own Account
	protected String[] aBaseUrl = {
			"http://a.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
			"http://b.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
			"http://c.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
			"http://d.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/" };

	// Default Satellite Map Tilesource
	protected final OnlineTileSourceBase MAPBOX_SATELLITE_LABELLED = new XYTileSource(
			"MapBoxSatelliteLabelled", ResourceProxy.string.mapquest_aerial,
			MINIMAL_ZOOM_LEVEL, MAXIMAL_ZOOM_LEVEL, 256, ".png", aBaseUrl);
	protected final ITileSource DEFAULT_TILESOURCE = TileSourceFactory.MAPNIK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	 protected void setUpMapView(){
			mapView = (MapView) this.findViewById(R.id.mapview);

			// Set Maptilesource
			Log.i(TAG, "Set Maptilesource to " + OSM_TILESOURCE.name());
			mapView.setTileSource(OSM_TILESOURCE);

			// Add Satellite Map TileSource
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
			mapController.setZoom(DEFAULT_ZOOM_LEVEL);
	 }
	
		protected void addOsmElementToMap(OsmElement element) {
			if (element.getClass().getSimpleName().equals("Node")) {
				Node node = (Node) element;
				Log.d(TAG, "Add Node with Coordinates " + node.toGeoPoint().toString());
				addNodeToMap(node);
			} else if (element.getClass().getSimpleName().equals("Way")) {
				Way way = (Way) element;
				if (way.isClosed()) {
					Log.d(TAG, "Add Area with Coordinates " + way.toString());
					addAreaToMap(way);
				} else {
					Log.d(TAG, "Add Path with Coordinates " + way.toString());
					addPathToMap(way);
				}
			}
		}
	
	protected void addNodeToMap(Node node) {
		Marker poi = new Marker(mapView);
		poi.setPosition(node.toGeoPoint());
		poi.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
		mapView.getOverlays().add(poi);
		mapView.postInvalidate();
	}

	protected void addAreaToMap(Way way) {
		Polygon area = new Polygon(this);
		area.setPoints(way.getGeoPoints());
		area.setFillColor(Color.argb(100, 0, 0, 255));
		area.setStrokeWidth(3.0f);
		area.setStrokeColor(Color.BLUE);
		mapView.getOverlays().add(area);
		mapView.postInvalidate();
	}

	protected void addPathToMap(Way way) {
		Polyline path = new Polyline(this);
		path.setPoints(way.getGeoPoints());
		path.setColor(Color.BLUE);
		path.setWidth(3.0f);
		mapView.getOverlays().add(path);
		mapView.postInvalidate();
	}


}
