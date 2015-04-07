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

import java.util.ArrayList;
import java.util.List;

import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.MapUtil;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

/**
 * With LongClick deletable Polyline.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapLine extends Polyline {

    private static final String TAG = "MapLine";
    private AbstractDataElement element;
    private AbstractActivity activity;
    private D4AMapView mapView;

    private boolean editable;

    //midpoint of the bounding box of the polyline
    Point midpoint;

    // start time for touch event action_down
    private long timeStart;

    // True when the edit mode is active.
    private boolean active = false;

    // the maximum time difference between action_down and action_up, so that
    // the mode will be changed
    private static final int TIME_DIFF = 200;

    // Default Stroke Color
    private static final int DEFAULT_STROKE_COLOR = Color.BLUE;
    // Active Stroke Color
    private static final int ACTIVE_STROKE_COLOR = Color.GREEN;
    
    // Maximum distance from the touch point to the mapline in pixel
    private static final int TOLERANCE = 20;

    /**
     * Modes for edits which differ from touch events.
     */
    private static final int NONE = 0;
    private static final int MOVE = 1;
    private static final int ROTATE = 2;
    private int mode = NONE;

    /**
     * Start values for rotation.
     */
    int xStartValue1 = 0;
    int xStartValue2 = 0;
    int yStartValue1 = 0;
    int yStartValue2 = 0;

    /**
     * Start values for moving.
     */
    private int xStart = 0;
    private int yStart = 0;

    /**
     * List of GeoPoints of the MapPolygon before it was edited.
     */
    private List<GeoPoint> originalPoints;

    /**
     * List of vectors from the first point in the MapPolygon to every point.
     */
    private List<Point> pointsOffset;

    /**
     * List of GeoPoints for editing the MapPolygon.
     */
    private List<GeoPoint> geoPointList;

    /**
     * First point of the MapPolygon in pixel coordinates.
     */
    private Point firstPoint;

    /**
     * Projection of the mapView.
     */
    private Projection pj;

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
     *            the associateded OsmElement
     */
    public MapLine(AbstractActivity ctx, D4AMapView mv, AbstractDataElement ele) {
        super(ctx);
        this.element = ele;
        this.activity = ctx;
        this.mapView = mv;
        if (activity instanceof MapViewActivity) {
            mInfoWindow = new CustomInfoWindow(this.mapView, ele, this,
                    activity);
        } else {
            mInfoWindow = null;
        }
        setInfo();
    }

    public void setInfo() {
        if (!element.getTags().keySet().isEmpty()
                && !element.getTags().values().isEmpty()) {
            Log.i(TAG, element.getTags().toString());
            Tag tag = (Tag) element.getTags().keySet().toArray()[0];
            String key = tag.getKey();
            String value = element.getTags().get(tag);
            Log.i(TAG, tag.toString());
            setTitle(activity.getString(tag.getNameRessource()));
            if (tag instanceof ClassifiedTag) {
                setSubDescription(getLocalizedName(activity, key, value));
            } else {
                setSubDescription(element.getTags().get(tag));
            }
        }
    }

    public String getLocalizedName(Context context, String key, String value) {
        Resources resources = context.getResources();
        String s = "name_" + key + "_" + value;
        int id = resources.getIdentifier(s.replace(":", "_"), "string",
                context.getPackageName());
        if (id == 0) {
            return null;
        } else {
            return resources.getString(id);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        super.onTouchEvent(event, mapView);

        if (editable) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                pj = mapView.getProjection();
                timeStart = System.currentTimeMillis();
                if (active) {
                    mode = MOVE;
                    // actual polygon point list
                    geoPointList = this.getPoints();
                    // get the offset of all points in the list to the first one
                    if (pointsOffset == null) {
                        pointsOffset = getOffset();
                    }

                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    Log.d(TAG, "action_down at point: " + xStart + " " + yStart);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "more than one pointer on screen");
                if (active) {
                    mode = ROTATE;
                    // set the start values for the rotation
                    xStartValue1 = (int) event.getX(0);
                    xStartValue2 = (int) event.getX(1);
                    yStartValue1 = (int) event.getY(0);
                    yStartValue2 = (int) event.getY(1);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "action_up");
                GeoPoint geoPoint = (GeoPoint) pj.fromPixels((int) event.getX(),
                        (int) event.getY());
                
                if (active) {
                    // set the new information to the element
                    ((PolyElement) element).setNodesFromGeoPoints(geoPointList);
                }
                if (Math.abs(timeStart - System.currentTimeMillis()) < TIME_DIFF
                        && this.isCloseTo(geoPoint, TOLERANCE, mapView)) {
                    changeMode();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "action_move");
                if (active) {
                    if (mode == MOVE) {
                        Log.d(TAG, "move polygon");
                        moveToNewPosition(event, mapView);
                    } else if (mode == ROTATE) {
                        Log.d(TAG, "rotate polygon");
                        //rotatePolygon(event);
                    }
                }
                break;
            default:
                Log.d(TAG, "detected another touch event");
            }
            return active;
        } else {
            return super.onTouchEvent(event, mapView);
        }
    }

    /**
     * change the mode whether the edit function is active or not.
     */
    public void changeMode() {
        Log.d(TAG, "actual activity mode: " + active);
        if (!active) {
            this.setColor(ACTIVE_STROKE_COLOR);
            mapView.invalidate();
            midpoint = pj.toPixels(MapUtil.getCenterFromOsmElement(element),
                    null);
            active = true;
        } else {
            this.setColor(DEFAULT_STROKE_COLOR);
            mapView.invalidate();
            active = false;
        }
    }

    /**
     * Move this polyline to the new position handling the touch events.
     * 
     * @param event
     *            the current MotionEvent from onTouchEvent
     * @param mapView
     *            the current mapView
     */
    public void moveToNewPosition(final MotionEvent event, final MapView mapView) {

        // set the end coordinates of the movement
        int xEnd = (int) event.getX();
        int yEnd = (int) event.getY();

        if (pointsOffset == null) {
            pointsOffset = getOffset();
        }

        // only move the polygon if there is a movement
        if (Math.abs(xEnd - xStart) > 0 && Math.abs(yEnd - yStart) > 0) {

            Log.i(TAG, "moveMapPolygon from: " + xStart + " " + yStart);
            Log.i(TAG, "moveMapPolygon to: " + xEnd + " " + yEnd);

            // move the midpoint
            midpoint.set((midpoint.x + (xEnd - xStart)),
                    (midpoint.y + (yEnd - yStart)));
            Log.i(TAG, "new midpoint :" + midpoint.x + " " + midpoint.y);

            // set all other points depending on the first point
            for (int i = 0; i < geoPointList.size(); i++) {
                Point newPoint = new Point();
                newPoint.set((midpoint.x + pointsOffset.get(i).x),
                        (midpoint.y + pointsOffset.get(i).y));
                geoPointList.set(i, (GeoPoint) pj.fromPixels((int) newPoint.x,
                        (int) newPoint.y));
            }
            // set new start values for the next move action
            xStart = (int) event.getX();
            yStart = (int) event.getY();

            // set the list with the changed points
            this.setPoints(geoPointList);
            mapView.invalidate();
        }
    }

    /**
     * Get the vectors to all points of the polyline starting from the first
     * point. Necessary for moving the polyline.
     * 
     * @return List with all vectors
     */
    public List<Point> getOffset() {
        Log.i(TAG, "number of points in the polygon: " + originalPoints.size());
        List<Point> pointsOffset = new ArrayList<Point>();

        if (originalPoints.size() > 0) {
            for (int i = 0; i < originalPoints.size(); i++) {
                Point point = pj.toPixels(originalPoints.get(i), null);
                int xOffset = (point.x - midpoint.x);
                int yOffset = (point.y - midpoint.y);
                pointsOffset.add(new Point(xOffset, yOffset));
            }
        }
        return pointsOffset;
    }

    /**
     * Set the points of the polyline. Called when the polyline is added to the
     * map.
     */
    public void setOriginalPoints() {
        this.originalPoints = this.getPoints();
    }

    /**
     * Setter for editable. Set whether the polyline is editable.
     * 
     * @param editable
     *            true if polyline is editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
