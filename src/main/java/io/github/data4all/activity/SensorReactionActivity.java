package io.github.data4all.activity;

import io.github.data4all.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * the class SensorReactionActivity 
 * compute the reaction-time for sensors when a user touching the screen
 * or when he move the phone. then it get a Dialog, when a move is detected
 *  
 * @author Steeve
 *
 */

public class SensorReactionActivity extends Activity implements
		OnTouchListener, SensorEventListener {

	// variable for storing the time of first click
	private long touchTime;

	// delay time for sensor
	private long reactionTime;

	// sensor Manager
	private SensorManager sManager;

	// jump value, which detected if phone is moving
	private static final double JUMP_VALUE = 2;

	// check if user touching the screen
	private boolean isTouched;

	private String tag = this.getClass().getSimpleName();

	// the actual x position
	private float actualX;

	// the actual y position
	private float actualY;

	// the actual z position
	private float actualZ;

	// The last x position.
	private float lastX;

	// The last y position.
	private float lastY;

	// The last z position.
	private float lastZ;

	// current time in nanoseconds
	private long nano;
	
	// last index of event.values
	public	static final int LAST_INDEX = 2;

	// show a AlertDialog, when phone moving or when user touching the screen
	private AlertDialog showDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("BLUB", "sensorReactionActivity started");
		setContentView(R.layout.activity_reaction_sensor);
		findViewById(R.id.main).setOnTouchListener(this);
		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	/*
	 * register the SensorListener in the Activity
	 */
	@Override
	protected void onResume() {
		super.onResume();
		sManager.registerListener(this,
				sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
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
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		Log.d("BLUB", "" + System.nanoTime());
		/*
		 * if user has touching the screen, then store the time, when he has
		 * touching the screen
		 */
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isTouched = true;
			touchTime = System.nanoTime();
			Log.i(tag, "touchTime " + touchTime);
		}
		return true;
	}

	/*
	 * if a movement is detected,then compute the reactionTime
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		Log.d("BLUB", "onSensorChanged");
      
		actualX = event.values[0];
		actualY = event.values[1];
		actualZ = event.values[LAST_INDEX];

		if ((Float.compare(this.lastX, 0) == 0) && (Float.compare(this.lastY, 0) 
				== 0) && (Float.compare(this.lastZ, 0) == 0)) {
			lastX = actualX;
			lastY = actualY;
			lastZ = actualZ;
			return;
		}

		// when a movement is detected
		if (Math.abs(actualX - lastX) > JUMP_VALUE
				|| Math.abs(actualY - lastY) > JUMP_VALUE
				|| Math.abs(actualZ - lastZ) > JUMP_VALUE) {
			nano = System.nanoTime();
			String message = "";
			// compute the reactionTime when user touching on the screen
			if (isTouched) {
				message = "touchevent: ";
				reactionTime = nano - touchTime;
				isTouched = false;
			} else {
				// compute the reactionTime when user moving the phone
				message = "shakeEvent: ";
				reactionTime = nano - event.timestamp;
			}

			if (showDialog == null) {
				showDialog = new AlertDialog.Builder(this).setTitle("Delay:")
						.setMessage(message + reactionTime)
						.setPositiveButton("Okay", null).create();
			} else {
				showDialog.setMessage(message + reactionTime);
			}
			showDialog.show();
			Log.i(tag, "reactionTime: " + reactionTime);
		}

		lastX = actualX;
		lastY = actualY;
		lastZ = actualZ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
	 * .Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
         //		Not implemented
	}

	public boolean getIsTouched() {
		return isTouched;
	}

	public float getLastX() {
		return lastX;
	}

	public float getLastY() {
		return lastY;
	}

	public float getLastZ() {
		return lastZ;
	}

	public AlertDialog getShowDialog() {
		return showDialog;
	}
}
