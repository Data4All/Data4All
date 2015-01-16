package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Testing the main functionality of the relation and relation member objects.
 * Inherited methods from OsmElement already tested in NodeTest.java.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RelationTest {

    private Relation relation;
    private Node node;

    /**
     * Executed for each test case.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        relation = new Relation(1, 1);
        node = new Node(1, 1, 10.1234567, 20.1234567);
    }

    /**
     * Executed after each test case.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        relation = null;
        node = null;
    }

    /**
     * Adds a new relation member to the relation.
     */
    @Test
    public void test_addMember() {
        RelationMember relationMember = new RelationMember("node", 1, "");
        relation.addMember(relationMember);
        assertEquals(1, relation.getMembers().size());
        assertEquals(true, relation.getMembers().contains(relationMember));
    }

    /**
     * Tests if getMember(OsmElement e) returns null, if the relationMember has
     * no reference to an element.
     */
    @Test
    public void test_null_getMember() {
        RelationMember relationMember = new RelationMember("node", 1, "");
        relation.addMember(relationMember);
        assertEquals(1, relation.getMembers().size());
        assertEquals(true, relation.getMembers().contains(relationMember));
        assertEquals(null, relation.getMember(node));
    }

    /**
     * Tests if getMember(OsmElement e) return the correct relationMember
     * containing the osm element.
     */
    @Test
    public void test_relationMember_getMember1() {
        RelationMember relationMember = new RelationMember("node", 1, "");
        relationMember.setElement(node);
        relation.addMember(relationMember);
        assertEquals(1, relation.getMembers().size());
        assertEquals(true, relation.getMembers().contains(relationMember));
        assertEquals(relationMember, relation.getMember(node));
    }

    /**
     * Test if getMember(String type, long id) returns the correct relation
     * member.
     */
    @Test
    public void test_type_id_getMember() {
        RelationMember relationMemberWay = new RelationMember("way", 1, "");
        RelationMember relationMemberNode = new RelationMember("node", 2, "");
        relation.addMember(relationMemberWay);
        relation.addMember(relationMemberNode);
        assertEquals(relationMemberWay, relation.getMember("way", 1));
    }

    /**
     * Test if getPosition() returns the correct position of the relation
     * member.
     */
    @Test
    public void test_getPosition() {
        RelationMember relationMemberWay = new RelationMember("way", 1, "");
        RelationMember relationMemberNode = new RelationMember("node", 2, "");
        RelationMember relationMemberRelation = new RelationMember("relation",
                3, "");
        relation.addMember(relationMemberWay);
        relation.addMember(relationMemberNode);
        relation.addMember(relationMemberRelation);
        assertEquals(1, relation.getPosition(relationMemberNode));
    }

    /**
     * Test if getRemovableMembers() returns an Iterator element.
     */
    @Test
    public void test_getRemovableMembers() {
        RelationMember relationMember = new RelationMember("way", 1, "");
        relation.addMember(relationMember);
        assertEquals(true, relation.getRemovableMembers().hasNext());
        assertEquals(relationMember, relation.getRemovableMembers().next());
    }

    /**
     * Test if hasMember() returns true if the relation contains the relation
     * member.
     */
    @Test
    public void test_hasMember() {
        RelationMember relationMember = new RelationMember("way", 1, "");
        relation.addMember(relationMember);
        assertEquals(true, relation.hasMember(relationMember));
    }

    /**
     * Test if removeMember() removes the relation member from the relation.
     */
    @Test
    public void test_removeMember() {
        RelationMember relationMember = new RelationMember("way", 1, "");
        relation.addMember(relationMember);
        assertEquals(true, relation.hasMember(relationMember));
        assertEquals(true, relation.getMembers().contains(relationMember));
        relation.removeMember(relationMember);
        assertEquals(false, relation.hasMember(relationMember));
        assertEquals(false, relation.getMembers().contains(relationMember));
    }

    /**
     * Test if appendMember() can append a new relation member at the begin or
     * the end of the list.
     */
    @Test
    public void test_appendMember() {
        RelationMember relationMemberWay = new RelationMember("way", 1, "");
        RelationMember relationMemberNode = new RelationMember("node", 2, "");
        RelationMember relationMemberRelation = new RelationMember("relation",
                3, "");
        relation.addMember(relationMemberWay);
        relation.addMember(relationMemberNode);
        relation.addMember(relationMemberRelation);
        assertEquals(3, relation.getMembers().size());
        RelationMember newMember1 = new RelationMember("relation", 4, "");
        relation.appendMember(relationMemberWay, newMember1);
        assertEquals(4, relation.getMembers().size());
        assertEquals(0, relation.getPosition(newMember1));
        RelationMember newMember2 = new RelationMember("relation", 5, "");
        relation.appendMember(relationMemberRelation, newMember2);
        assertEquals(5, relation.getMembers().size());
        assertEquals(4, relation.getPosition(newMember2));
        RelationMember newMember3 = new RelationMember("relation", 6, "");
        relation.appendMember(relationMemberNode, newMember3);
        assertEquals(5, relation.getMembers().size());
    }

    /**
     * Test if addMemberAfter() adds a new relation member behind a reference
     * member.
     */
    @Test
    public void test_addMemberAfter() {
        RelationMember relationMemberWay = new RelationMember("way", 1, "");
        RelationMember relationMemberNode = new RelationMember("node", 2, "");
        RelationMember relationMemberRelation = new RelationMember("relation",
                3, "");
        relation.addMember(relationMemberWay);
        relation.addMember(relationMemberNode);
        relation.addMember(relationMemberRelation);
        assertEquals(3, relation.getMembers().size());
        RelationMember newMember = new RelationMember("relation", 4, "");
        relation.addMemberAfter(relationMemberNode, newMember);
        assertEquals(4, relation.getMembers().size());
    }

    /**
     * Adds a new relation member to a given position. Test if all relation
     * member have the correct position.
     */
    @Test
    public void test_pos_relationMember_addMember() {
        RelationMember relationMemberWay = new RelationMember("way", 1, "");
        RelationMember relationMemberNode = new RelationMember("node", 2, "");
        RelationMember relationMemberRelation = new RelationMember("relation",
                3, "");
        relation.addMember(relationMemberWay);
        relation.addMember(relationMemberNode);
        relation.addMember(relationMemberRelation);
        RelationMember newMember = new RelationMember("node", 4, "");
        relation.addMember(1, newMember);
        assertEquals(4, relation.getMembers().size());
        assertEquals(0, relation.getPosition(relationMemberWay));
        assertEquals(1, relation.getPosition(newMember));
        assertEquals(2, relation.getPosition(relationMemberNode));
        assertEquals(3, relation.getPosition(relationMemberRelation));
    }

    /**
     * Tests the addMembers() method. Adds a list of relation member to the
     * relation at the beginning. Checks the correct position of the members.
     */
    @Test
    public void test_addMembers() {
        RelationMember relationMember1 = new RelationMember("way", 4, "");
        RelationMember relationMember2 = new RelationMember("node", 5, "");
        RelationMember relationMember3 = new RelationMember("relation", 6, "");
        relation.addMember(relationMember1);
        relation.addMember(relationMember2);
        relation.addMember(relationMember3);
        assertEquals(0, relation.getPosition(relationMember1));
        assertEquals(1, relation.getPosition(relationMember2));
        assertEquals(2, relation.getPosition(relationMember3));
        RelationMember relationMemberWay = new RelationMember("way", 1, "");
        RelationMember relationMemberNode = new RelationMember("node", 2, "");
        RelationMember relationMemberRelation = new RelationMember("relation",
                3, "");
        List<RelationMember> relationMemberList = new ArrayList<RelationMember>();
        relationMemberList.add(relationMemberWay);
        relationMemberList.add(relationMemberNode);
        relationMemberList.add(relationMemberRelation);
        relation.addMembers(relationMemberList, true);
        assertEquals(3, relation.getPosition(relationMember1));
        assertEquals(4, relation.getPosition(relationMember2));
        assertEquals(5, relation.getPosition(relationMember3));
        assertEquals(0, relation.getPosition(relationMemberWay));
        assertEquals(1, relation.getPosition(relationMemberNode));
        assertEquals(2, relation.getPosition(relationMemberRelation));
    }

    /**
     * Tests the getMemberWithRole() method. Check if the result list contains
     * the correct relation members.
     */
    @Test
    public void test_getMembersWithRole() {
        RelationMember relationMember1 = new RelationMember("way", 4, "test");
        RelationMember relationMember2 = new RelationMember("node", 5, "none");
        RelationMember relationMember3 = new RelationMember("relation", 6,
                "test");
        relation.addMember(relationMember1);
        relation.addMember(relationMember2);
        relation.addMember(relationMember3);
        List<RelationMember> testRoles = relation.getMembersWithRole("test");
        assertEquals(2, testRoles.size());
        assertEquals(true, testRoles.contains(relationMember1));
        assertEquals(true, testRoles.contains(relationMember3));
        relation.getMembersWithRole("none");
        List<RelationMember> noneRoles = relation.getMembersWithRole("none");
        assertEquals(1, noneRoles.size());
        assertEquals(true, noneRoles.contains(relationMember2));
    }

    /**
     * Replaces a member in the list. Tests if the new member is in the list and
     * at the correct position.
     */
    @Test
    public void test_replaceMember() {
        RelationMember relationMember1 = new RelationMember("way", 4, "test");
        RelationMember relationMember2 = new RelationMember("node", 5, "none");
        RelationMember relationMember3 = new RelationMember("relation", 6,
                "test");
        relation.addMember(relationMember1);
        relation.addMember(relationMember2);
        relation.addMember(relationMember3);
        RelationMember newMember = new RelationMember("way", 2, "new way");
        relation.replaceMember(relationMember1, newMember);
        assertEquals(0, relation.getPosition(newMember));
    }

    /**
     * Test if the result list contains all relation member with a reference to
     * an osm object.
     */
    @Test
    public void test_getMemberElements() {
        Node node1 = new Node(1, 1, 10.1234567, 20.1234567);
        Node node2 = new Node(2, 1, 10.1234567, 20.1234567);
        RelationMember relationMember1 = new RelationMember("way", 4, "test");
        RelationMember relationMember2 = new RelationMember("node", 5, "none");
        RelationMember relationMember3 = new RelationMember("node", 6, "none");
        relationMember1.setElement(node1);
        relationMember2.setElement(node2);
        relation.addMember(relationMember1);
        relation.addMember(relationMember2);
        relation.addMember(relationMember3);
        assertEquals(2, relation.getMemberElements().size());
        assertEquals(true, relation.getMemberElements().contains(node1));
        assertEquals(true, relation.getMemberElements().contains(node2));
    }
    
    /**
     * Create a new parcial to save/parcelable the testWay, 
     * afterwards a new node is created from the parcel and we check if it contains all attributes.
     */
    @Test 
    public void test_parcelable_way() {
    	Parcel newParcel = Parcel.obtain();
    	Relation testRelation = new Relation(1, 1);
    	testRelation.addOrUpdateTag("testtag", "test");
    	testRelation.addOrUpdateTag("foo", "bar");


    	//        Relation relation1 = new Relation(3, 1);
    	//        testWay.addParentRelation(relation1);
    	//        Relation relation2 = new Relation(4, 2);
    	//        testWay.addParentRelation(relation2);
    	//        testWay.addNode(testNode1);
    	//        testWay.addNode(testNode2);
    	//        testWay.addNode(testNode3);
    	testRelation.writeToParcel(newParcel, 0);
    	newParcel.setDataPosition(0);
    	Relation deParcelRelation = Relation.CREATOR.createFromParcel(newParcel);
    	//    	assertEquals(1, deParcelWay.getOsmId()); 
    	//    	assertEquals(2, deParcelWay.getOsmVersion());
    	assertEquals("test", deParcelRelation.getTagWithKey("testtag"));
    	assertEquals("bar", deParcelRelation.getTagWithKey("foo"));
    	//    	assertEquals(2, deParcelWay.getParentRelations().size());
    	//    	assertEquals(3, deParcelWay.getParentRelations().get(0).getOsmId());
    	//    	assertEquals(1, deParcelWay.getParentRelations().get(0).getOsmVersion());
    	//    	assertEquals(4, deParcelWay.getParentRelations().get(1).getOsmId());
    	//    	assertEquals(2, deParcelWay.getParentRelations().get(1).getOsmVersion());
    	//    	assertEquals(3, deParcelWay.getNodes().size());
    	//        assertEquals(10, deParcelWay.getNodes().get(0).getOsmId());
    	//        assertEquals(11, deParcelWay.getNodes().get(1).getOsmId());
    	//        assertEquals(12, deParcelWay.getNodes().get(2).getOsmId());
    	//        assertEquals(1, deParcelWay.getNodes().get(0).getOsmVersion());
    	//        assertEquals(1, deParcelWay.getNodes().get(1).getOsmVersion());
    	//        assertEquals(1, deParcelWay.getNodes().get(2).getOsmVersion());	
    }  

}