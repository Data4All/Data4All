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
    
    /**
     * ID of the TrackPoint, set to default.
     */
    private long id = -1;

    /**
     * Latitude of the Trackpoint.
     */
    private final double latitude;

    /**
     * Longitude of the Trackpoint.
     */
    private final double longitude;

    /**
     * Alitude of the Trackpoint.
     */
    private final double altitude;

    /**
     * Timestamp of the Trackpoint.
     */
    private final long time;

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
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.altitude = in.readDouble();
        this.time = in.readLong();
    }

    /**
     * Default constructor of a TrackPoint.
     * 
     * @param original
     *            Location object
     */
    public TrackPoint(Location original) {
        this.latitude = original.getLatitude();
        this.longitude = original.getLongitude();
        if (original.hasAltitude()) {
            this.altitude = original.getAltitude();
        } else {
            this.altitude = Double.NaN;
        }
        this.time = original.getTime();
    }
    
    public void setID(long id) {
        this.id = id;
    }
    
    public long getID() {
        return id;
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
        if (!Double.isNaN(altitude)) {
            return altitude;
        } else {
            return 0d;
        }
    }

    /**
     * Returns if the object has an altitude.
     * 
     * @return true if TrackPoint has an altitude
     */
    public boolean hasAltitude() {
        return !Double.isNaN(altitude);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.US, "%f, %f", latitude, longitude);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the latitude, longitude, altidude and time to a parcel.
     *
     * @param dest
     *            destination parcel
     * @param flags
     *            additional flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeLong(time);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(altitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TrackPoint other = (TrackPoint) obj;
        return Double.doubleToLongBits(altitude) == Double
                .doubleToLongBits(other.altitude)
                && Double.doubleToLongBits(latitude) == Double
                        .doubleToLongBits(other.latitude)
                && Double.doubleToLongBits(longitude) == Double
                        .doubleToLongBits(other.longitude)
                && time == other.time;
    }

}
