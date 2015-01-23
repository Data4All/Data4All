package io.github.data4all.activity;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class MapViewActivityTest {
    MapViewActivity activity;

    @Before
    public void setup() throws Exception{
        this.activity = Robolectric.buildActivity(MapViewActivity.class)
                .create().get();
    }

    @Test
    public void shouldNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void getIntentFile() throws Exception {

    }
}
