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

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

/**
 * AsyncTask to parse a {@link Track} into xml structure. Returns the
 * {@link Track} as a string.
 * 
 * @author sbrede
 * @author fkirchge
 *
 */
@SuppressLint("SimpleDateFormat")
public class TrackParserTask extends AsyncTask<Void, Void, String> {

    /**
     * Log Tag.
     */
    private static final String TAG = "TrackParserTask";

    /**
     * The {@link Track} which should be parsed.
     */
    private Track track;

    /**
     * For conversion from UNIX epoch time and back.
     */
    private static final SimpleDateFormat ISO8601FORMAT;

    static {
        // Hardcode 'Z' timezone marker as otherwise '+0000' will be used, which
        // is invalid in GPX
        ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ISO8601FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * XML header.
     */
    private static final String XML_HEADER = "<?xml "
            + "version=\"1.0\" encoding=\"UTF-8\" ?>";

    /**
     * GPX opening tag.
     */
    private static final String OPENING_TAG = "<gpx "
            + "xmlns=\"http://www.topografix.com/GPX/1/1\" "
            + "version=\"1.1\" "
            + "creator=\"Data4All - https://data4all.github.io/\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 "
            + "http://www.topografix.com/GPX/1/1/gpx.xsd \">";

    /**
     * Constructor for this Task.
     * 
     * @param track
     *            The track which should be parsed
     */
    public TrackParserTask(Track track) {
        this.track = track;
    }

    /**
     * This method parses the {@link Track} into a xml string.
     *
     * @param track
     *            The {@link Track} to parse.
     * @return The xml parsed {@link Track} as string.
     */
    public String parseTrack(Track track) {
        if (track.getTrackPoints().isEmpty()) {
            Log.i(TAG, "No need to save anything. Empty Track.");
            return null;
        }

        final StringBuilder sb = new StringBuilder();

        // List of points contained by this track
        final List<TrackPoint> points = track.getTrackPoints();

        sb.append(XML_HEADER);
        sb.append(OPENING_TAG);
        sb.append("<trk>");
        sb.append("<name> " + track.getTrackName() + " </name>");
        sb.append("<trkseg>");

        for (TrackPoint trackPoint : points) {
            sb.append("<trkpt lat=\"");
            sb.append(trackPoint.getLat());
            sb.append("\" lon=\"");
            sb.append(trackPoint.getLon());
            sb.append("\">");
            if (trackPoint.hasAltitude()) {
                sb.append("<elem>");
                sb.append(trackPoint.getAlt());
                sb.append("</elem>");
            }
            sb.append("<time>");
            sb.append(ISO8601FORMAT.format(new Date(trackPoint.getTime())));
            sb.append("</time>");
            sb.append("</trkpt>");
        }
        sb.append("</trkseg>");
        sb.append("</trk>");
        sb.append("</gpx>");

        Log.i(TAG, "GPS Track contains " + points.size() + " Trackpoints.");
        Log.d(TAG,
                "Parsed GPS Track with ID " + track.getID() + " : "
                        + sb.toString());
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
     */
    @Override
    protected String doInBackground(Void... params) {
        return this.parseTrack(track);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute()");
        super.onPostExecute(result);
    }
}
