package io.github.data4all.model.data;

/**
 * RelationMember stores the necessary information for a relation member, if the
 * element field is null the element itself is not present (not downloaded
 * typically) and only the osm id, type (needed to make the id unique) and role
 * fields are stored.
 * 
 * @author simon, fkirchge
 *
 */
public class RelationMember {

    /**
     * type can be node/way or relation
     */
	private static String TYPE_NODE = "Node";
	private static String TYPE_WAY = "Way";
	private static String TYPE_RELATION = "Relation";
	
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
    	if (element != null) {
            if (element.getClass().equals(Node.class)){
            	type = TYPE_NODE;
            }
            if (element.getClass().equals(Way.class)){
            	type = TYPE_WAY;
            }
            if (element.getClass().equals(Relation.class)){
            	type = TYPE_RELATION;
            }
        }
    	
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

}
