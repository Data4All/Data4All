package io.github.data4all.model.drawing;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;

import org.mockito.verification.VerificationMode;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Test-utils for theclasses implementing MotionInterpreter
 * 
 * @author tbrose
 */
public abstract class MotionInterpreterTest {
    protected static void verifyDrawCircle(Canvas canvas, VerificationMode mode) {
        verify(canvas, mode).drawCircle(anyFloat(), anyFloat(), anyFloat(),
                (Paint) anyObject());
    }

    protected static void verifyDrawLine(Canvas canvas, VerificationMode mode) {
        verify(canvas, mode).drawLine(anyFloat(), anyFloat(), anyFloat(),
                anyFloat(), (Paint) anyObject());
    }

    protected static DrawingMotion getDrawingMotion(float... points) {
        DrawingMotion motion = new DrawingMotion();
        for (int i = 0; i < points.length; i = i + 2) {
            motion.addPoint(points[i], points[i + 1]);
        }
        return motion;
    }
}
