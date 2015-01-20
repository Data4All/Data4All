package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests if the methods in Tags.java return the correct number of elements.
 * @author fkirchge
 *
 */
public class TagsTest {
	
	/**
	 * Tests if the getAllNodeTags() method return the correct number of node tags.
	 */
	 @Test
	 public void test_getAllNodeTags() {
		 assertEquals(3, Tags.getAllNodeTags().size());
	 }
	 
	 /**
	  * Tests if the getAllWayTags() method return the correct number of way tags.
	  */
	 @Test
	 public void test_getAllWayTags() {
		 assertEquals(2, Tags.getAllWayTags().size());
	 }
	 
	 /**
	  * Tests if the getAllRelationTags() method return the correct number of relation tags.
	  */
	 @Test
	 public void test_getAllRelationTags() {
		 assertEquals(3, Tags.getAllRelationTags().size());
	 }
	 
	 /**
	  * Tests if the getAllAreaTags() method return the correct number of area tags.
	  */
	 @Test
	 public void test_getAllAreaTags() {
		 assertEquals(2, Tags.getAllAreaTags().size());
	 }
	 
	 /**
	  * Tests if the getAllAddressTags() method return the correct number of address tags.
	  */
	 @Test
	 public void test_getAllAddressTags() {
		 assertEquals(5, Tags.getAllAddressTags().size());
	 }
	 
	 /**
	  * Tests if the getAllContactTags() method return the correct number of contact tags.
	  */
	 @Test
	 public void test_getAllContactTags() {
		 assertEquals(4, Tags.getAllContactTags().size());
	 }

}
