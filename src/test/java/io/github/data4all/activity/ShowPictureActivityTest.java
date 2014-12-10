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
public class ShowPictureActivityTest {
    ShowPictureActivity activity;

    @Before
    public void setup() {
        this.activity = Robolectric.buildActivity(ShowPictureActivity.class)
                .create().get();
        Intent intent = new Intent();
        File test = new File("@drawable/android3");
        intent.putExtra("file_path", test);
        activity.setIntent(intent);
    }

    @Test
    public void shouldNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void getIntentFile() throws Exception {

    }

}
