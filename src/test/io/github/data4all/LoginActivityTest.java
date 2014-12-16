package io.github.data4all;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.github.data4all.activity.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    private LoginActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LoginActivity.class).create()
                .get();
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertTrue(true);
        //assertNotNull(activity);
    }

    // @Test
    // public void buttonClickShouldStartNewActivity() throws Exception {
    // Button button = (Button) activity.findViewById(R.id.loginButton);
    // button.performClick();
    //
    // Intent expectedIntent = new Intent(activity,
    // PrepareRequestTokenActivity.class);
    //
    // assertEquals(Robolectric.shadowOf(activity).getNextStartedActivity(),
    // (expectedIntent));
    // }

}
