package io.github.data4all.activity;

import io.github.data4all.R;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * this class serves as a global class for all activities. It includes methods that are needed
 * for more than one activitie, such as the implementation of the action bar.
 * 
 * This class inherits from activity 
 * 
 * 
 * @author AndreKoch
 * 
 */
public class BasicActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_actionbar, menu);
		return super.onCreateOptionsMenu(menu);

	}
}
