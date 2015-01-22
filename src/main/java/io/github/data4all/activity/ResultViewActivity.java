package io.github.data4all.activity;



import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

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
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ResultViewActivity extends BasicActivity implements OnClickListener {
	

	   private static final String TAG = "ResultViewActivity";
	
	   final Context context = this;
	   
	   private MapView mapView;
	
	   //Default OpenStreetMap TileSource
		private final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;

		private MapController mapController;
		
		private MyLocationNewOverlay myLocationOverlay;
		//Default Zoom Level
		private final int DEFAULT_ZOOM_LEVEL = 18;
		
		private final ITileSource DEFAULT_TILESOURCE = TileSourceFactory.MAPNIK;
		
		private ListView listView;
		
		private OsmElement element;
		
		private SortedMap<String, String> map;
		
		private Dialog dialog;

		private List<String> endList;

		private String key;

		private ArrayList<String> keyList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_view);
		mapView = (MapView) this.findViewById(R.id.mapviewResult);
		element = getIntent().getParcelableExtra("OSM_ELEMENT");
		
		mapView.setTileSource(OSM_TILESOURCE);
		
		mapView.setTileSource(DEFAULT_TILESOURCE);
		
		
		mapController = (MapController) this.mapView.getController();
		
		mapController.setZoom(DEFAULT_ZOOM_LEVEL);
		
		if(element instanceof Node){
			Node node = (Node) element;
			mapController.setCenter(node.toGeoPoint());
			mapController.animateTo(node.toGeoPoint());
		}else{
			Way way = (Way) element;
			mapController.setCenter(way.getFirstNode().toGeoPoint());
		}
		
		myLocationOverlay = new MyLocationNewOverlay(this, mapView);
		
		mapView.getOverlays().add(myLocationOverlay);
		
		listView = (ListView) this.findViewById(R.id.listViewResult);
		map = new TreeMap<String, String>();
		map = element.getTags();
		
		output();
		listView.setOnItemClickListener(new OnItemClickListener() {
		

			public void onItemClick(AdapterView parent, View view, final int position, long id) {
				dialog = new Dialog(context);
				dialog.setContentView(R.layout.dialog_dynamic);
				dialog.setTitle(keyList.get(position));
				final Button okay = new Button(context);
				final EditText text = new EditText(context);
				LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.dialogDynamic);
				layout.addView(text);
				layout.addView(okay);
				okay.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
					
						element.addOrUpdateTag(keyList.get(position), text.getText().toString());
						map = element.getTags();
						output();
						dialog.dismiss();
					}
					
				});
				
				dialog.show();
				
			}
		});
		
	Button resultButton = (Button) this.findViewById(R.id.buttonResult);	
	resultButton.setOnClickListener(this);	
		
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
	
	private List<String> output(){
		endList = new ArrayList<String>();
		keyList = new ArrayList<String>();
        for(Entry entry : map.entrySet()){
			String key = (String) entry.getKey();
			keyList.add(key);
			endList.add(key + "=" + map.get(key));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, endList);
            listView.setAdapter(adapter);
        }
        return keyList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonResult:
			startActivity(new Intent(this, MapViewActivity.class));
			break;
		}
		
	}
	
}
