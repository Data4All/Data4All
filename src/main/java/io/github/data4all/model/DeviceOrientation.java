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
package io.github.data4all.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * this class represent the model for OrientationListener.
 * 
 * @author: Steeve, fkirchge
 */
public class DeviceOrientation implements Parcelable {

    /** rotation around the Z axis. */
    private float azimuth;
    /** rotation around the X axis. */
    private float pitch;
    /** rotation around Y axis. */
    private float roll;

    /** constants for comparison. */
    private static final double EPSILON = 0.00001;

    private long timestamp;

    /**
     * CREATOR that generates instances of {@link DeviceOrientation} from a
     * Parcel.
     */
    public static final Parcelable.Creator<DeviceOrientation> CREATOR =
            new Parcelable.Creator<DeviceOrientation>() {
                public DeviceOrientation createFromParcel(Parcel in) {
                    return new DeviceOrientation(in);
                }

                public DeviceOrientation[] newArray(int size) {
                    return new DeviceOrientation[size];
                }
            };

    /**
     * constructor.
     * 
     * @param azimuth
     *            the azimuth value
     * @param pitch
     *            the pitch value
     * @param roll
     *            the roll value
     * @param timestamp
     *            the timestamp value
     */
    public DeviceOrientation(float azimuth, float pitch, float roll,
            long timestamp) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
        this.timestamp = timestamp;
    }

    /**
     * Constructor to create a node from a parcel.
     * 
     * @param in
     *            The Parcel to read from
     */
    private DeviceOrientation(Parcel in) {
        azimuth = in.readFloat();
        pitch = in.readFloat();
        roll = in.readFloat();
        timestamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if ((o instanceof DeviceOrientation)
                && (Math.abs(azimuth - ((DeviceOrientation) o).getAzimuth()) < EPSILON)
                && (Math.abs(pitch - ((DeviceOrientation) o).getPitch()) < EPSILON)
                && (Math.abs(roll - ((DeviceOrientation) o).getRoll()) < EPSILON)
                && timestamp == ((DeviceOrientation) o).getTimestamp()
                && this.hashCode() == o.hashCode()) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = (int) (hash * 17 + azimuth);
        hash = (int) (hash * 13 + pitch);
        hash = (int) (hash * 23 + roll);
        hash = (int) (hash * 19 + timestamp);
        return hash;

    }

    /** all getter and setter methods. */
    public float getAzimuth() {
        return azimuth;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(azimuth);
        dest.writeFloat(pitch);
        dest.writeFloat(roll);
        dest.writeLong(timestamp);
    }

    public JSONArray toJSON() throws JSONException {
        List<Object> attributes = new ArrayList<Object>(4);
        attributes.add((double) azimuth);
        attributes.add((double) pitch);
        attributes.add((double) roll);
        attributes.add(timestamp);
        return new JSONArray(attributes);
    }

    public static DeviceOrientation fromJSON(JSONArray json)
            throws JSONException {

        return new DeviceOrientation((float) json.getDouble(0),
                (float) json.getDouble(1), (float) json.getDouble(2),
                json.getLong(3));
    }
}
