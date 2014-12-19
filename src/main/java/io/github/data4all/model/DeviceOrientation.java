package io.github.data4all.model;

/**
 * this class represent the model for OrientationListener
 * @author: Steeve
 */
public class DeviceOrientation {
	
	/** rotation around the Z axis */
	private float azimuth;
	/** rotation around the X axis */
	private float pitch;
    /** rotation around Y axis	*/
	private float roll;
	
    private	long timestamp;
    
    
    public DeviceOrientation(float azimuth, float pitch, float roll, long timestamp){
    	this.azimuth = azimuth;
    	this.pitch = pitch;
    	this.roll = roll;
    	this.timestamp = timestamp;
    }
    
    /** all getter and setter Method */
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
