package io.github.data4all.service;

//import io.github.data4all.model.DevicePosition;
import io.github.data4ll.model.DevicePosition;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class PositionListener extends Service implements SensorEventListener {

	// sensor accelerometer
	Sensor accelerometer;
	// sensor magnetic_field
	Sensor magnetometer;
	// sensorManager
	private SensorManager sManager;
	// model DevicePosition for saving data
	private DevicePosition devicePosition;

	public void onCreate() {
		Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
	}
      
	 /*
	  * start recording data from accelerometer and magnetic_field
	  * register the SensorListener in The Service
	  * and when Android kill the sensor to free up valuable resources,
	  * then use Start_STICKY to restart the Service 
	  * when Resource become available again
	  */
	@Override
	   public int onStartCommand(Intent intent, int flags, int startId) {
		
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent) 
	 * when the two Sensors data are available then saved this in model
	 */
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
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
				setDevicePosition(new DevicePosition(orientation[0],
						orientation[1], orientation[2],
						System.currentTimeMillis()));

			}
		}
	}

	// unregister the SensorListener in The Service
	@Override
	public void onDestroy() {
		sManager.unregisterListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		sManager.unregisterListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public DevicePosition getDevicePosition() {
		return devicePosition;
	}

	public void setDevicePosition(DevicePosition devicePosition) {
		this.devicePosition = devicePosition;
	}

}
