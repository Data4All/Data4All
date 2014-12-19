package io.github.data4all.service;

//import io.github.data4all.model.DevicePosition;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import io.github.data4all.util.Optimizer;

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
	/** model DevicePosition for saving data */
	private DeviceOrientation deviceOrientation;

	private static final String TAG = "OrientationListener";

	public void onCreate() {
		Log.i(TAG, "Service was startet");
	}

	/**
	 * start recording data from accelerometer and magnetic_field, register the
	 * SensorListener in The Service and when Android kill the sensor to free up
	 * valuable resources, then use Start_STICKY to restart the Service when
	 * Resource become available again
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service startet");

		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		sManager.registerListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		sManager.registerListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);

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

		float[] mGravity = new float[3];
		float[] mGeomagnetic = new float[3];
		// check sensor type
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;

		// when the 2 Sensors data are available
		if (mGravity != null && mGeomagnetic != null) {
			float[] mR = new float[9];

			boolean success = SensorManager.getRotationMatrix(mR, null,
					mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(mR, orientation);
				// save data in model

				setDeviceOrientation(new DeviceOrientation(orientation[0],
						orientation[1], orientation[2],
						System.currentTimeMillis()));
				//Optimizer.putPos();
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

	public DeviceOrientation getDeviceOrientation() {
		return deviceOrientation;
	}

	public void setDeviceOrientation(DeviceOrientation deviceOrientation) {
		this.deviceOrientation = deviceOrientation;
	}

}
