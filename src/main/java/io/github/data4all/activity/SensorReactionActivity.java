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

@SuppressLint("ClickableViewAccessibility")
public class SensorReactionActivity extends Activity implements
        OnTouchListener, SensorEventListener {

    // variable for storing the time of first click
   private long startTime;

    // delay time for sensor
    private long reactionTime;

    // sensor Manager
    private SensorManager sManager;

    // jump value, which detected if phone is moving
    private static final double JUMP_VALUE = 2;

	// check if user touching the screen
	private boolean isTouched = false;

    private String TAG = this.getClass().getSimpleName();

    /** The last x position. */
    private float lastX = 0;

    /** The last y position. */
    private float lastY = 0;

    /** The last z position. */
    private float lastZ = 0;

	/** show a AlertDialog, when phone moving or when user touching the screen */
	AlertDialog showDialog = null;

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
			Log.i(TAG, "touchTime " + touchTime);
		}
		return true;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 * 
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d("BLUB", "onSensorChanged");

		float actualX = event.values[0];
		float actualY = event.values[1];
		float actualZ = event.values[2];

		if (lastX == 0 && lastY == 0 && lastZ == 0) {
			lastX = actualX;
			lastY = actualY;
			lastZ = actualZ;
			return;
		}

		//when a movement is detected
		if (Math.abs(actualX - lastX) > JUMP_VALUE
				|| Math.abs(actualY - lastY) > JUMP_VALUE
				|| Math.abs(actualZ - lastZ) > JUMP_VALUE) {
			long nano = System.nanoTime();
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
			Log.i(TAG, "reactionTime: " + reactionTime);
		}

		lastX = actualX;
		lastY = actualY;
		lastZ = actualZ;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public boolean getIsTouched(){
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
}
