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
package io.github.data4all.util;

import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Maurice Boyke
 *
 */
public abstract class Tagging {
    /**
     * 
     * @return the Keys of Tags
     */
    public static List<Tag> getKeys(int type) {
        switch (type) {
        case 1:
            return Tags.getAllNodeTags();
        case 2:
            return Tags.getAllWayTags();
        case 3:
            return Tags.getAllRelationTags();
        case 4:
            return Tags.getAllAreaTags();
        }
        return null;

    }

    public static String[] getArrayKeys(int type) {
        String[] array = new String[getKeys(type).size()];
        for (int i = 0; i < getKeys(type).size(); i++) {
            array[i] = getKeys(type).get(i).getKey();
        }

        return array;

    }

    public static Map<String, ClassifiedTag> getMapKeys(int type) {
        Map<String, ClassifiedTag> map = new HashMap<String, ClassifiedTag>();
        for (int i = 0; i < getKeys(type).size(); i++) {
            map.put(getKeys(type).get(i).getKey(),
                    (ClassifiedTag) getKeys(type).get(i));
        }

        return map;

    }

    /**
     * 
     * @param key
     *            the Key of the Hashmap
     * @return the Values of the Key
     */
    public static List<String> getValues(String key) {
        Map<String, String> map = new HashMap<String, String>();
        // map = Tags.getClassifiedTags();
        String[] split;
        split = map.get(key).split(",");
        List<String> list = new ArrayList<String>(Arrays.asList(split));
        return list;
    }

    /**
     * 
     * @param key
     * @param value
     * @return a HashMap with the Keys and Values
     */
    public static Map<String, String> hashMapTag(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }

    public static Map<String, String> addressToTag(List<String> addressTags,
            Map<String, String> map) {
        ArrayList<Tag> tag;
        tag = Tags.getAllAddressTags();
        for (int i = 0; i < tag.size(); i++) {
            final String value = addressTags.get(i);
            //make sure that the new Tag contains all informations for last tag
            if (!value.equals("")) {
                final Tag tagActual = tag.get(i);
                map.put(tagActual.getKey(), value);
                tagActual.setLastValue(value);
            }
        }
        return map;

    }

    public static Map<String, String> contactToTag(List<String> contactTags,
            Map<String, String> map) {
        ArrayList<Tag> tag;
        tag = Tags.getAllContactTags();
        for (int i = 0; i < tag.size(); i++) {
            final String value = contactTags.get(i);
            //make sure that the new Tag contains all informations for last tag
            if (!value.equals("")) {
                final Tag tagActual = tag.get(i);
                map.put(tagActual.getKey(), value);
                tagActual.setLastValue(value);
            }
        }
        return map;

    }

    public static Boolean isClassifiedTag(String key, CharSequence[] array) {

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(key)) {
                return true;
            }

        }
        return false;
    }

}
