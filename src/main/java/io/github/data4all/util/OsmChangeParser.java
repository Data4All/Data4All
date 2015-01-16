package io.github.data4all.util;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.model.data.Way;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

public class OsmChangeParser {

	
	public static void parse(String filename){
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			
			
			
			writer.flush();
			Log.i("OsmChangeParser", "Data is flushed");
			writer.close();
			Log.i("OsmChangeParser", "Writer is closed");			
			
		} catch (IOException e) {
			Log.e("OsmChangeParser", "Problem in writing the OsmChangeFile");
			e.printStackTrace();
		}
	}
	
	private void parseNode(PrintWriter writer, Node node, long changesetID){
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
	
	private void parseWay(PrintWriter writer, Way way, long changesetID){
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
	
	private void parseRelation(PrintWriter writer, Relation relation,long changesetID){
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
