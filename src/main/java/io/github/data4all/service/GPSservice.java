package io.github.data4all.service;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.task.TrackParserTask;
import io.github.data4all.util.Optimizer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

public class GPSservice extends Service implements LocationListener {

    Optimizer optimizer = new Optimizer();

    private static final String TAG = "GPSservice";

    /**
     * LocationManager
     */
    private LocationManager lmgr;

    private WakeLock wakeLock;

    // record gps for
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
        track = new Track(getApplicationContext());

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
        // when there is an existing track and it contains trackpoints, start
        // the task to parse and save it
        if (track != null && !track.getTrackPoints().isEmpty()) {
            Log.d(TAG, "execute TrackParserTask");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new TrackParserTask(getApplicationContext(), track)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new TrackParserTask(getApplicationContext(), track).execute();
            }
        }

        // Remove registratiuon for location updates
        lmgr.removeUpdates(this);

        wakeLock.release();
    }

    public void onLocationChanged(Location loc) {
        optimizer.putLoc(loc);

        // TODO check only for different lat lon values
        // prevent from duplicate trackpoints
        if (!track.getTrackPoints().contains(
                new TrackPoint(optimizer.currentBestLoc()))) {
            track.addTrackPoint(optimizer.currentBestLoc()); // add a trackpoint
                                                             // to
                                                             // a current track
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not interested in provider status
    }

    public void onProviderEnabled(String provider) {
        if (track == null) {
            // start new track
            track = new Track(getApplicationContext());
        } else {
            // overrides old track with null and start a new track everytime gps
            // is enabled
            track = null;
            track = new Track(getApplicationContext());
        }
    }

    public void onProviderDisabled(String provider) {
        if (track.getTrackPoints().isEmpty()) {
            // track does not contain any trackpoints and gps is not available,
            // so clear track
            track = null;
        } else {
            // Track with trackpoints exist, so start task to parse and save it
            new TrackParserTask(getApplicationContext(), track).execute();

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
