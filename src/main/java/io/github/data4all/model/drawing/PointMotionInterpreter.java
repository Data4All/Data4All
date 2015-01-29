package io.github.data4all.model.drawing;

import io.github.data4all.model.data.OsmElement;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This PointMotionInterpreter is a MotionInterpreter for Points.<br/>
 * 
 * It interprets the last motion in the given List.<br/>
 * 
 * If this motion is a dot, the average is calculated and shown.<br/>
 * If this motion is a path, the last Point of the path is shown.
 * 
 * @author tbrose
 * @version 2
 * @see MotionInterpreter
 */
public class PointMotionInterpreter implements MotionInterpreter {

    private PointToCoordsTransformUtil pointTrans;
    
    /**
     * Creates an PointMotionInterpreter with the specified transformation
     * utility.
     * 
     * @param pointTrans the transformation utility
     */
    public PointMotionInterpreter(PointToCoordsTransformUtil pointTrans) {
        this.pointTrans = pointTrans;
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
        if (drawingMotion == null || interpreted.size() > 3) {
            return interpreted;
        } else if (drawingMotion.getPathSize() == 0) {
            return new ArrayList<Point>();
        } else {
            final List<Point> result = new ArrayList<Point>();
            if (drawingMotion.isPoint()) {
                // for dots use the average of the given points
                result.add(drawingMotion.average());
            } else {
                // for a path use the last point
                result.add(drawingMotion.getEnd());
            }
            return result;
        }
    }

    /**
     * @author sbollen
     */
    @Override
    public OsmElement create(List<Point> polygon, int rotation) {
        //The list contains only one Node which then will be returned
        return pointTrans.transform(polygon, rotation).get(0);
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
