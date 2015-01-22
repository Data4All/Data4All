package io.github.data4all.util;

import android.annotation.SuppressLint;
import android.content.Context;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.model.data.Way;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;


/**
 * @author Richard
 *
 */
@SuppressLint("SimpleDateFormat")
public class OsmChangeParser {

	/** Parses a List of OsmElements into the OSM Change Format
	 * 
	 * @param Context of the Application
	 * @param elems the List of Element which should be uploaded
	 * @param changesetID the changesetID requiered for the upload
	 */
	
	
	public static void parse(Context context, List<OsmElement> elems, long changesetID){
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(context.getFilesDir().getAbsolutePath() + "/OsmChangeUpload.osc"))));
			ArrayList<Node> nodes = new ArrayList<Node>();
			ArrayList<Way> ways = new ArrayList<Way>();
			ArrayList<Relation> relations = new ArrayList<Relation>();
			ArrayList<RelationMember> relationMembers = new ArrayList<RelationMember>();
			
			for(OsmElement osm :elems){
				if(osm.osmType.equals("NODE")){ 
					nodes.add((Node) osm);
				}
				if(osm.osmType.equals("WAY")){ 
					Way way =(Way) osm;
					for (Node n : way.getNodes()){
						if(!nodes.contains(n)){
							nodes.add(n);
						}
					}
					ways.add(way);
				}
				if(osm.osmType.equals("RELATION")){
					Relation rel =(Relation) osm;
					rel.getMembers();
					//TODO: RelationMembers hinzufügen benötigt DataBaseHandler
					relations.add(rel);
				}
			}
			
			// Ab hier wird geparst
			
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<osmChange version=\"1\" generator=\"Data4All\">");
			writer.println("<create>");
			
			for (Node n: nodes){
				Log.i("OsmChangeParser", "Node parsed" + n.toString());
				parseNode(writer,n,changesetID);
				Log.i("OsmChangeParser", "Node parsed");
			}
			for (Way w: ways){
				parseWay(writer,w,changesetID);
			}
			for (Relation r: relations){
				parseRelation(writer,r,changesetID);
			}
			
			writer.println("</create>");
			writer.println("</osmChange>");
			
			writer.flush();
			Log.i("OsmChangeParser", "Data is flushed to :" + context.getFilesDir().getAbsolutePath() + "/OsmChangeUpload.osc");
			writer.close();
			Log.i("OsmChangeParser", "Writer is closed");			
			
		} catch (IOException e) {
			Log.e("OsmChangeParser", "Problem in writing the OsmChangeFile");
			e.printStackTrace();
		}
	}
	
	private static void parseNode(PrintWriter writer, Node node, long changesetID){
		Log.i("OsmChangeParser", "in die Methode");
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SZ");
		
		writer.print("<node id=\""+ node.getOsmId() +"\" timestamp=\""+ dateformat.format(new Date()) 
				+ "\" lat=\""+ node.getLat() + "\" lon=\""+ node.getLon() 
				+ "\" changeset=\""+ changesetID + "\" version=\""+ node.getOsmVersion() +"\""); 
		
		SortedMap<String, String> tags = node.getTags(); 
		if(tags.isEmpty()){
			writer.print("/>");
			writer.println();
			return;
		}
		writer.print(">");
		writer.println();
	    for (String key : tags.keySet()){
	    	writer.println("<tag k=\""+ key + "\" v=\""+ tags.get(key) +"\"/>");
	    }
		writer.println("</node>");
	}
	
	private static void parseWay(PrintWriter writer, Way way, long changesetID){
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SZ");
		writer.println("<way id=\""+ way.getOsmId() +"\" timestamp=\""+ dateformat.format(new Date()) 
				+ "\" changeset=\""+ changesetID + "\" version=\""+ way.getOsmVersion() +"\">"); 
		SortedMap<String, String> tags = way.getTags();
		for(Node nd: way.getNodes()){
			writer.println("<nd ref=\"" + nd.getOsmId() +"\"/>");
		}
		
	    for (String key : tags.keySet()){
	    	writer.println("<tag k=\""+ key + "\" v=\""+ tags.get(key) +"\"/>");
	    }
		writer.println("</way>");	
	}
	
	private static void parseRelation(PrintWriter writer, Relation relation,long changesetID){
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SZ");
		writer.println("<relation id=\""+ relation.getOsmId() +"\" timestamp=\""+ dateformat.format(new Date()) 
				+ "\" changeset=\""+ changesetID + "\" version=\""+ relation.getOsmVersion() +"\">"); 
		SortedMap<String, String> tags = relation.getTags();
		for(RelationMember member: relation.getMembers()){
			writer.println("<member type=\"" + member.getType() 
					+"\" ref=\"" + member.getRef()
					+"\" role=\"" + member.getRole()+"\"/>");
		}
		
	    for (String key : tags.keySet()){
	    	writer.println("<tag k=\""+ key + "\" v=\""+ tags.get(key) +"\"/>");
	    }
		writer.println("</relation>");
	}
	
	
	
	
	
	
}
