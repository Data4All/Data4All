package io.github.data4all.activity;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author sbrede
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TrackDetailsActivityTest {

    @Test
    public void testInstanziation() {
        TrackDetailsActivity activity = Robolectric.buildActivity(TrackDetailsActivity.class)
                .create().get();
        assertThat(activity, notNullValue());
    }
}
