package io.github.data4all.network;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.ManifestUtil;

import android.content.Context;

public class MapBoxTileSourceV4 extends OnlineTileSourceBase {

    private static final String[] mapBoxBaseUrl = new String[] {
            "http://a.tiles.mapbox.com/v4/", "http://b.tiles.mapbox.com/v4/",
            "http://c.tiles.mapbox.com/v4/", "http://d.tiles.mapbox.com/v4/" };

    private static final String MAPBOX_AUTHKEY = "MAPBOX_AUTHKEY";
    
    private String mapBoxMapId = "";
    private static String mapBoxAuthKey = "";
    
    public MapBoxTileSourceV4(String mapId, int min, int max)
    {
        
        super(mapId, ResourceProxy.string.mapbox, min, max, 256, ".png", mapBoxBaseUrl);
        mapBoxMapId = mapId;
    }

    /**
     * Read the API key from the manifest.<br>
     * This method should be invoked before class instantiation.<br>
     */
    public static void retrieveMapBoxAuthKey(final Context aContext) {
        // Retrieve the AuthKey from the Manifest
        mapBoxAuthKey = ManifestUtil.retrieveKey(aContext, MAPBOX_AUTHKEY);
    }
    
    @Override
    public String getTileURLString(final MapTile aMapTile) {
        StringBuffer url = new StringBuffer(getBaseUrl());
        url.append(mapBoxMapId);
        url.append("/");
        url.append(aMapTile.getZoomLevel());
        url.append("/");
        url.append(aMapTile.getX());
        url.append("/");
        url.append(aMapTile.getY());
        url.append(".png?access_token=");
        url.append(mapBoxAuthKey);

        String res = url.toString();

        return res;
    }
}
