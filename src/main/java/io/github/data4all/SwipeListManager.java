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

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * @author tbrose
 *
 */
public class SwipeListManager {
    private static final int DEFAULT_ANIMATION_DURATION = 500;

    private static final Integer green = 0xffaaffaa;
    private static final Integer gray = 0xffaaaaaa;

    private final TextView mMiddleText;
    private final TextView mLeftText;
    private final TextView mRightText;
    private final List<String> textItems;

    private int mCurrentIndex;

    private int animationDuration;

    private boolean canSwipe;

    public SwipeListManager(Activity activity, List<String> textItems) {
        this(activity, textItems, DEFAULT_ANIMATION_DURATION);
    }

    public SwipeListManager(Activity activity, List<String> textItems,
            int animationDuration) {
        this.animationDuration = animationDuration;
        if (textItems == null) {
            throw Exceptions.nullArgument("textItems");
        }
        this.textItems = textItems;
        mMiddleText = (TextView) activity.findViewById(R.id.middleText);
        mLeftText = (TextView) activity.findViewById(R.id.leftText);
        mRightText = (TextView) activity.findViewById(R.id.rightText);
        setContent(0);
    }

    public void swipeFromLeft() {
        if (canSwipe && mCurrentIndex > 0) {
            final int middleWidth = mMiddleText.getWidth();
            final int leftWidth = mLeftText.getWidth();
            final int shift = (middleWidth + leftWidth + 40) / 2;
            performSwipe(shift);
        }
    }

    public void swipeFromRight() {
        if (canSwipe && mCurrentIndex < textItems.size() - 1) {
            final int middleWidth = mMiddleText.getWidth();
            final int rightWidth = mRightText.getWidth();
            final int shift = (middleWidth + rightWidth + 40) / 2;
            performSwipe(-shift);
        }
    }

    private void performSwipe(final int shift) {
        canSwipe = false;
        final float middleX = mMiddleText.getX();
        final float leftX = mLeftText.getX();
        final float rightX = mRightText.getX();

        final ValueAnimator middleColorAnimation =
                ValueAnimator.ofObject(new ArgbEvaluator(), green, gray);
        middleColorAnimation.setDuration(animationDuration).addUpdateListener(
                new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        mMiddleText.setTextColor((Integer) animator
                                .getAnimatedValue());
                    }

                });
        middleColorAnimation.setInterpolator(new DecelerateInterpolator());

        final ValueAnimator otherColorAnimation =
                ValueAnimator.ofObject(new ArgbEvaluator(), gray, green);
        otherColorAnimation.setDuration(animationDuration).addUpdateListener(
                new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        if (shift > 0) {
                            mLeftText.setTextColor((Integer) animator
                                    .getAnimatedValue());
                        } else {
                            mRightText.setTextColor((Integer) animator
                                    .getAnimatedValue());
                        }
                    }

                });
        otherColorAnimation.setInterpolator(new AccelerateInterpolator());

        final ViewPropertyAnimator middleAnimator =
                mMiddleText.animate().xBy(shift).setDuration(animationDuration);
        final ViewPropertyAnimator rightAnimator =
                mRightText.animate().xBy(shift).setDuration(animationDuration);
        final ViewPropertyAnimator leftAnimator =
                mLeftText.animate().xBy(shift).setDuration(animationDuration)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                middleAnimator.cancel();
                                rightAnimator.cancel();
                                middleColorAnimation.cancel();
                                otherColorAnimation.cancel();
                                mMiddleText.setX(middleX);
                                mMiddleText.setTextColor(green);
                                mRightText.setX(rightX);
                                mRightText.setTextColor(gray);
                                mLeftText.setX(leftX);
                                mLeftText.setTextColor(gray);
                                setContent(mCurrentIndex
                                        - (int) Math.signum(shift));
                                canSwipe = true;
                            }
                        });

        middleAnimator.start();
        middleColorAnimation.start();
        rightAnimator.start();
        leftAnimator.start();
        otherColorAnimation.start();
    }

    public void setContent(int index) {
        if (index < 0 || index >= textItems.size()) {
            throw new IndexOutOfBoundsException("index is out of range [0..."
                    + (textItems.size() - 1) + "]");
        }

        mCurrentIndex = index;
        canSwipe = true;

        if (index - 1 >= 0) {
            mLeftText.setText(textItems.get(index - 1));
        } else {
            mLeftText.setText(null);
        }

        mMiddleText.setText(textItems.get(index));

        if (index + 1 < textItems.size()) {
            mRightText.setText(textItems.get(index + 1));
        } else {
            mRightText.setText(null);
        }
    }
}
