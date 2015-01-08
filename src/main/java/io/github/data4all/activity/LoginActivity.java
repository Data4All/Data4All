package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG,
                "SharedPreferences:"
                        + PreferenceManager.getDefaultSharedPreferences(
                                getBaseContext()).getAll());

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        // for debugging
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
    }
    
    
    
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.loginButton:
    		SharedPreferences sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(getBaseContext());

    		// Stay logged in?
    		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);

    		// Already got token?
    		if (!sharedPrefs.contains(OAuth.OAUTH_TOKEN)
    				&& !sharedPrefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {

    			setTemporaryField(!checkBox.isChecked());
    			startActivity(new Intent().setClass(v.getContext(),
    					PrepareRequestTokenActivity.class));
    		} else {

    			Toast.makeText(getApplicationContext(),
    					R.string.alreadyLoggedIn, Toast.LENGTH_SHORT)
    					.show();

    		}
    	case R.id.deleteButton:
    		deleteTokenFromSharedPreferences();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Delete OAuthToken when onDestroy() is called
        if (isTokenTemporary()) {
            deleteTokenFromSharedPreferences();
        }

    }

    private void deleteTokenFromSharedPreferences() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        if (sharedPrefs.contains(OAuth.OAUTH_TOKEN)
                && sharedPrefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {

            Editor ed = sharedPrefs.edit();
            ed.remove(OAuth.OAUTH_TOKEN);
            ed.remove(OAuth.OAUTH_TOKEN_SECRET);
            ed.remove("IS_TEMPORARY");
            ed.commit();
        }
        Log.i(TAG, "SharedPreferences:" + sharedPrefs.getAll());
    }

    private boolean isTokenTemporary() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        return sharedPrefs.getBoolean("IS_TEMPORARY", false);
    }

    private void setTemporaryField(boolean b) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        Editor ed = sharedPrefs.edit();
        ed.putBoolean("IS_TEMPORARY", b);
        ed.commit();
        Log.i(TAG, "SharedPreferences:" + sharedPrefs.getAll());

    }

}
