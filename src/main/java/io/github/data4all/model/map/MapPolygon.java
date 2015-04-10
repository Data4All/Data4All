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

import io.github.data4all.R;
import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.MapUtil;
import io.github.data4all.util.MathUtil;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

/**
 * Polygon which is editable. It has an InfoWindow opened with a single tap, if
 * it is not editable and it is movable and rotatable with a TouchEvent, if it
 * is editable.
 * 
 * @author Oliver Schwartz
 * @author sbollen
 * @author burghardt
 *
 */
public class MapPolygon extends Polygon {

    private static final String TAG = "MapPolygon";
    private AbstractActivity activity;
    private D4AMapView mapView;
    private AbstractDataElement element;
    private boolean editable;


    // start time for touch event action_down
    private long timeStart;

    // True when the edit mode is active
    private boolean active;

    // the maximum time difference between action_down and action_up, so that
    // the mode will be changed
    private static final int TIME_DIFF = 200;

    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;
    // Active Stroke Color
    protected static final int ACTIVE_STROKE_COLOR = Color.GREEN;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);
    // Fill Color for activated Polygons
    protected static final int ACTIVE_FILL_COLOR = Color.argb(100, 50, 255, 50);

    // Maximum distance from the touch point to the mapline in pixel
    private static final int TOLERANCE = 1;

    /**
     * Modes for edits which differ from touch events.
     */
    private static final int NONE = 0;
    private static final int MOVE = 1;
    private static final int ROTATE = 2;
    private int mode = NONE;

    /**
     * Start values for any movements.
     */
    private List<Point> startPos;

    /**
     * List of the geopoints in a coordinate system with the center as the
     * average of all Points.
     */
    private List<double[]> pointCoords;
    
    /**
     * the Perimeter of the 3 Startpoints to scale 
     */
    private float startPosPerimeter;

    /**
     * Average of all Points saved as a Location.
     */
    private Location midLocation;



    /**
     * List of GeoPoints for editing the MapPolygon.
     */
    private List<GeoPoint> geoPointList;

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
     *            the associated OsmElement
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
            final Tag tag = (Tag) element.getTags().keySet().toArray()[0];
            final String key = tag.getKey();
            final String value = element.getTags().get(tag);
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
     *            the context of the application
     * @param key
     *            the tag key
     * @param value
     *            the tag value
     * @return the localized name
     */
    public String getLocalizedName(Context context, String key, String value) {
        final Resources resources = context.getResources();
        final String s = "name_" + key + "_" + value;
        final int id = resources.getIdentifier(s.replace(":", "_"), "string",
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
                    geoPointList = this.getPoints();
                    startPos = new ArrayList<Point>();
                    startPos.add(new Point((int) event.getX(0), (int) event
                            .getY(0)));;
                    Log.d(TAG, "action_down at point: " + event.getX(0) + " "
                            + event.getY(0));
                    Log.d("TEST", "Startpoints");
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "more than one pointer on screen");
                if (active) {
                    mode = ROTATE;
                    // set the start values for the rotation
                    saveGeoPoints();
                    startPos = new ArrayList<Point>();
                    startPos.add(new Point((int) event.getX(0), (int) event
                            .getY(0)));
                    startPos.add(new Point((int) event.getX(1), (int) event
                            .getY(1)));
                    if (event.getPointerCount() == 3) {
                        startPos.add(new Point((int) event.getX(2), (int) event
                                .getY(2)));
                        startPosPerimeter = MathUtil.perimeter(startPos);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "action_up");
                if (active) {
                    // set the new information to the element
                    ((PolyElement) element).setNodesFromGeoPoints(geoPointList);
                }
                if (Math.abs(timeStart - System.currentTimeMillis()) < TIME_DIFF
                        && isTapped(event)) {
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
                        this.moveToNewPos(event);
                    } else if (mode == ROTATE) {
                        if (event.getPointerCount() == 3) {
                            Log.d(TAG, "scale polygon");
                            this.scalePolygon(event);
                        } else {
                            Log.d(TAG, "rotate polygon");
                            this.rotatePolygon(event);
                        }
                    }
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
     * change the mode whether the edit function is active or not.
     */
    public void changeMode() {
        if (!active) {
            // change mode to active, polygon is now rotatable and movable
            this.setFillColor(ACTIVE_FILL_COLOR);
            this.setStrokeColor(ACTIVE_STROKE_COLOR);
            pj = mapView.getProjection();
            mapView.invalidate();
            active = true;
        } else {
            // change mode to not active, polygon is not modifiable now
            this.setFillColor(CustomInfoWindow.MARKED_FILL_COLOR);
            this.setStrokeColor(CustomInfoWindow.MARKED_STROKE_COLOR);
            mapView.invalidate();
            active = false;
        }
        Log.d(TAG, "actual activity mode: " + active);
        mapView.setBuiltInZoomControls(active);
    }

    /**
     * Move this polygon to the new position handling the touch events. Move the
     * midpoint of the bounding box of the polygon and after that add the offset
     * of all points of the polygon to the new midpoint.
     * 
     * @param event
     *            the current MotionEvent from onTouchEvent
     * @param mapView
     *            the current mapView
     */
    public void moveToNewPos(final MotionEvent event) {
        // set the end coordinates of the movement
        Point endPoint = new Point((int) event.getX(0), (int) event
                .getY(0));
        int x = endPoint.x - startPos.get(0).x ;
        int y = endPoint.y - startPos.get(0).y ;
        pj = mapView.getProjection();
        List<GeoPoint> returnList = new ArrayList<GeoPoint>();
        for(GeoPoint geoPoint : geoPointList){
            Point point = pj.toPixels(geoPoint, null);
            point = new Point(point.x + x, point.y +y);
            returnList.add((GeoPoint) pj.fromPixels(point.x, point.y));
        }
        super.setPoints(returnList);
        mapView.invalidate();
    }

    /**
     * Scales the given Polygon with a 3 Pointer MotionEvent
     * 
     * @param event
     *            the current MotionEvent from onTouchEvent
     */
    private void scalePolygon(MotionEvent event) {
        // set end values for the next rotation action
        List<Point> endPos = new ArrayList<Point>();
        endPos.add(new Point((int) event.getX(0), (int) event.getY(0)));
        endPos.add(new Point((int) event.getX(1), (int) event.getY(1)));
        endPos.add(new Point((int) event.getX(2), (int) event.getY(2)));
        float scaleFactor = MathUtil.perimeter(endPos) / startPosPerimeter;
        geoPointList = new ArrayList<GeoPoint>();
        // rotate all coordinates
        for (double[] preCoord : pointCoords) {
            double[] coord = new double[2];
            coord[0] = preCoord[0] * scaleFactor;
            coord[1] = preCoord[1] * scaleFactor;
            // transfer coordinates to gpsPoints
            final Node node = MathUtil.calculateGPSPoint(midLocation, coord);
            geoPointList.add(new GeoPoint(node.getLat(), node.getLon()));
        }
        // set the list with the changed points
        if (MapUtil.getBoundingBoxForPointList(geoPointList)
                .getDiagonalLengthInMeters() < 100 || scaleFactor < 1) {
            super.setPoints(geoPointList);
            mapView.invalidate();
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
        final int xEndPo1 = (int) event.getX(0);
        final int xEndPo2 = (int) event.getX(1);

        final int yEndPo1 = (int) event.getY(0);
        final int yEndPo2 = (int) event.getY(1);

        // get the rotation angle
        final double delta_xEnd = (xEndPo1 - xEndPo2);
        final double delta_yEnd = (yEndPo1 - yEndPo2);
        final double radians1 = Math.atan2(delta_yEnd, delta_xEnd);

        final double delta_xStart = startPos.get(0).x - startPos.get(1).x;
        final double delta_yStart = startPos.get(0).y - startPos.get(1).y;
        final double radians2 = Math.atan2(delta_yStart, delta_xStart);
        final double radians = radians1 - radians2;

        geoPointList = new ArrayList<GeoPoint>();
        // rotate all coordinates
        for (double[] preCoord : pointCoords) {
            double[] coord = new double[2];
            coord[1] = preCoord[1] * Math.cos(radians) - preCoord[0]
                    * Math.sin(radians);
            coord[0] = preCoord[1] * Math.sin(radians) + preCoord[0]
                    * Math.cos(radians);
            // transfer coordinates to gpsPoints
            final Node node = MathUtil.calculateGPSPoint(midLocation, coord);
            geoPointList.add(new GeoPoint(node.getLat(), node.getLon()));
        }
        // set the list with the changed points
        super.setPoints(geoPointList);
        mapView.invalidate();
    }

  

    /**
     * Set the original points of the polygon. Called when the polygon is added
     * to the map.
     */
    public void setOriginalPoints() {
        geoPointList = this.getPoints();
    }

    /**
     * Saves all Points in a different coordinate system and saves the Location
     * of this system.
     */
    public void saveGeoPoints() {
        double lat = 0, lon = 0;
        int i = 0;
        for (GeoPoint geoPoint : this.getPoints()) {
            lat += geoPoint.getLatitude();
            lon += geoPoint.getLongitude();
            i++;
        }
        this.midLocation = new Location("provide");
        this.midLocation.setLatitude(lat / i);
        this.midLocation.setLongitude(lon / i);
        this.pointCoords = new ArrayList<double[]>();
        for (GeoPoint geoPoint : this.getPoints()) {
            final double[] preCoord = MathUtil
                    .calculateCoordFromGPS(
                            midLocation,
                            new Node(0, geoPoint.getLatitude(), geoPoint
                                    .getLongitude()));
            pointCoords.add(preCoord);
        }
    }

    /**
     * Set whether the polygon is editable.
     * 
     * @param editable
     *            true if polygon is editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent event,
            final MapView mapView) {
        if (!editable) {
            if (mInfoWindow == null) {
                // no support for tap:
                return false;
            }
            final boolean isTapped = isTapped(event);
            if (isTapped) {
                mInfoWindow.open(this,
                        MapUtil.getCenterFromOsmElement(element), 0, 0);
                mapView.getController().animateTo(
                        MapUtil.getCenterFromOsmElement(element));
            }
            return isTapped;
        } else {
            return super.onSingleTapConfirmed(event, mapView);
        }
    }

    public boolean isTapped(MotionEvent event) {
        if (contains(event)) {
            return true;
        } else {
            final Projection pj = mapView.getProjection();
            final GeoPoint position = (GeoPoint) pj.fromPixels(
                    (int) event.getX(), (int) event.getY());
            final int distToCent = position.distanceTo(MapUtil
                    .getCenterFromOsmElement(element));
            return (distToCent < TOLERANCE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osmdroid.bonuspack.overlays.Polygon#setPoints(java.util.List)
     */
    @Override
    public void setPoints(final List<GeoPoint> points) {
        super.setPoints(points);
        if (active) {
            final GeoPoint center = MapUtil.getCenterFromPointList(points);
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            final Resources res = activity.getResources();
            final String key = res
                    .getString(R.string.pref_moving_animation_key);
            if ("Animate".equals(prefs.getString(key, null))) {
                mapView.getController().animateTo(center);
            } else {
                mapView.getController().setCenter(center);
            }
            mapView.postInvalidate();
        }
    }
}
