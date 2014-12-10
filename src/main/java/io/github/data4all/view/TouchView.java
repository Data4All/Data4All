package io.github.data4all.view;

import io.github.data4all.logger.Log;
import io.github.data4all.model.drawing.DrawingMotion;
import io.github.data4all.model.drawing.MotionInterpreter;
import io.github.data4all.model.drawing.WayMotionInterpreter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author tbrose
 *
 */
public class TouchView extends View {

    private List<DrawingMotion> motions = new ArrayList<DrawingMotion>();
    private DrawingMotion currentMotion;
    private MotionInterpreter<Void> interpreter;

    public TouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context) {
        super(context);
    }

    public void setInterpreter(MotionInterpreter<Void> interpreter) {
        this.interpreter = interpreter;
    }

    public MotionInterpreter<Void> getInterpreter() {
        return interpreter;
    }

    /**
     * Remove all recorded DrawingMotions from this TouchView
     */
    public void clearMotions() {
        motions.clear();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        interpreter = new WayMotionInterpreter();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(0, 0, 0, 0);
        interpreter.draw(canvas, motions);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            currentMotion = new DrawingMotion();
            motions.add(currentMotion);
            currentMotion.addPoint(event.getX(), event.getY());
            Log.d(this.getClass().getSimpleName(),
                    "Motion start: " + currentMotion.getPathSize()
                            + ", point: " + currentMotion.isPoint());
            postInvalidate();
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            Log.d(this.getClass().getSimpleName(),
                    "Motion end: " + currentMotion.getPathSize() + ", point: "
                            + currentMotion.isPoint());
            currentMotion = null;
            postInvalidate();
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            currentMotion.addPoint(event.getX(), event.getY());
            Log.d(this.getClass().getSimpleName(),
                    "Motion move: " + currentMotion.getPathSize() + ", point: "
                            + currentMotion.isPoint());
            postInvalidate();
            return true;
        }
        return false;
    }
}
