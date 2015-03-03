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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.util.Log;

/**
 * @author Maurice Boyke
 *
 */
public final class SpeechRecognition {

	
    // The logger
    private static final String TAG = "Tagging";
    /**
     * Private Constructor, prevents instantiation.
     */
    private SpeechRecognition() {

    }

    /**
     * 
     * @param matchesText
     *            is a Array List from the results of the SpeechRecognition
     * @param k 
     * @return The HashMap of the matching Tags
     */
    public static Map<Tag, String> speechToTag(List<String> matchesText, int k, Resources res) {
    	Map<Tag, String> map = new LinkedHashMap<Tag, String>();
    	List<Tag> list = new ArrayList<Tag>();
    	list = Tagging.getKeys(k);
    	Log.i(TAG, list.toString());
    	for (Tag classifiedTag : list) {
    		List<ClassifiedValue> tags = new ArrayList<ClassifiedValue>();
    		tags = ((ClassifiedTag) classifiedTag).getClassifiedValues();
    		Log.i(TAG, tags.toString());
    		if(compareStringTag(tags, matchesText, res) != null){
    			map.put(classifiedTag, compareStringTag(tags, matchesText, res));
    		}
		}
		return map;
    	
    }; 
     
    /**
     * splitString splits all the Strings and adds them to the ArrayList.
     * 
     * @param matchesText
     *            is a Array List from the results of the SpeechRecognition
     */
    public static void splitStrings(List<String> matchesText) {
        for (int j = 0; j < matchesText.size(); j++) {
            String[] split;
            split = matchesText.get(j).split(" ");
            for (int i = 0; i < split.length; i++) {
                if (!matchesText.contains(split[i])) {
                    matchesText.add(split[i]);
                }
            }
        }
    }

    /**
     * It Compares the list of the Google Speechrecognition and the array of key
     * Values.
     * 
     * @param arrayList
     *            The List of ClassifiedValues to compare with
     * @param matchesText
     *            The List of String to compare with
     * @return the String that matches with the tagValue
     */
    private static String compareStringTag(List<ClassifiedValue> arrayList,
            List<String> matchesText, Resources res) {
        for (int i = 0; i < matchesText.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                // Compares the String of matchesText with split
                if (matchesText.get(i).equalsIgnoreCase(res.getString(arrayList.get(j).getNameRessource()))) {
                    return arrayList.get(j).getValue();
                }
            }
        }
        return null;
    }
}
