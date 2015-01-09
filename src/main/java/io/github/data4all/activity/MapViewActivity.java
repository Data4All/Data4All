package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.service.GPSservice;


import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.ResourceProxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MapViewActivity extends Activity implements OnClickListener {


	//Logger Tag
	private static final String TAG = "MapViewActivity";
	
	private MapView mapView;
	private MapController mapController;
	private MyLocationNewOverlay myLocationOverlay;
	
	//Default Zoom Level
	private final int DEFAULT_ZOOM_LEVEL = 18;
	
	//Minimal Zoom Level
	private final int MINIMAL_ZOOM_LEVEL = 10;
	
	//Maximal Zoom Level
	private final int MAXIMAL_ZOOM_LEVEL = 20;
	
	//Default OpenStreetMap TileSource
	private final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;
	
	//BaseURL For SatelliteMap download. 
	//TODO Create Own Account
	private String[] aBaseUrl = { "http://a.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
            "http://b.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
            "http://c.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/",
            "http://d.tiles.mapbox.com/v3/dennisl.map-6g3jtnzm/"};
	
	//Default Satellite Map Tilesource
	private final OnlineTileSourceBase MAPBOX_SATELLITE_LABELLED = new XYTileSource(
			"MapBoxSatelliteLabelled",
			ResourceProxy.string.mapquest_aerial, MINIMAL_ZOOM_LEVEL,
			MAXIMAL_ZOOM_LEVEL, 256, ".png", aBaseUrl);
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

		//Add Satellite Map TileSource
		TileSourceFactory.addTileSource(MAPBOX_SATELLITE_LABELLED);

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
			//switch to OSM Map
			if(mapView.getTileProvider().getTileSource().name().equals("MapBoxSatelliteLabelled")){
				Log.i(TAG, "Set Maptilesource to "
						+ mapView.getTileProvider().getTileSource().name());
				mapView.setTileSource(OSM_TILESOURCE);
				ImageButton button = (ImageButton)findViewById(R.id.switch_maps);
				button.setText(R.string.switch_to_satellite);
				mapView.postInvalidate();
			//switch to Satellite Map
			}else{
				Log.i(TAG, "Set Maptilesource to "
						+ mapView.getTileProvider().getTileSource().name());
				mapView.setTileSource(MAPBOX_SATELLITE_LABELLED);
				ImageButton button = (ImageButton)findViewById(R.id.switch_maps);
				button.setText(R.string.switch_to_osm);
				mapView.postInvalidate();		
			}
			break;
		case R.id.to_camera:
			startActivity(new Intent(this, CameraActivity.class));
			break;
		case R.id.new_point:
			break;
		}
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
		stopService(new Intent(this, OrientationListener.class));
	}
}
