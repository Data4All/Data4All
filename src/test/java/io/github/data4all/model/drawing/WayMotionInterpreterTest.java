package io.github.data4all.model.drawing;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test cases for the WayMotionInterpreter class
 * 
 * @author tbrose
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class WayMotionInterpreterTest extends MotionInterpreterTest {

    /**
     * The interpreter to test
     */
    private WayMotionInterpreter interpreter;

    @Test
    public void interprete_addOnePoint_oneMorePointInList() {
        List<Point> interprete = new ArrayList<Point>();
        interprete.add(new Point(0, 0));
        DrawingMotion drawingMotion = getDrawingMotion(100, 100);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(2));
        assertThat(interpreted.get(0), equalTo(new Point(0, 0)));
        assertThat(interpreted.get(1), equalTo(new Point(100, 100)));
    }

    // Tests for method interprete()

    /**
     * If the User is typing a straight line, there should be two points added
     */
    @Test
    public void interprete_addStraightLine_twoPointInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0, 0, 10, 0, 20, 0,
                30);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(2));
        assertThat(interpreted.get(0), equalTo(new Point(0, 0)));
        assertThat(interpreted.get(1), equalTo(new Point(0, 30)));
    }

    /**
     * If the User is typing a straight triangle, there should be three points
     * added
     */
    @Test
    public void interprete_addTriangle_threePointInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0, 0, 10, 0, 20, 0,
                30, 10, 30, 20, 30, 30, 30);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(3));
        assertThat(interpreted.get(0), equalTo(new Point(0, 0)));
        assertThat(interpreted.get(1), equalTo(new Point(0, 30)));
        assertThat(interpreted.get(2), equalTo(new Point(30, 30)));
    }

    @Test
    public void interprete_motionIsNull_noModification() {
        List<Point> interprete = new ArrayList<Point>();
        List<Point> interpreted = interpreter.interprete(interprete, null);
        assertThat(interpreted, sameInstance(interprete));
    }

    /**
     * If the User is typing two points near to each other, there should be one
     * point drawn
     */
    @Test
    public void interprete_pointNearToLastInList_noMorePointsInList() {
        List<Point> interprete = new ArrayList<Point>();
        interprete.add(new Point(100, 100));
        interprete.add(new Point(0, 0));
        DrawingMotion drawingMotion = getDrawingMotion(1, 1);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(2));
        assertThat(interpreted.get(0), equalTo(new Point(100, 100)));
        assertThat(interpreted.get(1), equalTo(new Point(0, 0)));
    }

    @Before
    public void setUp() {
        PointToCoordsTransformUtil pointTrans = null;
        interpreter = new WayMotionInterpreter(pointTrans);
    }
}
