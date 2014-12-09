package io.github.data4all.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Intent;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class GPSserviceTest {
    
    
    @Test
    public void testGPSservice() throws Exception {

        Intent startIntent = new Intent(Robolectric.application, GPSservice.class);
        GPSservice service = new GPSservice();

        service.onStartCommand(startIntent, 0, 0);
           
        assertEquals("io.github.data4all.service.GPSservice",startIntent.getComponent().getClassName());
        
    }

}
