/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
     * Create a new Parcel to save/parcelable the testRelationMember,
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
