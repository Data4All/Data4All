package io.github.data4all.model.data;

/**
 * Geopoints are objects that contain latitude and longitude coordinates.
 * 
 * @author fkirchge
 *
 */
public interface GeoPoint {

    /** @return the latitude of this point */
    public double getLat();

    /** @return the longitude of this point */
    public double getLon();

}