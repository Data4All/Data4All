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
public class GPSserviceTest {

    @Test
    public void testGPSservice() throws Exception {

        Intent startIntent = new Intent(Robolectric.application,
                GPSservice.class);
        GPSservice service = new GPSservice();

        service.onStartCommand(startIntent, 0, 0);

        assertEquals("io.github.data4all.service.GPSservice", startIntent
                .getComponent().getClassName());

    }

}
