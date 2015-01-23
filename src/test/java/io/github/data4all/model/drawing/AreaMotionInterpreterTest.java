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
 * Test cases for the AreaMotionInterpreter class
 * 
 * @author tbrose
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class AreaMotionInterpreterTest extends MotionInterpreterTest {

    /**
     * The interpreter to test
     */
    private AreaMotionInterpreter interpreter;

    /**
     * A mocked canvas to verify, if the right points and lines where drawn
     */
    private Canvas canvas;

    @Before
    public void setUp() {
        interpreter = new AreaMotionInterpreter();
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
     * If the User is typing two points near to each other, there should be one
     * point drawn
     */
    @Test
    public void draw_twoNearPoints_onePointIsDrawn() {
        DrawingMotion motion1 = getDrawingMotion(0, 0);
        DrawingMotion motion2 = getDrawingMotion(1, 0);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2));
        
        verifyDrawCircle(canvas, times(1), 0.5f, 0);
    }

    /**
     * If the User is typing a line which is a dot, there should be one point
     * drawn, because a dot is only point
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
        DrawingMotion motion3 = getDrawingMotion(50, 50, 40, 40, 30, 30, 20,
                20, 10, 10);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2, motion3));
        verifyDrawCircle(canvas, times(3));

        // Try with one gestures, three more invocations should happen
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 50, 10, 50, 20, 50, 30, 50, 40, 50, 50, 40, 40, 30,
                30, 20, 20, 10, 10);
        interpreter.draw(canvas, Arrays.asList(motion));
        verifyDrawCircle(canvas, times(6));
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
     * If the User is typing a line which is a dot, there should be one lines
     * drawn, because a dot is only point
     */
    @Test
    public void draw_twoNearPointsInSamePath_zeroLinesWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 1, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(1));
    }

    /**
     * If the User is typing a straight line, there should be two lines drawn<br/>
     * A line from point one to two and one line from point two to one
     */
    @Test
    public void draw_straightLine_twoLinesWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80, 0, 90, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(2));
    }

    /**
     * If the User is typing a approximated line, there should be two lines
     * drawn<br/>
     * A line from point one to two and one line from point two to one
     */
    @Test
    public void draw_approximatedLine_twoLinesWereDrawn() {
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 1, 20, 0, 30, -1, 40,
                0, 50, 1, 60, 0, 70, -1, 80, 0);
        interpreter.draw(canvas, Arrays.asList(motion));

        verifyDrawLine(canvas, times(2));
    }

    /**
     * If the User is typing a straight triangle, there should be three lines
     * drawn.
     */
    @Test
    public void draw_straightTriangle_threeLinesWereDrawn() {
        // Try with three gestures
        DrawingMotion motion1 = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0);
        DrawingMotion motion2 = getDrawingMotion(50, 0, 50, 10, 50, 20, 50, 30,
                50, 40);
        DrawingMotion motion3 = getDrawingMotion(50, 50, 40, 40, 30, 30, 20,
                20, 10, 10);
        interpreter.draw(canvas, Arrays.asList(motion1, motion2, motion3));
        verifyDrawLine(canvas, times(3));

        // Try with one gestures, three more invocations should happen
        DrawingMotion motion = getDrawingMotion(0, 0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 50, 10, 50, 20, 50, 30, 50, 40, 50, 50, 40, 40, 30,
                30, 20, 20, 10, 10);
        interpreter.draw(canvas, Arrays.asList(motion));
        verifyDrawLine(canvas, times(6));
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

    /**
     * If the User is typing a straight line, there should be two points
     * added
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
}
