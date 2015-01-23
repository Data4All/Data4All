package io.github.data4all.smoothing;

/**
 * Smoothing sensor data with a low pass filter
 * 
 * @author Steeve
 *
 */
public class BasicSmoothingSensorMethod implements SensorSmoother {
	/*
	 * time smoothing constant for low-pass filter for more smoothing is it
	 * important that APHA been between 0 and 1 ;
	 * 
	 * @See:
	 * http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
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

		for (int i = 0; i < input.length; i++) {
			filteredValues[i] = output[i] + ALPHA
					* (input[i] - output[i]);
		}
		return filteredValues;
	}
}
