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

import io.github.data4all.model.data.AbstractDataElement;

import java.util.List;

import android.graphics.Color;

/**
 * A MotionInterpreter uses {@link DrawingMotion DrawingMotions} to interpret
 * the motions in the context of the specific interpreter and generates a
 * polygon that matches the user input<br/>
 * This means that e.g. an interpreter for areas may interpret a single elliptic
 * motion as a ellipse and tries to smooth it<br/>
 * Also you can create an AbstractDataElement from the interpreted polygon
 * 
 * @author tbrose
 * 
 * @version 2
 * 
 */
public interface MotionInterpreter {
    public static final int POINT_COLOR = Color.BLUE;
    public static final int PATH_COLOR = Color.BLUE;
    public static final int AREA_COLOR = Color.BLUE;
    public static final float PATH_STROKE_WIDTH = 5f;

    /**
     * Interprets the given motions and creates an AbstractDataElement which
     * represents the content of the interpreted motions.
     * 
     * @param polygon
     *            the interpreted polygon
     * @return the created AbstractDataModel
     */
    AbstractDataElement create(List<Point> polygon);

    /**
     * Interprets the given motion and apply it to the polygon<br/>
     * Please note that the returned list needs to be mutable!
     * 
     * @param interpreted
     *            the List of the previous interpreted points
     * @param drawingMotion
     *            the new motion to interpret
     * @return the
     */
    List<Point> interprete(List<Point> interpreted, DrawingMotion drawingMotion);

    /**
     * Used by the drawing component to determine if the first and the last
     * point of the polygon should be connected by a line.
     * 
     * @return if the polygon should be drawn as an area
     */
    boolean isArea();

    /**
     * Returns the specific number of nodes a Motion has to have to be a valid
     * construct
     * 
     * @author konerman
     * 
     * @return the minimum of nodes
     */
    int minNodes();
}
