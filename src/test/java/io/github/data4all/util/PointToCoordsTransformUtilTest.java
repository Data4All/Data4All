/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
		TransformationParamBean tps = new TransformationParamBean(2.0, Math.toRadians(90) ,
		Math.toRadians(90) , 1000, 1000, location);
        List<Node> test;
		DeviceOrientation deviceOrientation = new DeviceOrientation(0.0f, 0.0f, 0.0f, 10L);
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(500, 500));
		points.add(new Point(1000, 500));
		points.add(new Point(500, 1000));
		points.add(new Point(1 , 500));
		points.add(new Point(500,1));
		 test = util.transform(tps, deviceOrientation, points,4);
		assertThat(test.get(0).getLat(), is(0.0));
		assertThat(test.get(0).getLon(), is(0.0));
		assertThat(test.get(1).getLat(), greaterThan(0.0));
		assertThat(test.get(1).getLon(), is(0.0));
		assertThat(test.get(2).getLat(), is(0.0));
		assertThat(test.get(2).getLon(), lessThan(0.0));
		assertThat(test.get(3).getLat(), lessThan(0.0));
		assertThat(test.get(3).getLon(), is(0.0));
		assertThat(test.get(4).getLat(), is(0.0));
		assertThat(test.get(4).getLon(), greaterThan(0.0));
		
		
        deviceOrientation = new DeviceOrientation((float) (Math.PI/2), 0.0f, 0.0f, 10L);
        test = util.transform(tps, deviceOrientation, points,4);
        assertThat(test.get(0).getLat(), is(0.0));
        assertThat(test.get(0).getLon(), is(0.0));
        assertThat(test.get(1).getLat(), closeTo(0.0, 0.00000000001));
        assertThat(test.get(1).getLon(), greaterThan(0.0)); 
        assertThat(test.get(2).getLat(), greaterThan(0.0));
        assertThat(test.get(2).getLon(), closeTo(0.0, 0.00000000001) );
        
        deviceOrientation = new DeviceOrientation((float) Math.toRadians(-45), (float)Math.toRadians(45),
                (float)Math.toRadians(45), 10L);
        ArrayList<Point> points2 = new ArrayList<Point>();
        points2.add(new Point(500, 500));
        test = util.transform(tps, deviceOrientation, points2,4);
        assertThat(test.get(0).getLon(), greaterThan(0.0));  
        assertThat(test.get(0).getLat(), greaterThan(0.0));
	}
	/*
	@Test
	public void calculateCoordFromPointTest(){
		location.setLatitude((float) (Math.PI/1.2));
		location.setLongitude((float) (-Math.PI/1.3));
		TransformationParamBean tps = new TransformationParamBean(2.0, Math.toRadians(90) ,
		Math.toRadians(90) , 1000, 1000, location);
		float a = (float) - Math.PI;
		float b = (float) -(Math.PI/2);
		float c = (float) -(Math.PI/2);
		while(a <= (float) Math.PI){
			b = (float) -(Math.PI/2);
			while(b <= (float) (Math.PI/2)){
				c = (float) -(Math.PI/2);
				while(c <= (float) (Math.PI/2)){
					DeviceOrientation deviceOrientation = new DeviceOrientation(a,b,c, 10L);
					int x=1;
					while(x<=1000){
						int y=1;
						while(y<=1000){
							Point point = new Point(x,y);
							double[] coord = util.calculateCoordFromPoint(tps, deviceOrientation, point);
							if(coord[2] == 0){
								Node node = util.calculateGPSPoint(location, coord);
								double[] coord2 = util.calculateCoordFromGPS(location, node);
								Point test = util.calculatePointFromCoords(tps, deviceOrientation, coord2);	
								assertThat((double) point.getX(), closeTo(test.getX() , 1));
								assertThat((double) point.getY(), closeTo(test.getY() , 1));
							}
							else{
							}
							y += 113;
						}
						x += 113;
					}
					c += (float) (Math.PI/11);
				}
				b += (float) (Math.PI/11);
			}
			a += (float) (Math.PI/11);			
		}
	}
	
	*/
}
