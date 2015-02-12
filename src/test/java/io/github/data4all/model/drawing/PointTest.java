/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.drawing;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

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

    @Test
    public void equals_otherClass_resultIsFalse() {
        assertThat(point.equals(new Object()), is(false));
    }

    // Tests for getBeta(Point, Point, Point)

    @Test
    public void equals_otherPointWithOtherXCoordinate_resultIsFalse() {
        assertThat(point.equals(new Point(2, 1)), is(false));
    }

    @Test
    public void equals_otherPointWithOtherYCoordinate_resultIsFalse() {
        assertThat(point.equals(new Point(1, 2)), is(false));
    }

    @Test
    public void equals_otherPointWithSameCoordinates_resultIsTrue() {
        assertThat(point.equals(new Point(1, 1)), is(true));
    }

    @Test
    public void equals_otherXCoordinate_resultIsFalse() {
        assertThat(point.equalsTo(2, 1), is(false));
    }

    @Test
    public void equals_otherYCoordinate_resultIsFalse() {
        assertThat(point.equalsTo(1, 2), is(false));
    }

    @Test
    public void equals_sameCoordinates_resultIsTrue() {
        assertThat(point.equalsTo(1, 1), is(true));
    }

    // Tests for equals(float, float)

    @Test
    public void equals_sameOject_resultIsTrue() {
        assertThat(point.equals(point), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBeta_allParametersAreNull_exception() {
        Point.getBeta(null, null, null);
    }

    @Test
    public void getBeta_line_pi() {
        assertThat(Point.getBeta(new Point(0, 0), new Point(0, 1), new Point(0,
                2)), closeTo(Math.PI, 0.1));
    }

    // Tests for equals(Object)

    @Test
    public void getBeta_samePoint_NaN() {
        assertThat(Point.getBeta(point, point, point), is(Double.NaN));
    }

    @Test
    public void getBeta_startIsEnd_0Or2pi() {
        assertThat(Point.getBeta(point, new Point(100, 100), point),
                either(closeTo(0, 0.1)).or(closeTo(2 * Math.PI, 0.1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBeta_thirdParameterIsNull_exception() {
        Point.getBeta(point, point, null);
    }

    @Test
    public void getBeta_triangle_halfPi() {
        assertThat(Point.getBeta(new Point(0, 0), new Point(1, 0), new Point(1,
                1)), closeTo(Math.PI / 2, 0.1));
    }

    @Before
    public void setup() {
        point = new Point(1, 1);
    }
}
