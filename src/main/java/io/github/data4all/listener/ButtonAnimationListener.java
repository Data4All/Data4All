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

import java.util.Iterator;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

/**
 * This OrientationEventListener rotates all given views to the current
 * orientation of the device.
 * 
 * @author vkochno
 */
public class ButtonAnimationListener{
	
	private boolean isAway;

    /**
     * The list of views to animate.
     */
    private List<View> viewsToAnimate;

    /**
     * Constructs a ButtonRotationListener with the given {@code context} which
     * rotates the views in {@code viewsToRotate}.
     * 
     * 
     * @param viewsToAnimate
     *            The views to animate
     */
    public ButtonAnimationListener(List<View> viewsToAnimate) {
        this.viewsToAnimate = viewsToAnimate;
    }

    public void onRotate() {
    	if(!isAway){
    	for(int i = 0; i < viewsToAnimate.size()-1;i++){
    	ObjectAnimator mover = ObjectAnimator.ofFloat(viewsToAnimate.get(i), "translationX", 0, 1500);
    	mover.start();
    	
    	}
    	ObjectAnimator ra = ObjectAnimator.ofFloat(viewsToAnimate.get(viewsToAnimate.size()-1), "rotation", 0f, 180.0f);
    	ra.start();
    	isAway = true;
    	}else{
    		for(int i = 0; i < viewsToAnimate.size()-1;i++){
    	    	ObjectAnimator mover = ObjectAnimator.ofFloat(viewsToAnimate.get(i), "translationX", 1500, 0);
    	    	mover.start();
    	}
    		ObjectAnimator ra = ObjectAnimator.ofFloat(viewsToAnimate.get(viewsToAnimate.size()-1), "rotation", 180.0f, 0.0f);
        	ra.start();
    		isAway = false;
    }
    	
    }
    
    public void onRotat() {
    	RotateAnimation ra = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	TranslateAnimation animation = new TranslateAnimation(0, 1500,0, 0);
    	AnimationSet set = new AnimationSet(true);
    	set.addAnimation(ra);
    	set.addAnimation(animation);
    	set.setFillAfter(true);
    	set.setDuration(1000);
    	for(View v:viewsToAnimate){
    		v.startAnimation(set);
    	}
    }
    
    public boolean isAway(){
    	return isAway;
    }
}
