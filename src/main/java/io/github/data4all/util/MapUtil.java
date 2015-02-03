/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    public static GeoPoint getCenterFromOsmElement(OsmElement element) {
        return getBoundingBoxForOsmElement(element).getCenter();
    }

    /**
     * Returns the BoundingBox of the given OsmElement.
     * 
     * @param elem
     *            the OsmElement whose BoundingBox should be calculated
     * @return the BoundingBox for the given OsmElement
     */
    public static BoundingBoxE6 getBoundingBoxForOsmElement(OsmElement elem) {
        if (elem instanceof Node) {
            final Node node = (Node) elem;
            final List<GeoPoint> array = new ArrayList<GeoPoint>();
            array.add(node.toGeoPoint());
            return BoundingBoxE6.fromGeoPoints((ArrayList<GeoPoint>) array);
        } else if (elem instanceof Way) {
            final Way way = (Way) elem;
            return BoundingBoxE6.fromGeoPoints(way.getUnsortedGeoPoints());
        }
        return null;
    }

}
