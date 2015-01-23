package io.github.data4all.model.data;

import io.github.data4all.logger.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A way is an ordered list of nodes which normally also has at least one tag or
 * is included within a Relation. A way can have between 2 and 2,000 nodes. A
 * way can be open or closed. A closed way is one whose last node on the way is
 * also the first on that way.
 * 
 * @author fkirchge
 *
 */
public class Way extends OsmElement {

    /**
     * List of nodes to define a way (waypoints).
     */
    private List<Node> nodes = null;

    /**
     * Max. number of nodes.
     */
    private final int MAX_WAY_NODES = 2000;

    /**
     * CREATOR that generates instances of {@link Way} from a Parcel
     */
    public static final Parcelable.Creator<Way> CREATOR = new Parcelable.Creator<Way>() {
        public Way createFromParcel(Parcel in) {
            return new Way(in);
        }

        public Way[] newArray(int size) {
            return new Way[size];
        }
    };

    /**
     * Default Constructor
     * 
     * @param osmId
     * @param osmVersion
     */
    public Way(long osmId, long osmVersion) {
        super(osmId, osmVersion);
        nodes = new LinkedList<Node>();
    }

    /**
     * Constructor to create a {@link Way} from a parcel.
     * 
     * @param in
     *            The {@link Parcel} to read the object's data from
     */
    private Way(Parcel in) {
        super(in);
        nodes = new LinkedList<Node>();
        in.readTypedList(nodes, Node.CREATOR);
    }

    /**
     * Adds a new node to the way. If the last node equals the new node you have
     * to use append to define a closed way.
     * 
     * @param node
     */
    public boolean addNode(final Node node) {
        if ((nodes.size() > 0) && (nodes.get(nodes.size() - 1) == node)) {
            Log.i(getClass().getSimpleName(),
                    "addNode attempt to add same node, use appendNode instead");
            return false;
        }
        if (nodes.size() >= MAX_WAY_NODES) {
            Log.d(getClass().getSimpleName(),
                    "addNode attempt to add more than 2000 nodes");
            return false;
        }
        nodes.add(node);
        Log.d(getClass().getSimpleName(),
                "successfully added a new node with id: " + node.getOsmId());
        return true;
    }

    /**
     * Insert a node behind the reference node.
     * 
     * @param nodeBefore
     * @param newNode
     */
    public boolean addNodeAfter(final Node nodeBefore, final Node newNode) {
        if (nodeBefore == newNode) { // user error
            Log.i(getClass().getSimpleName(),
                    "addNodeAfter unable to add new node, refNode equals newNode");
            return false;
        }
        if (nodes.size() >= MAX_WAY_NODES) {
            Log.d(getClass().getSimpleName(),
                    "addNodeAfter attempt to add more than 2000 nodes");
            return false;
        }
        nodes.add(nodes.indexOf(nodeBefore) + 1, newNode);
        Log.i(getClass().getSimpleName(),
                "addNodeAfter successfully added new node: "
                        + newNode.getOsmId() + " after the refNode");
        return true;
    }

    /**
     * Adds multiple nodes to the way in the order in which they appear in the
     * list. They can be either prepended or appended to the existing nodes.
     * 
     * @param newNodes
     *            a list of new nodes
     * @param atBeginning
     *            if true, nodes are prepended, otherwise, they are appended
     */
    public void addNodes(List<Node> newNodes, boolean atBeginning) {
        if (newNodes.size() < MAX_WAY_NODES) {
            if (atBeginning) {
                if ((nodes.size() > 0)
                        && nodes.get(0) == newNodes.get(newNodes.size() - 1)) { // user
                    // error
                    Log.i(getClass().getSimpleName(),
                            "addNodes attempt to add same node");
                    if (newNodes.size() > 1) {
                        Log.i(getClass().getSimpleName(), "retrying addNodes");
                        newNodes.remove(newNodes.size() - 1);
                        addNodes(newNodes, atBeginning);
                    }
                    return;
                }
                nodes.addAll(0, newNodes);
            } else {
                if ((nodes.size() > 0)
                        && newNodes.get(0) == nodes.get(nodes.size() - 1)) { // user
                    // error
                    Log.i(getClass().getSimpleName(),
                            "addNodes attempt to add same node");
                    if (newNodes.size() > 1) {
                        Log.i(getClass().getSimpleName(), "retrying addNodes");
                        newNodes.remove(0);
                        addNodes(newNodes, atBeginning);
                    }
                    return;
                }
                nodes.addAll(newNodes);
            }
        } else {
            Log.i(getClass().getSimpleName(),
                    "the list of newNodes contains to much nodes");
        }
    }

