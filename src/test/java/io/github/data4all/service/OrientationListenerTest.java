package io.github.data4all.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OrientationListenerTest {
	
    /**
     * Test basic startup/shutdown of Service
     */
    @Test
    public void testOrientationListener() throws Exception {

        Intent startIntent = new Intent(Robolectric.application,
                OrientationListener.class);
        OrientationListener orientationListener = new OrientationListener();
        orientationListener.onStartCommand(startIntent, 0, 0);

        assertEquals("io.github.data4all.service.OrientationListener", startIntent
                .getComponent().getClassName());

    }
    
    
}
