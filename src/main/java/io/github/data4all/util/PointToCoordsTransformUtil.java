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
		this.height = tps.getHeight();				
		for(Point point : points){			
			double[] coord = calculateCoordFromPoint(tps, deviceOrientation, point);
			Node node = calculateGPSPoint(tps.getLocation(), coord);
			nodes.add(node);		
		}	
		return nodes;
	}
	
	
	
	
	public Point calculate4Point(List<Point> points){
		return calculate4Point(tps, deviceOrientation, points);		
	}
	
	
	
	public Point calculate4Point(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, List<Point> points){
		if(points.size() != 3){
			return null;
		}
		List<double[]> coords = new ArrayList<double[]>();
		this.height = tps.getHeight();			
		for(Point point : points){
			coords.add(calculateCoordFromPoint(tps, deviceOrientation, point));					
		}	
		double[] coord  = add4Point(coords);
		return calculatePointFromCoords(tps, deviceOrientation, coord);
	}
	
	
	
	public double[] calculateCoordFromPoint(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, Point point){
		this.height = tps.getHeight();
		double[] orientation = new double[3];
		orientation[0] = -deviceOrientation.getAzimuth();
		orientation[1] = calculateAngleFromPixel(point.getX(), tps.getPhotoWidth(),
				tps.getCameraMaxPitchAngle(),deviceOrientation.getPitch());
		orientation[2] = calculateAngleFromPixel(point.getY(), tps.getPhotoHeight(),
				tps.getCameraMaxRotationAngle(), deviceOrientation.getRoll());
		double[] vector = calculateVectorfromOrientation(orientation);
		return calculate2dPoint(vector);
	}
	
	
	
	/**
     * Calculates the fourth point in dependence of the first three points of
     * the given list
     * 
     * @param areaPoints
     *            A list with exact three points
     */
    private static double[] add4Point(List<double[]> coords) {
    	double[] a = coords.get(0);
      	double[] b = coords.get(1);
      	double[] c = coords.get(2);
        double[] coord = new double[2];
        coord[0] = a[0] + (c[0] - b[0]);
        coord[1] = a[1] + (c[1] - b[1]);
        return coord;
    }
	
	

	
	/**
	 * Calculates the Angle altered by the chosen Pixel
	 * @param pixel
	 * @param width
	 * @param maxAngle
	 * @param oldAngle
	 * @return
	 */
	public double calculateAngleFromPixel(double pixel, double width, double maxAngle, double oldAngle){

		Log.d(TAG, "Calculate Angle, OldAngle: " + oldAngle + " maxANgle: " + maxAngle);
		if((pixel - (width / 2)) == 0){
			return oldAngle;
		}
		double percent = pixel  / width;
		double angle = maxAngle * percent;	
		angle = angle - (maxAngle / 2);
		return oldAngle - angle;
	}

	
	
	/**
	 * @param orientation
	 * @return coords in m in a System with (0,0) = Phoneposition; 
	 * 			x = West/East Axis and y = Norh/South Axis
	 */
	public double[] calculate2dPoint(double[] vector){
		
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
	
	
	public Point calculatePointFromCoords(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, double[] coord){
		double x =  ((coord[0] * Math.cos(deviceOrientation.getAzimuth())) 
				- (coord[1] * Math.sin(deviceOrientation.getAzimuth())));
		double y =  ((coord[0] * Math.sin(deviceOrientation.getAzimuth())) 
				+ (coord[1] * Math.cos(deviceOrientation.getAzimuth())));
		double rx = x / tps.getHeight();
		double py = y / tps.getHeight();
		double rotation = Math.atan(rx);
		double pitch = Math.atan(py);
		double rotation2 = +rotation-deviceOrientation.getRoll();
		double pitch2 = pitch+deviceOrientation.getPitch();		

		double x1 =  (pitch2 + (tps.getCameraMaxPitchAngle() / 2)) / tps.getCameraMaxPitchAngle();
		double y1 =  -((rotation2 - (tps.getCameraMaxRotationAngle() / 2)) / tps.getCameraMaxRotationAngle()) ;
		
		float xx =(float) ((int) ( x1 * tps.getPhotoWidth()));
		float yy =(float) ((int) ( y1 * tps.getPhotoHeight()));
		Point point = new Point(xx, yy);
		return point;
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
		lat2 = Math.toDegrees(lat2);
		lon2 = Math.toDegrees(lon2);
		
		
		Node node = new Node(osmID, osmVersion, lat2, lon2);
		osmID--;
		return node;
	}
		
}
