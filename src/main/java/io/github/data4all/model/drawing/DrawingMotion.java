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
package io.github.data4all.model.drawing;

import java.util.ArrayList;
import java.util.List;

/**
 * The DrawingMotion stores the path of a motion and provides methods to
 * determine the behavior to the motion.<br/>
 * <p/>
 * It is used by the painting component to store the user input.<br/>
 * Also its used by the MotionInterpreters to interpret the user input.
 * 
 * An activity can easily implement the usage of DrawingMotion by overwriting
 * the onTouchEvent-method as following:
 * 
 * <pre>
 *  {@code
 * public boolean onTouchEvent(MotionEvent event) {
 *     int action = event.getAction();
 *     if (action == MotionEvent.ACTION_DOWN) {
 *         currentMotion = new DrawingMotion();
 *         currentMotion.addPoint(event.getX(), event.getY());
 *         return true;
 *     } else if (action == MotionEvent.ACTION_MOVE) {
 *         currentMotion.addPoint(event.getX(), event.getY());
 *         return true;
 *     } else if (action == MotionEvent.ACTION_UP) {
 *         // Do something with the finished motion
 *         return true;
 *     }
 *     return false;
 * }
 * </pre>
 * 
 * @author tbrose
 */
public class DrawingMotion {

    /**
     * The default tolerance for a Point.
     */
    public static final float POINT_TOLERANCE = 5f;

    /**
     * List of all added Points.
     */
    private List<Point> points = new ArrayList<Point>();

    /**
     * Calculates the euclidean distance between point a and point b.
     * 
     * @param a
     *            the first point
     * @param b
     *            the second point
     * @return the euclidean distance between point a and point b
     */
    private static float delta(Point a, Point b) {
        return (float) Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
    }

    /**
     * Adds a Point to the DrawingMotion.
     * 
     * @param x
     *            the x value of the point
     * @param y
     *            the y value of the point
     */
    public void addPoint(float x, float y) {
        points.add(new Point(x, y));
    }

    /**
     * Calculates the average point over all points in this motion.
     * 
     * @return The average point over all points or {@code null} if there is no
     *         point in this motion
     */
    public Point average() {
        if (this.getPathSize() == 0) {
            return null;
        } else {
            float x = 0;
            float y = 0;
            for (Point p : this.getPoints()) {
                x += p.getX();
                y += p.getY();
            }
            return new Point(x / this.getPathSize(), y / this.getPathSize());
        }
    }

    /**
     * Returns the last point of this DrawingMotion if there is at least one
     * point in this motion.
     * 
     * @return the last point of the motion or null if there is no point in the
     *         motion
     */
    public Point getEnd() {
        return points.isEmpty() ? null : points.get(points.size() - 1);
    }

    /**
     * Returns the number of points in this DrawingMotion.
     * 
     * @return the number of points
     */
    public int getPathSize() {
        return points.size();
    }

    /**
     * Returns a copy of the point at the given index.
     * 
     * @param index
     *            the given index
     * @return a copy of the point at the given index
     * @throws IndexOutOfBoundsException
     *             if the given index is out of the bounds
     */
    public Point getPoint(int index) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                    + points.size());
        } else {
            return points.get(index);
        }
    }

    /**
     * Returns a copy of the points in this DrawingMotion.
     * 
     * @return a copy of the points in this DrawingMotion
     */
    public List<Point> getPoints() {
        return new ArrayList<Point>(points);
    }

    /**
     * Returns the first point of this DrawingMotion if there is at least one
     * point in this motion.
     * 
     * @return the first point of the motion or null if there is no point in the
     *         motion
     */
    public Point getStart() {
        return points.isEmpty() ? null : points.get(0);
    }

    /**
     * Calculates if this DrawingMotion is a Path. <br/>
     * A DrawingMotion with zero entries is not a Path. <br/>
     * A DrawingMotion with more entries is a Path if it is not a point.
     * 
     * @return true - if the motion has a path-size over zero and is not a
     *         point. <br/>
     *         false otherwise
     * 
     * @see DrawingMotion#isPoint()
     * @see DrawingMotion#POINT_TOLERANCE
     */
    public boolean isPath() {
        return !(this.getPathSize() == 0) && !isPoint();
    }

    /**
     * Calculates if this DrawingMotion is a Point. <br/>
     * A DrawingMotion with zero entries is not a Point. <br/>
     * A DrawingMotion with more entries is a Point, if all the Points describes
     * a spot on the screen with at least {@link DrawingMotion#POINT_TOLERANCE
     * POINT_TOLERANCE} difference from the start-point of the motion.
     * 
     * @return true - if all Points in the motion are on the given tolerance
     *         spot around the starting point <br/>
     *         false otherwise
     * 
     * @see DrawingMotion#POINT_TOLERANCE
     */
    public boolean isPoint() {
        if (this.getPathSize() == 0) {
            return false;
        }
        for (Point p : points) {
            if (delta(this.getStart(), p) > POINT_TOLERANCE) {
                return false;
            }
        }
        return true;
    }
}
