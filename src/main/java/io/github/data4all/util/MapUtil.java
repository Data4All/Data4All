package io.github.data4all.util;

import java.util.ArrayList;
import java.util.List;

import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

/**
 * Utility Class for Map Related Actions.
 * 
 * @author Oliver Schwartz
 *
 */
public class MapUtil {

    /**
     * Private Default Constructor
     **/
    private MapUtil() {
    }

    /**
     * Returns the Center of the given OsmElement
     *  
     * @param element
     *            the OsmElement whose center should be calculated 
     * @return the Center of the OsmElement 
     */
    public static GeoPoint getCenterFromOsmElement(OsmElement element) {
        return getBoundingBoxForOsmElement(element).getCenter();
    }

    /**
     * Returns the BoundingBox of the given OsmElement
     * 
     * @param element
     *            the OsmElement whose BoundingBox should be calculated
     * @return the BoundingBox for the given OsmElement
     */
    public static BoundingBoxE6 getBoundingBoxForOsmElement(OsmElement element) {
        if (element instanceof Node) {
            final Node node = (Node) element;
            final List<GeoPoint> array = new ArrayList<GeoPoint>();
            array.add(node.toGeoPoint());
            return BoundingBoxE6.fromGeoPoints((ArrayList<GeoPoint>) array);
        } else if (element instanceof Way) {
             final Way way = (Way) element;
            return BoundingBoxE6.fromGeoPoints(way.getUnsortedGeoPoints());
        }
        return null;
    }

}
