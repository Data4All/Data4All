package io.github.data4all.activity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class SensorReactionActivityTest {

	SensorReactionActivity sensorReactionActivity;
	
	
    @Before
	public void setUp() throws Exception {
		
		this.sensorReactionActivity = Robolectric
				.buildActivity(SensorReactionActivity.class).create().get();
	}

	@Test
	public void checkActivityNotNull() throws Exception {
		assertNotNull(sensorReactionActivity);
	}
	
	

	

	
}