    /**
     * Append a node at the begin or end of the list. If the refNode is the
     * first element, the new node is added at the begin of the list. If the
     * refNode is the last element, the new node is added to the end of the
     * list.
     * 
     * @param refNode
     * @param newNode
     */
    public boolean appendNode(final Node refNode, final Node newNode) {
        if (refNode == newNode) { // user error
            Log.i(getClass().getSimpleName(),
                    "appendNode unable to add new node, refNode equals newNode");
            return false;
        }
        if (nodes.size() >= MAX_WAY_NODES) {
            Log.d(getClass().getSimpleName(),
                    "appendNode attempt to add more than 2000 nodes");
            return false;
        }
        if (nodes.get(0) == refNode) {
            nodes.add(0, newNode);
            Log.i(getClass().getSimpleName(),
                    "appendNode successfully added new node: "
                            + newNode.getOsmId()
                            + "  at the begin of the waypoint list");
            return true;
        } else if (nodes.get(nodes.size() - 1) == refNode) {
            nodes.add(newNode);
            Log.i(getClass().getSimpleName(),
                    "appendNode successfully added new node: "
                            + newNode.getOsmId()
                            + " at the end of the waypoint list");
            return true;
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    /**
     * Returns the first node of this way.
     * 
     * @return node
     */
    public Node getFirstNode() {
        return nodes.get(0);
    }
	/**
	 * Returns all points which belong to the way.
	 * 
	 * @return list of points
	 */
	public List<org.osmdroid.util.GeoPoint> getGeoPoints() {
		List<org.osmdroid.util.GeoPoint> points = new LinkedList<org.osmdroid.util.GeoPoint>();
		for (Node n : nodes){
			points.add(n.toGeoPoint());
		}
		return points;
	}

	/**
	 * Returns all points which belong to the way.
	 * 
	 * @return list of points
	 */
	public ArrayList<org.osmdroid.util.GeoPoint> getUnsortedGeoPoints() {
		ArrayList<org.osmdroid.util.GeoPoint> points = new ArrayList<org.osmdroid.util.GeoPoint>();
		for (Node n : nodes){
			points.add(n.toGeoPoint());
		}
		return points;
	}
	
	/**
	 * Returns true if the node is part of the way.
	 * 
	 * @param node
	 * @return true/false
	 */
	public boolean hasNode(final Node node) {
		return nodes.contains(node);
	}

    /**
     * Returns the last node of this way.
     * 
     * @return node
     */
    public Node getLastNode() {
        return nodes.get(nodes.size() - 1);
    }

    /**
     * Returns all nodes which belong to the way.
     * 
     * @return list of nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Returns true if the given way contains common nodes with the this way
     * object.
     * 
     * @param way
     * @return true/false
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
     * Returns true if the node is part of the way.
     * 
     * @param node
     * @return true/false
     */
    public boolean hasNode(final Node node) {
        return nodes.contains(node);
    }

    /**
     * return true if first == last node, will not work for broken geometries
     * 
     * @return true/false
     */
    public boolean isClosed() {
        return nodes.get(0).equals(nodes.get(nodes.size() - 1));
    }

    /**
     * Checks if a node is an end node of the way (i.e. either the first or the
     * last one)
     * 
     * @param node
     *            a node to check
     * @return true/false
     */
    public boolean isEndNode(final Node node) {
        return getFirstNode() == node || getLastNode() == node;
    }

    /**
     * return the number of nodes in the is way
     * 
     * @return int
     */
    public int length() {
        return nodes.size();
    }

    /**
     * Removes a node from the way.
     * 
     * @param node
     */
    public void removeNode(final Node node) {
        int index = nodes.lastIndexOf(node);
        if (index > 0 && index < (nodes.size() - 1)) { // not the first or last
            // node
            if (nodes.get(index - 1) == nodes.get(index + 1)) {
                nodes.remove(index - 1);
                Log.i(getClass().getSimpleName(),
                        "removeNode removed duplicate node");
            }
        }
        while (nodes.remove(node)) {
            ;
        }
    }

    /**
     * Replace an existing node in a way with a different node.
     * 
     * @param existing
     *            The existing node to be replaced.
     * @param newNode
     *            The new node.
     */
    public boolean replaceNode(Node existing, Node newNode) {
        if (nodes.contains(existing)) {
            int idx;
            while ((idx = nodes.indexOf(existing)) != -1) {
                nodes.set(idx, newNode);
            }
            Log.i(getClass().getSimpleName(),
                    "replaceNode replaced all existing node: "
                            + existing.getOsmId() + " with newNode: "
                            + newNode.getOsmId());
            return true;
        } else {
            Log.i(getClass().getSimpleName(),
                    "replaceNode cant replace existing node, nodes does not contain existing node");
            return false;
        }

	public void writeToParcel(Parcel dest, int flags) {		
		super.writeToParcel(dest, flags);
		dest.writeTypedList(nodes);
	}
	
	/**
	 * Constructor to create a way from a parcel.
	 * @param in
	 */
    private Way(Parcel in) {
    	super(in);
        nodes = new LinkedList<Node>();
        in.readTypedList(nodes, Node.CREATOR);
    }	
    
    public String toString(){
    	String s = "";
    	for(Node n : nodes){
    		s += n.toString() + " ";
    	}
    	return s;
    }
}
