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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

    // Tests for method interprete()

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

    @Test
    public void interprete_motionIsNull_noModification() {
        List<Point> interprete = new ArrayList<Point>();
        List<Point> interpreted = interpreter.interprete(interprete, null);
        assertThat(interpreted, sameInstance(interprete));
    }

    @Before
    public void setUp() {
        PointToCoordsTransformUtil pointTrans = null;
        interpreter = new BuildingMotionInterpreter(pointTrans);
    }
}
