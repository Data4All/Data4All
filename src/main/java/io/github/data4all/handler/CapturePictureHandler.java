package io.github.data4all.handler;

import io.github.data4all.activity.CameraActivity;

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

        // Create a File Reference of Photo File
        // Image Type is JPEG

        // This will store the file to the internal directory of the app
        // photoFile = new File(context.getFilesDir(),
        // System.currentTimeMillis() + ".JPEG");
        // Log.d(TAG, "Picturepath:" + photoFile);

        // Create a new folder on the internal storage named Data4all
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Data4all");
        if (!folder.exists() && folder.mkdirs()) {
            Toast.makeText(context, "New Folder Created", Toast.LENGTH_SHORT)
                    .show();
        }

        // Save the picture to the folder Data4all in the internal storage
        photoFile = new File(Environment.getExternalStorageDirectory()
                + "/Data4all", System.currentTimeMillis() + ".jpeg");

        final String GPS_LONGITUDE = "gps_longitude";
        final String GPS_LATITUDE = "gps_latitude";
        final String GPS_LONGITUDE_REF = "W";
        final String GPS_LATITUDE_REF = "N";

        // Write GPS tags into the EXIF of the picture
        try {
            exif = new ExifInterface(photoFile.getPath());
            //exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS_LATITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS_LONGITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                    GPS_LONGITUDE_REF); //"N" or "S"
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                    GPS_LATITUDE_REF); //"W" or "O"
            //Set the position and tilt data to the UserComment Tag
            exif.setAttribute("UserComment", "Tilt and position data");
            

            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Start a thread to save the Raw Image in JPEG into SDCard
        new SavePhotoTask().execute(raw);
        
        // Intent intent = new Intent(context, ShowPictureActivity.class);
        // intent.putExtra("file_path", photoFile);
        // context.startActivity(intent);
        
        //Test for returning metadata
        ExifInterface testexif;
        try {
            testexif = new ExifInterface(photoFile.getPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String test = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String test2 = "test";
        Log.i(getClass().getSimpleName(), test);
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

            return "successful";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(getClass().getSimpleName(), "########### onPostExecute");
            if (result.equals("successful")) {
                Toast.makeText(context, "Picture successfully saved",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Failed on taking picture",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}