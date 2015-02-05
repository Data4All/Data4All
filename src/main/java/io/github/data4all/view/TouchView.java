/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.view;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.drawing.Analyzer;
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

    private static final int CATCH_DISTANCE = 0;
    /**
     * The paint to draw the path with.
     */
    private final Paint pathPaint = new Paint();
    private final Paint areaPaint = new Paint();

    /**
     * The motion interpreted Polygon.
     */
    private List<Point> polygon = new ArrayList<Point>();

    /**
     * The Polygon with the current pending motion.
     */
    private List<Point> newPolygon = new ArrayList<Point>();

    /**
     * The current motion the user is typing via the screen.
     */
    private DrawingMotion currentMotion;

    /**
     * An object for the calculation of the point transformation.
     */
    private PointToCoordsTransformUtil pointTrans;

    /**
     * The currently used interpreter.
     */
    private MotionInterpreter interpreter;

    /**
     * The current used RedoUndo object.
     */
    private RedoUndo redoUndo;
    private UndoRedoListener undoRedoListener;
    private List<Point> catchPoints;

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
     * Remove all recorded DrawingMotions from this TouchView.
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
            catchPolygonPoints();
            Log.d(this.getClass().getSimpleName(),
                    "Motion " + action + ": " + currentMotion.getPathSize()
                            + ", point: " + currentMotion.isPoint());
            postInvalidate();
        }
    }

    /**
     * Catches the points of the polygon to the pre-set catch-points.
     */
    private void catchPolygonPoints() {
        if (catchPoints != null && newPolygon != null) {
            for (int i = 0; i < newPolygon.size(); i++) {
                final Point polygonPoint = newPolygon.get(i);
                for (Point catchPoint : catchPoints) {
                    if (polygonPoint.distance(catchPoint) < CATCH_DISTANCE) {
                        newPolygon.set(i, new Point(catchPoint));
                        continue;
                    }
                }
            }
        }
    }

    /**
     * Deletes a Point of the polygon.
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
     * given coordinates.
     * 
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
     * Returns a {@link PointMover} for the given {@link Point}.<br/>
     * 
     * Use moveTo() afterwards to actually move the point.
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

    public void setCatchPoints(List<Point> catchPoints) {
        this.catchPoints = catchPoints;
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
     * @author sbollen Set the actual PointToCoordsTransformUtil with the actual
     *         location and camera parameters
     * @param pointTrans
     *            the actual object
     */
    public void setTransformUtil(PointToCoordsTransformUtil pointTrans) {
        this.pointTrans = pointTrans;
    }

    public void setUndoRedoListener(UndoRedoListener undoRedoListener) {
        this.undoRedoListener = undoRedoListener;
    }

    /**
     * @author sbollen Create an OsmElement from the given polygon
     * @return the created OsmElement (with located nodes)
     */
    public OsmElement create() {
        return interpreter.create(polygon);
    }

    /**
     * Pointer of the position of a point in the polygon.
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
         * {@link TouchView} afterwards.
         * 
         * @author konerman
         * 
         * @param x
         *            the new x-coordinate
         * @param y
         *            the new y-coordinate
         * 
         */
        public void moveTo(float x, float y) {
            polygon.set(idx, new Point(x, y));
            postInvalidate();
        }
    }
}
