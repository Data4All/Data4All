package io.github.data4all.model.drawing;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.model.data.Way;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * This AreaMotionInterpreter is a MotionInterpreter for Areas<br/>
 * 
 * It interprets dot-wise, path-wise and single-path user input
 * 
 * @author tbrose
 * @see MotionInterpreter
 */
public class AreaMotionInterpreter implements MotionInterpreter {
    /**
     * The log-tag for this class
     */
    private static final String TAG = AreaMotionInterpreter.class
            .getSimpleName();

    /**
     * The maximum angle-variation where a point is reduced
     */
    private static final int ANGLE_VARIATION = 25;

    /**
     * The maximum combine-variation where points were combined
     */
    private static final int COMBINE_VARIATION = 25;

    /**
     * The paint to draw the points with
     */
    @Deprecated
    private final Paint pointPaint = new Paint();

    /**
     * The paint to draw the path with
     */
    @Deprecated
    private final Paint pathPaint = new Paint();

    /**
     * An object for the calculation of the point transformation
     */
    private PointToCoordsTransformUtil pointTrans;

    @Deprecated
    public AreaMotionInterpreter() {
        // Draw dark blue points
        pointPaint.setColor(POINT_COLOR);

        // Draw semi-thick light blue lines
        pathPaint.setColor(PATH_COLOR);
        pathPaint.setStrokeWidth(PATH_STROKE_WIDTH);
    }

    public AreaMotionInterpreter(PointToCoordsTransformUtil pointTrans) {
        this.pointTrans = pointTrans;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#draw(android.graphics
     * .Canvas, java.util.List)
     */
    @Deprecated
    public void draw(Canvas canvas, List<DrawingMotion> drawingMotions) {
        List<Point> areaPoints = new ArrayList<Point>();

        // Calculate all way points of the area
        for (DrawingMotion motion : drawingMotions) {
            if (motion.getPathSize() != 0 && motion.isPoint()) {
                // for dots calculate the average of the given points
                areaPoints.add(motion.average());
            } else {
                areaPoints.addAll(motion.getPoints());
            }
        }

        // reduce the polygon
        areaPoints = reduce(areaPoints);
        Log.d(TAG, "Drawing " + areaPoints.size() + " Points");

        // first draw all lines
        for (int i = 0; i < areaPoints.size(); i++) {
            // The next point in the polygon
            Point b = areaPoints.get((i + 1) % areaPoints.size());
            Point a = areaPoints.get(i);

            canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), pathPaint);
        }

        // afterwards draw the points
        for (Point p : areaPoints) {
            canvas.drawCircle(p.getX(), p.getY(), POINT_RADIUS, pointPaint);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#interprete(java.util
     * .List, io.github.data4all.model.drawing.DrawingMotion)
     */
    @Override
    public List<Point> interprete(List<Point> interpreted,
            DrawingMotion drawingMotion) {
        ArrayList<Point> result;

        if (drawingMotion == null) {
            return interpreted;
        } else if (interpreted == null) {
            result = new ArrayList<Point>();
        } else {
            result = new ArrayList<Point>(interpreted);
        }

        if (drawingMotion.isPoint()) {
            result.add(drawingMotion.average());
        } else {
            // for a path use the last point
            result.addAll(drawingMotion.getPoints());
        }

        return reduce(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#create(java.util.List)
     */
    @Override
    public OsmElement create(List<Point> polygon) {
        Way newWay = new Way(-1, 1);

        List<Node> nodeList = pointTrans.transform(polygon);
        newWay.addNodes(nodeList, false);
        return newWay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.model.drawing.MotionInterpreter#isArea()
     */
    @Override
    public boolean isArea() {
        return true;
    }

    /**
     * First reduces the points of the polygon by removing points where the
     * angel between the previous and the next point is nearly 180 degrees (25
     * degree tolerance).<br/>
     * After this procedure the edge-points are
     * {@link AreaMotionInterpreter#combine(List) combined} and returned.
     * 
     * @param polygon
     *            the polygon of the area
     * @return a reduced polygon which approximates the input polygon with a
     *         minimum of points
     */
    private static List<Point> reduce(List<Point> polygon) {
        // We need at least three points to reduce the polygon
        if (polygon.size() >= 3) {
            List<Point> newPolygon = new ArrayList<Point>();
            // The first point of the polygon wont be reduced
            newPolygon.add(polygon.get(0));

            for (int i = 0; i < polygon.size() - 1; i++) {
                // Get the previous, current and next Point in the polygon
                // The previous point is the last point added to the reduced
                // polygon for better circle detection
                Point a = newPolygon.get(newPolygon.size() - 1);
                Point b = polygon.get(i + 1);
                Point c = polygon.get((i + 2) % polygon.size());

                double alpha = Point.getBeta(a, b, c);
                Log.d(TAG, "point " + (i + 1) + ": " + Math.toDegrees(alpha)
                        + "degree");
                double variation = Math.abs(Math.toDegrees(alpha) - 180);
                if (variation >= ANGLE_VARIATION) {
                    newPolygon.add(polygon.get(i + 1));
                } else if (i == polygon.size() - 2 && newPolygon.size() < 2) {
                    // If we reduced the polygon to a line we need so keep the
                    // end-point of the line
                    newPolygon.add(b);
                }
            }
            return combine(newPolygon);
        } else {
            return polygon;
        }
    }

    /**
     * Combines the edge-points of the given polygon so that points which are
     * relatively close to each other are combined into one single point
     * 
     * @param polygon
     *            the polygon of the area
     * @return a reduced polygon which approximates the input polygon with a
     *         minimum of points
     */
    private static List<Point> combine(List<Point> polygon) {
        // A polygon with less than two points has no need to be combined
        if (polygon.size() > 1) {
            List<Point> newPolygon = new ArrayList<Point>();

            // Start at the first point with the combination
            Point mid = polygon.get(0);
            int count = 1;

            for (int i = 1; i < polygon.size(); i++) {
                Point p = polygon.get(i);

                if (Math.hypot(mid.getX() - p.getX(), mid.getY() - p.getY()) <= COMBINE_VARIATION) {
                    // The point is in range of the current mid, add him to the
                    // combined point
                    float midSumX = mid.getX() * count + p.getX();
                    float midSumY = mid.getY() * count + p.getY();
                    count++;
                    mid = new Point(midSumX / count, midSumY / count);
                } else {
                    // The point is to far away, add the 'old' combined point
                    // and start a new combination at this point
                    newPolygon.add(mid);
                    mid = p;
                    count = 1;
                }
            }

            // If the last point is not in range of the first point add him to
            // the combined polygon
            if (newPolygon.size() > 0) {
                mid = newPolygon.get(0);
                Point lastPoint = polygon.get(polygon.size() - 1);

                if (Math.hypot(mid.getX() - lastPoint.getX(), mid.getY()
                        - lastPoint.getY()) > COMBINE_VARIATION) {
                    newPolygon.add(lastPoint);
                }
            }

            return newPolygon;
        } else {
            return polygon;
        }
    }
}
