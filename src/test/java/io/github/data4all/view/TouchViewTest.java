package io.github.data4all.view;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.view.TouchView.PointMover;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    private TouchView touchview;
    private List<Point> polygon;
    
    private Point point1;
    private Point point2;
    private Point point3;
    private Point point4;
    private Point point5;
    private Point point6;

    @Before
    public void setUp() throws Exception {
        touchview = new TouchView(Robolectric.application);

        point1 = new Point(2, 2);
        point2 = new Point(40, 40);
        point3 = new Point(43, 43);
        point4 = new Point(33, 33);
        point5 = new Point(1, 1);
        point6 = new Point(1, 1);

        Field declaredField = touchview.getClass().getDeclaredField("polygon");
        declaredField.setAccessible(true);
        polygon = new ArrayList<Point>();
        declaredField.set(touchview, polygon);

        polygon.add(point1);
        polygon.add(point2);
        polygon.add(point3);
        polygon.add(point4);
        polygon.add(point5);
        polygon.add(point6);
    }

    
    @Test
    public void movePoint_PointIsNull_NullReturned() {
        assertThat(touchview.movePoint(null), is(nullValue()));
    }
    
    @Test
    public void movePoint_PointIsNotInPolygon_NullReturned() {
        assertThat(touchview.movePoint(new Point(0,0)), is(nullValue()));
    }
    
    @Test
    public void movePoint_PointMoved_XYchanged() {
        PointMover pm = touchview.movePoint(point5);
        
        pm.moveTo(5.0F,6.0F);
        assertThat(polygon.get(pm.idx).getX(), is(5.0F));  
        assertThat(polygon.get(pm.idx).getY(), is(6.0F));  
      }
}
