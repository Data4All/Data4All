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

import org.junit.Before;
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

/**
 * Test class for the class PointToCoordsTransformationUtil.
 * 
 * @author burghardt
 * @author sbollen
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class PointToCoordsTransformUtilTest {

    PointToCoordsTransformUtil util;
    TransformationParamBean tps;
    DeviceOrientation deviceOrientation;
    Location location;

    @Before
    public void setUp() {
        location = new Location("Provider");
        // height is 1.7, photoWidth is 500 and photoHeight is 1000
        tps = new TransformationParamBean(1.7, Math.toRadians(90),
                Math.toRadians(90), 500, 1000, location);
        util = new PointToCoordsTransformUtil(tps, deviceOrientation);
    }

    @Test
    public void transformTest() {
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        TransformationParamBean tps = new TransformationParamBean(2.0,
                Math.toRadians(90), Math.toRadians(90), 1000, 1000, location);
        List<Node> test;
        DeviceOrientation deviceOrientation = new DeviceOrientation(0.0f, 0.0f,
                0.0f, 10L);
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(new Point(500, 500));
        points.add(new Point(1000, 500));
        points.add(new Point(500, 1000));
        points.add(new Point(1, 500));
        points.add(new Point(500, 1));
        test = util.transform(tps, deviceOrientation, points, 4);
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

        deviceOrientation = new DeviceOrientation((float) (Math.PI / 2), 0.0f,
                0.0f, 10L);
        test = util.transform(tps, deviceOrientation, points, 4);
        assertThat(test.get(0).getLat(), is(0.0));
        assertThat(test.get(0).getLon(), is(0.0));
        assertThat(test.get(1).getLat(), closeTo(0.0, 0.00000000001));
        assertThat(test.get(1).getLon(), greaterThan(0.0));
        assertThat(test.get(2).getLat(), greaterThan(0.0));
        assertThat(test.get(2).getLon(), closeTo(0.0, 0.00000000001));

        deviceOrientation = new DeviceOrientation((float) Math.toRadians(-45),
                (float) Math.toRadians(45), (float) Math.toRadians(45), 10L);
        ArrayList<Point> points2 = new ArrayList<Point>();
        points2.add(new Point(500, 500));
        test = util.transform(tps, deviceOrientation, points2, 4);
        assertThat(test.get(0).getLon(), greaterThan(0.0));
        assertThat(test.get(0).getLat(), greaterThan(0.0));
    }

    /*
     * @Test public void calculateCoordFromPointTest(){
     * location.setLatitude((float) (Math.PI/1.2));
     * location.setLongitude((float) (-Math.PI/1.3)); TransformationParamBean
     * tps = new TransformationParamBean(2.0, Math.toRadians(90) ,
     * Math.toRadians(90) , 1000, 1000, location); float a = (float) - Math.PI;
     * float b = (float) -(Math.PI/2); float c = (float) -(Math.PI/2); while(a
     * <= (float) Math.PI){ b = (float) -(Math.PI/2); while(b <= (float)
     * (Math.PI/2)){ c = (float) -(Math.PI/2); while(c <= (float) (Math.PI/2)){
     * DeviceOrientation deviceOrientation = new DeviceOrientation(a,b,c, 10L);
     * int x=1; while(x<=1000){ int y=1; while(y<=1000){ Point point = new
     * Point(x,y); double[] coord = util.calculateCoordFromPoint(tps,
     * deviceOrientation, point); if(coord[2] == 0){ Node node =
     * util.calculateGPSPoint(location, coord); double[] coord2 =
     * util.calculateCoordFromGPS(location, node); Point test =
     * util.calculatePointFromCoords(tps, deviceOrientation, coord2);
     * assertThat((double) point.getX(), closeTo(test.getX() , 1));
     * assertThat((double) point.getY(), closeTo(test.getY() , 1)); } else{ } y
     * += 113; } x += 113; } c += (float) (Math.PI/11); } b += (float)
     * (Math.PI/11); } a += (float) (Math.PI/11); } }
     */

    
    // Tests for the method changePixelCoordSystem(point, rotation)
    
    /**
     * Rotation is 0 and point in the middle of the coordinate system is given.
     */
    @Test
    public void changePixelCoordSystemTest_Rot0Normal() {
        Point point = new Point(100, 100);
        Point newPoint = util.changePixelCoordSystem(point, 0);
        assertThat((double) newPoint.getX(), closeTo(901, 1));
        assertThat((double) newPoint.getY(), closeTo(401, 1));

        Point point1 = new Point(100, 500);
        Point newPoint1 = util.changePixelCoordSystem(point1, 0);
        assertThat((double) newPoint1.getX(), closeTo(501, 1));
        assertThat((double) newPoint1.getY(), closeTo(401, 1));
    }

    /**
     * Rotation is 0 and point at the edge of the coordinate system is given.
     */
    @Test
    public void changePixelCoordSystemTest_Rot0Edge() {
        Point point = new Point(500, 100);
        Point newPoint = util.changePixelCoordSystem(point, 0);
        assertThat((double) newPoint.getX(), closeTo(901, 1));
        assertThat((double) newPoint.getY(), closeTo(1, 1));

        Point point1 = new Point(0, 0);
        Point newPoint1 = util.changePixelCoordSystem(point1, 0);
        assertThat((double) newPoint1.getX(), closeTo(1001, 1));
        assertThat((double) newPoint1.getY(), closeTo(501, 1));
    }

    /**
     * Rotation is 0 and point is not in the given coordinate system.
     */
    @Test
    public void changePixelCoordSystemTest_Rot0overTheEdge() {
        Point point = new Point(1000, 100);
        Point newPoint = util.changePixelCoordSystem(point, 0);
        assertThat((double) newPoint.getX(), closeTo(901, 1));
        assertThat((double) newPoint.getY(), closeTo(-499, 1));
    }

    /**
     * Rotation is 1 and point in the middle of the coordinate system is given.
     */
    @Test
    public void changePixelCoordSystemTest_Rot1Normal() {
        Point point = new Point(100, 100);
        Point newPoint = util.changePixelCoordSystem(point, 1);
        assertThat((double) newPoint.getX(), closeTo(401, 1));
        assertThat(newPoint.getY(), is(point.getY()));

        Point point1 = new Point(300, 500);
        Point newPoint1 = util.changePixelCoordSystem(point1, 1);
        assertThat((double) newPoint1.getX(), closeTo(201, 1));
        assertThat(newPoint1.getY(), is(point1.getY()));
    }

    /**
     * Rotation is 1 and point at the edge of the coordinate system is given.
     */
    @Test
    public void changePixelCoordSystemTest_Rot1Edge() {
        Point point = new Point(500, 1000);
        Point newPoint = util.changePixelCoordSystem(point, 1);
        assertThat((double) newPoint.getX(), closeTo(1, 1));
        assertThat(newPoint.getY(), is(point.getY()));

        Point point1 = new Point(0, 0);
        Point newPoint1 = util.changePixelCoordSystem(point1, 1);
        assertThat((double) newPoint1.getX(), closeTo(501, 1));
        assertThat(newPoint1.getY(), is(point1.getY()));
    }

    /**
     * Rotation is 1 and point is not in the given coordinate system.
     */
    @Test
    public void changePixelCoordSystemTest_Rot1overTheEdge() {
        Point point = new Point(600, 1200);
        Point newPoint = util.changePixelCoordSystem(point, 1);
        assertThat((double) newPoint.getX(), closeTo(-99, 1));
        assertThat(newPoint.getY(), is(point.getY()));
    }

    /**
     * Rotation is 3 and point in the middle of the coordinate system is given.
     */
    @Test
    public void changePixelCoordSystemTest_Rot3Normal() {
        Point point = new Point(100, 100);
        Point newPoint = util.changePixelCoordSystem(point, 3);
        assertThat(newPoint.getX(), is(point.getX()));
        assertThat((double) newPoint.getY(), closeTo(901, 1));

        Point point1 = new Point(300, 500);
        Point newPoint1 = util.changePixelCoordSystem(point1, 3);
        assertThat(newPoint1.getX(), is(point1.getX()));
        assertThat((double) newPoint1.getY(), closeTo(501, 1));
    }

    /**
     * Rotation is 3 and point at the edge of the coordinate system is given.
     */
    @Test
    public void changePixelCoordSystemTest_Rot3Edge() {
        Point point = new Point(500, 1000);
        Point newPoint = util.changePixelCoordSystem(point, 3);
        assertThat(newPoint.getX(), is(point.getX()));
        assertThat((double) newPoint.getY(), closeTo(1, 1));

        Point point1 = new Point(0, 0);
        Point newPoint1 = util.changePixelCoordSystem(point1, 3);
        assertThat(newPoint1.getX(), is(point1.getX()));
        assertThat((double) newPoint1.getY(), closeTo(1001, 1));
    }

    /**
     * Rotation is 3 and point is not in the given coordinate system.
     */
    @Test
    public void changePixelCoordSystemTest_Rot3overTheEdge() {
        Point point = new Point(600, 1200);
        Point newPoint = util.changePixelCoordSystem(point, 3);
        assertThat(newPoint.getX(), is(point.getX()));
        assertThat((double) newPoint.getY(), closeTo(-199, 1));
    }

    /**
     * Rotation is none of the three possibilities 0, 1 and 3.
     */
    @Test
    public void changePixelCoordSystemTest_OtherRotation() {
        Point point = new Point(1500, 100);
        Point newPoint = util.changePixelCoordSystem(point, 4);
        assertThat(newPoint.getX(), is(point.getX()));
        assertThat(newPoint.getY(), is(point.getY()));
    }

    /**
     * Point is null.
     */
    @Test
    public void changePixelCoordSystemTest_PointIsNull() {
        Point point = null;
        Point newPoint = util.changePixelCoordSystem(point, 1);
        assertThat(newPoint, is(nullValue()));
    }
}