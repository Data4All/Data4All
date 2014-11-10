package io.github.businessmodel;

import java.util.ArrayList;
import java.util.Date;

/**
 * This class acts as superclass for node, way and relation objects, it contains all common attributes of these elements.
 * @author Felix Kirchgeorg
 *
 */
public class OsmObject {
	
	private int id;
	private String user;
	private int uid;
	private Date timestamp;
	private boolean visible;
	private int version;
	private int changeset;
	private ArrayList<Tag> tags; 
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public int getUid() {
		return uid;
	}
	
	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getChangeset() {
		return changeset;
	}
	
	public void setChangeset(int changeset) {
		this.changeset = changeset;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}
	
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}	
	
	/**
	 * Adds a new tag to the relation.
	 * @param tag
	 */
	public void addTag(Tag tag) {
		if (!tags.contains(tag)) {
			tags.add(tag);
		}
	}
	
	/**
	 * Removes a tag from the relation. 
	 * @param tag
	 */
	public void removeTag(Tag tag) {
		if (tags.contains(tag)) {
			tags.remove(tag);
		}
	}	
}