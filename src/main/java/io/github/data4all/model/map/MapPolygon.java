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
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.MapUtil;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Polygon which is editable. It is deletable with a LongClick and movable with
 * a TouchEvent.
 * 
 * @author Oliver Schwartz
 * @author sbollen
 *
 */
public class MapPolygon extends Polygon {

    private static final String TAG = "MapPolygon";
    private AbstractActivity activity;
    private D4AMapView mapView;
    private AbstractDataElement element;
    private boolean editable;
    
    //midpoint of the bounding box of the polygon
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

    // Fill Color for Polygons
    private static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);
    // Fill Color for activated Polygons
    private static final int ACTIVE_FILL_COLOR = Color.argb(100, 50, 255, 50);

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
    public MapPolygon(AbstractActivity ctx, D4AMapView mv,
            AbstractDataElement ele) {
        super(ctx);
        this.element = ele;
        this.activity = ctx;
        this.mapView = mv;
        this.editable = false;
        if (activity instanceof MapViewActivity) {
            mInfoWindow = new CustomInfoWindow(this.mapView, ele, this,
                    activity);
        } else {
            mInfoWindow = null;
        }
        setInfo();
    }

    /**
     * Set the info of the MapPolygon for the InfoWindow.
     */
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

    /**
     * Get the localized name of the element to show in the InfoWindow.
     * 
     * @param context
     * @param key
     * @param value
     * @return
     */
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
                if (active) {
                    // set the new information to the element
                    ((PolyElement) element).setNodesFromGeoPoints(geoPointList);
                }
                if (Math.abs(timeStart - System.currentTimeMillis()) < TIME_DIFF
                        && contains(event)) {
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
                        rotatePolygon(event);
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
            this.setFillColor(ACTIVE_FILL_COLOR);
            this.setStrokeColor(ACTIVE_STROKE_COLOR);
            mapView.invalidate();
            midpoint = pj.toPixels(MapUtil.getCenterFromOsmElement(element),
                    null);
            active = true;
        } else {
            this.setFillColor(DEFAULT_FILL_COLOR);
            this.setStrokeColor(DEFAULT_STROKE_COLOR);
            mapView.invalidate();
            active = false;
        }
    }

    /**
     * Rotate the polygon handling the touch events.
     *
     * @param event
     *            the actual touch event
     */
    private void rotatePolygon(MotionEvent event) {
        // set end values for the next rotation action
        int xEndValue1 = (int) event.getX(0);
        int xEndValue2 = (int) event.getX(1);

        int yEndValue1 = (int) event.getY(0);
        int yEndValue2 = (int) event.getY(1);

        // get the rotation angle
        double delta_xEnd = (xEndValue1 - xEndValue2);
        double delta_yEnd = (yEndValue1 - yEndValue2);
        double radians1 = Math.atan2(delta_yEnd, delta_xEnd);

        double delta_xStart = (xStartValue1 - xStartValue2);
        double delta_yStart = (yStartValue1 - yStartValue2);
        double radians2 = Math.atan2(delta_yStart, delta_xStart);

        double radians = radians1 - radians2;
        Log.d(TAG, "Rotation in radians: " + radians);

        // Get the midpoint of the element to rotate around this point.
        Point midpoint = pj.toPixels(MapUtil.getCenterFromOsmElement(element),
                null);
        Log.i(TAG, "midpoint of element: " + midpoint.x + " " + midpoint.y);

        // Get the sin and cos of the rotation angle
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);

        // translate point back to origin:
        firstPoint.x -= midpoint.x;
        firstPoint.y -= midpoint.y;

        // rotate point
        float xnew = firstPoint.x * cos - firstPoint.y * sin;
        float ynew = firstPoint.x * sin + firstPoint.y * cos;

        // translate point back:
        firstPoint.x = (int) xnew + midpoint.x;
        firstPoint.y = (int) ynew + midpoint.y;

        geoPointList.set(0, (GeoPoint) pj.fromPixels((int) firstPoint.x,
                (int) firstPoint.y));

        // set all other points depending on the first point
        for (int i = 1; i < geoPointList.size(); i++) {
            Point newPoint = new Point();
            Point offset = pointsOffset.get(i);
            // calculate new offset with rotation angle
            int xOffset = (int) (offset.x * cos - offset.y * sin);
            int yOffset = (int) (offset.x * sin + offset.y * cos);
            offset.set(xOffset, yOffset);
            // TODO problem: offset is changing and therefore the polygon is
            // changing
            pointsOffset.set(i, offset);

            newPoint.set((firstPoint.x + xOffset), (firstPoint.y + yOffset));
            geoPointList.set(i, (GeoPoint) pj.fromPixels((int) newPoint.x,
                    (int) newPoint.y));
        }

        // set new start values for the next rotation action
        xStartValue1 = (int) event.getX(0);
        xStartValue2 = (int) event.getX(1);

        yStartValue1 = (int) event.getY(0);
        yStartValue2 = (int) event.getY(1);

        // set the list with the changed points
        this.setPoints(geoPointList);
        mapView.invalidate();
    }

    /**
     * Move this polygon to the new position handling the touch events.
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
     * Get the vectors to all points of the polygon starting from the first
     * point. Necessary for moving the polygon.
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
     * Set the points of the polygon. Called when the polygon is added to the
     * map.
     */
    public void setOriginalPoints() {
        this.originalPoints = this.getPoints();
    }

    /**
     * Setter for editable. Set whether the polygon is editable.
     * 
     * @param editable
     *            true if polygon is editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
