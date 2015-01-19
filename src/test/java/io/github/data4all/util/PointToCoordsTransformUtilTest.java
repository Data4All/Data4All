package io.github.data4all.util;


import static org.junit.Assert.assertThat;
//import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.util.PointToCoordsTransformUtil;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class PointToCoordsTransformUtilTest {
	PointToCoordsTransformUtil util = new PointToCoordsTransformUtil();
	Location location = new Location("Test");
	
	@Test
	public void transformTest(){
		location.setLatitude(0.0);
		location.setLongitude(0.0);
		TransformationParamBean tps = new TransformationParamBean(2.0, Math.toRadians(45) ,
		Math.toRadians(45) , 1000, 1000, location);
		DeviceOrientation deviceOrientation = new DeviceOrientation(0.0f, 0.0f, 0.0f, 10L);
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(500, 500));
		points.add(new Point(1000, 500));
		points.add(new Point(500, 1000));
		points.add(new Point(1 , 500));
		points.add(new Point(500,1));
		List<Node> test = util.transform(tps, deviceOrientation, points);
		assertThat(test.get(0).getLat(), is(0.0));
		assertThat(test.get(0).getLon(), is(0.0));
		assertThat(test.get(1).getLat(), is(0.0));
		assertThat(test.get(1).getLon(), greaterThan(0.0));
		assertThat(test.get(2).getLat(), lessThan(0.0));
		assertThat(test.get(2).getLon(), is(0.0));
		assertThat(test.get(3).getLat(), is(0.0));
		assertThat(test.get(3).getLon(), lessThan(0.0));
		assertThat(test.get(4).getLat(), greaterThan(0.0));
		assertThat(test.get(4).getLon(), is(0.0));
		
		
		deviceOrientation = new DeviceOrientation((float) (Math.PI/2), 0.0f, 0.0f, 10L);
		test = util.transform(tps, deviceOrientation, points);
		assertThat(test.get(0).getLat(), is(0.0));
		assertThat(test.get(0).getLon(), is(0.0));
		assertThat(test.get(1).getLat(), greaterThan(0.0));
		assertThat(test.get(1).getLon(), closeTo(0.0, 0.0000001));	
		assertThat(test.get(2).getLat(), closeTo(0.0, 0.0000001));
		assertThat(test.get(2).getLon(), greaterThan(0.0) );
	}
	
	@Test
	public void calculateCoordFromPointTest(){
		location.setLatitude((float) (Math.PI/1.2));
		location.setLongitude((float) (-Math.PI/1.3));
		TransformationParamBean tps = new TransformationParamBean(2.0, Math.toRadians(90) ,
		Math.toRadians(90) , 1000, 1000, location);
		float a = (float) (Math.PI/4);
		
		DeviceOrientation deviceOrientation = new DeviceOrientation(0.0f,a,0.0f, 10L);
		float x=500;
		float y = 500;
		Point point = new Point(x,y);
		double[] coord = util.calculateCoordFromPoint(tps, deviceOrientation, point);
		Point test = util.calculatePointFromCoords(tps, deviceOrientation, coord);
		
		assertThat((double) point.getX(), closeTo(test.getX() , 1));
		assertThat((double) point.getY(), closeTo(test.getY() , 1));
		/*
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(123, 624));
		points.add(new Point(123, 624));
		points.add(new Point(123, 624));
		util.calculate4Point(tps, deviceOrientation, points);*/
	}

	/*
	@Test
	public void calculateVectorfromOrientationTest(){		
		double[] orientation = {0.0,0.0,0.0};
		double[] vector = {0.0,0.0,1.0};
	//	assertThat(util.calculateVectorfromOrientation(orientation), );
		double[] orientation2 = {Math.PI,0.0,0.0};
		double[] vector2 = {0.0,0.0,-2.0};
		assertThat(util.calculateVectorfromOrientation(orientation), is(vector));
	}*/
}
