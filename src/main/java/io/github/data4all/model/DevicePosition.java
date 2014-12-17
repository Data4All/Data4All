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
    
    public DevicePosition(long timestamp,float xAccel,float yAccel,float zAccel,
    		float azimuth,float pitch,float roll){
    	this.timestamp = timestamp;
    	xAccel= accelValues[0];
    	yAccel= accelValues[1];
    	zAccel = accelValues[2];
    	azimuth = magValues[0];
    	pitch = magValues[1];
    	roll = magValues[2];
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
