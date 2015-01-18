package io.github.data4all.model.data;

import io.github.data4all.model.data.Tag.InputType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * List of tags for osm elements: - unclassifiedTags: user have to input text
 * manually to set the value - classifiedTags: user can choose the input from
 * listed values
 * 
 * @author fkirchge
 *
 */
public class Tags {

	/**
	 * list of all classified and unclassified tags
	 */
	public static ArrayList<Tag> tagList = new ArrayList<Tag>();

	/**
	 * fills the tagList array with classified and unclassified tags
	 * 
	 * convention for osmObject IDs: 
	 * 1: Node 
	 * 2: Way 
	 * 3: Relation
	 */
	static {
		
		// address tags
		tagList.add(new Tag("addr:street", InputType.KEYBOARD, new int[] { 1,
				2, 3 }));
		tagList.add(new Tag("addr:housenumber", InputType.NUMPAD, new int[] {
				1, 2, 3 }));
		tagList.add(new Tag("addr:postcode", InputType.NUMPAD, new int[] { 1,
				2, 3 }));
		tagList.add(new Tag("addr:city", InputType.KEYBOARD, new int[] { 1, 2,
				3 }));
		tagList.add(new Tag("addr:country", InputType.KEYBOARD, new int[] { 1,
				2, 3 }));

		// contact tags
		tagList.add(new Tag("contact:phone", InputType.NUMPAD,
				new int[] { 1, 3 }));
		tagList.add(new Tag("contact:fax", InputType.NUMPAD, new int[] { 1, 3 }));
		tagList.add(new Tag("contact:website", InputType.KEYBOARD, new int[] {
				1, 3 }));
		tagList.add(new Tag("contact:email", InputType.KEYBOARD, new int[] { 1,
				3 }));

		// classified tag: highway
		ArrayList<String> highwayValues = new ArrayList<String>(Arrays.asList(
				"motorway", "residential", "service", "track", "footway",
				"road", "path"));
		tagList.add(new ClassifiedTag("highway", null, highwayValues,
				new int[] { 2 }));

		// classified tag: barrier
		ArrayList<String> barrierValues = new ArrayList<String>(Arrays.asList(
				"citywall", "fence", "wall", "bollard", "gate"));
		tagList.add(new ClassifiedTag("barrier", null, barrierValues,
				new int[] { 1, 2 }));

		// classified tag: amenity
		ArrayList<String> amenityValues = new ArrayList<String>(Arrays.asList(
				"bar", "cafe", "restaurant", "fast_food", "pub", "collage",
				"library", "school", "university", "parking", "taxi", "fuel",
				"bank", "hospital", "pharmacy", "cinema", "bench", "embassy",
				"marketplace", "police", "post_office", "toilets",
				"water_point", "fire_station", "public_building"));
		tagList.add(new ClassifiedTag("amenity", null, amenityValues,
				new int[] { 1, 3 }));

		// classified tag: building
		ArrayList<String> buildingValues = new ArrayList<String>(Arrays.asList(
				"apartments", "farm", "hotel", "house", "commercial",
				"industrial", "retail", "warehouse", "church", "hospital",
				"train_station", "university"));
		tagList.add(new ClassifiedTag("building", null, buildingValues,
				new int[] { 1, 3 }));

		// classified tag: landuse
		ArrayList<String> landuseValues = new ArrayList<String>(Arrays.asList(
				"commercial", "construction", "farmland", "forest", "grass",
				"industrial", "millitary", "residential"));
		tagList.add(new ClassifiedTag("landuse", null, landuseValues,
				new int[] { 3 }));

	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllTags() {
		return tagList;
	}
	
	/**
	 * returns an arraylist containing all classified and unclassified tags which are relevant for node objects
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllNodeTags() {
		ArrayList<Tag> result = new ArrayList<Tag>(); 
		for (Tag t : tagList) {
			int[] osmObjects = t.getOsmObjects();
			if (osmObjects.length > 0) {
				for (int i = 0; i < osmObjects.length; i++) {
					if (osmObjects[i] == 1) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * returns an arraylist containing all classified and unclassified tags which are relevant for way objects
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllWayTags() {
		ArrayList<Tag> result = new ArrayList<Tag>(); 
		for (Tag t : tagList) {
			int[] osmObjects = t.getOsmObjects();
			if (osmObjects.length > 0) {
				for (int i = 0; i < osmObjects.length; i++) {
					if (osmObjects[i] == 2) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * returns an arraylist containing all classified and unclassified tags which are relevant for relation objects
	 * @return tagList
	 */
	public static ArrayList<Tag> getAllRelationTags() {
		ArrayList<Tag> result = new ArrayList<Tag>(); 
		for (Tag t : tagList) {
			int[] osmObjects = t.getOsmObjects();
			if (osmObjects.length > 0) {
				for (int i = 0; i < osmObjects.length; i++) {
					if (osmObjects[i] == 1) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}

}