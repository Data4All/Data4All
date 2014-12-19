package io.github.data4all.model.drawing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;

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
    public void setUp() {
        interpreter = new PointMotionInterpreter();
        canvas = mock(Canvas.class);
    }

    /**
     * If the User is not typing any point, there should be nothing drawn
     */
    @Test
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
    public void draw_twoLines_lastPointIsDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0);
        DrawingMotion motion2 = getDrawingMotion(0, 0, 0, 10, 0, 20, 0, 30);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(1), 0, 30);
        verifyDrawLine(canvas, times(0));
    }
}
