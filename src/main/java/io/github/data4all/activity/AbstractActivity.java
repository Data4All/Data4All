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
 * Global activity for all children activities.
 * 
 * This activity serves as a global class for all subclasses. Contains all the
 * methods that are relevant for all. For example the title bar.
 * 
 * @author Andre Koch
 * @author tbrose
 * @CreationDate 10.01.2015
 * @LastUpdate 27.02.2015
 * @version 1.3
 * 
 */

public abstract class AbstractActivity extends Activity {

    /**
     * The default requestcode for
     * {@link AbstractActivity#startActivityForResult(Intent)
     * startActivityForResult(Intent)}.
     * 
     * @see AbstractActivity#startActivityForResult(android.content.Intent)
     */
    public static final int WORKFLOW_CODE = 9999;

    public static final int RESULT_FINISH = 9998;

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
        case R.id.action_help:
            // TODO set help activity here
            status = true;
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return status;
    }

    /**
     * Same as calling startActivityForResult(Intent, int) with
     * {@link AbstractActivity#WORKFLOW_CODE WORKFLOW_CODE}.
     * 
     * @author tbrose
     * 
     * @param intent
     *            The intent to start.
     * @see android.app.Activity#startActivityForResult(android.content.Intent,
     *      int)
     */
    public void startActivityForResult(Intent intent) {
        super.startActivityForResult(intent, WORKFLOW_CODE);
    }

    /**
     * Call this when your activity is done and should be closed. The
     * ActivityResult which is propagated back is
     * {@link AbstractActivity#RESULT_FINISH RESULT_FINISH}.
     * 
     * @author tbrose
     * 
     * @see AbstractActivity#startActivityForResult(Intent)
     * @see AbstractActivity#WORKFLOW_CODE
     */
    public void finishWorkflow() {
        super.setResult(RESULT_FINISH);
        super.finish();
    }

    /**
     * @author tbrose
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), "onActivityResult(" + requestCode
                + ", " + resultCode + ", " + data + ");");
        if (requestCode == WORKFLOW_CODE && resultCode == RESULT_FINISH) {
            this.onWorkflowFinished(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Is called when the called activity finished the workflow with
     * {@link AbstractActivity#RESULT_FINISH RESULT_FINISH}.
     * 
     * @author tbrose
     * 
     * @param data
     *            An Intent, which can return result data to the caller (various
     *            data can be attached to Intent "extras").
     */
    protected abstract void onWorkflowFinished(Intent data);

    /**
     * Applies a bottom margin to the given view if the device have a
     * navigationbar. The margin to set is the height of the navigationbar.
     * 
     * @author tbrose
     * 
     * @param resources
     *            The resources to lookup the navigationbar height from
     * @param view
     *            The view to set the bottom margin from
     */
    public static void addNavBarMargin(Resources resources, View view) {
        if (resources != null) {
            final boolean hasBar = AbstractActivity.hasNavBar(resources);
            Log.v("HAS_NAVBAR", "" + hasBar);
            if (view != null && hasBar) {
                final LayoutParams lp = view.getLayoutParams();
                if (lp instanceof MarginLayoutParams) {
                    final MarginLayoutParams mlp = (MarginLayoutParams) lp;
                    mlp.setMargins(0, 0, 0,
                            AbstractActivity.getNavBarHeight(resources));
                }
            }
        }
    }

    /**
     * Returns if the device uses the navigationbar.
     * 
     * @author tbrose
     * 
     * @param resources
     *            The resources to lookup if navigationbar is shown
     * @return if the device uses the navigationbar
     */
    public static boolean hasNavBar(Resources resources) {
        final int id =
                resources.getIdentifier("config_showNavigationBar", "bool",
                        "android");
        if (id > 0) {
            return resources.getBoolean(id);
        } else {
            return false;
        }
    }

    /**
     * Returns the height of the navigationbar. This can be non-zero even if the
     * device does not use the navigationbar.
     * 
     * @author tbrose
     * 
     * @param resources
     *            The resources to lookup the navigationbar height from
     * @return The height of the navigationbar
     */
    public static int getNavBarHeight(Resources resources) {
        final int id =
                resources.getIdentifier("navigation_bar_height", "dimen",
                        "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }
}
