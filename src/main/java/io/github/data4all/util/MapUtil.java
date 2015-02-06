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
        return getBoundingBoxForOsmElement(element).getCenter();
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
        if (element instanceof Node) {
            final Node node = (Node) element;
            final List<GeoPoint> array = new ArrayList<GeoPoint>();
            array.add(node.toGeoPoint());
            return BoundingBoxE6.fromGeoPoints((ArrayList<GeoPoint>) array);
        } else if (element instanceof PolyElement) {
            final PolyElement polyElement = (PolyElement) element;
            return BoundingBoxE6
                    .fromGeoPoints((ArrayList<GeoPoint>) polyElement
                            .getUnsortedGeoPoints());
        }
        return null;
    }

}
