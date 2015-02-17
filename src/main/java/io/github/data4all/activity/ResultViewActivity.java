/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.Tagging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.R.color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ResultViewActivity extends BasicActivity implements
        OnClickListener {
    private static final String TAG = "ResultViewActivity";
    private MapView mapView;

    // Default OpenStreetMap TileSource
    private static final ITileSource OSM_TILESOURCE = TileSourceFactory.MAPNIK;
    private MapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    
    // Minimal Zoom Level
    protected static final int MINIMAL_ZOOM_LEVEL = 10;

    // Maximal Zoom Level
    protected static final int MAXIMAL_ZOOM_LEVEL = 20;

    // Default Zoom Level
    private static final int DEFAULT_ZOOM_LEVEL = 18;
    private static final ITileSource DEFAULT_TILESOURCE =
            TileSourceFactory.MAPNIK;

    // Listview for the Dialog
    private ListView listView;

    // The OSM Element
    private AbstractDataElement element;

    // The Dialog for the unclassified tags
    private Dialog dialog;

    // The List that will be shown in the Activity
    private List<String> endList;

    // The list of all Keys
    private List<String> keyList;
    private AlertDialog alert;
    private Map<String, ClassifiedTag> tagMap;
    private Resources res;
    private Map<String,Tag> mapTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);
        mapView = (MapView) this.findViewById(R.id.mapviewResult);
        element = getIntent().getParcelableExtra("OSM_ELEMENT");
        mapView.setTileSource(OSM_TILESOURCE);
        mapView.setTileSource(DEFAULT_TILESOURCE);
        mapController = (MapController) this.mapView.getController();
        mapController.setZoom(MINIMAL_ZOOM_LEVEL);
        mapController.setCenter(MapUtil.getCenterFromOsmElement(element));
        myLocationOverlay = new MyLocationNewOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        listView = (ListView) this.findViewById(R.id.listViewResult);
        // The Sorted Keys of the ShowPictureActivity
        res = getResources();
        tagMap = Tagging.getMapKeys(getIntent().getExtras().getInt("TYPE_DEF"), res);
        mapTag = Tagging.getUnclassifiedMapKeys(element.getTags(), res);
        this.output();
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    final int position, long id) {
            	Log.i(TAG, "Tagkey" + keyList.get(position));
                // Change Classified Tags
                if (Tagging.isClassifiedTag(keyList.get(position), res)) {
                    Log.i(TAG, "Classified Tag");
                    final AlertDialog.Builder alertDialog =
                            new AlertDialog.Builder(ResultViewActivity.this,
                                    android.R.style.Theme_Holo_Dialog_MinWidth);
                    alertDialog.setTitle("Select Tag");
                    final CharSequence[] showArray;
                    showArray =
                            tagMap.get(keyList.get(position))
                                    .getClassifiedValues()
                                    .toArray(
                                            new String[tagMap
                                                    .get(keyList.get(position))
                                                    .getClassifiedValues()
                                                    .size()]);
                    alertDialog.setItems(showArray,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    element.addOrUpdateTag(
                                            tagMap.get(keyList.get(position)),
                                            (String) showArray[which]);
                                    output();
                                }
                            });
                    alert = alertDialog.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alert.show();
                }
                // Change Unclasssified Tags
                else {
                    dialog =
                            new Dialog(ResultViewActivity.this,
                                    android.R.style.Theme_Holo_Dialog_MinWidth);
                    dialog.setContentView(R.layout.dialog_dynamic);
                    dialog.setTitle(keyList.get(position));
                    final Button okay = new Button(ResultViewActivity.this);
                    final EditText text = new EditText(ResultViewActivity.this);
                    text.setTextColor(-1);
                    final LinearLayout layout =
                            (LinearLayout) dialog
                                    .findViewById(R.id.dialogDynamic);
                    layout.addView(text);
                    layout.addView(okay);
                    okay.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            element.addOrUpdateTag(mapTag.get(keyList.get(position)), text.getText().toString());
                            output();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        final ImageButton resultButton =
                (ImageButton) this.findViewById(R.id.buttonResult);
        resultButton.setOnClickListener(this);
        final ImageButton resultButtonToCamera =
                (ImageButton) this.findViewById(R.id.buttonResultToCamera);
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
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Shows the Tags in a List 
     */
    private void output() {
        endList = new ArrayList<String>();
        keyList = new ArrayList<String>();
        for (Entry entry : element.getTags().entrySet()) {
            final Tag tagKey =  (Tag) entry.getKey();
            Log.i(TAG, tagKey.getKey());
            keyList.add(res.getString(tagKey.getNameRessource()));
            endList.add(res.getString(tagKey.getNameRessource()) + "=" + element.getTags().get(tagKey));
        }
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, endList);
        listView.setAdapter(adapter);
    }

    public void onResume(){
    	super.onResume();
    	BoundingBoxE6 boundingBox = MapUtil.getBoundingBoxForOsmElement(element);
    	Log.i(TAG, boundingBox.toString());
    	
    	mapView.zoomToBoundingBox(boundingBox);
    	mapView.postInvalidate();
    	//mapView.setScrollableAreaLimit(boundingBox);
    	
    }
   
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonResult) {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(ResultViewActivity.this);
            builder.setMessage(R.string.resultViewAlertDialogMessage);
            final Intent intent = new Intent(this, MapViewActivity.class);
            final Intent intent1 = new Intent(this, LoginActivity.class);
            intent.putExtra("OSM_Element", element);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(intent1);
                        }
                    });
            builder.setNegativeButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(intent);
                        }
                    });
            alert = builder.create();
            alert.show();
        } else if (v.getId() == R.id.buttonResultToCamera) {
        	Log.i(TAG, MapUtil.getBoundingBoxForOsmElement(element).toString());
        	mapView.zoomToBoundingBox(MapUtil.getBoundingBoxForOsmElement(element));
           // startActivity(new Intent(this, CameraActivity.class));
        }
    }
}