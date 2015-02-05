/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * RelationMember stores the necessary information for a relation member, if the
 * element field is null the element itself is not present (not downloaded
 * typically) and only the osm id, type (needed to make the id unique) and role
 * fields are stored.
 * 
 * @author simon, fkirchge
 *
 */
public class RelationMember implements Parcelable {

    /**
     * type can be node/way or relation
     */
    private String type = null;

    /**
     * id of the reference element
     */
    private long ref;

    /**
     * optional textual field describing the function of a member of the
     * relation
     */
    private String role = null;

    /**
     * reference element
     */
    private OsmElement element = null;

    /**
     * CREATOR that generates instances of {@link RelationMember} from a Parcel
     */
    public static final Parcelable.Creator<RelationMember> CREATOR = new Parcelable.Creator<RelationMember>() {
        public RelationMember createFromParcel(Parcel in) {
            return new RelationMember(in);
        }

        public RelationMember[] newArray(int size) {
            return new RelationMember[size];
        }
    };

    /**
     * Constructor to create a {@link RelationMember} from a parcel.
     * 
     * @param in
     *            The {@link Parcel} to read the object's data from
     */
    private RelationMember(Parcel in) {
        type = in.readString();
        role = in.readString();
        ref = in.readLong();
        element = OsmElementBuilder.read(in);
    }

    /**
     * Constructor for copying, assumes that only role changes
     */
    public RelationMember(final RelationMember rm) {
        if (rm.element == null) {
            type = rm.type;
            ref = rm.ref;
            role = new String(rm.role);
        } else {
            role = new String(rm.role);
            element = rm.element;
        }
    }

    /**
     * Default constructor Constructor for members that have not been downloaded
     */
    public RelationMember(final String type, final long refId, final String role) {
        this.type = type;
        this.ref = refId;
        this.role = role;
    }

    /**
     * Constructor for members that have been downloaded
     */
    public RelationMember(final String role, final OsmElement element) {
        this.role = role;
        this.element = element;
    }

    public int describeContents() {
        return 0;
    }

    public OsmElement getElement() {
        return element;
    }

    public long getRef() {
        if (element != null) {
            return element.getOsmId();
        }
        return ref;
    }

    public String getRole() {
        return role;
    }

    public String getType() {
        return type;
    }

    /**
     * set the element, used for post processing relations
     * 
     * @param e
     */
    public void setElement(final OsmElement e) {
        this.element = e;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(role);
        dest.writeLong(ref);
        OsmElementBuilder.write(dest, element, flags);
    }
}
