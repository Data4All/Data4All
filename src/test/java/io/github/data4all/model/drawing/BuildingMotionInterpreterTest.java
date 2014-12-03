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
 * Test cases for the BuildingMotionInterpreter class
 * 
 * @author tbrose
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class BuildingMotionInterpreterTest extends MotionInterpreterTest {

    /**
     * The interpreter to test
     */
    private BuildingMotionInterpreter interpreter;

    /**
     * A mocked canvas to verify, if the right points and lines where drawn
     */
    private Canvas canvas;

    @Before
    public void setUp() {
        interpreter = new BuildingMotionInterpreter();
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
     * If the User is typing a path, there should be one point drawn<br/>
     * This is because the last point of the should be used
     */
    @Test
    public void draw_onePath_onePointIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 0, 10, 0, 20, 0, 30);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawCircle(canvas, times(1), 0, 30);
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
     * If the User is typing three points, there should be four points drawn<br/>
     * This is because the fourth point is calculated
     */
    @Test
    public void draw_threePoints_fourPointsWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(0, 20);
        DrawingMotion motion3 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2, motion3));

        verifyDrawCircle(canvas, times(1), 0, 0);
        verifyDrawCircle(canvas, times(1), 0, 20);
        verifyDrawCircle(canvas, times(1), 20, 20);
        // 20|0 is calculated by the interpreter
        verifyDrawCircle(canvas, times(1), 20, 0);
    }

    // Tests for lines

    /**
     * If the User is typing one point, there should be one line with the length
     * of 0 drawn
     */
    @Test
    public void draw_onePoint_oneLineIsDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(1));
    }

    /**
     * If the User is typing two points, there should be two lines drawn<br/>
     * A line from point one to two and one line from point two to one
     */
    @Test
    public void draw_twoPoints_twoLinesWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));

        verifyDrawLine(canvas, times(2));
    }

    /**
     * If the User is typing three points, there should be four lines drawn<br/>
     * This is because the fourth point is calculated
     */
    @Test
    public void draw_threePoints_fourLinesWereDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(0, 20);
        DrawingMotion motion3 = getDrawingMotion(20, 20);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2, motion3));

        verifyDrawLine(canvas, times(4));
    }
}
