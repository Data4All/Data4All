package io.github.data4all.model.data;

import java.util.ArrayList;

import io.github.data4all.model.data.GPSBean;
import io.github.data4all.model.drawing.Point;

public class TransformationParamBean {
	private double height;
	private double cameraMaxRotationAngle;
	private double cameraMaxPitchAngle;
	private int photoWidth;
	private int photoHeight;
	private ArrayList<Point> points = new ArrayList<Point>();

	public TransformationParamBean(double height, double cameraMaxRotationAngle, 
					double cameraMaxPitchAngle, int photoWidth, int photoHeight){
		this.height = height;
		this.cameraMaxPitchAngle = cameraMaxPitchAngle;
		this.cameraMaxRotationAngle = cameraMaxRotationAngle;
		this.photoHeight = photoHeight;
		this.photoWidth = photoWidth;

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
