package io.github.data4all.smoothing;

/**
 * this class applies a low pass filter to filter sensor data
 * 
 * @author Steeve
 *
 */
public class BasicSmoothingSensorMethod implements SensorSmoother {
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.smoothing.Smoother#lowPass(float[], float[])
     */
    @Override
    public float[] filter(float[] input, float[] output) {
        if (output == null)
            return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
