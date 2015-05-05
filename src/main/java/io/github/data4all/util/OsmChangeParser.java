/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.util;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.DataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.Tag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * The OsmChangeParser is a util for Parsing a List of DataElements into a File
 * which can then be uploaded to the OSM API.
 * 
 * @author Richard
 * @author tbrose (rearranging)
 */
@SuppressLint("SimpleDateFormat")
public final class OsmChangeParser {

    private static final String TAG = "OsmChangeParser";
    private static final String TIMEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SZZZZZ";
    private static final String TIMESTAMP = "\" timestamp=\"";
    private static final String CHANGESET = "\" changeset=\"";
    private static final String TAGKEY = "<tag k=\"";
    private static final String TAGVALUE = "\" v=\"";

    /**
     * Private Constructor, prevents instantiation.
     */
    private OsmChangeParser() {

    }

    /**
     * Parses a List of OsmElements into the OSM Change Format.
     * 
     * @param context
     *            of the Application
     * @param elems
     *            the List of Element which should be uploaded
     * @param changesetID
     *            the changesetID required for the upload
     */
    public static void parseElements(Context context,
            List<DataElement> elems, long changesetID) {
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(new File(context.getFilesDir()
                            .getAbsolutePath() + "/OsmChangeUpload.osc"))));
            parseElements(elems, changesetID, writer);
            writer.close();
            Log.i(TAG, "Writer is closed");
        } catch (IOException e) {
            Log.e(TAG, "Problem in writing the OsmChangeFile", e);
        }

    }

    /**
     * Parses a List of OsmElements into the OSM Change Format.
     * 
     * @param elems
     *            the List of Element which should be uploaded.
     * @param changesetID
     *            the changesetID required for the upload.
     * @param writer
     *            {@link PrintWriter} object.
     */
    public static void parseElements(List<DataElement> elems,
            long changesetID, PrintWriter writer) {
        final List<Node> nodes = new ArrayList<Node>();
        final List<PolyElement> ways = new ArrayList<PolyElement>();
        final List<PolyElement> relations = new ArrayList<PolyElement>();

        for (DataElement osm : elems) { // NOSONAR
            if (osm instanceof Node) {
                nodes.add((Node) osm);
            }
            if (osm instanceof PolyElement) {
                handlePolyElement(osm, nodes, ways, relations);
            }
        }

        // From here Parsing
        parseData(nodes, ways, relations, changesetID, writer);
    }

    /**
     * Method of how to handle a PolyElement.
     * 
     * @param osm
     *            the AbstractDataElement
     * @param nodes
     *            the list of nodes which should be uploaded
     * @param ways
     *            the list of ways which should be uploaded
     * @param relations
     *            the list of relations which should be uploaded
     */
    private static void handlePolyElement(
            DataElement osm, // NOSONAR
            List<Node> nodes, List<PolyElement> ways,
            List<PolyElement> relations) {
        final PolyElement poly = (PolyElement) osm;
        for (Node n : poly.getNodes()) {
            if (!nodes.contains(n)) {
                nodes.add(n);
            }
        }
        switch (poly.getType()) {
        case WAY:
            ways.add(poly);
            break;
        case AREA:
            relations.add(poly);
            break;
        case BUILDING:
            relations.add(poly);
            break;
        default:
            break;
        }
    }

    /**
     * Method to parse the general Data.
     */
    private static void parseData(List<Node> nodes, List<PolyElement> ways,
            List<PolyElement> relations, long changesetID, PrintWriter writer) {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<osmChange version=\"1\" generator=\"Data4All\">");
        writer.println("<create>");

        for (Node n : nodes) {
            Log.i(TAG, "Node parsed ID:" + n.getOsmId() + " LAT: " + n.getLat()
                    + " LONG: " + n.getLon());
            parseNode(writer, n, changesetID);
            Log.i(TAG, "Node parsed ID:" + n.getOsmId() + " LAT: " + n.getLat()
                    + " LONG: " + n.getLon());
        }
        for (PolyElement w : ways) {
            parseWay(writer, w, changesetID);
        }
        for (PolyElement r : relations) {
            parseWay(writer, r, changesetID);
        }

        writer.println("</create>");
        writer.println("</osmChange>");

        writer.flush();
        Log.i(TAG, "Data is flushed");
    }

    /**
     * Parses a Node into the OSM Change Format.
     * 
     * @param writer
     *            the writer where the Node is parsed
     * @param node
     *            the Node which should be parsed
     * @param changesetID
     *            the changesetID required for the upload
     */
    private static void parseNode(PrintWriter writer, Node node,
            long changesetID) {
        final SimpleDateFormat dateformat = new SimpleDateFormat(TIMEFORMAT);
        Log.d(TAG, "<node id=\"" + getId(node.getOsmId()) + TIMESTAMP
                + dateformat.format(new Date()) + "\" lat=\"" + node.getLat()
                + "\" lon=\"" + node.getLon() + CHANGESET + changesetID
                + "\" version=\"1\"");

        writer.print("<node id=\"" + getId(node.getOsmId()) + TIMESTAMP
                + dateformat.format(new Date()) + "\" lat=\"" + node.getLat()
                + "\" lon=\"" + node.getLon() + CHANGESET + changesetID
                + "\" version=\"1\"");

        final Map<Tag, String> tags = (LinkedHashMap<Tag, String>) node
                .getTags();
        if (tags.isEmpty()) {
            writer.print("/>");
            writer.println();
            return;
        }
        writer.print(">");
        writer.println();
        for (Tag key : tags.keySet()) {
            writer.println(TAGKEY + key.getKey() + TAGVALUE + tags.get(key)
                    + "\"/>");
        }
        writer.println("</node>");
    }

    /**
     * Parses a Way into the OSM Change Format.
     * 
     * @param writer
     *            the writer where the PolyElement is parsed
     * @param way
     *            the PolyElement WAY which should be parsed
     * @param changesetID
     *            the changesetID required for the upload
     */
    private static void parseWay(PrintWriter writer, PolyElement way,
            long changesetID) {
        final SimpleDateFormat dateformat = new SimpleDateFormat(TIMEFORMAT);
        writer.println("<way id=\"" + getId(way.getOsmId()) + TIMESTAMP
                + dateformat.format(new Date()) + CHANGESET + changesetID
                + "\" version=\"1\">");
        final Map<Tag, String> tags = (LinkedHashMap<Tag, String>) way
                .getTags();
        for (Node nd : way.getNodes()) {
            writer.println("<nd ref=\"" + getId(nd.getOsmId()) + "\"/>");
        }
        if (way.getType() != PolyElementType.WAY) {
            writer.println("<nd ref=\"" + getId(way.getFirstNode().getOsmId())
                    + "\"/>");
        }
        for (Tag key : tags.keySet()) {
            writer.println(TAGKEY + key.getKey() + TAGVALUE + tags.get(key)
                    + "\"/>");
        }
        writer.println("</way>");
    }

    /**
     * Parses a Relation into the OSM Change Format.
     * 
     * @param writer
     *            the writer where the PolyElement is parsed
     * @param relation
     *            the PolyElement AREA or BUILDING which should be parsed
     * @param changesetID
     *            the changesetID required for the upload
     */
    private static void parseRelation(PrintWriter writer, PolyElement relation,
            long changesetID) {
        final SimpleDateFormat dateformat = new SimpleDateFormat(TIMEFORMAT);
        writer.println("<relation id=\"" + getId(relation.getOsmId())
                + TIMESTAMP + dateformat.format(new Date()) + CHANGESET
                + changesetID + "\" version=\"1\">");
        final Map<Tag, String> tags = (LinkedHashMap<Tag, String>) relation
                .getTags();
        for (Node member : relation.getNodes()) {
            writer.println("<member type=\"node\" ref=\""
                    + getId(member.getOsmId()) + "\" role=\"\"/>");
        }

        for (Tag key : tags.keySet()) {
            writer.println(TAGKEY + key.getKey() + TAGVALUE + tags.get(key)
                    + "\"/>");
        }
        writer.println("</relation>");
    }

    /**
     * Returns a negative version of the given id.<br/>
     * If the id is negative, nothing is changed.
     * 
     * @param osmId
     *            The osmId of an object
     * @return the negative version of this id
     */
    private static long getId(long osmId) {
        return (long) (-1 * Math.abs(osmId));
    }

}
