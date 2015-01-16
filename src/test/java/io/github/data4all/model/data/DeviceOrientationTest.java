package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;
import io.github.data4all.model.DeviceOrientation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Testing parcelable functionality of the device orientation objects. 
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DeviceOrientationTest {
	
    /**
     * Create a new parcial to save/parcelable the testDeviceOrientation, afterwards 
     * a new deviceorientation is created from the parcel and we check if it contains all attributes.
     */
	@Test 
    public void test_parcelable_node() {
    	Parcel newParcel = Parcel.obtain();
    	DeviceOrientation testDeviceOrientation = new DeviceOrientation(10, 20, 30, 1234567);
      
    	testDeviceOrientation.writeToParcel(newParcel, 0);
    	newParcel.setDataPosition(0);
    	DeviceOrientation deParcelDeviceOrientation = DeviceOrientation.CREATOR.createFromParcel(newParcel);
    	
    	assertEquals(10, deParcelDeviceOrientation.getAzimuth(), 0);
    	assertEquals(20, deParcelDeviceOrientation.getPitch(), 0);
    	assertEquals(30, deParcelDeviceOrientation.getRoll(), 0);
    	assertEquals(1234567, deParcelDeviceOrientation.getTimestamp(), 0);
    
    }

}
