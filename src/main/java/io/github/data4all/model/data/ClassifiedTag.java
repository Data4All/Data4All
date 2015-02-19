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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.util.List;

/**
 * This class represents a classified tag. A classified tag is a subclass of
 * tag. classifiedTags are tags where the user can choose input/value from
 * listed values.
 * 
 * @author fkirchge, Steeve
 *
 */
public class ClassifiedTag extends Tag {

    /**
     * stores all classified values for the specific tag.
     */
    private List<String> classifiedValues;


    /**
     * Default constructor.
     * 
     * @param id The id of the tag
     * @param key The key of the tag
     * @param type The Type of the tag
     * @param classifiedValues the classifiedValues of the tag
     * @param osmObjects The OpenStreetObject the tag refers to
     */
    public ClassifiedTag(int id, String key, InputType type,
            List<String> classifiedValues, int... osmObjects) {
        super(id, key, type, osmObjects);
        this.classifiedValues = classifiedValues;
        lastChoice = null;
       
    }

    /**
     * @return classifiedValues or classifiedValuesSuggestion
     * 
     *         when the list of suggestion Values is empty , then get the
     *         classifiedValues when not then get classifiedValuesSuggestion
     * 
     */
    public List<String> getClassifiedValues() {
        if (lastChoice == null) {
            return classifiedValues;
        }
        return lastChoice.getClassifiedValues();
    }

    /**
     * @param classifiedValues
     * 
     */

    public void setClassifiedValues(List<String> classifiedValues) {
        this.classifiedValues = classifiedValues;
    }

  

    /**
     * @param suggestion
     *    lastChoice is a classifiedTag
     *    it would created, when user mapping a object which belong to the same Category
     *    as the last object.
     *    when lastChoice is null, then create a new classifiedTag and add the suggestion
     *    when lastChoice is not null then update suggestion.
     *          
     */
    public void addSuggestion(String suggestion) {

        // create a tag with this suggestion
          lastChoice = Tags.getLastChoice();
        if (lastChoice == null) {
            lastChoice = new ClassifiedTag(getHintRessource(), "Last Choice", getType(),
                    Arrays.asList(suggestion), getOsmObjects());
            Tags.TAG_LIST.add(lastChoice);
        } else {
            lastChoice.setClassifiedValues(Arrays.asList(suggestion));
            lastChoice.setType(getType());
            lastChoice.setOsmObjects(getOsmObjects());

        }
        if ("Last Choice".equalsIgnoreCase(getKey())) {
            lastChoice.setOriginKey(getOriginKey());
        } else {
            lastChoice.setOriginKey(getKey());
        }

    }

    /**
     * @return classifiedValues 
     *                     get all classifiedValues
     * 
     */
    public List<String> getAllClassifiedValues() {
        return classifiedValues;
    }

}
