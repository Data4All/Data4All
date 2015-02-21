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

    public ButtonRotationListener(Context context, List<View> viewsToRotate) {
        super(context);
        this.viewsToRotate = viewsToRotate;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        int newOrientation = ((orientation + 45) % 360) / 90;
        if (newOrientation != currentOrientation) {
            Log.d("BLUB", "New orientation: " + newOrientation);
            rotate(currentOrientation, newOrientation);
            currentOrientation = newOrientation;
        }
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
            rotate(view, from, to);
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
                                    .rotation((3 - ((to + 3) % 4)) * 90)
                                    .setInterpolator(
                                            new DecelerateInterpolator())
                                    .setDuration(400).start();
                        }
                    }).start();
        } else {
            view.animate().rotation((3 - ((to + 3) % 4)) * 90)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(500).start();
        }
    }

}
