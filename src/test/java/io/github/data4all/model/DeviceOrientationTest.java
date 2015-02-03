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
package io.github.data4all.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Test cases for the DeviceOrientation class
 * 
 * @author steeve, fkirchge
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DeviceOrientationTest {
    /**
     * The instance for testing
     */
    private DeviceOrientation deviceOrientation;

    @Before
    public void setup() {
        deviceOrientation = new DeviceOrientation(100.10f, -20.40f, 1.71f, 1);
    }

    // Tests for equals(Object)

    @Test
    public void equals_sameOject_resultIsTrue() {
        assertTrue(deviceOrientation.equals(deviceOrientation));
    }

    @Test
    public void equals_otherDeviceOrientationWithSameCoordinates_resultIsTrue() {
        assertTrue(deviceOrientation.equals(new DeviceOrientation(100.10f,
                -20.40f, 1.71f, 1)));
    }

    /**
     * Create a new Parcel to save/parcelable the testDeviceOrientation,
     * afterwards a new deviceorientation is created from the parcel and we
     * check if it contains all attributes.
     */
    @Test
    public void test_parcelable_node() {
        Parcel newParcel = Parcel.obtain();
        DeviceOrientation testDeviceOrientation = new DeviceOrientation(10, 20,
                30, 1234567);

        testDeviceOrientation.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        DeviceOrientation deParcelDeviceOrientation = DeviceOrientation.CREATOR
                .createFromParcel(newParcel);

        assertEquals(testDeviceOrientation.getAzimuth(),
                deParcelDeviceOrientation.getAzimuth(), 0);
        assertEquals(testDeviceOrientation.getPitch(),
                deParcelDeviceOrientation.getPitch(), 0);
        assertEquals(testDeviceOrientation.getRoll(),
                deParcelDeviceOrientation.getRoll(), 0);
        assertEquals(testDeviceOrientation.getTimestamp(),
                deParcelDeviceOrientation.getTimestamp(), 0);

    }
}
