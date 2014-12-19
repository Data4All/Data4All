package io.github.data4all.util;

/**
 * This Class uses the Orientation of the Phone, the Pixel 
 * and the Camera-Parameters to calculate the Distance between 
 * the Phone and the Object in a 2D System.
 * 
 * @author AndreasBurghardt
 * @version 0.1
 *
 */


import io.github.data4all.logger.*;
import java.lang.Math;


public class PointToCoordsTransformUtil {
	static String TAG = "PointToWorldCoords";
	private float[] orientation;
	private float height = 1700;
	float[] coords;
	
	public float[] calculate (float[] o){
		this.orientation = o;
		
		Log.d(TAG,"Delivered Phoneorientation: azimuth = " 
				+ o[0] +" ,pitch = " + o[1] + ", roll = " + o[2]);
		
		//calculate Z with Pitch
		float z = (float) Math.cos(orientation[1]); 
		
		//calculate temp.Y with Pitch
		float yy = (float) Math.sin(-orientation[1]); 
		
		//calculate temp.X with fix Z and Roll
		float xx = (float) (Math.tan(orientation[2]) * z);
		
		// Rotate Vector with Azimuth
		float x = (float) ((xx * Math.cos(orientation[0])) - (yy * Math.sin(orientation[0])));
		float y = (float) ((xx * Math.sin(orientation[0])) + (yy * Math.cos(orientation[0])));
		
		if(z >= 0){
			z=-1;
			Log.d(TAG,"Camera is looking to the sky.");
		}
		
		z = height / (-z);
		x = x * z;
		y = y * z;
		coords[0] = x;
		coords[1] = y;
		
		Log.d(TAG,"Calculated x = " + x + " and y = " + y);
		return coords;
		
	}
		
}
