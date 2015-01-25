package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Tests the main functionality of PolyElement objects. Inherited methods from
 * OsmElement already tested in NodeTest.java.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class PolyElementTest {

    private PolyElement PolyElement;
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
        this.PolyElement = new PolyElement(10);
        this.testNode1 = new Node(10, 10.1234567, 20.1234567);
        this.testNode2 = new Node(11, 10.1234567, 20.1234567);
        this.testNode3 = new Node(12, 10.1234567, 20.1234567);
        this.nodes = new ArrayList<Node>();
        this.nodes.add(testNode1);
        this.nodes.add(testNode2);
        this.nodes.add(testNode3);
    }

    /**
     * Tests if a new node is added correctly to the list. Tests if it is not
     * possible to add more than 2000 nodes.
     */
    @Test
    public void test_addNode() {
        Node newNode = new Node(1, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode);
        assertEquals(1, PolyElement.getNodes().size());
        assertEquals(true, PolyElement.getNodes().contains(newNode));
        for (int i = 0; i <= 2100; i++) {
            Node node = new Node(1, 10.1234567, 10.1234567);
            PolyElement.addNode(node);
        }
        assertEquals(2000, PolyElement.getNodes().size());
        PolyElement.addNode(newNode);
        PolyElement.addNode(newNode);
    }

    /**
     * Tests if a node is appended correctly at the begin or end of the PolyElement.
     */
    @Test
    public void test_appendNode() {
        Node newNode1 = new Node(1, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode1);
        Node newNode2 = new Node(2, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode2);
        Node newNode3 = new Node(3, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode3);
        assertEquals(3, PolyElement.getNodes().size());
        assertEquals(newNode1, PolyElement.getFirstNode());
        PolyElement.appendNode(newNode1, testNode1);
        assertEquals(testNode1, PolyElement.getFirstNode());
        assertEquals(4, PolyElement.getNodes().size());
        PolyElement.appendNode(newNode3, testNode2);
        assertEquals(testNode2, PolyElement.getLastNode());
        assertEquals(5, PolyElement.getNodes().size());
        PolyElement.appendNode(testNode3, newNode2);
        assertEquals(5, PolyElement.getNodes().size());
    }

    /**
     * Tests if a new node is inserted after a existing node.
     */
    @Test
    public void test_addNodeAfter() {
        Node newNode1 = new Node(1, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode1);
        Node newNode2 = new Node(2, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode2);
        Node newNode3 = new Node(3, 10.1234567, 20.1234567);
        PolyElement.addNode(newNode3);
        PolyElement.addNodeAfter(newNode2, testNode1);
        assertEquals(4, PolyElement.getNodes().size());
        assertEquals(2, PolyElement.getNodes().indexOf(testNode1));
    }

    /**
     * Tests if a list of nodes is added at the begin of a existing list.
     */
    @Test
    public void test_addNodes() {
        PolyElement.addNodes(nodes, true);
        assertEquals(3, PolyElement.getNodes().size());
        assertEquals(testNode1, PolyElement.getFirstNode());
        Node newNode1 = new Node(1, 10.1234567, 20.1234567);
        Node newNode2 = new Node(2, 10.1234567, 20.1234567);
        Node newNode3 = new Node(3, 10.1234567, 20.1234567);
        List<Node> nodes1 = new ArrayList<Node>();
        nodes1.add(newNode1);
        nodes1.add(newNode2);
        nodes1.add(newNode3);
        PolyElement.addNodes(nodes1, true);
        assertEquals(newNode1, PolyElement.getFirstNode());
        assertEquals(6, PolyElement.getNodes().size());
    }

    /**
     * Tests if the given node is part of the PolyElement.
     */
    @Test
    public void test_hasNode() {
        assertEquals(false, PolyElement.hasNode(testNode1));
        PolyElement.addNode(testNode1);
        assertEquals(true, PolyElement.hasNode(testNode1));
    }

    /**
     * Tests if existing PolyElements has common node with given node.
     */
    @Test
    public void test_hasCommonNode() {
        PolyElement.addNode(testNode1);
        PolyElement secondPolyElement = new PolyElement(1);
        secondPolyElement.addNode(testNode1);
        assertEquals(true, PolyElement.hasCommonNode(secondPolyElement));
    }

    /**
     * Tests if the given node is removed from the list.
     */
    @Test
    public void test_removeNode() {
        PolyElement.addNode(testNode1);
        assertEquals(true, PolyElement.hasNode(testNode1));
        PolyElement.removeNode(testNode1);
        assertEquals(false, PolyElement.hasNode(testNode1));
        PolyElement.addNode(testNode2);
        assertEquals(true, PolyElement.hasNode(testNode2));
        PolyElement.addNode(testNode3);
        assertEquals(true, PolyElement.hasNode(testNode3));
        PolyElement.removeNode(testNode2);
        PolyElement.removeNode(testNode3);
        assertEquals(false, PolyElement.hasNode(testNode2));
        assertEquals(false, PolyElement.hasNode(testNode3));
    }

    /**
     * Tests if existing node gets replaced by the given node.
     */
    @Test
    public void test_replaceNode() {
        PolyElement.addNode(testNode1);
        PolyElement.addNode(testNode2);
        PolyElement.addNode(testNode3);
        Node replaceNode = new Node(99, 10.1234567, 20.1234567);
        PolyElement.replaceNode(testNode2, replaceNode);
        assertEquals(3, PolyElement.getNodes().size());
        assertEquals(true, PolyElement.hasNode(replaceNode));

    }

    /**
     * Tests if the first node equals the last node.
     */
    @Test
    public void test_isClosed() {
        PolyElement.addNode(testNode1);
        PolyElement.addNode(testNode2);
        PolyElement.appendNode(testNode2, testNode1);
        assertEquals(true, PolyElement.isClosed());
    }

    /**
     * Tests if the given node is the first node or the last node.
     */
    @Test
    public void test_isEndNode() {
        PolyElement.addNode(testNode1);
        PolyElement.addNode(testNode2);
        assertEquals(true, PolyElement.isEndNode(testNode1));
        assertEquals(true, PolyElement.isEndNode(testNode2));
    }

    /**
     * Test if the method getFirstNode() returns the correct node.
     */
    @Test
    public void test_getFirstNode() {
        PolyElement.addNode(testNode1);
        PolyElement.addNode(testNode2);
        assertEquals(testNode1, PolyElement.getFirstNode());
    }

    /**
     * Test if the method getLastNode() returns the correct node.
     */
    @Test
    public void test_getLastNode() {
        PolyElement.addNode(testNode1);
        PolyElement.addNode(testNode2);
        assertEquals(testNode2, PolyElement.getLastNode());
    }

    /**
     * Create a new Parcel to save/parcelable the testPolyElement, afterwards a new PolyElement
     * is created from the parcel and we check if it contains all attributes.
     */
    @Test
    public void test_parcelable_PolyElement() {
        Parcel newParcel = Parcel.obtain();
        PolyElement testPolyElement = new PolyElement(1);

        testPolyElement.addOrUpdateTag(Tags.getAllAddressTags().get(0), "foo");
        testPolyElement.addOrUpdateTag(Tags.getAllAddressTags().get(1), "bar");

        testPolyElement.addNode(testNode1);
        testPolyElement.addNode(testNode2);
        testPolyElement.addNode(testNode3);

        testPolyElement.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        PolyElement deParcelPolyElement = PolyElement.CREATOR.createFromParcel(newParcel);

        assertEquals(testPolyElement.getOsmId(), deParcelPolyElement.getOsmId());

        assertEquals(testPolyElement.getTagValueWithKey(Tags.getAllAddressTags().get(0)),
                deParcelPolyElement.getTagValueWithKey(Tags.getAllAddressTags().get(0)));
        assertEquals(testPolyElement.getTagValueWithKey(Tags.getAllAddressTags().get(1)),
                deParcelPolyElement.getTagValueWithKey(Tags.getAllAddressTags().get(1)));

        assertEquals(testPolyElement.getNodes().size(), deParcelPolyElement.getNodes().size());

        assertEquals(testNode1.getOsmId(), deParcelPolyElement.getNodes().get(0)
                .getOsmId());
        assertEquals(testNode2.getOsmId(), deParcelPolyElement.getNodes().get(1)
                .getOsmId());
        assertEquals(testNode3.getOsmId(), deParcelPolyElement.getNodes().get(2)
                .getOsmId());

    }
}
