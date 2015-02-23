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

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.map.MapMarker;
import io.github.data4all.util.MapUtil;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Activity to show an Osm_Element on a Preview Map.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapPreviewActivity extends MapActivity implements OnClickListener {

    // Logger Tag
    private static final String TAG = "MapPreviewActivity";

    // The OsmElement which should be added
    private AbstractDataElement element;

    private BoundingBoxE6 boundingBox;

    /**
     * Standard Constructor
     **/
    public MapPreviewActivity() {
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
        setContentView(R.layout.activity_map_preview);
        setUpMapView(savedInstanceState);
        view.setVisibility(View.GONE);
        if(getIntent().hasExtra("OSM_ELEMENT")){
            element = getIntent().getParcelableExtra("OSM_ELEMENT");
        }
        mapView.addOsmElementToMap(this, element);
        if(getIntent().hasExtra("LOCATION")){
            Location l = (Location) getIntent().getParcelableExtra("LOCATION");
            Marker m = new Marker(mapView);
            m.setPosition(new GeoPoint(l));
            m.setIcon(new DefaultResourceProxyImpl(this).getDrawable(ResourceProxy.bitmap.person));
            mapView.getOverlays().add(m);
        }

        boundingBox = MapUtil.getBoundingBoxForOsmElement(element);
        mapView.setBoundingBox(boundingBox);

        // Set Overlay for the actual Position
        Log.i(TAG, "Added User Location Overlay to the map");
        mapView.getOverlays().add(myLocationOverlay);

        int id = R.id.return_to_actual_Position;
        final ImageButton returnToPosition = (ImageButton) findViewById(id);
        returnToPosition.setOnClickListener(this);

        id = R.id.okay;
        final ImageButton okay = (ImageButton) findViewById(R.id.okay);
        okay.setOnClickListener(this);

        id = R.id.switch_maps;
        final ImageButton satelliteMap = (ImageButton) findViewById(id);
        satelliteMap.setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        setCenter(MapUtil.getCenterFromOsmElement(element));
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.return_to_actual_Position:
            mapController.setCenter(MapUtil.getCenterFromOsmElement(element));
            mapView.zoomToBoundingBox(boundingBox);
            break;
        case R.id.switch_maps:
            switchMaps();
            break;
        case R.id.okay:
            this.accept();
            break;
        default:
            break;
        }
    }

    /*
     * Starts new Tagactivity with Osm Object and Type Definition in the Intent
     */
    private void accept() {
        final Intent intent = new Intent(this, TagActivity.class);

        // Set Type Definition for Intent
        Log.i(TAG, "Set intent extra " + TYPE + " to "
                + getIntent().getExtras().getInt(TYPE));
        intent.putExtra(TYPE, getIntent().getExtras().getInt(TYPE));

        // Set OsmElement for Intent to POI
        Log.i(TAG, "Set Intent Extra " + OSM + " "
                + element.getClass().getSimpleName() + " with Coordinates "
                + element.toString());
        intent.putExtra(OSM, element);

        startActivity(intent);
    }

}
