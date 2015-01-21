package io.github.data4all.smoothing;

/**
 * @author tbrose
 *
 */
public interface SensorSmoother {

    /**
     * this method filters the input values, applies low pass filter and outputs
     * the filtered signals
     * 
     * @param input
     * @param output
     * @return
     */
    public abstract float[] filter(float[] input, float[] output);

}