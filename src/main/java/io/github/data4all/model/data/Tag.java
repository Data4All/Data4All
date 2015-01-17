package io.github.data4all.model.data;

import io.github.data4all.R;

/**
 * 
 * @author fkirchge, tbrose
 *
 */
public class Tag {
	
	/**
	 * key for the internal representation in osm 
	 * e.g. addr:street
	 */
	private String key; 
	
	/**
	 * nameRessource defines the displayed name/value in the tagging activity
	 */
	private int nameRessource;
	
	/**
	 * hintRessource defines the displayed hint/value in the tagging activity
	 */
	private int hintRessource;
	
	/**
	 * type defines if the tagging activity should display a keyboard or a numpad as
	 * input method
	 */
	private InputType type;
	
	/**
	 * define to which osm objects the tag refers
	 */
	private int[] osmObjects;
	
	/**
	 *	defines different input types
	 */
	public static enum InputType {
		KEYBOARD, NUMPAD;
	}
	
	/**
	 * Default constructor to create a tag
	 * @param key
	 * @param nameRessource
	 * @param hintRessource
	 * @param type
	 */
	public Tag(String key, int nameRessource, int hintRessource, InputType type, int[] osmObjects) {
		this.key = key;
		this.nameRessource = nameRessource;
		this.hintRessource = hintRessource;
		this.type = type;
		this.setOsmObjects(osmObjects);
	}
	
	/**
	 * Constructor to create nameRessource and hintRessource from the key
	 * @param key
	 * @param type
	 * @param osmObjects
	 */
	public Tag(String key, InputType type, int[] osmObjects) {
		this.key = key;
		try {
			this.nameRessource = (Integer) R.string.class.getDeclaredField("name_" + key.replaceAll(":", "_")).get(null);
			this.hintRessource = (Integer) R.string.class.getDeclaredField("hint_" + key.replaceAll(":", "_")).get(null);
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getNameRessource() {
		return nameRessource;
	}

	public void setNameRessource(int nameRessource) {
		this.nameRessource = nameRessource;
	}

	public int getHintRessource() {
		return hintRessource;
	}

	public void setHintRessource(int hintRessource) {
		this.hintRessource = hintRessource;
	}

	public InputType getType() {
		return type;
	}

	public void setType(InputType type) {
		this.type = type;
	}

	public int[] getOsmObjects() {
		return osmObjects;
	}

	public void setOsmObjects(int[] osmObjects) {
		this.osmObjects = osmObjects;
	}
	
}