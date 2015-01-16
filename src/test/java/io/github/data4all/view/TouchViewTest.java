package io.github.data4all.view;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.github.data4all.model.drawing.Point;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test cases for the TouchView class
 * 
 * @author konerman
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TouchViewTest {

    TouchView touchview;
    List<Point> polygon;
    Point point1;
    Point point2;
    Point point3;

    Point point4;
    Point point5;
    Point point6;

    @Before
    public void setUp() throws Exception {
        touchview = new TouchView(Robolectric.application);

        point1 = new Point(2, 2);
        point2 = new Point(40, 40);
        point3 = new Point(43, 43);
        point4 = new Point(33, 33);
        Field declaredField = touchview.getClass().getDeclaredField("polygon");
        declaredField.setAccessible(true);
        polygon = new ArrayList<Point>();
        declaredField.set(touchview, polygon);
        polygon.add(point1);
        polygon.add(point2);
        polygon.add(point3);
        polygon.add(point4);
    }

    @Test
    public void testDeletePoint() {
        // point5 isnt in the polygon
        touchview.deletePoint(point5);
        assertEquals(false, polygon.contains(point5));

        // first check if point 4 is in the polygon and check afterwards
        assertTrue(polygon.contains(point4));
        touchview.deletePoint(point4);
        assertEquals(false, polygon.contains(point4));

    }

    @Test
    public void testLookUp() {
        // there is no point around
        assertNull(touchview.lookUp(20, 20, 2));
        // there is a point near x=1 and y=1 -->point1
        assertEquals(point1, touchview.lookUp(1, 1, 5));
        // there is no point within the maximum distance. point 1 is too far
        // away
        assertNull(touchview.lookUp(5, 5, 2));
        // we hit exactly a point
        assertEquals(point1, touchview.lookUp(2, 2, 0));
        // there are two points around (point 2 and 3)
        assertEquals(point3, touchview.lookUp(42, 42, 2));

    }
}
