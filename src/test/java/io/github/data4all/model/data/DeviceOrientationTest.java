package io.github.data4all.model.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.github.data4all.model.DeviceOrientation;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for the DeviceOrientation class
 * 
 * @author steeve
 */
public class DeviceOrientationTest {
	/**
     * The instance for testing
     */
	private DeviceOrientation deviceOrientation;
	
	 @Before
	    public void setup() {
	        deviceOrientation = new DeviceOrientation(100.10f,-20.40f,1.71f,1 );
	    }
	 
	// Tests for equals(float, float,float,long)

	    @Test
	    public void equals_sameCoordinates_resultIsTrue() {
	        assertTrue(deviceOrientation.equalsTo(100.10f,-20.40f,1.71f,1));
	    }
	    
	    @Test
	    public void equals_otherAzimuthCoordinate_resultIsFalse() {
	        assertFalse(deviceOrientation.equalsTo(10.10f,-20.02f,1.71f,1));
	    }

	    @Test
	    public void equals_otherPitchCoordinate_resultIsFalse() {
	        assertFalse(deviceOrientation.equalsTo(100.10f,2f,1.71f,1));
	    }
	    
	    @Test
	    public void equals_othersRollCoordinate_resultIsFalse() {
	        assertFalse(deviceOrientation.equalsTo(100.10f,2f,1.0f,1));
	    }
	    
	    @Test
	    public void equals_otherTimeCoordinate_resultIsFalse() {
	        assertFalse(deviceOrientation.equalsTo(100.10f,2f,1.71f,2));
	    }
	    
	 // Tests for equals(Object)

	    @Test
	    public void equals_sameOject_resultIsTrue() {
	        assertTrue(deviceOrientation.equals(deviceOrientation));
	    }

	    @Test
	    public void equals_otherDeviceOrientationWithSameCoordinates_resultIsTrue() {
	        assertTrue(deviceOrientation.equals(new DeviceOrientation(100.10f,-20.40f,1.71f,1 )));
	    }

	   

}
