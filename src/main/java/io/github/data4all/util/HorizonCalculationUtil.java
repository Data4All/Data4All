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
package io.github.data4all.util;

import java.util.ArrayList;
import java.util.List;

import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.drawing.Point;

/**
 * This class calculates the horizon for the display of the device.
 * 
 * @author burghardt
 * @version 0.1
 *
 */

public class HorizonCalculationUtil {
    private Point point1, point2;
    private Point[] corners;
    /**
     * 3 for first two edges 5 for first and 3. corner 9 for first and 4. corner
     * 6 for second and 3. corner 10 for second and 4. corner 12 for 3. and 4.
     * corner
     */
    private int edges = 0;
    /**
     * Information for the general direction of the horizon
     */
    private int direction;
    /**
     * 2 Vectors for the x- and the y-axe
     */
    final private double[] xaxe = { 1, 0, 0 };
    final private double[] yaxe = { 0, 1, 0 };

    /**
     * calculates the horizon for the display of the device.
     * 
     * @param maxPitch
     *            maxPitchangle of the Camera
     * @param maxRoll
     *            maxROllangle of the Camera
     * @param maxWidth
     *            maxWidth in pixel of the devicedisplay
     * @param maxHeight
     *            maxHeight in pixel of the devicedisplay
     * @param maxhorizon
     *            the degree of the horizon in radian
     * @param deviceOrientationobject
     *            of DeviceOrientation
     * @return object of the inner class returnValues
     */
    public ReturnValues calcHorizontalPoints(float maxPitch, float maxRoll,
            float maxWidth, float maxHeight, float maxhorizon,
            DeviceOrientation deviceOrientation) {
        final ReturnValues rV = new ReturnValues();
        // Set Cornerpoints
        Point[] corners = { new Point(0.0f, 0.0f), new Point(maxWidth, 0.0f),
                new Point(maxWidth, maxHeight), new Point(0.0f, maxHeight), };
        this.corners = corners;

        // initial Vector
        final double[] vector = { 0, 0, -1, };
        // rotate around y-axe with roll
        final double roll = -deviceOrientation.getRoll();
        double[] vector2 = rotate(vector, yaxe, roll);
        // rotate around x-axe with pitch
        final double pitch = deviceOrientation.getPitch();
        double[] vector3 = rotate(vector2, xaxe, pitch);
        // calculate angle between vector and z-axis and subtract from
        // maxhorizon.
        final double angle = maxhorizon - Math.acos(-vector3[2]);
        // check if the device is looking above the horizon
        if (angle <= 0) {
            rV.setSkylook(true);
        }

        /*
         * calculate a roatationvector vertical to vector3 with the length of 1
         * and wich is on the x-y-plane.
         */
        final double rotateVectorLengthMultiplicator = Math
                .sqrt((vector3[0] * vector3[0]) + (vector3[1] * vector3[1]));
        final double[] rotateVector = {
                -vector3[1] / rotateVectorLengthMultiplicator,
                vector3[0] / rotateVectorLengthMultiplicator, 0, };

        /*
         * rotate the (0|0|-1) vector with the calculated angle and.
         * rotationvector.
         */
        final double[] vector4 = new double[3];
        vector4[0] = (rotateVector[1] * Math.sin(angle));
        vector4[1] = (rotateVector[0] * Math.sin(angle));
        vector4[2] = -Math.cos(angle);

        // calculate the pitch- and roll-angles.
        double horizonPitch = Math.atan(vector4[1] / (vector4[2]));
        double horizonRoll = Math.atan(vector4[0] / (vector4[2]));
        // calculate a point on the horizont vertical to the mid of the display.
        float x = calculatePixelFromAngle(horizonRoll, maxWidth, maxRoll);
        float y = calculatePixelFromAngle(horizonPitch, maxHeight, maxPitch);

        // set general direction / side of te horizon
        if (y <= 0 && x < 0) {
            direction = 0;
        }
        if (y < 0 && x >= 0) {
            direction = 1;
        }
        if (y >= 0 && x >= 0) {
            direction = 2;
        }
        if (y > 0 && x <= 0) {
            direction = 3;
        }

        // calculation of the gradiants
        vector2 = rotate(xaxe, xaxe, pitch);
        vector3 = rotate(vector2, yaxe, -roll);
        // calculate the pitch- and roll-angles.
        horizonPitch = Math.atan(vector3[1] / (vector3[2]));
        horizonRoll = Math.atan(vector3[0] / (vector3[2]));
        float xgradiant = calculatePixelFromAngle(horizonRoll, maxWidth,
                maxRoll);
        float ygradiant = calculatePixelFromAngle(horizonPitch, maxHeight,
                maxPitch);

        vector2 = rotate(yaxe, xaxe, pitch);
        vector3 = rotate(vector2, yaxe, -roll);
        // calculate the pitch- and roll-angles.
        horizonPitch = Math.atan(vector3[1] / (vector3[2]));
        horizonRoll = Math.atan(vector3[0] / (vector3[2]));
        // calculate a point on the horizont vertical to the mid of the display.
        xgradiant = xgradiant
                - calculatePixelFromAngle(horizonRoll, maxWidth, maxRoll);
        ygradiant = ygradiant
                - calculatePixelFromAngle(horizonPitch, maxHeight, maxPitch);

        // calculate and return the returnValues.
        return calculatePoints(maxWidth, maxHeight, x, y, xgradiant, ygradiant,
                rV);
    }

