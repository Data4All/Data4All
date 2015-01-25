package io.github.data4all.model.data;

import static io.github.data4all.model.data.Tag.*;
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
    public static final List<Tag> tagList = new ArrayList<Tag>();

    /**
     * list of all address tags
     */
    public static final List<Tag> addressTagList = new ArrayList<Tag>();

    /**
     * list of all contact tags
     */
    public static final List<Tag> contactTagList = new ArrayList<Tag>();

    /**
     * Returns a Tag with the passed id.
     * @param id
     */
    public static Tag getTagWithId(int id) {
        for (Tag t : tagList) {
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
		return addressTagList;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for relation objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllAreaTags() {
		List<Tag> result = new ArrayList<Tag>();
		for (Tag t : tagList) {
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
		ArrayList<ClassifiedTag> result = new ArrayList<ClassifiedTag>();
		for(Tag t : tagList) {
			if(t instanceof ClassifiedTag){
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
	 * which are relevant for node objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllNodeTags() {
		List<Tag> result = new ArrayList<Tag>();
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
	 * which are relevant for relation objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllRelationTags() {
		List<Tag> result = new ArrayList<Tag>();
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
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllTags() {
		return tagList;
	}

	/**
	 * returns an arraylist containing all classified and unclassified tags
	 * which are relevant for way objects
	 * 
	 * @return tagList
	 */
	public static List<Tag> getAllWayTags() {
		List<Tag> result = new ArrayList<Tag>();
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
	 * fills the addressTagList arraylist with address tags
	 */
	static {
		// address tags
		addressTagList.add(new Tag(1, "addr:street", InputType.KEYBOARD, NODE_TAG,
				WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag(2, "addr:housenumber", InputType.NUMPAD,
				NODE_TAG, WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag(3, "addr:postcode", InputType.NUMPAD, NODE_TAG,
				WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag(4, "addr:city", InputType.KEYBOARD, NODE_TAG,
				WAY_TAG, RELATION_TAG, AREA_TAG));
		addressTagList.add(new Tag(5, "addr:country", InputType.KEYBOARD,
				NODE_TAG, WAY_TAG, RELATION_TAG, AREA_TAG));
	}	
	
	/**
	 * fills the contactTagList arraylist with contact tags.
	 */
	static {
		// contact tags
		contactTagList.add(new Tag(6, "contact:phone", InputType.NUMPAD, NODE_TAG,
				RELATION_TAG));
		contactTagList.add(new Tag(7, "contact:fax", InputType.NUMPAD, NODE_TAG,
				RELATION_TAG));
		contactTagList.add(new Tag(8, "contact:website", InputType.KEYBOARD,
				NODE_TAG, RELATION_TAG));
		contactTagList.add(new Tag(9, "contact:email", InputType.KEYBOARD,
				NODE_TAG, RELATION_TAG));
	}

	/**
	 * fills the tagList arraylist with all classified and unclassified tags.
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
		List<String> highwayValues = new ArrayList<String>(Arrays.asList(
				"motorway", "residential", "service", "track", "footway",
				"road", "path"));
		tagList.add(new ClassifiedTag(10, "highway", null, highwayValues, WAY_TAG));

		// classified tag: barrier
		List<String> barrierValues = new ArrayList<String>(Arrays.asList(
				"citywall", "fence", "wall", "bollard", "gate"));
		tagList.add(new ClassifiedTag(11, "barrier", null, barrierValues, NODE_TAG,
				WAY_TAG, AREA_TAG));

		// classified tag: amenity
		List<String> amenityValues = new ArrayList<String>(Arrays.asList(
				"bar", "cafe", "restaurant", "fast_food", "pub", "collage",
				"library", "school", "university", "parking", "taxi", "fuel",
				"bank", "hospital", "pharmacy", "cinema", "bench", "embassy",
				"marketplace", "police", "post_office", "toilets",
				"water_point", "fire_station", "public_building"));
		tagList.add(new ClassifiedTag(12, "amenity", null, amenityValues,
				new int[] { NODE_TAG, RELATION_TAG }));

		// classified tag: building
		List<String> buildingValues = new ArrayList<String>(Arrays.asList(
				"apartments", "farm", "hotel", "house", "commercial",
				"industrial", "retail", "warehouse", "church", "hospital",
				"train_station", "university"));
		tagList.add(new ClassifiedTag(13, "building", null, buildingValues,
				new int[] { NODE_TAG, RELATION_TAG }));

		// classified tag: landuse
		List<String> landuseValues = new ArrayList<String>(Arrays.asList(
				"commercial", "construction", "farmland", "forest", "grass",
				"industrial", "millitary", "residential"));
		tagList.add(new ClassifiedTag(14, "landuse", null, landuseValues,
				new int[] { RELATION_TAG, AREA_TAG }));

	}

}