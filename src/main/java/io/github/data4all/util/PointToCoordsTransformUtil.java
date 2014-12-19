package io.github.data4all.util;

/**
 * This Class uses the Orientation of the Phone, the Pixel 
 * and the Camera-Parameters to calculate the Distance between 
 * the Phone and the Object in a 2D System.
 * 
 * @author burghardt
 * @version 0.1
 *
 */


import io.github.data4all.logger.*;
import java.lang.Math;
import java.util.ArrayList;


public class PointToCoordsTransformUtil {
	static String TAG = "PointToWorldCoords";
	private float height = 1700;
	
	/**
	 * @param pointlist
	 * @param orientation
	 * @return
	 */
	public ArrayList<float[]> calculate(ArrayList<float[]> pointlist, float[] orientation){
		ArrayList<float[]> calculatedPoints = new ArrayList<float[]>();
		float[] adjustedOrientation = null;
		for(float[] point : pointlist){
			adjustedOrientation = orientation;
			
			calculatedPoints.add(calculate2dPoint(adjustedOrientation));			
		}		
		return calculatedPoints;
	}
	

	/**
	 * @param orientation
	 * @return coords
	 */
	public float[] calculate2dPoint (float[] o){
		float[] vector = calculateVectorfromOrientation(o);
		
		if(vector[2] <= 0){
			vector[2]=-1;
			Log.d(TAG,"Camera is looking to the sky.");
		}		

		float[] coords = null;
		float z = height / vector[2];
		coords[0] = vector[0] * z;
		coords[1] = vector[1] * z;		
		
		Log.d(TAG,"Calculated X = " + vector[0] + " and Y = " + vector[1]);
		
		return coords;
		
	}
	
	/**
	 * @param orientation
	 * @return
	 */
	private float[] calculateVectorfromOrientation(float[] orientation){
		
		Log.d(TAG,"Delivered Phoneorientation: azimuth = " 
				+ orientation[0] +" ,pitch = " + orientation[1]
				+ ", roll = " + orientation[2]);
		
		//calculate Z with Pitch
		float z = (float) Math.cos(orientation[1]); 
		
		//calculate temp.Y with Pitch
		float yy = (float) Math.sin(-orientation[1]); 
		
		//calculate temp.X with fix Z and Roll
		float xx = (float) (Math.tan(orientation[2]) * z);
		
		// Rotate Vector with Azimuth
		float x = (float) ((xx * Math.cos(orientation[0])) 
				- (yy * Math.sin(orientation[0])));
		float y = (float) ((xx * Math.sin(orientation[0])) 
				+ (yy * Math.cos(orientation[0])));
		
		orientation[0] = x;
		orientation[1] = y;
		orientation[2] = z;
		Log.d(TAG,"Calculated Vector: X = " + orientation[0] 
				+ " ,Y = " + orientation[1]
				+ ", Z = " + orientation[2]);
		
		return orientation;
	}
		
}
