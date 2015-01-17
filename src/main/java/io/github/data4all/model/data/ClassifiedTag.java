package io.github.data4all.model.data;

import java.util.ArrayList;

/**
 * This class represents a classified tag. 
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
	public ClassifiedTag(String key, InputType type, ArrayList<String> classifiedValues, int[] osmObjects) {
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
