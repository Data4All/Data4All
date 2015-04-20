/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
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

    private Node testNode;

    @Before
    public void setUp() {
        testNode = new Node(1, 30.123456, 40.1234567);
    }

    /**
     * Tests if the setTags() methods works correctly. Add tags, check if tags
     * are stored, replace all existing tags with newTags and check again if
     * they are stored.
     */
    @Test
    public void test_setTags() {
        Map<Tag, String> tags = new LinkedHashMap<Tag, String>();
        tags.put(Tags.getAllAddressTags().get(0), "foo");
        tags.put(Tags.getAllAddressTags().get(1), "bar");
        tags.put(Tags.getAllAddressTags().get(2), "test");
        testNode.addTags(tags);
        assertEquals(tags.size(), testNode.getTags().size());
        assertEquals(tags, testNode.getTags());

        Map<Tag, String> newTags = new LinkedHashMap<Tag, String>();
        newTags.put(Tags.getTagWithId(10), "motorway");
        newTags.put(Tags.getTagWithId(11), "fence");
        newTags.put(Tags.getTagWithId(12), "restaurant");
        assertTrue(testNode.setTags(newTags));
        assertEquals(newTags, testNode.getTags());
        assertEquals(newTags.size(), testNode.getTags().size());
    }

    /**
     * Adds a new tag using the addOrUpdateTag() method. Replacing existing tag
     * with new value. Test if they are stored correctly.
     */
    @Test
    public void test_addOrUpdateTag() {
        testNode.addOrUpdateTag(Tags.getTagWithId(10), "motorway");
        assertEquals("motorway",
                testNode.getTagValueWithKey(Tags.getTagWithId(10)));
        assertEquals(1, testNode.getTags().size());
        testNode.addOrUpdateTag(Tags.getTagWithId(10), "service");
        assertEquals("service",
                testNode.getTagValueWithKey(Tags.getTagWithId(10)));
        assertEquals(1, testNode.getTags().size());
    }

    /**
     * Adding a list of tags using the addTag() method. Tests if all tags are
     * stored correctly.
     */
    @Test
    public void test_addTags() {
        Map<Tag, String> tags = new LinkedHashMap<Tag, String>();
        tags.put(Tags.getTagWithId(10), "motorway");
        tags.put(Tags.getTagWithId(11), "fence");
        tags.put(Tags.getTagWithId(12), "restaurant");
        testNode.addTags(tags);
        assertEquals(tags, testNode.getTags());
    }

    /**
     * Tests the hasTag() method.
     */
    @Test
    public void test_hasTag() {
        Map<Tag, String> tags = new LinkedHashMap<Tag, String>();
        tags.put(Tags.getTagWithId(10), "motorway");
        testNode.addTags(tags);
        assertTrue(testNode.hasTag(Tags.getTagWithId(10), "motorway"));
    }

    /**
     * Tests the getTagWithKey() method.
     */
    @Test
    public void test_getTagWithKey() {
        Map<Tag, String> tags = new LinkedHashMap<Tag, String>();
        tags.put(Tags.getTagWithId(10), "orchard");
        System.out.println(Tags.getTagWithId(10));
        testNode.addTags(tags);
        assertEquals("orchard",
                testNode.getTagValueWithKey(Tags.getTagWithId(10)));
    }

    /**
     * Tests the hasTagKey() method.
     */
    @Test
    public void test_hasTagKey() {
        Map<Tag, String> tags = new LinkedHashMap<Tag, String>();
        tags.put(Tags.getTagWithId(10), "motorway");
        testNode.addTags(tags);
        assertTrue(testNode.hasTagKey(Tags.getTagWithId(10)));
    }

    /**
     * Tests the isTagged() method.
     */
    @Test
    public void test_isTagged() {
        Map<Tag, String> tags = new LinkedHashMap<Tag, String>();
        tags.put(Tags.getTagWithId(10), "motorway");
        testNode.addTags(tags);
        assertTrue(testNode.isTagged());
    }

    /**
     * Create a new Parcel to save/parcelable the testNode, afterwards a new
     * node is created from the parcel and we check if it contains all
     * attributes.
     */
    @Test
    public void test_parcelable_node() {
        Parcel newParcel = Parcel.obtain();

        testNode.addOrUpdateTag(Tags.getAllAddressTags().get(0), "foo");
        testNode.addOrUpdateTag(Tags.getAllAddressTags().get(1), "bar");

        testNode.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        Node deParcelNode = Node.CREATOR.createFromParcel(newParcel);

        assertEquals(testNode.getOsmId(), deParcelNode.getOsmId());

        assertEquals(testNode.getLon(), deParcelNode.getLon(), 0);
        assertEquals(testNode.getLat(), deParcelNode.getLat(), 0);

        assertEquals(testNode.getTagValueWithKey(Tags.getAllAddressTags()
                .get(0)), deParcelNode.getTagValueWithKey(Tags
                .getAllAddressTags().get(0)));
        assertEquals(testNode.getTagValueWithKey(Tags.getAllAddressTags()
                .get(1)), deParcelNode.getTagValueWithKey(Tags
                .getAllAddressTags().get(1)));

    }

}
