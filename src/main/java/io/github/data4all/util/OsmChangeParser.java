package io.github.data4all.util;

/**
 * The OsmChangeParser is a util for 
 * Parsing a List of DataElements into a File 
 * which can then be uploaded to the OSM API
 */
import android.annotation.SuppressLint;
import android.content.Context;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
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

/**
 * @author Richard
 *
 */
@SuppressLint("SimpleDateFormat")
public class OsmChangeParser {

	/**
	 * Parses a List of OsmElements into the OSM Change Format
	 * 
	 * @param Context
	 *            of the Application
	 * @param elems
	 *            the List of Element which should be uploaded
	 * @param changesetID
	 *            the changesetID required for the upload
	 */

	public static void parseElements(Context context,
			List<AbstractDataElement> elems, long changesetID) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(context.getFilesDir()
							.getAbsolutePath() + "/OsmChangeUpload.osc"))));
			ArrayList<Node> nodes = new ArrayList<Node>();
			ArrayList<PolyElement> ways = new ArrayList<PolyElement>();
			ArrayList<PolyElement> relations = new ArrayList<PolyElement>();

			for (AbstractDataElement osm : elems) {
				if (osm instanceof Node) {
					nodes.add((Node) osm);
				}
				if (osm instanceof PolyElement) {
					PolyElement poly = (PolyElement) osm;
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
					}
				}

			}

			// From here Parsing

			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<osmChange version=\"1\" generator=\"Data4All\">");
			writer.println("<create>");

			for (Node n : nodes) {
				Log.i("OsmChangeParser", "Node parsed" + n.toString());
				parseNode(writer, n, changesetID);
				Log.i("OsmChangeParser", "Node parsed");
			}
			for (PolyElement w : ways) {
				parseWay(writer, w, changesetID);
			}
			for (PolyElement r : relations) {
				parseRelation(writer, r, changesetID);
			}

			writer.println("</create>");
			writer.println("</osmChange>");

			writer.flush();
			Log.i("OsmChangeParser", "Data is flushed to :"
					+ context.getFilesDir().getAbsolutePath()
					+ "/OsmChangeUpload.osc");
			writer.close();
			Log.i("OsmChangeParser", "Writer is closed");

		} catch (IOException e) {
			Log.e("OsmChangeParser", "Problem in writing the OsmChangeFile");
			e.printStackTrace();
		}
	}

	/**
	 * Parses a Node into the OSM Change Format
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
		Log.i("OsmChangeParser", "in die Methode");
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SZ");

		writer.print("<node id=\"" + node.getOsmId() + "\" timestamp=\""
				+ dateformat.format(new Date()) + "\" lat=\"" + node.getLat()
				+ "\" lon=\"" + node.getLon() + "\" changeset=\"" + changesetID
				+ "\" version=\"1\"");

		LinkedHashMap<Tag, String> tags = (LinkedHashMap<Tag, String>) node
				.getTags();
		if (tags.isEmpty()) {
			writer.print("/>");
			writer.println();
			return;
		}
		writer.print(">");
		writer.println();
		for (Tag key : tags.keySet()) {
			writer.println("<tag k=\"" + key.getKey() + "\" v=\""
					+ tags.get(key) + "\"/>");
		}
		writer.println("</node>");
	}

	/**
	 * Parses a Way into the OSM Change Format
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
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SZ");
		writer.println("<way id=\"" + way.getOsmId() + "\" timestamp=\""
				+ dateformat.format(new Date()) + "\" changeset=\""
				+ changesetID + "\" version=\"1\">");
		LinkedHashMap<Tag, String> tags = (LinkedHashMap<Tag, String>) way
				.getTags();
		for (Node nd : way.getNodes()) {
			writer.println("<nd ref=\"" + nd.getOsmId() + "\"/>");
		}

		for (Tag key : tags.keySet()) {
			writer.println("<tag k=\"" + key.getKey() + "\" v=\""
					+ tags.get(key) + "\"/>");
		}
		writer.println("</way>");
	}

	/**
	 * Parses a Relation into the OSM Change Format
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
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SZ");
		writer.println("<relation id=\"" + relation.getOsmId()
				+ "\" timestamp=\"" + dateformat.format(new Date())
				+ "\" changeset=\"" + changesetID + "\" version=\"1\">");
		LinkedHashMap<Tag, String> tags = (LinkedHashMap<Tag, String>) relation
				.getTags();
		for (Node member : relation.getNodes()) {
			writer.println("<member type=\"Node\" ref=\"" + member.getOsmId()
					+ "\" role=\"\"/>");
		}

		for (Tag key : tags.keySet()) {
			writer.println("<tag k=\"" + key.getKey() + "\" v=\""
					+ tags.get(key) + "\"/>");
		}
		writer.println("</relation>");
	}

}
