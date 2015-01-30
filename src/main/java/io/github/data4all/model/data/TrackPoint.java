package io.github.data4all.model.data;

import java.util.Locale;

import android.location.Location;

/**
 * Represents a single point in a track. A list of trackpoints could be a
 * tracksegment in .gpx file.
 * 
 * @author sbrede
 *
 */
public class TrackPoint {

    public final double latitude;
    public final double longitude;
    public final double altitude;
    public final long time;

    public TrackPoint(Location original) {
        latitude = original.getLatitude();
        longitude = original.getLongitude();
        altitude = original.hasAltitude() ? original.getAltitude() : Double.NaN;
        time = original.getTime();
    }

    public double getLat() {
        return latitude;
    }

    public double getLon() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public double getAlt() {
        return !Double.isNaN(altitude) ? altitude : 0d;
    }

    public boolean hasAltitude() {
        return !Double.isNaN(altitude);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%f, %f", latitude, longitude);
    }
}
