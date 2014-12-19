package io.github.data4all.model.drawing;

import io.github.data4all.model.data.OsmElement;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * A MotionInterpreter uses {@link DrawingMotion DrawingMotions} to interpret
 * the motions in the context of the specific interpreter and generates a
 * polygon that matches the user input<br/>
 * This means that e.g. an interpreter for areas may interpret a single elliptic
 * motion as a ellipse and tries to smooth it<br/>
 * Also you can create an OsmElement from the interpreted polygon
 * 
 * @author tbrose
 * 
 * @version 2
 * 
 */
public interface MotionInterpreter {
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
    @Deprecated
    void draw(Canvas canvas, List<DrawingMotion> drawingMotions);

    /**
     * Interprets the given motion and apply it to the polygon
     * 
     * @param interpreted
     *            the List of the previous interpreted points
     * @param drawingMotion
     *            the new motion to interpret
     * @return the
     */
    List<Point> interprete(List<Point> interpreted, DrawingMotion drawingMotion);

    /**
     * Interprets the given motions and creates an OsmElement which represents
     * the content of the interpreted motions
     * 
     * @param polygon
     *            the interpreted polygon
     */
    OsmElement create(List<Point> polygon);

    /**
     * Used by the drawing component to determine if the first and the last
     * point of the polygon should be connected by a line
     * 
     * @return if the polygon should be drawn as an area
     */
    boolean isArea();
}
