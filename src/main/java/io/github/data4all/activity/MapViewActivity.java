package io.github.data4all.activity;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import io.github.data4all.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MapViewActivity extends Activity implements OnClickListener {

	private MapView mapView;
	private MapController mapController;
	private MyLocationNewOverlay myLocationOverlay;
	private final int DEFAULT_ZOOM_LEVEL = 20;
	private final int MINIMAL_ZOOM_LEVEL = 10;
	private final int MAXIMAL_ZOOM_LEVEL = 20;

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
		setContentView(R.layout.osm_map_view);
		mapView = (MapView) this.findViewById(R.id.mapview);

		// Activate default Zoom
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);

		// Set Min/Max Zoom Level
		mapView.setMinZoomLevel(MINIMAL_ZOOM_LEVEL);
		mapView.setMaxZoomLevel(MAXIMAL_ZOOM_LEVEL);

		mapController = (MapController) this.mapView.getController();

		// Set Default Zoom Level
		mapController.setZoom(DEFAULT_ZOOM_LEVEL);

		// Set Overlay for the actual Position
		myLocationOverlay = new MyLocationNewOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		Button returnToPosition = (Button) findViewById(R.id.return_to_actual_Position);
		returnToPosition.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
	    	case R.id.return_to_actual_Position:
	    		if(myLocationOverlay.isMyLocationEnabled()){
	    			mapController.setCenter(myLocationOverlay.getMyLocation());
	    			mapView.postInvalidate();
	    		}
	    		break;
	    	case R.id.upload_data:
	    		break;
	    	case R.id.switch_to_satellite_map:
	    		break;
	    	case R.id.to_camera:
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
		myLocationOverlay.enableMyLocation();

		// enable Location Listener to update the Position
		myLocationOverlay.enableFollowLocation();
		mapView.postInvalidate();
	}

	@Override
	public void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableFollowLocation();
	}
}
