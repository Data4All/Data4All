package io.github.data4all.model.drawing;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * This PointMotionInterpreter is a MotionInterpreter for Points<br/>
 * 
 * It interprets the last motion in the given List.<br/>
 * 
 * If this motion is a dot, the average is calculated and shown<br/>
 * If this motion is a path, the last Point of the path is shown
 * 
 * @author tbrose
 * @see MotionInterpreter
 */
public class PointMotionInterpreter implements MotionInterpreter<Void> {
    /**
     * The paint to draw the points with
     */
    private final Paint pointPaint = new Paint();

    public PointMotionInterpreter() {
        // Draw dark blue points
        pointPaint.setColor(POINT_COLOR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#draw(android.graphics
     * .Canvas, java.util.List)
     */
    public void draw(Canvas canvas, List<DrawingMotion> drawingMotions) {
        if (drawingMotions != null && drawingMotions.size() > 0) {
            DrawingMotion lastMotion = drawingMotions
                    .get(drawingMotions.size() - 1);
            Point point;
            if (lastMotion.getPathSize() != 0 && lastMotion.isPoint()) {
                point = average(lastMotion);
            } else {
                point = lastMotion.getEnd();
            }
            if (point != null) {
                canvas.drawCircle(point.getX(), point.getY(), POINT_RADIUS,
                        pointPaint);
            }
        }
    }

    /**
     * Calculates the average point over all points in the given motion
     * 
     * @param motion
     *            The motion to calculate the average point from
     * @return The average point over all points in the motion
     */
    private static Point average(DrawingMotion motion) {
        if (motion.getPathSize() == 0) {
            return null;
        } else {
            float x = 0;
            float y = 0;
            for (Point p : motion.getPoints()) {
                x += p.getX();
                y += p.getY();
            }
            return new Point(x / motion.getPathSize(), y / motion.getPathSize());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#create(java.util.List)
     */
    public Void create(List<DrawingMotion> drawingMotions) {
        return null;
    }

}