    /**
     * @param angle
     *            difference to the deviceorientation
     * @param width
     *            width or height of the devicedisplay
     * @param maxAngle
     *            maximum angle of the camera
     * @return the pixel
     */
    private float calculatePixelFromAngle(double angle, double width,
            double maxAngle) {
        final double adjacent = (width / 2) / Math.tan(maxAngle / 2);
        return (float) (Math.tan(angle) * adjacent);
    }

    /**
     * An inner Class for saving points, the visibility of the horizon and if
     * more then 50% of the display is above the horizon
     */
    public class ReturnValues {
        /**
         * A list of Points to draw the horizon on the view
         */
        private List<Point> points = new ArrayList<Point>();
        /**
         * true if more than 50% of the display is above the horizon
         */
        private boolean skylook;
        /**
         * false if the horzion is not visible on the display
         */
        private boolean visible = true;

        public List<Point> getPoints() {
            return points;
        }

        public void setPoints(List<Point> points) {
            this.points = points;
        }

        public boolean isSkylook() {
            return skylook;
        }

        public void setSkylook(boolean skylook) {
            this.skylook = skylook;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    /**
     * @param maxWidth
     *            maxWidth in pixel of the devicedisplay
     * @param maxHeight
     *            maxHeight in pixel of the devicedisplay
     * @param x
     *            x-value of Point on horizon
     * @param y
     *            y-value of Point on horizon
     * @param rV
     *            object of returnValues to return the calculations
     * @return object of returnValues
     */
    private ReturnValues calculatePoints(float maxWidth, float maxHeight,
            float x, float y, float xgradiant, float ygradiant, ReturnValues rV) {
        // counter for the added points.
        int iter = 0;
        // information Bits to check which edges are hit by the horizon
        edges = 0;
        // check if horizon is parallel to a side of the display.
        if (y == 0) {
            point1 = new Point(maxWidth / 2 + x, 0);
            point2 = new Point(maxWidth / 2 + x, maxHeight);
            edges = 10;
        } else if (x == 0) {
            point1 = new Point(0, maxHeight / 2 + y);
            point2 = new Point(maxWidth, maxHeight / 2 + y);
            edges = 5;
        } else {
            // calculade the collision of the horizonline with the displayedges.
            // check wich collision is important and add it to the returnvalues.
            final float xMin = y + ygradiant
                    * ((-maxWidth / 2 - x) / xgradiant) + maxHeight / 2;
            if (xMin > 0 && xMin <= maxHeight) {
                point1 = new Point(0, xMin);
                iter++;
                edges += 1;
            }

            final float yMin = x + xgradiant
                    * ((-maxHeight / 2 - y) / ygradiant) + maxWidth / 2;
            if (yMin > 0 && yMin <= maxWidth) {
                if (iter == 0) {
                    point1 = new Point(yMin, 0);
                } else {
                    point2 = new Point(yMin, 0);
                }
                iter++;
                edges += 2;
            }

            final float xMax = y + ygradiant * ((maxWidth / 2 - x) / xgradiant)
                    + maxHeight / 2;
            if (xMax > 0 && xMax <= maxHeight) {
                if (iter == 0) {
                    point1 = new Point(maxWidth, xMax);
                } else {
                    point2 = new Point(maxWidth, xMax);
                }
                iter++;
                edges += 4;
            }

            final float yMax = x + xgradiant
                    * ((maxHeight / 2 - y) / ygradiant) + maxWidth / 2;
            if (yMax > 0 && yMax <= maxWidth) {
                if (iter == 0) {
                    point1 = new Point(yMax, maxHeight);
                } else {
                    point2 = new Point(yMax, maxHeight);
                }
                iter++;
                edges += 8;
            }
            // check if more or less then 2 points have been added.
            if (iter != 2) {
                rV.setVisible(false);
                return rV;
            }
        }
        return addCorners(rV);
    }

    /**
     * 
     * @param rV
     *            the returnValue with set Skylook and Visibility
     * @return an ReturnValueObjekt with all needed information
     */
    private ReturnValues addCorners(ReturnValues rV) {
        // list of points for the drawingPath
        List<Point> points = new ArrayList<Point>();
        // add first Point
        points.add(point1);
        // Check if the Horizon hits two facing edges .
        // and adding the corners to the list
        if (edges == 5) {
            if (direction >= 2) {
                points.add(corners[3]);
                points.add(corners[2]);
            } else {
                points.add(corners[0]);
                points.add(corners[1]);
            }
        } else if (edges == 10) {
            if (direction == 1 || direction == 2) {
                points.add(corners[1]);
                points.add(corners[2]);
            } else {
                points.add(corners[0]);
                points.add(corners[3]);
            }
        } else {
            // Horizon doesnt hit facing edges, so add the one missing corner
            points.add(corners[direction]);
        }
        // add last point
        points.add(point2);
        rV.setPoints(points);
        return rV;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    /**
     * 
     * @param vector
     *            which is going to be rotated
     * @param axis
     *            around which the vector is rotated
     * @param the
     *            rotation angle
     * @return the rotated vector
     */
    private double[] rotate(double[] vector, double[] axis, double angle) {
        double[][] matrix = new double[3][3];
        matrix[0][0] = axis[0] * axis[0] * (1 - Math.cos(angle))
                + Math.cos(angle);
        matrix[0][1] = axis[0] * axis[1] * (1 - Math.cos(angle)) - axis[2]
                * Math.sin(angle);
        matrix[0][2] = axis[0] * axis[2] * (1 - Math.cos(angle)) + axis[1]
                * Math.sin(angle);
        matrix[1][0] = axis[1] * axis[0] * (1 - Math.cos(angle)) + axis[2]
                * Math.sin(angle);
        matrix[1][1] = axis[1] * axis[1] * (1 - Math.cos(angle))
                + Math.cos(angle);
        matrix[1][2] = axis[1] * axis[2] * (1 - Math.cos(angle)) - axis[0]
                * Math.sin(angle);
        matrix[2][0] = axis[2] * axis[0] * (1 - Math.cos(angle)) - axis[1]
                * Math.sin(angle);
        matrix[2][1] = axis[2] * axis[1] * (1 - Math.cos(angle)) + axis[0]
                * Math.sin(angle);
        matrix[2][2] = axis[2] * axis[2] * (1 - Math.cos(angle))
                + Math.cos(angle);

        double[] returnVec = new double[3];
        for (int i = 0; i < 3; i++) {
            double value = 0;
            for (int b = 0; b < 3; b++) {
                value += vector[b] * matrix[i][b];
            }
            returnVec[i] = value;
        }
        return returnVec;
    }

}
