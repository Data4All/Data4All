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
    private List<String> classifiedValues;

    /**
     * Default constructor.
     * 
     * @param key
     * @param type
     * @param classifiedValues
     * @param osmObjects
     */
    public ClassifiedTag(String key, InputType type,
            List<String> classifiedValues, int... osmObjects) {
        super(key, type, osmObjects);
        this.classifiedValues = classifiedValues;
    }

    public List<String> getClassifiedValues() {
        return classifiedValues;
    }

    public void setClassifiedValues(List<String> classifiedValues) {
        this.classifiedValues = classifiedValues;
    }

}
