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


import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;

import java.util.ArrayList;


public class PointToCoordsTransformUtil {
	static String TAG = "PointToWorldCoords";
	private float height = 1700;
	
	
	
	
	/**
	 * 
	 * @param tps
	 * @param deviceOrientation
	 * @return
	 */
	public ArrayList<Point> transform(TransformationParamBean tps, DeviceOrientation deviceOrientation){
		ArrayList<Point> coords = new ArrayList<Point>(); //to save calculated coordinates
		float[] orientation = new float[3];
		this.height = tps.getHeight();		
		orientation[0] = deviceOrientation.getAzimuth();
		
		for(Point point : tps.getPoints()){
			orientation[1] = calculateAngle(point.getX(), tps.getPhotoWidth(),
					tps.getCameraMaxPitchAngle(),deviceOrientation.getPitch());
			orientation[2] = calculateAngle(point.getY(), tps.getPhotoHeight(),
					tps.getCameraMaxRotationAngle(), deviceOrientation.getRoll());
			float[] coord = calculate2dPoint(orientation);
			Point p = new Point(coord[0], coord[1]);
			coords.add(p);			
		}		
		return coords;
	}
	
	

	
	public float[] calculate2dPoint(DeviceOrientation deviceOrientation){
		float[] orientation = new float[3];
		orientation[0] = deviceOrientation.getAzimuth();
		orientation[1] = deviceOrientation.getPitch();
		orientation[2] = deviceOrientation.getRoll();
		return calculate2dPoint(orientation);
	}
	
	/**
	 * Calculates the Angle altered by the chosen Pixel
	 * @param pixel
	 * @param width
	 * @param maxAngle
	 * @param oldAngle
	 * @return
	 */
	private float calculateAngle(float pixel, float width, float maxAngle, float oldAngle){
		if((pixel - (width / 2)) == 0){
			return oldAngle;
		}
		float percent = (width / 2) / (pixel - (width / 2));
		float angle = maxAngle * percent;		
		return oldAngle + angle;
	}

	
	
	/**
	 * @param orientation
	 * @return coords in mm in a System with (0,0) = Phoneposition; 
	 * 			x = West/East Axis and y = Norh/South Axis
	 */
	public float[] calculate2dPoint(float[] orientation){
		float[] vector = calculateVectorfromOrientation(orientation);
		
		if(vector[2] <= 0){
			vector[2]=-1;
			Log.d(TAG,"Camera is looking to the sky.");
		}		

		float[] coords = new float[2];
		float z = height / vector[2];
		coords[0] = vector[0] * z;
		coords[1] = vector[1] * z;		
		
		Log.d(TAG,"Calculated X = " + coords[0] + " and Y = " + coords[1]);

		return coords;		
	}
	
	
	
	/**
	 * Calculates a Vector with the given Orientation. 
	 * The Coordinate-System: y = North , x = West , z = Earth-Center
	 * @param orientation
	 * @return 
	 */
	private float[] calculateVectorfromOrientation(float[] orientation){
		
		Log.d(TAG,"Delivered Phoneorientation: azimuth = " 
				+ orientation[0] +" ,pitch = " + orientation[1]
				+ ", roll = " + orientation[2]);		
		
		//calculate fix Z with Pitch
		float z = (float) Math.cos(orientation[1]); 
		
		//calculate temp.Y with Pitch
		float y = (float) Math.sin(-orientation[1]); 
		
		//calculate temp.X with fix Z and Roll
		float x = (float) (Math.tan(orientation[2]) * z);
		
		Log.d(TAG,"Calculated Vector without azimuth: X = " + x 
				+ " ,Y = " + y
				+ ", Z = " + z);
		
		
		// Rotate Vector with Azimuth (Z is fix))
		float[] vector = new float[3];
		vector[0] = (float) ((x * Math.cos(orientation[0])) 
				- (y * Math.sin(orientation[0])));
		vector[1] = (float) ((x * Math.sin(orientation[0])) 
				+ (y * Math.cos(orientation[0])));
		vector[2] = z;	
		
		
		Log.d(TAG,"Calculated Vector: X = " + vector[0] 
				+ " ,Y = " + vector[1]
				+ ", Z = " + vector[2]);
		
		return vector;
	}
		
}
