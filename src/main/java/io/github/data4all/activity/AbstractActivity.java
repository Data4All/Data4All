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
import io.github.data4all.model.data.Track;
import io.github.data4all.util.HelpOverlay;
import io.github.data4all.util.TrackUtil;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CheckBox;

/**
 * Global activity for all children activities.
 * 
 * This activity serves as a global class for all subclasses. Contains all the
 * methods that are relevant for all. For example the title bar.
 * 
 * @author Andre Koch
 * @author tbrose
 * @author sbrede
 * @CreationDate 10.01.2015
 * @LastUpdate 03.04.2015
 * @version 1.4
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

    private HelpOverlay overlay;

    private static final int NOTIFICATION_EX = 1;

    private NotificationManager notificationManager;

    // Counter is used to count the start of activities to remove the status bar
    // icon when no active is running
    private static int counter;

    private static final String SHARED_PREFS = "shared_prefs";

    /**
     * Handle the changing of record/stop icon
     */
    boolean isChecked = false;

    /**
     * Indicates current recording
     */
    boolean recordActive = false;

    /**
     * Checkobox for RecordDialog
     */
    CheckBox dontShowAgain;

    private TrackUtil trackUtil;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initHelp();

        // Count up on each Activity which is create
        counter++;

        // set a notification to Status Bar
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     
        Intent notificationIntent = new Intent(this, MapViewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        final Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_white)
                .setOngoing(true)
                .setContentTitle(getString(R.string.statusNotificationHeadline))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText(getString(R.string.statusNotification));

        notificationManager.notify(NOTIFICATION_EX, mBuilder.build());

        trackUtil = new TrackUtil(getApplicationContext());
    }

    /**
     * Initiate the help-layout for the activity.
     */
    private void initHelp() {
        overlay = new HelpOverlay(this);
        overlay.showOnFirstTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // count down on each activity which is destroyed
        counter--;
        // when counter is 0, we have to remove the notification
        if (counter <= 0) {
            notificationManager.cancel(NOTIFICATION_EX);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);

        // Remove the menu item if there is no overlay for this activity
        if (!overlay.hasHelpOverlay()) {
            menu.removeItem(R.id.action_help);
        }

        final ActionBar bar = getActionBar();
        if (bar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        case R.id.action_about:
            startActivity(new Intent(this, AboutActivity.class));
            status = true;
            break;
        case R.id.action_licenses:
            startActivity(new Intent(this, LicensesActivity.class));
            status = true;
            break;
        case R.id.action_help:
            overlay.show();
            status = true;
            break;
        case R.id.list_tracks:
            startActivity(new Intent(this, GpsTrackListActivity.class));
            status = true;
            break;
        // finish workflow, return to mapview
        case android.R.id.home:
            this.onHomePressed();
            status = true;
            break;
        case R.id.toggleButton:
            handleDialog(item);
            status = true;
            break;
        default:
            Log.d("AbstractActivity", "default click");
            return super.onOptionsItemSelected(item);
        }
        return status;
    }

    protected void onHomePressed() {
        onWorkflowFinished(null);
    }

    /**
     * Fetches from the SharedPreferences if the dialogs should be shown. When
     * dialogs should be shown, it starts the {@link #recordDialog(MenuItem)} or
     * {@link #stopDialog(MenuItem)} method. When there is no need for dialogs,
     * it directly will start or stop a {@link Track}.
     * 
     * @author sbrede
     * 
     * @param item
     *            The button to toggle the icon
     */
    private void handleDialog(MenuItem item) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        boolean skipDialog = settings.getBoolean("skipRecordDialog", false);
        if (!skipDialog) {
            if (!recordActive) {
                recordDialog(item);
            } else {
                stopDialog(item);
            }
        } else {
            if (!recordActive) {
                Log.d("AbstractActivity", "start a track without dialog");
                isChecked = true;
                changeIconOfRecordButton(item, isChecked);
                recordActive = true;
                startTrack();
            } else {
                Log.d("AbstractActivity", "stop a track without dialog");
                isChecked = false;
                changeIconOfRecordButton(item, isChecked);
                recordActive = false;
                stopTrack();
            }
        }
    }

    /**
     * Switch the icons of the record/stop button.
     * 
     * @author sbrede
     * @param item
     *            The button in the menu
     * @param status
     *            Indicates which icon should be switched
     */
    private void changeIconOfRecordButton(MenuItem item, boolean status) {
        if (status) {
            item.setIcon(R.drawable.ic_stop);
        } else {
            item.setIcon(R.drawable.ic_record);
        }
    }

    /**
     * Set up a dialog for recording a track. Contains a checkbox linked with
     * the shared preferences, so you can suppress this dialog.
     * 
     * On positive click it starts recording a track. On negative click it
     * closes the dialog and nothing happens.
     * 
     * @author sbrede
     */
    private void recordDialog(final MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View mLayout = adbInflater.inflate(R.layout.checkbox, null);
        dontShowAgain = (CheckBox) mLayout.findViewById(R.id.skip);
        adb.setView(mLayout);
        adb.setTitle(R.string.recordTrack);
        adb.setMessage(R.string.startRecordTrack);
        adb.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getSharedPreferences(
                                SHARED_PREFS, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("skipRecordDialog",
                                dontShowAgain.isChecked());
                        // Commit the edits!
                        editor.commit();
                        isChecked = true;
                        changeIconOfRecordButton(item, isChecked);
                        recordActive = true;
                        startTrack();

                        Log.d("AbstractActivity", "start a track with dialog");
                        return;
                    }
                });

        adb.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getSharedPreferences(
                                SHARED_PREFS, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("skipRecordDialog",
                                dontShowAgain.isChecked());
                        // Commit the edits!
                        editor.commit();
                        isChecked = false;
                        changeIconOfRecordButton(item, isChecked);
                        recordActive = false;
                        Log.d("AbstractActivity", "deny start in dialog");
                        return;
                    }
                });
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        boolean skipDialog = settings.getBoolean("skipRecordDialog", false);
        if (!skipDialog) {
            adb.show();
        }

    }

    /**
     * Set up a dialog to stop and save a track. Contains a checkbox linked with
     * the shared preferences, so you can suppress this dialog.
     * 
     * On positive click it stops and saves a track. On negative click it closes
     * the dialog and nothing happens (A running track will not be saved and
     * closed).
     * 
     * @author sbrede
     */
    private void stopDialog(final MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View mLayout = adbInflater.inflate(R.layout.checkbox, null);
        dontShowAgain = (CheckBox) mLayout.findViewById(R.id.skip);
        adb.setView(mLayout);
        adb.setTitle(R.string.stopTrack);
        adb.setMessage(R.string.stopRecordTrack);
        adb.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getSharedPreferences(
                                SHARED_PREFS, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("skipStopDialog",
                                dontShowAgain.isChecked());
                        // Commit the edits!
                        editor.commit();
                        isChecked = false;
                        changeIconOfRecordButton(item, isChecked);
                        recordActive = false;

                        stopTrack();
                        Log.d("AbstractActivity", "stop a track with dialog");
                        return;
                    }
                });

        adb.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getSharedPreferences(
                                SHARED_PREFS, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("skipStopDialog",
                                dontShowAgain.isChecked());
                        // Commit the edits!
                        editor.commit();
                        isChecked = true;
                        recordActive = true;
                        changeIconOfRecordButton(item, isChecked);
                        Log.d("AbstractActivity", "deny stop in dialog");
                        return;
                    }
                });
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        boolean skipDialog = settings.getBoolean("skipStopDialog", false);
        if (!skipDialog) {
            adb.show();
        }

    }

    private void startTrack() {
        trackUtil.startNewTrack();
    }

    private void stopTrack() {
        Track track = trackUtil.getLastTrack();
        if (track != null) {
            trackUtil.saveTrack(track);
        }
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
     * @param data
     *            The data to propagate back to the originating activity
     * 
     * @see AbstractActivity#startActivityForResult(Intent)
     * @see AbstractActivity#WORKFLOW_CODE
     */
    public void finishWorkflow(Intent data) {
        super.setResult(RESULT_FINISH, data);
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
        final int id = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
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
        final int id = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

}
