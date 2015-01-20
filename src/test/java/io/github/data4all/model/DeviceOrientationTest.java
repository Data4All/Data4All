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

    // Tests for equals(float, float,float,long)

    @Test
    public void equals_sameCoordinates_resultIsTrue() {
        assertTrue(deviceOrientation.equalsTo(100.10f, -20.40f, 1.71f, 1));
    }

    @Test
    public void equals_otherAzimuthCoordinate_resultIsFalse() {
        assertFalse(deviceOrientation.equalsTo(10.10f, -20.02f, 1.71f, 1));
    }

    @Test
    public void equals_otherPitchCoordinate_resultIsFalse() {
        assertFalse(deviceOrientation.equalsTo(100.10f, 2f, 1.71f, 1));
    }

    @Test
    public void equals_othersRollCoordinate_resultIsFalse() {
        assertFalse(deviceOrientation.equalsTo(100.10f, 2f, 1.0f, 1));
    }

    @Test
    public void equals_otherTimeCoordinate_resultIsFalse() {
        assertFalse(deviceOrientation.equalsTo(100.10f, 2f, 1.71f, 2));
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
     * Create a new parcial to save/parcelable the testDeviceOrientation,
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
