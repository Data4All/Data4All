package io.github.data4ll.model;

/**
 * this class represent the model for PositionListener
 * @author: Steeve
 */
public class DevicePosition {
	
	//rotation around the Z axis
	private float azimuth;
	//rotation around the X axis
	private float pitch;
    // rotation around Y axis	
	private float roll;
	
    private	long timestamp;
    
    
    public DevicePosition(float azimuth, float pitch, float roll, long timestamp){
    	this.azimuth = azimuth;
    	this.pitch = pitch;
    	this.roll = roll;
    	this.timestamp = timestamp;
    }
    
    //all getter and setter Methoden
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
