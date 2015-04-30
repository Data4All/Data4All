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
package io.github.data4all.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.github.data4all.handler.CopyOfDataBaseHandler;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.model.data.User;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;

/**
 * This class tests all methods of the DataBaseHandler.
 * 
 * @author Kristin Dahnken
 * @author fkirchge
 * @author tbrose
 * 
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DataBaseHandlerTest {

    private DataBaseHandler dbHandler;

    @Before
    public void setUp() {
        dbHandler = new DataBaseHandler(Robolectric.application);
        System.out.println("Setup done.");
    }

    @Test
    public void testUserCRUD() {
        // TODO
    }

    @Test
    public void testDataElementCRUD() throws JSONException {
        // TODO
    }

    @Test
    public void testTrackPointCRUD() {
        Location loc1 = new Location("provider");
        loc1.setAltitude(10.10);
        loc1.setLatitude(10.10);
        loc1.setLongitude(10.10);
        loc1.setTime(10000);
        TrackPoint tp1 = new TrackPoint(loc1);
        tp1.setID(1);

        Location loc2 = new Location("provider");
        loc2.setAltitude(11.11);
        loc2.setLatitude(11.11);
        loc2.setLongitude(11.11);
        loc2.setTime(20000);
        TrackPoint tp2 = new TrackPoint(loc2);
        tp2.setID(2);

        Location loc3 = new Location("provider");
        loc3.setAltitude(12.12);
        loc3.setLatitude(12.12);
        loc3.setLongitude(12.12);
        loc3.setTime(30000);
        TrackPoint tp3 = new TrackPoint(loc3);
        tp3.setID(3);

        List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();
        trackPoints.add(tp1);
        trackPoints.add(tp2);
        trackPoints.add(tp3);

        dbHandler.createTrackPoints(trackPoints);

        List<Long> trackPointIDs = new ArrayList<Long>();
        trackPointIDs.add(tp1.getID());
        trackPointIDs.add(tp2.getID());
        trackPointIDs.add(tp3.getID());

        trackPoints = dbHandler.getTrackPoints(trackPointIDs);

        assertEquals(10.10, trackPoints.get(0).getAlt(), 0.0);
        assertEquals(11.11, trackPoints.get(1).getLat(), 0.0);
        assertEquals(12.12, trackPoints.get(2).getLon(), 0.0);

        assertEquals(3, dbHandler.getTrackPointCount());

        trackPointIDs.remove(2);
        trackPointIDs.remove(1);
        dbHandler.deleteTrackPoints(trackPointIDs);

        assertEquals(2, dbHandler.getTrackPointCount());

        dbHandler.deleteAllTrackPoints();

        assertEquals(0, dbHandler.getTrackPointCount());
    }

    @Ignore
    @Test
    public void testTrackCRUD() throws JSONException {
        Location loc1 = new Location("User");
        loc1.setAltitude(10.10);
        loc1.setLatitude(10.10);
        loc1.setLongitude(10.10);
        loc1.setTime(10000);
        TrackPoint tp1 = new TrackPoint(loc1);
        tp1.setID(2);

        Location loc2 = new Location("User");
        loc2.setAltitude(11.11);
        loc2.setLatitude(11.11);
        loc2.setLongitude(11.11);
        loc2.setTime(20000);
        TrackPoint tp2 = new TrackPoint(loc2);
        tp2.setID(3);

        Location loc3 = new Location("User");
        loc3.setAltitude(12.12);
        loc3.setLatitude(12.12);
        loc3.setLongitude(12.12);
        loc3.setTime(30000);
        TrackPoint tp3 = new TrackPoint(loc3);
        tp3.setID(4);

        Location loc4 = new Location("User");
        loc4.setAltitude(13.13);
        loc4.setLatitude(13.13);
        loc4.setLongitude(13.13);
        loc4.setTime(13455);
        TrackPoint tp4 = new TrackPoint(loc4);
        tp4.setID(5);

        List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();
        trackPoints.add(tp1);
        trackPoints.add(tp2);
        trackPoints.add(tp3);

        Track track = new Track();
        track.setID(1);
        track.setTrackPoints(trackPoints);

        dbHandler.createGPSTrack(track);

        assertEquals(1, dbHandler.getGPSTrackCount());

        Track reTrack = dbHandler.getGPSTrack(track.getID());

        assertEquals(track.getTrackName(), reTrack.getTrackName());

        track.setTrackName("2015_02_20_15_18_25");

        trackPoints.add(tp4);
        track.setTrackPoints(trackPoints);

        dbHandler.updateGPSTrack(track);

        reTrack = dbHandler.getGPSTrack(track.getID());

        assertEquals(4, reTrack.getTrackPoints().size());
    }

    @After
    public void tearDown() {
        dbHandler.close();
    }
}
