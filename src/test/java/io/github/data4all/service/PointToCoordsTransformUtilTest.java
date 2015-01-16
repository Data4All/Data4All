package io.github.data4all.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.data4all.logger.Log;
import io.github.data4all.util.PointToCoordsTransformUtil;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class PointToCoordsTransformUtilTest {
	PointToCoordsTransformUtil util = new PointToCoordsTransformUtil();
	
	
	@Test
	public void transformTest(){
		
	}

	@Test
	public void calculateVectorfromOrientationTest(){
		
		double[] orientation = new double[3];
		orientation[0] = 0.0;
		orientation[1] = 0.0;
		orientation[2] = 0.0;
		double[] vector = util.calculateVectorfromOrientation(orientation);
		assertEquals(0.0,vector[0], 0);
		assertEquals(0.0,vector[1],0);
		assertEquals(1.0,vector[2],0);
	}
}
