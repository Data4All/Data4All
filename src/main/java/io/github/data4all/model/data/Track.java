package io.github.data4all.model.data;

import io.github.data4all.logger.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.xmlpull.v1.XmlSerializer;

import de.blau.android.osm.Track.TrackPoint;
import android.annotation.SuppressLint;
import android.location.Location;

/**
 * @author sbrede
 *
 */
@SuppressLint("SimpleDateFormat")
public class Track {

    private static final String           TAG              = "Track";

    // Filename/TrackName is timestamp of format "yyyy_MM_dd_HH_mm_ss"
    private final String                  trackName;

    private final ArrayList<TrackPoint>   tracklist;

    /**
     * Ensure only one instance may be open at a time
     */
    private static volatile boolean       isOpen           = false;

    /**
     * For conversion from UNIX epoch time and back
     */
    private static final SimpleDateFormat ISO8601FORMAT;
    private static final Calendar         calendarInstance = Calendar
                                                                   .getInstance(TimeZone
                                                                           .getTimeZone("UTC"));
    static {
        // Hardcode 'Z' timezone marker as otherwise '+0000' will be used, which
        // is invalid in GPX
        ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ISO8601FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    /**
     * XML header.
     */
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    
    /**
     * GPX opening tag
     */
    private static final String TAG_GPX = "<gpx"
        + " xmlns=\"http://www.topografix.com/GPX/1/1\""
        + " version=\"1.1\""
        + " creator=\"Data4All - https://data4all.github.io/\""
        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
        + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";
    

    public Track() {
        tracklist = new ArrayList<TrackPoint>();
        trackName = getTimeStamp();
        isOpen = true;
        Log.d(TAG, "New Track with name: " + trackName + " created.");
    }

    public void saveTrack() {
        // TODO parse to xml and save on internal memory
    }

    private String getTimeStamp() {
        return (new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()));
    }

    public void addTrackPoint(final Location location) {
        if (location != null) {
            tracklist.add(new TrackPoint(location));
        }
    }

    public List<TrackPoint> getTrackPoints() {
        // return new ArrayList<TrackPoint>(tracklist);
        return tracklist;
    }

    public boolean isOpen() {
        return isOpen;
    }
    
    @Override
    public String toString() {
        String str = "";
        for (TrackPoint loc : tracklist) {
            str += loc.toString() + '\n';
        }
        return str;
    }

    public class TrackPoint implements GeoPoint {

        public final double latitude;
        public final double longitude;
        public final double altitude;
        public final long   time;

        public TrackPoint(Location original) {
            latitude = original.getLatitude();
            longitude = original.getLongitude();
            altitude = original.hasAltitude() ? original.getAltitude()
                    : Double.NaN;
            time = original.getTime();
        }

        @Override
        public double getLat() {
            return latitude;
        }

        @Override
        public double getLon() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getAlt() {
            return altitude;
        }

        public long getTime() {
            return time;
        }

        public boolean hasAltitude() {
            return !Double.isNaN(altitude);
        }

        public double getAltitude() {
            return !Double.isNaN(altitude) ? altitude : 0d;
        }

        /**
         * Adds a GPX trkpt (track point) tag to the given serializer
         * (synchronized due to use of calendarInstance)
         * 
         * @param serializer
         *            the xml serializer to use for output
         * @throws IOException
         */
        public synchronized void toXml(XmlSerializer serializer)
                throws IOException {
            serializer.startTag(null, "trkpt");
            serializer.attribute(null, "lat",
                    String.format(Locale.US, "%f", latitude));
            serializer.attribute(null, "lon",
                    String.format(Locale.US, "%f", longitude));
            if (hasAltitude()) {
                serializer.startTag(null, "ele")
                        .text(String.format(Locale.US, "%f", altitude))
                        .endTag(null, "ele");
            }
            calendarInstance.setTimeInMillis(time);
            String timestamp = ISO8601FORMAT.format(new Date(time));
            serializer.startTag(null, "time").text(timestamp)
                    .endTag(null, "time");
            serializer.endTag(null, "trkpt");
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "%f, %f", latitude, longitude);
        }

    }

}
