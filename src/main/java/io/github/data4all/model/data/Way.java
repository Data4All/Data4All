package io.github.data4all.model.data;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

/**
 * A way is an ordered list of nodes which normally also has at least one tag or is included within a Relation.
 * A way can have between 2 and 2,000 nodes. 
 * A way can be open or closed. A closed way is one whose last node on the way is also the first on that way. 
 * @author fkirchge
 *
 */
public class Way extends OsmElement {

	/**
	 * List of nodes to define a way (waypoints).
	 */
	private final List<Node> nodes;
	
	/**
	 * Max. number of nodes.
	 */
	private final int MAX_WAY_NODES = 2000; 
	
	/**
	 * Default Constructor
	 * @param osmId
	 * @param osmVersion
	 */
	public Way(long osmId, long osmVersion) {
		super(osmId, osmVersion);
		nodes = new ArrayList<Node>();
	}

	/**
	 * Adds a new node to the way.
	 * @param node
	 */
	public void addNode(final Node node) {
		if ((nodes.size() > 0) && (nodes.get(nodes.size() - 1) == node)) {
			Log.i(getClass().getSimpleName(), "addNode attempt to add same node");
			return;
		} 
		if(nodes.size() >= MAX_WAY_NODES) {
			Log.i(getClass().getSimpleName(), "addNode attempt to add more than 2000 nodes");
			return;
		}
		nodes.add(node);
	}
	
	/**
	 * Append a node at the begin or end of the list.
	 * @param refNode 
	 * @param newNode
	 */
	public void appendNode(final Node refNode, final Node newNode) {
		if (refNode == newNode) { // user error
			Log.i(getClass().getSimpleName(), "appendNode attempt to add same node");
			return;
		}
		if (nodes.get(0) == refNode) {
			nodes.add(0, newNode);
		} else if (nodes.get(nodes.size() - 1) == refNode) {
			nodes.add(newNode);
		}
	}

	/**
	 * Insert a node behind the reference node.
	 * @param nodeBefore
	 * @param newNode
	 */
	public void addNodeAfter(final Node nodeBefore, final Node newNode) {
		if (nodeBefore == newNode) { // user error
			Log.i(getClass().getSimpleName(), "addNodeAfter attempt to add same node");
			return;
		}
		nodes.add(nodes.indexOf(nodeBefore) + 1, newNode);
	}	
	
	/**
	 * Adds multiple nodes to the way in the order in which they appear in the list.
	 * They can be either prepended or appended to the existing nodes.
	 * @param newNodes a list of new nodes
	 * @param atBeginning if true, nodes are prepended, otherwise, they are appended
	 */
	public void addNodes(List<Node> newNodes, boolean atBeginning) {
		if (atBeginning) {
			if ((nodes.size() > 0) && nodes.get(0) == newNodes.get(newNodes.size()-1)) { // user error
				Log.i(getClass().getSimpleName(), "addNodes attempt to add same node");
				if (newNodes.size() > 1) {
					Log.i(getClass().getSimpleName(), "retrying addNodes");
					newNodes.remove(newNodes.size()-1);
					addNodes(newNodes, atBeginning);
				}
				return;
			}
			nodes.addAll(0, newNodes);
		} else {
			if ((nodes.size() > 0) && newNodes.get(0) == nodes.get(nodes.size()-1)) { // user error
				Log.i(getClass().getSimpleName(), "addNodes attempt to add same node");
				if (newNodes.size() > 1) {
					Log.i(getClass().getSimpleName(), "retrying addNodes");
					newNodes.remove(0);
					addNodes(newNodes, atBeginning);
				}
				return;
			}
			nodes.addAll(newNodes);
		}
	}	
	
	/**
	 * Returns all nodes which belong to the way.
	 * @return
	 */
	public List<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Returns true if the node is part of the way.
	 * @param node
	 * @return
	 */
	public boolean hasNode(final Node node) {
		return nodes.contains(node);
	}

	/**
	 * Returns true if the given way contains common nodes with the this way object.
	 * @param way
	 * @return
	 */
	public boolean hasCommonNode(final Way way) {
		for (Node n : this.nodes) {
			if (way.hasNode(n)) {
				return true;
			}
		}
 		return false;
	}
	
	/**
	 * Removes a node from the way. 
	 * @param node
	 */
	public void removeNode(final Node node) {
		int index = nodes.lastIndexOf(node);
		if (index > 0 && index < (nodes.size()-1)) { // not the first or last node 
			if (nodes.get(index-1) == nodes.get(index+1)) {
				nodes.remove(index-1);
				Log.i(getClass().getSimpleName(), "removeNode removed duplicate node");
			}
		}
		while (nodes.remove(node)) {
			;
		}
	}
	
	/**
	 * Replace an existing node in a way with a different node.
	 * @param existing The existing node to be replaced.
	 * @param newNode The new node.
	 */
	public void replaceNode(Node existing, Node newNode) {
		int idx;
		while ((idx = nodes.indexOf(existing)) != -1) {
			nodes.set(idx, newNode);
		}
	}	
	
	/**
	 * return true if first == last node, will not work for broken geometries
	 * @return
	 */
	public boolean isClosed() {
		return nodes.get(0).equals(nodes.get(nodes.size() - 1));
	}
	
	/**
	 * Checks if a node is an end node of the way (i.e. either the first or the last one)
	 * @param node a node to check
	 * @return 
	 */
	public boolean isEndNode(final Node node) {
		return getFirstNode() == node || getLastNode() == node;
	}
	
	/**
	 * Returns the first node of this way.
	 * @return
	 */
	public Node getFirstNode() {
		return nodes.get(0);
	}

	/**
	 * Returns the last node of this way.
	 * @return
	 */
	public Node getLastNode() {
		return nodes.get(nodes.size() - 1);
	}

	/** 
	 * return the number of nodes in the is way
	 * @return
	 */
	public int length() {
		return nodes.size();
	}		
	
}