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
package io.github.data4all.util;

import io.github.data4all.logger.Log;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

/**
 * TODO
 * 
 * @author tbrose
 */
public class HelpOverlay {
    private static final String PREF_KEY_WAS_SHOWN_PREFIX =
            "pref_helpoverlay_wasshown_";

    private static final String TAG = HelpOverlay.class.getSimpleName();

    private static final int BACKGROUND_COLOR = Color.parseColor("#77557777");

    private final Activity activity;
    private final String className;
    private final int resourceId;
    private View helpView;
    private SharedPreferences prefs;
    private boolean isShown;

    /**
     * TODO
     */
    public HelpOverlay(Activity activity) {
        this.activity = activity;
        this.className = activity.getClass().getSimpleName().toLowerCase();
        this.resourceId =
                activity.getResources().getIdentifier("help_" + className,
                        "layout", activity.getPackageName());
        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    /**
     * Inflate the view, if it exists and add the OnClickListener.
     */
    private void setupView() {
        if (hasHelpOverlay() && helpView == null) {
            helpView =
                    activity.getLayoutInflater().inflate(this.resourceId, null);

            helpView.setBackgroundColor(BACKGROUND_COLOR);
            helpView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    HelpOverlay.this.hide();
                }
            });

            helpView.setAlpha(0);
            helpView.setVisibility(View.GONE);

            ViewGroup rootView =
                    (ViewGroup) activity.getWindow().getDecorView();

            rootView.addView(helpView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            helpView.bringToFront();
        }
    }

    /**
     * TODO
     */
    public boolean hasHelpOverlay() {
        boolean result = resourceId != 0;
        Log.d(TAG, "hasHelpOverlay() = " + result);
        return result;
    }

    /**
     * TODO
     */
    public boolean wasShown() {
        boolean result =
                prefs.getBoolean(PREF_KEY_WAS_SHOWN_PREFIX + className, false);
        Log.d(TAG, "wasShown() = " + result);
        return result;
    }

    /**
     * TODO
     */
    public void show() {
        if (!isShown()) {
            this.setupView();
            this.setShown(true);

            helpView.setVisibility(View.VISIBLE);
            helpView.animate().alpha(1).setDuration(500).start();

            prefs.edit()
                    .putBoolean(PREF_KEY_WAS_SHOWN_PREFIX + className, true)
                    .commit();
        }
    }

    /**
     * TODO
     */
    public void showOnFirstTime() {
        if (this.hasHelpOverlay() && !this.wasShown()) {
            this.show();
        }
    }

    /**
     * TODO
     */
    public void hide() {
        if (helpView != null && isShown()) {
            helpView.animate().alpha(0).setDuration(500)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            HelpOverlay.this.setShown(false);
                            helpView.setVisibility(View.GONE);
                        }
                    }).start();
        }
    }

    /**
     * TODO
     */
    private synchronized void setShown(boolean isShown) {
        Log.d(TAG, "setShown(" + isShown + ")");
        this.isShown = isShown;
    }

    /**
     * TODO
     */
    public synchronized boolean isShown() {
        return isShown;
    }
}
