package io.github.data4all.service;

import io.github.data4all.logger.Log;
import io.github.data4all.util.RingBuffer;
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

    public RingBuffer GPShistory = new RingBuffer(10);

    private static final String TAG = "GPSservice";

    int j = 0;
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

        if (lmgr.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0000, // minimum
                                                                            // of
                                                                            // time
                    0, this); // minimum of meters
        }

        if (lmgr.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0000,
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

        wakeLock.release();
        stopSelf();
    }

    public void onLocationChanged(Location location) {
        // We're receiving location, so GPS is enabled
        isGpsEnabled = true;

        // add
        GPShistory.add(location);

        j++;

        if(GPShistory.get(GPShistory.index())!= null){
        Log.d(TAG, "aktuellste GPS: "
                + GPShistory.get(GPShistory.index()).getLongitude());
        }
        //just for testpurpose:
        if (j>10) {
            Location[] temp = new Location[10];
            temp = GPShistory.getAll();
            for (Location loc : temp) {
                Log.d(TAG, "" + loc.getLongitude());
            }
        }

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
