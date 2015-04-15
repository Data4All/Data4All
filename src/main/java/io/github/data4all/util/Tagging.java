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

import io.github.data4all.handler.LastChoiceHandler;
import io.github.data4all.model.data.AbstractDataElement;
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

import android.content.Context;
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
    public static String[] getArrayKeys(int type, Context context) {
    	if(getKeys(type) == null){
    		final String [] list = new String [0];
    		return list;
    	}else{
    		
    		List<Tag> tags = getKeys(type);
    		Log.i(TAG, "tags" + tags.toString());
        final String[] array = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
        	array[i] = tags.get(i).getNamedKey(context);
        	
        }

        return LastChoiceHandler.addLastChoiceForType(type,array,context.getResources());
    	}
    }
    /**
     * 
     * @param type is the Type for Area,Node,Way,Building
     * @param res The Ressource
     * @return A Map with the translated Tags and the Tag
     */
    public static Map<String, ClassifiedTag> getMapKeys(int type, Context context) {
        final Map<String, ClassifiedTag> map = new HashMap<String, ClassifiedTag>();
        for (int i = 0; i < getKeys(type).size(); i++) {
            map.put(getKeys(type).get(i).getNamedKey(context),
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
             Context context) {
        final Map<String, Tag> map = new LinkedHashMap<String, Tag>();
        final List <Tag> list = new ArrayList<Tag>();
        list.addAll(Tags.getAllAddressTags());
        list.addAll(Tags.getAllContactTags());
        for (Tag tag : list) {
			map.put(tag.getNamedKey(context), tag);
		}
        return map;
    }

    /**
     * Gives all the AddressTags to the Hashmap for the Element
     * 
     * @param addressTags the List of String from the editText
     * @param map The Hashmap with the Tags
     * @return The Hasmap with all the AddressTags
     */
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
    /**
     * Gives all the ContactTags to the Hashmap for the Element
     * @param contactTags the List of String from the editText
     * @param map The Hashmap with the Tags
     * @return The Hasmap with all the ContactTags
     */

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

    /**
     * Gives true back if the Key is from a Classified Tag
     * 
     * @param key String of the Key 
     * @param res The Ressource
     * @return true if the key is from an classifiedTag
     */
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

    /**
     * Cheks if the type is for Contact Tags
     * @param types list of int
     * @param type the int of the Tag
     * @return true if the Tags has contactTags
     */
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
    /**
     * Gives the Map of Strings and the ClassifiedValues
     * 
     * @param list of ClassifiedValues
     * @param res The Ressource
     * @param international true for the english version
     * @return The Map with the translated or international Veriosn and Tag
     */

    public static Map<String, ClassifiedValue> classifiedValueMap(
            List<ClassifiedValue> list, Context context, boolean international) {
        Map<String, ClassifiedValue> map;
        map = new HashMap<String, ClassifiedValue>();
        for (int i = 0; i < list.size(); i++) {
            if (international) {
                map.put(list.get(i).getValue(), list.get(i));
            } else {
                map.put(list.get(i).getLocalizedName(context), list.get(i));
            }
        }
        return map;

    }

    /**
     * Gives the Array of Translated Strings 
     * @param list of ClassifiedValues
     * @param res The Ressource
     * @return The Array with the String of the ClassifiedValues 
     */
    public static List<String> ClassifiedValueList(List<ClassifiedValue> list,
            Context context) {
        List<String> listValue = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            listValue.add(list.get(i).getLocalizedName(context));
        }
        return listValue;
    }
    
    /**
     * Gives a List of Tags which are not used so far
     * 
     * @param map The Hashmap of all used Tags of the Element
     * @param type The type (area, node, etc.)
     * @return The List of Tags 
     */
    public static List <Tag> getAllNonSelectedTags(Map <Tag, String> map, ClassifiedValue value){
    	List <Tag> tagList = new ArrayList<Tag>();
    	tagList.addAll(Tags.getAllAddressTags());
    	tagList.addAll(Tags.getAllContactTags());
    	List <Boolean> booleanList = new ArrayList<Boolean>();
    	if(value != null){
    	booleanList = value.getAllUnclassifiedBooleans();
    	for (int i = 0; i < tagList.size(); i++) {
			if(!booleanList.get(i)){
				tagList.remove(tagList.get(i));
			}
		}
    	}
		for (Entry entry : map.entrySet()) {
            final Tag tag = (Tag) entry.getKey();
            if(tagList.contains(tag)){
            	tagList.remove(tag);
            }
        }
		Log.i(TAG, tagList.toString());
		return tagList;
    	
    }
    /**
     * Gives the String Array with all the translated TagKeys 
     * @param list list of Tags
     * @param res The Ressource
     * @return An Array of Strings
     */
    public static String [] TagsToStringRes (List<Tag> list, Context context){
    	 String[] resList = new String[list.size()];
    	for (int i = 0; i < list.size(); i++) {
			resList [i] = list.get(i).getNamedKey(context);
		}
		return resList;
    	
    }
    /**
     * Get all unclassified Tags
     * @param value The ClassifiedValue
     * @param res The Ressource
     * @param keyList The List of Strings in which they are stored
     * @param element The Abstract Data Element
     * @return The keyList with the new Values
     */
    public static List <String> getUnclassifiedTags(ClassifiedValue value, Context context, List <String> keyList, AbstractDataElement element){
    	List <Tag> tagList = new ArrayList<Tag>();
    	tagList.addAll(Tags.getAllAddressTags());
    	tagList.addAll(Tags.getAllContactTags());
    	List <Boolean> booleanList = new ArrayList<Boolean>();
    	booleanList = value.getAllUnclassifiedBooleans();
    	for (int i = 0; i < booleanList.size(); i++) {
			if(booleanList.get(i) && !compareStrings(tagList.get(i).getNamedKey(context) ,keyList) ){
				keyList.add(tagList.get(i).getNamedKey(context));
			}
		}
    	return keyList;
    }
    
    private static boolean compareStrings(String key, List<String> list){
    	for (String string : list) {
			if(string.equals(key)){
				return true;
			}
		}
    	return false;
    }
    /**
     * Compares the UnclassifiedTag with the Tags of the classifiedValue
     * @param classValue The classifiedValues
     * @param map The map of the unclassified Tags
     * @return The Map of the right unclassified Tags
     */
    
    public static Map<Tag, String> compareUnclassifiedTags(ClassifiedValue classValue, Map <Tag, String> map){
    	Log.i(TAG, "adasdasd" + map.toString());
    	Log.i(TAG, classValue.toString());
    	List <Tag> tagList = new ArrayList<Tag>();
    	Map <Tag, String> tagMap = new LinkedHashMap<Tag, String>();
    	tagList.addAll(Tags.getAllAddressTags());
    	tagList.addAll(Tags.getAllContactTags());
    	List <Boolean> booleanList = new ArrayList<Boolean>();
    	booleanList = classValue.getAllUnclassifiedBooleans();
    	List <Tag> rightTag = new ArrayList<Tag>();
    	for (int i = 0; i < booleanList.size(); i++) {
			if(booleanList.get(i)){
				rightTag.add(tagList.get(i));
			}
		}
    
    	for (Entry<Tag,String> entry : map.entrySet()) {
    			Log.i(TAG, "keyset of map " + entry.getKey().toString());
    			Log.i(TAG, "BOOLEAN " + compareTags(entry.getKey(), rightTag));
    			
    			if(compareTags(entry.getKey(), rightTag)){
    				tagMap.put(entry.getKey(), map.get(entry.getKey()));
    				
            	}
    		
   	 	}	
    	
    	Log.i(TAG, tagMap.toString());
    	map.clear();
    	map.putAll(tagMap);
		return map;
    	
    }
    
    
    private static boolean compareTags(Tag tag, List<Tag> tags){
    	for (Tag tag2 : tags) {
			if(tag.getKey().equals(tag2.getKey())){
				return true;
			}
		}
    	return false;
    }
    
    public static List <String> addUnclassifiedValue(AbstractDataElement element, List<String> endList, List<String> keyList, Context context) {
    	List <String> tags = new ArrayList<String>();
    	List<Tag> tagKeys = new ArrayList<Tag>();
    	for (Entry<Tag,String> entry : element.getTags().entrySet()) {
            tags.add(entry.getKey().getNamedKey(context));
            tagKeys.add(entry.getKey());
   	 	}     
		for (int i = 1; i < keyList.size(); i++) {
			if(tags.contains(keyList.get(i))){
				
				endList.add(element.getTags().get(tagKeys.get(tags.indexOf(keyList.get(i)))));
			} else {
				endList.add("");
			}
		
		}
		return endList;
    	
    }

}
