package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.view.CameraPreview;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * This class serves as acitivity for the camera . This class creates the Camera
 * with all associated views and handles the control of the camera in
 * cooperation with the CameraPreview Class
 * 
 * @author: Andre Koch
 */

public class CameraActivity extends Activity {

    // Logger Tag
    private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview cameraPreview;
    private int deviceHeight;
    private ImageButton btnCapture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate is called");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_camera);

        // Checking camera availability
        Log.d(TAG, "check if device support Camera");
        if (!isDeviceSupportCamera()) {
            finish();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.noCamSupported), Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "device supports no camera");

        }
        Log.d(TAG, "get here");
        btnCapture = (ImageButton) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(btnCaptureOnClickListener);

    }

    private OnClickListener btnCaptureOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(shutterCallback, null,
                    new CapturePictureHandler(getApplicationContext()));
        }
    };

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume is called");
        super.onResume();

        if (mCamera == null) {
            Log.d(TAG, "camera is null, so we have to create a new one");
            createCamera();
        }

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause is called");
        super.onPause();

        if (mCamera != null) {
            Log.d(TAG, "camera is not null, set everything to null");
            cameraPreview.setCamera(null);
            releaseCamera();
        }

        btnCapture.setOnClickListener(null);
        btnCapture.setClickable(false);

    }

    private void createCamera() {
        Log.i(TAG, "createCamera is called");

        if (isDeviceSupportCamera()) {
            Log.d(TAG, "seems as the device has a camera");
            
            mCamera = getCameraInstance();

            Log.d(TAG, "now set camera");
            // Calculate the camera previews
            if (mCamera == null) {
                Log.d(TAG, "shit,camera is null");
                finish();
            }
            Log.d(TAG, "DEBUG " + mCamera.hashCode());
            cameraPreview.setCamera(mCamera);

            Log.d(TAG, "finish create Camera");
        } else {
            
            Log.e(TAG, "Cant believe");
        }
    }

    /**
     * This method looks whether the device has a camera and then returns a
     * boolean.
     * 
     * @return boolean true if device has a camera, false otherwise
     */
    private boolean isDeviceSupportCamera() {
        Log.d(TAG, "look if device has camera");
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private void releaseCamera() {
        Log.d(TAG, "release Camera is called");
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    
    @Override
    protected void onDestroy(){
        super.onDestroy();
        
        releaseCameraAndPreview();
    }
    
    
    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Log.d(TAG, "get camera instance is called");
        Camera camera = null;
        try {
            releaseCameraAndPreview();
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        } catch (Exception e) {
            Log.e(TAG, "failed to open Camera");
            e.printStackTrace();
        }

        return camera;
    }

    private void releaseCameraAndPreview() {
        cameraPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    
        ShutterCallback shutterCallback = new ShutterCallback() {
            public void onShutter() {
                // Log.d(TAG, "onShutter'd");
            }
        };

    
}
