/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import android.location.Location;

/**
 * Optimize the position and location data of the device. Save the latest data
 * in a RingBuffer and optimize these to have one perfect location and position
 * object
 * 
 * @author sbollen
 *
 */

public final class Optimizer {

    // The timeDifference at which one location should be significant older
    // or newer than another one, 1000 is one second
    public static final double TIME_DIFFERENCE = 1000;

    // The accuracy difference at which one location should be significant more
    // accurate than another one
    public static final int ACC_DIFF = 50;

    // Size of the two ringbuffer
    public static final int RB_SIZE = 20;

    // a new Ringbuffer for saving the location objects
    private static RingBuffer<Location> locRB = new RingBuffer<Location>(
            RB_SIZE);
    // a new Ringbuffer for saving the DevicePosition objects
    private static RingBuffer<DeviceOrientation> posRB = new RingBuffer<DeviceOrientation>(
            RB_SIZE);

    /**
     * Private Constructor, prevents instantiation.
     */
    private Optimizer() {

    }

    /**
     * Put a Location object to the Location RingBuffer.
     * 
     * @param loc
     *            the new location
     */
    public static void putLoc(Location loc) {
        locRB.put(loc);
    }

    /**
     * Put a DeviceOrientation object to the DeviceOrientation RingBuffer.
     * 
     * @param pos
     *            the new device orientation
     */
    public static void putPos(DeviceOrientation pos) {
        posRB.put(pos);
    }

    /**
     * Give the last location.
     * 
     * @return the last location
     */
    public static Location currentLocation() {
        return locRB.getLast();
    }

    /**
     * Give the current best location.
     * 
     * @return the current best location
     */
    public static Location currentBestLoc() {
        return calculateBestLoc();
    }

    /**
     * Give the current DeviceOrientation object which has the pitch, roll and
     * azimuth values.
     * 
     * @return the current DeviceOrientation
     */
    public static DeviceOrientation currentDeviceOrientation() {
        return posRB.getLast();
    }

    /**
     * Calculate from all saved locations in the ringbuffer the best one.
     * 
     * @return the best location of all saved locations
     */
    public static Location calculateBestLoc() {
        final Location lastLoc = locRB.getLast();
        Location bestLoc = lastLoc;
        for (Object location : locRB.getAll()) {
            final Location loc = (Location) location;
            // this location must be better than the actual best and last one
            if (loc != null && isBetterLocation(loc, lastLoc)
                    && isBetterLocation(loc, bestLoc)) {
                bestLoc = loc;
            }
        }
        return bestLoc;
    }

    /**
     * Determines whether one Location reading is better than the current
     * Location fix.
     * 
     * @param location
     *            The new Location that you want to evaluate
     * 
     * @param currentBestLocation
     *            The current Location fix, to which you want to compare the new
     *            one
     * 
     * @return true if the first location is better than the second
     */
    protected static boolean isBetterLocation(Location loc,
            Location currentBestLoc) {

        if (currentBestLoc == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        final long timeDelta = loc.getTime() - currentBestLoc.getTime();
        final boolean isSignificantlyNewer = timeDelta > (TIME_DIFFERENCE);
        final boolean isSignificantlyOlder = timeDelta < -(TIME_DIFFERENCE);
        final boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        final int accuracyDelta = (int) (loc.getAccuracy() - currentBestLoc
                .getAccuracy());
        final boolean isLessAccurate = accuracyDelta > 0;
        final boolean isMoreAccurate = accuracyDelta < 0;
        final boolean isSignifLessAccurate = accuracyDelta > ACC_DIFF;

        // Check if the old and new location are from the same provider
        final boolean isFromSameProvider = isSameProvider(loc.getProvider(),
                currentBestLoc.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if ((isMoreAccurate) || (isNewer && !isLessAccurate)
                || (isNewer && !isSignifLessAccurate && isFromSameProvider)) {
            return true;
        }
        return false;
    }

    /**
     * checks whether two providers are the same.
     * 
     * @param provider1
     *            one provider
     * @param provider2
     *            the other provider
     * @return true if the two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Clear all stored locations of the Optimizer.
     * 
     * @author tbrose
     */
    public static void clear() {
        locRB.clear();
        posRB.clear();
    }
}
