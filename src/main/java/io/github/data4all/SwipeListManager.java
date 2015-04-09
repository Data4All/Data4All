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
package io.github.data4all;

import java.util.List;

import android.app.Activity;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

/**
 * This class manage swipe events of the top bar of the camera and animates it.
 * 
 * @author tbrose
 *
 */
public class SwipeListManager {
    /**
     * The margin for the inner element.
     */
    private static final int MARGIN = 40;

    /**
     * The default animation duration.
     */
    private static final int DEFAULT_ANIMATION_DURATION = 500;

    /**
     * The view object of the middle image.
     */
    private final ImageView mMiddleImage;

    /**
     * The view object of the left image.
     */
    private final ImageView mLeftImage;

    /**
     * The view object of the right image.
     */
    private final ImageView mRightImage;

    /**
     * The list off all drawable-ids that should be selectable
     */
    private final List<Integer> idItems;

    /**
     * The index of the current image selected.
     */
    private int mCurrentIndex;

    /**
     * The animation duration for this manager.
     */
    private int animationDuration;

    /**
     * Whether or not swipes will be performed.
     */
    private boolean canSwipe;

    /**
     * Constructs a SwipeListManager for the given ressource-ids with the
     * DEFAULT_ANIMATION_DURATION.
     * 
     * @param activity
     *            The context of this manager
     * @param idItems
     *            The ressource-ids to use
     */
    public SwipeListManager(Activity activity, List<Integer> idItems) {
        this(activity, idItems, DEFAULT_ANIMATION_DURATION);
    }

    /**
     * Constructs a SwipeListManager for the given ressource-ids with the given
     * animation duration.
     * 
     * @param activity
     *            The context of this manager
     * @param idItems
     *            The ressource-ids to use
     * @param animationDuration
     *            The animation duration to use
     */
    public SwipeListManager(Activity activity, List<Integer> idItems,
            int animationDuration) {
        this.animationDuration = animationDuration;
        if (idItems == null) {
            throw Exceptions.nullArgument("textItems");
        }
        this.idItems = idItems;

        mMiddleImage = (ImageView) activity.findViewById(R.id.middleImage);
        mLeftImage = (ImageView) activity.findViewById(R.id.leftImage);
        mRightImage = (ImageView) activity.findViewById(R.id.rightImage);
        this.setContent(0);
    }

    /**
     * Perform a swipe from left to right. After the swipe, the current index is
     * decremented by one and the item that was left of the middle item is now
     * the middle item.
     * 
     * If the current index is 0, nothing is done.
     */
    public void swipeFromLeft() {
        if (canSwipe && mCurrentIndex > 0) {
            final int middleWidth = mMiddleImage.getWidth();
            final int leftWidth = mLeftImage.getWidth();
            final int shift = (middleWidth + leftWidth + MARGIN) / (1 + 1);
            this.performSwipe(shift);
        }
    }

    /**
     * Perform a swipe from right to left. After the swipe, the current index is
     * incremented by one and the item that was right of the middle item is now
     * the middle item.
     * 
     * If the current index is at the last item in the list, nothing is done.
     */
    public void swipeFromRight() {
        if (canSwipe && mCurrentIndex < idItems.size() - 1) {
            final int middleWidth = mMiddleImage.getWidth();
            final int rightWidth = mRightImage.getWidth();
            final int shift = (middleWidth + rightWidth + MARGIN) / (1 + 1);
            this.performSwipe(-shift);
        }
    }

    /**
     * Animates the movement of the items on the screen and resets the views
     * afterwards.
     * 
     * @param shift
     *            The movement direction and length
     */
    private void performSwipe(final int shift) {
        canSwipe = false;
        final float middleX = mMiddleImage.getX();
        final float leftX = mLeftImage.getX();
        final float rightX = mRightImage.getX();

        final ViewPropertyAnimator middleAnimator =
                mMiddleImage.animate().xBy(shift)
                        .setDuration(animationDuration);
        final ViewPropertyAnimator rightAnimator =
                mRightImage.animate().xBy(shift).setDuration(animationDuration);
        final ViewPropertyAnimator leftAnimator =
                mLeftImage.animate().xBy(shift).setDuration(animationDuration)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                middleAnimator.cancel();
                                rightAnimator.cancel();
                                mMiddleImage.setX(middleX);
                                mRightImage.setX(rightX);
                                mLeftImage.setX(leftX);
                                SwipeListManager.this.setContent(mCurrentIndex
                                        - (int) Math.signum(shift));
                                canSwipe = true;
                            }
                        });

        middleAnimator.start();
        rightAnimator.start();
        leftAnimator.start();
    }

    /**
     * Sets the content of the list that was shown on the screen to the given
     * index.
     * 
     * @param index
     *            The index of the item that should now be the middle item
     */
    public void setContent(int index) {
        if (index < 0 || index >= idItems.size()) {
            throw new IndexOutOfBoundsException("index is out of range [0..."
                    + (idItems.size() - 1) + "]");
        }

        mCurrentIndex = index;
        canSwipe = true;

        if (index - 1 >= 0) {
            mLeftImage.setImageResource(idItems.get(index - 1));
        } else {
            mLeftImage.setImageBitmap(null);
        }

        mMiddleImage.setImageResource(idItems.get(index));

        if (index + 1 < idItems.size()) {
            mRightImage.setImageResource(idItems.get(index + 1));
        } else {
            mRightImage.setImageBitmap(null);
        }
    }
}
