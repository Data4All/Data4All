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
import io.github.data4all.logger.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

/**
 * global activity for all children activities.
 * 
 * This activity serves as a global class for all subclasses . Contains all the
 * methods that are relevant for all. For example the title bar.
 * 
 * @author Andre Koch
 * @CreationDate 10.01.2015
 * @LastUpdate 12.02.2015
 * @version 1.2
 * 
 */

public abstract class AbstractActivity extends Activity {

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.Menu)
     */
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
            startActivity(new Intent(this, SettingsActivity.class));
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

    /**
     * Applies a bottom margin to the given view if the device have a
     * navigationbar. The margin to set is the height of the navigationbar.
     * 
     * @param resources
     *            The resources to lookup the navigationbar height from
     * @param view
     *            The view to set the bottom margin from
     * 
     * @author tbrose
     */
    public static void addNavBarMargin(Resources resources, View view) {
        if (resources != null) {
            boolean hasBar = AbstractActivity.hasNavBar(resources);
            Log.v("HAS_NAVBAR", "" + hasBar);
            if (view != null && hasBar) {
                LayoutParams lp = view.getLayoutParams();
                if (lp instanceof MarginLayoutParams) {
                    MarginLayoutParams mlp = (MarginLayoutParams) lp;
                    mlp.setMargins(0, 0, 0,
                            AbstractActivity.getNavBarHeight(resources));
                }
            }
        }
    }

    /**
     * Returns if the device uses the navigationbar.
     * 
     * @param resources
     *            The resources to lookup if navigationbar is shown
     * @return if the device uses the navigationbar
     * 
     * @author tbrose
     */
    public static boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool",
                "android");
        if (id > 0)
            return resources.getBoolean(id);
        else
            return false;
    }

    /**
     * Returns the height of the navigationbar. This can be non-zero even if the
     * device does not use the navigationbar.
     * 
     * @param resources
     *            The resources to lookup the navigationbar height from
     * @return The height of the navigationbar
     * 
     * @author tbrose
     */
    public static int getNavBarHeight(Resources resources) {
        int id = resources.getIdentifier("navigation_bar_height", "dimen",
                "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }
}
