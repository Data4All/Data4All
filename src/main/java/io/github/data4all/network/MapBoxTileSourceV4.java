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
package io.github.data4all.network;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.ManifestUtil;

import android.content.Context;

/**
 * MapBox Interface for the new Version 4 Api.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapBoxTileSourceV4 extends OnlineTileSourceBase {

    // Mapbox Api version 4 Base URL
    private static final String[] MAPBOX_BASE_URL = new String[] {
            "http://a.tiles.mapbox.com/v4/", "http://b.tiles.mapbox.com/v4/",
            "http://c.tiles.mapbox.com/v4/", "http://d.tiles.mapbox.com/v4/" };

    // Name of the Authkey in the Android Manifest
    private static final String MAPBOX_AUTHKEY = "MAPBOX_AUTHKEY";

    // The Mapbox MapId
    private String mapBoxMapId = "";

    // The Mapbox Public Key for the Version 4 API
    private static String mapBoxAuthKey = "";

    /**
     * Sets minimal/maximal Zoomlevel and the MapId.
     * 
     * @param mapId
     *            MapId of the intended Map
     * @param min
     *            minimal zoomlevel
     * @param max
     *            maximal zoomlevel
     */
    public MapBoxTileSourceV4(String mapId, int min, int max) {

        super(mapId, ResourceProxy.string.mapbox, min, max, 256, ".png",
                MAPBOX_BASE_URL);
        mapBoxMapId = mapId;
    }

    /**
     * Read the Public API key from the manifest.<br>
     * This method should be invoked before class instantiation.<br>
     */
    public static void retrieveMapBoxAuthKey(final Context aContext) {
        // Retrieve the AuthKey from the Manifest
        mapBoxAuthKey = ManifestUtil.retrieveKey(aContext, MAPBOX_AUTHKEY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase#getTileURLString
     * (org.osmdroid.tileprovider.MapTile)
     */
    @Override
    public String getTileURLString(final MapTile aMapTile) {
        final StringBuilder url = new StringBuilder(getBaseUrl());
        url.append(mapBoxMapId);
        url.append("/");
        url.append(aMapTile.getZoomLevel());
        url.append("/");
        url.append(aMapTile.getX());
        url.append("/");
        url.append(aMapTile.getY());
        url.append(".png?access_token=");
        url.append(mapBoxAuthKey);

        return url.toString();
    }
}
