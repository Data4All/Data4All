package io.github.data4all;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeechRecognition {

	 public void speechToTag(List<String> matchesText){
			List<String> list = new ArrayList<String>();
			Map<String, String> tagData = new HashMap<String, String>();
			list.add("primary");
			list.add("motorway");
			list.add("secondary");
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0 ; j < matchesText.size() ; j++) {				
					if(list.get(i).equals(matchesText.get(j))){
						tagData.put("highway",list.get(i));
					}
				}
		
			}	
				matchesText.clear();
				matchesText.add("highway = " + tagData.get("highway")); 
				
		 }
	
	 public void splitStrings(List<String> matchesText){
		 for (int j = 0; j < matchesText.size(); j++) {
			String[] split;
			split = matchesText.get(j).split(" ");
				for (int i = 0; i < split.length; i++) {
					if(!matchesText.contains(split[i])){
						matchesText.add(split[i]);
					}
				}
		}
}
}
