package io.github.data4all.model.drawing;

import io.github.data4all.model.data.OsmElement;

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
public class PointMotionInterpreter implements MotionInterpreter {
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
                point = lastMotion.average();
            } else {
                point = lastMotion.getEnd();
            }
            if (point != null) {
                canvas.drawCircle(point.getX(), point.getY(), POINT_RADIUS,
                        pointPaint);
            }
        }
    }

    /* (non-Javadoc)
     * @see io.github.data4all.model.drawing.MotionInterpreter#interprete(java.util.List, io.github.data4all.model.drawing.DrawingMotion)
     */
    @Override
    public List<Point> interprete(List<Point> interpreted,
            DrawingMotion drawingMotion) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see io.github.data4all.model.drawing.MotionInterpreter#create(java.util.List)
     */
    @Override
    public OsmElement create(List<Point> polygon) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see io.github.data4all.model.drawing.MotionInterpreter#isArea()
     */
    @Override
    public boolean isArea() {
        // TODO Auto-generated method stub
        return false;
    }

}
