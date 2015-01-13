package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.SpeechRecognition;
import io.github.data4all.util.Tagging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

/**
 * 
 * @author Maurice Boyke
 *
 */
public class TagActivity extends Activity {

    private static final int REQUEST_CODE = 1234;
    final Context context = this;
    private ArrayList<String> keys;
    private String key;
    private Map<String, String> map;

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
        setContentView(R.layout.activity_tag);
        Button start = (Button) findViewById(R.id.speech);
        start.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        Button startTagging = (Button) findViewById(R.id.startTagging);
        startTagging.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                final Dialog dialog = new Dialog(TagActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E6808080")));
                dialog.setContentView(R.layout.dialog_matches);
                dialog.setTitle("Select Tag");
                final ListView keyList = (ListView) dialog
                        .findViewById(R.id.list);
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
                            
                            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {

                                String value = keys.get(position);
                                map = new LinkedHashMap<String, String>();
                                map.put(key, value);
                                if (key.equals("building")
                                        || key.equals("amenity")) {
                                  //  dialogDetails();                                    
                                    createDialog(Tags.getAddressTags(), "Add Address", key.equals("building"), true);
                                }
                                output();
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
        	new Dialog(TagActivity.this);
            ListView textList = (ListView) findViewById(R.id.listView1);
            List<String> matchesText = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SpeechRecognition.splitStrings(matchesText);
            Map<String, String> map = SpeechRecognition
                    .speechToTag(matchesText);
            matchesText.clear();
            Iterator<String> keySetIterator = map.keySet().iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
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
    public void dialogDetails() {

        final Dialog dialog = new Dialog(TagActivity.this);
        dialog.setContentView(R.layout.dialog_details);
        dialog.setTitle("Add address");
        final EditText street = (EditText) dialog.findViewById(R.id.editStreet);
        final EditText houseNumber = (EditText) dialog
                .findViewById(R.id.editHouseNumber);
        final EditText postcode = (EditText) dialog
                .findViewById(R.id.editPostcode);
        final EditText city = (EditText) dialog.findViewById(R.id.editCity);
        final EditText country = (EditText) dialog
                .findViewById(R.id.editCountry);
        Button next = (Button) dialog.findViewById(R.id.buttonNext);
        Button finish = (Button) dialog.findViewById(R.id.buttonFinish);
        if (key.equals("building")) {
            next.setEnabled(false);
        }

        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                List<String> addressTags = new ArrayList<String>();
                addressTags.add(street.getText().toString());
                addressTags.add(houseNumber.getText().toString());
                addressTags.add(postcode.getText().toString());
                addressTags.add(city.getText().toString());
                addressTags.add(country.getText().toString());
                map = Tagging.addressToTag(addressTags, map);
                dialogContacts();
                output();
                dialog.hide();
            }
        });

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                List<String> addressTags = new ArrayList<String>();
                addressTags.add(street.getText().toString());
                addressTags.add(houseNumber.getText().toString());
                addressTags.add(postcode.getText().toString());
                addressTags.add(city.getText().toString());
                addressTags.add(country.getText().toString());
                map = Tagging.addressToTag(addressTags, map);
                output();
                dialog.hide();
            }
        });
        dialog.show();

    }

    /**
     * to show the tags in the listview
     */

    private void output() {
        List<String> endList = new ArrayList<String>();
        Iterator<String> keySetIterator = map.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            endList.add(key + "=" + map.get(key));
            ListView textList = (ListView) findViewById(R.id.listView1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, endList);
            textList.setAdapter(adapter);

        }

    }

    /**
     * creates the Dialog with the Contacts
     */

    public void dialogContacts() {
    
        final Dialog dialog1 = new Dialog(TagActivity.this);
        dialog1.setContentView(R.layout.dialog_contacts);
        dialog1.setTitle("Add contacts");
     
        final EditText phone = (EditText) dialog1.findViewById(R.id.editPhone);
        final EditText fax = (EditText) dialog1.findViewById(R.id.editFax);
        final EditText website = (EditText) dialog1
                .findViewById(R.id.editWebsite);
        final EditText email = (EditText) dialog1.findViewById(R.id.editEmail);
        Button finish = (Button) dialog1.findViewById(R.id.buttonFinish1);
        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                List<String> addressTags = new ArrayList<String>();
                addressTags.add(phone.getText().toString());
                addressTags.add(fax.getText().toString());
                addressTags.add(website.getText().toString());
                addressTags.add(email.getText().toString());
                map = Tagging.contactToTag(addressTags, map);
                output();
                dialog1.hide();
            }
        });

        dialog1.show();
    }
    

	public void createDialog(String [] [] list, String title, final Boolean but, final Boolean first){
    	final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_dynamic);
		dialog.setTitle(title);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E6808080")));
		LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.dialogDynamic);
		final Button next = new Button(this);
		final Button finish = new Button(this);
		next.setText(R.string.next);
		finish.setText(R.string.finish);
		final List <EditText> edit = new ArrayList<EditText>();
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
		finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<String> tags = new ArrayList<String>();
				
				for (int i = 0; i < edit.size(); i++) {
					tags.add(edit.get(i).getText().toString());
				}
				if(first){
					map = Tagging.addressToTag(tags, map);
				}
				else{
					map = Tagging.contactToTag(tags, map);
				}
				output();
				dialog.hide();
			}
		});
		
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<String> tags = new ArrayList<String>();
				
				for (int i = 0; i < edit.size(); i++) {
					tags.add(edit.get(i).getText().toString());
				}
				map = Tagging.addressToTag(tags, map);
				createDialog(Tags.getContactTags(), "Add Contacts", true, false);
				dialog.hide();
			}
		});
    		dialog.show();       
    }
 

}