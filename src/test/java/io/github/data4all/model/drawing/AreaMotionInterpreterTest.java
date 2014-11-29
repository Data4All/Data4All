package io.github.data4all.model.drawing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.graphics.Canvas;

/**
 * Test cases for the AreaMotionInterpreter class
 * 
 * @author tbrose
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class AreaMotionInterpreterTest extends MotionInterpreterTest {

    private AreaMotionInterpreter interpreter;

    /**
     * A mocked canvas to verify, if the right points and lines where drawed
     */
    private Canvas canvas;

    @Before
    public void setUp() {
        interpreter = new AreaMotionInterpreter();
        canvas = mock(Canvas.class);
    }

    /**
     * If the User is typing one point, there should be one point drawn
     */
    @Test
    public void draw_onePoint_onePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1));
    }

    /**
     * If the User is typing two points, there should be two points drawn
     */
    @Test
    public void draw_twoPoints_twoPointsWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(2));
    }

    /**
     * If the User is typing two points tear to each other, there should be two
     * points drawn, because a line always have a start and a end point
     */
    @Test
    public void draw_twoNearPoints_twoPointsWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(1, 0);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawCircle(canvas, times(2));
    }

    /**
     * If the User is typing a line which is a dot, there should be one
     * point drawn, because a dot is only point
     */
    @Test
    public void draw_twoNearPointsInSamePath_onePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 1, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1));
    }
}
