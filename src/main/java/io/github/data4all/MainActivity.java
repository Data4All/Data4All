package io.github.data4all;

import java.util.ArrayList;

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
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final int REQUEST_CODE = 1234;
	Button speech;
	ArrayList<String> matches_text;
	Dialog match_text_dialog;
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        Button b = (Button) findViewById(R.id.buttonCamera);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MakePhotoActivity.class));
            }
        });
        
        speech = (Button) findViewById(R.id.button6);
        speech.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {           
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                
            }
        });
     
    }


	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
     // dialog for search result
     match_text_dialog = new Dialog(MainActivity.this);
     match_text_dialog.setContentView(R.layout.dialog_matches);
     match_text_dialog.setTitle("Select Matching Tag");
     ListView textlist = (ListView)match_text_dialog.findViewById(R.id.list);
     matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
     ArrayAdapter<String> adapter =    new ArrayAdapter<String>(this,
           android.R.layout.simple_list_item_1, matches_text);
     textlist.setAdapter(adapter);
     textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     // show the result 
     @Override
     public void onItemClick(AdapterView<?> parent, View view,
                             int position, long id) {
       speech.setText("You have said " +matches_text.get(position));
       match_text_dialog.hide();
     }
     });
     match_text_dialog.show();
     }
     super.onActivityResult(requestCode, resultCode, data);
    }

    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(io.github.data4all.R.menu.main, menu);
	return true;
    }
    
    /** Called when user clicks the help button. */
    public void showHelpToast(View view){
    	Toast toast;
    	toast = Toast.makeText(getApplicationContext(), "Default help text.", Toast.LENGTH_SHORT);
    	
    	if(toast != null){
    		toast.show();
    	}
    }

}

