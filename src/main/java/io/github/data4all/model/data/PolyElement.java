package io.github.data4all.model.data;

import io.github.data4all.logger.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A PolyElement is an ordered list of nodes which normally also has at least
 * one tag or is included within a Relation. A PolyElement can have between 2
 * and 2,000 nodes. A PolyElement can be open or closed. A closed PolyElement is
 * one whose last node on the PolyElement is also the first on that PolyElement.
 * 
 * @author fkirchge
 *
 */
public class PolyElement extends AbstractDataElement {

    /**
     * List of nodes to define a PolyElement (PolyElementpoints).
     */
    private List<Node> nodes = null;

    /**
     * Max. number of nodes.
     */
    private final int MAX_POLYELEMENT_NODES = 2000;

    /**
     * type of the PolyElement.
     */
    public enum PolyElementType {
        WAY, AREA, BUILDING
    };

    private PolyElementType type;

    /**
     * Default Constructor.
     * 
     * @param osmId
     * @param osmVersion
     */
    public PolyElement(long osmId, PolyElementType type) {
        super(osmId);
        this.type = type;
        this.nodes = new LinkedList<Node>();
    }

    /**
     * Adds a new node to the PolyElement. If the last node equals the new node
     * you have to use append to define a closed PolyElement.
     * 
     * @param node
     */
    public boolean addNode(final Node node) {
        if ((nodes.size() > 0) && (nodes.get(nodes.size() - 1) == node)) {
            Log.i(getClass().getSimpleName(),
                    "addNode attempt to add same node, use appendNode instead");
            return false;
        }
        if (nodes.size() >= MAX_POLYELEMENT_NODES) {
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
        if (nodes.size() >= MAX_POLYELEMENT_NODES) {
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
     * Adds multiple nodes to the PolyElement in the order in which they appear
     * in the list. They can be either prepended or appended to the existing
     * nodes.
     * 
     * @param newNodes
     *            a list of new nodes
     * @param atBeginning
     *            if true, nodes are prepended, otherwise, they are appended
     */
    public void addNodes(List<Node> newNodes, boolean atBeginning) {
        if (newNodes.size() < MAX_POLYELEMENT_NODES) {
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
        if (refNode == newNode) {
            Log.i(getClass().getSimpleName(),
                    "appendNode unable to add new node, refNode equals newNode");
            return false;
        }
        if (nodes.size() >= MAX_POLYELEMENT_NODES) {
            Log.d(getClass().getSimpleName(),
                    "appendNode attempt to add more than 2000 nodes");
            return false;
        }
        if (nodes.get(0) == refNode) {
            nodes.add(0, newNode);
            Log.i(getClass().getSimpleName(),
                    "appendNode successfully added new node: "
                            + newNode.getOsmId()
                            + "  at the begin of the PolyElementpoint list");
            return true;
        } else if (nodes.get(nodes.size() - 1) == refNode) {
            nodes.add(newNode);
            Log.i(getClass().getSimpleName(),
                    "appendNode successfully added new node: "
                            + newNode.getOsmId()
                            + " at the end of the PolyElementpoint list");
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns the first node of this PolyElement.
     * 
     * @return node
     */
    public Node getFirstNode() {
        return nodes.get(0);
    }

    /**
     * Returns all points which belong to the PolyElement.
     * 
     * @return list of points
     */
    public List<org.osmdroid.util.GeoPoint> getGeoPoints() {
        List<org.osmdroid.util.GeoPoint> points = new LinkedList<org.osmdroid.util.GeoPoint>();
        for (Node n : nodes) {
            points.add(n.toGeoPoint());
        }
        return points;
    }

    /**
     * Returns all points which belong to the PolyElement.
     * 
     * @return list of points
     */
    public ArrayList<org.osmdroid.util.GeoPoint> getUnsortedGeoPoints() {
        ArrayList<org.osmdroid.util.GeoPoint> points = new ArrayList<org.osmdroid.util.GeoPoint>();
        for (Node n : nodes) {
            points.add(n.toGeoPoint());
        }
        return points;
    }

    /**
     * Returns the last node of this PolyElement.
     * 
     * @return node
     */
    public Node getLastNode() {
        return nodes.get(nodes.size() - 1);
    }

    /**
     * Returns all nodes which belong to the PolyElement.
     * 
     * @return list of nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    public PolyElementType getType() {
        return type;
    }

    public void setType(PolyElementType type) {
        this.type = type;
    }

    /**
     * Returns true if the given PolyElement contains common nodes with the this
     * PolyElement object.
     * 
     * @param PolyElement
     * @return true/false
     */
    public boolean hasCommonNode(final PolyElement PolyElement) {
        for (Node n : this.nodes) {
            if (PolyElement.hasNode(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the node is part of the PolyElement.
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
     * Checks if a node is an end node of the PolyElement (i.e. either the first
     * or the last one)
     * 
     * @param node
     *            a node to check
     * @return true/false
     */
    public boolean isEndNode(final Node node) {
        return getFirstNode() == node || getLastNode() == node;
    }

    /**
     * return the number of nodes in the is PolyElement
     * 
     * @return int
     */
    public int length() {
        return nodes.size();
    }

    /**
     * Removes a node from the PolyElement.
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
     * Replace an existing node in a PolyElement with a different node.
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
    }

    /**
     * Methods to write and restore a Parcel.
     */
    public static final Parcelable.Creator<PolyElement> CREATOR = new Parcelable.Creator<PolyElement>() {

        public PolyElement createFromParcel(Parcel in) {
            return new PolyElement(in);
        }

        public PolyElement[] newArray(int size) {
            return new PolyElement[size];
        }
    };

    /**
     * Writes the nodes to the given parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(nodes);
        switch (type) {
        case WAY:
            dest.writeInt(1);
            break;
        case AREA:
            dest.writeInt(2);
            break;
        case BUILDING:
            dest.writeInt(3);
            break;
        }

    }

    /**
     * Constructor to create a PolyElement from a parcel.
     * 
     * @param in
     */
    private PolyElement(Parcel in) {
        super(in);
        nodes = new LinkedList<Node>();
        in.readTypedList(nodes, Node.CREATOR);
        int typeInt = in.readInt();
        switch (typeInt) {
        case 1: 
            type = PolyElementType.WAY;
            break;
        case 2: 
            type = PolyElementType.AREA;
            break;
        case 3: 
            type = PolyElementType.BUILDING;
            break;
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (Node n : nodes) {
            s += n.toString() + " ";
        }
        return s;
    }
}
