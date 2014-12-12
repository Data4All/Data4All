package io.github.data4all.activity;

import io.github.data4all.R;
import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Activity to start authentication process
 * @author sb
 *
 */
public class LoginActivity extends Activity {

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
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                // By checking oauth tokens will be saved permanently
                // Otherwise they will be destroyed on by calling onDestroy()
               CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);

                // Already got token?
                if (!sharedPrefs.contains(OAuth.OAUTH_TOKEN)
                        && !sharedPrefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {

                    //Setting flag to remember tokens
                    setTemporaryFlag(!checkBox.isChecked());
                    
                    //Starting oAuth process
                    startActivity(new Intent().setClass(v.getContext(),
                            PrepareRequestTokenActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.alreadyLoggedIn, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // Delete tokens
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                deleteTokenFromSharedPreferences();
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
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
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
        Log.d(TAG, "SharedPreferences:" + sharedPrefs.getAll());
    }

    private boolean isTokenTemporary() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        return sharedPrefs.getBoolean("IS_TEMPORARY", false);
    }

    private void setTemporaryFlag(boolean b) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        Editor ed = sharedPrefs.edit();
        ed.putBoolean("IS_TEMPORARY", b);
        ed.commit();
        Log.i(TAG, "SharedPreferences:" + sharedPrefs.getAll());

    }

}
