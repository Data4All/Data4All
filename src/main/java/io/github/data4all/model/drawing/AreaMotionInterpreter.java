/**
 * 
 */
package io.github.data4all.model.drawing;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

/**
 * This AreaMotionInterpreter is a MotionInterpreter for Areas<br/>
 * 
 * It interprets dot-wise, path-wise and single-path user input
 * 
 * @author tbrose
 * @see MotionInterpreter
 */
public class AreaMotionInterpreter implements MotionInterpreter<Void> {

    private final Paint pointPaint = new Paint();
    private final Paint pathPaint = new Paint();
    private final Paint areaPaint = new Paint();
    private static final int POINT_RADIUS = 5;

    public AreaMotionInterpreter() {
        pointPaint.setColor(0xFF0E0EEF);
        pathPaint.setColor(0xFFAECEEF);
        areaPaint.setColor(0xFFBEEEEF);

        areaPaint.setColor(Color.BLUE);
        areaPaint.setStyle(Style.FILL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#draw(android.graphics
     * .Canvas, java.util.List)
     */
    public void draw(Canvas canvas, List<DrawingMotion> drawingMotions) {
        List<Point> areaPoints = new ArrayList<Point>();

        // Calculate all way points of the area
        for (DrawingMotion motion : drawingMotions) {
            if (motion.getPathSize() != 0 && motion.isPoint()) {
                areaPoints.add(average(motion));
            } else {
                areaPoints.addAll(motion.getPoints());
            }
        }

        // Draw the Points
        Log.d(getClass().getSimpleName(), "Drawing all Points");
        Point previous = null;
        // for (Point p : areaPoints) {
        // if (previous != null) {
        // canvas.drawLine(previous.getX(), previous.getY(), p.getX(),
        // p.getY(), pathPaint);
        // }
        // previous = p;
        // canvas.drawCircle(p.getX(), p.getY(), POINT_RADIUS, pointPaint);
        // }
        // if (previous != null) {
        // canvas.drawLine(previous.getX(), previous.getY(), areaPoints.get(0)
        // .getX(), areaPoints.get(0).getY(), pathPaint);
        // }
        //
        //
        // previous = null;
        areaPoints = reduce(areaPoints);
        for (Point p : areaPoints) {
            if (previous != null) {
                canvas.drawLine(previous.getX(), previous.getY(), p.getX(),
                        p.getY(), pathPaint);
            }
            previous = p;
            canvas.drawCircle(p.getX(), p.getY(), POINT_RADIUS, pointPaint);
        }
        if (previous != null) {
            canvas.drawLine(previous.getX(), previous.getY(), areaPoints.get(0)
                    .getX(), areaPoints.get(0).getY(), pathPaint);
        }
    }

    /**
     * 
     * @param polygon
     * @return
     */
    private static List<Point> reduce(List<Point> polygon) {
        if (polygon.size() >= 3) {
            List<Point> newPolygon = new ArrayList<Point>();
            newPolygon.add(polygon.get(0));

            for (int i = 0; i < polygon.size() - 2; i++) {
                double alpha = getBeta(newPolygon.get(newPolygon.size() - 1),
                        polygon.get(i + 1), polygon.get(i + 2));
                Log.d(AreaMotionInterpreter.class.getSimpleName(),
                        "Errechneter Winkel: " + Math.toDegrees(alpha));
                if (Math.abs(Math.toDegrees(alpha) - 180) >= 25
                        || Math.abs(Math.toDegrees(alpha) - 180) <= -25) {
                    newPolygon.add(polygon.get(i + 1));
                }
            }

            if (newPolygon.size() > 1) {
                double alpha = getBeta(newPolygon.get(newPolygon.size() - 1),
                        polygon.get(polygon.size() - 1), newPolygon.get(0));
                Log.d(AreaMotionInterpreter.class.getSimpleName(),
                        "Errechneter Winkel: " + Math.toDegrees(alpha));
                if (Math.abs(Math.toDegrees(alpha) - 180) >= 25
                        || Math.abs(Math.toDegrees(alpha) - 180) <= -25) {
                    newPolygon.add(polygon.get(polygon.size() - 1));
                }
            } else {
                newPolygon.add(polygon.get(polygon.size() - 1));
            }

            return combine(newPolygon);
        } else {
            return polygon;
        }
    }

    /**
     * @param newPolygon
     * @return
     */
    private static List<Point> combine(List<Point> polygon) {
        List<Point> newPolygon = new ArrayList<Point>();

        Point mid = null;
        int count = 0;

        for (Point p : polygon) {
            if (mid == null) {
                Log.d(AreaMotionInterpreter.class.getSimpleName(),
                        "First Point");
                mid = p;
                count = 1;
            } else if (Math.hypot(mid.getX() - p.getX(), mid.getY() - p.getY()) <= 25) {
                Log.d(AreaMotionInterpreter.class.getSimpleName(), "Extend Mid");
                mid = new Point((mid.getX() * count + p.getX()) / (count + 1),
                        (mid.getY() * count + p.getY()) / (count + 1));
                count++;
            } else {
                Log.d(AreaMotionInterpreter.class.getSimpleName(), "Add mid");
                newPolygon.add(mid);
                mid = p;
                count = 1;
            }
        }

        if (newPolygon.size() > 0) {
            mid = newPolygon.get(0);
            Point lastPoint = polygon.get(polygon.size() - 1);

            if (Math.hypot(mid.getX() - lastPoint.getX(), mid.getY()
                    - lastPoint.getY()) > 25) {
                newPolygon.add(lastPoint);
            }
        }

        return newPolygon;
    }

    private static double getBeta(Point a, Point b, Point c) {
        Point x = new Point(a.getX() - b.getX(), a.getY() - b.getY());
        Point y = new Point(c.getX() - b.getX(), c.getY() - b.getY());

        return Math.acos((x.getX() * y.getX() + x.getY() * y.getY())
                / (Math.hypot(x.getX(), x.getY()) * Math.hypot(y.getX(),
                        y.getY())));
    }

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
