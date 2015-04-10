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
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.handler.LastChoiceHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.TwoColumnAdapter;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.network.MapBoxTileSourceV4;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.Tagging;
import io.github.data4all.view.D4AMapView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * View after Drawing and Tagging
 * 
 * @author Maurice Boyke
 *
 */
public class ResultViewActivity extends AbstractActivity implements
        OnClickListener {
    private static final String TAG = "ResultViewActivity";
    private D4AMapView mapView;

    // Default OpenStreetMap TileSource
    protected MapBoxTileSourceV4 osmMap;

    protected static final String OSM_MAP_NAME = "mapbox.streets";

    // Minimal Zoom Level
    protected static final int MINIMAL_ZOOM_LEVEL = 10;

    // Maximal Zoom Level
    protected static final int MAXIMAL_ZOOM_LEVEL = 22;

    private MapController mapController;

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
    // The alertDialog of the Classified tags
    private AlertDialog alert;
    // The Map with the String and the ClassifiedTag
    private Map<String, ClassifiedTag> tagMap;
    // The Rescources
    private Resources res;
    // The Map wiht the String and the Tag
    private Map<String, Tag> mapTag;
    // The Map with the String and ClassifiedValue
    private Map<String, ClassifiedValue> classifiedMap;
 
    private ClassifiedValue classifiedValue;
    
    public static String tmp;

    private Map<Tag, String> map = new LinkedHashMap<Tag, String>();
    
    private View viewFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        osmMap = new MapBoxTileSourceV4(OSM_MAP_NAME, MINIMAL_ZOOM_LEVEL,
                MAXIMAL_ZOOM_LEVEL);

        // Here is the OsmDroidMap created
        setContentView(R.layout.activity_result_view);
        mapView = (D4AMapView) this.findViewById(R.id.mapviewResult);
        element = getIntent().getParcelableExtra("OSM_ELEMENT");
        mapView.setTileSource(osmMap);
        mapController = (MapController) this.mapView.getController();
        mapController.setCenter(MapUtil.getCenterFromOsmElement(element));
        final BoundingBoxE6 boundingBox = MapUtil
                .getBoundingBoxForOsmElement(element);
        mapView.setBoundingBox(boundingBox);
        mapView.setScrollable(false);
        mapView.addOsmElementToMap(this, element);
        // Here the List of tags is created
        Log.i(TAG, "map ready");
        listView = (ListView) this.findViewById(R.id.listViewResultView);
        res = getResources();
        tagMap = Tagging.getMapKeys(getIntent().getExtras().getInt("TYPE_DEF"),
                res);
        Log.i(TAG, "tagMap " + tagMap);
        mapTag = Tagging.getUnclassifiedMapKeys(res);
        classifiedValue = null;
        /**if (!Tagging.getAllNonSelectedTags(element.getTags(),
                classifiedValue).isEmpty()) {
            final LayoutInflater inflater = getLayoutInflater();
            viewFooter = ((LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
                    R.drawable.footer_listviewresult, null, false);
            listView.addFooterView(viewFooter);
            final TextView tVFooter = ((TextView) viewFooter
                    .findViewById(R.id.titleFooter));
            tVFooter.setOnClickListener(this);
            viewFooter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDialogAddTags();

                }
            });
        }*/
        if(element.getTags().isEmpty()){
        	addClassifiedTag();
        }
        this.output();
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!Tagging.isClassifiedTag(keyList.get(position), res)){
					removeTag(keyList.get(position));
				}
				return true;
			}
		});
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    final int position, long id) {
                Log.i(TAG, "pos" + position);
                Log.i(TAG, "Tagkey" + keyList.get(position));
                final String selectedString = keyList.get(position);
                if (Tagging.isClassifiedTag(keyList.get(position), res)) {
                    ResultViewActivity.this.changeClassifiedTag(selectedString);
                } else if (keyList.get(position).equals(res.getString(R.string.SelectTag))){
                	addClassifiedTag();
                } else {
                    ResultViewActivity.this
                            .changeUnclassifiedTag(selectedString);
                }
            }
        });
       
        final ImageButton resultButton = (ImageButton) this
                .findViewById(R.id.buttonResult);
        resultButton.setOnClickListener(this);
        final ImageButton resultButtonToCamera = (ImageButton) this
                .findViewById(R.id.buttonResultToCamera);
        resultButtonToCamera.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_view, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean status;
        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            status = true;
            break;
        case R.id.action_help:
            // TODO set help activity here
            status = true;
            break;
        // finish workflow, return to mapview
        case android.R.id.home:
            onWorkflowFinished(null);
            status = true;
            break;    
        default:
            return super.onOptionsItemSelected(item);
        }
        return status;
    }
    /**
     * The Method to remove a Tag and display an AlertDailog
     * 
     * @param selectedString The String of the Tag
     */
    private void removeTag(final String selectedString){
    	final AlertDialog.Builder builder = new AlertDialog.Builder(
                ResultViewActivity.this);
        builder.setMessage(R.string.deleteTagsResultview);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	element.removeTag(mapTag.get(selectedString));
                    	output();
                    }
                });
        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        
                    }
                });
        alert = builder.create();
        alert.show();
    	
    }
    
    /**
     * adds the classified Tag 
     */
    private void addClassifiedTag() {
    	  final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                  ResultViewActivity.this,
                  android.R.style.Theme_Holo_Dialog_MinWidth);
          alertDialog.setTitle(R.string.SelectTag);
          final CharSequence[] showArray;
          showArray = Tagging.getArrayKeys(
                  getIntent().getExtras().getInt("TYPE_DEF"), res);
          alertDialog.setItems(showArray, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  final String key = (String) showArray[which];
                  Log.i(TAG, tagMap.get(key).getKey());
                  ResultViewActivity.this.changeClassifiedTag(key);
              }
          });
          alertDialog.setCancelable(false);
          alert = alertDialog.create();
          alert.getWindow().setBackgroundDrawable(
                  new ColorDrawable(android.graphics.Color.TRANSPARENT));
          alert.show();    	
    }
    /**
     * Shows the Tags in a List
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private void output() {
        endList = new LinkedList<String>();
        keyList = new LinkedList<String>();
        List<Tag> tagList = new LinkedList<Tag>();
        final Button resultButton = (Button) this
                .findViewById(R.id.buttonClassifiedTag);
        resultButton.setOnClickListener(this);
        for (Entry<Tag,String> entry : element.getTags().entrySet()) {
            if(entry.getKey() != null){
            final Tag tagKey = entry.getKey();
            tagList.add(tagKey);
            keyList.add(res.getString(tagKey.getNameRessource()));
            if (Tagging.isClassifiedTag(getString(tagKey.getNameRessource()),
                    res)) {
                try {
                    endList.add(getString((Integer) R.string.class
                            .getDeclaredField(
                                    "name_"
                                            + tagKey.getKey()
                                            + "_"
                                            + element.getTags().get(tagKey)
                                                    .replaceAll(":", "_")).get(
                                    null)));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "", e);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "", e);
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, "", e);
                }
            } else {
                endList.add(element.getTags().get(tagKey));
            }

            }
        }
        	
            if(keyList.size() >= 1){
            Log.i(TAG, "KeyLIST" + keyList.toString());	
        	Log.i(TAG, "Tag1 " + keyList.get(0));
        	Log.i(TAG, "Tag " + tagMap.get(keyList.get(0)));
        	classifiedMap = Tagging.classifiedValueMap(tagMap.get(keyList.get(0))
                    .getClassifiedValues(), res, false);
        	Log.i(TAG, classifiedMap.toString());
        	Tag tagKey = null;
        	for (Entry<Tag,String> entry : element.getTags().entrySet()) {
                 tagKey = entry.getKey();
                 break;
        	 }     
            String value = null;
			try {
				value = getString((Integer) R.string.class
				        .getDeclaredField(
				                "name_"
				                        + tagKey.getKey()
				                        + "_"
				                        + element.getTags().get(tagKey)
				                                .replaceAll(":", "_")).get(
				                null));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            ClassifiedValue classi = classifiedMap.get(value);
            classifiedValue = classi;
            keyList = Tagging.getUnclassifiedTags(classi, res, keyList, element);
            
        }
        
        if(keyList.isEmpty() && endList.isEmpty()){
        	String selectTag = res.getString(R.string.SelectTag);
        	keyList.add(selectTag);
        	endList.add("");
        }
        keyList.remove(0);
        resultButton.setText(endList.get(0));
        Log.i(TAG, "ClassifiedTagValue" + endList.get(0));
        endList.remove(0);
        listView.setAdapter(new TwoColumnAdapter(this, keyList, endList));
       /** LastChoiceHandler.getInstance().setLastChoice(
                getIntent().getExtras().getInt("TYPE_DEF"), element.getTags());
        LastChoiceHandler.getInstance().save(this); */
    }

    /**
     * Changes the Classified Tags with the selected String and saves the new
     * one
     * 
     * @param selectedString
     *            is the Selected String
     */
    private void changeClassifiedTag(final String selectedString) {
        Log.i(TAG, "Classified Tag");
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ResultViewActivity.this,
                android.R.style.Theme_Holo_Dialog_MinWidth);
        alertDialog.setTitle(R.string.SelectTag);
        final CharSequence[] showArray;
        showArray = Tagging.ClassifiedValueList(tagMap.get(selectedString)
                .getClassifiedValues(), res);
        classifiedMap = Tagging.classifiedValueMap(tagMap.get(selectedString)
                .getClassifiedValues(), res, false);
         
        alertDialog.setItems(showArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String value = (String) showArray[which];
                final String realValue = classifiedMap.get(value).getValue();
                Log.i(TAG, "Value " + realValue);
                Log.i(TAG, tagMap.get(selectedString).toString());
                	Tag tagKey = null;
                	for (Entry<Tag,String> entry : element.getTags().entrySet()) {
                        tagKey = entry.getKey();
                        break;
               	 	}	     
                	element.removeTag(tagKey);
                	map.putAll(element.getTags());
                	map.remove(tagKey);
                	Log.i(TAG, "MAP" + map.toString());
                	element.clearTags();
                	
                
                element.addOrUpdateTag(tagMap.get(selectedString), realValue);
                Log.i(TAG, "MAPPPPPPPPP" + map.toString());
                Tagging.compareUnclassifiedTags(classifiedMap.get(value), map);
                
                element.addTags(map);
                Log.i(TAG, "TAAAAAAAAAAGGGGGGGGGSSSSSSS" + element.getTags().toString());
                ResultViewActivity.this.output();
            }
        });
        alertDialog.setCancelable(false);
        alert = alertDialog.create();
        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alert.dismiss();
                    element.removeTag(tagMap.get(selectedString));
                    addClassifiedTag();
                    return true;
                }
                return true;
            }
        });
        alert.show();
    }

    /**
     * Changes the selected Unclassified Tag and saves the new one in element
     * 
     * @param selectedString
     *            is the String which is selected
     */
    private void changeUnclassifiedTag(final String selectedString) {
        dialog = new Dialog(ResultViewActivity.this,
                android.R.style.Theme_Holo_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_dynamic);
        dialog.setTitle(selectedString);
        Map <Tag, String> hashMap = new LinkedHashMap<Tag, String>();
        map.putAll(element.getTags());
        final Button okay = new Button(ResultViewActivity.this);
        final Button next = new Button(ResultViewActivity.this);
        final EditText text = new EditText(ResultViewActivity.this);
        text.setTextColor(Color.WHITE);
        final Tag tag = mapTag.get(selectedString);
        text.setInputType(tag.getType());
        Log.i(TAG, tag.toString());
        Log.i(TAG, "VALUE: " + map.toString());
        for(Entry<Tag,String> entry : map.entrySet()){
        	Tag tag1 = entry.getKey();
        	if(tag1.getKey().equals(tag.getKey())){
        		Log.i(TAG, "true");
        		text.setText(map.get(tag1));
        	}
        }
        //text.setText(map.get(tag));
        okay.setText(R.string.ok);
        okay.setTextColor(Color.WHITE);
        next.setText(R.string.next);
        next.setTextColor(Color.WHITE);
        final LinearLayout layout = (LinearLayout) dialog
                .findViewById(R.id.dialogDynamic);
        layout.addView(text);
        if(keyList.indexOf(selectedString) < keyList.size()-1){
        	layout.addView(next);
        }
        layout.addView(okay);
        //Displays the Keyboard
        dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        okay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (text.getText().toString().matches("")) {
                    ResultViewActivity.this.output();
                    dialog.dismiss();
                } else {
                	for(Entry<Tag,String> entry : map.entrySet()){
                    	Tag tag1 = entry.getKey();
                    	if(tag1.getKey().equals(tag.getKey())){
                    		Log.i(TAG, "true");
                    		element.removeTag(tag1);
                    	}
                    }
                    element.addOrUpdateTag(mapTag.get(selectedString), text
                            .getText().toString());
                    ResultViewActivity.this.output();
                    dialog.dismiss();
                }
            }
        });
        next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (text.getText().toString().matches("")) {
					dialog.dismiss();
					ResultViewActivity.this.changeUnclassifiedTag(keyList.get(keyList.indexOf(selectedString) +1));
				} else {
                    element.addOrUpdateTag(mapTag.get(selectedString), text
                            .getText().toString());
                    dialog.dismiss();
                    ResultViewActivity.this.changeUnclassifiedTag(keyList.get(keyList.indexOf(selectedString) +1));            
                }            
			}
		});
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonResult:
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    ResultViewActivity.this);
            Log.i(TAG, element.getTags().toString());
            builder.setMessage(R.string.resultViewAlertDialogMessage);
            final Intent intent = new Intent(this, LoginActivity.class);
            builder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            addOsmElementToDB(element);
                            startActivityForResult(intent);
                        }
                    });
            builder.setNegativeButton(R.string.maybe,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        	
                            setResult(RESULT_OK);
                            finishWorkflow(null);
                        }
                    });
            builder.setNeutralButton(R.string.no, 
            		new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addOsmElementToDB(element);
                    setResult(RESULT_OK);
                    finishWorkflow(null);
                    
				}
			});
            alert = builder.create();
            alert.show();
            break;
        case R.id.buttonResultToCamera:
            this.addOsmElementToDB(element);
            final Intent i = new Intent();
            i.putExtra(CameraActivity.FINISH_TO_CAMERA, true);
            finishWorkflow(i);
            break;
       /** case R.id.titleFooter:
            createDialogAddTags();
            break; */
        case R.id.buttonClassifiedTag:
        	addClassifiedTag();
        default:
            break;
        }
    }

    /**
     * creates the Dialog with the List of all unclassified Tags which are not
     * used.
     * 
     */
   /** private void createDialogAddTags() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ResultViewActivity.this,
                android.R.style.Theme_Holo_Dialog_MinWidth);
        Log.i(TAG, "bla" + classifiedValue);
        final List<Tag> list = Tagging.getAllNonSelectedTags(element.getTags(), classifiedValue);
        Log.i(TAG, list.toString());
        final String[] listString;
        listString = Tagging.TagsToStringRes(list, res);
        alertDialog.setItems(listString, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, listString[which]);
                changeUnclassifiedTag(listString[which]);
                alert.dismiss();
            }
        });
        list.clear();
        alert = alertDialog.create();
        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.show();

    }
*/
    /**
     * Adds an Osm Element to the DataBase
     * 
     * @param the
     *            Data Element to add
     **/
    private void addOsmElementToDB(AbstractDataElement dataElement) {
        final DataBaseHandler db = new DataBaseHandler(this);
        if (dataElement.getOsmId() == -1) {
            db.createDataElement(dataElement);
        } else {
            //if the Element allready exists
            db.updateDataElement(dataElement);
        }
        db.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android
     * .content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        finishWorkflow(data);
    }
}
