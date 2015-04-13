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

import io.github.data4all.Data4AllApplication;
import io.github.data4all.R;
import io.github.data4all.logger.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.text.InputType;

/**
 * List of tags for osm elements unclassifiedTags: user have to input text
 * manually to set the value / classifiedTags: user can choose the input from
 * listed values.
 * 
 * @author fkirchge, optimized by tbrose
 *
 */
public final class Tags {
    
    /**
     * Logger.
     */
    public static String LOG_TAG = Tags.class.getSimpleName();
    /**
     * Resource to the key properties.
     */
    public static int RESOURCE_TAG_KEYS = R.raw.tag_keys;

    /***
     * Resource to the value properties.
     */
    public static int RESOURCE_TAG_VALUES = R.raw.tag_values;

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
    public static final List<Tag> CONTACT_TAG_LIST = new ArrayList<Tag>();

    /**
     * Private constructor.
     */
    private Tags() {

    }

    /**
     * Reads all Tags from the properties.
     * 
     * @throws IOException
     */
    private static void readTags() throws IOException {
        for (int id : readKeys().keySet()) {
            String key = readKeys().get(id);
            TAG_LIST.add(createClassifiedTag(id, key));
        }
    }

    /**
     * Creates a new {@link ClassifiedTag} object loades all
     * {@link ClassifiedValue} refering to the {@link ClassifiedTag} from the
     * properties.
     * 
     * @param id
     *            id of the {@link ClassifiedTag}.
     * @param key
     *            key of the {@link ClassifiedTag}.
     * @return {@link Tag} object
     * @throws IOException
     */
    private static Tag createClassifiedTag(int id, String key)
            throws IOException {
        final List<ClassifiedValue> classifiedValues =
                new LinkedList<ClassifiedValue>();
        for (String[] s : readValues()) {
            if (s.length == 16) {
                if (s[2].equals(Integer.toString(id))) {
                    ClassifiedValue cv =
                            new ClassifiedValue(Integer.parseInt(s[0]), key,
                                    s[1]);
                    cv.setCanBeNode(Boolean.parseBoolean(s[3]));
                    cv.setCanBeWay(Boolean.parseBoolean(s[4]));
                    cv.setCanBeArea(Boolean.parseBoolean(s[5]));
                    cv.setCanBeBuilding(Boolean.parseBoolean(s[6]));
                    cv.setHasAddrStreet(Boolean.parseBoolean(s[7]));
                    cv.setHasAddrHousnumber(Boolean.parseBoolean(s[8]));
                    cv.setHasAddrPostcode(Boolean.parseBoolean(s[9]));
                    cv.setHasAddrCity(Boolean.parseBoolean(s[10]));
                    cv.setHasAddrCountry(Boolean.parseBoolean(s[11]));
                    cv.setHasContactPhone(Boolean.parseBoolean(s[12]));
                    cv.setHasContactFax(Boolean.parseBoolean(s[13]));
                    cv.setHasContactWebsite(Boolean.parseBoolean(s[14]));
                    cv.setHasContactEmail(Boolean.parseBoolean(s[15]));
                    classifiedValues.add(cv);
                }
            }
        }
        ClassifiedTag tag = new ClassifiedTag(id, key, -1, classifiedValues);
        return tag;
    }

