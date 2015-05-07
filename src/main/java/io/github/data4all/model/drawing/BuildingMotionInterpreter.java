/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.model.drawing;

import io.github.data4all.model.data.DataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.util.PointToCoordsTransformUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This BuildingMotionInterpreter is a MotionInterpreter for buildings.<br/>
 * 
 * It interprets a three dot user input and calculates the fourth point of the
 * building.<br/>
 * If a motion is not a dot, the end point is used.
 * 
 * @author tbrose
 * @version 2
 * @see MotionInterpreter
 */
public class BuildingMotionInterpreter implements MotionInterpreter {

    private PointToCoordsTransformUtil pointTrans;
    private static final int MIN_NODES = 4;

    /**
     * Creates an BuildingMotionInterpreter with the specified transformation
     * utility.
     * 
     * @param pointTrans
     *            the transformation utility
     */
    public BuildingMotionInterpreter(PointToCoordsTransformUtil pointTrans) {
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
        final List<Point> result;

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
            result.add(pointTrans.fourthBuildingPoint(result));
        }

        return result;
    }

    /**
     * @author sbollen
     * @see io.github.data4all.model.drawing.MotionInterpreter#create(java.util.List,
     *      int)
     */
    @Override
    public DataElement create(List<Point> polygon) {
        final PolyElement element = new PolyElement(-1,
                PolyElementType.BUILDING);

        final List<Node> nodeList = pointTrans.transform(polygon);
        nodeList.add(nodeList.get(0));
        element.addNodes(nodeList, false);
        return element;
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

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.model.drawing.MotionInterpreter#minNodes()
     */
    @Override
    public int minNodes() {
        return MIN_NODES;
    }

}
