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
package io.github.data4all.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.util.HorizonCalculationUtil.returnValues;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test class for the class HorizonCalculationUtil.
 * 
 * @author burghardt
 *
 */

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class HorizonCalculationUtilTest {

    HorizonCalculationUtil util;
    DeviceOrientation deviceOrientation;
    returnValues rV;

    @Before
    public void setUp() {
        util = new HorizonCalculationUtil();
        rV = util.new returnValues();
    }

    /*
     * Tests for method calcHorizontalPoints(float maxPitch, float maxRoll,
     * float maxWidth, float maxHeight, float maxhorizon, DeviceOrientation
     * deviceOrientation)
     */

    /**
     * pitch with roll = 0 and roll with pitch = 0
     */
    @Test
    public void calcHorizontalPoints_PitchTest() {
        DeviceOrientation deviceOrientation;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(60), (float) Math.toRadians(0), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getY(), is(rV.getPoint2().getY()));
        assertThat(rV.getPoint1().getX(), is(0.0f));
        assertThat(rV.getPoint2().getX(), is(500.0f));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(0), (float) Math.toRadians(60), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getX(), is(rV.getPoint2().getX()));
        assertThat(rV.getPoint1().getY(), is(0.0f));
        assertThat(rV.getPoint2().getY(), is(1000.0f));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(true));
    }

    /**
     * horizon isn't visible
     */
    @Test
    public void calcHorizontalPoints_RollTest() {
        DeviceOrientation deviceOrientation;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(0), (float) Math.toRadians(0), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1(), is((Point) null));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(false));
    }

    /**
     * more then 50% of the display is above the horizon
     */
    @Test
    public void calcHorizontalPoints_PitchRollTest() {
        DeviceOrientation deviceOrientation;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(71), (float) Math.toRadians(0), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getY(), is(rV.getPoint2().getY()));
        assertThat(rV.getPoint1().getX(), is(0.0f));
        assertThat(rV.getPoint2().getX(), is(500.0f));
        assertThat(rV.isSkylook(), is(true));
        assertThat(rV.isVisible(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(0), (float) Math.toRadians(71), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getX(), is(rV.getPoint2().getX()));
        assertThat(rV.getPoint1().getY(), is(0.0f));
        assertThat(rV.getPoint2().getY(), is(1000.0f));
        assertThat(rV.isSkylook(), is(true));
        assertThat(rV.isVisible(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(71), (float) Math.toRadians(40), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.isSkylook(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(40), (float) Math.toRadians(71), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.isSkylook(), is(true));
    }

    /**
     * pitch and roll != 0, visible horizon and less then 50% of the display is
     * above the horizon
     */
    @Test
    public void calcHorizontalPoints_SkylookTest() {
        DeviceOrientation deviceOrientation;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(30), (float) Math.toRadians(30), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getX(), is(500.0f));
        assertThat(rV.getPoint1().getY(), greaterThan(0.0f));
        assertThat(rV.getPoint1().getY(), lessThan(1000.0f));
        assertThat(rV.getPoint2().getY(), is(1000.0f));
        assertThat(rV.getPoint2().getX(), greaterThan(0.0f));
        assertThat(rV.getPoint2().getX(), lessThan(500.0f));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(-30), (float) Math.toRadians(30), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getY(), is(0.0f));
        assertThat(rV.getPoint1().getX(), greaterThan(0.0f));
        assertThat(rV.getPoint1().getX(), lessThan(500.0f));
        assertThat(rV.getPoint2().getX(), is(500.0f));
        assertThat(rV.getPoint2().getY(), greaterThan(0.0f));
        assertThat(rV.getPoint2().getY(), lessThan(1000.0f));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(-30), (float) Math.toRadians(-30), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getX(), is(0.0f));
        assertThat(rV.getPoint1().getY(), greaterThan(0.0f));
        assertThat(rV.getPoint1().getY(), lessThan(1000.0f));
        assertThat(rV.getPoint2().getY(), is(0.0f));
        assertThat(rV.getPoint2().getX(), greaterThan(0.0f));
        assertThat(rV.getPoint2().getX(), lessThan(500.0f));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(true));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(30), (float) Math.toRadians(-30), 10L);
        rV = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(rV.getPoint1().getX(), is(0.0f));
        assertThat(rV.getPoint1().getY(), greaterThan(0.0f));
        assertThat(rV.getPoint1().getY(), lessThan(1000.0f));
        assertThat(rV.getPoint2().getY(), is(1000.0f));
        assertThat(rV.getPoint2().getX(), greaterThan(0.0f));
        assertThat(rV.getPoint2().getX(), lessThan(500.0f));
        assertThat(rV.isSkylook(), is(false));
        assertThat(rV.isVisible(), is(true));
    }
}
