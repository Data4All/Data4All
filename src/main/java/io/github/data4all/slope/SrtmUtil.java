/**
 * 
 */
package io.github.data4all.slope;

import io.github.data4all.logger.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

import android.os.AsyncTask;

/**
 * @author sbollen
 *
 */
public class SrtmUtil {

    public static final String EURASIA = "Eurasia";
    public static final String AFRICA = "Africa";
    public static final String ISLANDS = "Islands";
    public static final String NORTH_AMERICA = "North_America";
    public static final String SOUTH_AMERICA = "South_America";
    public static String SRTM3_URL = "http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/";
    
    private final static String TAG = "SrtmUtil";

    /**
     * Download the SRTM file from the dds.cr.usgs.gov to the specified Folder.
     * The downloaded file is returned
     *
     * @param lat
     *            Latitude
     * @param lon
     *            Longitude
     * @param region
     *            The region where the tile is contained
     * @param toFolder
     *            Folder where the file is downloaded
     * @return The downloaded File
     * @throws IllegalStateException
     * @throws IOException
     */
    public void downloadSrtm(double lat, double lon, String region,
            File toFolder) throws IOException {
        String srtmFileName = getSrtmFileName(lat, lon) + ".hgt.zip";
        URL url = new URL(SRTM3_URL + region + "/" + srtmFileName);
        Log.i("SrtmUtil", "URL: " + url.toString());

        final NetworkTask nt = new NetworkTask();
        nt.execute(srtmFileName, url, toFolder);
    }

    /**
     * Gets the InputStream of the given URL
     *
     * @param url
     *            String that represents an URL
     * @return InputStream
     * @throws IllegalStateException
     * @throws IOException
     */
    public static InputStream getInputStream(String urlStr)
            throws IllegalStateException, IOException {

        URL url = new URL(urlStr.replaceAll(" ", "%20"));
        HttpURLConnection urlConnection = (HttpURLConnection) url
                .openConnection();
        Log.i("SrtmNetwork", "connection open");
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        Log.i("SrtmNetwork", "connected");

        return urlConnection.getInputStream();
    }

    /*
     * @Description: An inner Class for downloading the actual srtm data
     */
    private class NetworkTask extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... param) {

            String srtmFileName = (String) param[0];
            URL url = (URL) param[1];
            File toFolder = (File) param[2];

            File srtmFile = new File(toFolder, srtmFileName);
            
            Log.i("SrtmUtil", "Filepath: " + srtmFile.getAbsolutePath());
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getInputStream(url.toString());
                fos = new FileOutputStream(srtmFile);
                final byte[] buf = new byte[1024];

                for (int count = is.read(buf); count != -1; count = is
                        .read(buf)) {
                    fos.write(buf, 0, count);
                }
                Log.i("SrtmUtil", "saved file: " + srtmFile.getAbsolutePath());

            } catch (IllegalStateException e) {
                Log.e(TAG, "" + e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "" + e);
                e.printStackTrace();
            } finally {

                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "" + e);
                    e.printStackTrace();
                }
            }

            return "successful";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("SrtmUtil", "success");
        }

        /**
         * Gets the InputStream of the given URL
         *
         * @param url
         *            String that represents an URL
         * @return InputStream
         * @throws IllegalStateException
         * @throws IOException
         */
        public InputStream getInputStream(String urlStr)
                throws IllegalStateException, IOException {

            URL url = new URL(urlStr.replaceAll(" ", "%20"));
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            Log.i("SrtmNetwork", "connection open");
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            Log.i("SrtmNetwork", "connected");

            return urlConnection.getInputStream();
        }
    }

    /**
     * Return the SRTM file name without the extension
     *
     * @param lat
     *            Latitude
     * @param lon
     *            Longitude
     * @return SRTM filename
     */
    public static String getSrtmFileName(double lat, double lon) {
        int nlat = Math.abs((int) Math.floor(lat));
        int nlon = Math.abs((int) Math.floor(lon));

        NumberFormat nf = NumberFormat.getInstance();
        String NS, WE;
        String f_nlat, f_nlon;

        if (lat > 0) {
            NS = "N";
        } else {
            NS = "S";
        }
        if (lon > 0) {
            WE = "E";
        } else {
            WE = "W";
        }

        nf.setMinimumIntegerDigits(2);
        f_nlat = nf.format(nlat);
        nf.setMinimumIntegerDigits(3);
        f_nlon = nf.format(nlon);

        return NS + f_nlat + WE + f_nlon;
    }

}
