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

    // Tests for method draw()

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

    // Tests for method interprete()

    @Test
    public void interprete_motionIsNull_noModification() {
        List<Point> interprete = new ArrayList<Point>();
        List<Point> interpreted = interpreter.interprete(interprete, null);
        assertThat(interpreted, sameInstance(interprete));
    }

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

    /**
     * If the User is typing a line, there should be the last point of the line
     * added
     */
    @Test
    public void interprete_addLine_onePointInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0, 0, 10, 0, 20, 0,
                30);
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(1));
        assertThat(interpreted.get(0), equalTo(new Point(0, 30)));
    }

    /**
     * If the User is typing a line, there should be the last point of the line
     * added, the exact position of the fourth point depends
     */
    @Test
    public void interprete_addThreePoints_fourPointsInList() {
        List<Point> interprete = new ArrayList<Point>();
        DrawingMotion drawingMotion = getDrawingMotion(0, 0, 0, 10, 0, 20, 0,
                30);
        interprete.add(new Point(0, 0));
        interprete.add(new Point(100, 0));
        interprete.add(new Point(100, 100));
        List<Point> interpreted = interpreter.interprete(interprete,
                drawingMotion);
        assertThat(interpreted.size(), is(4));
        assertThat(interpreted.get(0), equalTo(new Point(0, 0)));
        assertThat(interpreted.get(1), equalTo(new Point(100, 0)));
        assertThat(interpreted.get(2), equalTo(new Point(100, 100)));
    }
}
