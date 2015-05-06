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
import io.github.data4all.handler.TagSuggestionHandler;
import io.github.data4all.listener.ButtonRotationListener;
import io.github.data4all.logger.Log;
import io.github.data4all.model.GalleryListAdapter;
import io.github.data4all.model.data.DataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.Track;
import io.github.data4all.service.GPSservice;
import io.github.data4all.service.MapTileService;
import io.github.data4all.service.OrientationListener;
import io.github.data4all.util.Optimizer;
import io.github.data4all.util.TrackUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Main Activity that shows the default MapView.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapViewActivity extends MapActivity implements OnClickListener {

    // Logger Tag
    private static final String TAG = "MapViewActivity";

    private DrawerLayout drawerLayout;

    private ListView drawer;

    private GalleryListAdapter drawerAdapter;

    private static long lastTime;

    ImageButton updateButton;
    
    public static boolean UPDATECLICKABLE = false;
    // Broadcast receiver for receiving status updates from the IntentService
    private class MapTileReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private MapTileReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to
        // receive

        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(OrientationListener.INTENT_CAMERA_UPDATE)) {
                showButton();
            }
        }
    }
    
    private TrackUtil trackUtil;

    /**
     * BroadcastReceiver to receive signal, if there was a change in the current
     * track
     */
    private final BroadcastReceiver TrackChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG,
                    "received broadcast with: " + intent.getLongExtra("id", -1)
                            + "from: " + context.toString());
            updateTrackInView(intent.getLongExtra("id", -1));
        }
    };

    /**
     * The preferences of the application for the shown-state.
     */
    private SharedPreferences prefs;

    /**
     * defines if its the first use of the app
     */
    private boolean firstUse = true;

    /**
     * Default constructor.
     */
    public MapViewActivity() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        lastTime =  new Date().getTime();
        setContentView(R.layout.activity_map_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (ListView) findViewById(R.id.left_drawer);
        drawerAdapter = new GalleryListAdapter(this);
        drawer.setAdapter(drawerAdapter);
        drawer.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, final long id) {
                new AlertDialog.Builder(MapViewActivity.this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.deleteDialog)
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        drawerAdapter.removeImage(id);
                                    }
                                }).setNegativeButton(R.string.no, null).show();
                return true;
            }
        });
        drawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                drawerAdapter.tagImage(id);
            }
        });
        setUpMapView(savedInstanceState);
        if (savedInstanceState == null) {
            setUpLoadingScreen();
        } else {
            view.setVisibility(View.GONE);
        }
        // Setup the rotation listener
        final List<View> buttons = new ArrayList<View>();

        // Set Listener for Buttons
        int id = R.id.return_to_actual_Position;
        final ImageButton returnToPosition = (ImageButton) findViewById(id);
        returnToPosition.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.switch_maps;
        final ImageButton satelliteMap = (ImageButton) findViewById(id);
        satelliteMap.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.to_camera;
        final ImageButton camera = (ImageButton) findViewById(id);
        camera.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.new_point;
        final ImageButton newPoint = (ImageButton) findViewById(id);
        newPoint.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.update;
        updateButton = (ImageButton) findViewById(id);
        updateButton.setOnClickListener(this);
        updateButton.setVisibility(View.INVISIBLE);
        buttons.add(findViewById(id));

        listener = new ButtonRotationListener(this, buttons);
        
        trackUtil = new TrackUtil(this);

        registerReceiver(TrackChangeReceiver, new IntentFilter(
                "trackpoint_updated"));

        // Dialog at first start to set the users height
        bodyheightdialog();
        // The filter's action is BROADCAST_CAMERA
        IntentFilter mStatusIntentFilter = new IntentFilter(
                MapTileService.BROADCAST_MAP);
        // Instantiates a new DownloadStateReceiver
        MapTileReceiver mMapTileReceiver = new MapTileReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMapTileReceiver, mStatusIntentFilter);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        // Add record button only in this activity
        inflater.inflate(R.menu.track_menu, menu);
        final boolean result = super.onCreateOptionsMenu(menu);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.activity.AbstractActivity#onHomePressed()
     */
    @Override
    protected void onHomePressed() {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        } else {
            drawerLayout.openDrawer(drawer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        // Set center to user Location
        case R.id.return_to_actual_Position:
            this.returnToActualPosition();
            break;
        // Upload new Data
        case R.id.upload_data:
            startActivity(new Intent(this, LoginActivity.class));
            break;
        // switch between Maps
        case R.id.switch_maps:
            switchMaps();
            break;
        // Make Photo
        case R.id.to_camera:
            this.startCamera();
            break;
        // Add new POI to the Map
        case R.id.new_point:
            this.createNewPOI();
            break;
        case R.id.update:
            mapView.getTileProvider().clearTileCache();
            mapView.postInvalidate();
            UPDATECLICKABLE = false;
            lastTime = new Date().getTime();
            final ImageButton update = (ImageButton) findViewById(R.id.update);
            update.setVisibility(View.INVISIBLE);
        default:
            break;
        }
    }

    private void startCamera() {
        if (Optimizer.currentLocation() == null) {
            final String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                    .show();
        } else {
            final Intent camera = new Intent(this, CameraActivity.class);
            startActivity(camera);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        // clear all Overlays
        mapView.getOverlays().clear();

        // Set Overlay for the actual Position
        Log.i(TAG, "Added User Location Overlay to the map");
        mapView.getOverlays().add(myLocationOverlay);

        // Enable User Position display
        Log.i(TAG, "Enable User Position Display");
        myLocationOverlay.enableMyLocation();

        myLocationOverlay.enableFollowLocation();

        // add osmElements from the database to the map
        final DataBaseHandler db = new DataBaseHandler(this);
        List<DataElement> list = db.getAllDataElements();
        List<Track> trackList = db.getAllGPSTracks();
        mapView.addGPSTracksToMap(this, trackList);
        mapView.addOsmElementsToMap(this, list);
        // load lastChoice from database
        LastChoiceHandler.load(db);
        db.close();

        // Start the GPS tracking
        Log.i(TAG, "Start GPSService");
        startService(new Intent(this, GPSservice.class));

        Intent mapTilesS = new Intent(this, MapTileService.class);
        mapTilesS.putExtra(MapTileService.TIME, lastTime);
        mapTilesS.putExtra(MapTileService.WEST, (double) mapView
                .getBoundingBox().getLonWestE6());
        mapTilesS.putExtra(MapTileService.SOUTH, (double) mapView
                .getBoundingBox().getLatSouthE6());
        mapTilesS.putExtra(MapTileService.EAST, (double) mapView
                .getBoundingBox().getLonEastE6());
        mapTilesS.putExtra(MapTileService.NORTH, (double) mapView
                .getBoundingBox().getLatNorthE6());

        Log.i(TAG, "Start MapTileService");
        startService(mapTilesS);
        drawerAdapter.invalidate();
    }

    private void showButton() {
        updateButton.setVisibility(View.VISIBLE);

    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.activity.MapActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();

        // Disable Actual Location Overlay
        Log.i(TAG, "Disable Actual Location Overlay");
        myLocationOverlay.disableMyLocation();
        myLocationOverlay.disableFollowLocation();
        stopService(new Intent(this, MapTileService.class));
    }

    /**
     * Creates new POI on the actual Position.
     **/
    private void createNewPOI() {
        final GeoPoint myPosition = myLocationOverlay.getMyLocation();
        if (myPosition == null) {
            final String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                    .show();
        } else {
            final Location location = new Location("overlay");
            location.setLatitude(myPosition.getLatitude());
            location.setLongitude(myPosition.getLongitude());
            TagSuggestionHandler.setLocation(location);
            
            
            final Intent intent = new Intent(this, MapPreviewActivity.class);
            final Node poi = new Node(-1, myPosition.getLatitude(),
                    myPosition.getLongitude());

            // Set Type Definition for Intent to Node
            Log.i(TAG, "Set intent extra " + TYPE + " to " + NODE_TYPE_DEF);
            intent.putExtra(TYPE, NODE_TYPE_DEF);

            // Set OsmElement for Intent to POI
            Log.i(TAG, "Set Intent Extra " + OSM + " to Node with Coordinates "
                    + poi.toString());
            intent.putExtra(OSM, poi);

            // Start MapPreview Activity
            Log.i(TAG, "Start MapPreview Activity");
            startActivityForResult(intent, 0);
        }
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
        final DataBaseHandler db = new DataBaseHandler(this);
        final List<DataElement> list = db.getAllDataElements();
        mapView.addOsmElementsToMap(this, list);
        db.close();
        mapView.postInvalidate();
    }

    /**
     * Set the Center to the User Position.
     **/
    private void returnToActualPosition() {
        if (myLocationOverlay.getMyLocation() != null) {
            setCenter(myLocationOverlay.getMyLocation());
            myLocationOverlay.enableFollowLocation();
        } else {
            final String text = getString(R.string.noLocationFound);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.activity.MapActivity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the GPS tracking
        Log.i(TAG, "Stop GPSService");
        stopService(new Intent(this, GPSservice.class));
        stopService(new Intent(this, MapTileService.class));
        unregisterReceiver(TrackChangeReceiver);
        
    }
    
    /**
     * Gets the track to corresponding id and calls {@link
     * D4AMapView.addGPSTrackToMap()}
     * 
     * @param id
     *            Id of a track
     */
    private void updateTrackInView(long id) {
        Track track = trackUtil.loadTrack(id);
        mapView.addGPSTrackToMap(this, track);
    }

    /*
     * Dialog at first start to set the users height
     * 
     * @author konerman
     */
    private void bodyheightdialog() {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences userPrefs = getSharedPreferences("UserPrefs", 0);
        firstUse = userPrefs.getBoolean("firstUse", true);

        if (firstUse) {
            RelativeLayout linearLayout = new RelativeLayout(this);
            final NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setMaxValue(250);
            numberPicker.setMinValue(80);
            numberPicker.setValue(180);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    50, 50);
            RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            linearLayout.setLayoutParams(params);
            linearLayout.addView(numberPicker, numPicerParams);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setTitle(R.string.pref_bodyheight_dialog_title);
            alertDialogBuilder
                    .setMessage(R.string.pref_bodyheight_dialog_message);
            alertDialogBuilder.setView(linearLayout);
            alertDialogBuilder.setCancelable(false).setPositiveButton(
                    R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG,
                                    "set bodyheight to: "
                                            + numberPicker.getValue());
                            prefs.edit()
                                    .putString(
                                            "PREF_BODY_HEIGHT",
                                            String.valueOf(numberPicker
                                                    .getValue())).commit();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            // set firstUse to false so this dialog is not shown again. ever.
            userPrefs.edit().putBoolean("firstUse", false).commit();
            firstUse = false;
        }
    }
}
