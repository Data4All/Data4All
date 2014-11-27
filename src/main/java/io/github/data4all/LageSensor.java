package io.github.data4all;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class LageSensor extends Activity implements SensorEventListener{
  
	
private SensorManager sManager;
	
	Sensor accelerometer;
	
	TextView xCoor;
	TextView yCoor;
	TextView zCoor;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		
		//get the TextView
		xCoor = (TextView) findViewById(R.id.xCoor);
		yCoor = (TextView) findViewById(R.id.yCoor);
		zCoor = (TextView) findViewById(R.id.zCoor);
		
		//get the sensor service
		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//get the accelerometer sensor
		sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// do something here, if sensor accuracy change		
	}

	public void onSensorChanged(SensorEvent event) {
		// check sensor type
		if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
			
			//assign directions
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			//display values using Textview
			xCoor.setText("X: " + x);
			yCoor.setText("Y: " + y );
			zCoor.setText("Z: " + z);
		}	
	}
	
	/*
	 * After we have read the values of x,y,z
	 * it is important to register the SensorListener in the Activity
	 */
	@Override
	protected void onResume(){
		super.onResume();
		sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	/*
	 * After we have read the values of x,y and z
	 * it is important to unregister the SensorListener in The Activity
	 * 
	 */
	@Override
	protected void onPause(){
		super.onPause();
		sManager.unregisterListener(this);
	}

	
	
}
