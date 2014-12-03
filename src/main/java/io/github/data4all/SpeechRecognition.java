package io.github.data4all;

import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpeechRecognition {

	private Tags tags;
	 public Map<String, String> speechToTag(List<String> matchesText){
			Map<String, String> map = new HashMap <String, String>();
			Map<String, String> tagData = new HashMap<String, String>();
			tags = new Tags();
			tagData = tags.getClassifiedTags();
			Iterator<String> keySetIterator = tagData.keySet().iterator();
			while(keySetIterator.hasNext()){
				String key = keySetIterator.next();
				String [] split;
				split = tagData.get(key).split(",");
				for (int i = 0; i < matchesText.size(); i++) {
					for (int j = 0; j < split.length; j++) {
						if(matchesText.get(i).equalsIgnoreCase(split[j])){
							map.put(key, split[j]);
							break;
						}
					}
				}
			}
			return map;
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
