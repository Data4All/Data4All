package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;
import android.os.Parcel;

/**
 * Testing the parcelablility of transformationbean objects.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TransformationParamBeanTest {
	
    /**
     * Create a new parcial to save/parcelable the testRelationMember, afterwards 
     * a new relation member is created from the parcel and we check if it contains all attributes.
     */
	@Test 
    public void test_parcelable_transformationbean() {
    	Parcel newParcel = Parcel.obtain();
    	Location location = new Location("test");
    	location.setLatitude(10);
    	location.setLongitude(20);
    	TransformationParamBean testTransformationParamBean = new TransformationParamBean(10, 20, 30, 40, 50, location);
    	
    	testTransformationParamBean.writeToParcel(newParcel, 0);
    	newParcel.setDataPosition(0);
    	TransformationParamBean deParcelTransformationParamBean = TransformationParamBean.CREATOR.createFromParcel(newParcel);
    	
    	assertEquals(testTransformationParamBean.getHeight(), deParcelTransformationParamBean.getHeight(), 0);
    	assertEquals(testTransformationParamBean.getCameraMaxRotationAngle(), deParcelTransformationParamBean.getCameraMaxRotationAngle(), 0);
    	assertEquals(testTransformationParamBean.getCameraMaxPitchAngle(), deParcelTransformationParamBean.getCameraMaxPitchAngle(), 0);
    	assertEquals(testTransformationParamBean.getPhotoWidth(), deParcelTransformationParamBean.getPhotoWidth(), 0);
    	assertEquals(testTransformationParamBean.getPhotoHeight(), deParcelTransformationParamBean.getPhotoHeight(), 0);
    	
    	assertEquals(location.getProvider(), deParcelTransformationParamBean.getLocation().getProvider());
    	assertEquals(location.getLatitude(), deParcelTransformationParamBean.getLocation().getLatitude(), 0); 
    	assertEquals(location.getLongitude(), deParcelTransformationParamBean.getLocation().getLongitude(), 0); 
    }

}
