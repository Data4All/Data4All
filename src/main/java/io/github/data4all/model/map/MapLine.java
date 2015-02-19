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
import io.github.data4all.activity.BasicActivity;
import io.github.data4all.activity.MapActivity;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.views.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;

/**
 * With LongClick deletable Polyline.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapLine extends Polyline implements
        DialogInterface.OnClickListener {

    private AbstractDataElement element;
    private BasicActivity activity;
    private D4AMapView mapView;
    
    /**
     * Default constructor.
     * 
     * @param ctx
     *            the Context for the Overlay
     */
    public MapLine(BasicActivity ctx, D4AMapView mv, AbstractDataElement ele ) {
        super(ctx);
        this.element = ele;
        this.activity = ctx;
        this.mapView = mv;

    }

    /*
     * (non-Javadoc)
     * @see org.osmdroid.views.overlay.Overlay#onLongPress(android.view.MotionEvent, org.osmdroid.views.MapView)
     */
    @Override
    public boolean onLongPress(final MotionEvent e, final MapView mapView) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(mapView.getContext());
        builder.setMessage(activity.getString(R.string.deleteDialog))
                .setPositiveButton(activity.getString(R.string.yes), this)
                .setNegativeButton(activity.getString(R.string.no), this)
                .show();

        return true;

    }

    /*
     * (non-Javadoc)
     * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            // Yes button clicked
            mapView.removeOverlayFromMap(this);
            if(activity instanceof MapViewActivity){
                DataBaseHandler db = new DataBaseHandler(activity);
                db.deleteDataElement(element);
                db.close();
            }
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            break;
        default:
            break;
        }
    }
}
