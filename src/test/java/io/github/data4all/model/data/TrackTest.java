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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;
import android.os.Parcel;

/**
 * @author sbrede
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TrackTest {

    /**
     * Test method for {@link io.github.data4all.model.data.Track#Track()}.
     */
    @Test
    public void testTrack() {
        Track track = new Track();
        assertNotNull(track);
    }

    /**
     * Test method for
     * {@link io.github.data4all.model.data.Track#getTrackName()}.
     */
    @Test
    public void testGetTrackName() {
        Track track = new Track();
        assertFalse("trackname should not be size 0", track.getTrackName()
                .isEmpty());
    }

    /**
     * Test method for
     * {@link io.github.data4all.model.data.Track#addTrackPoint(android.location.Location)}
     * .
     */
    @Test
    public void testAddTrackPoint() {
        Track track = new Track();
        assertTrue("Should be empty", track.getTrackPoints().isEmpty());

        Location aloc = new Location("provider");
        aloc.setLatitude(53.07929619999999);
        aloc.setLongitude(8.801693699999987);
        aloc.setTime(new Date().getTime());

        track.addTrackPoint(aloc);

        assertTrue("Should contain one element, but was "
                + track.getTrackPoints().size(),
                track.getTrackPoints().size() == 1);

        double expectedALat = track.getLastTrackPoint().getLat();
        double expectedALon = track.getLastTrackPoint().getLon();
        double expectedAAlt = track.getLastTrackPoint().getAlt();

        double actualALat = aloc.getLatitude();
        double actualALon = aloc.getLongitude();
        double actualAAlt = aloc.getAltitude();

        double delta = 0;

        assertEquals(expectedALat, actualALat, delta);
        assertEquals(expectedALon, actualALon, delta);
        assertEquals(expectedAAlt, actualAAlt, delta);

        Location bloc = new Location("provider");
        bloc.setLatitude(53.0792962);
        bloc.setLongitude(8.8016937);
        bloc.setAltitude(2911.4);
        bloc.setTime(new Date().getTime());

        track.addTrackPoint(bloc);
        assertTrue("Should contain two elements",
                track.getTrackPoints().size() == 2);

        double expectedBLat = track.getLastTrackPoint().getLat();
        double expectedBLon = track.getLastTrackPoint().getLon();
        double expectedBAlt = track.getLastTrackPoint().getAlt();

        double actualBLat = bloc.getLatitude();
        double actualBLon = bloc.getLongitude();
        double actualBAlt = bloc.getAltitude();

        assertEquals(expectedBLat, actualBLat, delta);
        assertEquals(expectedBLon, actualBLon, delta);
        assertEquals(expectedBAlt, actualBAlt, delta);

    }

    /**
     * Test method for
     * {@link io.github.data4all.model.data.Track#getTrackPoints()}.
     */
    @Test
    public void testGetTrackPoints() {
        Track track = new Track();
        assertTrue("Should be empty", track.getTrackPoints().isEmpty());

        Location aloc = new Location("provider");
        aloc.setLatitude(53.07929619999999);
        aloc.setLongitude(8.801693699999987);
        aloc.setTime(new Date().getTime());

        Location bloc = new Location("provider");
        bloc.setLatitude(53.0792962);
        bloc.setLongitude(8.8016937);
        bloc.setAltitude(2911.4);
        bloc.setTime(new Date().getTime());

        Location cloc = new Location("provider");
        cloc.setLatitude(53.0792963);
        cloc.setLongitude(8.80169379999);
        cloc.setTime(new Date().getTime());

        track.addTrackPoint(aloc);
        track.addTrackPoint(bloc);
        track.addTrackPoint(cloc);

        assertTrue("Should be three but was " + track.getTrackPoints().size(),
                track.getTrackPoints().size() == 3);
    }

    /**
     * Test method for
     * {@link io.github.data4all.model.data.Track#getLastTrackPoint()}.
     */
    @Test
    public void testGetLastTrackPoint() {
        Track track = new Track();
        assertTrue("Should be empty", track.getTrackPoints().isEmpty());
        assertNull("Should be null", track.getLastTrackPoint());

        Location aloc = new Location("provider");
        aloc.setLatitude(53.079296);
        aloc.setLongitude(8.801693);
        aloc.setTime(new Date().getTime());

        track.addTrackPoint(aloc);

        double expectedALat = track.getLastTrackPoint().getLat();
        double expectedALon = track.getLastTrackPoint().getLon();
        double expectedAAlt = track.getLastTrackPoint().getAlt();

        double actualBLat = aloc.getLatitude();
        double actualBLon = aloc.getLongitude();
        double actualBAlt = aloc.getAltitude();

        double delta = 0;

        assertEquals(expectedALat, actualBLat, delta);
        assertEquals(expectedALon, actualBLon, delta);
        assertEquals(expectedAAlt, actualBAlt, delta);

        Location bloc = new Location("provider");
        bloc.setLatitude(53.079296);
        bloc.setLongitude(8.801693);
        bloc.setAltitude(2911.4);
        bloc.setTime(new Date().getTime());

        track.addTrackPoint(bloc);
        assertTrue("Should be the same but was "
                + track.getLastTrackPoint().toString(), track
                .getLastTrackPoint().equals(new TrackPoint(bloc)));
    }

    /**
     * Test method for {@link io.github.data4all.model.data.Track#toString()}.
     */
    @Test
    public void testToString() {
        Track track = new Track();

        Location aloc = new Location("provider");
        aloc.setLatitude(53.07929619999999);
        aloc.setLongitude(8.801693699999987);
        aloc.setTime(new Date().getTime());

        Location bloc = new Location("provider");
        bloc.setLatitude(53.0792962);
        bloc.setLongitude(8.8016937);
        bloc.setAltitude(2911.4);
        bloc.setTime(new Date().getTime());

        Location cloc = new Location("provider");
        cloc.setLatitude(53.0792963);
        cloc.setLongitude(8.80169379999);
        cloc.setTime(new Date().getTime());

        track.addTrackPoint(aloc);
        track.addTrackPoint(bloc);
        track.addTrackPoint(cloc);

        String expected = track.getTrackName() + "\n";
        for (TrackPoint tp : track.getTrackPoints()) {
            String tmp = tp.toString();
            expected += tmp + "\n";

        }
        String actual = track.toString();
        assertTrue("Should be " + expected + " but was " + actual,
                expected.equals(actual));
    }

    /**
     * Test method for
     * {@link io.github.data4all.model.data.Track#describeContents()}.
     */
    @Test
    public void testDescribeContents() {
        Track track = new Track();
        assertTrue(track.describeContents() == 0);
    }

    /**
     * Test method for
     * {@link io.github.data4all.model.data.Track#writeToParcel(android.os.Parcel, int)}
     * .
     */
    @Test
    public void testWriteToParcel() {
        Parcel newParcel = Parcel.obtain();
        Track track = new Track();

        Location aloc = new Location("provider");
        aloc.setLatitude(53.07929619999999);
        aloc.setLongitude(8.801693699999987);
        aloc.setTime(new Date().getTime());

        Location bloc = new Location("provider");
        bloc.setLatitude(53.0792962);
        bloc.setLongitude(8.8016937);
        bloc.setAltitude(2911.4);
        bloc.setTime(new Date().getTime());

        Location cloc = new Location("provider");
        cloc.setLatitude(53.0792963);
        cloc.setLongitude(8.80169379999);
        cloc.setTime(new Date().getTime());

        track.addTrackPoint(aloc);
        track.addTrackPoint(bloc);
        track.addTrackPoint(cloc);

        track.writeToParcel(newParcel, 0);

        newParcel.setDataPosition(0);
        Track deParcelTrack = Track.CREATOR.createFromParcel(newParcel);

        assertEquals(track.getTrackName(), deParcelTrack.getTrackName());

        List<TrackPoint> tp = track.getTrackPoints();
        List<TrackPoint> tpParcel = deParcelTrack.getTrackPoints();

        for (int i = 0; i < tp.size(); i++) {
            assertTrue(tp.get(i).equals(tpParcel.get(i)));
        }
    }
}
