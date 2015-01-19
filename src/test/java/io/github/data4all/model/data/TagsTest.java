package io.github.data4all.model.data;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 * @author fkirchge
 *
 */
public class TagsTest {
	
	 @Test
	 public void test_getAllNodeTags() {
		 ArrayList<Tag> tags = Tags.getAllNodeTags();
		 for (Tag t : tags) {
			 System.out.println("Tag for Node: " +t.toString());
		 }
	 }
	 
	 @Test
	 public void test_getAllWayTags() {
		 ArrayList<Tag> tags = Tags.getAllWayTags();
		 for (Tag t : tags) {
			 System.out.println("Tag for Way: " +t.toString());
		 }
	 }
	 
	 @Test
	 public void test_getAllRelationTags() {
		 ArrayList<Tag> tags = Tags.getAllRelationTags();
		 for (Tag t : tags) {
			 System.out.println("Tag for Relation: " +t.toString());
		 }
	 }

}
