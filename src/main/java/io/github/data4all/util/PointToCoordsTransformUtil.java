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
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;


public class PointToCoordsTransformUtil {
	static String TAG = "PointToWorldCoords";
	private int osmID = -1;
	private static int osmVersion = 1;
	private double height = 1.0;
	TransformationParamBean tps;
	DeviceOrientation deviceOrientation;
	
	public PointToCoordsTransformUtil() {
		
	}
	
	public PointToCoordsTransformUtil(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation) {
		this.tps = tps;
		this.deviceOrientation = deviceOrientation;		
	}
	
	public List<Node> transform(List<Point> points){
		return transform(tps, deviceOrientation, points);
	}
	
	/**
	 * 
	 * @param tps
	 * @param deviceOrientation
	 * @return
	 */
	public List<Node> transform(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, List<Point> points){
		
		List<Node> nodes = new ArrayList<Node>();
		double[] orientation = new double[3];
		this.height = tps.getHeight();		
		orientation[0] = - deviceOrientation.getAzimuth();		
		for(Point point : points){
			orientation[1] = calculateAngle(point.getX(), tps.getPhotoWidth(),
					tps.getCameraMaxPitchAngle(),deviceOrientation.getPitch());
			orientation[2] = calculateAngle(point.getY(), tps.getPhotoHeight(),
					tps.getCameraMaxRotationAngle(), deviceOrientation.getRoll());
			double[] coord = calculate2dPoint(orientation);
			Node node = calculateGPSPoint(tps.getLocation(), coord);
			nodes.add(node);		
		}	
		return nodes;
	}
	
	
	
	
	public Point calculate4Point(List<Point> points){
		if(points.size() != 0){
			return null;
		}
		return calculate4Point(tps, deviceOrientation, points);		
	}
	
	public Point calculate4Point(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, List<Point> points){
		
		return null;
	}
	
	
	
	
	/*

	
	public double[] calculate2dPoint(DeviceOrientation deviceOrientation){/*
		double[] orientation = new double[3];
		orientation[0] = deviceOrientation.getAzimuth();
		orientation[1] = deviceOrientation.getPitch();
		orientation[2] = deviceOrientation.getRoll();
		return calculate2dPoint(orientation);
		
		
		Location location = new Location("TEST");
		Camera mCamera = Camera.open();
		Camera.Parameters params = mCamera.getParameters();
		double rotation = Math.toRadians(params.getHorizontalViewAngle());
		double pitch = Math.toRadians(params.getVerticalViewAngle());
		
		TransformationParamBean tps = new TransformationParamBean(1.7, 
				rotation, pitch , 1000, 1000, location);
		mCamera.release();
		Log.d(TAG, "Horizontal: " + rotation);
		
		Point p = new Point(500,500);
		tps.addPoint(p);/*
		p = new Point(1,1000);
		tps.addPoint(p);
		p = new Point(1000, 1000);
		List<double[]> list = transform(tps, deviceOrientation);
		Log.d(TAG, "tranform finished");
		return list.get(0);
	}*/
	
	/**
	 * Calculates the Angle altered by the chosen Pixel
	 * @param pixel
	 * @param width
	 * @param maxAngle
	 * @param oldAngle
	 * @return
	 */
	public double calculateAngle(double pixel, double width, double maxAngle, double oldAngle){

		Log.d(TAG, "Calculate Angle, OldAngle: " + oldAngle + " maxANgle: " + maxAngle);
		if((pixel - (width / 2)) == 0){
			return oldAngle;
		}
		double percent = (width / 2) / (pixel - (width / 2));
		double angle = maxAngle * percent;	
		
		return oldAngle - angle;
	}

	
	
	/**
	 * @param orientation
	 * @return coords in m in a System with (0,0) = Phoneposition; 
	 * 			x = West/East Axis and y = Norh/South Axis
	 */
	public double[] calculate2dPoint(double[] orientation){
		double[] vector = calculateVectorfromOrientation(orientation);
		
		if(vector[2] <= 0){
			vector[2]=1;
			Log.d(TAG,"Camera is looking to the sky.");
		}		

		double[] coords = new double[2];
		double z = height / vector[2];
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
	public double[] calculateVectorfromOrientation(double[] orientation){
		
		Log.i(TAG,"Delivered Phoneorientation: azimuth = " 
				+ orientation[0] +" ,pitch = " + orientation[1]
				+ ", roll = " + orientation[2]);		
		
		//calculate fix Z with Pitch
		double z = Math.cos(orientation[1]); 
		
		//calculate temp.Y with Pitch
		double y = Math.sin(-orientation[1]); 
		double x;
		if (orientation[1] != 0.0){
			//calculate temp.X with fix Z and Roll
			x = (Math.tan(orientation[2]) * z);
		}
		else{
			x = Math.sin(orientation[2]);
			z = Math.cos(orientation[2]);
		}
		
		Log.d(TAG,"Calculated Vector without azimuth: X = " + x 
				+ " ,Y = " + y
				+ ", Z = " + z);		
		
		// Rotate Vector with Azimuth (Z is fix))
		double[] vector = new double[3];
		vector[0] =  ((x * Math.cos(orientation[0])) 
				- (y * Math.sin(orientation[0])));
		vector[1] =  ((x * Math.sin(orientation[0])) 
				+ (y * Math.cos(orientation[0])));
		vector[2] = z;	
		
		
		Log.d(TAG,"Calculated Vector: X = " + vector[0] 
				+ " ,Y = " + vector[1]
				+ ", Z = " + vector[2]);
		
		return vector;
	}
	
	
	public Node calculateGPSPoint(Location location, double[] point){
		double radius = 6371004.0;
		double lat = Math.toRadians(location.getLatitude());
		double lon = Math.toRadians(location.getLongitude());
		

		double latLength = radius * Math.cos(lat);
		latLength = latLength * 2 * Math.PI;
		double lat2 =lat + Math.toRadians(((point[0]) / (latLength / 360)));
		if (lat2 < (-Math.PI/2)){
			lat2 += Math.PI;
		}
		if (lat2 > (Math.PI/2)){
			lat2 -= Math.PI;
		}
		
		double lonLength = radius * 2 * Math.PI;
		double lon2 = lon + Math.toRadians(((point[1]) / (lonLength / 360)));
		if (lon2 > (Math.PI/4)){
			lon2 = (Math.PI/2) - lon2;
		}
		if (lon2 < (-Math.PI/4)){
			lon2 = -(Math.PI/2) + lon2;
		}						
		
		Node node = new Node(osmID, osmVersion, lat2, lon2);
		osmID--;
		return node;
	}
		
}
