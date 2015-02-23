/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.listener.ButtonRotationListener;
import io.github.data4all.logger.Log;
import io.github.data4all.service.OrientationListener;
import io.github.data4all.view.AutoFocusCrossHair;
import io.github.data4all.view.CameraPreview;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Activity for constructing the camera.
 * 
 * This activity is used to to create and handle the lifecycle. It produces the
 * layout. checks for the existence of a camera and generates the animations.
 * This activity stands in connection with the classes {@link CameraPreview
 * } and
 * {@link AutoFocusCrossHair}.
 * 
 * @author Andre Koch
 * @CreationDate 09.02.2015
 * @LastUpdate 12.02.2015
 * @version 1.2
 * 
 */
public class CameraActivity extends Activity {

    // Logger Tag
    private static final String TAG = CameraActivity.class.getSimpleName();

    private Camera mCamera;

    private CameraPreview cameraPreview;
    private ImageButton btnCapture;
    private AutoFocusCrossHair mAutoFocusCrossHair;

    private OrientationEventListener listener;
    private ShutterCallback shutterCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate is called");
        super.onCreate(savedInstanceState);

        // Checking camera availability
        if (!this.isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.noCamSupported), Toast.LENGTH_LONG)
                    .show();
            finish();
            Log.d(TAG, "device supports no camera");
            return;
        }

        // remove title and status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.activity_camera);

        // Set the capturing button
        btnCapture = (ImageButton) findViewById(R.id.btnCapture);
        setListener(btnCapture);

        listener = new ButtonRotationListener(this,
                Arrays.asList((View) btnCapture));

        // Set the Focus animation
        mAutoFocusCrossHair = (AutoFocusCrossHair) findViewById(R.id.af_crosshair);

        shutterCallback = new ShutterCallback() {
            public void onShutter() {
                final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(200);
            }
        };
        AbstractActivity.addNavBarMargin(getResources(), btnCapture);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume is called");
        super.onResume();

        this.startService(new Intent(this, OrientationListener.class));

        if (mCamera == null) {
            Log.d(TAG, "camera is null, so we have to recreate");
            this.createCamera();
            cameraPreview.setCamera(mCamera);
            mCamera.startPreview();
        }

        btnCapture.setEnabled(true);
        listener.enable();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause is called");
        super.onPause();

        this.stopService(new Intent(this, OrientationListener.class));

        super.onPause();

        if (mCamera != null) {
            Log.d(TAG, "camera is not null on pause, so release it");
            cameraPreview.setCamera(null);
            this.releaseCamera();
        }

        btnCapture.setEnabled(false);
        listener.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.releaseCamera();
    }

    /* ********************************************************** *
     * ********************************************************** *
     * **********************************************************
     */

    /**
     * Set the camera-action listener to the given image-button.
     * 
     * @param button
     *            The image-button to use.
     * 
     * @author tbrose
     */
    private void setListener(ImageButton button) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // After photo is taken, disable button for clicking twice
                btnCapture.setEnabled(false);
                mCamera.takePicture(shutterCallback, null,
                        new CapturePictureHandler(CameraActivity.this));
            }
        });

        button.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // After photo is taken, disable button for clicking twice
                btnCapture.setEnabled(false);

                mAutoFocusCrossHair.showStart();
                mAutoFocusCrossHair.doAnimation();
                mCamera.autoFocus(new AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            mAutoFocusCrossHair.success();
                            mCamera.takePicture(shutterCallback, null,
                                    new CapturePictureHandler(
                                            CameraActivity.this));
                        } else {
                            mAutoFocusCrossHair.fail();
                            btnCapture.setEnabled(true);
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * This method setup the camera.It calls a method from @CameraPreview and
     * sets all camera parameters.
     */
    private void createCamera() {
        Log.i(TAG, "createCamera is called");

        if (this.isDeviceSupportCamera()) {
            mCamera = this.getCameraInstance();
            cameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
            cameraPreview.setCamera(mCamera);
        } else {
            Log.e(TAG, "Device not support camera");
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

    /**
     * This method release the camera for other applications and set camera to
     * null.
     */
    private void releaseCamera() {
        Log.d(TAG, "release Camera is called");
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Log.d(TAG, "try to get an instance of camera");
        Camera camera = null;

        this.releaseCamera();
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        return camera;
    }
}
