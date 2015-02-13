package io.github.data4all.slope;

import io.github.data4all.logger.Log;

import java.io.File;
import java.io.IOException;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;

public class ConnectionUtil {

    public static double value;
    public SrtmUtil srtmUtil = new SrtmUtil();
    private final static String TAG = "SrtmConnectionUtil";

    public ConnectionUtil() {
        // TODO Auto-generated constructor stub
    }

    public double getValue(Location loc) {
        final File folder = new File(Environment.getExternalStorageDirectory()
                + "/Data4all");

        String filepath = (SrtmUtil.getSrtmFileName(loc.getLatitude(),
                loc.getLongitude()))
                + ".hgt.zip";
        String absFilepath = folder.getAbsolutePath() + "/" + filepath;

        if (!(new File(absFilepath)).exists()) {
            Log.i(TAG, "neccessary file is not downloaded yet");
            try {
                srtmUtil.downloadSrtm(loc.getLatitude(), loc.getLongitude(),
                        "Eurasia", folder);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "File could not be saved " + e);
                e.printStackTrace();
            }
        }

        try {
            final SrtmElevationTask srtmEt = new SrtmElevationTask();
            srtmEt.execute(folder, loc);
        } catch (Exception e) {
            Log.e(TAG, "Exception at getting Elevation");
            e.printStackTrace();
        }
        return value;
    }

    /*
     * @Description: An inner Class for downloading the actual srtm data
     */
    private class SrtmElevationTask extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... param) {

            File folder = (File) param[0];
            Location loc = (Location) param[1];

            try {
                ElevationAPI api = new SrtmElevationAPI(folder);
                value = api.getElevation(loc);
                Log.i(TAG, "value: " + value);
            } catch (Exception e) {
                Log.e(TAG, "Exception at getting Elevation");
                e.printStackTrace();
            }
            return "successful";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "getElevation was successfull");
        }

    }
}
