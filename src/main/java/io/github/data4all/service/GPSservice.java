package io.github.data4all.service;

import io.github.data4all.logger.Log;
import io.github.data4all.util.Optimizer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

/**
 * A service for listening for location changes.
 * 
 * @author konermann
 * 
 */
public class GPSservice extends Service implements LocationListener {

    private static final String TAG = "GPSservice";

    /**
     * LocationManager.
     */
    private LocationManager lmgr;

    private WakeLock wakeLock;
    private Location lastKnownLocation;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        Log.d("GPSSERVICE", "service started");
        // wakelock, so the cpu is never shut down and is able to track at all
        // time
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "GPSservice");
        wakeLock.acquire();

        lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Optimizer.putLoc(findLastKnownLocation());
        
        if (lmgr.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // second value is minimum of time, third value is minimum of meters
            lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,
                    this);
        }

        if (lmgr.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                    0, this);
        }
    }

    public Location findLastKnownLocation() {
        Location loc = lmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc != null) {
            Log.d(this.getClass().getSimpleName(), "lastknownloc gps");
            return loc;
        }
        loc = lmgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {
            Log.d(this.getClass().getSimpleName(), "lastknownloc network");
            return loc;
        }
        Log.d(this.getClass().getSimpleName(), "lastknownloc is null");
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();

    }

    public void onLocationChanged(Location loc) {

        Optimizer.putLoc(loc);

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not interested in provider status

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onProviderDisabled(String provider) {

        Toast.makeText(getBaseContext(),
                "Gps turned off, GPS tracking not possible ", Toast.LENGTH_LONG)
                .show();
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    
    public class LocalBinder extends Binder {
        public GPSservice getService() {
            // Return this instance of LocalService so clients can call public methods
            return GPSservice.this;
        }
    }   
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
