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

import java.util.List;

/**
 * This class represents a classified tag. A classified tag is a subclass of
 * tag. classifiedTags are tags where the user can choose input/value from
 * listed values.
 * 
 * @author fkirchge
 *
 */
public class ClassifiedTag extends Tag {

    /**
     * stores all classified values for the specific tag.
     */
    private List<ClassifiedValue> classifiedValues;

    /**
     * Default constructor.
     * 
     * @param id The id of the tag
     * @param key The key of the tag
     * @param type The Type of the tag
     * @param classifiedValues the classifiedValues of the tag
     * @param osmObjects The OpenStreetObject the tag refers to
     */
    public ClassifiedTag(int id, String key, int type,
            List<ClassifiedValue> classifiedValues, int... osmObjects) {
        super(id, key, type, osmObjects);
        this.classifiedValues = classifiedValues;
    }

    public List<ClassifiedValue> getClassifiedValues() {
        return classifiedValues;
    }

    public void setClassifiedValues(List<ClassifiedValue> classifiedValues) {
        this.classifiedValues = classifiedValues;
    }

}
