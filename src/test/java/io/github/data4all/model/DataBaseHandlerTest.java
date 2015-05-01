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
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

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
    }

    @Test
    public void testUserCRUD() {
        // TODO
    }

    @Test
    public void getDataElementCount_empty_zeroSize() {
        assertEquals(0, dbHandler.getDataElementCount());
    }

    @Test
    public void getDataElementCount_oneNode_oneSize() {
        dbHandler.createDataElement(new Node(0, 0, 0));
        assertEquals(1, dbHandler.getDataElementCount());
    }

    @Test
    public void getDataElementCount_onePolyElement_oneSize() {
        PolyElement polyElement = new PolyElement(0, PolyElementType.WAY);
        polyElement.addNode(new Node(0, 0, 0));
        polyElement.addNode(new Node(0, 0, 0));
        dbHandler.createDataElement(polyElement);
        assertEquals(1, dbHandler.getDataElementCount());
    }

    @Test
    public void getDataElementCount_oneNodeAndOnePolyElement_twoSize() {
        dbHandler.createDataElement(new Node(0, 0, 0));
        PolyElement polyElement = new PolyElement(0, PolyElementType.WAY);
        polyElement.addNode(new Node(0, 0, 0));
        polyElement.addNode(new Node(0, 0, 0));
        dbHandler.createDataElement(polyElement);
        assertEquals(2, dbHandler.getDataElementCount());
    }

    @Test
    public void createDataElement_insertNode_rightElementSaved() {
        final double lat = 1.2;
        final double lon = 3.4;
        dbHandler.createDataElement(new Node(1, lat, lon));

        AbstractDataElement element = dbHandler.getAllDataElements().get(0);
        assertEquals(Node.class, element.getClass());

        Node node = (Node) element;
        assertEquals(lat, node.getLat(), 1e-10);
        assertEquals(lon, node.getLon(), 1e-10);
    }

    @Test
    public void createDataElement_insertPolyElement_rightElementSaved() {
        final double lat = 1.2;
        final double lon = 3.4;
        final Node polyNode = new Node(1, lat, lon);
        final PolyElementType type = PolyElementType.WAY;

        PolyElement toInsert = new PolyElement(0, type);
        toInsert.addNode(polyNode);
        dbHandler.createDataElement(toInsert);

        AbstractDataElement element = dbHandler.getAllDataElements().get(0);
        assertEquals(PolyElement.class, element.getClass());

        PolyElement poly = (PolyElement) element;
        assertEquals(type, poly.getType());
        assertEquals(1, poly.getNodes().size());

        Node node = poly.getNodes().get(0);
        assertEquals(lat, node.getLat(), 1e-10);
        assertEquals(lon, node.getLon(), 1e-10);
    }

    @Test
    public void createDataElement_insertElementWithTags_rightTagsSaved() {
        final Node toInsert = new Node(1, 1.2, 3.4);
        toInsert.addOrUpdateTag(Tags.getTagWithId(19), "FOO");
        toInsert.addOrUpdateTag(Tags.getTagWithId(20), "BAR");
        dbHandler.createDataElement(toInsert);

        AbstractDataElement element = dbHandler.getAllDataElements().get(0);
        assertEquals(toInsert.getTags().size(), element.getTags().size());
        for (Entry<Tag, String> entry : toInsert.getTags().entrySet()) {
            assertTrue(element.getTags().containsKey(entry.getKey()));
            assertEquals(entry.getValue(),
                    element.getTagValueWithKey(entry.getKey()));
        }
    }

    @Test
    public void updateDataElement_updateNode_rightElementSaved() {
        final double lat = 1.2;
        final double lon = 3.4;
        Node toUpdate = new Node(1, 12, 23);
        dbHandler.createDataElement(toUpdate);
        toUpdate = new Node(toUpdate.getOsmId(), lat, lon);
        dbHandler.updateDataElement(toUpdate);

        AbstractDataElement element = dbHandler.getAllDataElements().get(0);
        assertEquals(Node.class, element.getClass());

        Node node = (Node) element;
        assertEquals(lat, node.getLat(), 1e-10);
        assertEquals(lon, node.getLon(), 1e-10);
    }

    @Test
    public void updateDataElement_updatePolyElement_rightElementSaved() {
        final double lat = 1.2;
        final double lon = 3.4;
        final Node polyNode = new Node(1, 12, 23);
        final PolyElementType type = PolyElementType.WAY;

        PolyElement toUpdate = new PolyElement(0, PolyElementType.AREA);
        toUpdate.addNode(polyNode);
        toUpdate.addNode(polyNode);
        dbHandler.createDataElement(toUpdate);

        toUpdate.setType(type);
        toUpdate.replaceNodes(Arrays.asList(new Node(1, lat, lon)));
        dbHandler.updateDataElement(toUpdate);

        AbstractDataElement element = dbHandler.getAllDataElements().get(0);
        assertEquals(PolyElement.class, element.getClass());

        PolyElement poly = (PolyElement) element;
        assertEquals(type, poly.getType());
        assertEquals(1, poly.getNodes().size());

        Node node = poly.getNodes().get(0);
        assertEquals(lat, node.getLat(), 1e-10);
        assertEquals(lon, node.getLon(), 1e-10);
    }

    @Test
    public void updateDataElement_updateElementWithTags_rightTagsSaved() {
        final Node toUpdate = new Node(1, 1.2, 3.4);
        toUpdate.addOrUpdateTag(Tags.getTagWithId(19), "FOO");
        toUpdate.addOrUpdateTag(Tags.getTagWithId(20), "BAR");
        dbHandler.createDataElement(toUpdate);

        toUpdate.clearTags();
        toUpdate.addOrUpdateTag(Tags.getTagWithId(97), "FOO2");
        toUpdate.addOrUpdateTag(Tags.getTagWithId(98), "BAR2");
        dbHandler.updateDataElement(toUpdate);

        AbstractDataElement element = dbHandler.getAllDataElements().get(0);
        assertEquals(toUpdate.getTags().size(), element.getTags().size());
        for (Entry<Tag, String> entry : toUpdate.getTags().entrySet()) {
            assertTrue(element.getTags().containsKey(entry.getKey()));
            assertEquals(entry.getValue(),
                    element.getTagValueWithKey(entry.getKey()));
        }
    }

    @Test
    public void deleteAllDataElements_empty_emptyAfterwards() {
        dbHandler.deleteAllDataElements();
        assertEquals(0, dbHandler.getDataElementCount());
    }

    @Test
    public void deleteAllDataElements_notEmpty_emptyAfterwards() {
        final Node node = new Node(0, 0, 0);
        dbHandler.createDataElement(node);
        dbHandler.createDataElement(node);
        dbHandler.createDataElement(node);

        dbHandler.deleteAllDataElements();
        assertEquals(0, dbHandler.getDataElementCount());
    }

    // ///////////////////////////////////////////////
    // ///////////////////////////////////////////////

    @Test
    public void getGPSTrackCount_empty_zeroSize() {
        assertEquals(0, dbHandler.getGPSTrackCount());
    }

    @Test
    public void getGPSTrackCount_oneEmptyTrack_oneSize() {
        dbHandler.createGPSTrack(new Track());
        assertEquals(1, dbHandler.getGPSTrackCount());
    }

    @Test
    public void getGPSTrackCount_oneNotEmptyTrack_oneSize() {
        final Track track = new Track();
        track.setTrackPoints(Arrays.asList(new TrackPoint(0, 0, 0, 0),
                new TrackPoint(0, 0, 0, 1)));
        dbHandler.createGPSTrack(track);
        assertEquals(1, dbHandler.getGPSTrackCount());
    }

    @Test
    public void createGPSTrack_insertTrack_rightTrackSaved() {
        final Track toInsert = new Track();
        toInsert.setTrackName("FOO");
        toInsert.setDescription("BAR");
        toInsert.setTags("FOO, BAR, 42");
        toInsert.finishTrack();

        dbHandler.createGPSTrack(toInsert);

        final Track track = dbHandler.getGPSTrack(toInsert.getID());

        assertEquals(toInsert.getID(), track.getID());
        assertEquals(toInsert.getTrackName(), track.getTrackName());
        assertEquals(toInsert.getDescription(), track.getDescription());
        assertEquals(toInsert.getTags(), track.getTags());
        assertEquals(toInsert.isFinished(), track.isFinished());
    }

    @Test
    public void createGPSTrack_insertTrack_rightTrackPointsSaved() {
        final Track toInsert = new Track();
        toInsert.setTrackPoints(Arrays.asList(new TrackPoint(0, 0, 0, 0),
                new TrackPoint(0, 0, 0, 1)));
        
        dbHandler.createGPSTrack(toInsert);

        final Track track = dbHandler.getGPSTrack(toInsert.getID());

        final List<TrackPoint> insertedPoints = toInsert.getTrackPoints();
        final List<TrackPoint> readPoints = track.getTrackPoints();
        assertEquals(insertedPoints.size(), readPoints.size());
        
        for (int i = 0; i < insertedPoints.size(); i++) {
            final TrackPoint insertedPoint = insertedPoints.get(i);
            final TrackPoint readPoint = readPoints.get(i);

            assertEquals(insertedPoint.getID(), readPoint.getID());
            assertEquals(insertedPoint.getLat(), readPoint.getLat(), 1e-10);
            assertEquals(insertedPoint.getLon(), readPoint.getLon(), 1e-10);
            assertEquals(insertedPoint.getAlt(), readPoint.getAlt(), 1e-10);
            assertEquals(insertedPoint.getTime(), readPoint.getTime());
        }
    }

    @Test
    @Ignore
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
        dbHandler.deleteAllDataElements();
        dbHandler.deleteAllGPSTracks();
        dbHandler.close();
    }
}
