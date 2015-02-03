package io.github.data4all.service;

import io.github.data4all.logger.Log;
import io.github.data4all.model.DataBaseHandler;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.util.Optimizer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

public class GPSservice extends Service implements LocationListener {

    Optimizer optimizer = new Optimizer();
    
    DataBaseHandler dbHandler = new DataBaseHandler(this.getApplicationContext());

    private static final String TAG = "GPSservice";

    /**
     * LocationManager
     */
    private LocationManager lmgr;

    private WakeLock wakeLock;

    private Track track;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        // wakelock, so the cpu is never shut down and is able to track at all
        // time
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        // new track is initialized and gets timestamp.
        // Does not contain any trackpoints yet
        track = new Track();
        dbHandler.createTrack(track);

        lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (lmgr.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, // minimum
                                                                            // of
                                                                            // time
                    0, this); // minimum of meters
        }

        if (lmgr.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                    0, this);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove registration for location updates
        lmgr.removeUpdates(this);

        wakeLock.release();
    }

    public void onLocationChanged(Location loc) {
        Optimizer.putLoc(loc);

        Location tp = Optimizer.currentBestLoc();

        // check if new Location is already stored
        if (sameTrackPoints(track.getLastTrackPoint(), tp)) {
            track.addTrackPoint(tp);
            // After ten trackpoints updateDatabase
            if ((track.getTrackPoints().size() % 10) == 0) {
                dbHandler.updateTrack(track);
            }
        }
    }

    /**
     * Easy comparison of two TrackPoints. Only compares longitude and latitude.
     * 
     * @param point1
     *            The first trackpoint
     * @param point2
     *            The second trackpoint
     * @return true if lon and lat of the trackpoints are the same, else false
     */
    private boolean sameTrackPoints(TrackPoint point1, Location loc) {
        TrackPoint point2 = new TrackPoint(loc);
        if (point1.getLat() == point2.getLat()
                && point1.getLon() == point2.getLon()) {
            return true;
        }
        return false;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not interested in provider status
    }

    public void onProviderEnabled(String provider) {
        if (track == null) {
            // start new track
            track = new Track();
            dbHandler.createTrack(track);
        } else {
            // overrides old track with null and start a new track everytime gps
            // is enabled
            track = null;
            track = new Track();
            dbHandler.createTrack(track);
        }
    }

    public void onProviderDisabled(String provider) {
        // Remove registration for location updates
        lmgr.removeUpdates(this);
        if (track.getTrackPoints().isEmpty()) {
            // track does not contain any trackpoints and gps is not available,
            // so clear track
            dbHandler.deleteTrack(track);
            track = null;
        } else {
            // Track with trackpoints exist, so save it to database
            dbHandler.updateTrack(track);
            track = null; // override current track with null
        }
        // TODO localization
        Toast.makeText(getBaseContext(),
                "Gps turned off, GPS tracking not possible ", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
