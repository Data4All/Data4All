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
package io.github.data4all.util;

import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

/**
 * Utility Class for Map Related Actions.
 * 
 * @author Oliver Schwartz
 *
 */
public final class MapUtil {

    /**
     * Private Default Constructor.
     **/
    private MapUtil() {
    }

    /**
     * Returns the Center of the given OsmElement.
     * 
     * @param element
     *            the OsmElement whose center should be calculated
     * @return the Center of the OsmElement
     */
    public static GeoPoint getCenterFromOsmElement(AbstractDataElement element) {
        BoundingBoxE6 bb = getBoundingBoxForOsmElement(element);
        if(bb == null){
            return null;
        }
        return bb.getCenter();
    }

    /**
     * Returns the Center of the given OsmElement.
     * 
     * @param list
     *            list of OsmElements whose center should be calculated
     * @return the Center of the OsmElement
     */
    public static GeoPoint getCenterFromOsmElements(
            List<AbstractDataElement> list) {
        BoundingBoxE6 bb = getBoundingBoxForOsmElements(list);
        if(bb == null){
            return null;
        }
        return bb.getCenter();
    }

    /**
     * Returns the BoundingBox of the given OsmElement.
     * 
     * @param element
     *            the OsmElement whose BoundingBox should be calculated
     * @return the BoundingBox for the given OsmElement
     */
    public static BoundingBoxE6 getBoundingBoxForOsmElement(
            AbstractDataElement element) {
        final ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
        list.addAll(getPointsForElement(element));
        if (list.isEmpty()) {
            return null;
        }
        return BoundingBoxE6.fromGeoPoints(list);
    }

    public static GeoPoint getCenterFromPointList(
            List<GeoPoint> list) {
        BoundingBoxE6 bb = getBoundingBoxForPointList(list);
        if(bb == null){
            return null;
        }
        return bb.getCenter();
    }
    
    public static BoundingBoxE6 getBoundingBoxForPointList(List<GeoPoint> list){
        final ArrayList<GeoPoint> aList = new ArrayList<GeoPoint>();
        aList.addAll(list);
        if (aList.isEmpty()) {
            return null;
        }
        return BoundingBoxE6.fromGeoPoints(aList);
    }

    /**
     * Returns the BoundingBox of the given OsmElement.
     * 
     * @param list
     *            list of OsmElements whose BoundingBox should be calculated
     * @return the BoundingBox for the given OsmElement
     */
    public static BoundingBoxE6 getBoundingBoxForOsmElements(
            List<AbstractDataElement> list) {
        final ArrayList<GeoPoint> array = new ArrayList<GeoPoint>();
        for (AbstractDataElement elem : list) {
            array.addAll(getPointsForElement(elem));
        }
        if (array.isEmpty()) {
            return null;
        }
        return BoundingBoxE6.fromGeoPoints(array);
    }

    /**
     * Returns the GeoPoints of the given OsmElement.
     * 
     * @param element
     *            the OsmElement whose GeoPoints should be returned
     * @return List of GeoPoints from the OsmElement
     */
    private static List<GeoPoint> getPointsForElement(AbstractDataElement elem) {
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        if (elem instanceof Node) {
            final Node node = (Node) elem;
            points.add(node.toGeoPoint());
        } else if (elem instanceof PolyElement) {
            final PolyElement polyElement = (PolyElement) elem;
            points = polyElement.getUnsortedGeoPoints();
        }
        return points;
    }

}
