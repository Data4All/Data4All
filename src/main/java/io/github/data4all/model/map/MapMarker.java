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
package io.github.data4all.model.map;

import io.github.data4all.R;
import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.MapPreviewActivity;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.Tagging;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ZoomControls;

/**
 * Map Marker which has an InfoWindow and is movable.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapMarker extends Marker {

    private static final String TAG = "MapMarker";
    private D4AMapView mapView;
    private AbstractActivity activity;
    private AbstractDataElement element;
    private boolean editable;
    // start time for touch event action_down
    private long timeStart;

    // True when the edit mode is active.
    private boolean active = false;

    // the maximum time difference between action_down and action_up, so that
    // the mode will be changed
    private static final int TIME_DIFF = 200;

    /**
     * Point representing the marker.
     */
    private GeoPoint point;

    /**
     * Modes for edits which differ from touch events.
     */
    private static final int NONE = 0;
    private static final int MOVE = 1;
    private int mode = NONE;

    /**
     * Start values for moving.
     */
    private int xStart = 0;
    private int yStart = 0;

    /**
     * Projection of the mapView.
     */
    private Projection pj;

    /**
     * MapCenter values.
     */
    private GeoPoint newMapcenter;
    private GeoPoint oldMapcenter;

    /**
     * Default constructor.
     * 
     * @param ctx
     *            the Context for the Overlay
     * 
     * @param mv
     *            the Mapview
     * 
     * @param ele
     *            the associated OsmElement
     */
    public MapMarker(AbstractActivity ctx, D4AMapView mv,
            AbstractDataElement ele) {
        super(mv, new DefaultResourceProxyImpl(mv.getContext()));
        this.element = ele;
        this.activity = ctx;
        this.mapView = mv;
        this.editable = false;
        setIcon(ctx.getResources().getDrawable(R.drawable.ic_setpoint_blue));
        if (activity instanceof MapViewActivity) {
            mInfoWindow = new CustomInfoWindow(this.mapView, ele, this,
                    activity);
        } else {
            mInfoWindow = null;
        }
}

    @Override
    public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        super.onTouchEvent(event, mapView);

        if (editable) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                timeStart = System.currentTimeMillis();
                if (active) {
                    mode = MOVE;
                    pj = mapView.getProjection();
                    // actual GeoPoint
                    point = this.getPosition();
                    oldMapcenter = (GeoPoint) mapView.getMapCenter();
                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    Log.d(TAG, "action_down at point: " + xStart + " " + yStart);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "action_up");
                if (active) {
                    // set the new information to the element
                    ((Node) element).setLat(getPosition().getLatitude());
                    ((Node) element).setLon(getPosition().getLongitude());
                }
                if (Math.abs(timeStart - System.currentTimeMillis()) < TIME_DIFF
                        && hitTest(event, mapView)) {
                    this.changeMode();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "action_move");
                if (active && mode == MOVE) {
                    Log.d(TAG, "move marker");
                    this.moveToNewPosition(event);

                }
                break;
            default:
                Log.d(TAG, "detected another touch event");
            }
            return active;
        } else {
            if (mInfoWindow != null && mInfoWindow.isOpen()) {
                mInfoWindow.close();
            }
            return super.onTouchEvent(event, mapView);
        }
    }

    /**
     * Move this Marker to the new position handling the touch events.
     * 
     * @param event
     *            the current MotionEvent from onTouchEvent
     */
    public void moveToNewPosition(final MotionEvent event) {

        // set the end coordinates of the movement
        final int xEnd = (int) event.getX();
        final int yEnd = (int) event.getY();

        int x = xEnd - xStart;
        int y = yEnd - yStart;
        pj = mapView.getProjection();
        Point marker = pj.toPixels(point, null);
        marker = new Point(marker.x + x, marker.y + y);

        Point center = pj.toPixels(oldMapcenter, null);
        center = new Point(center.x + x, center.y + y);
        newMapcenter = (GeoPoint) pj.fromPixels(center.x, center.y);

        this.setPosition((GeoPoint) pj.fromPixels((int) marker.x,
                (int) marker.y));

        mapView.invalidate();
    }

    /**
     * change the mode whether the edit function is active or not.
     */
    public void changeMode() {
        Log.d(TAG, "actual activity mode: " + active);
        if (!active) {
            setIcon(activity.getResources().getDrawable(R.drawable.ic_setpoint));
            mapView.invalidate();
            active = true;
        } else {
            setIcon(activity.getResources().getDrawable(
                    R.drawable.ic_setpoint_red));
            mapView.invalidate();
            active = false;
        }
        if (activity instanceof MapPreviewActivity) {
            ZoomControls zoomControls = (ZoomControls) activity
                    .findViewById(R.id.zoomcontrols);
            int v = active ? View.VISIBLE : View.GONE;
            zoomControls.setVisibility(v);
        }
    }

    /**
     * Setter for editable. Set whether the Marker is editable.
     * 
     * @param editable
     *            true if polygon is editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    protected boolean onMarkerClickDefault(Marker marker, MapView mapView) {
        if (!editable) {
        marker.showInfoWindow();
        mapView.getController().animateTo(marker.getPosition());
        return true;
        } else {
            return super.onMarkerClickDefault(marker, mapView);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osmdroid.bonuspack.overlays.Polygon#setPoints(java.util.List)
     */
    @Override
    public void setPosition(final GeoPoint point) {
        super.setPosition(point);
        if (active) {
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            final Resources res = activity.getResources();
            final String key = res
                    .getString(R.string.pref_moving_animation_key);
            if ("Animate".equals(prefs.getString(key, null))) {
                mapView.getController().animateTo(newMapcenter);
            } else {
                mapView.getController().setCenter(newMapcenter);
            }
            mapView.postInvalidate();
        }
    }
}
