package io.github.data4all.model.data;

import io.github.data4all.logger.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

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
public class Track implements Parcelable {

    private static final String TAG = "Track";

    // trackName is timestamp of format "yyyy_MM_dd_HH_mm_ss"
    private final String trackName;

    // a list of trackpoints
    private final ArrayList<TrackPoint> tracklist;

    /**
     * Methods to write and restore a Parcel.
     */
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {

        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    /**
     * Constructor to create a Track from a parcel.
     * 
     * @param in
     *            the parcel to read from
     */
    private Track(Parcel in) {
        tracklist = new ArrayList<TrackPoint>();
        in.readTypedList(tracklist, TrackPoint.CREATOR);
        trackName = in.readString();
    }

    /**
     * Constructor to create a Track. Name of the track will be the creation
     * time. "yyyy_MM_dd_HH_mm_ss"
     */
    public Track() {
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

    /**
     * Adds a TrackPoint to the ArrayList of TrackPoints.
     * 
     * @param location
     *            The Location
     */
    public void addTrackPoint(final Location location) {
        if (location != null) {
            tracklist.add(new TrackPoint(location));
            Log.d(TAG, "Added TrackPoint: " + location.toString());
        }
    }

    public List<TrackPoint> getTrackPoints() {
        return new ArrayList<TrackPoint>(tracklist);
    }

    /**
     * Returns the latest trakpoint from this track
     * 
     * @return trackpoint The latest trackpoint in the list
     */
    public TrackPoint getLastTrackPoint() {
        if (tracklist.isEmpty()) {
            return null;
        }
        return tracklist.get(tracklist.size()-1);
    }

    @Override
    public String toString() {
        String str = trackName + '\n';
        for (TrackPoint loc : tracklist) {
            str += loc.toString() + '\n';
        }
        return str;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tracklist);
        dest.writeString(trackName);
    }
}
