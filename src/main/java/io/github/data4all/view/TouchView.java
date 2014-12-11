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
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            handleMotion(event, "end");
            break;
        case MotionEvent.ACTION_UP:
            currentMotion = new DrawingMotion();
            motions.add(currentMotion);
            handleMotion(event, "start");
            break;
        case MotionEvent.ACTION_MOVE:
            handleMotion(event, "move");
            break;
        }
        return true;
    }

    /**
     * Handles the given motion:<br/>
     * Add the point to the current motion<br/>
     * Logs the motion<br/>
     * Causes the view to redraw itself afterwards
     * 
     * @param event
     *            The touch event
     * @param action
     *            the named action which is in progress
     */
    private void handleMotion(MotionEvent event, String action) {
        currentMotion.addPoint(event.getX(), event.getY());
        Log.d(this.getClass().getSimpleName(),
                "Motion " + action + ": " + currentMotion.getPathSize()
                        + ", point: " + currentMotion.isPoint());
        postInvalidate();
    }
}
