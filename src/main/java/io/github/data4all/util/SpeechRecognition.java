package io.github.data4all.util;

import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maurice Boyke
 *
 */
public class SpeechRecognition {
    
    /**
     * Private Constructor, prevents instantiation.
     */
    private SpeechRecognition() {
        
    }

    /**
     * 
     * @param matchesText
     *            is a Array List from the results of the SpeechRecognition
     * @return The HashMap of the matching Tags
     */
    public static Map<String, String> speechToTag(List<String> matchesText) {
        final Map<String, String> map = new HashMap<String, String>();
        List<ClassifiedTag> tagData = new ArrayList<ClassifiedTag>();
        tagData = (ArrayList<ClassifiedTag>) Tags.getAllClassifiedTags();
        for (ClassifiedTag entry : tagData) {
            final String key = (String) entry.getKey();
            // split is the Array from the Key Values
            if (compareStringTag(
                    (ArrayList<String>) entry.getClassifiedValues(),
                    matchesText) != null) {
                map.put(key,
                        compareStringTag(
                                (ArrayList<String>) entry.getClassifiedValues(),
                                matchesText));
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
     * It Compares the list of the Google Speechrecognition and the array of key
     * Values
     * 
     * @param arrayList
     * @param matchesText
     * @return the String that matches with the tagValue
     */
    private static String compareStringTag(List<String> arrayList,
            List<String> matchesText) {
        for (int i = 0; i < matchesText.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                // Compares the String of matchesText with split
                if (matchesText.get(i).equalsIgnoreCase(arrayList.get(j))) {
                    return arrayList.get(j);
                }
            }
        }
        return null;
    }
}
