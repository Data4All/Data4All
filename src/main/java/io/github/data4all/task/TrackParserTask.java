package io.github.data4all.task;

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * AsyncTask to save a Track to the internal memory Opens file and a
 * FileInputStream and writes as xml to this file.
 * 
 * @author sbrede
 *
 */
@SuppressLint("SimpleDateFormat")
public class TrackParserTask extends AsyncTask<Void, Void, Integer> {

    private static final String TAG = "TrackParserTask";

    private Context context;
    private Track track;

    /**
     * For conversion from UNIX epoch time and back
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
     * GPX opening tag
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
     * @param context
     *            The context the task is started from
     * @param track
     *            The track which should be parsed
     */
    public TrackParserTask(Context context, Track track) {
        this.context = context;
        this.track = track;
    }

    /*
     * Method for reading a track from memory. Return a string representation of
     * a saved track.
     * 
     * @param context The context of the application
     * 
     * @param track A track
     * 
     * @return str A track as string
     */
    private String readData(Context context, Track track) {
        FileInputStream input = null;
        int content;
        String str = "";
        try {
            // Returns a FileInputStream of file
            input = context.openFileInput(track.getTrackName() + ".gpx");

            while ((content = input.read()) != -1) {
                // convert to char and display it
                str += (char) content;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Error while reading data from file!");
            e.printStackTrace();
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ex) {
                Log.e(TAG, "Error while closing FileInputStream!");
                ex.printStackTrace();
            }
        }
        return str;
    }

    private boolean checkOutputFile(Context context, Track track) {
        String file = readData(context, track);
        String end = file.substring(file.lastIndexOf("\n"));
        if (end == "</gpx>") {
            return true;
        }
        return false;
    }
    
    private void appendEndToFile() {
        String filename = track.getTrackName() + ".gpx";        
        try {
            FileOutputStream file = context.openFileOutput(filename, Context.MODE_PRIVATE);
            
            PrintWriter writer = new PrintWriter(file);
            String tmp = readData(context, track);
            writer.print(tmp);
            writer.print("\t\t</trkseg>\n");
            writer.print("\t</trk>\n");
            writer.print("</gpx>");
            
            writer.flush();
            Log.i("TrackParser", "Data is flushed to :" + filename);
            writer.close();
            Log.i("TrackParser", "Writer is closed");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException in appenEndToFile()");
            e.printStackTrace();
        }
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    /*
     * Method to parse a Track to xml and save it as .gpx
     * 
     * @param Context context
     * 
     * @param Track the track to parse
     * 
     * @return -1 if something bad happens, 0 if track contains no trackpoints,
     * 1 else
     */
    public int parseTrack(Context context, Track track) {
        if (track.getTrackPoints().isEmpty()) {
            Log.i(TAG, "No need to save anything. Empty Track.");
            return 0;
        }
        try {
            // yyyy_mm_dd_hh_mm_ss.gpx
            String filename = track.getTrackName() + ".gpx";

            // List of points contained by this track
            List<TrackPoint> points = track.getTrackPoints();

            // Save file in application package
            FileOutputStream file = context.openFileOutput(filename,
                    Context.MODE_PRIVATE);

            // Use PrintWriter is easier, so you can directly pass strings
            PrintWriter writer = new PrintWriter(file);

            writer.println(XML_HEADER);
            writer.println(OPENING_TAG);
            writer.println("\t<trk>");
            writer.println("\t<name> " + filename + " </name>");
            writer.println("\t\t<trkseg>");

            Log.d(TAG, "vor for");
            for (TrackPoint trackPoint : points) {
                writer.print("\t\t\t<trkpt lat=\"");
                writer.print(trackPoint.getLat());
                writer.print("\" lon=\"");
                writer.print(trackPoint.getLon());
                writer.print("\">\n");
                if (trackPoint.hasAltitude()) {
                    writer.print("\t\t\t\t<elem>");
                    writer.print(trackPoint.getAlt());
                    writer.print("</elem>\n");
                }
                writer.print("\t\t\t\t<time>");
                writer.print(ISO8601FORMAT.format(new Date(trackPoint.getTime())));
                writer.print("</time>\n");
                writer.print("\t\t\t</trkpt>\n");
            }
            writer.println("\t\t</trkseg>");
            writer.println("\t</trk>");
            writer.println("</gpx>");

            writer.flush();
            Log.i("TrackParser", "Data is flushed to :" + filename);
            writer.close();
            Log.i("TrackParser", "Writer is closed");
            return 1;

        } catch (IOException e) {
            Log.e("TrackParser", "Problem while writing the track");
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return parseTrack(context, track);
    }

    // TODO not sure if this makes sense. depends on where the task is called
    @Override
    protected void onPostExecute(Integer result) {
        Log.d(TAG, "onPostExecute()");
        super.onPostExecute(result);
        switch (result) {
        case -1:
            // TODO localization
            Toast.makeText(context.getApplicationContext(),
                    "Could not save the track", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Could not save the track");
            break;
        case 0:
            // TODO localization
            Toast.makeText(context.getApplicationContext(),
                    "Did not save track. Empty Track.", Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG, "Did not save track. Empty Track.");
            break;
        case 1:
            if (checkOutputFile(context, track)) {
                // TODO localization
                Toast.makeText(context.getApplicationContext(),
                        "Saved a track", Toast.LENGTH_SHORT).show();
                Log.d(TAG, readData(context, track));
            } else {
                appendEndToFile();
                Log.e(TAG, "Saved incorrect Track. Incomplete file. Try again");
            }
            break;

        default:
            break;
        }
    }
}