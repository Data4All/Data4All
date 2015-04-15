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

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;

import io.github.data4all.R;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.listener.ButtonRotationListener;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.util.Gallery;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.MathUtil;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ZoomControls;
import android.widget.ZoomButtonsController;

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
        if (getIntent().hasExtra("OSM_ELEMENT")) {
            element = getIntent().getParcelableExtra("OSM_ELEMENT");
        }
        this.setUpOverlays();

        final BoundingBoxE6 boundingBox = MapUtil
                .getBoundingBoxForOsmElement(element);
        mapView.setBoundingBox(boundingBox);

        // Set Overlay for the actual Position
        Log.i(TAG, "Added User Location Overlay to the map");
        mapView.getOverlays().add(myLocationOverlay);

        // Setup the rotation listener
        final List<View> buttons = new ArrayList<View>();

        int id = R.id.return_to_actual_Position;
        final ImageButton returnToPosition = (ImageButton) findViewById(id);
        returnToPosition.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.okay;
        final ImageButton okay = (ImageButton) findViewById(R.id.okay);
        okay.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.switch_maps;
        final ImageButton satelliteMap = (ImageButton) findViewById(id);
        satelliteMap.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.rect;
        final ImageButton rect = (ImageButton) findViewById(id);
        rect.setOnClickListener(this);
        buttons.add(findViewById(id));

        id = R.id.zoomcontrols;
        final ZoomControls zoomControls = (ZoomControls) findViewById(id);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getController().zoomIn();
                zoomControls.setIsZoomInEnabled(mapView.canZoomIn());
                zoomControls.setIsZoomOutEnabled(mapView.canZoomOut());

            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomControls.setIsZoomOutEnabled(mapView.canZoomOut());
                mapView.getController().zoomOut();
                zoomControls.setIsZoomInEnabled(mapView.canZoomIn());
                zoomControls.setIsZoomOutEnabled(mapView.canZoomOut());

            }
        });

        zoomControls.hide();

        listener = new ButtonRotationListener(this, buttons);

        if (element instanceof PolyElement) {
            PolyElement elem = (PolyElement) element;
            if (elem.getNodes().size() != 5
                    || elem.getType().equals(PolyElement.PolyElementType.WAY)) {
                rect.setClickable(false);
                rect.setVisibility(View.GONE);
            }
        } else {
            rect.setClickable(false);
            rect.setVisibility(View.GONE);
        }

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
            mapController.animateTo(MapUtil.getCenterFromOsmElement(element));
            break;
        case R.id.switch_maps:
            List<Overlay> list = new ArrayList<Overlay>();
            list.addAll(mapView.getOverlays());
            switchMaps();
            mapView.getOverlays().clear();
            mapView.getOverlays().addAll(list);
            mapView.postInvalidate();
            break;
        case R.id.okay:
            this.accept();
            break;
        case R.id.rect:
            this.startRectangularPreview();
            break;
        default:
            break;
        }
    }

    private void setUpOverlays() {
        if (getIntent().hasExtra("LOCATION")) {
            final Location l = (Location) getIntent().getParcelableExtra(
                    "LOCATION");
            final Marker m = new Marker(mapView);
            m.setPosition(new GeoPoint(l));
            m.setIcon(new DefaultResourceProxyImpl(this)
                    .getDrawable(ResourceProxy.bitmap.person));
            m.setInfoWindow(null);
            mapView.getOverlays().add(m);
        }
        final DataBaseHandler db = new DataBaseHandler(this);
        final List<AbstractDataElement> list = db.getAllDataElements();
        list.remove(element);
        mapView.addOsmElementsToMap(this, list);
        db.close();
        mapView.addOsmElementToMap(this, element, true);
    }

    /*
     * Starts new Tagactivity with Osm Object and Type Definition in the Intent
     */
    private void accept() {
        final Intent intent = new Intent(this, ResultViewActivity.class);

        // Set Type Definition for Intent
        Log.i(TAG, "Set intent extra " + TYPE + " to "
                + getIntent().getExtras().getInt(TYPE));
        intent.putExtra(TYPE, getIntent().getExtras().getInt(TYPE));

        // Set OsmElement for Intent to POI
        Log.i(TAG, "Set Intent Extra " + OSM + " "
                + element.getClass().getSimpleName() + " with Coordinates "
                + element.toString());
        intent.putExtra(OSM, element);

        if (getIntent().hasExtra(Gallery.GALLERY_ID_EXTRA)) {
            intent.putExtra(Gallery.GALLERY_ID_EXTRA,
                    getIntent().getLongExtra(Gallery.GALLERY_ID_EXTRA, 0));
        }

        startActivityForResult(intent);
    }

    /*
     * Starts new MapPreview with now rectangular Object
     */
    private void startRectangularPreview() {
        if (element instanceof PolyElement) {
            PolyElement rect = (PolyElement) element;
            if (rect.replaceNodes(MathUtil.transformIntoRectangle(rect
                    .getNodes()))) {
                mapView.getOverlays().clear();
                this.setUpOverlays();
            }
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
        finishWorkflow(data);
    }
}
