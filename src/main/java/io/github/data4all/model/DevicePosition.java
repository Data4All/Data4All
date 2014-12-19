package io.github.data4all.model;

/**
 * A model for the postion of the device. It includes the location and the
 * rotation of the device.
 * 
 * @author sbollen, Steeve
 * 
 */

public class DevicePosition {
 
	
	//rotation around the Z axis
	private float azimuth;
   // rotation around the X axis	
	private float pitch;
	// rotation around Y axis
	private float roll;
	
	private long timestamp;

	public DevicePosition(long timestamp, float azimuth, float pitch, float roll) {
		this.timestamp = timestamp;
		this.azimuth = azimuth;
		this.pitch = pitch;
		this.roll = roll;

	}
    
	//getter and setter Methoden
	
	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
