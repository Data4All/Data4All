package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import android.location.Location;

/**
 * Optimize the position and location data of the device. Save the latest data
 * in a RingBuffer and optimize these to have one perfect location and position object
 * 
 * @author sbollen
 *
 */

public class Optimizer {

    // a new Ringbuffer for saving the location objects
    static RingBuffer<Location> locRB = new RingBuffer<Location>(20);
    // a new Ringbuffer for saving the DevicePosition objects
    static RingBuffer<DeviceOrientation> posRB = new RingBuffer<DeviceOrientation>(
            20);
    
    // The timeDifference at which one location should be significant older
    // or newer than another one, 1000 is one second
    final double TIME_DIFFERENCE = 1000;

    public Optimizer() {
    }

    /*
     * Put a Location object to the Location RingBuffer
     */
    public void putLoc(Location loc) {
        locRB.put(loc);
    }

    /*
     * Put a DeviceOrientation object to the DeviceOrientation RingBuffer
     */
    public void putPos(DeviceOrientation pos) {
        posRB.put(pos);
    }

    /*
     * Give the current best location
     * 
     * @return the current best location
     */
    public Location currentBestLoc() {
        return calculateBestLoc();
    }

    /*
     * Give the current DevicePosition
     * 
     * @return the current best DevicePosition
     */
    public DeviceOrientation currentBestPos() {
        return posRB.get(posRB.getIndex());
    }

    /*
     * Calculate from all saved locations in the ringbuffer the best one
     * 
     * @return the best location of all saved locations
     */
    public Location calculateBestLoc() {
        Location lastLoc = locRB.getLast();
        Location bestLoc = lastLoc;
        for (Object location : locRB.getAll()) {
            Location loc = (Location) location;
            //this location must be better than the actual best and last one
            if (loc != null && isBetterLocation(loc, lastLoc) && isBetterLocation(loc, bestLoc)) {
                bestLoc = loc;
            }
        }
        return bestLoc;
    }

    /*
     * Determines whether one Location reading is better than the current
     * Location fix
     * 
     * @param location The new Location that you want to evaluate
     * 
     * @param currentBestLocation The current Location fix, to which you want to
     * compare the new one
     */
    protected boolean isBetterLocation(Location location,
            Location currentBestLocation) {

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > (TIME_DIFFERENCE);
        boolean isSignificantlyOlder = timeDelta < -(TIME_DIFFERENCE);
        boolean isNewer = timeDelta > 0;

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
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 50;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /* Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
