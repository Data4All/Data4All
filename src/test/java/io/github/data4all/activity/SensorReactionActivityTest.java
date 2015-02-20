package io.github.data4all.activity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSensorManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

/**
 * Test cases for the SensorReactionActivity class
 * 
 * @author Steeve
 */

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class SensorReactionActivityTest {

    // the activity to test
    private SensorReactionActivity sensorReactionActivity;

    // a object for the class shadowSensorManager
    private ShadowSensorManager shadow;
    private SensorManager sManager;
    private SensorEvent event;

    @Before
    public void setUp() throws Exception {

        this.sensorReactionActivity = Robolectric
                .buildActivity(SensorReactionActivity.class).create().get();

        shadow = new ShadowSensorManager();
        sManager = (SensorManager) Robolectric.application
                .getSystemService(Context.SENSOR_SERVICE);
        shadow.addSensor(Sensor.TYPE_ACCELEROMETER,
                sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(sensorReactionActivity);
    }

    @After
    public void tearDown() {
        sensorReactionActivity = null;
        sManager = null;
        shadow = null;
    }

    /*
     * create a touch event MotionEvent and check if isTouched is true
     */
    @Test
    public void touchEventStart() {
        // create touch event MotionEvent
        MotionEvent event = MotionEvent.obtain(System.nanoTime(),
                System.nanoTime(), MotionEvent.ACTION_DOWN, 1.5f, 2.3f, 0);
        sensorReactionActivity.onTouch(
                sensorReactionActivity.getCurrentFocus(), event);
        assertTrue(sensorReactionActivity.getIsTouched());
    }

    /*
     * check if the activity has a listener after Registering listener
     */
    @Test
    public void shouldReturnHasListenerAfterRegisteringListener() {
        SensorEventListener listener = registerListener();
        assertTrue(shadow.hasListener(listener));
    }

    /*
     * to register a listener
     */
    private SensorEventListener registerListener() {
        SensorEventListener listener = new SensorReactionActivity();
        Sensor accelerometer = sManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shadow.registerListener(listener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        return listener;
    }

    /*
     * check if the activity has always a listener after the listener was
     * unregister
     */
    @Test
    public void shouldReturnHasNoListenerAfterUnregisterListener() {
        SensorEventListener listener = registerListener();
        shadow.unregisterListener(listener,
                sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        assertFalse(shadow.hasListener(listener));
    }

    /*
     * check if the activity has a listener by Default
     */
    @Test
    public void shouldReturnHasNoListenerByDefault() {
        SensorEventListener listener = new SensorReactionActivity();
        assertFalse(shadow.hasListener(listener));
    }

    /*
     * check if a AlertDialog appear by default, when phone not move
     */
    @Test
    public void shouldReturnHasNoDialogByDefault() {
        event = shadow.createSensorEvent();
        event.timestamp = System.nanoTime();
        assertNull(sensorReactionActivity.getShowDialog());
    }

    /*
     * get a sensorevent
     */
    public SensorEvent getEvent() {
        return event;
    }
}
