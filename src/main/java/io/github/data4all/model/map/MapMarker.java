/* 
 * Copyright (c) 2014, 2015 Data4All
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.map;

import io.github.data4all.R;
import io.github.data4all.activity.MapActivity;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;

/**
 * With LongClick deletable Map Marker.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapMarker extends Marker implements
        DialogInterface.OnClickListener {

    private MapActivity mapActivity;

    /**
     * Default constructor.
     * 
     * @param ctx
     *            the Context for the Overlay
     */
    public MapMarker(MapActivity ctx, MapView mv) {
        super(mv, new DefaultResourceProxyImpl(mv.getContext()));
        mapActivity = ctx;

    }

    @Override
    public boolean onLongPress(final MotionEvent e, final MapView mapView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                mapView.getContext());
        builder.setMessage(mapActivity.getString(R.string.deleteDialog))
                .setPositiveButton(mapActivity.getString(R.string.yes), this)
                .setNegativeButton(mapActivity.getString(R.string.no), this)
                .show();

        return true;

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            // Yes button clicked
            mapActivity.removeOverlayFromMap(this);
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            break;
        default:
            break;
        }
    }
}
