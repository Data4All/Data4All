package io.github.data4all.model.drawing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the DrawingMotion class
 * 
 * @author tbrose
 */
public class PointTest {
    /**
     * The instance for testing
     */
    private Point point;

    @Before
    public void setup() {
        point = new Point(1, 1);
    }

    // Tests for equals(float, float)

    @Test
    public void equals_sameCoordinates_resultIsTrue() {
        assertTrue(point.equalsTo(1, 1));
    }

    @Test
    public void equals_otherXCoordinate_resultIsFalse() {
        assertFalse(point.equalsTo(2, 1));
    }

    @Test
    public void equals_otherYCoordinate_resultIsFalse() {
        assertFalse(point.equalsTo(1, 2));
    }

    // Tests for equals(Object)

    @Test
    public void equals_sameOject_resultIsTrue() {
        assertTrue(point.equals(point));
    }

    @Test
    public void equals_otherPointWithSameCoordinates_resultIsTrue() {
        assertTrue(point.equals(new Point(1, 1)));
    }

    @Test
    public void equals_otherPointWithOtherXCoordinate_resultIsFalse() {
        assertFalse(point.equals(new Point(2, 1)));
    }

    @Test
    public void equals_otherPointWithOtherYCoordinate_resultIsFalse() {
        assertFalse(point.equals(new Point(1, 2)));
    }

    @Test
    public void equals_otherClass_resultIsFalse() {
        assertFalse(point.equals(new Object()));
    }
}
