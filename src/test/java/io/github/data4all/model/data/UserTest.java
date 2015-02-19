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
        //assertEquals(user.getLoginToken(), deParcelUser.getLoginToken());

        assertEquals(user.getOAuthToken(), deParcelUser.getOAuthToken());
        assertEquals(user.getOauthTokenSecret(),
                deParcelUser.getOauthTokenSecret());
    }

}
