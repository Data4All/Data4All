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
package io.github.data4all.smoothing;

/**
 * Smoothing sensor data with a low pass filter.
 * 
 * @author Steeve
 *
 */
public class BasicSensorSmoother implements SensorSmoother {

    /**
     * time smoothing constant for low-pass filter. It is important that ALPHA
     * been between 0 and 1.
     * 
     * @See: 
     *       http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
     */
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.smoothing.Smoother#lowPass(float[], float[])
     */
    @Override
    public float[] filter(float[] input, float[] output) {
        float[] filteredValues = new float[3];
        if (output == null)
            return input;
        if (input != null) {
            for (int i = 0; i < input.length; i++) {
                filteredValues[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
        } else {
            return output;
        }
        return filteredValues;
    }
}
