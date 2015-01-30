/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
