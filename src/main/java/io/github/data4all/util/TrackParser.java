package io.github.data4all.util;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("SimpleDateFormat")
public class TrackParser {

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
    private static final String           XML_HEADER       = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

    /**
     * GPX opening tag
     */
    private static final String           OPENING_TAG      = "<gpx"
                                                                   + '\n'
                                                                   + " xmlns=\"http://www.topografix.com/GPX/1/1\""
                                                                   + '\n'
                                                                   + " version=\"1.1\""
                                                                   + '\n'
                                                                   + " creator=\"Data4All - https://data4all.github.io/\""
                                                                   + '\n'
                                                                   + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                                                                   + '\n'
                                                                   + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">"
                                                                   + '\n';

    public void parseTrack(Context context, Track track) {
        Log.d("FICK DIE UNI", "Massage ist cool"); //TODO wegmachen
        try {
            String filename = track.getTrackName();
            

            //PrintWriter writer = new PrintWriter(new BufferedWriter(
            //        new FileWriter(new File(context.getFilesDir() ,filename))));
            Log.d("TrackParser", context.toString());
            Log.d("TrackParser", filename.toString());
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(XML_HEADER.getBytes());
            outputStream.write(OPENING_TAG.getBytes());
            outputStream.flush();
            outputStream.close();
            
//            List<TrackPoint> points = track.getTrackPoints();
//
//            writer.print(XML_HEADER);
//            writer.print(OPENING_TAG);
//            writer.println("<trk>");
//            writer.println("<name> " + filename + " </name>");
//            writer.println("<trkseg>");
//
//            for (TrackPoint trackPoint : points) {
//                writer.print("<trkpt lat=\" ");
//                writer.print(trackPoint.getLat());
//                writer.print("\" lon=\"");
//                writer.print(trackPoint.getLon());
//                writer.print("\">\n");
//                if (trackPoint.hasAltitude()) {
//                    writer.print("<elem>");
//                    writer.print(trackPoint.getAlt());
//                    writer.print("</elem");
//                }
//                writer.print("<time>");
//                // TODO check if UTC an GMT is the same
//                writer.print(ISO8601FORMAT.format(new Date(trackPoint.getTime())));
//                writer.print("</time>");
//                writer.print("</trkpt>\n");
//            }
//            writer.println("</trkseg>");
//            writer.println("</trk>");
//            writer.println("</gpx>");
//
//            writer.flush();
//            Log.i("TrackParser", "Data is flushed to :" + filename);
//            writer.close();
//            Log.i("TrackParser", "Writer is closed");

            //return filename;
        } catch (IOException e) {
            Log.e("TrackParser", "Problem while writing the track");
            e.printStackTrace();
        }
        //return null;
    }

}
