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
package io.github.data4all.util;

import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.res.Resources;
import android.util.Log;

/**
 * 
 * 
 * 
 * @author Maurice Boyke
 * 
 *
 */
public class Tagging {
    private static final String TAG = "Tagging";

    /**
     * Private Constructor, prevents instantiation.
     */
    private Tagging() {

    }

    /**
     * 
     * 
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
            return Tags.getAllBuildingTags();
        case 4:
            return Tags.getAllAreaTags();
        }
        return null;
    }

    public static String[] getArrayKeys(int type, Resources res) {
        List<Tag> tags = getKeys(type);

        final String[] array = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            array[i] = res.getString(tags.get(i).getNameRessource());
        }
        return array;
    }

    public static Map<String, ClassifiedTag> getMapKeys(int type, Resources res) {
        final Map<String, ClassifiedTag> map = new HashMap<String, ClassifiedTag>();
        for (int i = 0; i < getKeys(type).size(); i++) {
            map.put(res.getString(getKeys(type).get(i).getNameRessource()),
                    (ClassifiedTag) getKeys(type).get(i));
        }
        return map;
    }

    public static Map<String, Tag> getUnclassifiedMapKeys(
            Map<Tag, String> tagMap, Resources res) {
        final Map<String, Tag> map = new HashMap<String, Tag>();
        for (Entry entry : tagMap.entrySet()) {

            final Tag tagKey = (Tag) entry.getKey();
            Log.i(TAG, "bla" + tagKey.getKey());
            map.put(res.getString(tagKey.getNameRessource()), tagKey);
        }
        return map;
    }

    public static Map<Tag, String> addressToTag(List<String> addressTags,
            Map<Tag, String> map) {
        ArrayList<Tag> tag;
        tag = (ArrayList<Tag>) Tags.getAllAddressTags();
        for (int i = 0; i < tag.size(); i++) {

            final String value = addressTags.get(i);
            //make sure that the new Tag contains all informations for last tag
            if (!value.equals("")) {
                final Tag tagActual = tag.get(i);
                map.put(tagActual, value);
                tagActual.setLastValue(value);
            }
            
        }
        return map;
    }

    public static Map<Tag, String> contactToTag(List<String> contactTags,
            Map<Tag, String> map) {
        ArrayList<Tag> tag;
        tag = (ArrayList<Tag>) Tags.getAllContactTags();
        for (int i = 0; i < tag.size(); i++) {

            final String value = contactTags.get(i);
            //make sure that the new Tag contains all informations for last tag
            if (!value.equals("")) {
                final Tag tagActual = tag.get(i);
                map.put(tagActual, value);
                tagActual.setLastValue(value);
            }
        }
        return map;
    }

    public static Boolean isClassifiedTag(String key, Resources res) {
        List<ClassifiedTag> classifiedTags = Tags.getAllClassifiedTags();
        for (int i = 0; i < classifiedTags.size(); i++) {
            if (res.getString(classifiedTags.get(i).getNameRessource()).equals(
                    key)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isContactTags(int[] types, int type) {
        for (int i = 0; i < types.length; i++) {
            Log.i(TAG, String.valueOf(types[i]));
            Log.i(TAG, String.valueOf(type));
            if (types[i] == type) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, ClassifiedValue> classifiedValueMap(
            List<ClassifiedValue> list, Resources res, boolean international) {
        Map<String, ClassifiedValue> map;
        map = new HashMap<String, ClassifiedValue>();
        for (int i = 0; i < list.size(); i++) {
            if (international) {
                map.put(list.get(i).getValue(), list.get(i));
            } else {
                map.put(res.getString(list.get(i).getNameRessource()),
                        list.get(i));
            }
        }
        return map;

    }

    public static String[] ClassifiedValueList(List<ClassifiedValue> list,
            Resources res) {
        String[] listValue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            listValue[i] = res.getString(list.get(i).getNameRessource());
        }
        return listValue;
    }

}
