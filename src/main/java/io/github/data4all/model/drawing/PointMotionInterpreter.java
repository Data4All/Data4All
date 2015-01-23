package io.github.data4all.model.drawing;

import io.github.data4all.model.data.OsmElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This PointMotionInterpreter is a MotionInterpreter for Points<br/>
 * 
 * It interprets the last motion in the given List.<br/>
 * 
 * If this motion is a dot, the average is calculated and shown<br/>
 * If this motion is a path, the last Point of the path is shown
 * 
 * @author tbrose
 * @version 2
 * @see MotionInterpreter
 */
public class PointMotionInterpreter implements MotionInterpreter {

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
        if (drawingMotion == null) {
            return interpreted;
        } else if (interpreted.size() > 3) {
            return interpreted;
        } else if (drawingMotion.getPathSize() == 0) {
            return new ArrayList<Point>();
        } else if (drawingMotion.isPoint()) {
            // for dots use the average of the given points
            List<Point> result = new ArrayList<Point>();
            result.add(drawingMotion.average());
            return result;
        } else {
            // for a path use the last point
            List<Point> result = new ArrayList<Point>();
            result.add(drawingMotion.getEnd());
            return result;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.model.drawing.MotionInterpreter#isArea()
     */
    @Override
    public boolean isArea() {
        return false;
    }

}
