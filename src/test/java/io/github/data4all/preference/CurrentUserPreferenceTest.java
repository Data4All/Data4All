package io.github.data4all.preference;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class CurrentUserPreferenceTest {
    private CurrentUserPreference pref;

    @Before
    public void setUp() {
        pref = new CurrentUserPreference(Robolectric.application);
    }

    @Test
    public void test_isPersistent_false() {
        Assert.assertThat(pref.isPersistent(), CoreMatchers.is(false));
    }

    @Test
    public void test_noUser_disabled() {
        Assert.assertThat(pref.isEnabled(), CoreMatchers.is(false));
    }
}
