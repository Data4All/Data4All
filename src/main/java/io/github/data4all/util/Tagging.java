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
        default:
        	break;
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
     * Get all unclassified Tags
     * @param value The ClassifiedValue
     * @param res The Ressource
     * @param keyList The List of Strings in which they are stored
     * @return The keyList with the new Values
     */
    public static List <String> getUnclassifiedTags(ClassifiedValue value, Context context, List <String> keyList, List <Tag> unclassifiedTags, AbstractDataElement element){
    	List <Tag> tagList = new ArrayList<Tag>();
    	tagList.addAll(Tags.getAllAddressTags());
    	tagList.addAll(Tags.getAllContactTags());
    	List <Boolean> booleanList = new ArrayList<Boolean>();
    	booleanList = value.getAllUnclassifiedBooleans();
    	for (int i = 0; i < booleanList.size(); i++) {
			if(booleanList.get(i) && !compareStrings(tagList.get(i).getNamedKey(context) ,keyList) ){
				keyList.add(tagList.get(i).getNamedKey(context));
				unclassifiedTags.add(tagList.get(i));
			} else if(!booleanList.get(i) && element.getTags().containsKey(tagList.get(i))) {
				element.removeTag(tagList.get(i));
			}
		}
    	return keyList;
    }
    
    public static List <String> addUnclassifiedValue(AbstractDataElement element, List<String> endList, List<Tag> unclassifiedTags, Context context) {
    	for (int i = 0; i < unclassifiedTags.size(); i++) {
			if(element.getTags().containsKey(unclassifiedTags.get(i))){
				endList.add(element.getTagValueWithKey(unclassifiedTags.get(i)));
			} else {
				endList.add("");
			}
		}
		return endList;
    	
    }
    
    private static boolean compareStrings(String key, List<String> list){
    	for (String string : list) {
			if(string.equals(key)){
				return true;
			}
		}
    	return false;
    }
 
    public static ClassifiedTag getClassifiedTagKey(AbstractDataElement element){
    	if(!element.getTags().isEmpty()){
    		return (ClassifiedTag) element.getTags().keySet().toArray()[0];
    	}
    	return null;    	
    }
    
    public static ClassifiedValue getClassifiedValue(AbstractDataElement element, ClassifiedTag tag){
    	String value = element.getTags().get(tag);
    	List<ClassifiedValue> classifiedValues = tag.getClassifiedValues();
    	for (int i = 0; i < classifiedValues.size(); i++) {
    		Log.i(TAG, value);
    		Log.i(TAG, classifiedValues.get(i).getValue());
			if(value.equals(classifiedValues.get(i).getValue())){
				return classifiedValues.get(i);
			}
		}
    	return null;
    }


}
