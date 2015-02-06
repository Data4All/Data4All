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

import static io.github.data4all.model.data.Tag.NODE_TAG;
import static io.github.data4all.model.data.Tag.WAY_TAG;
import static io.github.data4all.model.data.Tag.AREA_TAG;
import static io.github.data4all.model.data.Tag.BUILDING_TAG;
import io.github.data4all.model.data.Tag.InputType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List of tags for osm elements: - unclassifiedTags: user have to input text
 * manually to set the value - classifiedTags: user can choose the input from
 * listed values.
 * 
 * @author fkirchge, optimized by tbrose
 *
 */
public final class Tags {
    
    /**
     * list of all classified and unclassified tags
     */
    public static final List<Tag> TAG_LIST = new ArrayList<Tag>();

    /**
     * list of all address tags
     */
    public static final List<Tag> ADDRESS_TAG_LIST = new ArrayList<Tag>();

    /**
     * list of all contact tags
     */
    public static final List<Tag> contactTagList = new ArrayList<Tag>();

    private Tags() {
        
    }
    
    /**
     * Returns a Tag with the passed id.
     * @param id
     * @return tag object
     */
    public static Tag getTagWithId(int id) {
        for (Tag t : TAG_LIST) {
            if (t.getId() == id) {
                return t; 
            }
        }
        return null;
    }
    
