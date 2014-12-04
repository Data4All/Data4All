package io.github.data4all.model.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

/**
 * Relation represents an OSM relation element which essentially is a collection of other OSM elements.
 * A relation consists of one or more tags and also an ordered list of one or more nodes, 
 * ways and/or relations as members which is used to define logical or geographic relationships between other elements.
 * 
 * @author simon, fkirchge
 *
 */
public class Relation extends OsmElement {

	/**
	 * List of all member of the relation. 
	 */
	private final ArrayList<RelationMember> members;

	/**
	 * Default constructor
	 * @param osmId
	 * @param osmVersion
	 */
	public Relation(final long osmId, final long osmVersion) {
		super(osmId, osmVersion);
		members = new ArrayList<RelationMember>();
	}

	/**
	 * Adds a new member to the relation. 
	 * @param member
	 */
	public void addMember(final RelationMember member) {
		members.add(member);
	}

	/**
	 * Returns all member of the relation.
	 * @return
	 */
	public List<RelationMember> getMembers() {
		return members;
	}
	
	/**
	 * Returns the relation member of the osm element. 
	 * @param e
	 * @return relation member
	 */
	public RelationMember getMember(OsmElement e) {
		for (int i = 0; i < members.size(); i++) {
			RelationMember member = members.get(i);
			if (member.getElement() == e) {
				return member;
			}
		}
		return null;
	}
	
	/**
	 * Returns the relation member which matches with the type and the id. 
	 * @param type
	 * @param id
	 * @return relation member
	 */
	public RelationMember getMember(String type, long id) {
		for (int i = 0; i < members.size(); i++) {
			RelationMember member = members.get(i);
			if (member.getRef() == id && member.getType().equals(type)) {
				return member;
			}
		}
		return null;
	}

	/**
	 * Returns the position of a relation member in the list of members.
	 * @param e
	 * @return position
	 */
	public int getPosition(RelationMember e) {
		return members.indexOf(e);
	}
	
	/**
	 * Returns a iterator for the relation member.
	 * @return list of members allowing {@link Iterator#remove()}.
	 */
	public Iterator<RelationMember> getRemovableMembers() {
		return members.iterator();
	}
	
	/**
	 * Check if the given member belongs to the relation.
	 * @param member
	 * @return
	 */
	public boolean hasMember(final RelationMember member) {
		return members.contains(member);
	}

	/**
	 * Removes a member from the relation. 
	 * @param member
	 */
	public void removeMember(final RelationMember member) {
		while (members.remove(member)) {
			;
		}
	}

	/**
	 * Append a new member at the begin or at the end of the relation member list. 
	 * @param refMember
	 * @param newMember
	 */
	public void appendMember(final RelationMember refMember, final RelationMember newMember) {
		if (members.get(0) == refMember) {
			members.add(0, newMember);
		} else if (members.get(members.size() - 1) == refMember) {
			members.add(newMember);
		}
	}

	/**
	 * Inserts a new relation member after the reference member.
	 * @param memberBefore
	 * @param newMember
	 */
	public void addMemberAfter(final RelationMember memberBefore, final RelationMember newMember) {
		members.add(members.indexOf(memberBefore) + 1, newMember);
	}
	
	/**
	 * Adds a new relation member to a given position.
	 * @param pos
	 * @param newMember
	 */
	public void addMember(int pos, final RelationMember newMember) {
		if (pos < 0 || pos > members.size()) {
			pos = members.size(); // append
		}
		members.add(pos, newMember);
	}
	
	/**
	 * Adds multiple elements to the relation in the order in which they appear in the list.
	 * They can be either prepended or appended to the existing nodes.
	 * @param newMembers a list of new members
	 * @param atBeginning if true, nodes are prepended, otherwise, they are appended
	 */
	public void addMembers(List<RelationMember> newMembers, boolean atBeginning) {
		if (atBeginning) {
			members.addAll(0, newMembers);
		} else {
			members.addAll(newMembers);
		}
	}
	
	/**
	 * Returns a list of member with the current role.
	 * @param role the name of the role
	 * @return list of relation member
	 */
	public ArrayList <RelationMember> getMembersWithRole(String role) {
		ArrayList <RelationMember> rl = new ArrayList<RelationMember>();
		for (RelationMember rm : members) {
			//Log.d(getClass().getSimpleName(), "getMembersWithRole " + rm.getRole()); weird nullpointer
			if (role.equals(rm.getRole())) {
				rl.add(rm);
			}
		}
		return rl;
	}
	
	/**
	 * Replace an existing member in a relation with a different member.
	 * @param existing The existing member to be replaced.
	 * @param newMember The new member.
	 */
	public void replaceMember(RelationMember existing, RelationMember newMember) {
		int idx;
		while ((idx = members.indexOf(existing)) != -1) {
			members.set(idx, newMember);
		}
	}

	/**
	 * Return a list of the downloaded elements.
	 * Return a list of relation member object which have an osm element reference (getElement() != null). 
	 * @return list of osm elements 
	 */
	public ArrayList<OsmElement> getMemberElements() {
		ArrayList<OsmElement> result = new ArrayList<OsmElement>();
		for (RelationMember rm : getMembers()) {
			if (rm.getElement() != null)
				result.add(rm.getElement());
		}
		return result;
	}
		
}