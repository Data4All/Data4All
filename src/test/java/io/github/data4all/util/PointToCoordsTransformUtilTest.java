package io.github.data4all.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsSame;
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
		double[] orientation = {0.0,0.0,0.0};
		double[] vector = {0.0,0.0,1.0};
	//	assertThat(util.calculateVectorfromOrientation(orientation), );
		double[] orientation2 = {Math.PI,0.0,0.0};
		double[] vector2 = {0.0,0.0,-2.0};
		assertThat(util.calculateVectorfromOrientation(orientation), is(vector));
	}
}
