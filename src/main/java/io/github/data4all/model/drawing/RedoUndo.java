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

import io.github.data4all.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to undo and redo points in the TouchView.
 * 
 * 
 * @author vkochno
 *
 */
public class RedoUndo {

    /**
     * List of all added Points.
     */
    private List<Point> motions;

    /**
     * length of the list.
     */
    private int maxCount;

    /**
     * current position.
     */
    private int currentCount;

    /**
     * Standard constructor with clean start.
     */
    public RedoUndo() {
        motions = new ArrayList<Point>();
        maxCount = 0;
        currentCount = 0;
    }

    /**
     * Constructor for using a already created list.
     * 
     * @param points
     *            List containing the points of the already drawn polygon
     */
    public RedoUndo(List<Point> points) {
        if (motions == null) {
            if (points != null) {
                motions = points;
                maxCount = points.size();
                currentCount = points.size();
            } else {
                motions = new ArrayList<Point>();
                maxCount = 0;
                currentCount = 0;
            }
        } else {
            if (points != null) {
                this.setList(points);
            }
        }
    }

    /**
     * Add a point into the RedoUndo List of points.
     * 
     * @param point
     *            Point to add
     */
    public void add(Point point) {
        Log.d(this.getClass().getSimpleName(), "ADD: " + currentCount + ":"
                + maxCount);
        if (maxCount == currentCount) {
            maxCount++;
            currentCount++;
        } else {
            for (int i = currentCount; i <= maxCount; i++) {
                motions.remove(i);
            }
            currentCount++;
            maxCount = currentCount;
        }
        motions.add(point);
    }

    private void setList(List<Point> newPoly) {
        for (Point p : newPoly) {
            motions.clear();
            Log.d(this.getClass().getSimpleName(),
                    "motions size" + motions.size());
            this.add(p);
        }
    }

    /**
     * Go a step back and return a new list.
     * 
     * @return new list with one step less
     */
    public List<Point> undo() {
        Log.d(this.getClass().getSimpleName(), "UNDO: " + currentCount + ":"
                + maxCount);
        if (currentCount != 0 && currentCount <= maxCount) {
            currentCount--;
            final List<Point> relist = new ArrayList<Point>();
            for (int i = 0; i < currentCount; i++) {
                relist.add(motions.get(i));
            }
            return relist;
        }
        return motions;
    }

    /**
     * Go a step forward,if there is a point, and return a new list.
     * 
     * @return new list with one step more
     */
    public List<Point> redo() {
        Log.d(this.getClass().getSimpleName(), "REDO: " + currentCount + ":"
                + maxCount);
        if (currentCount < maxCount) {
            currentCount++;
            final List<Point> relist = new ArrayList<Point>();
            for (int i = 0; i < currentCount; i++) {
                relist.add(motions.get(i));
            }
            return relist;
        }
        return motions;
    }

    /**
     * Getter for the current step.
     * 
     * @return current position in the list
     */
    public int getCurrent() {
        return currentCount;
    }

    /**
     * Getter for the max length.
     * 
     * @return max length of the list
     */
    public int getMax() {
        return maxCount;
    }

    /**
     * A listener for events of "undo/redo-is-(un)available".
     * 
     * @author tbrose
     */
    public interface UndoRedoListener {
        /**
         * Informs about the current undo state.
         * 
         * @param state
         *            The current undo state
         */
        void canUndo(boolean state);

        /**
         * Informs about the current redo state.
         * 
         * @param state
         *            The current redo state
         */
        void canRedo(boolean state);
    }
}
