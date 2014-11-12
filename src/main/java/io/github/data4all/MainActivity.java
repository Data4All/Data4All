package io.github.data4all;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

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
        
        Button c = (Button) findViewById(R.id.button4);
        c.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
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

