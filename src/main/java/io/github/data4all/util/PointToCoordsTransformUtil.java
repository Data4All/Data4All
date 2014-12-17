package io.github.data4all.util;

import java.util.ArrayList;

import io.github.data4all.logger.*;

import java.lang.Math;

import toxi.geom.*;

public class PointToCoordsTransformUtil {
	static String TAG = "PointToWorldCoords";
	private float[] orientation;
	private float height = 1700;
	float[] coords;
	
	public float[] calculate (float[] o){
		this.orientation = o;
		
		float z = (float) Math.cos(orientation[1]); //calculate Z with Pitch
		float yy = (float) Math.sin(-orientation[1]); //calculate temp.Y with Pitch
		float xx = (float) (Math.tan(orientation[2]) * z);//calculate temp.X with fix Z and Roll
		// Rotate Vector with Azimuth
		float x = (float) ((xx * Math.cos(orientation[0])) - (yy * Math.sin(orientation[0])));
		float y = (float) ((xx * Math.sin(orientation[0])) + (yy * Math.cos(orientation[0])));
		
		z = height / z;
		x = x * z;
		y = y * z;
		coords[0] = x;
		coords[1] = y;
		return coords;
		
	}
	
	
}
