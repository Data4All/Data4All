package io.github.data4all.service;

//import io.github.data4all.model.DevicePosition;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.util.Optimizer;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

/**
 * 
 * @author Steeve
 * 
 */
public class OrientationListener extends Service implements SensorEventListener {

    /** sensor accelerometer */
    Sensor accelerometer;
    /** sensor magnetic_field */
    Sensor magnetometer;
    /** sensorManager */
    private SensorManager sManager;
    
    private static final String TAG = "OrientationListener";

    // RotationmatrixR
    float[] mR = new float[16];
    // RotationmatrixI
    float[] mI = new float[16];
    // accelerometer sensor data
    float[] mGravity = new float[3];
    // magnetic field sensor data
    float[] mGeomagnetic = new float[3];
    // orientation values
    float[] orientation = new float[3];

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

    /**
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

    /**
     * (non-Javadoc)
     * 
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware
     *      .SensorEvent)
     * @param event
     *            when the two Sensors data are available then saved this in
     *            model
     */
    public void onSensorChanged(SensorEvent event) {

        // check sensor type
        switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            System.arraycopy(event.values, 0, mGravity, 0, 3);
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);
            break;
        }

        // when the 2 Sensors data are available
        if (mGravity != null && mGeomagnetic != null) {

            boolean success = SensorManager.getRotationMatrix(mR, mI, mGravity,
                    mGeomagnetic);

            if (success) {
                SensorManager.getOrientation(mR, orientation);
                // saving the new model with the orientation in the RingBuffer
                Optimizer.putPos(new DeviceOrientation(orientation[0],
                        orientation[1], orientation[2], System
                                .currentTimeMillis()));
            }
        }
    }

    /**
     * stop to recording data from accelerometer and magnetic_field and
     * unregister the SensorListener in The Service
     **/
    @Override
    public void onDestroy() {
        sManager.unregisterListener(this, accelerometer);
        sManager.unregisterListener(this, magnetometer);
        Log.i(TAG, "Service Destroyed");
    }

    /**
     * (non-Javadoc) description
     * 
     * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor,
     *      int)
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc) description
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
