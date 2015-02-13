package io.github.data4all.model.data;

import java.util.Locale;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a single point in a track. A list of trackpoints could be a
 * tracksegment in .gpx file.
 * 
 * @author sbrede
 *
 */
public class TrackPoint implements Parcelable {

    public final double latitude;
    public final double longitude;
    public final double altitude;
    public final long time;

    /**
     * CREATOR that generates instances of {@link TrackPoint} from a Parcel.
     */
    public static final Parcelable.Creator<TrackPoint> CREATOR = new Parcelable.Creator<TrackPoint>() {
        public TrackPoint createFromParcel(Parcel in) {
            return new TrackPoint(in);
        }

        public TrackPoint[] newArray(int size) {
            return new TrackPoint[size];
        }
    };

    /**
     * Constructor to create a {@link TrackPoint} from a parcel.
     * 
     * @param in
     *            The {@link Parcel} to read the object's data from
     */
    private TrackPoint(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        time = in.readLong();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeLong(time);
    }

    public boolean equals(TrackPoint tp) {
        int lat = Double.compare(this.getLat(), tp.getLat());
        int lon = Double.compare(this.getLon(), tp.getLon());
        int alt = Double.compare(this.getAlt(), tp.getAlt());
        int time = Double.compare(this.getTime(), tp.getTime());
        if (lat == 0 && lon == 0 && alt == 0 && time == 0) {
            return true;
        }
        return false;
    }
}
