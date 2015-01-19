package io.github.data4all.model.data;

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
        osmType = "NODE";
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


}
