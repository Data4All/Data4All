package io.github.data4all.model.drawing;

import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * This BuildingMotionInterpreter is a MotionInterpreter for buildings<br/>
 * 
 * It interprets a three dot user input and calculates the fourth point of the
 * building<br/>
 * If a motion is not a dot, the end point is used
 * 
 * @author tbrose
 * @version 2
 * @see MotionInterpreter
 */
public class BuildingMotionInterpreter implements MotionInterpreter {

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
    public BuildingMotionInterpreter() {
        // Draw dark blue points
        pointPaint.setColor(POINT_COLOR);

        // Draw semi-thick light blue lines
        pathPaint.setColor(PATH_COLOR);
        pathPaint.setStrokeWidth(PATH_STROKE_WIDTH);
    }

    public BuildingMotionInterpreter(PointToCoordsTransformUtil pointTrans) {
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

        // Collect the first three motions
        for (DrawingMotion motion : drawingMotions) {
            if (motion.getPathSize() != 0 && motion.isPoint()) {
                // for dots calculate the average of the given points
                areaPoints.add(motion.average());
            } else {
                // for a path use the last point
                areaPoints.add(motion.getEnd());
            }
            if (areaPoints.size() > 2) {
                break;
            }
        }

        if (areaPoints.size() == 3) {
            addFourthPoint(areaPoints);
        }

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

    /**
     * Calculates the fourth point in dependence of the first three points of
     * the given list
     * 
     * @param areaPoints
     *            A list with exact three points
     */
    private static void addFourthPoint(List<Point> areaPoints) {
        Point a = areaPoints.get(0);
        Point b = areaPoints.get(1);
        Point c = areaPoints.get(2);

        float x = a.getX() + (c.getX() - b.getX());
        float y = a.getY() + (c.getY() - b.getY());

        Point d = new Point(x, y);
        areaPoints.add(d);
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
        } else if (interpreted.size() > 3) {
            return interpreted;
        } else {
            result = new ArrayList<Point>(interpreted);
        }

        if (drawingMotion.getPathSize() != 0 && drawingMotion.isPoint()) {
            // for dots use the average of the given points
            result.add(drawingMotion.average());
        } else {
            // for a path use the last point
            result.add(drawingMotion.getEnd());
        }

        if (result.size() == 3) {
            addFourthPoint(result);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.model.drawing.MotionInterpreter#create(java.util.List)
     */
    @Override
    public OsmElement create(List<Point> polygon) {
        // create a new Relation and add the given nodes as relationMembers to
        // the Relation
        Relation relation = new Relation(-1, 1);
        for (Node node : pointTrans.transform(polygon)) {
            relation.addMember(new RelationMember(null, node));
        }
        return relation;
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

}
