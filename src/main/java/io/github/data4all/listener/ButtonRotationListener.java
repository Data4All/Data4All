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
package io.github.data4all.listener;

import io.github.data4all.logger.Log;

import java.util.List;

import android.content.Context;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * This OrientationEventListener rotates all given views to the current
 * orientation of the device.
 * 
 * @author tbrose
 */
public class ButtonRotationListener extends OrientationEventListener {

    /**
     * Indicates if a rotation to 360° is in progress.
     */
    private boolean backRotating;

    /**
     * The list of views to rotate.
     */
    private List<View> viewsToRotate;

    /**
     * The current orientation.
     */
    private int currentOrientation;

    /**
     * Constructs a ButtonRotationListener with the given {@code context} which
     * rotates the views in {@code viewsToRotate}.
     * 
     * @param context
     *            The context for the OrientationEventListener
     * 
     * @param viewsToRotate
     *            The views to rotate
     */
    public ButtonRotationListener(Context context, List<View> viewsToRotate) {
        super(context);
        this.viewsToRotate = viewsToRotate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.OrientationEventListener#onOrientationChanged(int)
     */
    @Override
    public void onOrientationChanged(int orientation) {
        final int newOrientation = ((orientation + 45) % 360) / 90;
        if (newOrientation != currentOrientation) {
            Log.d("ButtonRotationListener", "New orientation: " + newOrientation);
            this.rotate(currentOrientation, newOrientation);
            currentOrientation = newOrientation;
        }
    }

    /**
     * @return The current orientation
     */
    public int getCurrentOrientation() {
        return currentOrientation;
    }

    /**
     * @return The current rotation in degrees
     */
    public int getCurrentRotation() {
        return this.toDegree(currentOrientation);
    }

    private int toDegree(int orientation) {
        return (3 - ((orientation + 3) % 4)) * 90;
    }

    /**
     * Rotates all views in {@link viewsToRotate} to the given orientation.
     *
     * @param from
     *            The old device rotation and current button rotation
     * @param to
     *            The new device rotation and
     */
    private void rotate(final int from, final int to) {
        for (View view : viewsToRotate) {
            this.rotate(view, from, to);
        }
    }

    /**
     * Rotates the given view to the given orientation.
     *
     * @param view
     *            The view to rotate
     * @param from
     *            The old device rotation and current button rotation
     * @param to
     *            The new device rotation and
     */
    private void rotate(final View view, final int from, final int to) {
        if (from == 0 && to == 1) {
            view.setRotation(359.9f);
            view.animate().rotation(270f)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(500).start();
        } else if (from == 1 && to == 0) {
            backRotating = true;
            view.animate().rotation(359.9f)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            view.setRotation(0f);
                            backRotating = false;
                        }
                    }).start();
        } else if (backRotating && to == 3) {
            backRotating = true;
            view.animate().rotation(359.9f).setDuration(100)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            view.setRotation(0f);
                            backRotating = false;
                            view.animate()
                                    .rotation(toDegree(to))
                                    .setInterpolator(
                                            new DecelerateInterpolator())
                                    .setDuration(400).start();
                        }
                    }).start();
        } else {
            view.animate().rotation(this.toDegree(to))
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(500).start();
        }
    }
}
