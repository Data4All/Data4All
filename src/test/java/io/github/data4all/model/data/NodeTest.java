package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Testing the main functionality of the node objects. Testing inherited methods
 * from OsmElement class.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class NodeTest {

    /**
     * Tests if the setTags() methods works correctly. Add tags, check if tags
     * are stored, replace all existing tags with newTags and check again if
     * they are stored.
     */
    @Test
    public void test_setTags() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Map<String, String> tags = new TreeMap<String, String>();
        tags.put("highway", "motorway");
        tags.put("barrier", "fence");
        tags.put("amenity", "bar");
        tags.put("building", "farm");
        testNode.addTags(tags);
        assertEquals(tags, testNode.getTags());
        assertEquals(4, testNode.getTags().size());
        Map<String, String> newTags = new TreeMap<String, String>();
        newTags.put("landuse", "commercial");
        newTags.put("building", "hotel");
        newTags.put("amenity", "restaurant");
        assertEquals(true, testNode.setTags(newTags));
        assertEquals(newTags, testNode.getTags());
        assertEquals(3, testNode.getTags().size());
    }

    /**
     * Adds a new tag using the addOrUpdateTag() method. Replacing existing tag
     * with new value. Test if they are stored correctly.
     */
    @Test
    public void test_addOrUpdateTag() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        testNode.addOrUpdateTag("highway", "motorway");
        assertEquals("motorway", testNode.getTagWithKey("highway"));
        assertEquals(1, testNode.getTags().size());
        testNode.addOrUpdateTag("highway", "service");
        assertEquals("service", testNode.getTagWithKey("highway"));
        assertEquals(1, testNode.getTags().size());
    }

    /**
     * Adding a list of tags using the addTag() method. Tests if all tags are
     * stored correctly.
     */
    @Test
    public void test_addTags() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Map<String, String> tags = new TreeMap<String, String>();
        tags.put("highway", "motorway");
        tags.put("barrier", "fence");
        tags.put("amenity", "bar");
        tags.put("building", "farm");
        testNode.addTags(tags);
        assertEquals(tags, testNode.getTags());
    }

    /**
     * Tests the hasTag() method.
     */
    @Test
    public void test_hasTag() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Map<String, String> tags = new TreeMap<String, String>();
        tags.put("highway", "motorway");
        testNode.addTags(tags);
        assertEquals(true, testNode.hasTag("highway", "motorway"));
    }

    /**
     * Tests the getTagWithKey() method.
     */
    @Test
    public void test_getTagWithKey() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Map<String, String> tags = new TreeMap<String, String>();
        tags.put("highway", "motorway");
        testNode.addTags(tags);
        assertEquals("motorway", testNode.getTagWithKey("highway"));
    }

    /**
     * Tests the hasTagKey() method.
     */
    @Test
    public void test_hasTagKey() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Map<String, String> tags = new TreeMap<String, String>();
        tags.put("highway", "motorway");
        testNode.addTags(tags);
        assertEquals(true, testNode.hasTagKey("highway"));
    }

    /**
     * Tests the isTagged() method.
     */
    @Test
    public void test_isTagged() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Map<String, String> tags = new TreeMap<String, String>();
        tags.put("highway", "motorway");
        testNode.addTags(tags);
        assertEquals(true, testNode.isTagged());
    }

    /**
     * Tests the addParentRelation() method.
     */
    @Test
    public void test_addParentRelation() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Relation relation = new Relation(3, 1);
        testNode.addParentRelation(relation);
        assertEquals(true, testNode.getParentRelations().contains(relation));
        assertEquals(true, testNode.hasParentRelation(relation));
    }

    /**
     * Tests if the relation is contained in the list of parent relations. Using
     * the relation as identifier.
     */
    @Test
    public void test_relation_hasParentRelation() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Relation relation = new Relation(3, 1);
        testNode.addParentRelation(relation);
        assertEquals(true, testNode.hasParentRelation(relation));
    }

    /**
     * Test if the relation is contained in the list of parent relations. Using
     * the osmId as identifier.
     * 
     */
    @Test
    public void test_osmid_hasParentRelation() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Relation relation = new Relation(3, 1);
        testNode.addParentRelation(relation);
        assertEquals(true, testNode.hasParentRelation(3));
    }

    /**
     * Tests if the node has a parent relation.
     */
    @Test
    public void test_osmid_hasParentRelation1() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        assertEquals(false, testNode.hasParentRelation(3));
    }

    /**
     * Adds a list of relations to as parent relations of the node. Tests if all
     * list elements are stored.
     */
    @Test
    public void test_addParentRelations() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        List<Relation> relationList = new ArrayList<Relation>();
        relationList.add(new Relation(3, 1));
        relationList.add(new Relation(4, 1));
        relationList.add(new Relation(5, 1));
        testNode.addParentRelations(relationList);
        assertEquals(3, testNode.getParentRelations().size());
        assertEquals(relationList, testNode.getParentRelations());
    }

    /**
     * Tests if the node has parent relations.
     */
    @Test
    public void test_hasParentRelations() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Relation relation = new Relation(3, 1);
        testNode.addParentRelation(relation);
        assertEquals(true, testNode.hasParentRelations());
    }

    /**
     * Tests if parent relations can be removed from the list by relation
     * identifier.
     */
    @Test
    public void test_relation_removeParentRelation() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Relation relation = new Relation(3, 1);
        testNode.addParentRelation(relation);
        assertEquals(true, testNode.hasParentRelations());
        assertEquals(true, testNode.getParentRelations().contains(relation));
        assertEquals(1, testNode.getParentRelations().size());
        testNode.removeParentRelation(relation);
        assertEquals(0, testNode.getParentRelations().size());
        assertEquals(false, testNode.getParentRelations().contains(relation));
    }

    /**
     * Tests if parent relations can be removed from the list by osmId as
     * identifier.
     */
    @Test
    public void test_osmid_removeParentRelation() {
        Node testNode = new Node(1, 2, 30.123456, 40.1234567);
        Relation relation = new Relation(3, 1);
        testNode.addParentRelation(relation);
        assertEquals(true, testNode.hasParentRelations());
        assertEquals(true, testNode.getParentRelations().contains(relation));
        assertEquals(1, testNode.getParentRelations().size());
        testNode.removeParentRelation(3);
        assertEquals(0, testNode.getParentRelations().size());
        assertEquals(false, testNode.getParentRelations().contains(relation));
    }
    
    /**
     * Create a new parcial to save/parcelable the testNode, afterwards 
     * a new node is created from the parcel and we check if it contains all attributes.
     */
	@Test 
    public void test_parcelable() {
    	Parcel newParcel = Parcel.obtain();
    	Node testNode = new Node(1, 2, 30.123456, 40.1234567);
    	testNode.addOrUpdateTag("testtag", "test");
    	testNode.addOrUpdateTag("foo", "bar");
        Relation relation1 = new Relation(3, 1);
        testNode.addParentRelation(relation1);
        Relation relation2 = new Relation(4, 2);
        testNode.addParentRelation(relation2);
    	testNode.writeToParcel(newParcel, 0);
    	newParcel.setDataPosition(0);
    	Node deParcelNode = Node.CREATOR.createFromParcel(newParcel);
    	assertEquals(1, deParcelNode.getOsmId()); 
    	assertEquals(2, deParcelNode.getOsmVersion());
    	assertEquals(40.1234567, deParcelNode.getLon(), 0);
    	assertEquals(30.123456, deParcelNode.getLat(), 0);
    	assertEquals("test", deParcelNode.getTagWithKey("testtag"));
    	assertEquals("bar", deParcelNode.getTagWithKey("foo"));
    	assertEquals(2, deParcelNode.getParentRelations().size());
    	assertEquals(3, deParcelNode.getParentRelations().get(0).getOsmId());
    	assertEquals(1, deParcelNode.getParentRelations().get(0).getOsmVersion());
    	assertEquals(4, deParcelNode.getParentRelations().get(1).getOsmId());
    	assertEquals(2, deParcelNode.getParentRelations().get(1).getOsmVersion());
    }

}