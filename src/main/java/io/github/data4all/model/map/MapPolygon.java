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
import android.view.MotionEvent;

/**
 * With LongClick deletable Polygon.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapPolygon extends Polygon {

    private static final String TAG = "MapPolygon";
    private AbstractActivity activity;
    private D4AMapView mapView;
    private AbstractDataElement element;
    private boolean editable;

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
        boolean tapped = contains(event);
        if (editable && tapped) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                // mIsDragged = false;
                // if (mOnMarkerDragListener != null)
                // mOnMarkerDragListener.onMarkerDragEnd(this);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                moveToEventPosition(event, mapView);
                return true;
            } else
                return false;
        } else
            return false;
    }

    public void moveToEventPosition(final MotionEvent event,
            final MapView mapView) {
        Log.i(TAG, "moveMapPolygon");
        final Projection pj = mapView.getProjection();
        // actual Polygon point list
        List<GeoPoint> gpointListOld = this.getPoints();
        Log.d(TAG, gpointListOld.get(0).getLatitude() + " "
                + gpointListOld.get(0).getLongitude());
        // new Polygon point list
        List<GeoPoint> gpointList = new ArrayList<GeoPoint>(
                gpointListOld.size());
        GeoPoint gpoint = (GeoPoint) pj.fromPixels((int) event.getX(),
                (int) event.getY());
        Log.i(TAG, "size before" + gpointListOld.size());
        
        gpointListOld.add(gpoint);
        this.setPoints(gpointListOld);
        Log.i(TAG, "size after, should be one more..." + this.getPoints().size());
        
        // for (GeoPoint gpoint : gpointListOld) {
        // pj.toProjectedPixels(gpoint, null)
        // gpoint = (GeoPoint) pj.fromPixels((int) event.getX(),
        // (int) event.getY());
        // gpointList.add(gpoint);
        // }
        //this.setPoints(gpointList);
//        Log.d(TAG, gpointList.get(0).getLatitude() + " "
//                + gpointList.get(0).getLongitude());

        mapView.invalidate();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
