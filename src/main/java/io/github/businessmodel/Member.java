package io.github.businessmodel;

/**
 * This class represents member of a relation.
 * Members are used to define logical or geographic relationships between other elements. 
 * Nodes, ways and/or relations can be part of a member. 
 * @author Felix Kirchgeorg
 *
 */
public class Member {
	
	private String type;
	private OsmObject ref;
	private String role;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public OsmObject getRef() {
		return ref;
	}
	
	public void setRef(OsmObject ref) {
		this.ref = ref;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
}