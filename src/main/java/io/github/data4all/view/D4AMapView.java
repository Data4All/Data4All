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
package io.github.data4all.view;

import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.map.MapLine;
import io.github.data4all.model.map.MapMarker;
import io.github.data4all.model.map.MapPolygon;

import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * MapView with Function to add OSM Object and set scrollable modus.
 * 
 * @author Oliver Schwartz
 */
public class D4AMapView extends MapView {

    // Default Stroke width
    protected static final float DEFAULT_STROKE_WIDTH = 3.0f;

    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);

    private boolean scrollable = true;

    private static final String TAG = "MapPreview";
    private BoundingBoxE6 boundingBox;

    /**
     * Default Constructor.
     * 
     * @param context
     *            The invoking Activity
     * 
     * @param attrs
     *            Attributes which are defined
     * 
     **/
    public D4AMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Setter for the Bounding Box of the Mapview.
     * 
     * @param boundingBox
     *            the BoundingBox to set
     **/
    public void setBoundingBox(BoundingBoxE6 boundingBox) {
        this.boundingBox = boundingBox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osmdroid.views.MapView#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(final boolean changed, final int l, final int t,
            final int r, final int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.boundingBox != null) {
            this.zoomToBoundingBox(this.boundingBox);
            this.boundingBox = null;
        }
        if (!scrollable) {
            this.setScrollableAreaLimit(this.boundingBox);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osmdroid.views.MapView#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        if (!scrollable) {
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * Adds a List of OsmElements as an Overlay to the Map.
     * 
     * @param ctx
     *            The invoking Activity
     * @param list
     *            the list of OsmElements which should be added to the map
     **/
    public void addOsmElementsToMap(AbstractActivity ctx,
            List<AbstractDataElement> list) {
        if (list != null && !list.isEmpty()) {
            for (AbstractDataElement elem : list) {
                this.addOsmElementToMap(ctx, elem, false);
            }
        }
    }

    /**
     * Adds an OsmElement as an Overlay to the Map.
     * 
     * @param ctx
     *            The invoking Activity
     * @param elem
     *            the OsmElement which should be added to the map
     * @param edit is OsmElement editable                 
     *            
     **/
    public void addOsmElementToMap(AbstractActivity ctx,
            AbstractDataElement elem, boolean edit) {
        if (elem != null) {
            // if the Element is a Node
            if (elem instanceof Node) {
                final Node node = (Node) elem;
                Log.i(TAG, "Add Node with Coordinates "
                        + node.toGeoPoint().toString());
                this.addNodeToMap(ctx, node, edit);
                // if the Element is Way
            } else if (elem instanceof PolyElement) {
                final PolyElement polyElement = (PolyElement) elem;

                // if the Element is an Path
                if (polyElement.getType() == PolyElementType.WAY) {
                    Log.i(TAG,
                            "Add Path with Coordinates "
                                    + polyElement.toString());
                    this.addPathToMap(ctx, polyElement);
                    // if the Element is an Area
                } else {
                    Log.i(TAG,
                            "Add Area with Coordinates "
                                    + polyElement.toString());
                    this.addAreaToMap(ctx, polyElement);
                }
            }
        }
    }

    /**
     * Adds an Node as an Overlay to the Map.
     *
     * @param ctx
     *            The invoking Activity
     * @param node
     *            the node which should be added to the map
     * @param edit is the node editable           
     **/
    private void addNodeToMap(AbstractActivity ctx, Node node, boolean edit) {
        final Marker poi = new MapMarker(ctx, this, node);
        Log.i(TAG, "Set Node Points to " + node.toString());
        poi.setPosition(node.toGeoPoint());
        poi.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if(edit){
            poi.setDraggable(true);
        }
        this.getOverlays().add(poi);
        this.postInvalidate();
    }

    /**
     * Adds an area as an Overlay to the Map.
     *
     * @param ctx
     *            The invoking Activity
     * @param polyElement
     *            the area which should be added to the map
     **/
    private void addAreaToMap(AbstractActivity ctx, PolyElement polyElement) {
        final Polygon area = new MapPolygon(ctx, this, polyElement);

        Log.i(TAG, "Set Area Points to " + polyElement.toString());
        area.setPoints(polyElement.getGeoPoints());

        Log.i(TAG, "Set Area Fill Color to " + DEFAULT_FILL_COLOR);
        area.setFillColor(DEFAULT_FILL_COLOR);

        Log.i(TAG, "Set Stroke Width to " + DEFAULT_STROKE_WIDTH);
        area.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        Log.i(TAG, "Set Stroke Color to " + DEFAULT_STROKE_COLOR);
        area.setStrokeColor(DEFAULT_STROKE_COLOR);

        this.getOverlays().add(area);
        this.postInvalidate();
    }

    /**
     * Adds an Path as an Overlay to the Map.
     *
     * @param ctx
     *            The invoking Activity
     * @param polyElement
     *            the path which should be added to the map
     **/
    private void addPathToMap(AbstractActivity ctx, PolyElement polyElement) {
        final Polyline path = new MapLine(ctx, this, polyElement);

        Log.i(TAG, "Set Path Points to " + polyElement.toString());
        path.setPoints(polyElement.getGeoPoints());

        Log.i(TAG, "Set Path Color to " + DEFAULT_STROKE_COLOR);
        path.setColor(DEFAULT_STROKE_COLOR);

        Log.i(TAG, "Set Path Width to " + DEFAULT_STROKE_WIDTH);
        path.setWidth(DEFAULT_STROKE_WIDTH);
        this.getOverlays().add(path);
        this.postInvalidate();
    }

    /**
     * Removes an Overlay from the Map.
     *
     * @param overlay
     *            the Overlay which should be removed from the map
     **/
    public void removeOverlayFromMap(Overlay overlay) {
        if (this.getOverlays().contains(overlay)) {
            this.getOverlays().remove(overlay);
            this.postInvalidate();
        }

    }

    /**
     * Sets Scrollable Map Modus.
     *
     * @param scrollable
     *            Scrollable Modus on or off
     **/
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;

    }

}
