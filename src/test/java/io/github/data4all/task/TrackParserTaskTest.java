package io.github.data4all.task;

import io.github.data4all.model.data.Track;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

/**
 * @author sbrede
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TrackParserTaskTest {

    public Context context;
    public Track   track;
    public Track   tracki;

    @Before
    public void init() {
        context = Robolectric.application.getApplicationContext();
        track = setUpTrack();
        tracki = new Track();
    }

    // TODO check if this test makes sense ;)
    @Test
    public void trackParserAsyncTaskExecutionTest()
            throws InterruptedException, ExecutionException {

        AsyncTask<Void, Void, Integer> asyncTask = new TrackParserTask(context,
                track);

        // start task
        asyncTask.execute();

        // wait for task code
        Robolectric.runBackgroundTasks();

        // can run asserts on result now
        Assert.assertTrue("Should be 1, was: " + asyncTask.get(),
                asyncTask.get() == 1);
        Assert.assertTrue("Toast was not the same", ShadowToast.getTextOfLatestToast().equals(
                "Saved a track"));

    }

    @Test
    public void parseTrackMethodTest() {
        int i = new TrackParserTask(context, track).parseTrack(context, track);
        Assert.assertTrue("Should be 1, was: " + i, i == 1);
    }

    // method for to create example track
    private Track setUpTrack() {
        Track track = new Track(context);

        Location aloc = new Location("provider");
        Location bloc = new Location("provider");
        Location cloc = new Location("provider");

        aloc.setLatitude(53.07929619999999);
        aloc.setLongitude(8.801693699999987);
        aloc.setTime(new Date().getTime());
        track.addTrackPoint(aloc);

        bloc.setLatitude(53.0792962);
        bloc.setLongitude(8.8016937);
        bloc.setTime(new Date().getTime());
        track.addTrackPoint(bloc);

        cloc.setLatitude(53.0792963);
        cloc.setLongitude(8.80169379999);
        cloc.setTime(new Date().getTime());
        track.addTrackPoint(cloc);

        return track;
    }
}
