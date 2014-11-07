package io.github.data4all;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MakePhotoActivity extends Activity {
    private final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;
    private static final int CAMERA_REQUEST = 100;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makephoto);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
    }

    public void onClick(View view) {

        // do we have a camera?
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

        // create Intent to take a picture and return control to the calling
        // application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode != RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);
        }
    }

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
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

}