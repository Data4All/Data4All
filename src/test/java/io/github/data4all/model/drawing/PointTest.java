package io.github.data4all.model.drawing;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the DrawingMotion class
 * 
 * @author tbrose
 */
public class PointTest {
    /**
     * The instance for testing
     */
    private Point point;

    @Before
    public void setup() {
        point = new Point(1, 1);
    }

    // Tests for getBeta(Point, Point, Point)

    @Test(expected = IllegalArgumentException.class)
    public void getBeta_thirdParameterIsNull_exception() {
        Point.getBeta(point, point, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBeta_allParametersAreNull_exception() {
        Point.getBeta(null, null, null);
    }

    @Test
    public void getBeta_samePoint_NaN() {
        assertThat(Point.getBeta(point, point, point), is(Double.NaN));
    }

    @Test
    public void getBeta_line_pi() {
        assertThat(Point.getBeta(new Point(0, 0), new Point(0, 1), new Point(0,
                2)), closeTo(Math.PI, 0.1));
    }

    @Test
    public void getBeta_startIsEnd_0Or2pi() {
        assertThat(Point.getBeta(point, new Point(100, 100), point),
                either(closeTo(0, 0.1)).or(closeTo(2 * Math.PI, 0.1)));
    }

    @Test
    public void getBeta_triangle_halfPi() {
        assertThat(Point.getBeta(new Point(0, 0), new Point(1, 0), new Point(1,
                1)), closeTo(Math.PI/2, 0.1));
    }

    // Tests for equals(float, float)

    @Test
    public void equals_sameCoordinates_resultIsTrue() {
        assertThat(point.equalsTo(1, 1), is(true));
    }

    @Test
    public void equals_otherXCoordinate_resultIsFalse() {
        assertThat(point.equalsTo(2, 1), is(false));
    }

    @Test
    public void equals_otherYCoordinate_resultIsFalse() {
        assertThat(point.equalsTo(1, 2), is(false));
    }

    // Tests for equals(Object)

    @Test
    public void equals_sameOject_resultIsTrue() {
        assertThat(point.equals(point), is(true));
    }

    @Test
    public void equals_otherPointWithSameCoordinates_resultIsTrue() {
        assertThat(point.equals(new Point(1, 1)), is(true));
    }

    @Test
    public void equals_otherPointWithOtherXCoordinate_resultIsFalse() {
        assertThat(point.equals(new Point(2, 1)), is(false));
    }

    @Test
    public void equals_otherPointWithOtherYCoordinate_resultIsFalse() {
        assertThat(point.equals(new Point(1, 2)), is(false));
    }

    @Test
    public void equals_otherClass_resultIsFalse() {
        assertThat(point.equals(new Object()), is(false));
    }
}
