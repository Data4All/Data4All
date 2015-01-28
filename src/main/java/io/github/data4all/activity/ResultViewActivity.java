package io.github.data4all.activity;



import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.Tagging;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ResultViewActivity extends BasicActivity implements OnClickListener {
	

	   private static final String TAG = "ResultViewActivity";
	   
	   final Context context = this;
	   
	   private MapView mapView;
	   
		// Default Stroke width
		protected final float DEFAULT_STROKE_WIDTH = 3.0f;

		// Default Stroke Color
		protected final int DEFAULT_STROKE_COLOR = Color.BLUE;

		// Fill Color for Polygons
		protected final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);
	
	   //Default OpenStreetMap TileSource
		private final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;

		private MapController mapController;
		
		private MyLocationNewOverlay myLocationOverlay;
		//Default Zoom Level
		private final int DEFAULT_ZOOM_LEVEL = 18;
		
		private final ITileSource DEFAULT_TILESOURCE = TileSourceFactory.MAPNIK;
		// Listview for the Dialog 
		private ListView listView;
		// The OSM Element 
		private OsmElement element;
		// The Dialog for the unclassified tags
		private Dialog dialog;
		// The List that will be shown in the Activity
		private List<String> endList;
		// The key Value of the Tag which will be changed 
		private String key;
		// The list of all Keys
		private ArrayList<String> keyList;
		
	    private CharSequence [] array;
	    
	    private AlertDialog alert;
	    
	    private AlertDialog alert1;

		private Map<String, ClassifiedTag> tagMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_view);
		mapView = (MapView) this.findViewById(R.id.mapviewResult);
		element = getIntent().getParcelableExtra("OSM_ELEMENT");
		
		mapView.setTileSource(OSM_TILESOURCE);
		
		mapView.setTileSource(DEFAULT_TILESOURCE);
		
		
		mapController = (MapController) this.mapView.getController();
		
	
		
		mapView.setMinZoomLevel(10);
		
		mapView.setMaxZoomLevel(40);
		
		mapController.setZoom(DEFAULT_ZOOM_LEVEL);
		
		//mapView.zoomToBoundingBox(MapUtil.getBoundingBoxForOsmElement(element));
		
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
		addOsmElementToMap(element);
		
		
		
		
		listView = (ListView) this.findViewById(R.id.listViewResult);
		// The Sorted Keys of the ShowPictureActivity
		array = Tagging.getArrayKeys( getIntent().getExtras().getInt("TYPE_DEF"));
		tagMap = Tagging.getMapKeys( getIntent().getExtras().getInt("TYPE_DEF"));
		output();
		listView.setOnItemClickListener(new OnItemClickListener() {
		
			public void onItemClick(AdapterView parent, View view, final int position, long id) {
				Log.i(TAG, Boolean.toString(Tagging.isClassifiedTag(keyList.get(position), array)));
				Log.i(TAG, keyList.get(position));
				Log.i(TAG, Integer.toString(endList.size()));
				Log.i(TAG, Integer.toString(position));
				//Change Classified Tags
				if(Tagging.isClassifiedTag(keyList.get(position), array)){
					Log.i(TAG, "Classified Tag");
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultViewActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth);
			        alertDialog.setTitle("Select Tag");
			        final CharSequence [] showArray;
			        showArray =  tagMap.get(keyList.get(position)).getClassifiedValues().toArray(new String [tagMap.get(keyList.get(position)).getClassifiedValues().size()]);
	            	alertDialog.setItems(showArray, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							element.addOrUpdateTag(keyList.get(position),(String) showArray [which]);
							output();
						}
					});
	            	
	            	alert = alertDialog.create();
	            	alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	                
	                alert.show();
				}
				// Change Unclasssified Tags
				else {
				dialog = new Dialog(context, android.R.style.Theme_Holo_Dialog_MinWidth);
				dialog.setContentView(R.layout.dialog_dynamic);
				dialog.setTitle(keyList.get(position));
				final ImageButton okay = new ImageButton(context);
				final EditText text = new EditText(context);
				LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.dialogDynamic);
				layout.addView(text);
				layout.addView(okay);
				okay.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						element.addOrUpdateTag(keyList.get(position), text.getText().toString());
						output();
						dialog.dismiss();
					}
					
				});
				
				dialog.show();
				
			}
			
		}
	});
	

	ImageButton resultButton = (ImageButton) this.findViewById(R.id.buttonResult);	
	resultButton.setOnClickListener(this);	
	

	ImageButton resultButtonToCamera = (ImageButton) this.findViewById(R.id.buttonResultToCamera);	
	resultButtonToCamera.setOnClickListener(this);	
		
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
	
	private void output(){
		endList = new ArrayList<String>();
		keyList = new ArrayList<String>();
        for(Entry entry : element.getTags().entrySet()){
			String key = (String) entry.getKey();
			keyList.add(key);
			endList.add(key + "=" + element.getTags().get(key));
           
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, endList);
        listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonResult:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(ResultViewActivity.this);
			builder.setMessage(R.string.resultViewAlertDialogMessage);
			final Intent intent = new Intent(this, MapViewActivity.class);
			final Intent intent1 = new Intent(this, LoginActivity.class);
			intent.putExtra("OSM_Element", element);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               startActivity(intent1);
		           }
		       });

			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   startActivity(intent);
		           }
		       });
			alert = builder.create();
            
            alert.show();

			break;
		case R.id.buttonResultToCamera:
			startActivity(new Intent(this, CameraActivity.class));
		}
		
	}
	
	
	
	
	private void addOsmElementToMap(OsmElement element) {
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
		Marker poi = new Marker(mapView);
		Log.i(TAG, "Set Node Points to " + node.toString());
		
		//disable InfoWindow
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
		Polygon area = new Polygon(this); 

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
		Polyline path = new Polyline(this);
		Log.i(TAG, "Set Path Points to " + way.toString());
		path.setPoints(way.getGeoPoints());

		Log.i(TAG, "Set Path Color to " + DEFAULT_STROKE_COLOR);
		path.setColor(DEFAULT_STROKE_COLOR);

		Log.i(TAG, "Set Path Width to " + DEFAULT_STROKE_WIDTH);
		path.setWidth(DEFAULT_STROKE_WIDTH);
		mapView.getOverlays().add(path);
		mapView.postInvalidate();
	}

	
	
	
	
}
