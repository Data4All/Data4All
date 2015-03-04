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
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Offers a settings view for the application.
 * 
 * @author tbrose
 */
public class SettingsActivity extends AbstractActivity {
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Settings()).commit();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPrepareOptionsMenu(android.os.Bundle)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_settings);
        item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android
     * .content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        // finishWorkflow to get back to main activity
        finishWorkflow();
    }

    /**
     * Inflates the app settings in this fragment.
     * 
     * @author tbrose
     */
    public static class Settings extends PreferenceFragment {
        /*
         * (non-Javadoc)
         * 
         * @see
         * android.preference.PreferenceFragment#onCreate(android.os.Bundle)
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
