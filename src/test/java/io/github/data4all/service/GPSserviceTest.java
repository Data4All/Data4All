/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.service;

import static org.junit.Assert.assertEquals;
import io.github.data4all.model.data.Track;
import io.github.data4all.util.Optimizer;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;
import android.location.Location;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GPSserviceTest {

    /**
     * Clear the Optimizer after every test.
     * 
     * @author tbrose
     */
    @After
    public void tearDown() {
        Optimizer.clear();
    }

    @Test
    public void testGPSservice() throws Exception {

        Intent startIntent = new Intent(Robolectric.application,
                GPSservice.class);
        GPSservice service = new GPSservice();

        service.onStartCommand(startIntent, 0, 0);

        assertEquals("io.github.data4all.service.GPSservice", startIntent
                .getComponent().getClassName());

    }

    /**
     * Tests, if there is no exception thrown if the method is called for the
     * first time.
     * 
     * @author tbrose
     */
    @Test
    public void test_onLocationChanged_firstChange() throws Exception {
        GPSservice service = new GPSservice();
        Field trackField = GPSservice.class.getDeclaredField("track");
        trackField.setAccessible(true);
        trackField.set(service, new Track());

        service.onLocationChanged(new Location("TEST"));
    }

}
