package io.github.data4all;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
/**
 * 
 * @author Maurice Boyke
 *
 */
public class MainActivity extends Activity {	


	private static final int REQUEST_CODE = 1234;
	final Context context = this;
	private ArrayList<String> keys;
	private String key;
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
		Button start = (Button) findViewById(R.id.speech);
		start.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		        startActivityForResult(intent, REQUEST_CODE);
			}
		});
		Button startTagging = (Button) findViewById(R.id.startTagging);
		startTagging.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.setContentView(R.layout.dialog_matches);
				dialog.setTitle("Select Tag");
				final Tagging tagging = new Tagging();
				final ListView keyList = (ListView)dialog.findViewById(R.id.list);
				keys = (ArrayList<String>) tagging.getKeys();
				System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
				System.out.println(keys);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, keys);
				keyList.setAdapter(adapter);  
				keyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				     @Override
				     public void onItemClick(AdapterView<?> parent, View view,
				                             int position, long id) {
				       key = keys.get(position);
				       keys = (ArrayList<String>) tagging.getValues(key);
				       ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, keys);
						keyList.setAdapter(adapter); 
						keyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						     @Override
						     public void onItemClick(AdapterView<?> parent, View view,
						                             int position, long id) {
						    	 
						    	 String value = keys.get(position);
						    	 List<String> endList = new ArrayList<String>();
						    	 endList.add(key +" = " + value);
						    	 
						    	 ListView textList = (ListView)findViewById(R.id.listView1);
							     ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, endList);
							     textList.setAdapter(adapter);  
							     Map <String, String> map = new HashMap<String, String>();
							     map = tagging.hashMapTag(key, value);
						       dialog.hide();
						     }
						 });
						
				     }
				 });
				dialog.show();
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
		     new Dialog(MainActivity.this);
		     ListView textList = (ListView)findViewById(R.id.listView1);
		     List<String> matchesText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		     SpeechRecognition speechRecognition = new SpeechRecognition(); 
		     speechRecognition.splitStrings(matchesText);
		     Map<String, String> map = speechRecognition.speechToTag(matchesText);
		     matchesText.clear();
		     Iterator<String> keySetIterator = map.keySet().iterator();
		     while(keySetIterator.hasNext()){
					String key = keySetIterator.next();
					matchesText.add(key + "=" + map.get(key));
		     }
		     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matchesText);
		     textList.setAdapter(adapter);    		
	     }
	     super.onActivityResult(requestCode, resultCode, data);
	    }
	 
	 }	 

