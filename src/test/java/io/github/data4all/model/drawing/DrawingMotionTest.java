/**
 * 
 */
package io.github.data4all.model.drawing;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the DrawingMotion class
 * 
 * @author tbrose
 */
public class DrawingMotionTest {
	/**
	 * The delta for float equals assertion
	 */
	private static final float ASSERT_FLOAT_DELTA = 0.0f;

	/**
	 * The instance for testing
	 */
	private DrawingMotion drawingMotion;

	@Before
	public void setup() {
		drawingMotion = new DrawingMotion();
	}

	/**
	 * Adds points to the DrawingMotion:<br/>
	 * Point_0 has coordinates (0,1)<br/>
	 * Point_1 has coordinates (2,3)<br/>
	 * And so on ...
	 * 
	 * @param count
	 *            the amount of points to be added
	 */
	private void addPoints(int count) {
		for (int i = 0; i < count; i++) {
			drawingMotion.addPoint(2 * i, 2 * i + 1);
		}
	}

	// Tests for addPoint

	@Test
	public void addPoint_() {

	}

	// Tests for isPath

	@Test
	public void isPath_noEntries_resultIsFalse() {
		assertEquals(drawingMotion.isPath(), false);
	}

	@Test
	public void isPath_edgeEntries_resultIsFalse() {
		drawingMotion.addPoint(0, 0);
		drawingMotion.addPoint(DrawingMotion.POINT_TOLERANCE, 0);
		drawingMotion.addPoint(-DrawingMotion.POINT_TOLERANCE, 0);
		drawingMotion.addPoint(0, DrawingMotion.POINT_TOLERANCE);
		drawingMotion.addPoint(0, -DrawingMotion.POINT_TOLERANCE);

		assertEquals(drawingMotion.isPath(), false);
	}

	@Test
	public void isPath_overEdgeEntries_resultIsTrue() {
		drawingMotion.addPoint(0, 0);
		drawingMotion.addPoint(Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
		drawingMotion.addPoint(-Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
		drawingMotion.addPoint(0, Math.nextUp(DrawingMotion.POINT_TOLERANCE));
		drawingMotion.addPoint(0, -Math.nextUp(DrawingMotion.POINT_TOLERANCE));

		assertEquals(drawingMotion.isPath(), true);
	}

	// Tests for isPoint

	@Test
	public void isPoint_noEntries_resultIsFalse() {
		assertEquals(drawingMotion.isPoint(), false);
	}

	@Test
	public void isPoint_edgeEntries_resultIsTrue() {
		drawingMotion.addPoint(0, 0);
		drawingMotion.addPoint(DrawingMotion.POINT_TOLERANCE, 0);
		drawingMotion.addPoint(-DrawingMotion.POINT_TOLERANCE, 0);
		drawingMotion.addPoint(0, DrawingMotion.POINT_TOLERANCE);
		drawingMotion.addPoint(0, -DrawingMotion.POINT_TOLERANCE);

		assertEquals(drawingMotion.isPoint(), true);
	}

	@Test
	public void isPoint_overEdgeEntries_resultIsFalse() {
		drawingMotion.addPoint(0, 0);
		drawingMotion.addPoint(Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
		drawingMotion.addPoint(-Math.nextUp(DrawingMotion.POINT_TOLERANCE), 0);
		drawingMotion.addPoint(0, Math.nextUp(DrawingMotion.POINT_TOLERANCE));
		drawingMotion.addPoint(0, -Math.nextUp(DrawingMotion.POINT_TOLERANCE));

		assertEquals(drawingMotion.isPoint(), false);
	}

	// Tests for getStart

	@Test
	public void getStart_noEntries_resultIsNull() {
		assertEquals(drawingMotion.getStart(), null);
	}

	@Test
	public void getStart_someEntries_resultIsCorrect() {
		drawingMotion.addPoint(-1, -1);
		addPoints(10);
		assertEquals(drawingMotion.getStart().equals(-1, -1), true);
	}

	// Tests for getEnd

	@Test
	public void getEnd_noEntries_resultIsNull() {
		assertEquals(drawingMotion.getEnd(), null);
	}

	@Test
	public void getEnd_someEntries_resultIsCorrect() {
		addPoints(10);
		drawingMotion.addPoint(-1, -1);
		assertEquals(drawingMotion.getEnd().equals(-1, -1), true);
	}

	@Test
	public void getStartGetEnd_oneEntries_samePoint() {
		drawingMotion.addPoint(-1, -1);
		assertEquals(drawingMotion.getStart(), drawingMotion.getEnd());
	}

	// Tests for getPathSize

	@Test
	public void getPathSize_noEntries_sizeIsZero() {
		assertEquals(drawingMotion.getPathSize(), 0);
	}

	@Test
	public void getPathSize_someEntries_sizeIsCorrect() {
		addPoints(10);
		assertEquals(drawingMotion.getPathSize(), 10);

		// Add some more points ... just to be on the safe side ...
		addPoints(10);
		assertEquals(drawingMotion.getPathSize(), 20);
	}

	// Tests for getPoints

	@Test
	public void getPoints_noEntries_resultIsEmpty() {
		assertEquals(drawingMotion.getPoints().isEmpty(), true);
	}

	@Test
	public void getPoints_someEntries_rightPointsAndOrder() {
		addPoints(10);
		List<Point> points = drawingMotion.getPoints();
		for (int i = 0; i < 10; i++) {
			Point point = points.get(i);
			assertEquals(point.getX(), 2 * i, ASSERT_FLOAT_DELTA);
			assertEquals(point.getY(), 2 * i + 1, ASSERT_FLOAT_DELTA);
		}
	}

	// Tests for getPoint

	@Test(expected = IndexOutOfBoundsException.class)
	public void getPoint_noEntries_indexOutOfBoundsException() {
		drawingMotion.getPoint(0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void getPoint_someEntriesIndexNegative_indexOutOfBoundsException() {
		addPoints(10);
		drawingMotion.getPoint(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void getPoint_someEntriesIndexToHigh_indexOutOfBoundsException() {
		addPoints(10);
		drawingMotion.getPoint(10);
	}

	@Test
	public void getPoint_someEntries_rightPointsAndOrder() {
		addPoints(10);
		for (int i = 0; i < 10; i++) {
			Point point = drawingMotion.getPoint(i);
			assertEquals(point.getX(), 2 * i, ASSERT_FLOAT_DELTA);
			assertEquals(point.getY(), 2 * i + 1, ASSERT_FLOAT_DELTA);
		}
	}
}
