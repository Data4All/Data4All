package io.github.data4all.view;

import io.github.data4all.activity.ShowPictureActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.drawing.AreaMotionInterpreter;
import io.github.data4all.model.drawing.BuildingMotionInterpreter;
import io.github.data4all.model.drawing.DrawingMotion;
import io.github.data4all.model.drawing.MotionInterpreter;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.model.drawing.PointMotionInterpreter;
import io.github.data4all.model.drawing.RedoUndo;
import io.github.data4all.model.drawing.RedoUndo.UndoRedoListener;
import io.github.data4all.model.drawing.WayMotionInterpreter;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
     * The paint to draw the path with
     */
    private final Paint pathPaint = new Paint();
    private final Paint areaPaint = new Paint();

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
     * An object for the calculation of the point transformation
     */
    private PointToCoordsTransformUtil pointTrans;

    /**
     * The currently used interpreter
     */
    private MotionInterpreter interpreter;

    /**
     * The current used RedoUndo object
     */
    private RedoUndo redoUndo;
    private UndoRedoListener undoRedoListener;

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
        interpreter = new WayMotionInterpreter(pointTrans);
        pathPaint.setColor(MotionInterpreter.POINT_COLOR);
        pathPaint.setColor(MotionInterpreter.PATH_COLOR);
        pathPaint.setStrokeWidth(MotionInterpreter.PATH_STROKE_WIDTH);
        areaPaint.setColor(MotionInterpreter.AREA_COLOR);
        areaPaint.setStyle(Paint.Style.FILL);
        areaPaint.setAlpha(100);
        redoUndo = new RedoUndo();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(0, 0, 0, 0);

        Path path = new Path();
        path.reset();

        if (newPolygon != null && newPolygon.size() != 0) {
            path.moveTo(newPolygon.get(0).getX(), newPolygon.get(0).getY());
            // first draw all lines
            int limit = newPolygon.size();
            // Don't draw the last line if it is not an area
            limit -= interpreter.isArea() ? 0 : 1;
            for (int i = 0; i < limit; i++) {
                // The next point in the polygon
                Point b = newPolygon.get((i + 1) % newPolygon.size());
                Point a = newPolygon.get(i);
                path.lineTo(a.getX(), a.getY());
                path.lineTo(b.getX(), b.getY());
                canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(),
                        pathPaint);
            }
            if (interpreter instanceof AreaMotionInterpreter
                    || interpreter instanceof BuildingMotionInterpreter) {
                canvas.drawPath(path, areaPaint);
            }
            // afterwards draw the points
            for (Point p : newPolygon) {
                canvas.drawCircle(p.getX(), p.getY(),
                        MotionInterpreter.POINT_RADIUS, pathPaint);
            }
            undoUseable();
            redoUseable();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            currentMotion = new DrawingMotion();
            handleMotion(event, "end");
            break;
        case MotionEvent.ACTION_UP:
            handleMotion(event, "start");
            polygon = newPolygon;
            redoUndo = new RedoUndo(polygon);
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
     * Deletes a Point of the polygon
     * 
     * @param point
     *            The point to delete
     * 
     * @author konerman
     */
    public void deletePoint(Point point) {
        if (polygon.remove(point)) {
            Log.d(this.getClass().getSimpleName(), "Point deleted");
        } else {
            Log.d(this.getClass().getSimpleName(), "Point not found");
        }
        postInvalidate();
    }

    /**
     * This function determines if there is a Point of the polygon close to the
     * given coordinates
     * 
     * @param x
     *            the X-value of the point
     * @param y
     *            the Y-value of the point
     * @param maxDistance
     *            the max distance between the x/y and the Point
     * @return the closest point to the given x/y or {@code null} if the nearest
     *         point is more than maxDistance away
     * 
     * @author konerman
     */
    public Point lookUp(float x, float y, float maxDistance) {
        double shortest = maxDistance;
        Point closePoint = null;

        if (!polygon.isEmpty()) {
            // runs through the list of points in the polygon and checks which
            // point is the closest
            for (Point p : polygon) {
                double distance = Math.hypot(x - p.getX(), y - p.getY());

                Log.d(this.getClass().getSimpleName(), "distance:" + distance);
                if (distance <= shortest) {
                    shortest = distance;
                    closePoint = p;
                }
            }
        }
        Log.d(this.getClass().getSimpleName(), "shortest distance:" + shortest);
        return closePoint;
    }

    /**
     * Returns a {@link PointMover} for the given {@link Point}<br/>
     * 
     * Use moveTo() afterwards to actually move the point
     * 
     * @param point
     *            the point you want to move
     * 
     * @return A {@link PointMover} for moving this point
     * 
     * @author konerman
     */
    public PointMover movePoint(Point point) {
        int i = polygon.indexOf(point);
        if (i == -1) {
            Log.d(this.getClass().getSimpleName(), "Point is not in polygon");
            return null;
        } else {
            Log.d(this.getClass().getSimpleName(), "PointMover for index " + i);
            return new PointMover(i);
        }
    }

    /**
     * Pointer of the position of a point in the polygon
     * 
     * @author konerman
     */
    public class PointMover {
        private final int idx;

        public PointMover(int idx) {
            this.idx = idx;
        }

        /**
         * moves the {@link Point} to the new coordinates and invalidates its
         * {@link TouchView} afterwards
         * 
         * @param x
         *            the new x-coordinate
         * @param y
         *            the new y-coordinate
         * 
         * @author konerman
         */
        public void moveTo(float x, float y) {
            polygon.set(idx, new Point(x, y));
            postInvalidate();
        }
    }

    public void setInterpretationType(InterpretationType type) {
        switch (type) {
        case AREA:
            interpreter = new AreaMotionInterpreter(pointTrans);
            break;
        case POINT:
            interpreter = new PointMotionInterpreter(pointTrans);
            break;
        case BUILDING:
            interpreter = new BuildingMotionInterpreter(pointTrans);
            break;
        case WAY:
            interpreter = new WayMotionInterpreter(pointTrans);
            break;
        default:
            throw new IllegalArgumentException("'type' cannot be null");
        }
    }

    public static enum InterpretationType {
        AREA, POINT, BUILDING, WAY;
    }

    public void redo() {
        newPolygon = redoUndo.redo();
        polygon = newPolygon;
        redoUseable();
    }

    public void undo() {
        newPolygon = redoUndo.undo();
        polygon = newPolygon;
        if (undoRedoListener != null) {
            undoRedoListener.canRedo(true);
        }
        undoUseable();
    }

    public boolean redoUseable() {
        if (redoUndo.getCurrent() == redoUndo.getMax()) {
            Log.d(this.getClass().getSimpleName(), "false redo");
            if (undoRedoListener != null) {
                undoRedoListener.canRedo(false);
            }
            return true;
        } else {
            Log.d(this.getClass().getSimpleName(), "false redo");
            if (undoRedoListener != null) {
                undoRedoListener.canRedo(true);
            }
            return false;
        }
    }

    public boolean undoUseable() {
        if (redoUndo.getMax() != 0 && redoUndo.getCurrent() != 0) {
            Log.d(this.getClass().getSimpleName(), "true undo");
            if (undoRedoListener != null) {
                undoRedoListener.canUndo(true);
            }
            return true;
        } else {
            Log.d(this.getClass().getSimpleName(), "false undo");
            if (undoRedoListener != null) {
                undoRedoListener.canUndo(false);
            }
            return false;
        }
    }

    /**
     * checks if the Polygon has the minimum amount of Nodes
     * 
     * @author konerman
     */
    public boolean hasEnoughNodes() {
        if (interpreter instanceof AreaMotionInterpreter && polygon.size() >= 3) {
            return true;
        }
        if (interpreter instanceof BuildingMotionInterpreter
                && polygon.size() >= 4) {
            return true;
        }
        if (interpreter instanceof PointMotionInterpreter
                && polygon.size() == 1) {
            return true;
        }
        if (interpreter instanceof WayMotionInterpreter && polygon.size() >= 2) {
            return true;
        }
        return false;
    }

    /**
     * Set the actual PointToCoordsTransformUtil with the actual location and
     * camera parameters
     * 
     * @param pointTrans
     *            the actual object
     * 
     * @author sbollen
     */
    public void setTransformUtil(PointToCoordsTransformUtil pointTrans) {
        this.pointTrans = pointTrans;
    }

    public void setUndoRedoListener(UndoRedoListener undoRedoListener) {
        this.undoRedoListener = undoRedoListener;
    }

    /**
     * Create an OsmElement from the given polygon
     * 
     * @return the created OsmElement (with located nodes)
     *
     * @author sbollen
     */
    public OsmElement create() {
        return interpreter.create(polygon);
    }
}
