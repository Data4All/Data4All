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
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;

/**
 * test cases for the OrientationListener class
 * 
 * @author Steeve
 *
 */

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OrientationListenerTest {

    // the service to test
    private OrientationListener service;

    public static final String SERVICE = "io.github.data4all.service.OrientationListener";

    @Before
    public void setUp() throws Exception {
        service = null;
        try {
            service = new OrientationListener();

        } catch (Exception e) {
            assertNotNull(service);
        }
    }

    /**
     * Shut down the Service under Test and make sure that all resources are
     * cleaned up
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        service = null;
    }

    /**
     * Test basic startup/shutdown of Service
     */
    @Test
    public void testOrientationListener() throws Exception {

        Intent startIntent =
                new Intent(Robolectric.application, OrientationListener.class);
        OrientationListener orientationListener = new OrientationListener();
        orientationListener.onStartCommand(startIntent, 0, 0);

        assertEquals("io.github.data4all.service.OrientationListener",
                startIntent.getComponent().getClassName());
    }
}
