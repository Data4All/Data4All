package io.github.data4all.model.drawing;

import io.github.data4all.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author vkochno
 *
 */
public class RedoUndo {

	/**
	 * List of all added Points
	 */
	private List<Point> motions = new ArrayList<Point>();
	int maxCount, currentCount;

	public RedoUndo(List<Point> points) {
		this.motions = points;
		if(points!=null){
		maxCount = points.size();
		currentCount = points.size();
		} else{
			maxCount = 0;
			currentCount = 0;
		}
	}

	public void addMotion(Point point) {
		Log.d(this.getClass().getSimpleName(),"ADD: " + currentCount + ":" + maxCount);
		motions.add(point);
		if(maxCount==currentCount){
			maxCount++;
			currentCount++;
		} else{
			currentCount++;
			maxCount = currentCount;
		}
	}
	
	public void addList(List<Point> newPoly){
		for(Point p:newPoly){
			addMotion(p);
		}
	}

	public List<Point> undo() {
		Log.d(this.getClass().getSimpleName(),"UNDO: " + currentCount + ":" + maxCount); 
		if (currentCount != 0 && currentCount <= maxCount) {
			currentCount--;
			List<Point> relist = new ArrayList<Point>();
			for (int i = 0; i < currentCount; i++) {
				relist.add(motions.get(i));
			}
			return relist;
		}
		return motions;
	}

	public List<Point> redo() {
		Log.d(this.getClass().getSimpleName(),"REDO: " + currentCount + ":" + maxCount); 
		if (currentCount < maxCount) {
			currentCount++;
			List<Point> relist = new ArrayList<Point>();
			for (int i = 0; i < currentCount; i++) {
				relist.add(motions.get(i));
			}
			return relist;
		}
		return motions;
	}
}
