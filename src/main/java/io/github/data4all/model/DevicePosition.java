package io.github.data4all.model;

import io.github.data4all.model.drawing.Point;

import java.util.Calendar;

import android.location.Location;

/**
 * A model for the postion of the device. It includes the location and the
 * rotation of the device.
 * 
 * @author sbollen
 *
 */

public class DevicePosition {

    // Location of the device (GPS)
    Location location;

    // ACCELEROMETER Sensor Values
    private float[] accelValues = new float[3];
    // MAGNETIC_FIELD Sensor Values
    private float[] magValues = new float[3];

    // Actual time when the data was recorded
    private long timestamp;
    
    public DevicePosition() {
        
    }

    public DevicePosition(Location location, float[] accelValues,
            float[] magValues, long timestamp) {
        this.location = location;
        this.accelValues = accelValues;
        this.magValues = magValues;
        this.timestamp = timestamp;

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float[] getAccelValues() {
        return accelValues;
    }

    public void setAccelValues(float[] accelValues) {
        this.accelValues = accelValues;
    }

    public float[] getMagValues() {
        return magValues;
    }

    public void setMagValues(float[] magValues) {
        this.magValues = magValues;
    }

    public boolean equals(Object o) {
        return this == o
                || ((o instanceof DevicePosition)
                        && location == ((DevicePosition) o).getLocation()
                        && accelValues == ((DevicePosition) o).getAccelValues()
                        && magValues == ((DevicePosition) o).getMagValues());
    }
}
