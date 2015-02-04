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
package io.github.data4all.model.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

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
     * stores all classified values for the specific tag
     */
    private ArrayList<String> classifiedValues;
    
    /**
     * stores the last classifiedValue Suggestion for the specific tag
     */
    private ArrayList<String> classifiedValuesSuggestion; 
    
    /**
     * Default constructor.
     * 
     * @param key
     * @param type
     * @param classifiedValues
     * @param osmObjects
     */
    public ClassifiedTag(String key, InputType type,
            ArrayList<String> classifiedValues, int... osmObjects) {
        super(key, type, osmObjects);
        this.classifiedValues = classifiedValues;
        classifiedValuesSuggestion = new ArrayList<String>();
    }

    /**
     * when the list of suggestion Values is empty ,
     * then get the classifiedValues
     * when not then get classifiedValuesSuggestion
     * @return classifiedValues or classifiedValuesSuggestion
     */
    public ArrayList<String> getClassifiedValues() {
        if(classifiedValuesSuggestion.isEmpty()){
            return classifiedValues;
        }
        return new ArrayList<String>(classifiedValuesSuggestion);
    }

    /**
     * @param classifiedValues
     * 
     */
    public void setClassifiedValues(ArrayList<String> classifiedValues) {
        this.classifiedValues = classifiedValues;
    }
     
    /**
     * get the list of all classifiedValuesSuggestion
     */
    public ArrayList<String> getClassifiedValuesSuggestion() {
        return classifiedValuesSuggestion;
    }
    
    /**
     * @param suggestion
     * add a suggestion to the list
     * or get all elements from list, when the user don't want a suggestion 
     */
    public void addSuggestion(String suggestion){
        classifiedValuesSuggestion.add(suggestion);
        if(classifiedValuesSuggestion.contains("ALL")){
            classifiedValuesSuggestion.remove("ALL");
        }
        classifiedValuesSuggestion.add("ALL");
        
    }
     
    /**
     * get all classifiedValues
     */
    public ArrayList<String> getAllClassifiedValues() {
        return classifiedValues;
    }
}
