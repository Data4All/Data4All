/*
* Copyright (c) 2014, 2015 Data4All
*
* <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain a
* copy of the License at
*
* <p>http://www.apache.org/licenses/LICENSE-2.0
*
* <p>Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
package io.github.data4all.model.data;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;
import android.os.Parcel;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TrackPointTest {

    private Location loc;
    private TrackPoint tp;

    @Before
    public void setUp() {
        loc = new Location("provider");
        loc.setLatitude(53.07929619999999);
        loc.setLongitude(8.801693699999987);

        long time = new Date().getTime();
        loc.setTime(time);

        tp = new TrackPoint(loc);

    }

    @Test
    public void testTrackPoint() {
        assertNotNull(new TrackPoint(loc));
    }

    @Test
    public void testGetLat() {
        assertEquals(loc.getLatitude(), tp.getLat(), 0);
    }

    @Test
    public void testGetLon() {
        assertEquals(loc.getLongitude(), tp.getLon(), 0);
    }

    @Test
    public void testGetTime() {
        assertEquals(loc.getTime(), tp.getTime(), 0);
    }

    @Test
    public void testGetAlt() {
        assertEquals(loc.getAltitude(), tp.getAlt(), 0);
    }

    @Test
    public void testHasAltitude() {
        assertFalse(tp.hasAltitude());
        assertTrue(!tp.hasAltitude());
    }

    @Test
    public void testDescribeContents() {
        assertTrue(tp.describeContents()==0);
    }

    @Test
    public void testWriteToParcel() {
        Parcel newParcel = Parcel.obtain();
        
        tp.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        TrackPoint deParcelTp = TrackPoint.CREATOR.createFromParcel(newParcel);
        
        assertEquals(tp.getLon(), deParcelTp.getLon(), 0);
        assertEquals(tp.getLat(), deParcelTp.getLat(), 0);
        assertEquals(tp.getAlt(), deParcelTp.getAlt(), 0);
        assertEquals(tp.getTime(), deParcelTp.getTime(), 0);
    }

    @Test
    public void testEqualsTrackPoint() {
        assertTrue(tp.equals(tp));
        
        Location newLoc = new Location("provider");
        newLoc.setLongitude(8.801693699999987);
        newLoc.setLatitude(53.07929619999999);
        newLoc.setAltitude(2911.4);
        newLoc.setTime(new Date().getTime());
        TrackPoint newTp = new TrackPoint(newLoc);
        
        assertFalse(tp.equals(newTp));
    }

}
