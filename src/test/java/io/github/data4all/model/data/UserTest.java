/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Testing the main functionality of the User objects.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class UserTest {

    /**
     * Create a new Parcel to save/parcelable the User, afterwards a new User is
     * created from the parcel and we check if it contains all attributes.
     */
    @Test
    public void test_parcelable_user() {
        Parcel newParcel = Parcel.obtain();

        User user = new User("foobar", "x123456", "y123456");

        user.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        User deParcelUser = User.CREATOR.createFromParcel(newParcel);

        assertEquals(user.getUsername(), deParcelUser.getUsername());
        // assertEquals(user.getLoginToken(), deParcelUser.getLoginToken());

        assertEquals(user.getOAuthToken(), deParcelUser.getOAuthToken());
        assertEquals(user.getOauthTokenSecret(),
                deParcelUser.getOauthTokenSecret());
    }

}
