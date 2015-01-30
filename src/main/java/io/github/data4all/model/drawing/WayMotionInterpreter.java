/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.model.drawing;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This WayMotionInterpreter is a MotionInterpreter for Ways.<br/>
 * 
 * It interprets dot-wise, path-wise and single-path user input.
 * 
 * @author tbrose
 * @see MotionInterpreter
 */
public class WayMotionInterpreter implements MotionInterpreter {

    /**
     * The log-tag for this class.
     */
    private static final String TAG = WayMotionInterpreter.class
            .getSimpleName();

    /**
     * The maximum angle-variation where a point is reduced.
     */
    private static final int ANGLE_VARIATION = 15;

    /**
     * The maximum combine-variation where points were combined.
     */
    private static final int COMBINE_VARIATION = 25;

    private PointToCoordsTransformUtil pointTrans;

    /**
     * Creates an WayMotionInterpreter with the specified transformation
     * utility.
     * 
     * @param pointTrans
     *            the transformation utility
     */
    public WayMotionInterpreter(PointToCoordsTransformUtil pointTrans) {
        this.pointTrans = pointTrans;
    }

    /**
     * Combines the edge-points of the given polygon so that points which are
     * relatively close to each other are combined into one single point.
     * 
     * @param polygon
     *            the polygon of the area
     * @return a reduced polygon which approximates the input polygon with a
     *         minimum of points
     */
    private static List<Point> combine(List<Point> polygon) {
        // A polygon with less than two points has no need to be combined
        if (polygon.size() > 1) {
            final List<Point> newPolygon = new ArrayList<Point>();

            // Start at the first point with the combination
            Point mid = polygon.get(0);
            int count = 1;

            for (int i = 1; i < polygon.size(); i++) {
                final Point p = polygon.get(i);

                if (Math.hypot(mid.getX() - p.getX(), mid.getY() - p.getY())
                        <= COMBINE_VARIATION) {
                    // The point is in range of the current mid, add him to the
                    // combined point
                    final float midSumX = mid.getX() * count + p.getX();
                    final float midSumY = mid.getY() * count + p.getY();
                    count++;
                    mid = new Point(midSumX / count, midSumY / count);
                } else {
                    // The point is to far away, add the 'old' combined point
                    // and start a new combination at this point
                    newPolygon.add(mid);
                    mid = p;
                    count = 1;
                }
            }

            // Add the last point to the combined polygon
            if (mid != null) {
                newPolygon.add(mid);
            }
            return newPolygon;
        } else {
            return polygon;
        }
    }

    /**
     * First reduces the points of the polygon by removing points where the
     * angel between the previous and the next point is nearly 180 degrees (25
     * degree tolerance).<br/>
     * After this procedure the edge-points are
     * {@link WayMotionInterpreter#combine(List) combined} and returned.
     * 
     * @param polygon
     *            the polygon of the area
     * @return a reduced polygon which approximates the input polygon with a
     *         minimum of points
     */
    private static List<Point> reduce(List<Point> polygon) {
        // We need at least three points to reduce the polygon
        if (polygon.size() >= 3) {
            final List<Point> newPolygon = new ArrayList<Point>();
            // The first point of the polygon wont be reduced
            newPolygon.add(polygon.get(0));

            for (int i = 0; i < polygon.size() - 1; i++) {
                // Get the previous, current and next Point in the polygon
                // The previous point is the last point added to the reduced
                // polygon for better circle detection
                final Point a = newPolygon.get(newPolygon.size() - 1);
                final Point b = polygon.get(i + 1);
                final Point c = polygon.get((i + 2) % polygon.size());

                final double alpha = Point.getBeta(a, b, c);
                Log.d(TAG, "point " + (i + 1) + ": " + Math.toDegrees(alpha)
                        + "degree");
                final double variation = Math.abs(Math.toDegrees(alpha) - 180);
                if (variation >= ANGLE_VARIATION) {
                    newPolygon.add(polygon.get(i + 1));
                } else if (i == polygon.size() - 2 && newPolygon.size() < 2) {
                    // If we reduced the polygon to a line we need so keep the
                    // end-point of the line
                    newPolygon.add(b);
                }
            }
            return combine(newPolygon);
        } else {
            return combine(polygon);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#interprete(java.util
     * .List, io.github.data4all.model.drawing.DrawingMotion)
     */
    @Override
    public List<Point> interprete(List<Point> interpreted,
            DrawingMotion drawingMotion) {
        final List<Point> result;

        if (drawingMotion == null) {
            return interpreted;
        } else if (interpreted == null) {
            result = new ArrayList<Point>();
        } else {
            result = new ArrayList<Point>(interpreted);
        }

        if (drawingMotion.isPoint()) {
            result.add(drawingMotion.average());
        } else {
            // for a path use the last point
            result.addAll(drawingMotion.getPoints());
        }

        return reduce(result);
    }

    /**
     * @author sbollen
     */
    @Override
    public OsmElement create(List<Point> polygon, int rotation) {
        // create a new Way and copy the List of Nodes to this way
        final Way newWay = new Way(-1, 1);

        final List<Node> nodeList = pointTrans.transform(polygon, rotation);

        newWay.addNodes(nodeList, false);
        return newWay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.model.drawing.MotionInterpreter#isArea()
     */
    @Override
    public boolean isArea() {
        return false;
    }

}
