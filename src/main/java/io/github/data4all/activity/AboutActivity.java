package io.github.data4all.activity;

import io.github.data4all.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        getActionBar().hide();
        
        TextView homepage = (TextView) findViewById(R.id.linkToHomepage);
        homepage.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView facebook = (TextView) findViewById(R.id.linkToFacebook);
        facebook.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView twitter = (TextView) findViewById(R.id.linkToTwitter);
        twitter.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView github = (TextView) findViewById(R.id.linkToGithub);
        github.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
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

    @Override
    protected void onWorkflowFinished(Intent data) {
     // finishWorkflow to get back to main activity
        finishWorkflow(data);
        
    }
}
