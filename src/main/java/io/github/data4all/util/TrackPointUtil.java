/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.util;

import java.util.List;

import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.TrackPoint;

/**
 * This class represents a utility for the creation of TrackPoints.
 * 
 * @author K. Dahnken
 * 
 */
public final class TrackPointUtil {

    private static double epsilon = 0.0000001;
    private static double circumferenceEarth = 40075.017; // km
    private static double distanceToCover = 0.005; // 5 meters in km

    /**
     * Default constructor.
     */
    private TrackPointUtil() {
    }

    /**
     * This method checks if a given track point already exists in the database.
     * 
     * @param trackPoint
     *            the given track point
     * @param db
     *            the database
     * @return <b>true</b> if the track point exists, <b>false</b> otherwise
     */
    public static boolean trackPointExists(TrackPoint trackPoint,
            DataBaseHandler db) {
        final List<TrackPoint> allTrackPoints = db.getAllTrackPoints();

        for (TrackPoint tp : allTrackPoints) {

            final double lat = Math.abs(tp.getLat() - trackPoint.getLat());
            final double lon = Math.abs(tp.getLon() - trackPoint.getLon());
            final double alt = Math.abs(tp.getAlt() - trackPoint.getAlt());

            if (lat < epsilon && lon < epsilon && alt < epsilon) {
                return true;
            }
        }
        return false;
    }

    public static void distanceCovered(TrackPoint currentTrackPoint) {
        
        double newLat = Double.NaN;
        double newLon = Double.NaN;

        double i = 360 * distanceToCover / circumferenceEarth; // N/S
        double ii = 360 * distanceToCover
                / (circumferenceEarth * Math.cos(currentTrackPoint.getLat())); // E/W

        // north
        newLat = currentTrackPoint.getLat() + i;
        newLon = currentTrackPoint.getLon();
        // south
        newLat = currentTrackPoint.getLat() - i;
        newLon = currentTrackPoint.getLon();
        // east
        newLat = currentTrackPoint.getLat();
        newLon = currentTrackPoint.getLon() - ii;
        // west
        newLat = currentTrackPoint.getLat();
        newLon = currentTrackPoint.getLon() + ii;
    }

}
