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
import java.util.LinkedHashMap;
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
    /**
     * Gives a list of translated Classified Tags
     * 
     * @param type is the Type for Area,Node,Way,Building
     * @param res The Ressource
     * @return The String Array with the right ressource name
     */
    public static String[] getArrayKeys(int type, Resources res) {
        List<Tag> tags = getKeys(type);

        final String[] array = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            array[i] = res.getString(tags.get(i).getNameRessource());
        }
        return array;
    }
    /**
     * 
     * @param type is the Type for Area,Node,Way,Building
     * @param res The Ressource
     * @return A Map with the translated Tags and the Tag
     */
    public static Map<String, ClassifiedTag> getMapKeys(int type, Resources res) {
        final Map<String, ClassifiedTag> map = new HashMap<String, ClassifiedTag>();
        for (int i = 0; i < getKeys(type).size(); i++) {
            map.put(res.getString(getKeys(type).get(i).getNameRessource()),
                    (ClassifiedTag) getKeys(type).get(i));
        }
        return map;
    }
    /**
     * Get a Map with all UnclassifiedTags with their Strings
     * 
     * @param res The Ressource
     * @return a Map with the unclassifiedMaps and their translated String
     */
    public static Map<String, Tag> getUnclassifiedMapKeys(
             Resources res) {
        final Map<String, Tag> map = new LinkedHashMap<String, Tag>();
        final List <Tag> list = new ArrayList<Tag>();
        list.addAll(Tags.getAllAddressTags());
        list.addAll(Tags.getAllContactTags());
        for (Tag tag : list) {
			map.put(res.getString(tag.getNameRessource()), tag);
		}
        return map;
    }

    /**
     * Gives all the Address to the Hashmap for the Element
     * 
     * @param addressTags the List of String
     * @param map
     * @return
     */
    public static Map<Tag, String> addressToTag(List<String> addressTags,
            Map<Tag, String> map) {
        ArrayList<Tag> tag;
        tag = (ArrayList<Tag>) Tags.getAllAddressTags();
        for (int i = 0; i < tag.size(); i++) {
            if (!addressTags.get(i).equals("")) {
                map.put(tag.get(i), addressTags.get(i));
            }
        }
        return map;
    }

    public static Map<Tag, String> contactToTag(List<String> contactTags,
            Map<Tag, String> map) {
        ArrayList<Tag> tag;
        tag = (ArrayList<Tag>) Tags.getAllContactTags();
        for (int i = 0; i < tag.size(); i++) {
            if (!contactTags.get(i).equals("")) {
                map.put(tag.get(i), contactTags.get(i));
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
    
    
    public static List <Tag> getAllNonSelectedTags(Map <Tag, String> map, int type){
    	List <Tag> tagList = new ArrayList<Tag>();
    	tagList.addAll(Tags.getAllAddressTags());
		 if (Tagging.isContactTags(Tags.getAllContactTags().get(0)
	                .getOsmObjects(), type)){
			 tagList.addAll(Tags.getAllContactTags());
		 }
		for (Entry entry : map.entrySet()) {
            Tag tag = (Tag) entry.getKey();
            if(tagList.contains(tag)){
            	tagList.remove(tag);
            }
        }
		Log.i(TAG, tagList.toString());
		return tagList;
    	
    }
    public static String [] TagsToStringRes (List<Tag> list, Resources res){
    	 String[] resList = new String[list.size()];
    	for (int i = 0; i < list.size(); i++) {
			resList [i] = res.getString(list.get(i).getNameRessource());
		}
		return resList;
    	
    }

}
