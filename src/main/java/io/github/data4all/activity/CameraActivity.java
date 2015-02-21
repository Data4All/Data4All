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
import io.github.data4all.logger.Log;
import io.github.data4all.view.AutoFocusCrossHair;
import io.github.data4all.view.CameraPreview;
import io.github.data4all.view.CaptureAssistView;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
    private CaptureAssistView mAssistView;

    private OrientationEventListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate is called");
        super.onCreate(savedInstanceState);

        // remove title and status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.activity_camera);

        // Checking camera availability
        Log.d(TAG, "check if device support Camera");
        if (!this.isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.noCamSupported), Toast.LENGTH_LONG)
                    .show();
            finish();
            Log.d(TAG, "device supports no camera");
            return;
        }

        // Set the capturing button
        btnCapture = (ImageButton) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(btnCaptureOnClickListener);

        listener = new OrientationEventListener(this) {
            private int mCurrentOrientation;
            private boolean backRotating;

            @Override
            public void onOrientationChanged(int orientation) {
                int newOrientation = ((orientation + 45) % 360) / 90;
                if (newOrientation != mCurrentOrientation) {
                    Log.d("BLUB", "New orientation: " + newOrientation);
                    rotateButton(mCurrentOrientation, newOrientation);
                    mCurrentOrientation = newOrientation;
                }
            }

            /**
             *
             * @param from
             *            The old device rotation and current button rotation
             * @param to
             *            The new device rotation and
             */
            private void rotateButton(int from, final int to) {
                if (from == 0 && to == 1) {
                    btnCapture.setRotation(359.9f);
                    btnCapture.animate().rotation(270f)
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(500).start();
                } else if (from == 1 && to == 0) {
                    backRotating = true;
                    btnCapture.animate().rotation(359.9f)
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(500).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    btnCapture.setRotation(0f);
                                    backRotating = false;
                                }
                            }).start();

                } else if (backRotating && to == 3) {
                    backRotating = true;
                    btnCapture.animate().rotation(359.9f).setDuration(100)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    btnCapture.setRotation(0f);
                                    backRotating = false;
                                    btnCapture
                                            .animate()
                                            .rotation((3 - ((to + 3) % 4)) * 90)
                                            .setInterpolator(
                                                    new DecelerateInterpolator())
                                            .setDuration(400).start();
                                }
                            }).start();
                } else {
                    btnCapture.animate().rotation((3 - ((to + 3) % 4)) * 90)
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(500).start();
                }
            }
        };

        // Set the Focus animation
        mAutoFocusCrossHair = (AutoFocusCrossHair) findViewById(R.id.af_crosshair);

        mAssistView = (CaptureAssistView) findViewById(R.id.cameraAssistView);
        updateAssistView(0.5);
    }

    private OnClickListener btnCaptureOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutoFocusCrossHair.showStart();
            mAutoFocusCrossHair.doAnimation();

            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                    if (success) {
                        mAutoFocusCrossHair.success();
                        mCamera.takePicture(shutterCallback, null,
                                new CapturePictureHandler(CameraActivity.this));
                    } else {
                        mAutoFocusCrossHair.fail();
                        mCamera.takePicture(shutterCallback, null,
                                new CapturePictureHandler(CameraActivity.this));
                    }
                }
            });
            // After photo is taken, disable button for clicking twice
            btnCapture.setOnClickListener(null);
            btnCapture.setClickable(false);
        }
    };

    /**
     * This method is called when the position of the mobile phones has changed
     * and the assist view must be adapted.
     * 
     * @param precentage
     */
    public void updateAssistView(double precentage) {
        mAssistView.setInvalidRegion(precentage);
        mAssistView.invalidate();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume is called");

        super.onResume();

        if (mCamera == null) {
            Log.d(TAG, "camera is null, so we have to recreate");
            this.createCamera();
            mAutoFocusCrossHair.clear();
        }

        btnCapture.setOnClickListener(btnCaptureOnClickListener);
        btnCapture.setClickable(true);

        listener.enable();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause is called");
        super.onPause();

        if (mCamera != null) {
            Log.d(TAG, "camera is not null on pause, so release it");
            cameraPreview.setCamera(null);
            this.releaseCamera();
            mAutoFocusCrossHair.clear();
        }

        btnCapture.setOnClickListener(null);
        btnCapture.setClickable(false);

        listener.disable();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.releaseCamera();
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

    private ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter is called");
        }
    };

}
