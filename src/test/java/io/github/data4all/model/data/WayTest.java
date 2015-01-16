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
 * Tests the main functionality of way objects. Inherited methods from
 * OsmElement already tested in NodeTest.java.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class WayTest {

    private Way way;
    private Node testNode1;
    private Node testNode2;
    private Node testNode3;
    private List<Node> nodes;

    /**
     * Executed for each test case.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.way = new Way(1, 1);
        this.testNode1 = new Node(10, 1, 10.1234567, 20.1234567);
        this.testNode2 = new Node(11, 1, 10.1234567, 20.1234567);
        this.testNode3 = new Node(12, 1, 10.1234567, 20.1234567);
        this.nodes = new ArrayList<Node>();
        this.nodes.add(testNode1);
        this.nodes.add(testNode2);
        this.nodes.add(testNode3);
    }

    /**
     * Executed after each test case.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        this.way = null;
        this.testNode1 = null;
        this.testNode2 = null;
        this.testNode3 = null;
        this.nodes = null;
    }

    /**
     * Tests if a new node is added correctly to the list. Tests if it is not
     * possible to add more than 2000 nodes.
     */
    @Test
    public void test_addNode() {
        Node newNode = new Node(1, 1, 10.1234567, 20.1234567);
        way.addNode(newNode);
        assertEquals(1, way.getNodes().size());
        assertEquals(true, way.getNodes().contains(newNode));
        for (int i = 0; i <= 2100; i++) {
            Node node = new Node(1, 1, 10.1234567, 10.1234567);
            way.addNode(node);
        }
        assertEquals(2000, way.getNodes().size());
        way.addNode(newNode);
        way.addNode(newNode);
    }

    /**
     * Tests if a node is appended correctly at the begin or end of the way.
     */
    @Test
    public void test_appendNode() {
        Node newNode1 = new Node(1, 1, 10.1234567, 20.1234567);
        way.addNode(newNode1);
        Node newNode2 = new Node(2, 1, 10.1234567, 20.1234567);
        way.addNode(newNode2);
        Node newNode3 = new Node(3, 1, 10.1234567, 20.1234567);
        way.addNode(newNode3);
        assertEquals(3, way.getNodes().size());
        assertEquals(newNode1, way.getFirstNode());
        way.appendNode(newNode1, testNode1);
        assertEquals(testNode1, way.getFirstNode());
        assertEquals(4, way.getNodes().size());
        way.appendNode(newNode3, testNode2);
        assertEquals(testNode2, way.getLastNode());
        assertEquals(5, way.getNodes().size());
        way.appendNode(testNode3, newNode2);
        assertEquals(5, way.getNodes().size());
    }

    /**
     * Tests if a new node is inserted after a existing node.
     */
    @Test
    public void test_addNodeAfter() {
        Node newNode1 = new Node(1, 1, 10.1234567, 20.1234567);
        way.addNode(newNode1);
        Node newNode2 = new Node(2, 1, 10.1234567, 20.1234567);
        way.addNode(newNode2);
        Node newNode3 = new Node(3, 1, 10.1234567, 20.1234567);
        way.addNode(newNode3);
        way.addNodeAfter(newNode2, testNode1);
        assertEquals(4, way.getNodes().size());
        assertEquals(2, way.getNodes().indexOf(testNode1));
    }

    /**
     * Tests if a list of nodes is added at the begin of a existing list.
     */
    @Test
    public void test_addNodes() {
        way.addNodes(nodes, true);
        assertEquals(3, way.getNodes().size());
        assertEquals(testNode1, way.getFirstNode());
        Node newNode1 = new Node(1, 1, 10.1234567, 20.1234567);
        Node newNode2 = new Node(2, 1, 10.1234567, 20.1234567);
        Node newNode3 = new Node(3, 1, 10.1234567, 20.1234567);
        List<Node> nodes1 = new ArrayList<Node>();
        nodes1.add(newNode1);
        nodes1.add(newNode2);
        nodes1.add(newNode3);
        way.addNodes(nodes1, true);
        assertEquals(newNode1, way.getFirstNode());
        assertEquals(6, way.getNodes().size());
    }

    /**
     * Tests if the given node is part of the way.
     */
    @Test
    public void test_hasNode() {
        assertEquals(false, way.hasNode(testNode1));
        way.addNode(testNode1);
        assertEquals(true, way.hasNode(testNode1));
    }

    /**
     * Tests if existing ways has common node with given node.
     */
    @Test
    public void test_hasCommonNode() {
        way.addNode(testNode1);
        Way secondWay = new Way(1, 1);
        secondWay.addNode(testNode1);
        assertEquals(true, way.hasCommonNode(secondWay));
    }

    /**
     * Tests if the given node is removed from the list.
     */
    @Test
    public void test_removeNode() {
        way.addNode(testNode1);
        assertEquals(true, way.hasNode(testNode1));
        way.removeNode(testNode1);
        assertEquals(false, way.hasNode(testNode1));
        way.addNode(testNode2);
        assertEquals(true, way.hasNode(testNode2));
        way.addNode(testNode3);
        assertEquals(true, way.hasNode(testNode3));
        way.removeNode(testNode2);
        way.removeNode(testNode3);
        assertEquals(false, way.hasNode(testNode2));
        assertEquals(false, way.hasNode(testNode3));
    }

    /**
     * Tests if existing node gets replaced by the given node.
     */
    @Test
    public void test_replaceNode() {
        way.addNode(testNode1);
        way.addNode(testNode2);
        way.addNode(testNode3);
        Node replaceNode = new Node(99, 1, 10.1234567, 20.1234567);
        way.replaceNode(testNode2, replaceNode);
        assertEquals(3, way.getNodes().size());
        assertEquals(true, way.hasNode(replaceNode));

    }

    /**
     * Tests if the first node equals the last node.
     */
    @Test
    public void test_isClosed() {
        way.addNode(testNode1);
        way.addNode(testNode2);
        way.appendNode(testNode2, testNode1);
        assertEquals(true, way.isClosed());
    }

    /**
     * Tests if the given node is the first node or the last node.
     */
    @Test
    public void test_isEndNode() {
        way.addNode(testNode1);
        way.addNode(testNode2);
        assertEquals(true, way.isEndNode(testNode1));
        assertEquals(true, way.isEndNode(testNode2));
    }

    /**
     * Test if the method getFirstNode() returns the correct node.
     */
    @Test
    public void test_getFirstNode() {
        way.addNode(testNode1);
        way.addNode(testNode2);
        assertEquals(testNode1, way.getFirstNode());
    }

    /**
     * Test if the method getLastNode() returns the correct node.
     */
    @Test
    public void test_getLastNode() {
        way.addNode(testNode1);
        way.addNode(testNode2);
        assertEquals(testNode2, way.getLastNode());
    }
    
    /**
     * Create a new parcial to save/parcelable the testWay, 
     * afterwards a new node is created from the parcel and we check if it contains all attributes.
     */
	@Test 
    public void test_parcelable_way() {
    	Parcel newParcel = Parcel.obtain();
    	Way testWay = new Way(1, 2);
    	testWay.addOrUpdateTag("testtag", "test");
    	testWay.addOrUpdateTag("foo", "bar");
        Relation relation1 = new Relation(3, 1);
        testWay.addParentRelation(relation1);
        Relation relation2 = new Relation(4, 2);
        testWay.addParentRelation(relation2);
        testWay.addNode(testNode1);
        testWay.addNode(testNode2);
        testWay.addNode(testNode3);
    	testWay.writeToParcel(newParcel, 0);
    	newParcel.setDataPosition(0);
    	Way deParcelWay = Way.CREATOR.createFromParcel(newParcel);
    	assertEquals(1, deParcelWay.getOsmId()); 
    	assertEquals(2, deParcelWay.getOsmVersion());
    	assertEquals("test", deParcelWay.getTagWithKey("testtag"));
    	assertEquals("bar", deParcelWay.getTagWithKey("foo"));
    	assertEquals(2, deParcelWay.getParentRelations().size());
    	assertEquals(3, deParcelWay.getParentRelations().get(0).getOsmId());
    	assertEquals(1, deParcelWay.getParentRelations().get(0).getOsmVersion());
    	assertEquals(4, deParcelWay.getParentRelations().get(1).getOsmId());
    	assertEquals(2, deParcelWay.getParentRelations().get(1).getOsmVersion());
    	assertEquals(3, deParcelWay.getNodes().size());
        assertEquals(10, deParcelWay.getNodes().get(0).getOsmId());
        assertEquals(11, deParcelWay.getNodes().get(1).getOsmId());
        assertEquals(12, deParcelWay.getNodes().get(2).getOsmId());
        assertEquals(1, deParcelWay.getNodes().get(0).getOsmVersion());
        assertEquals(1, deParcelWay.getNodes().get(1).getOsmVersion());
        assertEquals(1, deParcelWay.getNodes().get(2).getOsmVersion());	
    }    
    
}