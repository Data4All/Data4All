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
	
	/**
	 * Constructor, which some Data
	 * @param tps
	 * @param deviceOrientation
	 */
	public PointToCoordsTransformUtil(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation) {
		this.tps = tps;
		this.deviceOrientation = deviceOrientation;		
	}
	
	/**
	 * Opens transform with saved informations
	 * @param points
	 * @return List of Nodes
	 */
	public List<Node> transform(List<Point> points){
		return transform(tps, deviceOrientation, points);
	}
	
	/**
	 *  transforms a List of Points in a List of GPS-coordinates
	 * @param tps
	 * @param deviceOrientation
	 * @return List of Nodes
	 */
	public List<Node> transform(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, List<Point> points){
		
		List<Node> nodes = new ArrayList<Node>();
		this.height = tps.getHeight();				
		for(Point point : points){		
			// first calculates local coordinates in meter
			double[] coord = calculateCoordFromPoint(tps, deviceOrientation, point);
			// transforms local coordinates in global GPS-coordinates
			Node node = calculateGPSPoint(tps.getLocation(), coord);
			nodes.add(node);		
		}	
		return nodes;
	}
	
	
	
	

	/**
	 * opens calculate4thPoint with saved information
	 * @param points
	 * @return
	 */
	public Point calculate4thPoint(List<Point> points){
		return calculate4thPoint(tps, deviceOrientation, points);		
	}
	
	
	/**
	 * calculates a 4th Point (for houses etc.) with 3 given Points
	 * @param points
	 * @return the 4th Point
	 */
	public Point calculate4thPoint(TransformationParamBean tps, 
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
	
	
	
	/**
	 * calculates local coordinates for a Point 
	 * with the orientation of the phone,
	 *  the pixel and information of the camera
	 * @param tps
	 * @param deviceOrientation
	 * @param point
	 * @return coordinates in a local system
	 */
	public double[] calculateCoordFromPoint(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, Point point){
		this.height = tps.getHeight();
		double[] orientation = new double[3];
		orientation[0] = -deviceOrientation.getAzimuth();
		orientation[1] = calculateAngleFromPixel(point.getX(), tps.getPhotoWidth(),
				tps.getCameraMaxPitchAngle(),deviceOrientation.getPitch());
		orientation[2] = calculateAngleFromPixel(point.getY(), tps.getPhotoHeight(),
				tps.getCameraMaxRotationAngle(), deviceOrientation.getRoll());
		
		if(orientation[1] <= (float) (-Math.PI/2) || orientation[1] >= (float) (Math.PI/2)
				|| orientation[2] <= (float) (-Math.PI/2) || orientation[2] >= (float) (Math.PI/2)){
			double[] fail = {0.0,0.0,-1};
			return fail;
		}
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
	 * Calculates the Angle altered by the given Pixel
	 * @param pixel
	 * @param width
	 * @param maxAngle
	 * @param oldAngle
	 * @return altered Angle
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
		double[] coord = new double[3];
		double z = height / vector[2];
		coord[0] = vector[0] * z;
		coord[1] = vector[1] * z;		
		coord[2] = 0;	
		Log.d(TAG,"Calculated X = " + coord[0] + " and Y = " + coord[1]);
		return coord;		
	}
	
	
	/**
	 * calculates 
	 * @param tps
	 * @param deviceOrientation
	 * @param coord
	 * @return calculated Point
	 */
	public Point calculatePointFromCoords(TransformationParamBean tps, 
			DeviceOrientation deviceOrientation, double[] coord){
		if (coord[2] == -1){
			Point point = new Point(1, 1);
			return point;
		}
		// rotates the vector with azimuth
		double x =  ((coord[0] * Math.cos(deviceOrientation.getAzimuth())) 
				- (coord[1] * Math.sin(deviceOrientation.getAzimuth())));
		double y =  ((coord[0] * Math.sin(deviceOrientation.getAzimuth())) 
				+ (coord[1] * Math.cos(deviceOrientation.getAzimuth())));
		// calculates the rotation and pitch of the Pixel given the approximate 
		// height and orientation of the phone 
		double rotation = Math.atan(x / tps.getHeight())
				-deviceOrientation.getRoll();
		double pitch = Math.atan(y / tps.getHeight())
				+deviceOrientation.getPitch();	

		// calculates the multiplier of the Pixel
		double x2 =  (pitch + (tps.getCameraMaxPitchAngle() / 2)) / tps.getCameraMaxPitchAngle();
		double y2 =  -((rotation - (tps.getCameraMaxRotationAngle() / 2)) / tps.getCameraMaxRotationAngle()) ;
		
		// multiply with the Width and Height of the Photo
		float xx =(float) ((int) ( x2 * tps.getPhotoWidth()));
		float yy =(float) ((int) ( y2 * tps.getPhotoHeight()));
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
		
		//calculate fix Z with Pitch
		double z = Math.cos(orientation[1]); 
		
		//calculate temp.Y with Pitch
		double y = Math.sin(-orientation[1]); 
		double x;
		// if the pitch is Zero, it wouldn't be possible to calculate the X-Variable
		if (orientation[1] != 0.0){
			//calculate a temporary X with fix Z and Roll
			x = (Math.tan(orientation[2]) * z);
		}
		else{
			//calculate new Z and X, Y is 0
			x = Math.sin(orientation[2]);
			z = Math.cos(orientation[2]);
		}
		
		// Rotate Vector with Azimuth (Z is fix))
		double[] vector = new double[3];
		vector[0] =  ((x * Math.cos(orientation[0])) 
				- (y * Math.sin(orientation[0])));
		vector[1] =  ((x * Math.sin(orientation[0])) 
				+ (y * Math.cos(orientation[0])));
		vector[2] = z;	
		
		return vector;
	}
	
	
	/**
	 * calculates GPS-Point from Coordinates in a local System and the given Location
	 * @param location
	 * @param point
	 * @return A Node with a GPS Point
	 */
	public Node calculateGPSPoint(Location location, double[] coord){
		double radius = 6371004.0;
		double lat = Math.toRadians(location.getLatitude());
		double lon = Math.toRadians(location.getLongitude());
		
		// calculate the Length of the current Latitude with the earth Radius
		double latLength = radius * Math.cos(lat);
		latLength = latLength * 2 * Math.PI;
		// add to the current Latitude the distance of the coord
		double lat2 =lat + Math.toRadians(((coord[0]) / (latLength / 360)));
		/*
		if (lat2 < (-Math.PI/2)){
			lat2 += Math.PI;
		}
		if (lat2 > (Math.PI/2)){
			lat2 -= Math.PI;
		}*/
		
		// calculate the Length of the current Longitude with the earth Radius
		double lonLength = radius * 2 * Math.PI;
		// add to the current Longitude the distance of the coord
		double lon2 = lon + Math.toRadians(((coord[1]) / (lonLength / 360)));
		/*
		if (lon2 > (Math.PI/4)){
			lon2 = (Math.PI/2) - lon2;
		}
		if (lon2 < (-Math.PI/4)){
			lon2 = -(Math.PI/2) + lon2;
		}		*/				
		lat2 = Math.toDegrees(lat2);
		lon2 = Math.toDegrees(lon2);		
		
		Node node = new Node(osmID, osmVersion, lat2, lon2);
		osmID--;
		return node;
	}
	
	public double[] calculateCoordFromGPS(Location location, Node node){
		double radius = 6371004.0;
		double lat = Math.toRadians(node.getLat() - location.getLatitude());
		double lon = Math.toRadians(node.getLon() - location.getLongitude());
		double localLat = Math.toRadians(location.getLatitude());
		
		// calculate the Length of the current Latitude with the earth Radius
		double latLength = radius * Math.cos(localLat);
		latLength = latLength * 2 * Math.PI;
		
		double[] coord = new double[3];
		coord[0] = latLength * lat;
		
		double lonLength = radius * 2 * Math.PI;
		coord[1] = lonLength * lon;
		coord[2] = 0;
		
		return coord;
	}
		
}
