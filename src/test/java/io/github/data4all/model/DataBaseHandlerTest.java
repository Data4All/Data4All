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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.model.data.User;
import io.github.data4all.model.data.PolyElement.PolyElementType;

/**
 * This class tests all methods of the DataBaseHandler.
 * 
 * @author Kristin Dahnken
 * @author fkirchge 
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

        User user1 = new User("Liadan", "abcde12345", "xyz");
        User user2 = new User("Manus", "fgh81375", "abc");

        dbHandler.createUser(user1);
        dbHandler.createUser(user2);

        assertEquals("abcde12345", dbHandler.getUser("Liadan").getOAuthToken());
        assertEquals("abc", dbHandler.getUser("Manus").getOauthTokenSecret());

        user1.setOAuthToken("xyz97");
        user2.setOauthTokenSecret("ads8790");

        dbHandler.updateUser(user1);
        dbHandler.updateUser(user2);

        assertEquals("xyz97", dbHandler.getUser("Liadan").getOAuthToken());
        assertEquals("ads8790", dbHandler.getUser("Manus")
                .getOauthTokenSecret());

        dbHandler.deleteUser(user1);

        assertEquals(1, dbHandler.getUserCount());

        dbHandler.createUser(user1);

        assertEquals(2, dbHandler.getUserCount());

        dbHandler.deleteAllUser();

        assertEquals(0, dbHandler.getUserCount());

    }

    @Test
    public void testNodeCRUD() {

        Node node1 = new Node(1, 30.123456, 40.1234567);
        Node node2 = new Node(2, 25.982423, 42.7483024);
        Node node3 = new Node(3, 23.325786, 41.0457094);

        dbHandler.createDataElement(node1);
        dbHandler.createDataElement(node2);
        dbHandler.createDataElement(node3);

        Node rNode1 = (Node) dbHandler.getDataElement(1);
        Node rNode2 = (Node) dbHandler.getDataElement(2);
        Node rNode3 = (Node) dbHandler.getDataElement(3);

        assertEquals(30.123456, rNode1.getLat(), 0.0);
        assertEquals(25.982423, rNode2.getLat(), 0.0);
        assertEquals(41.0457094, rNode3.getLon(), 0.0);

        node1.setLat(31.123456);
        node2.setLat(26.986764);
        node3.setLon(42.869686);

        dbHandler.updateNode(node1);
        dbHandler.updateNode(node2);
        dbHandler.updateNode(node3);

        rNode1 = (Node) dbHandler.getDataElement(1);
        rNode2 = (Node) dbHandler.getDataElement(2);
        rNode3 = (Node) dbHandler.getDataElement(3);

        assertEquals(31.123456, rNode1.getLat(), 0.0);
        assertEquals(26.986764, rNode2.getLat(), 0.0);
        assertEquals(42.869686, rNode3.getLon(), 0.0);

        dbHandler.deleteNode(node1);

        assertEquals(2, dbHandler.getNodeCount());

        dbHandler.createDataElement(node1);

        assertEquals(3, dbHandler.getNodeCount());

        dbHandler.deleteAllNode();

        assertEquals(0, dbHandler.getNodeCount());
    }

    /*@Test
    public void testPolyElementCRUD() throws JSONException {

        PolyElement polyElement1 = new PolyElement(11, PolyElementType.AREA);
        PolyElement polyElement2 = new PolyElement(12, PolyElementType.WAY);
        PolyElement polyElement3 =
                new PolyElement(13, PolyElementType.BUILDING);

        Node node1 = new Node(4, 30.123456, 40.1234567);
        Node node2 = new Node(5, 25.982423, 42.7483024);
        Node node3 = new Node(6, 23.325786, 41.0457094);
        Node node4 = new Node(7, 34.744587, 19.0937468);
        Node node5 = new Node(8, 28.935876, 45.6767676);
        Node node6 = new Node(9, 33.333333, 44.4444444);

        polyElement1.addNode(node1);
        polyElement1.addNode(node2);
        assertEquals(2, polyElement1.getNodes().size());

        polyElement2.addNode(node3);
        polyElement2.addNode(node4);
        assertEquals(2, polyElement2.getNodes().size());

        polyElement3.addNode(node5);
        polyElement3.addNode(node6);
        assertEquals(2, polyElement3.getNodes().size());

        dbHandler.createDataElement(polyElement1);
        dbHandler.createDataElement(polyElement2);
        dbHandler.createDataElement(polyElement3);

        assertEquals(3, dbHandler.getPolyElementCount());
        assertEquals(6, dbHandler.getAllNode().size());

        polyElement1.setType(PolyElementType.BUILDING);
        polyElement2.setType(PolyElementType.WAY);
        polyElement3.setType(PolyElementType.AREA);

        dbHandler.updatePolyElement(polyElement1);
        dbHandler.updatePolyElement(polyElement2);
        dbHandler.updatePolyElement(polyElement3);

        assertEquals(
                polyElement1.getType(),
                ((PolyElement) dbHandler.getDataElement(polyElement1.getOsmId()))
                        .getType());
        assertEquals(
                polyElement2.getType(),
                ((PolyElement) dbHandler.getDataElement(polyElement2.getOsmId()))
                        .getType());
        assertEquals(
                polyElement3.getType(),
                ((PolyElement) dbHandler.getDataElement(polyElement3.getOsmId()))
                        .getType());

        dbHandler.deleteDataElement(polyElement1);

        assertEquals(2, dbHandler.getPolyElementCount());
        assertEquals(4, dbHandler.getNodeCount());

        dbHandler.createDataElement(polyElement1);

        assertEquals(3, dbHandler.getPolyElementCount());
        assertEquals(6, dbHandler.getNodeCount());

        dbHandler.deleteAllDataElements();

        assertEquals(0, dbHandler.getAllDataElements().size());
    }
*/
    /*@Test
    public void testDataElementCRUD() throws JSONException {
        PolyElement polyElement1 = new PolyElement(1, PolyElementType.BUILDING);
        Node node1 = new Node(2, 30.123456, 40.1234567);

        Map<Tag, String> tagMap = new Hashtable<Tag, String>();
        Tag tag1 = Tags.getTagWithId(1);
        tagMap.put(tag1, "Hollywood Blvd.");
        Tag tag2 = Tags.getTagWithId(2);
        tagMap.put(tag2, "113");

        polyElement1.addTags(tagMap);

        dbHandler.createDataElement(polyElement1);
        dbHandler.createDataElement(node1);

        assertEquals(2, dbHandler.getDataElementCount());
        assertEquals(1, dbHandler.getPolyElementCount());
        assertEquals(1, dbHandler.getNodeCount());

        assertEquals(2, dbHandler.getDataElement(1).getTags().size());
        assertEquals(0, dbHandler.getDataElement(2).getTags().size());

        PolyElement returnedPE = (PolyElement) dbHandler.getDataElement(1);
        assertEquals(PolyElementType.BUILDING, returnedPE.getType());

        Node returnedN = (Node) dbHandler.getDataElement(2);
        assertEquals(30.123456, returnedN.getLat(), 0.0);

        polyElement1.setType(PolyElementType.AREA);
        node1.setLon(42.1234567);

        dbHandler.updateDataElement(polyElement1);
        dbHandler.updateDataElement(node1);

        returnedPE =
                (PolyElement) dbHandler.getDataElement(polyElement1.getOsmId());
        returnedN = (Node) dbHandler.getDataElement(node1.getOsmId());

        assertEquals(PolyElementType.AREA, returnedPE.getType());
        assertEquals(42.1234567, returnedN.getLon(), 0.0);

        dbHandler.deleteDataElement(node1);

        assertEquals(1, dbHandler.getDataElementCount());
        assertEquals(1, dbHandler.getPolyElementCount());
        assertEquals(0, dbHandler.getNodeCount());

        dbHandler.createDataElement(node1);

        assertEquals(2, dbHandler.getDataElementCount());
        assertEquals(1, dbHandler.getPolyElementCount());
        assertEquals(1, dbHandler.getNodeCount());

        dbHandler.deleteAllDataElements();

        assertEquals(0, dbHandler.getDataElementCount());
    }*/

    /*@Test
    public void testTagMapCRUD() {
        Node node = new Node(1, 30.123456, 40.1234567);

        Map<Tag, String> tagMap = new LinkedHashMap<Tag, String>();
        Tag tag1 = Tags.getTagWithId(1);
        tagMap.put(tag1, "Hollywood");
        Tag tag2 = Tags.getTagWithId(2);
        tagMap.put(tag2, "113");
        Tag tag3 = Tags.getTagWithId(3);
        tagMap.put(tag3, "04229");

        ArrayList<Integer> tagIDs = new ArrayList<Integer>();
        tagIDs.add(tag1.getId());
        tagIDs.add(tag2.getId());
        tagIDs.add(tag3.getId());

        node.addTags(tagMap);
        dbHandler.createDataElement(node);

        Node rNode = (Node) dbHandler.getDataElement(node.getOsmId());
        assertEquals(3, dbHandler.getTagMapCount(node.getOsmId()));

        Tag tag4 = Tags.getTagWithId(4);
        tagMap.put(tag4, "Los Angeles");
        tagIDs.add(tag4.getId());

        node.setTags(tagMap);
        dbHandler.updateDataElement(node);
        assertEquals(4, dbHandler.getTagMapCount(node.getOsmId()));
        rNode = (Node) dbHandler.getDataElement(node.getOsmId());

        assertTrue(rNode.getTags().containsKey(tag1));
        assertTrue(rNode.getTags().containsValue("Hollywood"));
        assertTrue(rNode.getTags().containsKey(tag2));
        assertTrue(rNode.getTags().containsValue("113"));
        assertTrue(rNode.getTags().containsKey(tag3));
        assertTrue(rNode.getTags().containsValue("Los Angeles"));

        node.getTags().clear();

        dbHandler.updateDataElement(node);

        assertEquals(0, dbHandler.getTagMapCount(node.getOsmId()));

        node.setTags(tagMap);

        dbHandler.updateDataElement(node);

        assertEquals(4, dbHandler.getTagMapCount(node.getOsmId()));

        dbHandler.deleteAllDataElements();

        assertEquals(0, dbHandler.getTagMapCount(node.getOsmId()));
    }
*/
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

        dbHandler.updateGPSTrack(track);

        reTrack = dbHandler.getGPSTrack(track.getID());
        
        assertEquals(track.getTrackName(), reTrack.getTrackName());

    }

    @After
    public void tearDown() {
        dbHandler.close();
    }
}
