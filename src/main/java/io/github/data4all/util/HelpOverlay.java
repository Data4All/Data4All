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
 * This class supports a full automatic help overlay for activities. The layout
 * name needs to be 'help_' followed by the lower case name of the activity. <br/>
 * 
 * If the file does not exists, the overlay cannot be shown. <br/>
 * 
 * If the layout-parent is a ViewGroup and all its children are also ViewGroups,
 * the layout is interpreted as a multipage-page layout and therefore only one
 * child is shown at the same time.<br/>
 * 
 * If the layout is a multipage-layout the next page is shown on touch,
 * otherwise the layout hides itself. After the last page of the
 * multipage-layout the layout hides itself on touch.
 * 
 * @author tbrose
 */
public class HelpOverlay {
    /**
     * The prefix for the preference for saving the shown-state.
     */
    private static final String PREF_KEY_WAS_SHOWN_PREFIX =
            "pref_helpoverlay_wasshown_";

    /**
     * The log tag of this class.
     */
    private static final String TAG = HelpOverlay.class.getSimpleName();

    /**
     * The background color of the overlay.
     */
    private static final int BACKGROUND_COLOR = Color.parseColor("#77557777");

    /**
     * The activity to load and show the overlay for.
     */
    private final Activity activity;

    /**
     * The lower case name of the activity.
     */
    private final String className;

    /**
     * The resource id of the overlay layout or {@code 0} if there is none.
     */
    private final int resourceId;

    /**
     * The View of the overlay layout.
     */
    private View helpView;

    /**
     * The preferences of the application for the shown-state.
     */
    private SharedPreferences prefs;

    /**
     * Whether or not the layout is currently shown.
     */
    private boolean isShown;

    /**
     * Whether or not the overlay is a multipage layout.
     */
    private boolean multiView;

    /**
     * The current visible child of the multipage layout. Unused, if the layout
     * is not a multipage layout.
     */
    private int currentChild;

    /**
     * The number of pages in the multipage layout. Unused, if the layout is not
     * a multipage layout.
     */
    private int childCount;

    /**
     * Constructs a new OverlayHelper for the given activity.
     * 
     * @param activity
     *            The activity to show the overlay for
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
     * Inflate the view, if it exists, add the OnClickListener and setup the
     * right page of a multipage layout.
     */
    private void setupView() {
        if (hasHelpOverlay() && helpView == null) {
            helpView =
                    activity.getLayoutInflater().inflate(this.resourceId, null);

            helpView.setBackgroundColor(BACKGROUND_COLOR);
            helpView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    HelpOverlay.this.next();
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

        if (isMultiViewOverlay()) {
            Log.d(TAG, "isMultiViewOverlay(true)");
            multiView = true;
            final ViewGroup group = (ViewGroup) helpView;
            for (int i = 0; i < childCount; i++) {
                final View child = group.getChildAt(i);
                child.setVisibility(View.GONE);
                child.setAlpha(0);
            }
            final View first = group.getChildAt(0);
            first.setVisibility(View.VISIBLE);
            first.setAlpha(1);
        } else {
            Log.d(TAG, "isMultiViewOverlay(false)");
        }
    }

    /**
     * TODO
     * 
     * @return
     */
    private boolean isMultiViewOverlay() {
        if (helpView instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) helpView;
            childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = group.getChildAt(i);
                if (!(child instanceof ViewGroup)) {
                    return false;
                }
            }
            return childCount > 1;
        }
        return false;
    }

    /**
     * TODO
     */
    private void next() {
        Log.d(TAG, "next(): currentChild=" + currentChild + " childCount="
                + childCount);
        if (multiView) {
            currentChild++;
            if (currentChild >= childCount) {
                this.hide();
            } else {
                Log.d(TAG, "next(): show next child " + currentChild);
                final View next =
                        ((ViewGroup) helpView).getChildAt(currentChild);
                final View last =
                        ((ViewGroup) helpView).getChildAt(currentChild - 1);

                // Hide last
                last.animate().alpha(0).setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                last.setVisibility(View.GONE);
                            }
                        }).start();

                // Show next
                next.setAlpha(0);
                next.setVisibility(View.VISIBLE);
                next.animate().alpha(1).setStartDelay(250).setDuration(250)
                        .start();
            }
        } else {
            this.hide();
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
        boolean result = prefs.getBoolean(getPrefKey(), false);
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

            prefs.edit().putBoolean(getPrefKey(), true).commit();
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
            currentChild = 0;
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
     * 
     * @return
     */
    private String getPrefKey() {
        return PREF_KEY_WAS_SHOWN_PREFIX + className;
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
    private synchronized boolean isShown() {
        return isShown;
    }
}
