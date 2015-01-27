package io.github.data4all.model.drawing;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.mockito.verification.VerificationMode;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Test-utilities for the classes implementing MotionInterpreter
 * 
 * @author tbrose
 */
public abstract class MotionInterpreterTest {
    /**
     * Constructs a DrawingMotion with the given coordinate-pairs <br/>
     * 
     * The length of points <b>needs to be even</b>, but <b>is NOT checked</b>
     * by this method!<br/>
     * 
     * Every float in points with an odd index i is the x coordinate of the
     * point with index (i-1)/2<br/>
     * float with
     * 
     * Every float in points with an even index j is the y coordinate of the
     * point with index j/2<br/>
     * 
     * @param points
     */
    protected static DrawingMotion getDrawingMotion(float... points) {
        DrawingMotion motion = new DrawingMotion();
        for (int i = 0; i < points.length; i = i + 2) {
            motion.addPoint(points[i], points[i + 1]);
        }
        return motion;
    }

    /**
     * Verify if the drawCircle method is called mode-often with the any
     * coordinates
     * 
     * @param canvas
     *            The canvas mock
     * @param mode
     *            The verification mode
     */
    protected static void verifyDrawCircle(Canvas canvas, VerificationMode mode) {
        verify(canvas, mode).drawCircle(anyFloat(), anyFloat(), anyFloat(),
                (Paint) anyObject());
    }

    /**
     * Verify if the drawCircle method is called mode-often with the given
     * coordinates
     * 
     * @param canvas
     *            The canvas mock
     * @param mode
     *            The verification mode
     * @param x
     *            The x coordinate to verify
     * @param y
     *            The y coordinate to verify
     */
    protected static void verifyDrawCircle(Canvas canvas,
            VerificationMode mode, float x, float y) {
        verify(canvas, mode).drawCircle(eq(x), eq(y), anyFloat(),
                (Paint) anyObject());
    }

    /**
     * Verify if the drawLine method is called mode-often
     * 
     * @param canvas
     *            The canvas mock
     * @param mode
     *            The verification mode
     */
    protected static void verifyDrawLine(Canvas canvas, VerificationMode mode) {
        verify(canvas, mode).drawLine(anyFloat(), anyFloat(), anyFloat(),
                anyFloat(), (Paint) anyObject());
    }
}
