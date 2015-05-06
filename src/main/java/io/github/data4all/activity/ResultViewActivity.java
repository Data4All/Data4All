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

import io.github.data4all.Data4AllApplication;
import io.github.data4all.R;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.handler.LastChoiceHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.TwoColumnAdapter;
import io.github.data4all.model.data.DataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Localizeable;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.network.MapBoxTileSourceV4;
import io.github.data4all.util.Gallery;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.Tagging;
import io.github.data4all.util.upload.Callback;
import io.github.data4all.view.AddressSuggestionView;
import io.github.data4all.view.D4AMapView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    // The OSM Element
    private DataElement element;
    // The Dialog
    private Dialog alert;
    // The Listview of the Activity
    private ListView listView;
    // The TypeDef String
    private String typeDef = "TYPE_DEF";
    // The integer of the Type
    private int type;
    // The Classifiedtag Key
    private ClassifiedTag classifiedTag;
    // The Classified Value
    private ClassifiedValue classifiedValue;
    // The List of all unclassifiedValues
    private LinkedList<String> endList;
    // The List of all unclassifiedKeys
    private LinkedList<String> keyList;
    // The Button for changing the Classified Value
    private Button changeClassifiedButton;
    // The List of all unclassified Tags
    private List<Tag> unclassifiedTags;
    private Button addressSuggestions;

    AddressSuggestionView addressSuggestionView;

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
        mapView.addOsmElementToMap(this, element, false);
        Log.i(TAG, "map ready");
        // Sets the Type of the element
        type = getIntent().getExtras().getInt(typeDef);
        // Sets the ListView
        listView = (ListView) this.findViewById(R.id.listViewResultView);
        if (element.getTags().isEmpty()) {
            Log.i(TAG, "new Element");
            addClassifiedTag();
        }
        // Sets all the Buttons
        changeClassifiedButton =
                (Button) this.findViewById(R.id.buttonClassifiedTag);
        changeClassifiedButton.setOnClickListener(this);

        addressSuggestions =
                (Button) this.findViewById(R.id.buttonAddressSuggestions);
        addressSuggestionView =
                new AddressSuggestionView(this, addressSuggestions,
                        new Callback<Void>() {
                            @Override
                            public int interval() {
                                return 0;
                            }

                            @Override
                            public void callback(Void t) {
                                output();
                            }
                        });

        final ImageButton resultButton =
                (ImageButton) this.findViewById(R.id.buttonResult);
        resultButton.setOnClickListener(this);
        final ImageButton resultButtonToCamera =
                (ImageButton) this.findViewById(R.id.buttonResultToCamera);
        resultButtonToCamera.setOnClickListener(this);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                changeUnclassifiedTag(position);

            }
        });
        // The LongClick Listener for removing unclassified Tags
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    final int position, long id) {
                new AlertDialog.Builder(ResultViewActivity.this)
                        .setMessage(R.string.deleteTagsResultview)
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        element.removeTag(unclassifiedTags
                                                .get(position));
                                        output();

                                    }
                                })
                        .setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {

                                    }
                                }).show();
                return true;
            }
        });
        if (!element.getTags().isEmpty()) {
            Log.i(TAG, "taggt element");
            addressSuggestions.setVisibility(1);
            output();
        } else {
            addressSuggestions.setVisibility(0);
        }
    }

    /**
     * This Method fills the List of Unclassified tags
     */
    private void output() {
        endList = new LinkedList<String>();
        keyList = new LinkedList<String>();
        if (classifiedTag == null) {
            classifiedTag = Tagging.getClassifiedTagKey(element);
            Log.i(TAG, classifiedTag.toString());
            classifiedValue =
                    Tagging.getClassifiedValue(element, classifiedTag);
        }
        unclassifiedTags = new ArrayList<Tag>();
        keyList =
                (LinkedList<String>) Tagging.getUnclassifiedTags(
                        classifiedValue, Data4AllApplication.context, keyList,
                        unclassifiedTags, element);
        endList =
                (LinkedList<String>) Tagging.addUnclassifiedValue(element,
                        endList, unclassifiedTags, Data4AllApplication.context);
        changeClassifiedButton.setText(classifiedValue
                .getLocalizedName(Data4AllApplication.context));
        Log.i(TAG, unclassifiedTags.toString());
        listView.setAdapter(new TwoColumnAdapter(this, keyList, endList));
        final TwoColumnAdapter twoColumnAdapter =
                new TwoColumnAdapter(this, keyList, endList);
        twoColumnAdapter.setSuggestionView(addressSuggestionView);
        twoColumnAdapter.notifyDataSetChanged();
        listView.setAdapter(twoColumnAdapter);

        addressSuggestionView.addKeyMapEntry(getResources(), classifiedValue);
        addressSuggestionView.setListview(listView);
        addressSuggestionView.setKeyList(keyList);
        addressSuggestionView.setElement(element);
        addressSuggestionView.setLocation(getLocationFromElement());
    }

    /**
     * 
     * @return the location of dataElement
     */
    public Location getLocationFromElement() {
        Location location = null;
        if (element instanceof PolyElement) {
            final PolyElement elem = (PolyElement) element;

            if (elem.getFirstNode() != null) {
                location = new Location("");
                location.setLatitude(elem.getFirstNode().getLat());
                location.setLongitude(elem.getFirstNode().getLon());
            }
        } else {

            final Node elem = (Node) element;
            location = new Location("");
            location.setLatitude(elem.getLat());
            location.setLongitude(elem.getLon());
        }
        return location;
    }

    /**
     * This Method shows an AlertDialog with all TagKeys which are important for
     * the tagged Element
     */
    private void addClassifiedTag() {
        // The AlertDialog settings
        final AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(ResultViewActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth);
        alertDialog.setTitle(R.string.SelectTag);
        // showArray is the Array with all Keys
        final CharSequence[] showArray = Tagging.getArrayKeys(type, this);
        alertDialog.setItems(showArray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String lastChoice = getString(R.string.name_lastchoice);
                if (lastChoice.equalsIgnoreCase((String) showArray[which])) {

                    element.setTags(LastChoiceHandler.getInstance()
                            .getLastChoice(
                                    getIntent().getExtras().getInt("TYPE_DEF")));
                    Log.i(TAG, "TAGSSSSSSLASTCHOICE"
                            + element.getTags().toString());
                    alert.dismiss();
                    ResultViewActivity.this.output();
                } else {
                    classifiedTag =
                            (ClassifiedTag) Tagging.getKeys(type).get(which);
                    alert.dismiss();
                    changeClassifiedValue();
                }

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
                    Log.i(TAG, element.getTags().toString());
                    if (element.getTags().isEmpty()) {
                        return false;
                    } else {
                        alert.dismiss();
                        return true;
                    }
                }
                return true;
            }
        });
        alert.show();

    }

    /**
     * Changes the ClassifiedValue with an Dialog with all Values of the
     * Classified Tag
     * 
     */
    private void changeClassifiedValue() {
        // The Dialog settings
        final Dialog dialog =
                new Dialog(ResultViewActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_matches);
        ListView listView = (ListView) dialog.findViewById(R.id.list);
        dialog.setTitle(R.string.SelectTag);
        // The Array Adapter with the Classified Values
        ArrayAdapter<? extends Localizeable> adapter =
                new ArrayAdapter<ClassifiedValue>(this,
                        android.R.layout.simple_list_item_1,
                        classifiedTag.getClassifiedValues()) {
                    @Override
                    public View getView(int position, View convertView,
                            ViewGroup parent) {
                        View view =
                                super.getView(position, convertView, parent);
                        TextView textView =
                                (TextView) view
                                        .findViewById(android.R.id.text1);
                        textView.setText(getItem(position).getLocalizedName(
                                getApplicationContext()));
                        textView.setTextColor(Color.WHITE);
                        return view;
                    }
                };
        listView.setAdapter(adapter);
        // The OnClickListener for the Value
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                classifiedValue =
                        classifiedTag.getClassifiedValues().get(position);
                // Adds the Classified Key Value pair to the element
                if (!element.getTags().containsKey(classifiedTag)
                        && !element.getTags().isEmpty()) {
                    Log.i(TAG, "false");
                    ClassifiedTag classiTag =
                            Tagging.getClassifiedTagKey(element);
                    element.removeTag(classiTag);
                    LinkedHashMap<Tag, String> map =
                            new LinkedHashMap<Tag, String>();
                    Log.d("BLUB", "" + element.getTags());
                    map.putAll(element.getTags());
                    element.clearTags();
                    element.addOrUpdateTag(classifiedTag,
                            classifiedValue.getValue());
                    element.addTags(map);
                } else {
                    Log.i(TAG, "true");
                    element.addOrUpdateTag(classifiedTag,
                            classifiedValue.getValue());
                }
                Log.i(TAG, "All Tags" + element.getTags().toString());
                dialog.dismiss();
                output();

            }
        });
        // The LongClickListerner for the Discription
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(ResultViewActivity.this,
                                android.R.style.Theme_Holo_Dialog_MinWidth);
                alertDialog.setMessage(classifiedTag.getClassifiedValues()
                        .get(position).getDescriptionResource());

                alertDialog.setNeutralButton(R.string.CancelButton,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                alert.dismiss();

                            }
                        });
                alert = alertDialog.create();
                alert.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alert.show();
                return true;
            }
        });
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    addClassifiedTag();
                    return true;
                }
                return false;
            }
        });

        dialog.show();
    }

    /**
     * Shows an AlertDialog where you can change the Unclassified Tag
     * 
     * @param position
     *            the Position in the ListView
     */
    private void changeUnclassifiedTag(final int position) {
        final EditText input = new EditText(this);
        input.setText(element.getTagValueWithKey(unclassifiedTags.get(position)));
        input.setTextColor(Color.WHITE);
        input.setInputType(unclassifiedTags.get(position).getType());
        if (input.getText().length() > 0) {
            input.setSelection(input.getText().length() - 1);
        }
        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(ResultViewActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth);
        dialog.setTitle(keyList.get(position));
        dialog.setView(input);
        dialog.setPositiveButton(R.string.next,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, unclassifiedTags.get(position).toString());
                        element.addOrUpdateTag(unclassifiedTags.get(position),
                                input.getText().toString());
                        Log.i(TAG, "All Tags" + element.getTags().toString());
                        if (position < unclassifiedTags.size() - 1) {
                            changeUnclassifiedTag(position + 1);
                        }
                        output();
                    }
                });
        dialog.setNegativeButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, unclassifiedTags.get(position).toString());
                        element.addOrUpdateTag(unclassifiedTags.get(position),
                                input.getText().toString());
                        Log.i(TAG, "All Tags" + element.getTags().toString());
                        output();

                    }
                });
        alert = dialog.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.show();

    }

    private void askForGalleryDelete() {
        if (getIntent().hasExtra(Gallery.GALLERY_ID_EXTRA)) {
            final long id =
                    getIntent().getLongExtra(Gallery.GALLERY_ID_EXTRA, 0);
            final String preferenceChoise = getPreferenceChoise();

            if ("yes".equals(preferenceChoise)) {
                new Gallery(this).deleteImage(id);
                this.createAlertDialogResult();
            } else if ("no".equals(preferenceChoise)) {
                this.createAlertDialogResult();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.deleteImage)
                        .setMessage(R.string.deleteImageText)
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        new Gallery(ResultViewActivity.this)
                                                .deleteImage(id);
                                        ResultViewActivity.this
                                                .createAlertDialogResult();
                                    }
                                })
                        .setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        ResultViewActivity.this
                                                .createAlertDialogResult();
                                    }
                                }).show();
            }
        } else {
            this.createAlertDialogResult();
        }
    }

    /**
     * Get the users preference of deleting an gallery image after tagging.
     * 
     * @return The users delete-mode choice
     */
    private String getPreferenceChoise() {
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        final Resources res = getResources();
        final String key = res.getString(R.string.pref_gallery_deletemode_key);
        final String choise = prefs.getString(key, null);
        return choise;
    }

    /**
     * create the AlertDialog at the end with a pos, negative and maybe Button
     */
    private void createAlertDialogResult() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(ResultViewActivity.this);
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
        alert = builder.show();
        alert.show();

    }

    /**
     * Adds an Osm Element to the DataBase
     * 
     * @param the
     *            Data Element to add
     **/
    private void addOsmElementToDB(DataElement dataElement) {
        final DataBaseHandler db = new DataBaseHandler(this);
        if (dataElement.getOsmId() == -1) {
            db.createDataElement(dataElement);
        } else {
            // if the Element allready exists
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonClassifiedTag:
            addClassifiedTag();
            break;
        case R.id.buttonResult:
            LastChoiceHandler.getInstance().setLastChoice(
                    getIntent().getExtras().getInt("TYPE_DEF"),
                    element.getTags());
            LastChoiceHandler.getInstance().save(this);

            askForGalleryDelete();
            break;
        case R.id.buttonResultToCamera:
            this.addOsmElementToDB(element);
            final Intent i = new Intent(this, CameraActivity.class);
            i.putExtra(CameraActivity.FINISH_TO_CAMERA, true);
            addOsmElementToDB(element);
            finishWorkflow(null);
            startActivityForResult(i);
            break;
        default:
            break;
        }

    }

}
