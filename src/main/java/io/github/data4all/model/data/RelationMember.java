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

    public String getType() {
        return type;
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

    public void setRole(final String role) {
        this.role = role;
    }

    public OsmElement getElement() {
        return element;
    }

    /**
     * set the element, used for post processing relations
     * 
     * @param e
     */
    public void setElement(final OsmElement e) {
        this.element = e;
    }
    
    /**
     * Methods to write and restore a Parcel
     */
    public static final Parcelable.Creator<RelationMember> CREATOR
            = new Parcelable.Creator<RelationMember>() {
    	
        public RelationMember createFromParcel(Parcel in) {
            return new RelationMember(in);
        }

        public RelationMember[] newArray(int size) {
            return new RelationMember[size];
        }
    };
    
    
    public int describeContents() {
		return 0;
	}

    /**
     * Writes the type, role, ref and element to the given parcel.
     */
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeString(type);
		dest.writeString(role);
		dest.writeLong(ref);
		OsmElementBuilder.write(dest, element, flags);
	}
	
	/**
	 * Constructor to create a relation member from a parcel.
	 * @param in
	 */
    private RelationMember(Parcel in) {
    	type = in.readString();
    	role = in.readString();
    	ref = in.readLong();
    	element = OsmElementBuilder.read(in);
    }

}
