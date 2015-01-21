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
    long startTime;

    // delay time for sensor
    long reactionTime;

    // sensor Manager
    private SensorManager sManager;

    // jump value, which detected if phone is moving
    private static final double JUMP_VALUE = 2;

    private String TAG = this.getClass().getSimpleName();

    /** The last x position. */
    private float lastX = 0;

    /** The last y position. */
    private float lastY = 0;

    /** The last z position. */
    private float lastZ = 0;

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
        Log.d("BLUB", "" + System.currentTimeMillis());
        /*
         * if user has touching the screen, then store the time, when he has
         * touching the screen
         */
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTime = System.currentTimeMillis();
            Log.i(TAG, "startTime " + startTime);
        }
        return super.onTouchEvent(event);
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
        // calculate movement
        float totalMovement = Math.abs(event.values[0] + event.values[1]
                + event.values[2] - lastX - lastY - lastZ);

        Log.i("BLUB", "totalMovement: " + totalMovement + " " + event.values[0]
                + " " + event.values[1] + " " + event.values[2]);
        if (totalMovement > JUMP_VALUE) {
            // Jump detected .....
            long millis = System.currentTimeMillis();
            reactionTime = millis - startTime;

            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];

            AlertDialog ad = new AlertDialog.Builder(this).setTitle("Delay:")
                    .setMessage("" + reactionTime)
                    .setPositiveButton("Okay", null).create();

            ad.show();
            Log.i(TAG, "reactionTime: " + reactionTime);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}
