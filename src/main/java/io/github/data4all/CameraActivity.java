package io.github.data4all;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {
    private final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera; // Instance of the camera hardware
    private int cameraId = 0;
    private static final int CAMERA_REQUEST = 100;
    private ImageView imageView;
    private String mCurrentPhotoPath;
    static final String PHOTO_PATH = "PhotoPath";

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     *            is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        // To get the path of the saved image
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(PHOTO_PATH);
        }
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the path
        savedInstanceState.putString(PHOTO_PATH, mCurrentPhotoPath);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Called, when the button 'takePhoto' is clicked.
     * 
     * @param view
     *            The view, that was clicked.
     */
    public void onClick(View view) {
        // Check for a hardware camera
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }
        }
        // new Intent for capturing a picture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            // Set the File, where the picture will be saved
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            Log.d(DEBUG_TAG, "Camera is starting");
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    /**
     * Creates an image file, where the taken picture is saved
     * 
     * @return the created image file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.GERMANY).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */storageDir /* directory */);
        // Save the path of the file
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    /**
     * Called, when the picture is taken and the app returns to the
     * CameraActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(mCurrentPhotoPath);
            imageView.setRotation(90);
            imageView.setImageURI(uri);
        }
    }

    /**
     * Check for a front camera.
     * 
     * @return the id of the front camera, -1 if there is none.
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

}