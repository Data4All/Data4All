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
        // zero if the general deviceorientation directed to the ground.
        ReturnValues rV = new ReturnValues();

        double pitch = deviceOrientation.getPitch();
        double roll = deviceOrientation.getRoll();

        final double[] vector = new double[3];
        // without any rotation .
        vector[2] = -1;
        vector[1] = 0;
        vector[0] = 0;
        // rotate around X-Achse with pitch.
        final double[] vector2 = new double[3];
        vector2[0] = 0;
        vector2[1] = Math.sin(pitch);
        vector2[2] = -Math.cos(pitch);
        // rotate around line through origin with pitch angle.
        final double[] vector3 = new double[3];
        vector3[0] = -vector2[1] * Math.sin(pitch) * Math.sin(roll)
                + vector2[2] * Math.cos(pitch) * Math.sin(roll);
        vector3[1] = vector2[1]
                * (Math.cos(pitch) * Math.cos(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll)) + vector2[2] * Math.cos(pitch)
                * Math.sin(pitch) * (1 - Math.cos(roll));
        vector3[2] = vector2[1]
                * Math.sin(pitch)
                * Math.cos(pitch)
                * (1 - Math.cos(roll))
                + vector2[2]
                * (Math.sin(pitch) * Math.sin(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll));

        // calculate angle between vector and z-axis and subtract from
        // maxhorizon.
        final double angle = maxhorizon - Math.acos(-vector3[2]);
        // check if the device is looking above the horizon
        if (angle <= 0) {
            rV.setSkylook(true);
        }
        /*
         * calculate a roatationvector vertical to vector3with the length of 1
         * and wich is on the x-y-plane.
         */
        double rotateVectorLengthMultiplicator = Math
                .sqrt((vector3[0] * vector3[0]) + (vector3[1] * vector3[1]));
        double[] rotateVector = {
                (-vector3[1] / rotateVectorLengthMultiplicator),
                (vector3[0] / rotateVectorLengthMultiplicator) };
        /*
         * rotate the (0|0|-1) vector with the calculated angle and.
         * rotationvector.
         */
        final double[] vector4 = new double[3];
        vector4[0] = (rotateVector[1] * Math.sin(angle));
        vector4[1] = (rotateVector[0] * Math.sin(angle));
        vector4[2] = Math.cos(angle);

        // calculate the pitch- and roll-angles.
        double horizonPitch = Math.atan(vector4[1] / (vector[2]));
        double horizonRoll = Math.atan(vector4[0] / (vector[2]));
        // calculate a point on the horizont vertical to the mid of the display.
        float x = calculatePixelFromAngle(horizonRoll, maxWidth, maxRoll);
        float y = calculatePixelFromAngle(horizonPitch, maxHeight, maxPitch);
        // calculate and return the returnValues.
        return calculatePoints(maxWidth, maxHeight, x, y, rV);
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
         * first point on the edge of the display representing the horizon
         */
        private Point point1;
        /**
         * second point on the edge of the display representing the horizon
         */
        private Point point2;
        /**
         * true if more than 50% of the display is above the horizon
         */
        private boolean skylook;
        /**
         * false if the horzion is not visible on the display
         */
        private boolean visible = true;

        public Point getPoint1() {
            return point1;
        }

        public void setPoint1(Point point1) {
            this.point1 = point1;
        }

        public Point getPoint2() {
            return point2;
        }

        public void setPoint2(Point point2) {
            this.point2 = point2;
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
            float x, float y, ReturnValues rV) {
        // counter for the added points.
        int iter = 0;
        // check if horizon is parallel to a side of the display.
        if (y == 0.0f) {
            rV.setPoint1(new Point(maxWidth / 2 + x, 0));
            rV.setPoint2(new Point(maxWidth / 2 + x, maxHeight));
        } else if (x == 0.0f) {
            rV.setPoint1(new Point(0, maxHeight / 2 + y));
            rV.setPoint2(new Point(maxWidth, maxHeight / 2 + y));
        } else {
            // calculade the collision of the horizonline with the displayedges.
            float xMin = y + x * ((maxWidth / 2 + x) / y) + maxHeight / 2;
            float xMax = y + x * ((-maxWidth / 2 + x) / y) + maxHeight / 2;
            float yMin = x - y * ((-maxHeight / 2 - y) / x) + maxWidth / 2;
            float yMax = x - y * ((maxHeight / 2 - y) / x) + maxWidth / 2;
            // check wich collision is important and add it to the returnvalues.
            if (xMin > 0 && xMin <= maxHeight) {
                rV.setPoint1(new Point(0, xMin));
                iter++;
            }
            if (yMin > 0 && yMin <= maxWidth) {
                if (iter == 0) {
                    rV.setPoint1(new Point(yMin, 0));
                } else {
                    rV.setPoint2(new Point(yMin, 0));
                }
                iter++;
            }
            if (xMax > 0 && xMax <= maxHeight) {
                if (iter == 0) {
                    rV.setPoint1(new Point(maxWidth, xMax));
                } else {
                    rV.setPoint2(new Point(maxWidth, xMax));
                }
                iter++;
            }
            if (yMax > 0 && yMax <= maxWidth) {
                if (iter == 0) {
                    rV.setPoint1(new Point(yMax, maxHeight));
                } else {
                    rV.setPoint2(new Point(yMax, maxHeight));
                }
                iter++;
            }
            // check if more or less then 2 points have been added.
            if (iter != 2) {
                rV.setVisible(false);
            }
        }
        return rV;
    }

}
