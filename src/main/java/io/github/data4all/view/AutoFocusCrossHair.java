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
package io.github.data4all.view;

import io.github.data4all.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Auto is a helper class for displaying the autofocus status.
 * 
 * This class draws a rectangle into this activity and animate this based on the
 * status of the autofocus.
 * 
 * @author Andre Koch
 * @author tbrose (Marked additions)
 * @CreationDate 12.02.2015
 * @LastUpdate 21.02.2015
 * @version 1.1
 * 
 */

public class AutoFocusCrossHair extends View {

    /**
     * Public Constructor for creating the AutoFocus Cross Hair
     * 
     * @param context
     * @param attrs
     */
    public AutoFocusCrossHair(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setDrawable(int resid) {
        this.setBackgroundResource(resid);
    }

    /**
     * method to set the image on the view
     */
    public void showStart() {
        this.setDrawable(R.drawable.crosshair);
        this.setVisibility(View.VISIBLE);
    }

    public void doAnimation() {
        scaleView(this, 1.0f, 1.5f);
    }

    private void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(1.0f, 1.5f, // Start and end values
                                                        // for the X axis
                                                        // scaling
                startScale, endScale, // Start and end values for the Y axis
                                      // scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(500);
        v.startAnimation(anim);
    }

    /**
     * Animate the success of autofocus.
     * 
     * @author tbrose
     */
    public void success() {
        this.setDrawable(R.drawable.crosshair_success);
        this.setScaleX(1.5f);
        this.setScaleY(1.5f);
        this.animate().alpha(0f).setStartDelay(400).setDuration(100)
        .withEndAction(new Runnable() {
            @Override
            public void run() {
                AutoFocusCrossHair.this.clear();
            }
        }).start();
    }

    /**
     * Animate the fail of autofocus.
     * 
     * @author tbrose
     */
    public void fail() {
        this.setDrawable(R.drawable.crosshair_fail);
        this.setScaleX(1.5f);
        this.setScaleY(1.5f);
        this.animate().alpha(0f).setStartDelay(250).setDuration(250)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        AutoFocusCrossHair.this.clear();
                    }
                }).start();
    }

    /**
     * method to remove Image from view
     */
    private void clear() {
        this.setBackgroundResource(0);
        this.setScaleX(1f);
        this.setScaleY(1f);
        this.setAlpha(1f);
        this.setVisibility(View.GONE);
    }

}
