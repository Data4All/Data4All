package io.github.data4all;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class CustomMenuActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_help:
            showHelp();
            return true;
        case R.id.action_camera:
            showCamera();
            return true;
        case R.id.action_map:
            showMap();
            return true;
        case R.id.action_settings:
            showSettings();
        }

        return true;
    }

    private void showSettings() {
        // Show the about screen
        Toast.makeText(getApplicationContext(), "Settings", 
                Toast.LENGTH_SHORT).show();
    }

    private void showCamera() {
        // Show the camera screen
        Toast.makeText(getApplicationContext(), "Camera", 
                Toast.LENGTH_SHORT).show();
    }

    private void showHelp() {
        // Show the settings screen
        Toast.makeText(getApplicationContext(), "Help", 
                Toast.LENGTH_SHORT).show();
    }

    private void showMap() {
        // Show the map screen
        Toast.makeText(getApplicationContext(), "Map", 
                Toast.LENGTH_SHORT).show();
    }

}
