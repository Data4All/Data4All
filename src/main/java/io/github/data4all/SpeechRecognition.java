package io.github.data4all;

import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Maurice Boyke
 *
 */
public class SpeechRecognition {

	/**
	 * 
	 * @param matchesText is a Array List from the results of the SpeechRecognition
	 * @return The HashMap of the matching Tags
	 */
	 public Map<String, String> speechToTag(List<String> matchesText){
			Map<String, String> map = new HashMap <String, String>();
			Map<String, String> tagData = new HashMap<String, String>();
			Tags tags = new Tags();
			tagData = tags.getClassifiedTags();
			Iterator<String> keySetIterator = tagData.keySet().iterator();
			while(keySetIterator.hasNext()){
				String key = keySetIterator.next();
				String [] split;
				split = tagData.get(key).split(",");
				for (int i = 0; i < matchesText.size(); i++) {
					for (int j = 0; j < split.length; j++) {
						if(matchesText.get(i).equalsIgnoreCase(split[j]) && map.get(key) == null){
							map.put(key, split[j]);
							break;
						}
					}
				}
			}
			return map;
		 }
	 
	 /**
	  * splitString splits all the Strings and adds them to the ArrayList
	  * @param matchesText is a Array List from the results of the SpeechRecognition 
	  */
	
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
