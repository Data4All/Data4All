package io.github.data4all.listener;

import io.github.data4all.activity.CameraActivity;
import io.github.data4all.util.HorizonCalculationUtil;
import io.github.data4all.util.MathUtil;

import java.text.NumberFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class CaptureSensorListener implements SensorEventListener {

    // Static Tag for Debug
    private static final String TAG = CaptureSensorListener.class
            .getSimpleName();

    private CameraActivity context;

    // ACCELEROMETER Sensor Values
    private float[] aValues = new float[3];

    // MAGNETIC_FIELD Sensor Values
    private float[] mValues = new float[3];

    private float[] laValues = new float[3];

    public static final int SAFE_TILE_ANGLE = 85;

    GeomagneticField geoField;

    private int oHeight;

    private final Sensor mRotationSensor;
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;

    private float cameraVerticalViewAngle;

    public CaptureSensorListener(CameraActivity context) {
        this.context = context;

        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mRotationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences settings = context.getSharedPreferences(null,
                Context.MODE_PRIVATE);
        cameraVerticalViewAngle = settings.getFloat(
                "camera_vertical_view_angle", 0.0f);

        oHeight = settings.getInt("observer_height", 0);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "SENSOR CHANGED");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            laValues = aValues.clone();
            aValues = event.values.clone();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mValues = event.values.clone();
        }

        if (Math.abs(Math.toDegrees(laValues[1]) - Math.toDegrees(aValues[1])) > 1) {

            update(calculateOrientation());
        }

    }

    private void update(float[] values) {

        updateAssistView(calculateHorizontalLine());

    }

    private void updateAssistView(float[] pointsToDraw) {
        Log.d(TAG, "update assist view");
        context.updateCameraAssistView(pointsToDraw);
    }

    private float[] calculateHorizontalLine() {
        float[] points = null;
        /**
         * float[] points = HorizonCalculationUtil.calcHorizontalPoints(
         * cameraVerticalViewAngle, cameraVerticalViewAngle,
         * cameraVerticalViewAngle, cameraVerticalViewAngle,
         * cameraVerticalViewAngle, null);
         **/
        
        return points;
    }

    private float[] calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        float[] outR = new float[9];

        SensorManager.getRotationMatrix(R, null, aValues, mValues);

        // Version_1 -- Pretty Works
        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, outR);

        // Version_2 -- APIs
        // SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y,
        // SensorManager.AXIS_MINUS_X, outR);

        // Version_3 -- StackWorkflow
        // SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z,
        // SensorManager.AXIS_MINUS_X, outR);

        SensorManager.getOrientation(outR, values);

        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);
        return values;
    }
}
