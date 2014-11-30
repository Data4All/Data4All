package io.github.data4all.model.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

/**
 * Relation represents an OSM relation element which essentially is a collection of other OSM elements.
 * 
 * @author simon, fkirchge
 *
 */
public class Relation extends OsmElement {

	protected final ArrayList<RelationMember> members;

	Relation(final long osmId, final long osmVersion) {
		super(osmId, osmVersion);
		members = new ArrayList<RelationMember>();
	}

	public void addMember(final RelationMember member) {
		members.add(member);
	}

	public List<RelationMember> getMembers() {
		return members;
	}
	
	public RelationMember getMember(OsmElement e) {
		for (int i = 0; i < members.size(); i++) {
			RelationMember member = members.get(i);
			if (member.getElement() == e) {
				return member;
			}
		}
		return null;
	}
	
	public RelationMember getMember(String type, long id) {
		for (int i = 0; i < members.size(); i++) {
			RelationMember member = members.get(i);
			if (member.getRef() == id && member.getType().equals(type)) {
				return member;
			}
		}
		return null;
	}

	public int getPosition(RelationMember e) {
		return members.indexOf(e);
	}
	
	/**
	 * 
	 * @return list of members allowing {@link Iterator#remove()}.
	 */
	public Iterator<RelationMember> getRemovableMembers() {
		return members.iterator();
	}
	
	public boolean hasMember(final RelationMember member) {
		return members.contains(member);
	}

	public void removeMember(final RelationMember member) {
		while (members.remove(member)) {
			;
		}
	}

	public void appendMember(final RelationMember refMember, final RelationMember newMember) {
		if (members.get(0) == refMember) {
			members.add(0, newMember);
		} else if (members.get(members.size() - 1) == refMember) {
			members.add(newMember);
		}
	}

	public void addMemberAfter(final RelationMember memberBefore, final RelationMember newMember) {
		members.add(members.indexOf(memberBefore) + 1, newMember);
	}
	
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
	
	public ArrayList <RelationMember> getMembersWithRole(String role) {
		ArrayList <RelationMember> rl = new ArrayList<RelationMember>();
		for (RelationMember rm : members) {
			Log.d("Relation", "getMembersWithRole " + rm.getRole());
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
	 * return a list of the downloaded elements
	 * @return
	 */
	public ArrayList<OsmElement> getMemberElements() {
		ArrayList<OsmElement> result = new ArrayList<OsmElement>();
		for (RelationMember rm:getMembers()) {
			if (rm.getElement()!=null)
				result.add(rm.getElement());
		}
		return result;
	}
		
}