/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.model.data;

import static io.github.data4all.model.data.Tag.AREA_TAG;
import static io.github.data4all.model.data.Tag.BUILDING_TAG;
import static io.github.data4all.model.data.Tag.NODE_TAG;
import static io.github.data4all.model.data.Tag.WAY_TAG;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.text.InputType;

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
     * list of all classified and unclassified tags.
     */
    public static final List<Tag> TAG_LIST = new ArrayList<Tag>();

    /**
     * list of all address tags.
     */
    public static final List<Tag> ADDRESS_TAG_LIST = new ArrayList<Tag>();

    /**
     * list of all contact tags.
     */
    public static final List<Tag> contactTagList = new ArrayList<Tag>();

    private Tags() {

    }

    /**
     * Returns a Tag with the passed id.
     * 
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
     * which are relevant for relation objects.
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
                        if (osmObjects[i] == Tag.AREA_TAG) {
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
     * 
     * @return list of all classified tags
     */
    public static List<ClassifiedTag> getAllClassifiedTags() {
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
                        if (osmObjects[i] == Tag.NODE_TAG) {
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
     * which are relevant for relation objects.
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
                        if (osmObjects[i] == Tag.BUILDING_TAG) {
                            result.add(t);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * returns an arraylist containing all classified and unclassified tags.
     * 
     * @return tagList
     */
    public static List<Tag> getAllTags() {
        return TAG_LIST;
    }

    /**
     * returns an arraylist containing all classified and unclassified tags
     * which are relevant for way objects.
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
                        if (osmObjects[i] == Tag.WAY_TAG) {
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
        ADDRESS_TAG_LIST.add(new Tag(1, "addr:street",
                InputType.TYPE_CLASS_TEXT, NODE_TAG, WAY_TAG, BUILDING_TAG,
                AREA_TAG));
        ADDRESS_TAG_LIST.add(new Tag(2, "addr:housenumber",
                InputType.TYPE_CLASS_NUMBER, NODE_TAG, WAY_TAG, BUILDING_TAG,
                AREA_TAG));
        ADDRESS_TAG_LIST.add(new Tag(3, "addr:postcode",
                InputType.TYPE_CLASS_NUMBER, NODE_TAG, WAY_TAG, BUILDING_TAG,
                AREA_TAG));
        ADDRESS_TAG_LIST.add(new Tag(4, "addr:city", InputType.TYPE_CLASS_TEXT,
                NODE_TAG, WAY_TAG, BUILDING_TAG, AREA_TAG));
        ADDRESS_TAG_LIST.add(new Tag(5, "addr:country",
                InputType.TYPE_CLASS_TEXT, NODE_TAG, WAY_TAG, BUILDING_TAG,
                AREA_TAG));
    }

    /**
     * fills the contactTagList arraylist with contact tags.
     */
    static {
        // contact tags
        contactTagList.add(new Tag(6, "contact:phone",
                InputType.TYPE_CLASS_PHONE, NODE_TAG, BUILDING_TAG));
        contactTagList.add(new Tag(7, "contact:fax",
                InputType.TYPE_CLASS_PHONE, NODE_TAG, BUILDING_TAG));
        contactTagList.add(new Tag(8, "contact:website",
                InputType.TYPE_CLASS_TEXT, NODE_TAG, BUILDING_TAG));
        contactTagList.add(new Tag(9, "contact:email",
                InputType.TYPE_CLASS_TEXT, NODE_TAG, BUILDING_TAG));
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
        final String highway = "highway";
        final List<ClassifiedValue> highwayValues =
                new LinkedList<ClassifiedValue>();
        highwayValues.add(new ClassifiedValue(101, highway, "residential"));
        highwayValues.add(new ClassifiedValue(102, highway, "service"));
        highwayValues.add(new ClassifiedValue(103, highway, "track"));
        highwayValues.add(new ClassifiedValue(104, highway, "footway"));
        highwayValues.add(new ClassifiedValue(105, highway, "path"));
        highwayValues.add(new ClassifiedValue(106, highway, "motorway"));
        highwayValues.add(new ClassifiedValue(106, highway, "road"));
        TAG_LIST.add(new ClassifiedTag(10, "highway", -1, highwayValues,
                WAY_TAG));

        // classified tag: barrier
        final String barrier = "barrier";
        final List<ClassifiedValue> barrierValues =
                new LinkedList<ClassifiedValue>();
        barrierValues.add(new ClassifiedValue(107, barrier, "fence"));
        barrierValues.add(new ClassifiedValue(108, barrier, "wall"));
        barrierValues.add(new ClassifiedValue(109, barrier, "gate"));
        barrierValues.add(new ClassifiedValue(110, barrier, "bollard"));
        barrierValues.add(new ClassifiedValue(111, barrier, "citywall"));
        TAG_LIST.add(new ClassifiedTag(11, "barrier", -1, barrierValues,
                NODE_TAG, WAY_TAG, AREA_TAG));

        // classified tag: amenity
        final String amenity = "amenity";
        final List<ClassifiedValue> amenityValues =
                new LinkedList<ClassifiedValue>();
        amenityValues.add(new ClassifiedValue(112, amenity, "parking"));
        amenityValues.add(new ClassifiedValue(113, amenity, "school"));
        amenityValues.add(new ClassifiedValue(114, amenity, "restaurant"));
        amenityValues.add(new ClassifiedValue(115, amenity, "bench"));
        amenityValues.add(new ClassifiedValue(116, amenity, "fuel"));
        amenityValues.add(new ClassifiedValue(117, amenity, "bank"));
        amenityValues.add(new ClassifiedValue(118, amenity, "fast_food"));
        amenityValues.add(new ClassifiedValue(119, amenity, "cafe"));
        amenityValues.add(new ClassifiedValue(120, amenity, "pharmacy"));
        amenityValues.add(new ClassifiedValue(121, amenity, "hospital"));
        amenityValues.add(new ClassifiedValue(122, amenity, "post_office"));
        amenityValues.add(new ClassifiedValue(123, amenity, "pub"));
        amenityValues.add(new ClassifiedValue(124, amenity, "public_building"));
        amenityValues.add(new ClassifiedValue(125, amenity, "toilets"));
        amenityValues.add(new ClassifiedValue(126, amenity, "bar"));
        amenityValues.add(new ClassifiedValue(127, amenity, "fire_station"));
        amenityValues.add(new ClassifiedValue(128, amenity, "police"));
        amenityValues.add(new ClassifiedValue(129, amenity, "library"));
        amenityValues.add(new ClassifiedValue(130, amenity, "university"));
        amenityValues.add(new ClassifiedValue(131, amenity, "college"));
        amenityValues.add(new ClassifiedValue(132, amenity, "marketplace"));
        amenityValues.add(new ClassifiedValue(133, amenity, "taxi"));
        amenityValues.add(new ClassifiedValue(134, amenity, "cinema"));
        amenityValues.add(new ClassifiedValue(135, amenity, "embassy"));
        amenityValues.add(new ClassifiedValue(136, amenity, "water_point"));
        TAG_LIST.add(new ClassifiedTag(12, "amenity", -1, amenityValues,
                new int[] {NODE_TAG, BUILDING_TAG}));

        // classified tag: building
        final String building = "building";
        final List<ClassifiedValue> buildingValues =
                new LinkedList<ClassifiedValue>();
        buildingValues.add(new ClassifiedValue(137, building, "house"));
        buildingValues.add(new ClassifiedValue(138, building, "residential"));
        buildingValues.add(new ClassifiedValue(139, building, "garage"));
        buildingValues.add(new ClassifiedValue(140, building, "apartments"));
        buildingValues.add(new ClassifiedValue(141, building, "industrial"));
        buildingValues.add(new ClassifiedValue(142, building, "commercial"));
        buildingValues.add(new ClassifiedValue(143, building, "retail"));
        TAG_LIST.add(new ClassifiedTag(13, "building", -1, buildingValues,
                new int[] {BUILDING_TAG}));

        // classified tag: landuse
        final String landuse = "landuse";
        final List<ClassifiedValue> landuseValues =
                new LinkedList<ClassifiedValue>();
        landuseValues.add(new ClassifiedValue(144, landuse, "forest"));
        landuseValues.add(new ClassifiedValue(145, landuse, "residential"));
        landuseValues.add(new ClassifiedValue(146, landuse, "grass"));
        landuseValues.add(new ClassifiedValue(147, landuse, "farmland"));
        landuseValues.add(new ClassifiedValue(148, landuse, "industrial"));
        landuseValues.add(new ClassifiedValue(149, landuse, "commercial"));
        landuseValues.add(new ClassifiedValue(150, landuse, "construction"));
        landuseValues.add(new ClassifiedValue(151, landuse, "millitary"));
        TAG_LIST.add(new ClassifiedTag(14, "landuse", -1, landuseValues,
                new int[] {AREA_TAG}));

    }

}