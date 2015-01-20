package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import org.osmdroid.util.GeoPoint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MapPreviewActivity extends MapActivity implements OnClickListener {
	
	// Logger Tag
	private static final String TAG = "MapPreviewActivity";
	
	
	//  the OsmElement which should be added
	private OsmElement element;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_preview);
		setUpMapView();
		element = getIntent().getParcelableExtra("OSM_ELEMENT");
		addOsmElementToMap(element);
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
		ImageButton returnToPosition = (ImageButton) findViewById(R.id.return_to_actual_Position);
		returnToPosition.setOnClickListener(this);

		ImageButton okay = (ImageButton) findViewById(R.id.okay);
		okay.setOnClickListener(this);

		ImageButton satelliteMap = (ImageButton) findViewById(R.id.switch_maps);
		satelliteMap.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(element instanceof Node){
			Node node = (Node) element;
			mapController.setCenter(node.toGeoPoint());
			mapController.animateTo(node.toGeoPoint());
		}else{
			Way way = (Way) element;
			mapController.setCenter(way.getFirstNode().toGeoPoint());
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.return_to_actual_Position:
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
		case R.id.okay:
			break;
		}
	}

}
