package io.github.data4all.service;

import java.util.ArrayList;

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
import io.github.data4all.logger.Log;
import android.widget.Toast;

public class GPSservice extends Service implements LocationListener {

    public static ArrayList<String> history = new ArrayList<String>();

    private static final String TAG = "GPStracker";
    /**
     * Are we currently tracking ?
     */
    private boolean isTracking = false;

    /**
     * Is GPS enabled ?
     */
    private boolean isGpsEnabled = false;

    /**
     * Last known location
     */
    private Location lastLocation;

    /**
     * LocationManager
     */
    private LocationManager lmgr;

    private WakeLock wakeLock;

    @Override
    public void onCreate() {
        // wakelock, so the cpu is never shut down and is able to track at all
        // time
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, // minimum
                                                                        // of
                                                                        // time
                0, this); // minimum of meters
        lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
                this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        wakeLock.release();

    }

    public void onLocationChanged(Location location) {
        // We're receiving location, so GPS is enabled
        isGpsEnabled = true;

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        // timestamp
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        // add
        history.add("time:" + ts + " lat=" + lat + " lon=" + lon);

        Log.d(TAG, "time:" + ts + " lat=" + lat + " lon=" + lon);
        Log.d(TAG, "Points in GPS history: " + history.size());

        lastLocation = location;

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not interested in provider status

    }

    public void onProviderEnabled(String provider) {
        isGpsEnabled = true;
    }

    public void onProviderDisabled(String provider) {
        isGpsEnabled = false;
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
