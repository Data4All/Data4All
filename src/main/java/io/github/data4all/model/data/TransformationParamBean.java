package io.github.data4all.model.data;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


public class TransformationParamBean implements Parcelable {
	
	private double height;
	private double cameraMaxRotationAngle;
	private double cameraMaxPitchAngle;
	private int photoWidth;
	private int photoHeight;
	private Location location;

	public TransformationParamBean(double height, double cameraMaxRotationAngle, 
					double cameraMaxPitchAngle, int photoWidth, int photoHeight, Location location){
		this.height = height;
		this.cameraMaxPitchAngle = cameraMaxPitchAngle;
		this.cameraMaxRotationAngle = cameraMaxRotationAngle;
		this.photoHeight = photoHeight;
		this.photoWidth = photoWidth;
		this.location = location;
	}
	
	public double getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public double getCameraMaxRotationAngle() {
		return cameraMaxRotationAngle;
	}

	public void setCameraMaxRotationAngle(float cameraMaxRotationAngle) {
		this.cameraMaxRotationAngle = cameraMaxRotationAngle;
	}

	public double getCameraMaxPitchAngle() {
		return cameraMaxPitchAngle;
	}

	public void setCameraMaxPitchAngle(float cameraMaxPitchAngle) {
		this.cameraMaxPitchAngle = cameraMaxPitchAngle;
	}

	public int getPhotoWidth() {
		return photoWidth;
	}

	public void setPhotoWidth(int photoWidth) {
		this.photoWidth = photoWidth;
	}

	public int getPhotoHeight() {
		return photoHeight;
	}

	public void setPhotoHeight(int photoHeight) {
		this.photoHeight = photoHeight;
	}


	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

    /**
     * Methods to write and restore a Parcel.
     */
    public static final Parcelable.Creator<TransformationParamBean> CREATOR
            = new Parcelable.Creator<TransformationParamBean>() {
    	
        public TransformationParamBean createFromParcel(Parcel in) {
            return new TransformationParamBean(in);
        }

        public TransformationParamBean[] newArray(int size) {
            return new TransformationParamBean[size];
        }
    };
    
    
    public int describeContents() {
		return 0;
	}
    
    /**
     * Writes the nodes to the given parcel.
     */
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeDouble(height);
		dest.writeDouble(cameraMaxRotationAngle);
		dest.writeDouble(cameraMaxPitchAngle);
		dest.writeInt(photoWidth);
		dest.writeInt(photoHeight);
		if (location != null) {
			dest.writeInt(1);
			dest.writeString(location.getProvider());
			dest.writeDouble(location.getLatitude());
			dest.writeDouble(location.getLongitude());
		} else {
			dest.writeInt(0);
		}
	}

	/**
	 * Constructor to create a transformationbean from a parcel.
	 * @param in
	 */
    private TransformationParamBean(Parcel in) {
    	height = in.readDouble();
    	cameraMaxRotationAngle = in.readDouble();
    	cameraMaxPitchAngle = in.readDouble();
    	photoWidth = in.readInt();
    	photoHeight = in.readInt();
    	if (in.readInt() != 0) {
    		location = new Location(in.readString());
    		location.setLatitude(in.readDouble());
    		location.setLongitude(in.readDouble());
    	}
    }	

	
}
