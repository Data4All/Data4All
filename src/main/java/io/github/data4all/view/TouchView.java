package io.github.data4all.view;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.drawing.AreaMotionInterpreter;
import io.github.data4all.model.drawing.BuildingMotionInterpreter;
import io.github.data4all.model.drawing.DrawingMotion;
import io.github.data4all.model.drawing.MotionInterpreter;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.model.drawing.PointMotionInterpreter;
import io.github.data4all.model.drawing.WayMotionInterpreter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This TouchView listen to MotionEvents from the user, saves them into
 * DrawingMotions, uses MotionInterpreters to interpret the input and draws the
 * interpreted polygons.
 * 
 * @author tbrose
 *
 * @see MotionEvent
 * @see DrawingMotion
 * @see MotionInterpreter
 */
public class TouchView extends View {

    /**
     * The paint to draw the points with
     */
    private final Paint pointPaint = new Paint();

    /**
     * The paint to draw the path with
     */
    private final Paint pathPaint = new Paint();

    /**
     * The motion interpreted Polygon
     */
    private List<Point> polygon = new ArrayList<Point>();

    /**
     * The Polygon with the current pending motion
     */
    private List<Point> newPolygon = new ArrayList<Point>();

    /**
     * The current motion the user is typing via the screen
     */
    private DrawingMotion currentMotion;

    /**
     * The currently used interpreter
     */
    private MotionInterpreter interpreter;

    public TouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context) {
        super(context);
    }

    /**
     * Remove all recorded DrawingMotions from this TouchView
     */
    public void clearMotions() {
        if (polygon != null) {
            polygon.clear();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        interpreter = new WayMotionInterpreter();
        pointPaint.setColor(MotionInterpreter.POINT_COLOR);
        pathPaint.setColor(MotionInterpreter.PATH_COLOR);
        pathPaint.setStrokeWidth(MotionInterpreter.PATH_STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(0, 0, 0, 0);

        if (newPolygon != null) {
            // first draw all lines
            int limit = newPolygon.size();
            // Don't draw the last line if it is not an area
            limit -= interpreter.isArea() ? 0 : 1;
            for (int i = 0; i < limit; i++) {
                // The next point in the polygon
                Point b = newPolygon.get((i + 1) % newPolygon.size());
                Point a = newPolygon.get(i);

                canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(),
                        pathPaint);
            }

            // afterwards draw the points
            for (Point p : newPolygon) {
                canvas.drawCircle(p.getX(), p.getY(),
                        MotionInterpreter.POINT_RADIUS, pointPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            currentMotion = new DrawingMotion();
            handleMotion(event, "start");
            break;
        case MotionEvent.ACTION_UP:
            handleMotion(event, "end");
            polygon = newPolygon;
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
        if (currentMotion != null) {
            currentMotion.addPoint(event.getX(), event.getY());
            newPolygon = interpreter.interprete(polygon, currentMotion);
            Log.d(this.getClass().getSimpleName(),
                    "Motion " + action + ": " + currentMotion.getPathSize()
                            + ", point: " + currentMotion.isPoint());
            postInvalidate();
        }
    }

    /**
     * deletes a Point of the polygon
     * 
     * @param point
     *            The selected point
     */
    public void deletePoint(Point point) {
        polygon.remove(point);
        newPolygon = interpreter.interprete(polygon, currentMotion);
        Log.d(this.getClass().getSimpleName(), "Point deleted");
        postInvalidate();

    }

    /**
     * gets the position of the point<br/>
     * 
     * use moveTo() afterwards to actually move the point
     * 
     * @param point
     *            the point you want to move
     * 
     * @return PointMover 
     *            the position in the polygon
     * 
     * @author konerman
     */
    public PointMover movePoint(Point point) {
        for (int i = 0; i < polygon.size() - 1; i++) {
            if (polygon.get(i).equals(point)) {
                Log.d(this.getClass().getSimpleName(), "new PointMover, index:"
                        + i);
                return new PointMover(i);
            }
        }
        Log.d(this.getClass().getSimpleName(), "");
        return null;
    }

    /**
     * Pointer of the position of a point in the polygon
     * 
     * @author konerman
     *
     */
    public class PointMover {
        public final int idx;

        public PointMover(int idx) {
            this.idx = idx;
        }

        /**
         * moves a Point to the new coordinates
         * 
         * @param x/y
         *            the new coordinates
         *            
         * @author konerman           
         */
        public void moveTo(float x, float y) {
            polygon.set(idx, new Point(x, y));
        }

    }

    public void setInterpretationType(InterpretationType type) {
        switch (type) {
        case AREA:
            interpreter = new AreaMotionInterpreter();
            break;
        case POINT:
            interpreter = new PointMotionInterpreter();
            break;
        case BUILDING:
            interpreter = new BuildingMotionInterpreter();
            break;
        case WAY:
            interpreter = new WayMotionInterpreter();
            break;
        default:
            throw new IllegalArgumentException("'type' cannot be null");
        }
    }

    public static enum InterpretationType {
        AREA, POINT, BUILDING, WAY;
    }
}