	/**
	 * returns an arraylist containing all address tags.
	 */
	public static List<Tag> getAllAddressTags() {
		return ADDRESS_TAG_LIST;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for relation objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllAreaTags() {
		final List<Tag> result = new ArrayList<Tag>();
		for (Tag t : TAG_LIST) {
			if (t instanceof ClassifiedTag) {
				final int[] osmObjects = t.getOsmObjects();
				if (osmObjects.length > 0) {
					for (int i = 0; i < osmObjects.length; i++) {
						if (osmObjects[i] == 4) {
							result.add(t);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns all classified tags.
	 * @return list of all classified tags
	 */
	public static List<ClassifiedTag> getAllClassifiedTags(){
		final List<ClassifiedTag> result = new ArrayList<ClassifiedTag>();
		for (Tag t : TAG_LIST) {
			if (t instanceof ClassifiedTag) {
				result.add((ClassifiedTag) t);
			}
		}
		return result;
	}

	/**
	 * returns an arraylist containing all contact tags.
	 */
	public static List<Tag> getAllContactTags() {
		return contactTagList;
	}
	
	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for node objects.
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllNodeTags() {
		final List<Tag> result = new ArrayList<Tag>();
		for (Tag t : TAG_LIST) {
			if (t instanceof ClassifiedTag) {
				final int[] osmObjects = t.getOsmObjects();
				if (osmObjects.length > 0) {
					for (int i = 0; i < osmObjects.length; i++) {
						if (osmObjects[i] == 1) {
							result.add(t);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for relation objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllBuildingTags() {
		final List<Tag> result = new ArrayList<Tag>();
		for (Tag t : TAG_LIST) {
			if (t instanceof ClassifiedTag) {
				final int[] osmObjects = t.getOsmObjects();
				if (osmObjects.length > 0) {
					for (int i = 0; i < osmObjects.length; i++) {
						if (osmObjects[i] == 3) {
							result.add(t);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllTags() {
		return TAG_LIST;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for way objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllWayTags() {
		final List<Tag> result = new ArrayList<Tag>();
		for (Tag t : TAG_LIST) {
			if (t instanceof ClassifiedTag) {
				final int[] osmObjects = t.getOsmObjects();
				if (osmObjects.length > 0) {
					for (int i = 0; i < osmObjects.length; i++) {
						if (osmObjects[i] == 2) {
							result.add(t);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * fills the addressTagList arraylist with address tags
	 */
	static {
		// address tags
		ADDRESS_TAG_LIST.add(new Tag(1, "addr:street", InputType.KEYBOARD, NODE_TAG,
				WAY_TAG, BUILDING_TAG, AREA_TAG));
		ADDRESS_TAG_LIST.add(new Tag(2, "addr:housenumber", InputType.NUMPAD,
				NODE_TAG, WAY_TAG, BUILDING_TAG, AREA_TAG));
		ADDRESS_TAG_LIST.add(new Tag(3, "addr:postcode", InputType.NUMPAD, NODE_TAG,
				WAY_TAG, BUILDING_TAG, AREA_TAG));
		ADDRESS_TAG_LIST.add(new Tag(4, "addr:city", InputType.KEYBOARD, NODE_TAG,
				WAY_TAG, BUILDING_TAG, AREA_TAG));
		ADDRESS_TAG_LIST.add(new Tag(5, "addr:country", InputType.KEYBOARD,
				NODE_TAG, WAY_TAG, BUILDING_TAG, AREA_TAG));
	}	
	
	/**
	 * fills the contactTagList arraylist with contact tags.
	 */
	static {
		// contact tags
		contactTagList.add(new Tag(6, "contact:phone", InputType.NUMPAD, NODE_TAG,
				BUILDING_TAG));
		contactTagList.add(new Tag(7, "contact:fax", InputType.NUMPAD, NODE_TAG,
				BUILDING_TAG));
		contactTagList.add(new Tag(8, "contact:website", InputType.KEYBOARD,
				NODE_TAG, BUILDING_TAG));
		contactTagList.add(new Tag(9, "contact:email", InputType.KEYBOARD,
				NODE_TAG, BUILDING_TAG));
	}

	/**
	 * fills the tagList arraylist with all classified and unclassified tags.
	 * 
	 * convention for osmObject IDs: 1: Node 2: Way 3: Relation (Building) 4:
	 * Area
	 */
	static {

		// address tags
		TAG_LIST.addAll(ADDRESS_TAG_LIST);

		// contact tags
		TAG_LIST.addAll(contactTagList);

		// classified tag: highway
		List<String> highwayValues = new ArrayList<String>(Arrays.asList(
				"motorway", "residential", "service", "track", "footway",
				"road", "path"));
		TAG_LIST.add(new ClassifiedTag(10, "highway", null, highwayValues, WAY_TAG));

		// classified tag: barrier
		List<String> barrierValues = new ArrayList<String>(Arrays.asList(
				"citywall", "fence", "wall", "bollard", "gate"));
		TAG_LIST.add(new ClassifiedTag(11, "barrier", null, barrierValues, NODE_TAG,
				WAY_TAG, AREA_TAG));

		// classified tag: amenity
		List<String> amenityValues = new ArrayList<String>(Arrays.asList(
				"bar", "cafe", "restaurant", "fast_food", "pub", "collage",
				"library", "school", "university", "parking", "taxi", "fuel",
				"bank", "hospital", "pharmacy", "cinema", "bench", "embassy",
				"marketplace", "police", "post_office", "toilets",
				"water_point", "fire_station", "public_building"));
		TAG_LIST.add(new ClassifiedTag(12, "amenity", null, amenityValues,
				new int[] { NODE_TAG, BUILDING_TAG } ));

		// classified tag: building
		List<String> buildingValues = new ArrayList<String>(Arrays.asList(
				"apartments", "farm", "hotel", "house", "commercial",
				"industrial", "retail", "warehouse", "church", "hospital",
				"train_station", "university"));
		TAG_LIST.add(new ClassifiedTag(13, "building", null, buildingValues,
				new int[] { NODE_TAG, BUILDING_TAG } ));

		// classified tag: landuse
		List<String> landuseValues = new ArrayList<String>(Arrays.asList(
				"commercial", "construction", "farmland", "forest", "grass",
				"industrial", "millitary", "residential"));
		TAG_LIST.add(new ClassifiedTag(14, "landuse", null, landuseValues,
				new int[] { BUILDING_TAG, AREA_TAG } ));

	}

}