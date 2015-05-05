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
package io.github.data4all.widged;

import io.github.data4all.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

/**
 * This method replaces the standard image button with a custom ImageButton .
 * This includes an animation for onclick effect.
 * 
 * @author Andre Koch
 * @CreationDate 05.03.2015
 * @LastUpdate 05.03.2015
 * @version 1.0
 * 
 */
public class CustomImageButton extends ImageButton {

    /**
     * Default Constructor
     * 
     * @param context
     */
    public CustomImageButton(Context context) {
        super(context);
    }

    /**
     * Default Constructor
     * 
     * @param context
     * @param attrs
     * 
     */
    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Default Constructor
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void drawableStateChanged() {
        if (isPressed()) {
            startAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.imagebutton));
        }
        super.drawableStateChanged();

    }

}
