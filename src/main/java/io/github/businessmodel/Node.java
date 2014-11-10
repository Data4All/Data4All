package io.github.businessmodel;

/**
 * This class represents a node/point on the open street map.
 * A node consists of a single point in space defined by its latitude, longitude and node id.
 * Nodes can be used to define standalone point features, but are more often used to define the shape or "path" of a way.  
 * @author Felix Kirchgeorg
 *
 */
public class Node extends OsmObject {

	private double latitude;
	private double longitude;
	
	public Node() {
		
	}
	
	public Node(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
}