package io.github.data4all.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.github.data4all.model.DataBaseHandler;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.Relation;
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
		
		User user1 = new User("Liadan", "abcde12345", true);
		User user2 = new User("Manus", "fgh81375", false);
		
		dbHandler.createUser(user1);
		dbHandler.createUser(user2);
		
		assertEquals("abcde12345", dbHandler.getUser("Liadan").getLoginToken());
		assertEquals("fgh81375", dbHandler.getUser("Manus").getLoginToken());		
	
		user1.setLoginToken("xyz97");
		user2.setLoginToken("ads8790");
		
		dbHandler.updateUser(user1);
		dbHandler.updateUser(user2);
		
		assertEquals("xyz97", dbHandler.getUser("Liadan").getLoginToken());
		assertEquals("ads8790", dbHandler.getUser("Manus").getLoginToken());
		
		dbHandler.deleteUser(user1);
		dbHandler.deleteUser(user2);
		
//		assertNull(dbHandler.getUser("Liadan"));
//		assertNull(dbHandler.getUser("Manus"));
		
		assertEquals(0, dbHandler.getUserCount());
	}
	
	@Test
	public void testNodeCRUD(){
		
		Node node1 = new Node(1, 2, 30.123456, 40.1234567);
		Node node2 = new Node(2, 3, 25.982423, 42.7483024);
		
		Relation relation1 = new Relation(2, 1);
		node1.addParentRelation(relation1);
		
//		dbHandler.createNode(node1);
//		dbHandler.createNode(node2);
		
//		assertEquals(30.123456, dbHandler.getNode(1).getLat(), 0.0);
//		assertEquals(25.982423, dbHandler.getNode(2).getLat(), 0.0);
	}
}
