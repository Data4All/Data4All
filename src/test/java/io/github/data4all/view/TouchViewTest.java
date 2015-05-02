///* 
// * Copyright (c) 2014, 2015 Data4All
// * 
// * <p>Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *     <p>http://www.apache.org/licenses/LICENSE-2.0
// * 
// * <p>Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.github.data4all.view;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertThat;
//import static org.junit.Assert.assertTrue;
//import io.github.data4all.model.drawing.Point;
//import io.github.data4all.view.TouchView.InterpretationType;
//import io.github.data4all.view.TouchView.PointMover;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
///**
// * Test cases for the TouchView class
// * 
// * @author konerman
// */
//@RunWith(RobolectricTestRunner.class)
//@Config(emulateSdk = 18)
//public class TouchViewTest {
//
//    private TouchView touchview;
//    private List<Point> polygon;
//
//    private Point point1;
//    private Point point2;
//    private Point point3;
//    private Point point4;
//    private Point point5;
//
//    @Before
//    public void setUp() throws Exception {
//        touchview = new TouchView(Robolectric.application);
//
//        point1 = new Point(2, 2);
//        point2 = new Point(40, 40);
//        point3 = new Point(43, 43);
//        point4 = new Point(33, 33);
//        point5 = new Point(1, 1);
//
//        java.lang.reflect.Field declaredField = touchview.getClass().getDeclaredField("polygon");
//        declaredField.setAccessible(true);
//        polygon = new ArrayList<Point>();
//        declaredField.set(touchview, polygon);
//
//        polygon.add(point1);
//        polygon.add(point2);
//        polygon.add(point3);
//        polygon.add(point4);
//        touchview.emptyRedoUndo();
//    }
//
//    @Test
//    public void deletePoint_deleteNull_nothingChanged() {
//        List<Point> list = new ArrayList<Point>(polygon);
//        touchview.deletePoint(null);
//        for (int i = 0; i < list.size(); i++) {
//            assertEquals(list.get(i), polygon.get(i));
//        }
//    }
//
//    @Test
//    public void deletePoint_pointNotInPolygon_nothingChanged() {
//        List<Point> list = new ArrayList<Point>(polygon);
//        Point point5 = new Point(333, 333);
//        touchview.deletePoint(point5);
//
//        assertFalse(polygon.contains(point5));
//        for (int i = 0; i < list.size(); i++) {
//            assertEquals(list.get(i), polygon.get(i));
//        }
//    }
//
//    @Test
//    public void deletePoint_pointInPolygon_pointRemoved() {
//        assertTrue(polygon.contains(point4));
//        touchview.deletePoint(point4);
//        assertFalse(polygon.contains(point4));
//    }
//
//    @Test
//    public void lookUp_noPointsInPolygon_nullReturned() {
//        polygon.clear();
//        assertNull(touchview.lookUp(5, 5, 2));
//    }
//
//    @Test
//    public void lookUp_noPointAround_nullReturned() {
//        assertNull(touchview.lookUp(20, 20, 2));
//    }
//
//    @Test
//    public void lookUp_pointAround_rightPointReturned() {
//        assertEquals(point1, touchview.lookUp(1, 1, 5));
//    }
//
//    @Test
//    public void lookUp_exactLookupOnPoint_rightPointReturned() {
//        assertEquals(point1, touchview.lookUp(2, 2, 0));
//    }
//
//    @Test
//    public void lookUp_towPointsInRange_nearestPointReturned() {
//        assertEquals(point3, touchview.lookUp(42, 42, 2));
//    }
//
//    @Test
//    public void movePoint_PointIsNull_NullReturned() {
//        assertThat(touchview.movePoint(null), is(nullValue()));
//    }
//
//    @Test
//    public void movePoint_PointIsNotInPolygon_NullReturned() {
//        assertThat(touchview.movePoint(new Point(0, 0)), is(nullValue()));
//    }
//
//    @Test
//    public void movePoint_getRightPositionInPolygon() {
//        polygon.add(point5);
//        int idx = polygon.indexOf(point5);
//        touchview.movePoint(point5);
//
//        assertThat(polygon.get(idx), is(polygon.get(4)));
//    }
//
//    @Test
//    public void moveTo_PointMoved_XYchanged() {
//        polygon.add(point5);
//        PointMover pm = touchview.movePoint(point5);
//        int idx = polygon.indexOf(point5);
//        pm.moveTo(5.0F, 6.0F);
//        assertThat(polygon.get(idx).getX(), is(5.0F));
//        assertThat(polygon.get(idx).getY(), is(6.0F));
//    }
//
//    @Test
//    public void hasEnoughNodes_BUILDING_false() {
//        polygon.clear();
//        touchview.setInterpretationType(InterpretationType.BUILDING);
//        polygon.add(point1);
//        assertThat(touchview.hasEnoughNodes(), is(false));
//    }
//
//    @Test
//    public void hasEnoughNodes_BUILDING_true() {
//        polygon.clear();
//        touchview.setInterpretationType(InterpretationType.BUILDING);
//        polygon.add(point1);
//        polygon.add(point2);
//        polygon.add(point3);
//        polygon.add(point4);
//        assertThat(touchview.hasEnoughNodes(), is(true));
//    }
//
//    @Test
//    public void hasEnoughNodes_AREA_false() {
//        polygon.clear();
//        touchview.setInterpretationType(InterpretationType.AREA);
//        assertThat(touchview.hasEnoughNodes(), is(false));
//    }
//
//    @Test
//    public void hasEnoughNodes_AREA_true() {
//        touchview.setInterpretationType(InterpretationType.AREA);
//        polygon.add(point1);
//        polygon.add(point2);
//        polygon.add(point3);
//        assertThat(touchview.hasEnoughNodes(), is(true));
//    }
//    
//    @Test
//    public void hasEnoughNodes_WAY_false() {
//        polygon.clear();
//        polygon.add(point1);
//        touchview.setInterpretationType(InterpretationType.WAY);
//        assertThat(touchview.hasEnoughNodes(), is(false));
//    }
//
//    @Test
//    public void hasEnoughNodes_WAY_true() {
//        touchview.setInterpretationType(InterpretationType.WAY);
//        polygon.add(point2);
//        assertThat(touchview.hasEnoughNodes(), is(true));
//    }
//    
//    @Test
//    public void hasEnoughNodes_POINT_false() {
//        polygon.clear();
//        touchview.setInterpretationType(InterpretationType.POINT);
//        assertThat(touchview.hasEnoughNodes(), is(false));
//    }
//
//    @Test
//    public void hasEnoughNodes_POINT_true() {
//        touchview.setInterpretationType(InterpretationType.POINT);
//        polygon.add(point1);
//        assertThat(touchview.hasEnoughNodes(), is(true));
//    }
//
//}
