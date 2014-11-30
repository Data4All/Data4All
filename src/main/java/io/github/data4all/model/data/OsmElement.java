package io.github.data4all.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Superclass for node, way and relation objects, contains the osm id and osm version.
 * @author fkirchge
 *
 */
public abstract class OsmElement {

	private long osmId;
	private long osmVersion;
	
	private ArrayList<Relation> parentRelations;

	private SortedMap<String, String> tags;
	
	OsmElement(final long osmId, final long osmVersion) {
		this.setOsmId(osmId);
		this.setOsmVersion(osmVersion);
		this.setTags(new TreeMap<String, String>());
		this.parentRelations = null;
	}

	public long getOsmId() {
		return osmId;
	}

	public void setOsmId(long osmId) {
		this.osmId = osmId;
	}

	public long getOsmVersion() {
		return osmVersion;
	}

	public void setOsmVersion(long osmVersion) {
		this.osmVersion = osmVersion;
	}

	public SortedMap<String, String> getTags() {
		return Collections.unmodifiableSortedMap(tags);
	}
	
	/**
	 * Set the tags of the element, replacing all existing tags.
	 * @param tags New tags to replace existing tags.
	 * @return Flag indicating if the tags have actually changed.
	 */
	public boolean setTags(final Map<String, String> tags) {
		if (!this.tags.equals(tags)) {
			this.tags.clear();
			addTags(tags);
			return true;
		}
		return false;
	}	
	
	public void addOrUpdateTag(final String key, final String value) {
		this.tags.put(key, value);
	}
	
	/**
	 * Add the tags of the element, replacing any existing tags.
	 * @param tags New tags to add or to replace existing tags.
	 */	
	public void addTags(final Map<String, String> tags) {
		if (tags != null) {
			this.tags.putAll(tags);
		}		
	}
	
	/**
	 * @param key the key to search for (case sensitive)
	 * @param value the value to search for (case sensitive)
	 * @return true if the element has a tag with this key and value.
	 */
	public boolean hasTag(final String key, final String value) {
		String keyValue = tags.get(key);
		return keyValue != null && keyValue.equals(value);
	}	

	/**
	 * @param key the key to search for (case sensitive)
	 * @return the value of this key.
	 */
	public String getTagWithKey(final String key) {
		return tags.get(key);
	}
	
	/**
	 * @param key the key to search for (case sensitive)
	 * @return true if the element has a tag with this key.
	 */
	public boolean hasTagKey(final String key) {
		return getTagWithKey(key) != null;
	}
	
	/**
	 * check if this element has tags of any kind
	 * @return
	 */
	public boolean isTagged() {
		return (tags != null) && (tags.size() > 0);
	}	
	
	/**
	 * Add reference to parent relation 
	 * Does not check id to avoid dups!
	 */
	public void addParentRelation(Relation relation) {
		if (parentRelations == null) {
			parentRelations = new ArrayList<Relation>();
		}
		parentRelations.add(relation);
	}
	
	/**
	 * Check for parent relation
	 * @param relation
	 * @return
	 */
	public boolean hasParentRelation(Relation relation) {
		return (parentRelations == null ? false : parentRelations.contains(relation));
	}
	
	/**
	 * Check for parent relation based on id
	 * @param relation
	 * @return
	 */
	public boolean hasParentRelation(long osmId) {
		if (parentRelations == null) {
			return false;
		}
		for (Relation r:parentRelations) {
			if (osmId == r.getOsmId())
				return true;
		}
		return false;
	}
	
	/**
	 * Add all parent relations, avoids dups
	 */
	public void addParentRelations(ArrayList<Relation> relations) {
		if (parentRelations == null) {
			parentRelations = new ArrayList<Relation>();
		}
		//  dedup
		for (Relation r : relations) {
			if (!parentRelations.contains(r)) {
				addParentRelation(r);
			}
		}
	}
	
	public ArrayList<Relation> getParentRelations() {
		return parentRelations;
	}
	
	public boolean hasParentRelations() {
		return (parentRelations != null) && (parentRelations.size() > 0);
	}
	
	/**
	 * Remove reference to parent relation
	 * does not check for id
	 */
	public void removeParentRelation(Relation relation) {
		if (parentRelations != null) {
			parentRelations.remove(relation);
		}
	}
	
	/**
	 * Remove reference to parent relation
	 */
	public void removeParentRelation(long osmId) {
		if (parentRelations != null) {
			ArrayList<Relation> tempRelList = new ArrayList<Relation>(parentRelations);
			for (Relation r:tempRelList) {
				if (osmId == r.getOsmId())
					parentRelations.remove(r);
			}
		}
	}	

}
