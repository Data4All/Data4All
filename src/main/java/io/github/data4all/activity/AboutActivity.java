/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * This Class represents the AboutActivity.
 * @author Kristin Dahnken
 * 
 */
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
