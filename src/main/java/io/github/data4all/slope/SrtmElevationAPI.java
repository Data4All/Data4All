/**
 * 
 */
package io.github.data4all.slope;

import io.github.data4all.model.data.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;

/**
 * @author sbollen
 *
 */

public class SrtmElevationAPI implements ElevationAPI {

    private SrtmHelper osmSrtm;

    /**
     * Init the SRTM based ElevationApi
     *
     * @param localDir
     *            The local folder that contains the .hgt or .zip srtm files
     */
    public SrtmElevationAPI(File localDir) {
        osmSrtm = new SrtmHelper(localDir);
    }

    @Override
    public double getElevation(double lat, double lon) throws IOException {
        return osmSrtm.srtmHeight(lat, lon);
    }

    @Override
    public double getElevation(Location p) throws IOException {
        return osmSrtm.srtmHeight(p.getLatitude(), p.getLongitude());
    }

    @Override
    public void setElevation(Location p) throws IOException {
        //p.setElevation(getElevation(p));
    }

    @Override
    public List<Node> getElevations(List<Location> points)
            throws IOException {
        List<Node> newPoints = new ArrayList<Node>(points.size());
        Node p;
        for (int i = 0; i < points.size(); i++) {
            p = new Node(-1, points.get(i).getLatitude(), points.get(i)
                    .getLongitude());
            //setElevation(p);
            newPoints.add(p);
        }
        return newPoints;
    }

    @Override
    public void setElevations(List<Location> points) throws IOException {
        for (Location p : points) {
            //setElevation(p);
        }
    }

}
