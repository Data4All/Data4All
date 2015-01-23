package io.github.data4all.model.drawing;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test cases for the PointMotionInterpreter class
 * 
 * @author tbrose
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class PointMotionInterpreterTest extends MotionInterpreterTest {

    /**
     * The interpreter to test
     */
    private PointMotionInterpreter interpreter;

    @Test
    public void interprete_motionIsNull_noModification() {
        List<Point> interprete = new ArrayList<Point>();
        List<Point> interpreted = interpreter.interprete(interprete, null);
        assertThat(interpreted, sameInstance(interprete));
    }

    // Tests for method interprete()

    @Test
    public void interprete_onePoint_replacePointsInList() {
        List<Point> interprete = Arrays.asList(new Point(100, 100), new Point(
                200, 200), new Point(300, 300));
        DrawingMotion drawingMotion = getDrawingMotion(0, 0);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(1));
        assertThat(interpreted.get(0), equalTo(new Point(0, 0)));
    }

    @Test
    public void interprete_onePoint_thisPointInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(1));
        assertThat(interpreted.get(0), equalTo(new Point(0, 0)));
    }

    @Test
    public void interprete_twoNearPoints_averageInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0,
                DrawingMotion.POINT_TOLERANCE, 0);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(1));
        assertThat(interpreted.get(0), equalTo(new Point(
                DrawingMotion.POINT_TOLERANCE / 2, 0)));
    }

    @Test
    public void interprete_twoPoints_lastPointIsInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0,
                DrawingMotion.POINT_TOLERANCE, DrawingMotion.POINT_TOLERANCE);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(1));
        assertThat(interpreted.get(0), equalTo(new Point(
                DrawingMotion.POINT_TOLERANCE, DrawingMotion.POINT_TOLERANCE)));
    }

    @Before
    public void setUp() {
        interpreter = new PointMotionInterpreter();
    }
}
