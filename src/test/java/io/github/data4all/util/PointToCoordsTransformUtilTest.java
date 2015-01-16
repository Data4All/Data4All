package io.github.data4all.util;


import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;

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
		TransformationParamBean tps = new TransformationParamBean(10000000.0, Math.toRadians(45) ,
		Math.toRadians(45) , 1000, 1000, location);
		DeviceOrientation deviceOrientation = new DeviceOrientation(0.0f, 0.1f, 0.0f, 10L);
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(500, 500));
		points.add(new Point(1000, 500));
		points.add(new Point(500, 1000));
		ArrayList<Node> test = util.transform(tps, deviceOrientation, points);
		assertThat(test.get(0).getLat(), is(0.0));
		assertThat(test.get(0).getLon(), is(0.0));
		assertThat(test.get(1).getLat(), is(0.0));
		//test.get(1).setLon(1.0);
		assertThat(test.get(1).getLon(), is(not(0.0)));
		
	}

	@Test
	public void calculateVectorfromOrientationTest(){		
		double[] orientation = {0.0,0.0,0.0};
		double[] vector = {0.0,0.0,1.0};
	//	assertThat(util.calculateVectorfromOrientation(orientation), );
		double[] orientation2 = {Math.PI,0.0,0.0};
		double[] vector2 = {0.0,0.0,-2.0};
		assertThat(util.calculateVectorfromOrientation(orientation), is(vector));
	}
}
