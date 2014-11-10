package io.github.businessmodel;

import java.util.ArrayList;

/**
 * This class represents an relation between open street map nodes.
 * A relation is one of the core data elements that consists of one or more tags and also an ordered list of one or more nodes, ways and/or relations as members which is used to define logical or geographic relationships between other elements.
 * A member of a relation can optionally have a role which describe the part that a particular feature plays within a relation. 
 * @author Felix Kirchgeorg
 *
 */
public class Relation extends OsmObject {
	
	private ArrayList<Member> members;
	
	public Relation() {
		
	}
	
	public Relation(ArrayList<Member> mbs) {
		this.members = mbs; 
	}
	
	public ArrayList<Member> getMembers() {
		return members;
	}
	
	public void setMembers(ArrayList<Member> members) {
		this.members = members;
	}
	
	/**
	 * Adds a new member to the relation. 
	 * @param member
	 */
	public void addMember(Member member) {
		if (members.size() <= 300) {
			members.add(member);
		}
	}
	
	/**
	 * Removes a member from the relation. 
	 * @param member
	 */
	public void removeMember(Member member) {
		if (members.contains(member)) {
			members.remove(member);
		}
	}

}