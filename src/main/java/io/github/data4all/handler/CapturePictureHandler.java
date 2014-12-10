package io.github.data4all.handler;

import io.github.data4all.activity.CameraActivity;
import io.github.data4all.activity.ShowPictureActivity;
import io.github.data4all.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Create the file to the taken picture with additional data like GPS and save
 * this file.
 * 
 * @author sbollen
 *
 */
public class CapturePictureHandler implements PictureCallback {

    private CameraActivity context;
    private File photoFile;
    private ExifInterface exif;

    private String directory = "/Data4all";
    private String fileformat = ".jpeg";
    private String filepath = "file_path";

    public CapturePictureHandler() {
    }

    public CapturePictureHandler(CameraActivity context) {
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.hardware.Camera.PictureCallback#onPictureTaken(byte[],
     * android.hardware.Camera)
     */
    public void onPictureTaken(byte[] raw, Camera camera) {
        Log.d(getClass().getSimpleName(), "Save the Picture");

        // Call the method where the file is created
        createFile();

        // Start a thread to save the Raw Image in JPEG into SDCard
        new SavePhotoTask().execute(raw);

        /* Passes the filepath to the ShowPictureActivity */
        // Intent intent = new Intent(context, ShowPictureActivity.class);
        // intent.putExtra(filepath, photoFile);
        // context.startActivity(intent);
    }

    /*
     * @Description: An inner Class for saving a picture in storage in a thread
     */
    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... photoData) {
            // Delete the picture while detecting existed one
            if (photoFile.exists()) {
                photoFile.delete();
            }
            try {
                Log.d(getClass().getSimpleName(), "Picturepath:" + photoFile);
                // Open file channel
                FileOutputStream fos = new FileOutputStream(photoFile.getPath());
                fos.write(photoData[0]);
                fos.flush();
                fos.close();

            } catch (IOException ex) {
                Log.d(getClass().getSimpleName(), ex.getMessage());
                return ex.getMessage();
            }

            // Call the method where the extra data is written to the exif of
            // the photofile
            writeExifToFile(photoFile.getPath());

            return "successful";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(getClass().getSimpleName(), "########### onPostExecute");
            if (result.equals("successful")) {
                Toast.makeText(context, "Picture successfully saved",
                        Toast.LENGTH_SHORT).show();

                // A method to log the saved metadata of the taken picture TODO
                // remove
                showReturnedMetadata();

            } else {
                Toast.makeText(context, "Failed on taking picture",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Create a directory Data4all if necessary and create a file for the
     * picture
     */
    private void createFile() {
        // Create a File Reference of Photo File
        // Image Type is JPEG

        // TODO decide where to save the files
        // This will store the file to the internal directory of the app
        // photoFile = new File(context.getFilesDir(),
        // System.currentTimeMillis() + fileformat);
        // Log.d(TAG, "Picturepath:" + photoFile);

        // Create a new folder on the internal storage named Data4all
        File folder = new File(Environment.getExternalStorageDirectory()
                + directory);
        if (!folder.exists() && folder.mkdirs()) {
            Toast.makeText(context, "New Folder Created", Toast.LENGTH_SHORT)
                    .show();
        }

        // Save the picture to the folder in the internal storage
        photoFile = new File(Environment.getExternalStorageDirectory()
                + directory, System.currentTimeMillis() + fileformat);
    }

    /*
     * Write the metadata like GPS and tilt and position data to the EXIF of the
     * photofile.
     */
    private void writeExifToFile(String filepath) {
        // Get the location in degrees TODO call real method for geo data
        double latitude = 47.345183;
        double longitude = -117.260489;
        String latitudeRef = latitude < 0.0d ? "S" : "N";
        String longitudeRef = longitude < 0.0d ? "W" : "E";

        try {
            // Write GPS tags into the EXIF of the picture
            exif = new ExifInterface(filepath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                    convertToDMS(latitude));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                    convertToDMS(longitude));
            // Set the Latitude reference (west or east)
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef);
            // Set the Longitude reference (north or south)
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef);

            // Set the position and tilt data to the UserComment Tag
            // TODO call real method for data
            exif.setAttribute("UserComment", "Tilt and position data");

            exif.saveAttributes();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "cannot access exif", e);
        }

    }

    /**
     * convert the given latitude and longitude from degrees into DMS (degree
     * minute second) format. For example -79.948862 becomes
     * 79/1,56/1,55903/1000
     * 
     * @param deg
     *            the given latitude or longitude in degrees
     * @return the given latitude or longitude in DMS
     */
    private String convertToDMS(double deg) {
        StringBuilder sb = new StringBuilder(20);
        deg = Math.abs(deg);
        int degree = (int) deg;
        deg *= 60;
        deg -= (degree * 60.0d);
        int minute = (int) deg;
        deg *= 60;
        deg -= (minute * 60.0d);
        int second = (int) (deg * 1000.0d);

        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
    }

    /**
     * Convert the latitude or latitude from DMS into degrees
     * 
     * @param stringDMS
     *            the given latitude or longitude in DMS
     * @return the given latitude or longitude in degrees
     */
    private Double convertToDegree(String stringDMS) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        // Get the degree value
        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;
        // Get the minute value
        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;
        // Get the second value
        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1].substring(0, stringS[1].length()));
        Double FloatS = S0 / S1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }

    /**
     * Just for developing to show how to get the metadata TODO remove later
     * 
     * An example, how to receive the metadata of the picture Show a log with
     * the returned metadata saved to the picture
     * 
     */
    private void showReturnedMetadata() {

        ExifInterface showexif = null;
        try {
            showexif = new ExifInterface(photoFile.getPath());
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "cannot read exif", e);
        }

        String attrLATITUDE = showexif
                .getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = showexif
                .getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = showexif
                .getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = showexif
                .getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        if (attrLATITUDE == null || attrLONGITUDE == null
                || attrLONGITUDE_REF == null || attrLATITUDE_REF == null) {
            Log.w(getClass().getSimpleName(), "No geotag in the exif of the imagefile");
        } else {
            double latitudeReturn;
            double longitudeReturn;
            if (attrLATITUDE_REF.equals("N")) {
                latitudeReturn = convertToDegree(attrLATITUDE);
            } else {
                latitudeReturn = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                longitudeReturn = convertToDegree(attrLONGITUDE);
            } else {
                longitudeReturn = 0 - convertToDegree(attrLONGITUDE);
            }

            Log.i(getClass().getSimpleName(), exif.getAttribute("UserComment"));

            Log.i(getClass().getSimpleName(), "Latitude: " + latitudeReturn);
            Log.i(getClass().getSimpleName(), "Longitude: " + longitudeReturn);
        }
    }

}