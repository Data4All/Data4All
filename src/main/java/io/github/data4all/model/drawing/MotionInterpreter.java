package io.github.data4all.model.drawing;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * A MotionInterpreter uses a list of {@link DrawingMotion DrawingMotions} to
 * render and interpret the motions in the context of the specific interpreter<br/>
 * This means that e.g. an interpreter for areas may interpret a single elliptic
 * motion as a ellipse and tries to smooth it
 * 
 * @author tbrose
 * 
 * @param T
 *            the type of the object which can be interpreted by this
 *            interpreter
 */
public interface MotionInterpreter<T> {
    public static final int POINT_COLOR = Color.BLUE;
    public static final int POINT_RADIUS = 10;
    public static final int PATH_COLOR = 0xFFBEEEEF;
    public static final float PATH_STROKE_WIDTH = 5f;

    /**
     * Interprets the given motions and draws the result on the given canvas
     * 
     * @param canvas
     *            the canvas on which the interpreted motions will be drawn
     * @param drawingMotions
     *            the motions to interpret
     */
    void draw(Canvas canvas, List<DrawingMotion> drawingMotions);

    /**
     * Interprets the given motions and creates an Object of Type T which
     * represents the content of the interpreted motions
     * 
     * @param drawingMotions
     *            the motions to interpret
     */
    T create(List<DrawingMotion> drawingMotions);
}
