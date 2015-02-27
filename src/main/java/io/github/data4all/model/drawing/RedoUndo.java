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
import java.util.Collections;
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
	 * List of all deleted Points.
	 */
	private List<Point> motions;

	/**
	 * List of the action
	 */
	private List<String> actions;

	/**
	 * List if the location in polygon
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
			if (points != null) {
				motions = new ArrayList<Point>();
				actions = new ArrayList<String>();
				locations = new ArrayList<Integer>();
				maxCount = points.size();
				currentCount = points.size();
				for (int i = 0; i < maxCount; i++) {
					motions.add(points.get(i));
					actions.add("ADD");
					locations.add(i);
				}
			} else {
				motions = new ArrayList<Point>();
				actions = new ArrayList<String>();
				locations = new ArrayList<Integer>();
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
	public void add(Point point, String action, int location) {
		Log.d(this.getClass().getSimpleName(), "ADDACTION:" + action
				+ currentCount + ":" + maxCount);
		if (maxCount != currentCount) {
			for (int i = currentCount; i < motions.size(); i++) {
				motions.remove(i);
				actions.remove(i);
				locations.remove(i);
			}
		}
		if (action.equals("DELET")
				&& actions.get(actions.size()-1).equals("MOVE_FROM") ||
				action.equals("DELET")
				&& actions.get(actions.size()-1).equals("DELET")) {
			actions.set(actions.size()-1,action);
		}else{
		if (action.equals("MOVE_TO")
				&& actions.get(actions.size()-1).equals("MOVE_TO")) {
			motions.set(actions.size()-1, point);
		} else {
			motions.add(point);
			actions.add(action);
			locations.add(location);
		}}
		currentCount++;
		maxCount = currentCount;
	}

	public void setList(List<Point> newPoly) {
		for (Point p : newPoly) {
			Log.d(this.getClass().getSimpleName(),
					"motions size" + motions.size());
			this.add(p, "ADD", maxCount);
		}
	}

	/**
	 * Go a step back and return a new list.
	 * 
	 * @return new list with one step less
	 */
	public Point undo() {
		currentCount--;
		Log.d(this.getClass().getSimpleName(), "UNDO: " + currentCount + ":"
				+ maxCount + ":" +" Locationsize:" + locations.size() + "actionsize:"
				+ actions.size() + "motionsize" + motions.size());
		for(String s:actions){
			Log.d(this.getClass().getSimpleName(), s);
		}
		if (currentCount >= 0 && currentCount <= maxCount) {
			return motions.get(currentCount);
		}
		return null;
	}

	/**
	 * Go a step forward,if there is a point, and return a new list.
	 * 
	 * @return new list with one step more
	 */
	public Point redo() {
		Log.d(this.getClass().getSimpleName(), "REDO: " + currentCount + ":"
				+ maxCount + ":" + locations.get(currentCount) + "actionsize:"
				+ actions.size() + "motionsize" + motions.size());
		for(String s:actions){
			Log.d(this.getClass().getSimpleName(), s);
		}
		if (currentCount != maxCount) {
			return motions.get(currentCount++);
		}
		return null;
	}

	/**
	 * Return a string with contains the current action
	 * 
	 * @return a action-string
	 */
	public String getAction() {
		return actions.get(currentCount);
	}

	/**
	 * Get the location of a point
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
	}
}
