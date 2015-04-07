/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.model.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A DataElement represents different kinds of OpenStreetMap objects like Node,
 * Way and Relation. Its a superclass for Node and PolyElement objects and
 * contains the object id and additionally all tags of the object.
 * 
 * @author fkirchge
 *
 */
public abstract class AbstractDataElement implements Parcelable {
    /**
     * osmId: Used for identifying the OpenStreetMap object, default value is
     * -1.
     */
    private long osmId = -1;
    /**
     * Sorted list of tags (key value pair) for the DataElement object.
     */
    private Map<Tag, String> tags;

    /**
     * Default constructor.
     * 
     * @param osmId
     *            id to identify the OpenStreetMap object, default value is -1
     */
    public AbstractDataElement(final long osmId) {
        this.setOsmId(osmId);
        this.tags = new LinkedHashMap<Tag, String>();
    }

    /**
     * Constructor to create an DataElement from a parcel.
     * 
     * @param in
     *            The Parcel to read from
     */
    protected AbstractDataElement(Parcel in) {
        osmId = in.readLong();
        tags = new LinkedHashMap<Tag, String>();
        final int count = in.readInt();
        for (int i = 0; i < count; i++) {
            tags.put(Tags.getTagWithId(in.readInt()), in.readString());
        }
    }

    /**
     * Adds a new tag or updates an existing tag of the DataElement.
     * 
     * @param tag
     *            the Tag object to add
     * @param value
     *            value of the Tag object
     */
    public void addOrUpdateTag(final Tag tag, final String value) {
        this.tags.put(tag, value);
    }
    
    /**
     * Removes an existing tag of the DataElement
     * 
     * @param tag the Tag to remove
     * 	
     */
    public void removeTag(final Tag tag) {
    	this.tags.remove(tag);
    }

    
    
    /**
     * Add the tags of the DataElement.
     * 
     * @param tags
     *            map of tags
     * 
     */
    public void addTags(final Map<Tag, String> tags) {
        if (tags != null) {
            this.tags.putAll(tags);
        }
    }

    public long getOsmId() {
        return osmId;
    }

    /**
     * Returns all tags which belong to the DataElement.
     * 
     * @return unmodifiable collection of tags
     */
    public Map<Tag, String> getTags() {
        if (!tags.isEmpty()) {
            return tags;
        } else {
            return tags;
        }
    }

    /**
     * Returns the value of specific tag.
     * 
     * @param key
     *            the tag to search for
     * @return the value of this key.
     */
    public String getTagValueWithKey(final Tag key) {
        return tags.get(key);
    }

    /**
     * Checks if the DataElement has a specific tag.
     * 
     * @param key
     *            the key to search for
     * @param value
     *            the value to search for (case sensitive)
     * @return true if the element has a tag with this key and value.
     */
    public boolean hasTag(final Tag key, final String value) {
        final String tagValue = tags.get(key);
        return tagValue != null && tagValue.equals(value);
    }

    /**
     * Checks if the DataElement has a specific tag.
     * 
     * @param key
     *            the key to search for
     * @return true if the element has a tag with this key.
     */
    public boolean hasTagKey(final Tag key) {
        return this.getTagValueWithKey(key) != null;
    }

    /**
     * Check if this element has tags of any kind.
     * 
     * @return true is the object has tags
     */
    public boolean isTagged() {
        return (tags != null) && (!tags.isEmpty());
    }

    public void setOsmId(long osmId) {
        this.osmId = osmId;
    }

    /**
     * Set the tags of the DataElement, replacing all existing tags.
     * 
     * @param tagMap
     *            New tags to replace existing tags.
     * @return Flag indicating if the tags have actually changed.
     */
    public boolean setTags(final Map<Tag, String> tagMap) {
        if (!this.tags.equals(tagMap)) {
            this.tags.clear();
            this.addTags(tagMap);
            return true;
        }
        return false;
    }

    /**
     * Writes the osmId and the tags to the given parcel.
     * 
     * @param dest
     *            the destination parcel
     * @param flags
     *            additional flags for writing the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(osmId);
        dest.writeInt(tags.size());
        for (Tag t : tags.keySet()) {
            dest.writeInt(t.getId());
            dest.writeString(tags.get(t));
        }
    }
    
}
