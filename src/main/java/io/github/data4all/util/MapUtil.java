package io.github.data4all.util;

import java.util.ArrayList;

import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;


/**
 * Utility Class for Map Related Actions
 * 
 * @author Oliver Schwartz
 *
 */
public class MapUtil {
	
	public static GeoPoint getCenterFromOsmElement(OsmElement element){
		return getBoundingBoxForOsmElement(element).getCenter();
	}
	
	public static BoundingBoxE6 getBoundingBoxForOsmElement(OsmElement element){
		if(element instanceof Node){
			Node node = (Node) element;
			ArrayList<GeoPoint> array = new ArrayList<GeoPoint>();
			array.add(node.toGeoPoint());
			return BoundingBoxE6.fromGeoPoints(array);
		}else if (element instanceof Way){
			Way way = (Way) element;
			return BoundingBoxE6.fromGeoPoints(way.getUnsortedGeoPoints());
		}
		return null;
	}

}
