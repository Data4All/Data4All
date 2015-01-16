package io.github.data4all.model.data;

import java.util.ArrayList;

import android.location.Location;
import io.github.data4all.model.data.GPSBean;
import io.github.data4all.model.drawing.Point;

public class TransformationParamBean {
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



}
