/* 
 * Copyright (c) 2014, 2015 Data4All
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import io.github.data4all.util.MapUtil;

import org.osmdroid.util.GeoPoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    private OsmElement element;

    public MapPreviewActivity() {

    }

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
                actCentLat = (Double) savedInstanceState
                        .getSerializable("actualCenterLatitude");
                actCentLong = (Double) savedInstanceState
                        .getSerializable("actualCenterLongitude");
                actualCenter = new GeoPoint(actCentLat, actCentLong);
                Log.i(TAG, "Set Mapcenter to" + actualCenter.toString());

            }
        }

        view.setVisibility(View.GONE);

        mapController.setZoom(actualZoomLevel);
        mapController.setCenter(actualCenter);

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

    @Override
    protected void onStart() {
        super.onStart();
        if (element instanceof Node) {
            Node node = (Node) element;
            mapController.setCenter(node.toGeoPoint());
            mapController.animateTo(node.toGeoPoint());
        } else {
            Way way = (Way) element;
            mapController.setCenter(way.getFirstNode().toGeoPoint());
            mapController.animateTo(way.getFirstNode().toGeoPoint());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.return_to_actual_Position:
            mapController.setCenter(MapUtil.getCenterFromOsmElement(element));
            break;
        case R.id.switch_maps:
            switchMaps();
            break;
        case R.id.okay:
            accept();
            break;
        default:
            break;
        }
    }

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
