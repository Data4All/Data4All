package io.github.data4all.model.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A node is one of the core elements in the OpenStreetMap data model. It
 * consists of a single point in space defined by its latitude, longitude and
 * node id.
 * 
 * @author fkirchge
 *
 */
public class Node extends OsmElement implements GeoPoint {

    /**
     * Latitude and Longitude of the Node.
     */
    private double lat;
    private double lon;

    /**
     * Default Constructor
     * 
     * @param osmId
     * @param osmVersion
     * @param lat
     *            Latitude is a decimal number between -90.0 and 90.0.
     * @param lon
     *            Longitude is a decimal number between -180.0 and 180.0.
     */
    public Node(final long osmId, final long osmVersion, final double lat,
            final double lon) {
        super(osmId, osmVersion);
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(final double lat) {
        this.lat = lat;
    }

    public void setLon(final double lon) {
        this.lon = lon;
    }

    /**
     * Methods to write and restore a Parcel
     */
    public static final Parcelable.Creator<Node> CREATOR
            = new Parcelable.Creator<Node>() {
    	
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        public Node[] newArray(int size) {
            return new Node[size];
        }
    };
    
    
    public int describeContents() {
		return 0;
	}

    /**
     * Writes the lat and the lon to the given parcel
     */
	public void writeToParcel(Parcel dest, int flags) {		
		super.writeToParcel(dest, flags);
		dest.writeDouble(lat);
		dest.writeDouble(lon);
	}
	
	/**
	 * Returns the Node as a GeoPoint representation
	 * 
	 * @return the node as a GeoPoint representation
	 */
	public org.osmdroid.util.GeoPoint toGeoPoint(){
		return new org.osmdroid.util.GeoPoint(lat,lon);
	}
	
	/**
	 * Constructor to create a node from a parcel
	 * @param in
	 */
    private Node(Parcel in) {
    	super(in);
    	lat = in.readDouble();
    	lon = in.readDouble();
    }

    public boolean equals(Node node){
		return node.getLat()==lat && node.getLon()==lon;
    	
    }
    
    public String toString(){
    	return toGeoPoint().toString();
    }
}