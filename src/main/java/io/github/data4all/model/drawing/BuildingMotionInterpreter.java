package io.github.data4all.model.drawing;

import io.github.data4all.model.data.OsmElement;

import java.util.ArrayList;
import java.util.List;

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
     * io.github.data4all.model.drawing.MotionInterpreter#create(java.util.List)
     */
    @Override
    public OsmElement create(List<Point> polygon) {
        // TODO Auto-generated method stub
        return null;
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
     * @see io.github.data4all.model.drawing.MotionInterpreter#isArea()
     */
    @Override
    public boolean isArea() {
        return true;
    }

}
