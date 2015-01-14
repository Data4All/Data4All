package io.github.data4all.util;

import io.github.data4all.model.data.Tags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Maurice Boyke
 *
 */
public abstract class SpeechRecognition {

    /**
     * 
     * @param matchesText
     *            is a Array List from the results of the SpeechRecognition
     * @return The HashMap of the matching Tags
     */
    public static Map<String, String> speechToTag(List<String> matchesText) {
    	Map<String, String> map = new HashMap <String, String>();
		Map<String, String> tagData = new HashMap<String, String>();
		Tags tags = new Tags();
		tagData = tags.getClassifiedTags();
		for(Entry entry : tagData.entrySet()){
			String key = (String) entry.getKey();
			// split is the Array from the Key Values
			String [] split = tagData.get(key).split(",");
			if(compareStringTag(split, matchesText) != null){
				map.put(key, compareStringTag(split, matchesText));
				break;
			}
		}
		return map;
    }

    /**
     * splitString splits all the Strings and adds them to the ArrayList
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
	  * It Compares the list of the Google Speechrecognition and the array of key Values
	  * @param tag
	  * @param matchesText
	  * @return the String that matches with the tagValue
	  */
	 private static String compareStringTag(String[] tag, List<String> matchesText){
		 for (int i = 0; i < matchesText.size(); i++) {
				for (int j = 0; j < tag.length; j++) {
					// Compares the String of matchesText with split 
					if(matchesText.get(i).equalsIgnoreCase(tag[j])){
						return tag[j];
					}	 
				}
		 	}
		return null;
	 }

}
