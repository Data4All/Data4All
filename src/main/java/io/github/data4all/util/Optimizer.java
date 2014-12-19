package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import android.location.Location;

/**
 * Optimize the position and location data of the device. Save the latest data
 * in a RingBuffer and optimize these to have one perfect location and position
 * 
 * @author sbollen
 *
 */

public class Optimizer {

    // a new Ringbuffer for saving the location objects
    //RingBuffer locRB = new RingBuffer();
    // a new Ringbuffer for saving the DevicePosition objects
    //RingBuffer posRB = new RingBuffer();

    public Optimizer() {
        // TODO Auto-generated constructor stub
    }

    /*
     * Put a location object to the location RingBuffer
     */
    public void putLoc(Location loc) {
        //locRB.put(loc);
    }

    /*
     * Put a DevicePosition object to the DevicePosition RingBuffer
     */
    public  void putPos(DeviceOrientation pos) {
        //posRB.put(pos);
    }

    /*
     * Give the current best location
     * 
     * @return the current best location
     */
    public Location currentLoc() {
        return null;
    }

    /*
     * Give the best current DevicePosition
     * 
     * @return the current best DevicePosition
     */
    public DeviceOrientation currentPos() {
        return null;
    }

    // 
    // double allLat = 0;
    // double allLong = 0;
    // Location newestLoc = /*Ringbuffer.getLastLocation */;
    // Location loc = newestLoc;
    //
    // for(Location loc : /*Ringbuffer*/) {
    // if (loc.distanceTo(nextLocation) > 10 && loc.distanceTo(previousLocation)
    // > 10) {//Distance in meter
    // deleteLocation();
    // }
    // allLat += devPos.getLocation().getLatitude();
    // allLong += devPos.getLocation().getLongitude();
    // }
    // double midLat = allLat / /*Anzahl Elemente im Ringbuffer*/;
    // double midLong = allLong / /*Anzahl Elemente im Ringbuffer*/;
    //
    // loc.setLatitude(midLat);
    // loc.setLongitude(midLong);
    //
    // if(Math.abs(loc.getLatitude()-newestLoc.getLatitude()) > 0.0001) {
    // currentBestLocation = loc;
    // return loc;
    // } else {
    // currentBestLocation = newestLoc;
    // return newestLoc;
    // }
    // }

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
        boolean isSignificantlyNewer = timeDelta > (1000 * 60); //One minute
        boolean isSignificantlyOlder = timeDelta < -(1000 * 60); //One minute
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
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

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
