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
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
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
        Map<String, String> map = new HashMap<String, String>();
        ArrayList<ClassifiedTag> tagData = new ArrayList<ClassifiedTag>();
        Tags tags = new Tags();
        tagData = tags.getAllClassifiedTags();
        for (ClassifiedTag entry : tagData) {
            String key = (String) entry.getKey();
            // split is the Array from the Key Values
            if (compareStringTag(entry.getClassifiedValues(), matchesText) != null) {
                map.put(key,
                        compareStringTag(entry.getClassifiedValues(),
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
     * @param list
     * @param matchesText
     * @return the String that matches with the tagValue
     */
    private static String compareStringTag(List<String> list,
            List<String> matchesText) {
        for (int i = 0; i < matchesText.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                // Compares the String of matchesText with split
                if (matchesText.get(i).equalsIgnoreCase(list.get(j))) {
                    return list.get(j);
                }
            }
        }
        return null;
    }

}
