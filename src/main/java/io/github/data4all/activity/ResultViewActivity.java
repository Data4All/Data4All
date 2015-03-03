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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        osmMap =
                new MapBoxTileSourceV4(OSM_MAP_NAME, MINIMAL_ZOOM_LEVEL,
                        MAXIMAL_ZOOM_LEVEL);

        // Here is the OsmDroidMap created
        setContentView(R.layout.activity_result_view);
        mapView = (D4AMapView) this.findViewById(R.id.mapviewResult);
        element = getIntent().getParcelableExtra("OSM_ELEMENT");
        mapView.setTileSource(osmMap);
        mapController = (MapController) this.mapView.getController();
        mapController.setCenter(MapUtil.getCenterFromOsmElement(element));
        final BoundingBoxE6 boundingBox =
                MapUtil.getBoundingBoxForOsmElement(element);
        mapView.setBoundingBox(boundingBox);
        mapView.setScrollable(false);
        mapView.addOsmElementToMap(this, element);
        // Here the List of tags is created
        listView = (ListView) this.findViewById(R.id.listViewResultView);
        res = getResources();
        tagMap =
                Tagging.getMapKeys(getIntent().getExtras().getInt("TYPE_DEF"),
                        res);
        mapTag = Tagging.getUnclassifiedMapKeys(element.getTags(), res);
        this.output();
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    final int position, long id) {
                Log.i(TAG, "Tagkey" + keyList.get(position));
                final String selectedString = keyList.get(position);
                if (Tagging.isClassifiedTag(keyList.get(position), res)) {
                    ResultViewActivity.this.changeClassifiedTag(selectedString);
                } else {
                    ResultViewActivity.this
                            .changeUnclassifiedTag(selectedString);
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
        final List<Tag> tagList = new ArrayList<Tag>();
        for (Entry entry : element.getTags().entrySet()) {
            final Tag tagKey = (Tag) entry.getKey();
            tagList.add(tagKey);
            Log.i(TAG, tagKey.getKey());
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

        listView.setAdapter(new TwoColumnAdapter(this, keyList, endList));
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
        final AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(ResultViewActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth);
        alertDialog.setTitle("Select Tag");
        final CharSequence[] showArray;
        showArray =
                Tagging.ClassifiedValueList(tagMap.get(selectedString)
                        .getClassifiedValues(), res);
        classifiedMap =
                Tagging.classifiedValueMap(tagMap.get(selectedString)
                        .getClassifiedValues(), res, false);
        alertDialog.setItems(showArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String value = (String) showArray[which];
                final String realValue = classifiedMap.get(value).getValue();
                Log.i(TAG, "Value " + realValue);
                Log.i(TAG, tagMap.get(selectedString).toString());
                element.addOrUpdateTag(tagMap.get(selectedString), realValue);
                ResultViewActivity.this.output();
            }
        });
        alert = alertDialog.create();
        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.show();
    }

    /**
     * Changes the selected Unclassified Tag and saves the new one in element
     * 
     * @param selectedString
     *            is the String which is selected
     */
    private void changeUnclassifiedTag(final String selectedString) {
        dialog =
                new Dialog(ResultViewActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_dynamic);
        dialog.setTitle(selectedString);
        final Button okay = new Button(ResultViewActivity.this);
        final EditText text = new EditText(ResultViewActivity.this);
        text.setTextColor(Color.WHITE);
        text.setInputType(mapTag.get(selectedString).getType());
        okay.setText(R.string.ok);
        okay.setTextColor(Color.WHITE);
        final LinearLayout layout =
                (LinearLayout) dialog.findViewById(R.id.dialogDynamic);
        layout.addView(text);
        layout.addView(okay);
        okay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                element.addOrUpdateTag(mapTag.get(selectedString), text
                        .getText().toString());
                ResultViewActivity.this.output();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonResult) {
            this.addOsmElementToDB(element);
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(ResultViewActivity.this);
            builder.setMessage(R.string.resultViewAlertDialogMessage);
            final Intent intent = new Intent(this, LoginActivity.class);
            builder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(intent);
                        }
                    });
            builder.setNegativeButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_OK);
                            finishWorkflow();
                        }
                    });
            alert = builder.create();
            alert.show();
        } else if (v.getId() == R.id.buttonResultToCamera) {
            this.addOsmElementToDB(element);
            finishWorkflow();
        }
    }

    /**
     * Adds an Osm Element to the DataBase
     * 
     * @param the
     *            Data Element to add
     **/
    private void addOsmElementToDB(AbstractDataElement dataElement) {
        final DataBaseHandler db = new DataBaseHandler(this);
        db.createDataElement(dataElement);
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
        finishWorkflow();
    }
}
