package io.github.data4all.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.github.data4all.model.DataBaseHandler;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.User;
/**
 * This class tests the DataBaseHandler.
 * 
 * @author Kristin Dahnken
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DataBaseHandlerTest {
	
	private DataBaseHandler dbHandler;
			
	@Before
	public void setUp(){
		dbHandler = new DataBaseHandler(Robolectric.application);
		System.out.println("Setup done.");
	}
	
	@Test
	public void testUserCRUD(){		
		
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
		assertEquals("ads8790", dbHandler.getUser("Manus").getOauthTokenSecret());
		
		dbHandler.deleteUser(user1);
		dbHandler.deleteUser(user2);
		
		assertEquals(0, dbHandler.getUserCount());
	}
	
	@Test
	public void testNodeCRUD(){
		
		Node node1 = new Node(1, 30.123456, 40.1234567);
		Node node2 = new Node(2, 25.982423, 42.7483024);
		Node node3 = new Node(3, 23.325786, 41.0457094);
		
		dbHandler.createNode(node1);
		dbHandler.createNode(node2);
		dbHandler.createNode(node3);
		
		assertEquals(30.123456, dbHandler.getNode(1).getLat(), 0.0);
		assertEquals(25.982423, dbHandler.getNode(2).getLat(), 0.0);
		assertEquals(41.0457094, dbHandler.getNode(3).getLon(), 0.0);
		
		node1.setLat(31.123456);
		node2.setLat(26.986764);
		node3.setLon(42.869686);
		
		dbHandler.updateNode(node1);
		dbHandler.updateNode(node2);
		dbHandler.updateNode(node3);
		
		assertEquals(31.123456, dbHandler.getNode(1).getLat(), 0.0);
		assertEquals(26.986764, dbHandler.getNode(2).getLat(), 0.0);
		assertEquals(42.869686, dbHandler.getNode(3).getLon(), 0.0);
		
		dbHandler.deleteNode(node1);
		dbHandler.deleteNode(node2);
		dbHandler.deleteNode(node3);
		
		assertEquals(0, dbHandler.getNodeCount());
	}
	
//	@Test
//	public void testWayCRUD(){
//		
//		Node node1 = new Node(1, 1, 30.123456, 40.1234567);
//		Node node2 = new Node(2, 1, 25.982423, 42.7483024);
//		Node node3 = new Node(3, 1, 34.096897, 42.6598236);
//		Node node4 = new Node(4, 1, 27.082759, 40.7533486);
//		
//		RelationMember relationMember1 = new RelationMember("node", 5, "");
//		RelationMember relationMember2 = new RelationMember("node", 6, "");
//		RelationMember relationMember3 = new RelationMember("node", 7, "");
//		RelationMember relationMember4 = new RelationMember("node", 8, "");
//		
//		Relation relation1 = new Relation(9, 1);
//		relation1.addMember(relationMember1);
//		node1.addParentRelation(relation1);
//		
//		Relation relation2 = new Relation(10, 1);
//		relation2.addMember(relationMember2);
//		node2.addParentRelation(relation2);
//		
//		Relation relation3 = new Relation(11, 1);
//		relation3.addMember(relationMember3);
//		node3.addParentRelation(relation3);
//		
//		Relation relation4 = new Relation(12, 1);
//		relation4.addMember(relationMember4);
//		node4.addParentRelation(relation4);
//		
//		relation1.addParentRelation(relation2);
//		relation2.addParentRelation(relation3);
//		relation3.addParentRelation(relation4);
//		
//		Way way1 = new Way(13, 1);
//		way1.addNode(node1);
//		way1.addNode(node2);
//		way1.addParentRelation(relation1);
//		way1.addParentRelation(relation2);
//		
//		Way way2 = new Way(14, 1);
//		way2.addNode(node3);
//		way2.addNode(node4);
//		way2.addParentRelation(relation3);
//		way2.addParentRelation(relation4);
//		
//		dbHandler.createRelationMember(relationMember1);
//		dbHandler.createRelationMember(relationMember2);
//		dbHandler.createRelationMember(relationMember3);
//		dbHandler.createRelationMember(relationMember4);
//		
//		dbHandler.createRelation(relation1);
//		dbHandler.createRelation(relation2);
//		dbHandler.createRelation(relation3);
//		dbHandler.createRelation(relation4);
//		
//		dbHandler.createNode(node1);
//		dbHandler.createNode(node2);
//		dbHandler.createNode(node3);
//		dbHandler.createNode(node4);
//		
//		dbHandler.createWay(way1);
//		dbHandler.createWay(way2);
//		
//		assertEquals(30.123456, dbHandler.getWay(13).getFirstNode().getLat(), 0.0);
////		assertEquals(40.7533486, dbHandler.getWay(14).getLastNode().getLon(), 0.0);
//		
//		way1.setOsmVersion(2);
//		
////		dbHandler.updateWay(way1);
//		
////		assertEquals(2, dbHandler.getWay(13).getOsmVersion());
//	}
}
