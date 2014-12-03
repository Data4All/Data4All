package io.github.data4all;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {	

	private SpeechRecognition speechRecognition;
	private Button start;
	private Dialog matchText;
	private List<String> matchesText;
	Map<String, String> map;
	public List<String> getMatchesText() {
		return matchesText;
	}

	public void setMatchesText(String string) {
		matchesText.add(string);
	}
	private ListView textList;
	private static final int REQUEST_CODE = 1234;
	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = (Button) findViewById(R.id.speech);
		start.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		        startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
		     matchText = new Dialog(MainActivity.this);
		     matchText.setContentView(R.layout.dialog_matches);
		     matchText.setTitle(R.string.selectTag);
		     textList = (ListView)matchText.findViewById(R.id.list);
		     matchesText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		     System.out.println(matchesText);
		     speechRecognition = new SpeechRecognition(); 
		     speechRecognition.splitStrings(matchesText);
		     System.out.println(matchesText);
		     map = speechRecognition.speechToTag(matchesText);
		     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matchesText);
		     textList.setAdapter(adapter);
		     textList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    	 @Override
		    	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    		 start.setText(map.get("highway"));   		 
		    		 matchText.hide();    		
	     }
		     });
		     matchText.show();
	     }
	     super.onActivityResult(requestCode, resultCode, data);
	    }
	 
	 
	 /**
	 private void speechToTag(){
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
	 // Split the Strings and 
	 private void splitStrings(){
		 for (int j = 0; j < matchesText.size(); j++) {
			String[] split;
			split = matchesText.get(j).split(" ");
				for (int i = 0; i < split.length; i++) {
					if(!matchesText.contains(split[i])){
						matchesText.add(split[i]);
					}
				}
		}
	 }*/
}	 

