package io.github.data4all.task;

import static org.junit.Assert.*;
import io.github.data4all.Constants;
import io.github.data4all.R;

import java.io.File;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class UploadToOpenStreetMapTaskTest {

    private OAuthConsumer consumer;
    private File          exampleFile = new File("samplegpx.gpx");

    @Before
    public void setUp() throws Exception {
        Robolectric.getBackgroundScheduler().pause();
        consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY,
                Constants.CONSUMER_SECRET);
        consumer.setTokenWithSecret(PreferenceManager
                .getDefaultSharedPreferences(Robolectric.application).getString("oauth_token", null), PreferenceManager
                .getDefaultSharedPreferences(Robolectric.application).getString("oauth_token_secret", null));

    }

    @Test
    public void testNormalFlow() throws Exception {
        AsyncTask<Void, Void, Void> asyncTask = new UploadToOpenStreetMapTask(
                Robolectric.application, consumer, exampleFile, "", "",
                "private");

        asyncTask.execute();

        Robolectric.runBackgroundTasks();

        assertEquals(ShadowToast.getTextOfLatestToast(),
                Robolectric.application.getString(R.string.success));

    }
}
