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
public class Node extends AbstractDataElement {

    /**
     * Latitude of the Node.
     */
    private double lat;

    /**
     * Longitude of the Node.
     */
    private double lon;

    /**
     * CREATOR that generates instances of {@link Node} from a Parcel.
     */
    public static final Parcelable.Creator<Node> CREATOR =
            new Parcelable.Creator<Node>() {
                public Node createFromParcel(Parcel in) {
                    return new Node(in);
                }

                public Node[] newArray(int size) {
                    return new Node[size];
                }
            };

    /**
     * Default Constructor.
     * 
     * @param osmId
     *            the id of the osm element
     * @param lat
     *            Latitude is a decimal number between -90.0 and 90.0.
     * @param lon
     *            Longitude is a decimal number between -180.0 and 180.0.
     */
    public Node(final long osmId, final double lat, final double lon) {
        super(osmId);
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Constructor to create a {@link Node} from a parcel.
     * 
     * @param in
     *            The {@link Parcel} to read the object's data from
     */
    private Node(Parcel in) {
        super(in);
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Checks if two Nodes are equal.
     * 
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final Node other = (Node) obj;
        return (Double.doubleToLongBits(lat) == Double
                .doubleToLongBits(other.lat))
                && (Double.doubleToLongBits(lon) == Double
                        .doubleToLongBits(other.lon))
                && getOsmId() == other.getOsmId();
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
     * Returns the Node as a GeoPoint representation.
     * 
     * @return the node as a GeoPoint representation
     */
    public org.osmdroid.util.GeoPoint toGeoPoint() {
        return new org.osmdroid.util.GeoPoint(lat, lon);
    }

    /**
     * Created a new GeoPoint and calls the toString method.
     * 
     * @return returns a geopoint and calls the to string method
     */
    @Override
    public String toString() {
        return this.toGeoPoint().toString();
    }

    /**
     * Writes the lat and the lon to the given parcel.
     * 
     * @param dest
     *            destination oparcel
     * @param flags
     *            additional flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

}