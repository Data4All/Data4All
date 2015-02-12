package io.github.data4all.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class AutoFocusCrossHair extends View {

    //private Point mLocationPoint;

    public AutoFocusCrossHair(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setDrawable(int resid) {
        this.setBackgroundResource(resid);
    }

    public void showStart() {
        //setDrawable(R.drawable.focus_crosshair_image);
    }

    public void clear() {
        setBackgroundDrawable(null);
    }

}
