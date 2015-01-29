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
 * Main Activity that shows the default mapview
 * 
 * @author Oliver Schwartz
 *
 */
public class MapViewActivity extends MapActivity implements OnClickListener {

	//Logger Tag
	private static final String TAG = "MapViewActivity";
	
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
		
		//for setting the actualZoomLevel and Center Position on Orientation Change
		if (savedInstanceState != null) {
			//Zoom Level
			if (savedInstanceState.getSerializable("actualZoomLevel") != null) {
				actualZoomLevel = (Integer) savedInstanceState
						.getSerializable("actualZoomLevel");
			}
			//Center Position
			if (savedInstanceState.getSerializable("actualCenterLongitude") != null
					&& savedInstanceState
							.getSerializable("actualCenterLatitude") != null) {
				actualCenterLatitude = (Double) savedInstanceState
						.getSerializable("actualCenterLatitude");
				actualCenterLongitude = (Double) savedInstanceState
						.getSerializable("actualCenterLongitude");
				actualCenter = new GeoPoint(actualCenterLatitude,
						actualCenterLongitude);
			}
			view.setVisibility(View.GONE);
		} else {
			//fading out the loading screen
			view.animate().alpha(0.0F).setDuration(1000).setStartDelay(1500)
					.withEndAction(new Runnable() {
						public void run() {
							view.setVisibility(View.GONE);
						}
					}).start();
		}
		
		//Set Zoomlevel and Center Position
		Log.i(TAG, "Set Mapcenter to "
				+ actualZoomLevel);
		mapController.setZoom(actualZoomLevel);

		// Set Listener for Buttons
        ImageButton returnToPosition = (ImageButton) findViewById(R.id.return_to_actual_Position);
		returnToPosition.setOnClickListener(this);

		ImageButton satelliteMap = (ImageButton) findViewById(R.id.switch_maps);
		satelliteMap.setOnClickListener(this);

		ImageButton camera = (ImageButton) findViewById(R.id.to_camera);
		camera.setOnClickListener(this);

		ImageButton newPoint = (ImageButton) findViewById(R.id.new_point);
		newPoint.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		//Set center to user Location
		case R.id.return_to_actual_Position:
			if (myLocationOverlay.getMyLocation() != null) {
				Log.i(TAG, "Set Mapcenter to"
						+ myLocationOverlay.getMyLocation().toString());
				mapController.setCenter(myLocationOverlay.getMyLocation());
				mapView.postInvalidate();
			}
			break;
		//Upload new Data	
		case R.id.upload_data:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		//switch between Maps
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
				button.setImageResource(R.drawable.ic_map);
				mapView.postInvalidate();
			}
			break;
		// Make Photo	
		case R.id.to_camera:
			startActivity(new Intent(this, CameraActivity.class));
			break;
		// Add new POI to the Map
		case R.id.new_point:
			GeoPoint myPosition = myLocationOverlay.getMyLocation();
			Intent intent = new Intent(this, MapPreviewActivity.class);
			Node poi = new Node(-1, myPosition.getLatitude(),
					myPosition.getLongitude());
			
			//Set Type Definition for Intent to Node
			Log.i(TAG, "Set intent extra " + TYPE + " to " + NODE_TYPE_DEF);
			intent.putExtra(TYPE, NODE_TYPE_DEF);
			
			//Set OsmElement for Intent to POI 
			Log.i(TAG, "Set Intent Extra " + OSM + " to Node with Coordinates " + poi.toString());
			intent.putExtra(OSM, poi);
			
			//Start MapPreview Activity
			Log.i(TAG, "Start MapPreview Activity");
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//Enable User Position display
		Log.i(TAG, "Enable User Position Display");
		myLocationOverlay.enableMyLocation();
		
		if(actualCenter!=null){
			Log.i(TAG, "Set Mapcenter to " + actualCenter.toString());
			mapController.setCenter(actualCenter);
		}else if(myLocationOverlay.getMyLocation() != null){
			actualCenter = myLocationOverlay.getMyLocation();
			Log.i(TAG, "Set Mapcenter to " + actualCenter.toString());
			mapController.setCenter(actualCenter);			
		}
		// Start the GPS tracking
		Log.i(TAG, "Start GPSService");
		startService(new Intent(this, GPSservice.class));
	}


	@Override
	public void onPause() {
		super.onPause();
		
		//Disable Actual Location Overlay
		Log.i(TAG, "Disable Actual Location Overlay");
		myLocationOverlay.disableMyLocation();

		// Pause the GPS tracking
		Log.i(TAG, "Stop GPSService");
		stopService(new Intent(this, GPSservice.class));
	}
}
