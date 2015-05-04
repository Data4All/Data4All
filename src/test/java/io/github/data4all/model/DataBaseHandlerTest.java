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
import io.github.data4all.model.data.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    public void getUserSize_empty_zeroSize() {
        assertEquals(0, dbHandler.getAllUser().size());
    }

    @Test
    public void getUserSize_oneUser_oneSize() {
        dbHandler.createUser(new User("foo", "bar", "42"));
        assertEquals(1, dbHandler.getAllUser().size());
    }

    @Test
    public void createUser_insertUser_rightUserSaved() {
        final User toInsert = new User("foo", "bar", "42");
        dbHandler.createUser(toInsert);

        User user = dbHandler.getAllUser().get(0);

        assertEquals(toInsert.getUsername(), user.getUsername());
        assertEquals(toInsert.getOAuthToken(), user.getOAuthToken());
        assertEquals(toInsert.getOauthTokenSecret(), user.getOauthTokenSecret());
    }

    @Test
    public void deleteUser_userWasSaved_userIsDeleted() {
        final User toDelete = new User("foo", "bar", "42");
        dbHandler.createUser(toDelete);
        dbHandler.deleteUser(toDelete);

        assertEquals(0, dbHandler.getAllUser().size());
    }

    // ///////////////////////////////////////////////
    // ///////////////////////////////////////////////

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
    public void updateGPSTrack_updateTrack_rightTrackSaved() {
        final Track toUpdate = new Track();
        toUpdate.setTrackName("oof");
        toUpdate.setDescription("rab");
        toUpdate.setTags("24 ,rab ,oof");

        dbHandler.createGPSTrack(toUpdate);

        toUpdate.setTrackName("FOO");
        toUpdate.setDescription("BAR");
        toUpdate.setTags("FOO, BAR, 42");
        toUpdate.finishTrack();
        dbHandler.updateGPSTrack(toUpdate);

        final Track track = dbHandler.getGPSTrack(toUpdate.getID());

        assertEquals(toUpdate.getID(), track.getID());
        assertEquals(toUpdate.getTrackName(), track.getTrackName());
        assertEquals(toUpdate.getDescription(), track.getDescription());
        assertEquals(toUpdate.getTags(), track.getTags());
        assertEquals(toUpdate.isFinished(), track.isFinished());
    }

    @Test
    public void updateGPSTrack_updateTrack_rightTrackPointsSaved() {
        final Track toUpdate = new Track();
        List<TrackPoint> points = new ArrayList<TrackPoint>();
        points.add(new TrackPoint(0, 0, 0, 0));
        points.add(new TrackPoint(0, 0, 0, 1));
        toUpdate.setTrackPoints(points);

        dbHandler.createGPSTrack(toUpdate);

        points.add(new TrackPoint(1, 1, 1, 2));
        points.add(new TrackPoint(2, 2, 2, 3));
        toUpdate.setTrackPoints(points);

        dbHandler.updateGPSTrack(toUpdate);

        final Track track = dbHandler.getGPSTrack(toUpdate.getID());

        final List<TrackPoint> insertedPoints = toUpdate.getTrackPoints();
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
    public void deleteAllGPSTracks_empty_emptyAfterwards() {
        dbHandler.deleteAllGPSTracks();
        assertEquals(0, dbHandler.getGPSTrackCount());
    }

    @Test
    public void deleteAllGPSTracks_notEmpty_emptyAfterwards() {
        final Track track = new Track();
        dbHandler.createGPSTrack(track);
        dbHandler.createGPSTrack(track);
        dbHandler.createGPSTrack(track);

        dbHandler.deleteAllGPSTracks();
        assertEquals(0, dbHandler.getGPSTrackCount());
    }

    @After
    public void tearDown() {
        dbHandler.deleteAllDataElements();
        dbHandler.deleteAllGPSTracks();
        for (User user : dbHandler.getAllUser()) {
            dbHandler.deleteUser(user);
        }
        dbHandler.close();
    }
}
