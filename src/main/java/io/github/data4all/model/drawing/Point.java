package io.github.data4all.model.drawing;

/**
 * A Point is a Object that can hold an x and y coordinate There is no further
 * logic in this class
 * 
 * @author tbrose
 */
public class Point {
	private final float x;
	private final float y;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		return (o == this)
				|| ((o instanceof Point) && x == ((Point) o).getX() && y == ((Point) o)
						.getY());
	}

	public boolean equals(float x, float y) {
		return this.x == x && this.y == y;
	}
}
