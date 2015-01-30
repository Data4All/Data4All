package io.github.data4all.model.data;

import io.github.data4all.logger.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

/**
 * A track is the represenation of a .gpx file. It has a name and a list of
 * trackpoints.
 * 
 * A track is initialized when the GPSservice starts. There should be only one
 * track for a whole GPSservice lifecycle. GPSservice.onCreate() starts a new
 * track. GPSservice.onDestroy() should start the parsing in to a file. This
 * object is parsed to a .gpx file
 * 
 * @author sbrede
 *
 */
@SuppressLint("SimpleDateFormat")
public class Track {

    private static final String         TAG = "Track";

    // trackName is timestamp of format "yyyy_MM_dd_HH_mm_ss"
    private final String                trackName;

    // a list of trackpoints
    private final ArrayList<TrackPoint> tracklist;

    public Context                      context;

    public Track(Context context) {
        context = this.context;
        tracklist = new ArrayList<TrackPoint>();
        trackName = getTimeStamp();
        Log.d(TAG, "New Track with name: " + trackName + " created.");
    }

    public String getTrackName() {
        return trackName;
    }

    private String getTimeStamp() {
        return (new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()));
    }

    public void addTrackPoint(final Location location) {
        if (location != null) {
            tracklist.add(new TrackPoint(location));
            Log.d(TAG, "Added TrackPoint: " + location.toString());
        }
    }

    public List<TrackPoint> getTrackPoints() {
        return new ArrayList<TrackPoint>(tracklist);
    }

    @Override
    public String toString() {
        String str = trackName + '\n';
        for (TrackPoint loc : tracklist) {
            str += loc.toString() + '\n';
        }
        return str;
    }
}
