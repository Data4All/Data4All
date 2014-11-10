package io.github.businessmodel;

import java.util.ArrayList;

/**
 * This class represents a way inside open street map.
 * A way is an ordered list of nodes which normally also has at least one tag or is included within a Relation.
 * A way can have between 2 and 2,000 nodes, although it's possible that faulty ways with zero or a single node exist. 
 * @author Felix Kirchgeorg
 *
 */
public class Way extends OsmObject {
	
	private ArrayList<Node> nds;
	
	public Way() {
		
	}
	
	public Way(ArrayList<Node> nodes) {
		this.nds = nodes; 
	}

	public ArrayList<Node> getNds() {
		return nds;
	}

	public void setNds(ArrayList<Node> nds) {
		this.nds = nds;
	} 
	
	/**
	 * Adds a node point to the way.
	 * @param nd 
	 */
	public void addNd(Node nd) {
		if (nds.size() <= 2000) {
			nds.add(nd);
		}
	}
	
	/**
	 * Removes a node point from the way.
	 * @param nd
	 */
	public void removeNd(Node nd) {
		if (nds.contains(nd)) {
			nds.remove(nd);
		}
	}
	
}