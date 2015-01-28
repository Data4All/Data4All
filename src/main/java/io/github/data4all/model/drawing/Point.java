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

    /**
     * Constructs a new point with the coordinates provided by the given point.
     * 
     * @param point
     *            The point to get the coordinates from
     */
    public Point(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("catchPoint is null");
        } else {
            this.x = point.x;
            this.y = point.y;
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o == this)
                || ((o instanceof Point) && x == ((Point) o).getX() && y == ((Point) o)
                        .getY());
    }

    public boolean equalsTo(float x, float y) {
        return this.x == x && this.y == y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return Float.valueOf(x).hashCode() + Float.valueOf(y).hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Point[x=" + x + ",y=" + y + "]";
    }

    /**
     * Measures the distance between this point and the given point.
     * 
     * @param point
     *            The point to measure the distance to
     * @return The distance between this point and the given point
     */
    public double distance(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("catchPoint is null");
        } else {
            return Math.hypot(x - point.getX(), y - point.getY());
        }
    }

    /**
     * Calculates the angle in Point b for the two lines (a,b) and (b,c)<br/>
     * If any point is {@code null} a IllegalArgumentException is thrown
     * 
     * @param a
     *            The first Point
     * @param b
     *            The second Point
     * @param c
     *            The third Point
     * 
     * @throws IllegalArgumentException
     *             If any parameter is {@code null}
     * @return The angle in Point b in radians
     */
    public static double getBeta(Point a, Point b, Point c) {
        // Calculate the two vectors
        if (a != null && b != null && c != null) {
            Point x = new Point(a.getX() - b.getX(), a.getY() - b.getY());
            Point y = new Point(c.getX() - b.getX(), c.getY() - b.getY());

            return Math.acos((x.getX() * y.getX() + x.getY() * y.getY())
                    / (Math.hypot(x.getX(), x.getY()) * Math.hypot(y.getX(),
                            y.getY())));
        } else {
            throw new IllegalArgumentException("parameters cannot be null");
        }
    }
}
