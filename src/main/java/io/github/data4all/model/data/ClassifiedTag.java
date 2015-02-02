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

/**
 * This class represents a classified tag. A classified tag is a subclass of tag.  
 * classifiedTags are tags where the user can choose input/value from listed values.
 * @author fkirchge
 *
 */
public class ClassifiedTag extends Tag {
	
	/**
	 * stores all classified values for the specific tag
	 */
	private ArrayList<String> classifiedValues;

	/**
	 * Default constructor.
	 * @param key
	 * @param type
	 * @param classifiedValues
	 * @param osmObjects
	 */
	public ClassifiedTag(String key, InputType type, ArrayList<String> classifiedValues, int... osmObjects) {
		super(key, type, osmObjects);
		this.classifiedValues = classifiedValues;
	}	
	
	public ArrayList<String> getClassifiedValues() {
		return classifiedValues;
	}

	public void setClassifiedValues(ArrayList<String> classifiedValues) {
		this.classifiedValues = classifiedValues;
	}
	

}
