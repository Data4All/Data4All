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

import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.bonuspack.overlays.Polyline;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

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
            Log.i(TAG, tag.toString());
            setTitle(activity.getString(tag.getNameRessource()));
            if(tag instanceof ClassifiedTag){
                setSubDescription(getLocalizedName(activity, element.getTags().get(tag)));
            }else{
                setSubDescription(element.getTags().get(tag));
            }
           // Log.i(TAG, getSubDescription());
        }
    }

    public String getLocalizedName(Context context, String key) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("name_" + key.replace(":", "_"),
                "string", context.getPackageName());

        if (id == 0) {
            return null;
        } else {
            return resources.getString(id);
        }
    }

}
