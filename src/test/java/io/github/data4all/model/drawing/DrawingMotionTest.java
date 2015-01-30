/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.model.drawing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the DrawingMotion class
 * 
 * @author tbrose
 */
public class DrawingMotionTest {

    /**
     * The instance for testing
     */
    private DrawingMotion drawingMotion;

    @Test
    public void addPoint_addingPoints_sizeIncreasesCorrect() {
        for (int i = 1; i < 20; i++) {
            drawingMotion.addPoint(0, 0);
            assertThat(drawingMotion.getPathSize(), is(i));
        }
    }

    /**
     * Adds points to the DrawingMotion:<br/>
     * Point_0 has coordinates (0,1)<br/>
     * Point_1 has coordinates (2,3)<br/>
     * And so on ...
     * 
     * @param count
     *            the amount of points to be added
     */
    private void addPoints(int count) {
        for (int i = 0; i < count; i++) {
            drawingMotion.addPoint(2 * i, 2 * i + 1);
        }
    }

    // Tests for addPoint

    @Test
    public void average_manyEntries_exactAverage() {
        addPoints(10);
        Point average = drawingMotion.average();
        assertThat(average.getX(), is(9f)); // 0,2,4,6,8,10,12,14,16,18
        assertThat(average.getY(), is(10f)); // 1,3,5,7,9,11,13,15,17,19
    }

    // Tests for isPath

    @Test
    public void average_noEntries_Null() {
        assertThat(drawingMotion.average(), is((Point) null));
    }

    @Test
    public void average_oneEntrie_averageIsFirstPoint() {
        addPoints(1);
        assertThat(drawingMotion.average(), equalTo(drawingMotion.getStart()));
    }

    @Test
    public void average_twoEntries_exactAverage() {
        addPoints(2);
        Point average = drawingMotion.average();
        assertThat(average.getX(), is(1f)); // 0,2
        assertThat(average.getY(), is(2f)); // 1,3
    }

    // Tests for isPoint

    @Test
    public void getEnd_noEntries_resultIsNull() {
        assertThat(drawingMotion.getEnd(), is((Point) null));
    }

    @Test
    public void getEnd_someEntries_resultIsCorrect() {
        addPoints(10);
        drawingMotion.addPoint(-1, -1);
        assertThat(drawingMotion.getEnd().equalsTo(-1, -1), is(true));
    }

    @Test
    public void getPathSize_noEntries_sizeIsZero() {
        assertThat(drawingMotion.getPathSize(), is(0));
    }

    // Tests for getStart

    @Test
    public void getPathSize_someEntries_sizeIsCorrect() {
        addPoints(10);
        assertThat(drawingMotion.getPathSize(), is(10));

        // Add some more points ... just to be on the safe side ...
        addPoints(10);
        assertThat(drawingMotion.getPathSize(), is(20));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getPoint_noEntries_indexOutOfBoundsException() {
        drawingMotion.getPoint(0);
    }

    // Tests for getEnd

    @Test
    public void getPoint_someEntries_rightPointsAndOrder() {
        addPoints(10);
        for (int i = 0; i < 10; i++) {
            Point point = drawingMotion.getPoint(i);
            assertThat(point.getX(), is(2f * i));
            assertThat(point.getY(), is(2f * i + 1));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getPoint_someEntriesIndexNegative_indexOutOfBoundsException() {
        addPoints(10);
        drawingMotion.getPoint(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getPoint_someEntriesIndexToHigh_indexOutOfBoundsException() {
        addPoints(10);
        drawingMotion.getPoint(10);
    }

    // Tests for getPathSize

    @Test
    public void getPoints_noEntries_resultIsEmpty() {
        assertThat(drawingMotion.getPoints().isEmpty(), is(true));
    }

    @Test
    public void getPoints_someEntries_rightPointsAndOrder() {
        addPoints(10);
        List<Point> points = drawingMotion.getPoints();
        for (int i = 0; i < 10; i++) {
            Point point = points.get(i);
            assertThat(point.getX(), is(2f * i));
            assertThat(point.getY(), is(2f * i + 1));
        }
    }

    // Tests for getPoints

    @Test
    public void getStart_noEntries_resultIsNull() {
        assertThat(drawingMotion.getStart(), is((Point) null));
    }

    @Test
    public void getStart_someEntries_resultIsCorrect() {
        drawingMotion.addPoint(-1, -1);
        addPoints(10);
        assertThat(drawingMotion.getStart().equalsTo(-1, -1), is(true));
    }

    // Tests for getPoint

    @Test
    public void getStartGetEnd_oneEntries_samePoint() {
        drawingMotion.addPoint(-1, -1);
        assertThat(drawingMotion.getStart(), equalTo(drawingMotion.getEnd()));
    }

    @Test
    public void isPath_edgeEntries_resultIsFalse() {
        drawingMotion.addPoint(0, 0);
        drawingMotion.addPoint(DrawingMotion.POINT_TOLERANCE, 0);
        drawingMotion.addPoint(-DrawingMotion.POINT_TOLERANCE, 0);
        drawingMotion.addPoint(0, DrawingMotion.POINT_TOLERANCE);
        drawingMotion.addPoint(0, -DrawingMotion.POINT_TOLERANCE);

        assertThat(drawingMotion.isPath(), is(false));
    }

    @Test
    public void isPath_noEntries_resultIsFalse() {
        assertThat(drawingMotion.isPath(), is(false));
    }

    @Test
    public void isPath_overEdgeEntries_resultIsTrue() {
        drawingMotion.addPoint(0, 0);
        drawingMotion.addPoint(Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
        drawingMotion.addPoint(-Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
        drawingMotion.addPoint(0, Math.nextUp(DrawingMotion.POINT_TOLERANCE));
        drawingMotion.addPoint(0, -Math.nextUp(DrawingMotion.POINT_TOLERANCE));

        assertThat(drawingMotion.isPath(), is(true));
    }

    // Tests for average()

    @Test
    public void isPoint_edgeEntries_resultIsTrue() {
        drawingMotion.addPoint(0, 0);
        drawingMotion.addPoint(DrawingMotion.POINT_TOLERANCE, 0);
        drawingMotion.addPoint(-DrawingMotion.POINT_TOLERANCE, 0);
        drawingMotion.addPoint(0, DrawingMotion.POINT_TOLERANCE);
        drawingMotion.addPoint(0, -DrawingMotion.POINT_TOLERANCE);

        assertThat(drawingMotion.isPoint(), is(true));
    }

    @Test
    public void isPoint_noEntries_resultIsFalse() {
        assertThat(drawingMotion.isPoint(), is(false));
    }

    @Test
    public void isPoint_overEdgeEntries_resultIsFalse() {
        drawingMotion.addPoint(0, 0);
        drawingMotion.addPoint(Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
        drawingMotion.addPoint(-Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
        drawingMotion.addPoint(0, Math.nextUp(DrawingMotion.POINT_TOLERANCE));
        drawingMotion.addPoint(0, -Math.nextUp(DrawingMotion.POINT_TOLERANCE));

        assertThat(drawingMotion.isPoint(), is(false));
    }

    @Before
    public void setup() {
        drawingMotion = new DrawingMotion();
    }
}
