package io.github.data4all.util;

import android.annotation.SuppressLint;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.model.data.Way;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

@SuppressLint("SimpleDateFormat")
public class OsmChangeParser {

	
	public static void parse(String filename, List<OsmElement> elems, long changesetID){
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			ArrayList<Node> nodes = new ArrayList<Node>();
			ArrayList<Way> ways = new ArrayList<Way>();
			ArrayList<Relation> relations = new ArrayList<Relation>();
			ArrayList<RelationMember> relationMembers = new ArrayList<RelationMember>();
			
			for(OsmElement osm :elems){
				if(osm.getClass().isInstance(Node.class)){
					nodes.add((Node) osm);
				}
				if(osm.getClass().isInstance(Way.class)){
					Way way =(Way) osm;
					nodes.addAll(way.getNodes());
					ways.add(way);
				}
				if(osm.getClass().isInstance(Relation.class)){
					Relation rel =(Relation) osm;
					rel.getMembers();
					//TODO: RelationMembers hinzufügen benötigt DataBaseHandler
					relations.add(rel);
				}
			}
			
			for (Node n: nodes){
				parseNode(writer,n,changesetID);
			}
			for (Way w: ways){
				parseWay(writer,w,changesetID);
			}
			for (Relation r: relations){
				parseRelation(writer,r,changesetID);
			}
			
			writer.flush();
			Log.i("OsmChangeParser", "Data is flushed");
			writer.close();
			Log.i("OsmChangeParser", "Writer is closed");			
			
		} catch (IOException e) {
			Log.e("OsmChangeParser", "Problem in writing the OsmChangeFile");
			e.printStackTrace();
		}
	}
	
	private static void parseNode(PrintWriter writer, Node node, long changesetID){
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SXXX");
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
				"yyyy-MM-dd'T'HH:mm:ss.SXXX");
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
				"yyyy-MM-dd'T'HH:mm:ss.SXXX");
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
