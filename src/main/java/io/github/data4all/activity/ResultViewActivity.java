package io.github.data4all.activity;



import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.ResourceProxy;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ResultViewActivity extends Activity {
	

	private static final String TAG = "ResultViewActivity";
	
	
	private MapView mapView;
	
	//Default OpenStreetMap TileSource
		private final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;

		private MapController mapController;
		
		private MyLocationNewOverlay myLocationOverlay;
		//Default Zoom Level
		private final int DEFAULT_ZOOM_LEVEL = 18;
		
		private final ITileSource DEFAULT_TILESOURCE = TileSourceFactory.MAPNIK;
		
		private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_view);
		mapView = (MapView) this.findViewById(R.id.mapviewResult);
		
		mapView.setTileSource(OSM_TILESOURCE);
		
		mapView.setTileSource(DEFAULT_TILESOURCE);
		
		mapView.setMultiTouchControls(true);
		
		mapController = (MapController) this.mapView.getController();
		
		mapController.setZoom(DEFAULT_ZOOM_LEVEL);
		
		myLocationOverlay = new MyLocationNewOverlay(this, mapView);
		
		mapView.getOverlays().add(myLocationOverlay);
		
		listView = (ListView) this.findViewById(R.id.listViewResult);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
