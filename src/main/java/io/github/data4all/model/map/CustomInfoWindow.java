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
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.MapUtil;

import java.util.List;

import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.OverlayWithIW;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * InfoWindow which shows Information about an OsmElement and allows to delete
 * or edit the Element.
 * 
 * @author Oliver Schwartz
 *
 */
public class CustomInfoWindow extends BasicInfoWindow implements
        OnClickListener, DialogInterface.OnClickListener {

    private static final String TYPE = "TYPE_DEF";
    private static final int POINT = 1;
    private static final int BUILDING = 3;
    private static final int WAY = 2;
    private static final int AREA = 4;
    private static final String OSM_ELEMENT = "OSM_ELEMENT";

    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);

    // Default Marked Color
    protected static final int MARKED_STROKE_COLOR = Color.RED;

    // Fill Color for Polygons
    protected static final int MARKED_FILL_COLOR = Color.argb(100, 255, 0, 0);

    private static final String TAG = "CustomInfoWindow";

    protected AbstractDataElement element;
    protected OverlayWithIW overlay;
    protected AbstractActivity activity;

    /**
     * Constructor for an InfoBubble on an given Overlay, Element and MapView.
     * Invoked by the given activity.
     * 
     * @param mapView
     *            the current mapView
     * @param the
     *            element to this InfoWindow
     * @param overlay
     *            the overlay with this InfoWindow
     * @param activity
     *            the context activity
     **/
    public CustomInfoWindow(MapView mapView, AbstractDataElement element,
            OverlayWithIW overlay, AbstractActivity activity) {
        super(R.layout.bonuspack_bubble, mapView);

        this.activity = activity;
        this.element = element;
        this.overlay = overlay;
        this.setInfo();
        int id = R.id.bubble_delete;
        final Button delete = (Button) mView.findViewById(id);
        delete.setOnClickListener(this);

        id = R.id.bubble_edit;
        final Button edit = (Button) mView.findViewById(id);
        edit.setOnClickListener(this);

        id = R.id.bubble_left;
        final ImageButton left = (ImageButton) mView.findViewById(id);
        left.setOnClickListener(this);

        id = R.id.bubble_right;
        final ImageButton right = (ImageButton) mView.findViewById(id);
        right.setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osmdroid.bonuspack.overlays.BasicInfoWindow#onClose()
     */
    @Override
    public void onClose() {
        super.onClose();
        if (overlay instanceof MapPolygon) {
            final MapPolygon poly = (MapPolygon) overlay;
            poly.setFillColor(DEFAULT_FILL_COLOR);
            poly.setStrokeColor(DEFAULT_STROKE_COLOR);
        } else if (overlay instanceof MapLine) {
            final MapLine line = (MapLine) overlay;
            line.setColor(DEFAULT_STROKE_COLOR);
        } else if (overlay instanceof MapMarker) {
            final MapMarker marker = (MapMarker) overlay;
            marker.setIcon(activity.getResources().getDrawable(
                    R.drawable.ic_setpoint_blue));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bubble_delete:
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(mMapView.getContext());
            builder.setMessage(activity.getString(R.string.deleteDialog))
                    .setPositiveButton(activity.getString(R.string.yes), this)
                    .setNegativeButton(activity.getString(R.string.no), this)
                    .show();
            break;
        case R.id.bubble_edit:
            this.editElement();
            close();
            break;
        case R.id.bubble_left:
            this.left();
            break;
        case R.id.bubble_right:
            this.right();
            break;
        default:
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osmdroid.bonuspack.overlays.BasicInfoWindow#onOpen(java.lang.Object)
     */
    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        mMapView.getController().animateTo(
                MapUtil.getCenterFromOsmElement(element));
        if (overlay instanceof MapPolygon) {
            final MapPolygon poly = (MapPolygon) overlay;
            poly.setFillColor(MARKED_FILL_COLOR);
            poly.setStrokeColor(MARKED_STROKE_COLOR);
        } else if (overlay instanceof MapLine) {
            final MapLine line = (MapLine) overlay;
            line.setColor(MARKED_STROKE_COLOR);
        } else if (overlay instanceof MapMarker) {
            final MapMarker marker = (MapMarker) overlay;
            marker.setIcon(activity.getResources().getDrawable(
                    R.drawable.ic_setpoint_red));
        }
    }

    /**
     * Open InfoWindow for the last known OverlayWithIW
     **/
    public void left() {
        final List<Overlay> list = mMapView.getOverlays();
        int i = list.indexOf(overlay);
        Log.d(TAG, "LISTNUMBER " + i);
        Overlay next;
        do {
            if (i == 0) {
                i = list.size() - 1;
            } else {
                i -= 1;
            }
            next = list.get(i);
        } while (!((next instanceof MapLine) || (next instanceof MapPolygon) || (next instanceof MapMarker)));
        if (next instanceof MapMarker) {
            ((MapMarker) next).showInfoWindow();
        } else {
            final CustomInfoWindow nextWindow =
                    (CustomInfoWindow) ((OverlayWithIW) next).getInfoWindow();
            final GeoPoint position =
                    MapUtil.getCenterFromOsmElement(nextWindow.element);
            nextWindow.open(next, position, 0, 0);
        }
    }

    /**
     * Open InfoWindow for the next known OverlayWithIW
     **/
    public void right() {
        final List<Overlay> list = mMapView.getOverlays();
        int i = list.indexOf(overlay);
        Overlay next;
        do {
            if (i == list.size() - 1) {
                i = 0;
            } else {
                i += 1;
            }
            next = list.get(i);
        } while (!((next instanceof MapLine) || (next instanceof MapPolygon) || (next instanceof MapMarker)));
        if (next instanceof MapMarker) {
            ((MapMarker) next).showInfoWindow();
        } else {
            final CustomInfoWindow nextWindow =
                    (CustomInfoWindow) ((OverlayWithIW) next).getInfoWindow();
            final GeoPoint position =
                    MapUtil.getCenterFromOsmElement(nextWindow.element);
            nextWindow.open(next, position, 0, 0);
        }
    }

    /**
     * Set the info of the MapPolyline for the InfoWindow.
     */
    public void setInfo() {
        if (!element.getTags().keySet().isEmpty()
                && !element.getTags().values().isEmpty()) {
            Log.i(TAG, element.getTags().toString());
            final Tag tag = (Tag) element.getTags().keySet().toArray()[0];
            final String value = element.getTags().get(tag);
            Log.i(TAG, tag.toString());
            overlay.setTitle(tag.getNamedKey(activity));
            final String classifiedValue = tag.getNamedValue(activity, value);
            if (classifiedValue == null) {
                overlay.setSubDescription(element.getTags().get(tag));
            } else {
                overlay.setSubDescription(tag.getNamedValue(activity, value));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.content.DialogInterface.OnClickListener#onClick(android.content
     * .DialogInterface, int)
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            // Yes button clicked
            mMapView.getOverlays().remove(overlay);
            final DataBaseHandler db = new DataBaseHandler(activity);
            db.deleteDataElement(element);
            db.close();
            mMapView.postInvalidate();
            close();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            break;
        default:
            break;
        }
    }

    /**
     * Invoke ResultViewActivity to edit the Tags of the given Element.
     **/
    private void editElement() {
        final Intent intent = new Intent(activity, MapPreviewActivity.class);
        int type = -1;
        if (element instanceof Node) {
            type = POINT;
        } else if (element instanceof PolyElement) {
            final PolyElement polyElement = (PolyElement) element;
            if (polyElement.getType() == PolyElementType.WAY) {
                type = WAY;
            } else if (polyElement.getType() == PolyElementType.AREA) {
                type = AREA;
            } else if (polyElement.getType() == PolyElementType.BUILDING) {
                type = BUILDING;
            }
        }

        // Set Type Definition for Intent
        Log.i(TAG, "Set intent extra " + TYPE + " to " + type);
        intent.putExtra(TYPE, type);

        intent.putExtra(OSM_ELEMENT, element);
        Log.i(TAG, element.getTags().toString());
        // Start ResultView Activity
        Log.i(TAG, "Start ResultViewActivity");
        activity.startActivityForResult(intent, 0);
    }
}
