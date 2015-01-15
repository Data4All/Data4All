package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.SpeechRecognition;
import io.github.data4all.util.Tagging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 
 * @author Maurice Boyke
 *
 */
public class CopyOfTagActivity extends Activity implements OnClickListener{

    private static final int REQUEST_CODE = 1234;
    final Context context = this;
    private ArrayList<String> keys;
    private String key;
    private Map<String, String> map;
    private List <EditText> edit;
    private Boolean first;
    private Dialog dialog1;
    private ImageView imageView;

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
      //  this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tagcoppy);    
       /** imageView = (ImageView) findViewById(R.id.imageView5);
        if (getIntent().hasExtra("file_path")) {
           setBackground(Uri.fromFile((File) getIntent().getExtras().get(
                   "file_path")));}
       
            */
        final ListView keyList = (ListView) findViewById(R.id.listView1);
        //ImageButton start = (ImageButton) findViewById(R.id.speech2);
        //start.setOnClickListener(this);
        keys = (ArrayList<String>) Tagging.getKeys();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_list_item_1, keys);
        keyList.setAdapter(adapter);
        keyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                key = keys.get(position);
                keys = (ArrayList<String>) Tagging.getValues(key);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        context, android.R.layout.simple_list_item_1,
                        keys);
                keyList.setAdapter(adapter);
                keyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {

                        String value = keys.get(position);
                        map = new LinkedHashMap<String, String>();
                        map.put(key, value);
                        if (key.equals("building")
                                || key.equals("amenity")) {                                  
                            createDialog(Tags.getAddressTags(), "Add Address", key.equals("building"), true);
                        }else{
                        output();
                        }
                    }
                });

            }
        });
    }

    public void onClick(View v) {
    	switch (v.getId()){
  	case R.id.speech2:
    		Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, REQUEST_CODE);
            break;
    	case R.id.buttonNext:
    		List<String> tags = new ArrayList<String>();
			
			for (int i = 0; i < edit.size(); i++) {
				tags.add(edit.get(i).getText().toString());
			}
			map = Tagging.addressToTag(tags, map);
			
			createDialog(Tags.getContactTags(), "Add Contacts", true, false);
			
			break;
    	case R.id.buttonFinish:	
    		setContentView(R.layout.activity_tagcoppy);
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
			output();
			break;
    	}
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
        	new Dialog(CopyOfTagActivity.this);
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



	public void createDialog(String [] [] list, String title, final Boolean but, final Boolean first1){
		setContentView(R.layout.dialog_dynamic);
		LinearLayout layout = (LinearLayout) findViewById(R.id.dialogDynamic);
		final Button next = new Button(this);
		final Button finish = new Button(this);
		next.setText(R.string.next);
		finish.setText(R.string.finish);
		next.setId(R.id.buttonNext);
		finish.setId(R.id.buttonFinish);
		first = first1;
		edit = new ArrayList<EditText>();
		for (int i = 0; i < list.length; i++) {
		final EditText text = new EditText(this);
			text.setHint(list [i] [1]);
			text.setHintTextColor(Color.DKGRAY);
    		text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    		text.setInputType(Integer.parseInt(list[i] [2]));
    		edit.add(text);
    		layout.addView(text);
		}

		if(!but){
		layout.addView(next);
		}
		layout.addView(finish);

		finish.setOnClickListener(this);
		
		next.setOnClickListener(this);
		     
    }


	private void setBackground(Uri selectedImage) {
		Bitmap bitmap;
		try { // try to convert a image to a bitmap
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), selectedImage);
			int display_mode = getResources().getConfiguration().orientation;
			Matrix matrix = new Matrix();
			if (display_mode == 1) {
				matrix.setRotate(90);
			}

			Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			Log.e(this.getClass().toString(), "ROTATION:");
			imageView.setImageBitmap(adjustedBitmap);
		} catch (FileNotFoundException e) {
			Log.e(this.getClass().toString(), "ERROR, no file found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(this.getClass().toString(), "ERROR, file is no image");
			e.printStackTrace();
		}
	}
	

}