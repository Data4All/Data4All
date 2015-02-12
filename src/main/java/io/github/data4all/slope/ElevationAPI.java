/**
 * 
 */
package io.github.data4all.slope;

import io.github.data4all.model.data.Node;

import java.util.List;

import android.location.Location;

/**
 * @author sbollen
 *
 */

public interface ElevationAPI {

    /**
     * Get the elevation of a given latitude and longitude
     *
     * @param lat
     *            Latitude
     * @param lon
     *            Longitude
     * @return The elevation of given point
     */
    public double getElevation(double lat, double lon) throws Exception;

    /**
     * Gets the elevation of the given points.
     *
     * @param points
     *            List of GeoPoints
     * @return List of GeoPoint that contains the elevations of given points
     * @throws Exception
     *             Exceptions thrown by fetching data
     */
    public List<Node> getElevations(List<Location> points) throws Exception;

    /**
     * Sets the elevation of the given points
     *
     * @param points
     *            Points
     * @throws Exception
     *             Exceptions thrown by fetching data
     */
    public void setElevations(List<Location> points) throws Exception;

    public void setElevation(Location p) throws Exception;

    double getElevation(Location p) throws Exception;

}
