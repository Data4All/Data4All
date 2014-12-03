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

    /**
     * A mocked canvas to verify, if the right points and lines were drawn
     */
    private Canvas canvas;

    @Before
    public void setUp() {
        interpreter = new WayMotionInterpreter();
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

    // Tests for points

    /**
     * If the User is typing one point, there should be one point drawn
     */
    @Test
    public void draw_onePoint_onePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 0, 0);
    }

    /**
     * If the User is typing two points, there should be two points drawn
     */
    @Test
    public void draw_twoPoints_twoPointsWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(1), 0, 0);
        verifyDrawCircle(canvas, times(1), 20, 20);
    }

    /**
     * If the User is typing two points near to each other, there should be two
     * points drawn, because a line always have a start and a end point
     */
    @Test
    public void draw_twoNearPoints_twoPointsWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(1, 0);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(1), 0, 0);
        verifyDrawCircle(canvas, times(1), 1, 0);
    }

    /**
     * If the User is typing a line which is a dot, there should be one point
     * drawn, because a dot is only a point
     */
    @Test
    public void draw_twoNearPointsInSamePath_onePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 1, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 0.5f, 0);
    }

    /**
     * If the User is typing a straight line, there should be two points drawn,
     * because the line only needs a start and a end point
     */
    @Test
    public void draw_straightLine_twoPointsWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80, 0, 90, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 0, 0);
        verifyDrawCircle(canvas, times(1), 90, 0);
    }

    /**
     * If the User is typing a approximated line, there should be two points
     * drawn, because the line only needs a start and a end point
     */
    @Test
    public void draw_approximatedLine_twoPointsWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 1, 20, 0, 30, -1, 40,
                0, 50, 1, 60, 0, 70, -1, 80, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(2));
    }

    /**
     * If the User is typing a straight triangle, there should be three points
     * drawn.
     */
    @Test
    public void draw_straightTriangle_threePointsWereDrawn() {
        // Try with three gestures
        DrawingMotion motion1 = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0);
        DrawingMotion motion2 = getDrawingMotion(50, 0, 50, 10, 50, 20, 50, 30,
                50, 40);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));
        verifyDrawCircle(canvas, times(3));

        // Try with one gestures, three more invocations should happen
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 50, 10, 50, 20, 50, 30, 50, 40);
        interpreter.draw(canvas, Arrays.asList(motion));
        verifyDrawCircle(canvas, times(6));
    }

    // Tests for lines

    /**
     * If the User is typing one point, there should be no line drawn
     */
    @Test
    public void draw_onePoint_zeroLinesWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(0));
    }

    /**
     * If the User is typing two points, there should be one line drawn
     */
    @Test
    public void draw_twoPoints_oneLineIsDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawLine(canvas, times(1));
    }

    /**
     * If the User is typing a line which is a dot, there should be no lines
     * drawn, because a dot is only a point
     */
    @Test
    public void draw_twoNearPointsInSamePath_zeroLinesWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 1, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(0));
    }

    /**
     * If the User is typing a straight line, there should be one line drawn
     */
    @Test
    public void draw_straightLine_oneLineIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80, 0, 90, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(1));
    }

    /**
     * If the User is typing a approximated line, there should be one line drawn
     */
    @Test
    public void draw_approximatedLine_oneLineIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 1, 20, 0, 30, -1, 40,
                0, 50, 1, 60, 0, 70, -1, 80, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(1));
    }

    /**
     * If the User is typing a straight triangle, there should be two lines
     * drawn.
     */
    @Test
    public void draw_straightTriangle_twoLinesWereDrawn() {
        // Try with three gestures
        DrawingMotion motion1 = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0);
        DrawingMotion motion2 = getDrawingMotion(50, 0, 50, 10, 50, 20, 50, 30,
                50, 40);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));
        verifyDrawLine(canvas, times(2));

        // Try with one gestures, two more invocations should happen
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 50, 10, 50, 20, 50, 30, 50, 40);
        interpreter.draw(canvas, Arrays.asList(motion));
        verifyDrawLine(canvas, times(4));
    }
}
