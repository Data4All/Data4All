package io.github.data4all.slope;

import io.github.data4all.logger.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;

public class ConnectionUtil {

    public static double value;

    public ConnectionUtil() {
        // TODO Auto-generated constructor stub
    }

    public static double getValue(Location loc) {
        final File folder = new File(Environment.getExternalStorageDirectory()
                + "/Data4all");
        File localDir = new File(Environment.getExternalStorageDirectory()
                + "/Data4all");
        Log.i("SrtmConnectionUtil", "" + localDir.getAbsolutePath());

        try {
//            File file = SrtmUtil.downloadSrtm(loc.getLatitude(),
//                    loc.getLongitude(), "Eurasia", folder);
//            Log.i("SrtmConnectionUtil", "file was saved");
//            ElevationAPI api = new SrtmElevationAPI(file);
            ElevationAPI api = new SrtmElevationAPI(localDir);
            value = api.getElevation(loc);
            Log.i("SrtmConnectionUtil", "" + value);
        } catch (IOException e) {
            Log.e("SrtmConnectionUtil", "IOException");
        } catch (Exception e) {
            Log.e("SrtmConnectionUtil", "Exception at getting Elevation");
            e.printStackTrace();
        }
        return value;
    }
}
