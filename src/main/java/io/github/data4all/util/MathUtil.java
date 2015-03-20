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

/**
 * This class provides some calculation methods
 * 
 * @author burghardt
 * @version 1.1
 *
 */
public class MathUtil {

    private MathUtil() {
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
    public static double[] rotate(double[] vector, double[] axis, double angle) {
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

    /**
     * @param angle
     *            difference to the deviceorientation
     * @param width
     *            width or height of the devicedisplay
     * @param maxAngle
     *            maximum angle of the camera
     * @return the pixel
     */
    public static float calculatePixelFromAngle(double angle, double width,
            double maxAngle) {
        final double adjacent = (width / 2) / Math.tan(maxAngle / 2);
        return (float) (Math.tan(angle) * adjacent);
    }
    
    
    /**
     * Calculates the angle altered by the given pixel.
     * 
     * @param pixel
     *            one coordinate of the pixel
     * @param axis
     *            the axis on which the angle is altered
     * @param maxAngle
     *            maximum camera angle
     * @return altered Angle
     */
    public static double calculateAngleFromPixel(double pixel, double axis,
            double maxAngle) {
        final double adjacent = (axis / 2) / Math.tan(maxAngle / 2);
        final double opposite = pixel - (axis / 2);
        return Math.atan(opposite / adjacent);
    }

}
