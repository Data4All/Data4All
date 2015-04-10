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
     * The log-tag for this class.
     */
    private static final String TAG = AreaMotionInterpreter.class
            .getSimpleName();

    /**
     * List of all deleted Points.
     */
    private List<Point> motions;

    /**
     * List of the actions.
     */
    private List<String> actions;

    /**
     * List of the locations in polygon.
     */
    private List<Integer> locations;

    /**
     * length of the list.
     */
    private int maxCount;

    /**
     * current position.
     */
    private int currentCount;
    
    /**
     * Standard strings for actions
     */
    final static String add = "ADD";
    final static String delete = "DELETE";
    final static String moveFrom = "MOVE_FROM";
    final static String moveTo = "MOVE_TO";
    final static String movePolyFrom = "MOVE_POLY_FROM";
    final static String movePolyTo = "MOVE_POLY_TO";


    /**
     * Standard constructor with clean start.
     */
    public RedoUndo() {
        motions = new ArrayList<Point>();
        actions = new ArrayList<String>();
        locations = new ArrayList<Integer>();
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
            Log.d(TAG, "motion is null");
            if (points != null) {
                Log.d(TAG, "motions is null but points are there, count: "
                        + points.size());
                motions = new ArrayList<Point>();
                actions = new ArrayList<String>();
                locations = new ArrayList<Integer>();
                maxCount = points.size();
                currentCount = points.size();
                for (int i = 0; i < maxCount; i++) {
                    motions.add(points.get(i));
                    actions.add(add);
                    locations.add(i);
                }
            } else {
                Log.d(TAG, "motions is null and point too");
                motions = new ArrayList<Point>();
                actions = new ArrayList<String>();
                locations = new ArrayList<Integer>();
                maxCount = 0;
                currentCount = 0;
            }
        } else if (points != null) {
            Log.d(TAG, "motion and point are not null");
            this.setList(points);
        }
    }

    /**
     * * Add a point,action and location into the RedoUndo Lists.
     * 
     * @param point
     *            Point to add
     * @param action
     *            action for point (ADD,DELET,MOVE_FROM,MOVE_TO)
     * @param location
     *            location where the point was in polygon
     */
    public void add(Point point, String action, int location) {

        Log.d(TAG, "ADD:" + action);
        if (maxCount == currentCount) {
            if (action.equals(delete)
                    && actions.get(actions.size() - 1).equals(moveFrom)) {
                actions.set(actions.size() - 1, action);
            } else if (action.equals(moveTo)
                    && actions.get(actions.size() - 1).equals(moveTo)) {
                motions.set(actions.size() - 1, point);
            } else if (action.equals(movePolyTo)
                    && actions.get(actions.size() - 1).equals(movePolyTo)) {
                motions.set(actions.size() - 1, point);
            }else {
            	Log.d(TAG, "ADD ELSE");
                motions.add(point);
                actions.add(action);
                locations.add(location);
                currentCount++;
                maxCount = currentCount;
            }
        } else {
            Log.d(TAG, "use remove");
            this.remove();
            this.add(point, action, location);
        }
        for (String s : actions) {
            Log.d(TAG, s);
        }
    }

    /**
     * Set the list of points of the new drawn polygon.
     * 
     * @param newPoly
     *            list of points of the new polygon
     */
    private void setList(List<Point> newPoly) {
        for (Point p : newPoly) {
            Log.d(TAG, "motions size" + motions.size());
            this.add(p, add, maxCount);
        }
    }

    /**
     * Delete all points from the list, which are from the last drawn polygon,
     * when a new polygon is drawn.
     */
    private void remove() {
        int loop = maxCount - currentCount;
        Log.d(TAG, "remove some elements: " + loop);
        while (loop >= 0) {
            motions.remove(motions.size() - 1);
            actions.remove(actions.size() - 1);
            locations.remove(locations.size() - 1);
            loop--;
        }
        currentCount = motions.size();
        maxCount = currentCount;
    }

    /**
     * Go a step back.
     * 
     * @return the undone point
     */
    public Point undo() {
        currentCount--;
        Log.d(TAG,
                "UNDO: " + currentCount + ":" + maxCount + ":"
                        + " Locationsize:" + locations.size() + "actionsize:"
                        + actions.size() + "motionsize" + motions.size());
        for (String s : actions) {
            Log.d(TAG, s);
        }
        if (currentCount >= 0 && currentCount <= maxCount) {
            return motions.get(currentCount);
        }
        return null;
    }

    /**
     * Go a step forward,if there is a point.
     * 
     * @return the redone point
     */
    public Point redo() {
        Log.d(TAG,
                "REDO: " + currentCount + ":" + maxCount + ":"
                        + locations.get(currentCount) + "actionsize:"
                        + actions.size() + "motionsize" + motions.size());
        for (String s : actions) {
            Log.d(TAG, s);
        }
        if (currentCount != maxCount) {
            return motions.get(currentCount++);
        }
        return null;
    }

    /**
     * Return a string which contains the current action.
     * 
     * @return a action-string
     */
    public String getAction() {
        return actions.get(currentCount);
    }

    /**
     * Get the location of a point.
     */
    public int getLocation() {
        return locations.get(currentCount);
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

        /**
         * Informas about the enough notes state
         * 
         * @param state
         *            the current enough note state
         */
        void okUseable(boolean state);
    }

}
