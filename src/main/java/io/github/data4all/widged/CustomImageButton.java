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
