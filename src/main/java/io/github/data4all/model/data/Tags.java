package io.github.data4all.model.data;

import static io.github.data4all.model.data.Tag.*;

import io.github.data4all.model.data.Tag.InputType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * List of tags for osm elements: - unclassifiedTags: user have to input text
 * manually to set the value - classifiedTags: user can choose the input from
 * listed values
 * 
 * @author fkirchge, optimized by tbrose
 *
 */
public class Tags {

	/**
	 * list of all classified and unclassified tags
	 */
	public static ArrayList<Tag> tagList = new ArrayList<Tag>();

	/**
	 * list of all address tags
	 */
	public static ArrayList<Tag> addressTagList = new ArrayList<Tag>();

	/**
	 * list of all contact tags
	 */
	public static ArrayList<Tag> contactTagList = new ArrayList<Tag>();

	/**
	 * fills the addressTagList arraylist with address tags
	 */
	static {
		// address tags
		addressTagList.add(new Tag("addr:street", InputType.KEYBOARD, NODE_TAG,
				WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag("addr:housenumber", InputType.NUMPAD,
				NODE_TAG, WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag("addr:postcode", InputType.NUMPAD, NODE_TAG,
				WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag("addr:city", InputType.KEYBOARD, NODE_TAG,
				WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag("addr:country", InputType.KEYBOARD,
				NODE_TAG, WAY_TAG, RELATION_TAG, AREA_TAG));
	}

	/**
	 * fills the contactTagList arraylist with contact tags
	 */
	static {
		// contact tags
		contactTagList.add(new Tag("contact:phone", InputType.NUMPAD, NODE_TAG,
				RELATION_TAG));
		contactTagList.add(new Tag("contact:fax", InputType.NUMPAD, NODE_TAG,
				RELATION_TAG));
		contactTagList.add(new Tag("contact:website", InputType.KEYBOARD,
				NODE_TAG, RELATION_TAG));
		contactTagList.add(new Tag("contact:email", InputType.KEYBOARD,
				NODE_TAG, RELATION_TAG));
	}

	/**
	 * fills the tagList arraylist with all classified and unclassified tags
	 * 
	 * convention for osmObject IDs: 1: Node 2: Way 3: Relation (Building) 4:
	 * Area
	 */
	static {

		// address tags
		tagList.addAll(addressTagList);

		// contact tags
		tagList.addAll(contactTagList);

		// classified tag: highway
		ArrayList<String> highwayValues = new ArrayList<String>(Arrays.asList(
				"motorway", "residential", "service", "track", "footway",
				"road", "path"));
		tagList.add(new ClassifiedTag("highway", null, highwayValues, WAY_TAG));

		// classified tag: barrier
		ArrayList<String> barrierValues = new ArrayList<String>(Arrays.asList(
				"citywall", "fence", "wall", "bollard", "gate"));
		tagList.add(new ClassifiedTag("barrier", null, barrierValues, NODE_TAG,
				WAY_TAG, AREA_TAG));

		// classified tag: amenity
		ArrayList<String> amenityValues = new ArrayList<String>(Arrays.asList(
				"bar", "cafe", "restaurant", "fast_food", "pub", "collage",
				"library", "school", "university", "parking", "taxi", "fuel",
				"bank", "hospital", "pharmacy", "cinema", "bench", "embassy",
				"marketplace", "police", "post_office", "toilets",
				"water_point", "fire_station", "public_building"));
		tagList.add(new ClassifiedTag("amenity", null, amenityValues,
				new int[] { NODE_TAG, RELATION_TAG }));

		// classified tag: building
		ArrayList<String> buildingValues = new ArrayList<String>(Arrays.asList(
				"apartments", "farm", "hotel", "house", "commercial",
				"industrial", "retail", "warehouse", "church", "hospital",
				"train_station", "university"));
		tagList.add(new ClassifiedTag("building", null, buildingValues,
				new int[] { NODE_TAG, RELATION_TAG }));

		// classified tag: landuse
		ArrayList<String> landuseValues = new ArrayList<String>(Arrays.asList(
				"commercial", "construction", "farmland", "forest", "grass",
				"industrial", "millitary", "residential"));
		tagList.add(new ClassifiedTag("landuse", null, landuseValues,
				new int[] { RELATION_TAG, AREA_TAG }));

	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * 
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllTags() {
		return tagList;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for node objects
	 * 
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllNodeTags() {
		ArrayList<Tag> result = new ArrayList<Tag>();
		for (Tag t : tagList) {
			if (t instanceof ClassifiedTag) {
				int[] osmObjects = t.getOsmObjects();
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
	 * which are relevant for way objects
	 * 
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllWayTags() {
		ArrayList<Tag> result = new ArrayList<Tag>();
		for (Tag t : tagList) {
			if (t instanceof ClassifiedTag) {
				int[] osmObjects = t.getOsmObjects();
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
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for relation objects
	 * 
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllRelationTags() {
		ArrayList<Tag> result = new ArrayList<Tag>();
		for (Tag t : tagList) {
			if (t instanceof ClassifiedTag) {
				int[] osmObjects = t.getOsmObjects();
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
	 * which are relevant for relation objects
	 * 
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllAreaTags() {
		ArrayList<Tag> result = new ArrayList<Tag>();
		for (Tag t : tagList) {
			if (t instanceof ClassifiedTag) {
				int[] osmObjects = t.getOsmObjects();
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
	 * returns an arraylist containing all address tags
	 */
	public static ArrayList<Tag> getAllAddressTags() {
		return addressTagList;
	}

	/**
	 * returns an arraylist containing all contact tags
	 */
	public static ArrayList<Tag> getAllContactTags() {
		return contactTagList;
	}

}