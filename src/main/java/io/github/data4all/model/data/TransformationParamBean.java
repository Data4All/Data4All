package io.github.data4all.model.data;

import java.util.ArrayList;

import io.github.data4all.model.data.GPSBean;
import io.github.data4all.model.drawing.Point;

public class TransformationParamBean {
	private float height;
	private float cameraMaxRotationAngle;
	private float cameraMaxPitchAngle;
	private int photoWidth;
	private int photoHeight;
	private ArrayList<Point> points = new ArrayList<Point>();

	private TransformationParamBean(float height, float cameraMaxRotationAngle, 
					float cameraMaxPitchAngle, int photoWidth, int photoHeight){
		this.height = height;
		this.cameraMaxPitchAngle = cameraMaxPitchAngle;
		this.cameraMaxRotationAngle = cameraMaxRotationAngle;
		this.photoHeight = photoHeight;
		this.photoWidth = photoWidth;

	}
	
	
	
	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getCameraMaxRotationAngle() {
		return cameraMaxRotationAngle;
	}

	public void setCameraMaxRotationAngle(float cameraMaxRotationAngle) {
		this.cameraMaxRotationAngle = cameraMaxRotationAngle;
	}

	public float getCameraMaxPitchAngle() {
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

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	
	public void addPoint(Point point){
		points.add(point);
	}



}
