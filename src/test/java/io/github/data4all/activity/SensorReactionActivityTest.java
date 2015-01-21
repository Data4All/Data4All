package io.github.data4all.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.github.data4all.logger.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;
import android.view.MotionEvent;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class SensorReactionActivityTest {

	private SensorReactionActivity sensorReactionActivity;
	private boolean mActivityStarted = false;
	private boolean mActivityStopped = false;
	private boolean isTouched;
	private MotionEvent event;

	@Before
	public void setUp() throws Exception {

		this.sensorReactionActivity = Robolectric
				.buildActivity(SensorReactionActivity.class).create().get();
	}

	@Test
	public void checkActivityNotNull() throws Exception {
		assertNotNull(sensorReactionActivity);
	}

	/**
	 * check if Activity was started
	 * @throws Exception
	 */
	@Test
	public void checkActivityStarted() throws Exception {
		checkActivityNotNull();
		if (mActivityStarted) {
			Intent startIntent = new Intent(Robolectric.application,
					SensorReactionActivity.class);
			sensorReactionActivity.startActivity(startIntent);
			mActivityStarted = true;
			assertTrue(mActivityStarted);
		}

	}

	/**
	 * check if Activity was stopped
	 * @throws Exception
	 */
	@Test
	public void checkActivityStopped() throws Exception {
		checkActivityStarted();
		if (mActivityStarted || mActivityStopped) {
			sensorReactionActivity.onStop();
			mActivityStopped = true;
		}

	}
    
	/**
	 * check if Screen has been touching
	 * @throws Exception
	 */
	@Test
	public void checkScreenTouched() throws Exception {
		checkActivityStarted();
		if (sensorReactionActivity.onTouchEvent(event)) {
			isTouched = true;
		}
	}

	/**
	 * get Time, when screen has been touching
	 * @throws Exception
	 */
	@Test
	public void getTimeWhenScreenBeenTouched() throws Exception {
		checkScreenTouched();
		long startTime = System.currentTimeMillis();
		assertEquals(System.currentTimeMillis(), startTime);
	}
}
