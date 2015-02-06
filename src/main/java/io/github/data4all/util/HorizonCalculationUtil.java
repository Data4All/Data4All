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

import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.drawing.Point;

import java.util.List;

public class HorizonCalculationUtil {

    public float[] calcHorizontalPoints(float maxPitch, float maxRoll,
            float maxWidth, float maxHeight, float maxhorizon,
            DeviceOrientation deviceOrientation) {
      //  float missPitch = maxhorizon - Math.abs(deviceOrientation.getPitch());
        //float missRoll = maxhorizon - Math.abs(deviceOrientation.getRoll());
        float skylook = 0;

        double pitch = -deviceOrientation.getPitch();
        double roll = -deviceOrientation.getRoll();

        double[] vector = new double[3];
        // Without any rotation
        vector[2] = -1;
        vector[1] = 0;
        vector[0] = 0;
        // rotate around X-Achse with pitch
        double[] vector2 = new double[3];
        vector2[0] = vector[0];
        vector2[1] = vector[1] * Math.cos(pitch) - vector[2] * Math.sin(pitch);
        vector2[2] = vector[1] * Math.sin(pitch) + vector[2] * Math.cos(pitch);
        // rotate around line through origin with pitch angle
        double[] vector3 = new double[3];
        vector3[0] = vector2[0] * Math.cos(roll) - vector2[1] * Math.sin(pitch)
                * Math.sin(roll) + vector2[2] * Math.cos(pitch)
                * Math.sin(roll);
        vector3[1] = vector2[0]
                * Math.sin(roll)
                * Math.sin(pitch)
                + vector2[1]
                * (Math.cos(pitch) * Math.cos(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll)) + vector2[2] * Math.cos(pitch)
                * Math.sin(pitch) * (1 - Math.cos(roll));
        vector3[2] = -vector2[0]
                * Math.cos(pitch)
                * Math.sin(roll)
                + vector2[1]
                * Math.sin(pitch)
                * Math.cos(pitch)
                * (1 - Math.cos(roll))
                + vector2[2]
                * (Math.sin(pitch) * Math.sin(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll));
        double vectorLength = Math.sqrt((vector3[0] * vector3[0])
                + (vector3[1] * vector3[1]) + (vector3[2] * vector3[2]));
        double angle = Math.acos(vector3[2] / vectorLength);
        double alpha = maxhorizon - angle;
        if (alpha < 0) {
            skylook = 1;
        }
        double[] rotateVector = { -vector3[1], vector3[0], 0 };
        double[] vector4 = new double[3];

        vector4[0] = (rotateVector[1] * Math.sin(alpha));
        vector4[1] = -(rotateVector[0] * Math.sin(alpha));
        vector4[2] = Math.cos(alpha);

        double horizonPitch = Math.atan(vector4[0] / vector[2]);
        double horizonRoll = Math.atan(vector4[1] / vector[2]);
        float[] point = {calculatePixelFromAngle(horizonPitch, maxWidth,
                maxPitch), calculatePixelFromAngle(horizonRoll, maxHeight,
                maxRoll), skylook};
        /*
        vector4[0] = vector3[0]
                * (rotateVector[0] * rotateVector[0] * (1 - Math.cos(alpha)) + Math
                        .cos(alpha)) + vector3[1]
                * (rotateVector[0] * rotateVector[1] * (1 - Math.cos(alpha)))
                + vector3[2] * (rotateVector[1] * Math.sin(alpha));
        vector4[1] = vector3[0]
                * (rotateVector[1] * rotateVector[0] * (1 - Math.cos(alpha)))
                + vector3[1]
                * (rotateVector[0] * rotateVector[0] * (1 - Math.cos(alpha)) + Math
                        .cos(alpha)) - vector3[2]
                * (rotateVector[0] * Math.sin(alpha));
        vector4[2] = vector3[0] * (-rotateVector[1] * Math.sin(alpha))
                + vector3[1] * (rotateVector[0] * Math.sin(alpha)) + vector3[2]
                * Math.cos(alpha);*/
        
        return point;
    }

    public float calculatePixelFromAngle(double angle, double width,
            double maxAngle) {

        double z = Math.sin(maxAngle / 2);
        float pixel = (float) ((width / 2) * ( Math.sin(angle) / (Math.sin(maxAngle)/2)));
        return pixel;

    }
    
    
    public double calculateAngleFromPixel(double pixel, double width,
            double maxAngle) {

        if ((pixel - (width / 2)) == 0) {
            return 0;
        }
        double percent = (2 * pixel - width) / width;
        double z = Math.sin(maxAngle / 2);
        double angle = Math.asin(z * percent);
        return angle;

    }

}
