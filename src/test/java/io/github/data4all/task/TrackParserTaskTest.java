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
package io.github.data4all.task;

import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    public Track track;
    public Track emptyTrack;
    
    public String expectedString;
    
    public static final SimpleDateFormat ISO8601FORMAT;

    static {
        ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ISO8601FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Before
    public void init() {
        context = Robolectric.application.getApplicationContext();
        track = setUpTrack();
        emptyTrack = new Track();
        List<TrackPoint> list = track.getTrackPoints();
        String LAT1 = Double.toString(list.get(0).getLat());
        String LAT2 = Double.toString(list.get(1).getLat());
        String LAT3 = Double.toString(list.get(2).getLat());
        
        String LON1 = Double.toString(list.get(0).getLon());
        String LON2 = Double.toString(list.get(1).getLon());
        String LON3 = Double.toString(list.get(2).getLon());
        
        String TIME1 = ISO8601FORMAT.format(new Date((list.get(0).getTime())));
        String TIME2 = ISO8601FORMAT.format(new Date((list.get(1).getTime())));
        String TIME3 = ISO8601FORMAT.format(new Date((list.get(2).getTime())));
        
        expectedString = "<?xml "
                + "version=\"1.0\" encoding=\"UTF-8\" ?>" + "<gpx "
                + "xmlns=\"http://www.topografix.com/GPX/1/1\" " 
                + "version=\"1.1\" "
                + "creator=\"Data4All - https://data4all.github.io/\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 "
                + "http://www.topografix.com/GPX/1/1/gpx.xsd \">"
                + "<trk>"
                + "<name> " + track.getTrackName() + " </name>"
                + "<trkseg>"
                + "<trkpt lat=\""
                + LAT1
                + "\" lon=\""
                + LON1
                + "\">"
                + "<time>"
                + TIME1
                + "</time>"
                + "</trkpt>"
                + "<trkpt lat=\""
                + LAT2
                + "\" lon=\""
                + LON2
                + "\">"
                + "<time>"
                + TIME2
                + "</time>"
                + "</trkpt>"
                + "<trkpt lat=\""
                + LAT3
                + "\" lon=\""
                + LON3
                + "\">"
                + "<time>"
                + TIME3
                + "</time>"
                + "</trkpt>"
                + "</trkseg>"
                + "</trk>"
                + "</gpx>";
        
    }

    @Test
    public void trackParserAsyncTaskExecutionTest()
            throws InterruptedException, ExecutionException {

        AsyncTask<Void, Void, String> asyncTask = new TrackParserTask(track);

        // start task
        asyncTask.execute();

        // wait for task code
        Robolectric.runBackgroundTasks();

        // can run asserts on result now
        Assert.assertTrue("String should be: " + expectedString + " but was: " + asyncTask.get(),
                asyncTask.get().equalsIgnoreCase(expectedString));
    }

    @Test
    public void emptyTrackParserAsyncTaskExecutionTest()
            throws InterruptedException, ExecutionException {

        AsyncTask<Void, Void, String> asyncTask = new TrackParserTask(
                emptyTrack);

        // start task
        asyncTask.execute();

        // wait for task code
        Robolectric.runBackgroundTasks();

        // can run asserts on result now
        Assert.assertTrue("Should be null, was: " + asyncTask.get(),
                asyncTask.get() == null);
    }

    @Test
    public void parseTrackMethodTest() {
        String i = new TrackParserTask(track).parseTrack(track);
        Assert.assertTrue("String should be: " + expectedString + " but was: " + i, i.equalsIgnoreCase(expectedString));
    }

    @Test
    public void parseEmptyTrackMethodTest() {
        String i = new TrackParserTask(emptyTrack).parseTrack(emptyTrack);
        Assert.assertTrue("Should be 0, was: " + i, i == null);
    }

    // method to create example track
    private Track setUpTrack() {
        Track track = new Track();

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
