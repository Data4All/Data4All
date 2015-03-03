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
import java.util.Map.Entry;

import io.github.data4all.R;
import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.util.Tagging;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.InfoWindow;
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
public class MapMarker extends Marker {

    private static final String TAG = "MapMarker";
    private D4AMapView mapView;
    private AbstractActivity activity;
    private AbstractDataElement element;

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
    public MapMarker(AbstractActivity ctx, D4AMapView mv,
            AbstractDataElement ele) {
        super(mv, new DefaultResourceProxyImpl(mv.getContext()));
        this.element = ele;
        this.activity = ctx;
        this.mapView = mv;
        setIcon(ctx.getResources().getDrawable(R.drawable.ic_setpoint));
        if(activity instanceof MapViewActivity){
            mInfoWindow = new CustomInfoWindow(this.mapView, ele, this, activity);
        }else {
            mInfoWindow = null;
        }
        setInfo();
    }
    
    public void setInfo() {
        ArrayList<String> endList = new ArrayList<String>();
        ArrayList<String> keyList = new ArrayList<String>();
        final List<Tag> tagList = new ArrayList<Tag>();
        for (Entry entry : element.getTags().entrySet()) {
            final Tag tagKey = (Tag) entry.getKey();
            tagList.add(tagKey);
            Log.i(TAG, tagKey.getKey());
            keyList.add(activity.getResources().getString(
                    tagKey.getNameRessource()));
            if (Tagging.isClassifiedTag(
                    activity.getString(tagKey.getNameRessource()),
                    activity.getResources())) {
                try {
                    endList.add(activity.getString((Integer) R.string.class
                            .getDeclaredField(
                                    "name_"
                                            + tagKey.getKey()
                                            + "_"
                                            + element.getTags().get(tagKey)
                                                    .replaceAll(":", "_")).get(
                                    null)));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "", e);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "", e);
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, "", e);
                }
            } else {
                endList.add(element.getTags().get(tagKey));
            }
        }
        
        if(!endList.isEmpty() && !keyList.isEmpty()){
            setTitle(keyList.get(0));
            setSubDescription(endList.get(0));
        }
    }

//     /*
//     * (non-Javadoc)
//     *
//     * @see
//     *
//     org.osmdroid.bonuspack.overlays.Marker#onLongPress(android.view.MotionEvent
//     * , org.osmdroid.views.MapView)
//     */
//     @Override
//     public boolean onLongPress(final MotionEvent e, final MapView mapView) {
//     if (activity instanceof MapViewActivity) {
//     final AlertDialog.Builder builder = new AlertDialog.Builder(
//     mapView.getContext());
//     builder.setMessage(activity.getString(R.string.deleteDialog))
//     .setPositiveButton(activity.getString(R.string.yes), this)
//     .setNegativeButton(activity.getString(R.string.no), this)
//     .show();
//     }
//     return true;
//    
//     }
//    
//     /*
//     * (non-Javadoc)
//     *
//     * @see
//     * android.content.DialogInterface.OnClickListener#onClick(android.content
//     * .DialogInterface, int)
//     */
//     @Override
//     public void onClick(DialogInterface dialog, int which) {
//     switch (which) {
//     case DialogInterface.BUTTON_POSITIVE:
//     // Yes button clicked
//     mapView.removeOverlayFromMap(this);
//     final DataBaseHandler db = new DataBaseHandler(activity);
//     db.deleteDataElement(element);
//     db.close();
//     break;
//     case DialogInterface.BUTTON_NEGATIVE:
//     // No button clicked
//     break;
//     default:
//     break;
//     }
//     }
}
