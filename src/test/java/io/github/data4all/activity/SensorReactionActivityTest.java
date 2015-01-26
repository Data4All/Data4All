package io.github.data4all.activity;

import junit.textui.TestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricBase;
import org.robolectric.RobolectricShadowOfLevel16;
import org.robolectric.RobolectricShadowOfLevel9;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.RoboLayoutInflater;
import org.robolectric.shadows.ShadowSensorManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import static org.junit.Assert.*;
//import static org.robolectric.Robolectric.shadowOf;
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class SensorReactionActivityTest {

	private SensorReactionActivity sensorReactionActivity;
	private ShadowSensorManager shadow;
	SensorManager sensorManager;
	
	@Before
	public void setUp() throws Exception {

		this.sensorReactionActivity = Robolectric
				.buildActivity(SensorReactionActivity.class).create().get();
		sensorManager = (SensorManager) sensorReactionActivity.getSystemService(Context.SENSOR_SERVICE);
		
	}

	@Test
	public void checkActivityNotNull() throws Exception {
		assertNotNull(sensorReactionActivity);
	}
	
	@Test
	public void touchEventStart (){
		//create touch event
		MotionEvent event  = MotionEvent.obtain(System.nanoTime(),System.nanoTime(), MotionEvent.ACTION_DOWN, 1.5f, 2.3f,  0);
		sensorReactionActivity.onTouch(sensorReactionActivity.getCurrentFocus(), event);
		assertTrue(sensorReactionActivity.getIsTouched());
	}
	
	@Test
	public void touchSensorEventStart (){
		shadow = Robolectric.shadowOf(sensorManager);
		//create a sensorevent
		SensorEvent sevent=shadow.createSensorEvent();
		
		sevent.values[0]=1.1f;
		sevent.values[1]=1.1f;
		sevent.values[2]=1.1f;
		sevent.timestamp=System.nanoTime();
		
		//first fired the event;
		sensorReactionActivity.onSensorChanged(sevent);
		//register the last position
		assertTrue(sensorReactionActivity.getLastX()==1.1f);
		assertTrue(sensorReactionActivity.getLastY()==1.1f);
		assertTrue(sensorReactionActivity.getLastZ()==1.1f);
		assertNull(sensorReactionActivity.showDialog);
		
		//create touch event
		MotionEvent event  = MotionEvent.obtain(System.nanoTime(),System.nanoTime(), MotionEvent.ACTION_DOWN, 1.5f, 2.3f,  0);
		sensorReactionActivity.onTouch(sensorReactionActivity.getCurrentFocus(), event);
		assertTrue(sensorReactionActivity.getIsTouched());
		
		//create new SensorEvent
		sevent=shadow.createSensorEvent();
		sevent.values[0]=4.1f;
		sevent.values[1]=1.1f;
		sevent.values[2]=1.1f;
		sevent.timestamp=System.nanoTime();
		//second fired the event;
		sensorReactionActivity.onSensorChanged(sevent);
		assertNotNull(sensorReactionActivity.showDialog);
		
		
		
	}
	
	@Test
	public void checkPositionMoved (){
		//SensorEvent event = S
	}
    
	
	
	
	//ich muss die sensor werte beiinflüssen und prüfen was passiert
	//shadowDialog.get gucken ob er ein Dialog geworfen hat.
	// roboelectric sensor recherche addsensor()
	//createsensorevent()
	
}
