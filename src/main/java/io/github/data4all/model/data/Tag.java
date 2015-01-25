package io.github.data4all.model.data;

import io.github.data4all.R;

import java.util.Arrays;

/**
 * This class represents a predefined osm tag. The name and hint for a specific
 * tag is defined in the tag_name.xml and tag_hint.xml.
 * 
 * @author fkirchge, tbrose
 *
 */
public class Tag {

    /**
     * defines different input types.
     */
    public static enum InputType {
        KEYBOARD, NUMPAD;
    }

    /**
     * id to identify the tag.
     */
    private int id;

    /**
     * key for the internal representation in osm e.g. addr:street.
     */
    private String key;

    /**
     * nameRessource defines the displayed name/value in the tagging activity.
     */
    private int nameRessource;

    /**
     * hintRessource defines the displayed hint/value in the tagging activity.
     */
    private int hintRessource;

    /**
     * type defines if the tagging activity should display a keyboard or a
     * numpad as input method.
     */
    private InputType type;
    
    /**
     * constant values to define which osmObject the tag refers to.
     */
    public static final int NODE_TAG = 1;
    public static final int WAY_TAG = 2;
    public static final int RELATION_TAG = 3;
    public static final int AREA_TAG = 4;

    /**
     * define to which osm objects the tag refers
     */
    private int[] osmObjects;

    /**
     * Constructor to create nameRessource and hintRessource from the key
     * 
     * @param key
     * @param type
     * @param osmObjects
     */
    public Tag(int id, String key, InputType type, int... osmObjects) {
        this.id = id;
        this.key = key;
        try {
            this.nameRessource = (Integer) R.string.class.getDeclaredField(
                    "name_" + key.replaceAll(":", "_")).get(null);
            if (type != null) {
                this.hintRessource = (Integer) R.string.class.getDeclaredField(
                        "hint_" + key.replaceAll(":", "_")).get(null);
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.type = type;
        this.setOsmObjects(osmObjects);
    }

    /**
     * Default constructor to create a tag
     * 
     * @param key
     * @param nameRessource
     * @param hintRessource
     * @param type
     */
    public Tag(int id, String key, int nameRessource, int hintRessource,
            InputType type, int... osmObjects) {
        this.id = id;
        this.key = key;
        this.nameRessource = nameRessource;
        this.hintRessource = hintRessource;
        this.type = type;
        this.setOsmObjects(osmObjects);
    }

    public int getHintRessource() {
        return hintRessource;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getNameRessource() {
        return nameRessource;
    }

    public int[] getOsmObjects() {
        return osmObjects;
    }

    public InputType getType() {
        return type;
    }

    public void setHintRessource(int hintRessource) {
        this.hintRessource = hintRessource;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNameRessource(int nameRessource) {
        this.nameRessource = nameRessource;
    }

    public void setOsmObjects(int[] osmObjects) {
        this.osmObjects = osmObjects;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    /**
     * toString method just for debug purpose.
     */
    @Override
    public String toString() {
        return "key: " + key + " nameRessource: " + nameRessource
                + " hintRessource: " + hintRessource + " osmObjects: "
                + Arrays.toString(osmObjects);
    }

}