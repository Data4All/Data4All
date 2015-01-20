package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.service.GPSservice;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

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
		myLocationOverlay = new MyLocationNewOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		// Set Overlay for the actual Position
		myLocationOverlay = new MyLocationNewOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		view = (ImageView) findViewById(R.id.imageView1);
		if (savedInstanceState != null) {
			if (savedInstanceState.getSerializable("actualZoomLevel") != null) {
				actualZoomLevel = (Integer) savedInstanceState
						.getSerializable("actualZoomLevel");
			}
			if (savedInstanceState.getSerializable("actualCenterLongitude") != null
					&& savedInstanceState
							.getSerializable("actualCenterLatitude") != null) {
				actualCenterLatitude = (Double) savedInstanceState
						.getSerializable("actualCenterLatitude");
				actualCenterLongitude = (Double) savedInstanceState
						.getSerializable("actualCenterLongitude");
				actualCenter = new GeoPoint(actualCenterLatitude,
						actualCenterLongitude);
				Log.i(TAG, "Set Mapcenter to" + actualCenter.toString());

			}
			view.setVisibility(View.GONE);
		} else {
			view.animate().alpha(0.0F).setDuration(1000).setStartDelay(1500)
					.withEndAction(new Runnable() {
						public void run() {
							view.setVisibility(View.GONE);
						}
					}).start();
		}
		
		mapController.setZoom(actualZoomLevel);
		mapController.setCenter(actualCenter);

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
				button.setImageResource(R.drawable.ic_map);
				mapView.postInvalidate();
				// switch to Satellite Map
			} else {
				Log.i(TAG, "Set Maptilesource to "
						+ mapView.getTileProvider().getTileSource().name());
				mapView.setTileSource(MAPBOX_SATELLITE_LABELLED);
				ImageButton button = (ImageButton) findViewById(R.id.switch_maps);
				button.setImageResource(R.drawable.ic_sat);
				mapView.postInvalidate();
			}
			break;
		case R.id.to_camera:
			startActivity(new Intent(this, CameraActivity.class));
			break;
		case R.id.new_point:
			GeoPoint myPosition = myLocationOverlay.getMyLocation();
			Intent tagIntent = new Intent(this, TagActivity.class);
			Node poi = new Node(-1, 1, myPosition.getLatitude(),
					myPosition.getLongitude());
			tagIntent.putExtra("TYPE_DEF", "POINT");
			tagIntent.putExtra("OSM_ELEMENT", poi);
			startActivity(tagIntent);
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();

		// enable Location Listener to update the Position
		// Log.i(TAG, "Enable Following Location Overlay");
		// myLocationOverlay.enableFollowLocation();
		mapController.setZoom(actualZoomLevel);
		if (actualCenter != null) {
			Log.i(TAG, "Set Mapcenter to"
					+ actualCenter.toString());
			mapController.setCenter(actualCenter);
		} 

		// if (myLocationOverlay.getMyLocation() == null) {
		// Log.e(TAG, "LocationAAAAAAAAAAAAAAAAH!!!!!!");
		// actualCenter = myLocationOverlay.getMyLocation();

		// }
		// Start the GPS tracking
		startService(new Intent(this, GPSservice.class));
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putSerializable("actualZoomLevel", actualZoomLevel);
		state.putSerializable("actualCenterLatitude", actualCenterLatitude);
		state.putSerializable("actualCenterLongitude", actualCenterLongitude);

	}


	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "Disable Actual Location Overlay");
		myLocationOverlay.disableMyLocation();

		Log.i(TAG, "Disable Following Location Overlay");
		myLocationOverlay.disableFollowLocation();

		actualCenterLatitude = mapView.getMapCenter().getLatitude();
		actualCenterLongitude = mapView.getMapCenter().getLongitude();
		actualZoomLevel = mapView.getZoomLevel();

		// Pause the GPS tracking
		stopService(new Intent(this, GPSservice.class));
	}
}
