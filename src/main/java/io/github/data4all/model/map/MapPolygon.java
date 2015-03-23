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
import io.github.data4all.model.data.Tag;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import android.content.Context;
import android.content.res.Resources;
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
    // TODO
    private boolean editable;
    boolean polygonMovable = false;
    int xStart = 0;
    int yStart = 0;
    List<GeoPoint> originalPoints;
    List<Point> pointsOffset;
    List<GeoPoint> geoPointList;
    Point firstPoint;
    Projection pj;

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
            // if the touch event is inside the polygon, set polygonMovable to
            // true
            if (!polygonMovable) {
                polygonMovable = contains(event);
            }
            Log.d(TAG, "polygonMovable: " + polygonMovable);
            if (polygonMovable) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pj = mapView.getProjection();
                    // actual polygon point list
                    geoPointList = this.getPoints();
                    // get the position of the first point as the basis for
                    // moving
                    firstPoint = pj.toPixels(geoPointList.get(0), null);
                    // get the offset of all points in the list to the first one
                    pointsOffset = getOffset();

                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    Log.d(TAG, "action_down at point: " + xStart + " " + yStart);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "action_up");
                    // if polygon is movable and the touch event is an action
                    // up, set polygonMovable to false again
                    polygonMovable = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "action_move");
                    moveToNewPosition(event, mapView);
                    break;
                default:
                    Log.d(TAG, "detected another touch event");
                }
            }
            return polygonMovable;
        } else {
            return super.onTouchEvent(event, mapView);
        }
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

        //set the end coordinates of the movement
        int xEnd = (int) event.getX();
        int yEnd = (int) event.getY();

        if (pointsOffset == null) {
            pointsOffset = getOffset();
        }

        // only move the polygon if there is a movement
        if (Math.abs(xEnd - xStart) > 0 && Math.abs(yEnd - yStart) > 0) {

            Log.i(TAG, "moveMapPolygon from: " + xStart + " " + yStart);
            Log.i(TAG, "moveMapPolygon to: " + xEnd + " " + yEnd);

            // move the first point
            firstPoint.set((firstPoint.x + (xEnd - xStart)),
                    (firstPoint.y + (yEnd - yStart)));
            geoPointList.set(0, (GeoPoint) pj.fromPixels((int) firstPoint.x,
                    (int) firstPoint.y));

            // set all other points depending on the first point
            for (int i = 1; i < geoPointList.size(); i++) {
                Point newPoint = new Point();
                newPoint.set((firstPoint.x + pointsOffset.get(i).x),
                        (firstPoint.y + pointsOffset.get(i).y));
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
        Log.i(TAG, "" + originalPoints.size());
        List<Point> pointsOffset = new ArrayList<Point>();
        if (originalPoints.size() > 0) {
            Point firstPoint = pj.toPixels(originalPoints.get(0), null);
            for (int i = 0; i < originalPoints.size(); i++) {
                Point point = pj.toPixels(originalPoints.get(i), null);
                int xOffset = (point.x - firstPoint.x);
                int yOffset = (point.y - firstPoint.y);
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
