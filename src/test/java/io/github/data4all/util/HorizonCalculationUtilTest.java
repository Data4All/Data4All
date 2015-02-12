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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import io.github.data4all.model.DeviceOrientation;

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

    @Before
    public void setUp() {
        util = new HorizonCalculationUtil();
    }

    /*
     * Tests for method calcHorizontalPoints(float maxPitch, float maxRoll,
     * float maxWidth, float maxHeight, float maxhorizon, DeviceOrientation
     * deviceOrientation)
     */

    /**
     * a few different pitchangles
     */
    @Test
    public void calcHorizontalPoints_PitchTest() {

        DeviceOrientation deviceOrientation;
        float[] coord;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(60), (float) Math.toRadians(0), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], lessThan((float) (500 / 2)));
        assertThat(coord[1], is((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(-60), (float) Math.toRadians(0), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], greaterThan((float) (500 / 2)));
        assertThat(coord[1], is((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
    }

    /**
     * a few different rollangles
     */
    @Test
    public void calcHorizontalPoints_RollTest() {

        DeviceOrientation deviceOrientation;
        float[] coord;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(0), (float) Math.toRadians(55), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], is((float) (500 / 2)));
        assertThat(coord[1], lessThan((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(0), (float) Math.toRadians(-55), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], is((float) (500 / 2)));
        assertThat(coord[1], greaterThan((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
    }

    /**
     * a few different roll- and pitchangles
     */
    @Test
    public void calcHorizontalPoints_PitchRollTest() {

        DeviceOrientation deviceOrientation;
        float[] coord;
        // roll and pitch is positiv
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(30), (float) Math.toRadians(30), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], lessThan((float) (500 / 2)));
        assertThat(coord[1], lessThan((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
        // roll and pitch is negativ
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(-30), (float) Math.toRadians(-30), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], greaterThan((float) (500 / 2)));
        assertThat(coord[1], greaterThan((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
        // roll is positiv and pitch is negativ
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(-30), (float) Math.toRadians(30), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], greaterThan((float) (500 / 2)));
        assertThat(coord[1], lessThan((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
        // pitch is positiv and roll is negativ
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(30), (float) Math.toRadians(-30), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], lessThan((float) (500 / 2)));
        assertThat(coord[1], greaterThan((float) (1000 / 2)));
        assertThat(coord[2], is(0.0f));
    }

    /**
     * devicedirecetion is above horizon (skylook)
     */
    @Test
    public void calcHorizontalPoints_SkylookTest() {

        DeviceOrientation deviceOrientation;
        float[] coord;
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(90), (float) Math.toRadians(0), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], greaterThan((float) (500 / 2)));
        assertThat(coord[1], is((float) (1000 / 2)));
        assertThat(coord[2], is(1.0f));
        deviceOrientation = new DeviceOrientation(0.0f,
                (float) Math.toRadians(-90), (float) Math.toRadians(0), 10L);
        coord = util.calcHorizontalPoints((float) Math.toRadians(60),
                (float) Math.toRadians(60), 500, 1000,
                (float) Math.toRadians(70), deviceOrientation);
        assertThat(coord[0], lessThan((float) (500 / 2)));
        assertThat(coord[1], is((float) (1000 / 2)));
        assertThat(coord[2], is(1.0f));
    }

}
