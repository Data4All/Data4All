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
     * Create a new parcial to save/parcelable the testRelationMember,
     * afterwards a new relation member is created from the parcel and we check
     * if it contains all attributes.
     */
    @Test
    public void test_parcelable_transformationbean() {
        Parcel newParcel = Parcel.obtain();
        Location location = new Location("test");
        location.setLatitude(10);
        location.setLongitude(20);
        TransformationParamBean testBean = new TransformationParamBean(10, 20,
                30, 40, 50, location);

        testBean.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        TransformationParamBean deParcelBean = TransformationParamBean.CREATOR
                .createFromParcel(newParcel);

        assertEquals(testBean.getHeight(), deParcelBean.getHeight(), 0);
        assertEquals(testBean.getCameraMaxRotationAngle(),
                deParcelBean.getCameraMaxRotationAngle(), 0);
        assertEquals(testBean.getCameraMaxPitchAngle(),
                deParcelBean.getCameraMaxPitchAngle(), 0);
        assertEquals(testBean.getPhotoWidth(), deParcelBean.getPhotoWidth(), 0);
        assertEquals(testBean.getPhotoHeight(), deParcelBean.getPhotoHeight(),
                0);

        assertEquals(location.getProvider(), deParcelBean.getLocation()
                .getProvider());
        assertEquals(location.getLatitude(), deParcelBean.getLocation()
                .getLatitude(), 0);
        assertEquals(location.getLongitude(), deParcelBean.getLocation()
                .getLongitude(), 0);
    }
}
