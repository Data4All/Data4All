package io.github.data4all.widged;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class CustomImageButton extends ImageButton {


	public CustomImageButton(Context context) {
		super(context);
	}

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void drawableStateChanged() {
		Log.d("Button", "isPressed: " + isPressed());
		if (isPressed()) {
			startAnimation(AnimationUtils.loadAnimation(getContext(),
					R.drawable.imagebutton));

		}
		super.drawableStateChanged();

	}

}
