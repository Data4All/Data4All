package io.github.data4all;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {	
    
	
	
	
	Button button_sensor;
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
		
		 button_sensor = (Button) findViewById(R.id.buttonSensor);
	     button_sensor.setOnClickListener(new OnClickListener() 
	        {            
	            @Override
	            public void onClick(View v) 
	            {
	         	        if(v == button_sensor){
	         	           startActivity(new Intent(MainActivity.this, LageSensor.class));
	         	                     
	            }
	        }; 
	    });
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
