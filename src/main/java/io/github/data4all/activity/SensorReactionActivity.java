package io.github.data4all.activity;

import io.github.data4all.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressLint("ClickableViewAccessibility")
public class SensorReactionActivity extends Activity implements
		OnTouchListener {

	// variable for storing the time of first click
	long startTime;

	// delay time for sensor
	long reactionTime;

	private float[] lastValues;

	private static final double JUMP_VALUE = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
	}

	public void onSensorChanged(SensorEvent event) {
		lastValues = event.values;
		if (Math.sqrt(Math.pow(event.values[0] - lastValues[0], 2)
				+ Math.pow(event.values[1] - lastValues[1], 2)
				+ Math.pow(event.values[2] - lastValues[2], 2)) > JUMP_VALUE) {
			// Jump detected .....
			long millis = System.currentTimeMillis();
			reactionTime = millis - startTime;

			new AlertDialog.Builder(this).setTitle("Delay:")
					.setMessage("" + reactionTime)
					.setPositiveButton("Okay", null).show();
		}
		
	}

	/**
	 * when the user touch the screen then store the initial Time on the
	 * variable startTime and the time when the user have touching the screen on
	 * the variable touchInitialTime
	 * 
	 */

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startTime = System.currentTimeMillis();
		}
		return super.onTouchEvent(event);
	}

}
