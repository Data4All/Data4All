package io.github.data4all.activity;

import io.github.data4all.R;
import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                // Stay logged in?
                CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);

                // Already got token?
                if (!sharedPrefs.contains(OAuth.OAUTH_TOKEN)
                        && !sharedPrefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {

                    if (checkBox.isChecked()) {
                        startActivity(new Intent().setClass(v.getContext(),
                                PrepareRequestTokenActivity.class));
                    } else {
                        // TODO By closing the app, token should removed
                        startActivity(new Intent().setClass(v.getContext(),
                                PrepareRequestTokenActivity.class));
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.alreadyLoggedIn, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // for debugging
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                if (sharedPrefs.contains(OAuth.OAUTH_TOKEN)
                        && sharedPrefs.contains(OAuth.OAUTH_TOKEN_SECRET)) {

                    Editor ed = sharedPrefs.edit();
                    ed.remove(OAuth.OAUTH_TOKEN);
                    ed.remove(OAuth.OAUTH_TOKEN_SECRET);
                    ed.commit();
                }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
