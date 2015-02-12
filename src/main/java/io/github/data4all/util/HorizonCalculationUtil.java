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

/**
 * This class calculates the horizon for the display of the device
 * 
 * @author burghardt
 * @version 0.1
 *
 */

public class HorizonCalculationUtil {

    /**
     * calculates the horizon for the display of the device
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
     * @return floatarray return[0] pixel for the xAxis return[1] pixel for the
     *         yAxis return[2] 0 if the general direction of the device is under
     *         the horizon 1 if the direction is above the horizon
     */
    public float[] calcHorizontalPoints(float maxPitch, float maxRoll,
            float maxWidth, float maxHeight, float maxhorizon,
            DeviceOrientation deviceOrientation) {
        // zero if the general deviceorientation directed to the ground.
        float skylook = 0;

        double pitch = -deviceOrientation.getPitch();
        double roll = deviceOrientation.getRoll();

        double[] vector = new double[3];
        // without any rotation .
        vector[2] = -1;
        vector[1] = 0;
        vector[0] = 0;
        // rotate around X-Achse with pitch.
        double[] vector2 = new double[3];
        vector2[0] = 0;
        vector2[1] = Math.sin(pitch);
        vector2[2] = - Math.cos(pitch);
        // rotate around line through origin with pitch angle.
        double[] vector3 = new double[3];
        vector3[0] = - vector2[1] * Math.sin(pitch) * Math.sin(roll)
                + vector2[2] * Math.cos(pitch) * Math.sin(roll);
        vector3[1] = vector2[1] * (Math.cos(pitch) * Math.cos(pitch)
                * (1 - Math.cos(roll)) + Math.cos(roll)) 
                + vector2[2] * Math.cos(pitch)
                * Math.sin(pitch) * (1 - Math.cos(roll));
        vector3[2] = vector2[1] * Math.sin(pitch) * Math.cos(pitch)
                * (1 - Math.cos(roll)) + vector2[2] * (Math.sin(pitch)
                * Math.sin(pitch) * (1 - Math.cos(roll)) + Math.cos(roll));

        //calculate angle between vector and z-axis and subtract from maxhorizon.
        double angle = maxhorizon - Math.acos(-vector3[2]);
        //check if the device is looking above the horizon
        if (angle <= 0) {
            skylook = 1;
        }
        /*calculate a roatationvector vertical to vector3
         *with the length of 1 and wich is on the x-y-plane.
         */
        double rotateVectorLengthMultiplicator = Math
                .sqrt((vector3[0] * vector3[0]) + (vector3[1] * vector3[1]));
        double[] rotateVector = {
                (-vector3[1] / rotateVectorLengthMultiplicator),
                (vector3[0] / rotateVectorLengthMultiplicator) };
        //rotate the (0|0|-1) vector with the calculated angle and rotationvector.
        double[] vector4 = new double[3];
        vector4[0] = (rotateVector[1] * Math.sin(angle));
        vector4[1] = -(rotateVector[0] * Math.sin(angle));
        vector4[2] = Math.cos(angle);
        
        //calculate the pitch- and roll-angles
        double horizonPitch = Math.atan(vector4[1] / (-vector[2]));
        double horizonRoll = Math.atan(vector4[0] / (-vector[2]));
        
        float[] point = {
                calculatePixelFromAngle(horizonPitch, maxWidth, maxPitch),
                calculatePixelFromAngle(horizonRoll, maxHeight, maxRoll),
                skylook };

        return point;
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
        double mid = width / 2;
        double angle2 = maxAngle / 2;
        double a = Math.tan(angle2);
        double b = Math.tan(angle);
        return (float) ((b / a) * mid + mid);
    }

}
