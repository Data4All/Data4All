package io.github.data4all.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;

/**
 * Test cases for the Optimizer class
 * 
 * @author sbollen
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OptimizerTest {

    //The instance for testing
    Optimizer optimizer;

    //different locations for comparing
    Location newLocation;
    Location oldLocation;
    Location location1;
    Location location2;
    Location location3;
    Location accurateLocation;
    Location nullLocation;

    //significant time difference and actual time for comparing location time stamps
    double timeDiff;
    long time;

    @Before
    public void setUp() throws Exception {
        optimizer = new Optimizer();

        newLocation = new Location("provider");
        oldLocation = new Location("provider");
        location1 = new Location("provider");
        location2 = new Location("provider");
        location3 = new Location("provider");
        accurateLocation = new Location("provider");
        nullLocation = null;

        // get the time difference for comparing locations here because this
        // value can change in the further progress
        timeDiff = optimizer.TIME_DIFFERENCE;
        // set a time
        time = System.currentTimeMillis();

        // the new location gets a significant bigger time than the old one
        newLocation.setTime((long) (2 * timeDiff));
        oldLocation.setTime(0);

        // set a better accuracy for accurateLocation than for location1
        accurateLocation.setTime(time);
        accurateLocation.setAccuracy(1);
        location1.setTime(time);
        location1.setAccuracy(2);

        // location3 is newer, not significantly less accurate and has the same
        // provider as location4
        location2.setTime(time);
        location3.setTime(time + 1);
        location2.setAccuracy(4);
        location3.setAccuracy(3);
        location2.setProvider("provider");
        location3.setProvider("provider");

    }

    //test for calculateBestLoc
    @Test
    public void testCalculateBestLoc() {
        // if there is no location
        assertNull(optimizer.calculateBestLoc());
        // put one location in the buffer and this has to be the best location
        optimizer.putLoc(oldLocation);
        assertEquals(oldLocation, optimizer.calculateBestLoc());
        // put another location in the buffer which is better than the first one
        optimizer.putLoc(newLocation);
        assertEquals(newLocation, optimizer.calculateBestLoc());
        // put another location in the buffer which is not better than the one
        // before
        optimizer.putLoc(oldLocation);
        assertEquals(newLocation, optimizer.calculateBestLoc());
    }

    //test for isBetterLocation
    @Test
    public void testIsBetterLocation() {
        // if the second location is a null location
        assertTrue(optimizer.isBetterLocation(location1, nullLocation));

        // if the first location is a significantly newer location
        assertTrue(optimizer.isBetterLocation(newLocation, oldLocation));

        // if the first location is a significantly older location
        assertFalse(optimizer.isBetterLocation(oldLocation, newLocation));

        // if the first location is more accurate and there is no significant
        // time difference
        assertTrue(optimizer.isBetterLocation(accurateLocation, location1));

        // set the time of the second location down so that it is older
        location1.setTime(time - 1);
        // set the accuracy of the second location to the same value as the
        // first location so that the first one is not less accurate
        location1.setAccuracy(1);
        // if the first location is newer and not less accurate
        assertTrue(optimizer.isBetterLocation(accurateLocation, location1));

        // if the first location is newer, not significantly less accurate and
        // from the same provider
        assertTrue(optimizer.isBetterLocation(location3, location2));

        // if the first location is newer, not significantly less accurate and
        // from the same provider (in this case there is no provider
        location2.setProvider(null);
        location3.setProvider(null);
        assertTrue(optimizer.isBetterLocation(location3, location2));

        // if the first location is older but not significantly older and not
        // less or more accurate
        location3.setTime(time - 1);
        location3.setAccuracy(4);
        assertFalse(optimizer.isBetterLocation(location3, location2));
    }
}
