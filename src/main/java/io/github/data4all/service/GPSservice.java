/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.service;

import io.github.data4all.logger.Log;
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

/**
 * A service for listening for location changes.
 * 
 * @author konermann
 * 
 */
public class GPSservice extends Service implements LocationListener {

    private static final String TAG = "GPSservice";
    private LocationManager lmgr;
    private WakeLock wakeLock;
    private PowerManager powerManager;
    /*
     * the minimum of time after we get a new locationupdate in ms.
     */
    private final long minTime = 1000;
    /*
     * the minimum of Distance after we get a new locationupdate.
     */
    private final float minDistance = 0;

    @Override
    public void onCreate() {
        // wakelock, so the cpu is never shut down and is able to track at all
        // time
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (lmgr.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // second value is minimum of time, third value is minimum of meters
            lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,
                    this);
        }

        if (lmgr.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            lmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
                    minDistance, this);
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
        wakeLock.release();

    }
    
    @Override
    public void onLocationChanged(Location loc) {
        Optimizer.putLoc(loc);
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not interested in provider status
    }
    
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void onProviderDisabled(String provider) {
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
