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
	private List<DrawingMotion> motions = new ArrayList<DrawingMotion>();
	int maxCount, currentCount;

	public RedoUndo(List<DrawingMotion> points) {
		this.motions = points;
		maxCount = 0;
		currentCount = 0;
	}

	public void addMotion(DrawingMotion p) {
		Log.d(this.getClass().getSimpleName(),"ADD: " + currentCount + ":" + maxCount);
		motions.add(p);
		maxCount++;
		currentCount++;
	}

	public List<DrawingMotion> undo() {
		Log.d(this.getClass().getSimpleName(),"UNDO: " + currentCount + ":" + maxCount); 
		if (currentCount != 0 && currentCount <= maxCount) {
			currentCount--;
			List<DrawingMotion> relist = new ArrayList<DrawingMotion>();
			for (int i = 0; i < currentCount; i++) {
				relist.add(motions.get(i));
			}
			return relist;
		}
		return motions;
	}

	public List<DrawingMotion> redo() {
		Log.d(this.getClass().getSimpleName(),"REDO: " + currentCount + ":" + maxCount); 
		if (currentCount < maxCount) {
			currentCount++;
			List<DrawingMotion> relist = new ArrayList<DrawingMotion>();
			for (int i = 0; i < currentCount; i++) {
				relist.add(motions.get(i));
			}
			return relist;
		}
		return motions;
	}
}
