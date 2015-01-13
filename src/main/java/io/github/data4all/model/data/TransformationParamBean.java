package io.github.data4all.model.data;

import io.github.data4all.model.data.GPSBean;

public class TransformationParamBean {
	private float height;
	private double rotate;
	private float cameraVerticalAngle;
	private float cameraHorizontalAngle;
	private float cameraDistanceInPixel;
	private int photoWidth;
	private int photoHeight;
	private GPSBean observerPostion;

	private TransformationParamBean(float h, double r,
			float cva, float cha, float cdip, int cw, int ch, GPSBean op) {
		this.height = h;
		this.rotate = r;
		this.cameraVerticalAngle = cva;
		this.cameraHorizontalAngle = cha;
		this.cameraDistanceInPixel = cdip;
		this.photoWidth = cw;
		this.photoHeight = ch;
		this.observerPostion = op;

	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public double getRotate() {
		return rotate;
	}

	public void setRotate(double rotate) {
		this.rotate = rotate;
	}

	public float getCameraVerticalAngle() {
		return cameraVerticalAngle;
	}

	public void setCameraVerticalAngle(float cameraVerticalAngle) {
		this.cameraVerticalAngle = cameraVerticalAngle;
	}

	public float getCameraHorizontalAngle() {
		return cameraHorizontalAngle;
	}

	public void setCameraHorizontalAngle(float cameraHorizontalAngle) {
		this.cameraHorizontalAngle = cameraHorizontalAngle;
	}

	public float getCameraDistanceInPixel() {
		return cameraDistanceInPixel;
	}

	public void setCameraDistanceInPixel(float cameraDistanceInPixel) {
		this.cameraDistanceInPixel = cameraDistanceInPixel;
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

	public GPSBean getObserverPostion() {
		return observerPostion;
	}

	public void setObserverPostion(GPSBean observerPostion) {
		this.observerPostion = observerPostion;
	}

}
