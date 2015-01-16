package io.github.data4all.model.drawing;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.graphics.Canvas;

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

    /**
     * A mocked canvas to verify, if the right points and lines where drawn
     */
    private Canvas canvas;

    @Before
    @Deprecated
    public void setUp() {
        interpreter = new PointMotionInterpreter();
        canvas = mock(Canvas.class);
    }

    // Tests for method draw()

    /**
     * If the User is not typing any point, there should be nothing drawn
     */
    @Test
    @Deprecated
    public void draw_zeroPoints_nothingIsDrawn() {
        interpreter.draw(canvas, new ArrayList<DrawingMotion>());

        verifyDrawCircle(canvas, times(0));
        verifyDrawLine(canvas, times(0));
    }

    // Tests for points, lines should never be drawn

    /**
     * If the User is typing one point, there should be one point drawn
     */
    @Test
    @Deprecated
    public void draw_onePoint_onePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 0, 0);
        verifyDrawLine(canvas, times(0));
    }

    /**
     * If the User is typing two points, there should be the last point drawn
     */
    @Test
    @Deprecated
    public void draw_twoPoints_lastPointIsDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(1), 20, 20);
        verifyDrawLine(canvas, times(0));
    }

    /**
     * If the User is typing a line which is a dot, there should be one point
     * drawn.
     */
    @Test
    @Deprecated
    public void draw_twoNearPointsInSamePath_averagePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 1, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 0.5f, 0);
        verifyDrawLine(canvas, times(0));
    }

    /**
     * If the User is typing a line, there should be the last point drawn
     */
    @Test
    @Deprecated
    public void draw_line_lastPointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80, 0, 90, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 90, 0);
        verifyDrawLine(canvas, times(0));
    }

    /**
     * If the User is typing two lines, there should be the last point of the
     * last line drawn
     */
    @Test
    @Deprecated
    public void draw_twoLines_lastPointIsDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0);
        DrawingMotion motion2 = getDrawingMotion(0, 0, 0, 10, 0, 20, 0, 30);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(1), 0, 30);
        verifyDrawLine(canvas, times(0));
    }

    // Tests for method interprete()

    @Test
    public void interprete_motionIsNull_noModification() {
        List<Point> interprete = new ArrayList<Point>();
        List<Point> interpreted = interpreter.interprete(interprete, null);
        assertThat(interpreted, sameInstance(interprete));
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
}
