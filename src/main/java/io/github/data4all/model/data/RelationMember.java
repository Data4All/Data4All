package io.github.data4all.model.data;

/**
 * RelationMember stores the necessary information for a relation member, if the element field is null the element itself is not present
 * (not downloaded typically) and only the osm id, type (needed to make the id unique) and role fields are stored.
 * @author simon, fkirchge
 *
 */
public class RelationMember {

	/** 
	 * type can be node/way or relation
	 */
	private String type = null;
	
	/** 
	 * id of the reference element 
	 */ 
	private long ref;
	
	/** 
	 * optional textual field describing the function of a member of the relation 
	 */
	private String role = null;
	
	/** reference element **/ 
	OsmElement element = null;
	
	/**
	 * Constructor for members that have not been downloaded
	 */
	public RelationMember(final String t, final long id, final String r)
	{
		type = t;
		ref = id;
		role = r;
	}
	
	/**
	 * Constructor for members that have been downloaded
	 */
	public RelationMember(final String r, final OsmElement e)
	{
		role = r;
		element = e;
	}
	
	/**
	 * Constructor for copying, assumes that only role changes
	 */
	public RelationMember(final RelationMember rm)
	{
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
		if (element != null)
		{
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
	 * @param e
	 */
	public void setElement(final OsmElement e) {
		element=e;
	}
	
}
