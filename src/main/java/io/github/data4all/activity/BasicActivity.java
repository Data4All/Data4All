package io.github.data4all.activity;

import io.github.data4all.R;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * this class serves as a global class for all activities. It includes methods
 * that are needed for more than one activities, such as the implementation of
 * the action bar.
 * 
 * This class inherits from activity
 * 
 * 
 * @author AndreKoch
 * 
 */
public abstract class BasicActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	        boolean status;
		switch (item.getItemId()) {
		case R.id.upload_data:
			startActivity(new Intent(this, LoginActivity.class));
			status = true;
			break;
		case R.id.action_settings:
			// TODO startActivity(new Intent(this, SettingsActivity.class));
		        status = true;
		        break;
		case R.id.action_camera:
			startActivity(new Intent(this, CameraActivity.class));
			status = true;
			break;
		case R.id.action_map:
			startActivity(new Intent(this, MapViewActivity.class));
			status = true;
			break;
		case R.id.action_help:
			// TODO set help activity here
		        status = true;
		        break;
		case R.id.action_login:
			startActivity(new Intent(this, LoginActivity.class));
			status = true;
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		return status;
	}
}
