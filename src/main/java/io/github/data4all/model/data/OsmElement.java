package io.github.data4all.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Superclass for node, way and relation objects, containing attributes osm id
 * and osm version. All parent relations and tags of the object are stored in a
 * list.
 * 
 * @author fkirchge
 *
 */
public abstract class OsmElement implements Parcelable {

    /**
     * osmId: Used for identifying the object. osmVersion: edit version of the
     * object.
     */
    private long osmId;
    private long osmVersion;

    /**
     * Stores a list of parent relations to which the osm object belongs.
     */
    private List<Relation> parentRelations;

    /**
     * Sorted list of tags (key value pair) for the osm object.
     */
    private SortedMap<String, String> tags;

    /**
     * Default constructor
     * 
     * @param osmId
     *            id to identify the osm object
     * @param osmVersion
     *            edit version of the osm object
     */
    public OsmElement(final long osmId, final long osmVersion) {
        this.setOsmId(osmId);
        this.setOsmVersion(osmVersion);
        this.tags = new TreeMap<String, String>();
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

    /**
     * Returns all tags which belong to the osm object.
     * 
     * @return
     */
    public SortedMap<String, String> getTags() {
        return Collections.unmodifiableSortedMap(tags);
    }

    /**
     * Set the tags of the element, replacing all existing tags.
     * 
     * @param tags
     *            New tags to replace existing tags.
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

    /**
     * Adds a new tag or updates an existing tag of the element
     * 
     * @param key
     * @param value
     */
    public void addOrUpdateTag(final String key, final String value) {
        this.tags.put(key, value);
    }

    /**
     * Add the tags of the element, replacing any existing tags.
     * 
     * @param tags
     *            New tags to add or to replace existing tags.
     */
    public void addTags(final Map<String, String> tags) {
        if (tags != null) {
            this.tags.putAll(tags);
        }
    }

    /**
     * @param key
     *            the key to search for (case sensitive)
     * @param value
     *            the value to search for (case sensitive)
     * @return true if the element has a tag with this key and value.
     */
    public boolean hasTag(final String key, final String value) {
        String keyValue = tags.get(key);
        return keyValue != null && keyValue.equals(value);
    }

    /**
     * @param key
     *            the key to search for (case sensitive)
     * @return the value of this key.
     */
    public String getTagWithKey(final String key) {
        return tags.get(key);
    }

    /**
     * @param key
     *            the key to search for (case sensitive)
     * @return true if the element has a tag with this key.
     */
    public boolean hasTagKey(final String key) {
        return getTagWithKey(key) != null;
    }

    /**
     * check if this element has tags of any kind
     * 
     * @return
     */
    public boolean isTagged() {
        return (tags != null) && (tags.size() > 0);
    }

    /**
     * Add reference to parent relation Does not check id to avoid dups!
     */
    public void addParentRelation(Relation relation) {
        if (parentRelations == null) {
            parentRelations = new ArrayList<Relation>();
        }
        parentRelations.add(relation);
    }

    /**
     * Check for parent relation
     * 
     * @param relation
     * @return
     */
    public boolean hasParentRelation(Relation relation) {
        return (parentRelations == null ? false : parentRelations
                .contains(relation));
    }

    /**
     * Check for parent relation based on id
     * 
     * @param relation
     * @return
     */
    public boolean hasParentRelation(long osmId) {
        if (parentRelations == null) {
            return false;
        }
        for (Relation r : parentRelations) {
            if (osmId == r.getOsmId())
                return true;
        }
        return false;
    }

    /**
     * Add all parent relations, avoids dups
     */
    public void addParentRelations(List<Relation> relations) {
        if (parentRelations == null) {
            parentRelations = new ArrayList<Relation>();
        }
        // dedup
        for (Relation r : relations) {
            if (!parentRelations.contains(r)) {
                addParentRelation(r);
            }
        }
    }

    /**
     * Returns the list of parent relations.
     * 
     * @return
     */
    public List<Relation> getParentRelations() {
        return parentRelations;
    }

    /**
     * Returns true if the object has parent relations.
     * 
     * @return
     */
    public boolean hasParentRelations() {
        return (parentRelations != null) && (parentRelations.size() > 0);
    }

    /**
     * Remove reference to parent relation does not check for id
     * 
     * @param relation
     */
    public void removeParentRelation(Relation relation) {
        if (parentRelations != null) {
            parentRelations.remove(relation);
        }
    }

    /**
     * Remove reference to parent relation, checking the id
     * 
     * @param osmId
     */
    public void removeParentRelation(long osmId) {
        if (parentRelations != null) {
            List<Relation> tempRelList = new ArrayList<Relation>(
                    parentRelations);
            for (Relation r : tempRelList) {
                if (osmId == r.getOsmId())
                    parentRelations.remove(r);
            }
        }
    }

    /**
     * Writes the osmId and the osmVersion to the given parcel
     */
    public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(osmId);
		dest.writeLong(osmVersion);
		//dest.writeList(parentRelations);
		dest.writeInt(tags.size());
		for (String s: tags.keySet()) {
			dest.writeString(s);
			dest.writeString(tags.get(s));
		}
		dest.writeTypedList(parentRelations);
	}
	
    /**
     * Constructor to create a osm element from a parcel
     * @param in
     */
    protected OsmElement(Parcel in) {
    	osmId = in.readLong();
    	osmVersion = in.readLong();
    	//parentRelations = new ArrayList<Relation>();
    	//parentRelations = new ArrayList<Relation>();
    	//tags = new TreeMap<String, String>();
    	//in.readList(parentRelations, null);
    	//in.readMap(tags, null);
    	tags = new TreeMap<String, String>();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            tags.put(in.readString(), in.readString());
        }
        parentRelations = new ArrayList<Relation>();
        in.readTypedList(parentRelations, Relation.CREATOR);
    }

}