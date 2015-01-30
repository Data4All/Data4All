package io.github.data4all.activity;

import io.github.data4all.R;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

/**
* this activity will measures the tilting motion and 
* orientation of a mobile phone(accelerometer) and 
* the rate or rotation in rad/s around a device's
*  x, y, and z axis(Gyroscope).
*/
public class SensorActivity extends Activity implements SensorEventListener {

	private SensorManager sManager;

	// for accelerometer values
	private TextView xCoor;
	private TextView yCoor;
	private TextView zCoor;

	// for gyroscope values
	private TextView x;
	private TextView y;
	private TextView z;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);

		// get the sensor service
		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// get the TextView for accelerometer and gyroscope
		xCoor = (TextView) findViewById(R.id.xCoor);
		yCoor = (TextView) findViewById(R.id.yCoor);
		zCoor = (TextView) findViewById(R.id.zCoor);

		x = (TextView) findViewById(R.id.x);
		y = (TextView) findViewById(R.id.y);
		z = (TextView) findViewById(R.id.z);

	}
	
    /*
     * (non-Javadoc)
     * @see android.hardware.
     * SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
     */
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// do something here, if sensor accuracy change(not implemented)
	}
     
	@Override
	public void onSensorChanged(SensorEvent event) {
		final int  last_index = 2; 
		synchronized (this) {

			// check sensor type and assign directions
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				xCoor.setText("Acceleration x :"
						+ Float.toString(event.values[0]));
				yCoor.setText("Acceleration y:"
						+ Float.toString(event.values[1]));
				zCoor.setText("Acceleration z:"
						+ Float.toString(event.values[last_index]));
			} else {
				if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
					x.setText("Gyroscope x in rad/s:"
							+ Float.toString(event.values[0]));
					y.setText("Gyroscope y in rad/s:"
							+ Float.toString(event.values[1]));
					z.setText("Gyroscope z in rad/s:"
							+ Float.toString(event.values[last_index]));
				} else {
					return;
				}
			}
			
		}
	}

	/*
	 * it is important to register the SensorListener in the Activity
	 */
	@Override
	protected void onResume() {
		super.onResume();
		sManager.registerListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		sManager.registerListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_FASTEST);

	}

	/*
	 * When this Activity isn't visible anymore then it is important to
	 * unregister the SensorListener in The Activity
	 */
	@Override
	protected void onStop() {
		super.onStop();
		sManager.unregisterListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		sManager.unregisterListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
	}

}
