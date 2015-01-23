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
public abstract class DataElement implements Parcelable {

    /**
     * osmId: Used for identifying the object.
     */
    private long osmId;

    /**
     * Sorted list of tags (key value pair) for the osm object.
     */
    private SortedMap<Tag, String> tags;

    /**
     * Default constructor
     * 
     * @param osmId
     *            id to identify the osm object
     */
    public DataElement(final long osmId) {
        this.setOsmId(osmId);
        this.tags = new TreeMap<Tag, String>();
    }

    public long getOsmId() {
        return osmId;
    }

    public void setOsmId(long osmId) {
        this.osmId = osmId;
    }

    /**
     * Returns all tags which belong to the osm object.
     * 
     * @return
     */
    public SortedMap<Tag, String> getTags() {
        return Collections.unmodifiableSortedMap(tags);
    }

    /**
     * Set the tags of the element, replacing all existing tags.
     * 
     * @param tags
     *            New tags to replace existing tags.
     * @return Flag indicating if the tags have actually changed.
     */
    public boolean setTags(final Map<Tag, String> tags) {
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
    public void addOrUpdateTag(final Tag tag, final String value) {
        this.tags.put(tag, value);
    }

    /**
     * Add the tags of the element, replacing any existing tags.
     * 
     * @param tags
     *            New tags to add or to replace existing tags.
     */
    public void addTags(final Map<Tag, String> tags) {
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
    public boolean hasTag(final Tag tag, final String value) {
        String keyValue = tags.get(tag);
        return keyValue != null && keyValue.equals(value);
    }

    /**
     * @param key
     *            the key to search for (case sensitive)
     * @return the value of this key.
     */
    public String getTagWithKey(final Tag tag) {
        return tags.get(tag);
    }

    /**
     * @param key
     *            the key to search for (case sensitive)
     * @return true if the element has a tag with this key.
     */
    public boolean hasTagKey(final Tag tag) {
        return getTagWithKey(tag) != null;
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
     * Writes the osmId and the osmVersion to the given parcel
     */
    public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(osmId);
//		dest.writeInt(tags.size());
//		for (String s: tags.keySet()) {
//			dest.writeString(s);
//			dest.writeString(tags.get(s));
//		}
//		dest.writeTypedList(parentRelations);
	}
	
    /**
     * Constructor to create a osm element from a parcel
     * @param in
     */
    protected DataElement(Parcel in) {
    	osmId = in.readLong();
    	tags = new TreeMap<Tag, String>();
        // int count = in.readInt();
        // for (int i = 0; i < count; i++) {
        // tags.put(in.readString(), in.readString());
        // }
        // parentRelations = new ArrayList<Relation>();
        // in.readTypedList(parentRelations, Relation.CREATOR);
    }

}