package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import io.github.data4all.service.GPSservice;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.ResourceProxy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MapViewActivity extends Activity implements OnClickListener {

	// Logger Tag
	private static final String TAG = "MapViewActivity";
	private MapView mapView;
	private ImageView view;
	private MapController mapController;
	private MyLocationNewOverlay myLocationOverlay;

	// Request Code
	private final int POI_REQUESTCODE = 98;
	// Default Zoom Level
	private final int DEFAULT_ZOOM_LEVEL = 18;

	// Minimal Zoom Level
	private final int MINIMAL_ZOOM_LEVEL = 10;

	// Maximal Zoom Level
	private final int MAXIMAL_ZOOM_LEVEL = 20;

	// Default OpenStreetMap TileSource
	private final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;

	// BaseURL For SatelliteMap download.
	// TODO Create Own Account
	private String[] aBaseUrl = {
			"http://a.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
			"http://b.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
			"http://c.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
			"http://d.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/" };

	// Default Satellite Map Tilesource
	private final OnlineTileSourceBase MAPBOX_SATELLITE_LABELLED = new XYTileSource(
			"MapBoxSatelliteLabelled", ResourceProxy.string.mapquest_aerial,
			MINIMAL_ZOOM_LEVEL, MAXIMAL_ZOOM_LEVEL, 256, ".png", aBaseUrl);
	private final ITileSource DEFAULT_TILESOURCE = TileSourceFactory.MAPNIK;

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
		mapView = (MapView) this.findViewById(R.id.mapview);

		// Set Maptilesource
		Log.i(TAG, "Set Maptilesource to " + OSM_TILESOURCE.name());
		mapView.setTileSource(OSM_TILESOURCE);

		// Add Satellite Map TileSource
		TileSourceFactory.addTileSource(MAPBOX_SATELLITE_LABELLED);
		view = (ImageView) findViewById(R.id.imageView1);
		view.animate().alpha(0.0F).setDuration(1000).setStartDelay(1500)
				.withEndAction(new Runnable() {
					public void run() {
						view.setVisibility(View.GONE);
					}
				}).start();

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

		// Set Overlay for the actual Position
		myLocationOverlay = new MyLocationNewOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		// Set Listener for Buttons

		ImageButton returnToPosition = (ImageButton) findViewById(R.id.return_to_actual_Position);
		returnToPosition.setOnClickListener(this);

		ImageButton uploadData = (ImageButton) findViewById(R.id.upload_data);
		uploadData.setOnClickListener(this);

		ImageButton satelliteMap = (ImageButton) findViewById(R.id.switch_maps);
		satelliteMap.setOnClickListener(this);

		ImageButton camera = (ImageButton) findViewById(R.id.to_camera);
		camera.setOnClickListener(this);

		ImageButton newPoint = (ImageButton) findViewById(R.id.new_point);
		newPoint.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_to_actual_Position:
			if (myLocationOverlay.isMyLocationEnabled()) {
				Log.i(TAG, "Set Mapcenter to"
						+ myLocationOverlay.getMyLocation().toString());
				mapController.setCenter(myLocationOverlay.getMyLocation());
				mapView.postInvalidate();
			}
			break;
		case R.id.upload_data:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.switch_maps:
			// switch to OSM Map
			if (mapView.getTileProvider().getTileSource().name()
					.equals("MapBoxSatelliteLabelled")) {
				Log.i(TAG, "Set Maptilesource to "
						+ mapView.getTileProvider().getTileSource().name());
				mapView.setTileSource(OSM_TILESOURCE);
				ImageButton button = (ImageButton) findViewById(R.id.switch_maps);
				button.setImageResource(R.drawable.ic_sat);
				mapView.postInvalidate();
				// switch to Satellite Map
			} else {
				Log.i(TAG, "Set Maptilesource to "
						+ mapView.getTileProvider().getTileSource().name());
				mapView.setTileSource(MAPBOX_SATELLITE_LABELLED);
				ImageButton button = (ImageButton) findViewById(R.id.switch_maps);
				button.setImageResource(R.drawable.ic_mic);
				mapView.postInvalidate();
			}
			break;
		case R.id.to_camera:
			startActivity(new Intent(this, CameraActivity.class));
			break;
		case R.id.new_point:
			GeoPoint myPosition = myLocationOverlay.getMyLocation();
			Intent tagIntent = new Intent(this, TagActivity.class);
			Node poi = new Node(-1, 1, myPosition.getLatitude(),myPosition.getLongitude());
			Node poi2 = new Node(-2, 1, myPosition.getLatitude()+0.001,myPosition.getLongitude());
			Node poi3 = new Node(-3, 1, myPosition.getLatitude()+0.001,myPosition.getLongitude()+0.001);
			Node poi4 = new Node(-4, 1, myPosition.getLatitude(),myPosition.getLongitude()+0.002); 
			Way way = new Way(-1,1);
			way.addNode((Node)poi);
			way.addNodeAfter(poi, poi2);
			way.addNodeAfter(poi2, poi3);
			way.addNodeAfter(poi3, poi4);
			//way.addNodeAfter(poi4, poi);
			tagIntent.putExtra("TYPE_DEF", "POINT");
			tagIntent.putExtra("OSM_ELEMENT", way);
			startActivityForResult(tagIntent, POI_REQUESTCODE);
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == POI_REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				OsmElement result = data.getParcelableExtra("OSM_ELEMENT");
				addOsmElementToMap(result);
			}
			if (resultCode == RESULT_CANCELED) {
				// TODO Write code if there's no result
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void addOsmElementToMap(OsmElement element) {
		Log.e(TAG, element.getClass().getSimpleName());
		if (element.getClass().getSimpleName().equals("Node")) {
			addNodeToMap((Node) element);
		} else if (element.getClass().getSimpleName().equals("Way")) {
			Way way = (Way) element;
			if (way.isClosed()) {
				addAreaToMap(way);
			} else {
				addPathToMap(way);
			}
		}
	}

	private void addNodeToMap(Node node) {
		Marker poi = new Marker(mapView);
		poi.setPosition(node.toGeoPoint());
		poi.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
		mapView.getOverlays().add(poi);
		mapView.postInvalidate();
	}

	private void addAreaToMap(Way way) {
		Polygon area = new Polygon(this);
		area.setPoints(way.getGeoPoints());
		area.setFillColor(Color.argb(100, 0, 0, 255));
		area.setStrokeWidth(3.0f);
		area.setStrokeColor(Color.BLUE);
		mapView.getOverlays().add(area);
		mapView.postInvalidate();
	}

	private void addPathToMap(Way way) {
		Polyline path = new Polyline(this);
		path.setPoints(way.getGeoPoints());
		path.setColor(Color.BLUE);
		path.setWidth(3.0f);
		mapView.getOverlays().add(path);
		mapView.postInvalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		// enable Overlay for actual Position
		Log.i(TAG, "Enable Actual Location Overlay");
		myLocationOverlay.enableMyLocation();

		// enable Location Listener to update the Position
		Log.i(TAG, "Enable Following Location Overlay");
		myLocationOverlay.enableFollowLocation();
		mapView.postInvalidate();

		// Start the GPS tracking
		startService(new Intent(this, GPSservice.class));
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "Disable Actual Location Overlay");
		myLocationOverlay.disableMyLocation();

		Log.i(TAG, "Disable Following Location Overlay");
		myLocationOverlay.disableFollowLocation();

		// Pause the GPS tracking
		stopService(new Intent(this, GPSservice.class));
	}
}