    /**
     * Reads all key from the file /res/raw/tag_keys.txt.
     * 
     * @return {@link Map} with the id of the key and the value.
     * @throws IOException
     */
    private static Map<Integer, String> readKeys() throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(
                        Data4AllApplication.context.getResources()
                                .openRawResource(RESOURCE_TAG_KEYS)));
        Map<Integer, String> keys = new HashMap<Integer, String>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] key;
            key = line.split(",");
            keys.put(Integer.parseInt(key[0]), key[1]);
        }
        reader.close();
        return keys;
    }

    /**
     * Reads all values from the file /res/raw/tag_values.txt.
     * 
     * @return {@link List} of values.
     * @throws IOException
     */
    private static List<String[]> readValues() throws IOException {
        List<String[]> values = new ArrayList<String[]>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(
                        Data4AllApplication.context.getResources()
                                .openRawResource(RESOURCE_TAG_VALUES)));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] value;
            value = line.split(",");
            values.add(value);
        }
        reader.close();
        return values;
    }

    /**
     * Returns a Tag with the passed id.
     * 
     * @param id
     *            The ID of the Tag.
     * @return tag object
     */
    public static Tag getTagWithId(int id) {
        for (Tag t : TAG_LIST) {
            if (t.getId() == id) {
                Log.d(LOG_TAG, "getTagWithId() return new tag with id: " + t.getId());
                return new Tag(t.getId(), t.getKey(), t.getType());
            }
            if (t instanceof ClassifiedTag) {
                for (ClassifiedValue v : ((ClassifiedTag) t)
                        .getClassifiedValues()) {
                    if (v.getId() == id) {
                        Log.d(LOG_TAG,
                                "getTagWithId() return new classified tag with id: "
                                        + v.getId());
                        return new ClassifiedTag(v.getId(), v.getKey(),
                                ((ClassifiedTag) t).getType(),
                                ((ClassifiedTag) t).getClassifiedValues());
                    }
                }
            }
        }
        Log.d(LOG_TAG, "getTagWithId() could not find tag with id: " + id);
        return null;
    }


    /**
     * returns an ArrayList containing all classified and unclassified tags
     * which are relevant for node objects.
     * 
     * @return tagList
     */
    public static List<Tag> getAllNodeTags() {
        List<Tag> result = new ArrayList<Tag>();
        for (Tag t : TAG_LIST) {
            List<ClassifiedValue> classifiedValues =
                    new ArrayList<ClassifiedValue>();
            if (t instanceof ClassifiedTag) {
                ClassifiedTag ct = (ClassifiedTag) t;
                for (ClassifiedValue v : ((ClassifiedTag) t)
                        .getClassifiedValues()) {
                    if (v.canBeNode()) {
                        classifiedValues.add(v);
                    }
                }
            }
            if (!classifiedValues.isEmpty()) {
                result.add(new ClassifiedTag(t.getId(), t.getKey(),
                        t.getType(), classifiedValues));
            }
        }
        return result;
    }

    /**
     * returns an ArrayList containing all classified and unclassified tags
     * which are relevant for way objects.
     * 
     * @return tagList
     */
    public static List<Tag> getAllWayTags() {
        List<Tag> result = new ArrayList<Tag>();
        for (Tag t : TAG_LIST) {
            List<ClassifiedValue> classifiedValues =
                    new ArrayList<ClassifiedValue>();
            if (t instanceof ClassifiedTag) {
                for (ClassifiedValue v : ((ClassifiedTag) t)
                        .getClassifiedValues()) {
                    if (v.canBeWay()) {
                        classifiedValues.add(v);
                    }
                }
            }
            if (!classifiedValues.isEmpty()) {
                result.add(new ClassifiedTag(t.getId(), t.getKey(),
                        t.getType(), classifiedValues));
            }
        }
        return result;
    }

    /**
     * returns an ArrayList containing all classified and unclassified tags
     * which are relevant for relation objects.
     * 
     * @return tagList
     */
    public static List<Tag> getAllAreaTags() {
        List<Tag> result = new ArrayList<Tag>();
        for (Tag t : TAG_LIST) {
            List<ClassifiedValue> classifiedValues =
                    new ArrayList<ClassifiedValue>();
            if (t instanceof ClassifiedTag) {
                for (ClassifiedValue v : ((ClassifiedTag) t)
                        .getClassifiedValues()) {
                    if (v.canBeArea()) {
                        classifiedValues.add(v);
                    }
                }
            }
            if (!classifiedValues.isEmpty()) {
                result.add(new ClassifiedTag(t.getId(), t.getKey(),
                        t.getType(), classifiedValues));
            }
        }
        return result;
    }

    /**
     * returns an ArrayList containing all classified and unclassified tags
     * which are relevant for relation objects.
     * 
     * @return tagList
     */
    public static List<Tag> getAllBuildingTags() {
        List<Tag> result = new ArrayList<Tag>();
        for (Tag t : TAG_LIST) {
            List<ClassifiedValue> classifiedValues =
                    new ArrayList<ClassifiedValue>();
            if (t instanceof ClassifiedTag) {
                for (ClassifiedValue v : ((ClassifiedTag) t)
                        .getClassifiedValues()) {
                    if (v.canBeBuilding()) {
                        classifiedValues.add(v);
                    }
                }
            }
            if (!classifiedValues.isEmpty()) {
                result.add(new ClassifiedTag(t.getId(), t.getKey(),
                        t.getType(), classifiedValues));
            }
        }
        return result;
    }

    /**
     * returns an ArrayList containing all classified and unclassified tags.
     * 
     * @return tagList
     */
    public static List<Tag> getAllTags() {
        return TAG_LIST;
    }

    /**
     * Returns all classified tags.
     * 
     * @return list of all classified tags
     */
    public static List<ClassifiedTag> getAllClassifiedTags() {
        List<ClassifiedTag> ct = new ArrayList<ClassifiedTag>();
        for (Tag t : TAG_LIST) {
            if (t instanceof ClassifiedTag) {
                ct.add((ClassifiedTag) t);
            }
        }
        return ct;
    }

    /**
     * method to print list of tags in the log for debug purpose.
     * 
     * @param tags
     *            list of {@link Tag} objects.
     */
    public static void printTags(List<Tag> tags) {
        for (Tag t : tags) {
            if (t instanceof ClassifiedTag) {
                ClassifiedTag ct = (ClassifiedTag) t;
                for (ClassifiedValue v : ct.getClassifiedValues()) {
                    Log.d("TAG", "classified: " + " key : " + v.getKey()
                            + " value: " + v.getValue());
                }
            } else {
                Log.d("TAG", "else");
            }
        }
    }

    /**
     * fills the ADDRESS_TAG_LIST ArrayList with address tags.
     */
    static {
        ADDRESS_TAG_LIST.add(new Tag(401, "addr:street",
                InputType.TYPE_CLASS_TEXT));
        ADDRESS_TAG_LIST.add(new Tag(402, "addr:housenumber",
                InputType.TYPE_CLASS_NUMBER));
        ADDRESS_TAG_LIST.add(new Tag(403, "addr:postcode",
                InputType.TYPE_CLASS_NUMBER));
        ADDRESS_TAG_LIST.add(new Tag(404, "addr:city",
                InputType.TYPE_CLASS_TEXT));
        ADDRESS_TAG_LIST.add(new Tag(405, "addr:country",
                InputType.TYPE_CLASS_TEXT));
    }

    /**
     * fills the CONTACT_TAG_LIST ArrayList with contact tags.
     */
    static {
        CONTACT_TAG_LIST.add(new Tag(406, "contact:phone",
                InputType.TYPE_CLASS_PHONE));
        CONTACT_TAG_LIST.add(new Tag(407, "contact:fax",
                InputType.TYPE_CLASS_PHONE));
        CONTACT_TAG_LIST.add(new Tag(408, "contact:website",
                InputType.TYPE_CLASS_TEXT));
        CONTACT_TAG_LIST.add(new Tag(409, "contact:email",
                InputType.TYPE_CLASS_TEXT));
    }

    /**
     * returns an ArrayList containing all address tags.
     */
    public static List<Tag> getAllAddressTags() {
        return ADDRESS_TAG_LIST;
    }

    /**
     * returns an ArrayList containing all contact tags.
     */
    public static List<Tag> getAllContactTags() {
        return CONTACT_TAG_LIST;
    }

    /**
     * Reads the tags from the properties.
     */
    static {
        try {
            TAG_LIST.addAll(ADDRESS_TAG_LIST);
            TAG_LIST.addAll(CONTACT_TAG_LIST);
            readTags();
        } catch (IOException e) {
            Log.e("Tags", "IOException:", e);
        }
    }

}
