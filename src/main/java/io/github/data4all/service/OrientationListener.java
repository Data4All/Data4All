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
package io.github.data4all.service;

import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.smoothing.BasicSensorSmoother;
import io.github.data4all.smoothing.SensorSmoother;
import io.github.data4all.util.Optimizer;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

/**
 * A service for listening for orientation changes. Whenever the sensor changes
 * the accelerometer and magnetic field values are checked out.
 * 
 * @author Steeve
 * @author sbollen
 * @author Richard (primary onAccuracyChanged)
 * 
 */
public class OrientationListener extends Service implements SensorEventListener {

    private final IBinder mBinder = new LocalBinder();

    private HorizonListener horizonListener;

    private DeviceOrientation deviceOrientation;

    /** sensor accelerometer. */
    private Sensor accelerometer;
    /** sensor magnetic_field. */
    private Sensor magnetometer;
    /** sensorManager. */
    private SensorManager sManager;

    /** object of SensorSmoother for smoothing the sensor data. */
    private SensorSmoother smoothing = new BasicSensorSmoother();

    private static final String TAG = "OrientationListener";

    // Array length for mGeomagnetic, mGravity and orientation
    private static final int ARRAYLENGTH = 3;

    // Array length for mR and mI
    private static final int LENGTH = 16;

    // last index for orientation
    private static final int LAST_INDEX = 2;

    // RotationmatrixR
    private float[] mR = new float[LENGTH];
    // RotationmatrixI
    private float[] mI = new float[LENGTH];
    // accelerometer sensor data
    private float[] mGravity = new float[ARRAYLENGTH];
    // magnetic field sensor data
    private float[] mGeomagnetic = new float[ARRAYLENGTH];
    // orientation values
    private float[] orientation = new float[ARRAYLENGTH];

    public final static String BROADCAST_CAMERA = "broadcastToCamera";
    public final static String INTENT_CAMERA_UPDATE = "update";
    // Calibration needed
    public final static int CALIBRATION_BROKEN_ALL = 300;
    public final static int CALIBRATION_BROKEN_ACCELEROMETER = 200;
    public final static int CALIBRATION_BROKEN_MAGNETOMETER = 201;
    public final static int CALIBRATION_OK = 100;
    public static int CALIBRATION_STATUS = CALIBRATION_BROKEN_ALL;
    private boolean accOk = false;
    private boolean magOk = false;

    @Override
    public void onCreate() {
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sManager.registerListener(this,
                sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,
                sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * start recording data from accelerometer and magnetic_field, register the
     * SensorListener in The Service and when Android kill the sensor to free up
     * valuable resources, then use Start_STICKY to restart the Service when
     * Resource become available again
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        return START_STICKY;
    }

    /*
     * (non-Javadoc)
     * 
     * @param event when the two Sensors data are available then saved this in
     * model
     * 
     * @see
     * android.hardware.SensorEventListener#onSensorChanged(android.hardware
     * .SensorEvent)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // smoothing sensor data
            mGravity = smoothing.filter(event.values.clone(), mGravity);
            System.arraycopy(event.values, 0, mGravity, 0, ARRAYLENGTH);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // smooting sensor data
            mGeomagnetic = smoothing.filter(event.values.clone(), mGeomagnetic);
            System.arraycopy(event.values, 0, mGeomagnetic, 0, ARRAYLENGTH);
        }

        // when the 2 sensors data are available
        if (mGravity != null && mGeomagnetic != null) {

            final boolean success = SensorManager.getRotationMatrix(mR, mI,
                    mGravity, mGeomagnetic);

            if (success) {
                SensorManager.getOrientation(mR, orientation);

                if (event.accuracy >= 1) {

                    // saving the new model with the orientation in the
                    // RingBuffer
                    deviceOrientation = new DeviceOrientation(orientation[0],
                            orientation[1], orientation[LAST_INDEX],
                            System.currentTimeMillis());
                    Optimizer.putDevOrient(deviceOrientation);

                    if (horizonListener != null) {
                        horizonListener.makeHorizon(true);
                    }

                }

            }

        }

    }

    /*
     * stop to recording data from accelerometer and magnetic_field and
     * unregister the SensorListener in The Service
     */
    @Override
    public void onDestroy() {
        sManager.unregisterListener(this, accelerometer);
        sManager.unregisterListener(this, magnetometer);
        Log.i(TAG, "Service Destroyed");
    }

    /*
     * (non-Javadoc) description
     * 
     * @see android.hardware.SensorEventListener#onAccuracyChanged
     * (android.hardware.Sensor,int)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (accuracy < SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
                Log.d(TAG, "The sensor: " + sensor.getName()
                        + " has now the accuracy of " + accuracy
                        + " it needs recalibration!");

                accOk = false;
            } else {

                Log.d(TAG, "The sensor: " + sensor.getName()
                        + " has now the accuracy of " + accuracy
                        + " App ready to use!");
                accOk = true;
            }
        }
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            if (accuracy < SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
                Log.d(TAG, "The sensor: " + sensor.getName()
                        + " has now the accuracy of " + accuracy
                        + " it needs recalibration!");

                magOk = false;
            } else {

                Log.d(TAG, "The sensor: " + sensor.getName()
                        + " has now the accuracy of " + accuracy
                        + " App ready to use!");
                magOk = true;
            }
        }
        checkAccuracy();
        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        Intent localIntent =
 new Intent(BROADCAST_CAMERA)
                // Puts the status into the Intent
                .putExtra(INTENT_CAMERA_UPDATE, true);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void checkAccuracy() {
        if (accOk) {
            if (magOk) {
                CALIBRATION_STATUS = CALIBRATION_OK;
            } else {
                CALIBRATION_STATUS = CALIBRATION_BROKEN_MAGNETOMETER;
            }
        } else {
            if (magOk) {
                CALIBRATION_STATUS = CALIBRATION_BROKEN_ACCELEROMETER;
            } else {
                CALIBRATION_STATUS = CALIBRATION_BROKEN_ALL;
            }
        }
    }

    public class LocalBinder extends Binder {
        public OrientationListener getService() {
            return OrientationListener.this;
        }
    }

    /*
     * (non-Javadoc) description
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * A
     * 
     * @author tbrose
     */
    public interface HorizonListener {
        /**
         * Draws a new Horizon.
         * 
         * @param state
         *            The current undo state
         */
        void makeHorizon(boolean state);
    }

    public HorizonListener getHorizonListener() {
        return horizonListener;
    }

    public void setHorizonListener(HorizonListener horizonListener) {
        this.horizonListener = horizonListener;
    }

    public DeviceOrientation getDeviceOrientation() {
        return deviceOrientation;
    }

    public void setDeviceOrientation(DeviceOrientation deviceOrientation) {
        this.deviceOrientation = deviceOrientation;
    }

}
