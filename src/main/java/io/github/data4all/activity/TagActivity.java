package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.SpeechRecognition;
import io.github.data4all.util.Tagging;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Maurice Boyke
 *
 */
public class TagActivity extends BasicActivity implements OnClickListener{

	//OSMElement Key
	protected static final String OSM = "OSM_ELEMENT";
    private static final int REQUEST_CODE = 1234;
    final Context context = this;
    private ArrayList<String> keys;
    private String key;
    private Map<Tag, String> map;
    private List <EditText> edit;
    private Boolean first;
    private Dialog dialog1;
    private CharSequence [] array;
    private AlertDialog alert;
    private AlertDialog alert1;
    private Map<String, ClassifiedTag> tagMap;

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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tag);    
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TagActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth);
        LayoutInflater inflater = getLayoutInflater();
        View view=inflater.inflate(R.drawable.header_listview, null);
        ((TextView) view.findViewById(R.id.titleDialog)).setText("Select Tag");;        
        alertDialog.setCustomTitle(view);
        ImageButton speechStart = (ImageButton) view.findViewById(R.id.speech); 
        speechStart.setOnClickListener(this);
        
        if (getIntent().hasExtra("TYPE_DEF")) {
        	array = Tagging.getArrayKeys(getIntent().getExtras().getInt("TYPE_DEF"));
        	tagMap = Tagging.getMapKeys(getIntent().getExtras().getInt("TYPE_DEF"));
        }
        
        alertDialog.setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	key = (String) array [which];
            	array =  tagMap.get(key).getClassifiedValues().toArray(new String [tagMap.get(key).getClassifiedValues().size()]);
            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TagActivity.this);
            	alertDialogBuilder.setTitle("Select Tag");
            	alertDialogBuilder.setItems(array, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String value = (String) array [which];
                        map = new LinkedHashMap<Tag, String>();
                        map.put(tagMap.get(key), value);
                        if (key.equals("building")
                                || key.equals("amenity")) {                                  
                            createDialog(Tags.getAllAddressTags(), "Add Address", key.equals("building"), true);
                        }
                        else{
                         finish();
                        }
					}
				});

                 alert1 = alertDialogBuilder.create();
                 alert1.show();
            }
            
        });alert = alertDialog.create();
        
        alert.show();
        
    }

    public void onClick(View v) {
    	switch (v.getId()){
  	case R.id.speech:
    		Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, REQUEST_CODE);
            alert.dismiss();
            break;
    	case R.id.buttonNext:
    		List<String> tags = new ArrayList<String>();
			
			for (int i = 0; i < edit.size(); i++) {
				tags.add(edit.get(i).getText().toString());
			}
			map = Tagging.addressToTag(tags, map);
			dialog1.dismiss();
			createDialog(Tags.getAllContactTags(), "Add Contacts", true, false);
			
			break;
    	case R.id.buttonFinish:	
    		List<String> tags1 = new ArrayList<String>();
			
			for (int i = 0; i < edit.size(); i++) {
				tags1.add(edit.get(i).getText().toString());
			}
			if(first){
				map = Tagging.addressToTag(tags1, map);
			}
			else{
				map = Tagging.contactToTag(tags1, map);
			}
			dialog1.dismiss();
			finish();
			break;
    	}
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        	new Dialog(TagActivity.this);
            ListView textList = (ListView) findViewById(R.id.listView1);
            List<String> matchesText = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SpeechRecognition.splitStrings(matchesText);
            Map<String, String> map = SpeechRecognition
                    .speechToTag(matchesText);
            matchesText.clear();
            for(Entry entry : map.entrySet()){
    			String key = (String) entry.getKey();
    			matchesText.add(key + "=" + map.get(key));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matchesText);
            textList.setAdapter(adapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * create the dialog for the address
     */

    /**
     * to show the tags in the listview
     */

    private void output() {
        List<String> endList = new ArrayList<String>();
        for(Entry entry : map.entrySet()){
			String key = (String) entry.getKey();endList.add(key + "=" + map.get(key));
            ListView textList = (ListView) findViewById(R.id.listView1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, endList);
            textList.setAdapter(adapter);
        }
    }



	public void createDialog(List<Tag> arrayList, String title, final Boolean but, final Boolean first1){
    	dialog1 = new Dialog(this);
		dialog1.setContentView(R.layout.dialog_dynamic);
		dialog1.setTitle(title);
		//dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E6808080")));
		LinearLayout layout = (LinearLayout) dialog1.findViewById(R.id.dialogDynamic);
		final Button next = new Button(this);
		final Button finish = new Button(this);
		next.setText(R.string.next);
		finish.setText(R.string.finish);
		next.setId(R.id.buttonNext);
		finish.setId(R.id.buttonFinish);
		first = first1;
		edit = new ArrayList<EditText>();
		for (int i = 0; i < arrayList.size(); i++) {
		final EditText text = new EditText(this);
			text.setHint(arrayList.get(i).getHintRessource());
			text.setHintTextColor(Color.DKGRAY);
    		text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    		//text.setInputType(arrayList.get(i).getType());
    		edit.add(text);
    		layout.addView(text);
		}
		finish.setOnClickListener(this);
		next.setOnClickListener(this);
		if(!but){
		layout.addView(next);
		}
		layout.addView(finish);
		dialog1.show();
	}
	
	

	@Override
	public void finish() {
	  AbstractDataElement element = getIntent().getParcelableExtra(OSM);
	  element.addTags(map);
	  Intent intent = new Intent(this, ResultViewActivity.class);
	  intent.putExtra(OSM, element);
	  intent.putExtra("TYPE_DEF", getIntent().getExtras().getInt("TYPE_DEF"));		
	  super.finish();
	  startActivity(intent);
	}
}