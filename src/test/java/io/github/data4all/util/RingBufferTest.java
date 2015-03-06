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
package io.github.data4all.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;

/**
 * Test cases for the RingBuffer class
 * 
 * @author konerman
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RingBufferTest {

    Location loc1;
    Location loc2;
    Location loc3;
    Location loc4;
    Location loc5;
    Location loc6;
    Location loc7;

    @Before
    public void setUp() throws Exception {

        loc1 = new Location("GPS");
        loc2 = new Location("GPS");
        loc3 = new Location("GPS");
        loc4 = new Location("GPS");
        loc5 = new Location("GPS");
        loc6 = new Location("GPS");
        loc7 = new Location("GPS");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testPut() {
        RingBuffer ringBuffer = new RingBuffer(5);
        ringBuffer.put(loc1);
        ringBuffer.put(loc2);
        ringBuffer.put(loc3);
        assertEquals(3, ringBuffer.getSize());
        ringBuffer.put(loc4);
        ringBuffer.put(loc5);
        assertEquals(5, ringBuffer.getSize());
        // ringBuffer is full, size should stay the same
        ringBuffer.put(loc6);
        assertEquals(5, ringBuffer.getSize());
        ringBuffer.put(loc7);
        assertEquals(5, ringBuffer.getSize());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testGet() {

        RingBuffer ringBuffer = new RingBuffer(5);
        ringBuffer.put(loc1);
        ringBuffer.put(loc2);
        ringBuffer.put(loc3);
        ringBuffer.put(loc4);
        ringBuffer.put(loc5);
        assertEquals(loc1, ringBuffer.get(0));
        assertEquals(loc3, ringBuffer.get(2));
        assertEquals(loc5, ringBuffer.get(4));
        // ringBuffer is full, should overwrite the oldest position
        ringBuffer.put(loc6);
        assertEquals(loc6, ringBuffer.get(0));
    }

    /**
     * length of the returned array should always be the same.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testGetAll() {
        RingBuffer ringBuffer = new RingBuffer(4);
        ringBuffer.put(loc1);
        ringBuffer.put(loc2);
        assertEquals(4, ringBuffer.getAll().size());
        ringBuffer.put(loc3);
        ringBuffer.put(loc4);
        assertEquals(4, ringBuffer.getAll().size());
        ringBuffer.put(loc5);
        assertEquals("blub", 4, ringBuffer.getAll().size());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGetLast() {
        RingBuffer ringBuffer = new RingBuffer(3);

        ringBuffer.put(loc1);
        assertEquals(loc1, ringBuffer.get(0));
        assertEquals(loc1, ringBuffer.getLast());
        ringBuffer.put(loc2);
        assertEquals(loc2, ringBuffer.getLast());
        ringBuffer.put(loc3);
        assertEquals(loc3, ringBuffer.getLast());
        ringBuffer.put(loc4);
        assertEquals(loc4, ringBuffer.getLast());
        ringBuffer.put(loc5);
        assertEquals(loc5, ringBuffer.getLast());
    }

    /**
     * tests an empty ringbuffer
     */

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testempty() {
        RingBuffer ringBuffer = new RingBuffer(3);

        assertEquals(null, ringBuffer.get(0));

    }

}
