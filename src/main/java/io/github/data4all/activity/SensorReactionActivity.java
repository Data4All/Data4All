package io.github.data4all.activity;

import io.github.data4all.R;
import android.annotation.SuppressLint;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressLint("ClickableViewAccessibility")
public class SensorReactionActivity extends SensorActivity implements
		OnTouchListener {

	// examine if the User touch the screen
	boolean isTouched;

	// variable for storing the time of first click
	long startTime;

	// time,when the current action started
	long touchInitialTime;

	// store how many time i pressed the screen
	long duration;

	// sensor reference time
	long sensorReferenceTime;
	
	
	//delay time for sensor
	long reactionTime;
	
	private static final float NS2S = 1.0f / 1000000000.0f;

	private static final String TAG = "SensorReactionActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);

		// i suppose the user don't touch the screen, when this activity is
		// starting
		isTouched = false;
	}
    
	public void onSensorChanged(SensorEvent event){
		if(sensorReferenceTime ==0){
			sensorReferenceTime = event.timestamp;
		}
		else{
			 sensorReferenceTime = System.currentTimeMillis() + (long) ((event.timestamp - sensorReferenceTime) * NS2S);    
		}
	}
	
	/**
	 * when the user touch the screen then store the initial Time on the variable startTime
	 * and the time when the user have touching the screen on the variable touchInitialTime
	 * 
	 */
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
			// show that i have touched the screen
			isTouched = true;
			startTime = System.currentTimeMillis();
			touchInitialTime = event.getDownTime();

            if(sensorReferenceTime >= touchInitialTime){
            	reactionTime = sensorReferenceTime - touchInitialTime;
            }	
            
			Log.d(TAG, "reaction time " + reactionTime);

		}  else if (event.getAction() == MotionEvent.ACTION_UP) {
			isTouched = false;
			long time = System.currentTimeMillis() - touchInitialTime;
			duration = duration + time;

			Log.d(TAG, "total time " + duration);
		}

		return super.onTouchEvent(event);
	}

}
