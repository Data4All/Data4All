package io.github.data4all.service;

//import io.github.data4all.model.DevicePosition;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class PositionListener extends Service implements SensorEventListener {

	Sensor accelerometer;
	Sensor magnetometer;
	private SensorManager sManager;


	public void onCreate() {

		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent) when the two Sensors data are available then saved this in
	 * model
	 */
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] mGravity = new float[3];
		float[] mGeomagnetic = new float[3];
		// check sensor type
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) // ==
																	// accelerometer)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			;
		mGeomagnetic = event.values;

		if (mGravity != null && mGeomagnetic != null) {
			float[] mR = new float[9];

			boolean success = SensorManager.getRotationMatrix(mR, null,
					mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(mR, orientation);
				// saved data in model
				/*DevicePosition devicePosition = new DevicePosition(
						System.currentTimeMillis(),orientation[0] ,
						, );*/

			}
		}
	}

	// register the SensorListener in the Service
	protected void onResume() {
		sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	// unregister the SensorListener in The Service
	public void onStop() {
		sManager.unregisterListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		sManager.unregisterListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
