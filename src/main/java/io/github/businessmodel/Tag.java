package io.github.businessmodel;

/**
 * This class represents a tag object.
 * A Tag consists of 'Key' and a 'Value'. Each tag describes a specific feature of a data element (nodes, ways and relations) or changesets. Both the key and value are free format text fields.
 * In practice, however, there are agreed conventions of how tags are used for most common purposes.
 * A list of common tags can be found inside the osm wiki:
 * http://wiki.openstreetmap.org/wiki/Map_Features
 * http://taginfo.openstreetmap.org/
 *   
 * @author Felix Kirchgeorg
 *
 */
public class Tag {

	private String key; 
	private String value;
	
	public Tag() {
		
	}
	
	public Tag(String k, String v) {
		this.key = k;
		this.value = v; 
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
