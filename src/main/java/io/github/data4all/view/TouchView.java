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
import io.github.data4all.model.data.AbstractDataElement;
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
 * interpreted polygons.<br/>
 * The shown polygon can be modified via deletion, {@link PointMover} or
 * {@link io.github.data4all.model.drawing.RedoUndo UndoRedo} and obtained as a
 * {@link AbstractDataElement}.
 * 
 * 
 * @author tbrose
 *
 * @see MotionEvent
 * @see DrawingMotion
 * @see MotionInterpreter
 * @see UndoRedoListener
 * @see PointToCoordsTransformUtil
 * @see AbstractDataElement
 * @see PointMover
 */
public class TouchView extends View {

    /**
     * The paint to draw the path with.
     */
    private final Paint pathPaint = new Paint();

    /**
     * The paint to draw the area with.
     */
    private final Paint areaPaint = new Paint();

    /**
     * The path to draw.
     */
    private final Path path = new Path();

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

    /**
     * The current used RedoUndo listener.
     */
    private UndoRedoListener undoRedoListener;

    /**
     * All types of interpretation that are provided by this {@link TouchView}.
     * 
     * @author tbrose
     */
    public static enum InterpretationType {
        AREA, POINT, BUILDING, WAY;
    }

    /**
     * Simple constructor to use when creating a view from code.
     * 
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     */
    public TouchView(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.<br/>
     * <br/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     * 
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     */
    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply {@code R.attr.buttonStyle} for defStyleAttr; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     * 
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr
     *            An attribute in the current theme that contains a reference to
     *            a style resource that supplies default values for the view.
     *            Can be 0 to not look for defaults.
     */
    public TouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Remove all recorded DrawingMotions from this TouchView.
     */
    public void clearMotions() {
        if (polygon != null) {
            polygon.clear();
            redoUndo = new RedoUndo();
            this.undoUseable();
            this.redoUseable();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onFinishInflate()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(0, 0, 0, 0);
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
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
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
            undoUseable();
            redoUseable();
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
     * Deletes a Point of the polygon.
     * 
     * @author konerman
     * 
     * @param point
     *            The point to delete
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
     * @author konerman
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

    /**
     * Sets the type of interpretation for this {@link TouchView}.
     * 
     * @param type
     *            The {@link InterpretationType} to set
     * @throws IllegalArgumentException
     *             if {@code type} is {@code null}
     */
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

    /**
     * Adds the last removed point to the current polygon
     * 
     * @author vkochno
     */
    public void redo() {
        newPolygon = redoUndo.redo();
        polygon = newPolygon;
        redoUseable();
    }

    /**
     * Removes the last point in the current polygon
     * 
     * @author vkochno
     */
    public void undo() {
        newPolygon = redoUndo.undo();
        polygon = newPolygon;
        if (undoRedoListener != null) {
            undoRedoListener.canRedo(true);
        }
        undoUseable();
    }

    /**
     * @author vkochno
     * 
     * @return If redo can be used
     */
    public boolean redoUseable() {
        if (redoUndo.getCurrent() == redoUndo.getMax()) {
            Log.d(this.getClass().getSimpleName(), "false redo");
            if (undoRedoListener != null) {
                undoRedoListener.canRedo(false);
            }
            return true;
        } else {
            Log.d(this.getClass().getSimpleName(), "true redo");
            if (undoRedoListener != null) {
                undoRedoListener.canRedo(true);
            }
            return false;
        }
    }

    /**
     * @author vkochno
     * 
     * @return If undo can be used
     */
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
     * Set the actual PointToCoordsTransformUtil with the actual location and
     * camera parameters.
     * 
     * @author sbollen
     * @param pointTrans
     *            the actual object
     */
    public void setTransformUtil(PointToCoordsTransformUtil pointTrans) {
        this.pointTrans = pointTrans;
    }

    /**
     * Sets the {@link UndoRedoListener} to use.
     * 
     * @param undoRedoListener
     *            The {@link UndoRedoListener} to set
     */
    public void setUndoRedoListener(UndoRedoListener undoRedoListener) {
        this.undoRedoListener = undoRedoListener;
    }

    /**
     * Create an AbstractDataElement from the given polygon.
     * 
     * @author sbollen
     * @return the created AbstractDataElement (with located nodes)
     */
    public AbstractDataElement create(int rotation) {
        return interpreter.create(polygon, rotation);
    }

    /**
     * Pointer of the position of a point in the polygon.
     * 
     * @author konerman
     */
    public class PointMover {
        private final int idx;

        /**
         * Constructs a PointMover for the given index.
         * 
         * @param idx
         *            The index of the point in the polygon
         */
        public PointMover(int idx) {
            this.idx = idx;
        }

        /**
         * Moves the {@link Point} to the new coordinates and invalidates its
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
