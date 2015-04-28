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
import io.github.data4all.SwipeListManager;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.listener.ButtonRotationListener;
import io.github.data4all.logger.Log;
import io.github.data4all.service.OrientationListener;
import io.github.data4all.service.OrientationListener.HorizonListener;
import io.github.data4all.service.OrientationListener.LocalBinder;
import io.github.data4all.util.upload.Callback;
import io.github.data4all.view.AutoFocusCrossHair;
import io.github.data4all.view.CameraPreview;
import io.github.data4all.view.CaptureAssistView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
 * @author tbrose
 * @CreationDate 09.02.2015
 * @LastUpdate 08.04.2015
 * @version 1.3
 * 
 * @author Richard Rohde(Kalibration Feedback)
 */

public class CameraActivity extends AbstractActivity {

    /**
     * The duration of the vibration after the image is taken.
     */
    private static final int VIBRATION_DURATION = 200;

    /**
     * The minimum velocity for a swipe to change the mode.
     */
    private static final int MIN_SWIPE_VELOCITY = 1000;

    // Logger Tag
    private static final String TAG = CameraActivity.class.getSimpleName();

    OrientationListener orientationListener;
    boolean orientationBound;

    public static final String FINISH_TO_CAMERA = "io.github.data4all.activity.CameraActivity:FINISH_TO_CAMERA";

    /**
     * Indicates the single picture mode
     */
    private static final int MODE_SINGLE = 0;

    /**
     * Indicates the gallery mode
     */
    private static final int MODE_GALLERY = 1;

    private Camera mCamera;

    private CameraPreview cameraPreview;
    private ImageButton btnCapture;
    private AutoFocusCrossHair mAutoFocusCrossHair;

    private OrientationEventListener listener;
    private ShutterCallback shutterCallback;

    private CaptureAssistView cameraAssistView;

    private ImageButton btnCStatus;
    // runs without a timer by reposting this handler at the end of the runnable
    private boolean setUpComplete = false;
    long startTime = 0;

    // Broadcast receiver for receiving status updates from the IntentService
    private class CalibrationReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private CalibrationReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to
        // receive

        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(OrientationListener.INTENT_CAMERA_UPDATE)) {
                updateCalibrationStatus();
            }
        }
    }

    private int currentMappingMode;

    private CapturePictureHandler pictureHandler;

    private GestureDetector mDetector;

    private SwipeListManager mSwipeListManager;

    private View mCallbackView;

    private boolean ignore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate is called");

        // setting ignore warnings
        ignore = false;
        // remove title and status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        
        // It is important to call the super method after the window-features
        // are requested
        super.onCreate(savedInstanceState);

        shutterCallback = new ShutterCallback() {
            public void onShutter() {
                final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(VIBRATION_DURATION);
            }
        };
        mDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                            float x, float y) {
                        if (x > MIN_SWIPE_VELOCITY) {
                            CameraActivity.this
                                    .switchMode(currentMappingMode - 1);
                            return true;
                        } else if (x < -MIN_SWIPE_VELOCITY) {
                            CameraActivity.this
                                    .switchMode(currentMappingMode + 1);
                            return true;
                        }
                        return false;
                    }
                });

        // The filter's action is BROADCAST_CAMERA
        IntentFilter mStatusIntentFilter = new IntentFilter(
                OrientationListener.BROADCAST_CAMERA);
        // Instantiates a new DownloadStateReceiver
        CalibrationReceiver mCalibrationReceiver = new CalibrationReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mCalibrationReceiver, mStatusIntentFilter);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    /**
     * Setup the layout and find the views.
     */
    private void setLayout() {
        setContentView(R.layout.activity_camera);

        final List<View> buttons = new ArrayList<View>();
        // Set the capturing button
        btnCapture = (ImageButton) findViewById(R.id.btnCapture);
        buttons.add(btnCapture);
        btnCStatus = (ImageButton) findViewById(R.id.calibrationStatus);
        buttons.add(btnCStatus);
        listener = new ButtonRotationListener(this, buttons);

        cameraAssistView =
                (CaptureAssistView) findViewById(R.id.cameraAssistView);

        // Set the Focus animation
        mAutoFocusCrossHair =
                (AutoFocusCrossHair) findViewById(R.id.af_crosshair);
        AbstractActivity.addNavBarMargin(getResources(), btnCapture);
        AbstractActivity.addNavBarMargin(getResources(), btnCStatus);

        mSwipeListManager = new SwipeListManager(this, Arrays.asList(
                R.drawable.ic_cam_single, R.drawable.ic_cam_multi));
        mSwipeListManager.setContent(currentMappingMode);

        mCallbackView = findViewById(R.id.cam_callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setLayout();
        if (this.isDeviceSupportCamera()) {
            try {
                cameraPreview =
                        (CameraPreview) findViewById(R.id.cameraPreview);

                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                cameraPreview.setCamera(mCamera);
                mCamera.startPreview();
                this.setListener(btnCapture);
            } catch (RuntimeException ex) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.noCamSupported), Toast.LENGTH_LONG)
                        .show();
                Log.e(TAG, "device supports no camera", ex);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.noCamSupported), Toast.LENGTH_LONG)
                    .show();
            finish();
            Log.d(TAG, "device supports no camera");
            return;
        }
        listener.enable();
        Intent intent = new Intent(this, OrientationListener.class);
        bindService(intent, orientationListenerConnection,
                Context.BIND_AUTO_CREATE);
        this.startService(intent);
        setUpComplete = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationListener.setHorizonListener(null);
        if (orientationBound) {
            unbindService(orientationListenerConnection);
            orientationBound = false;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            cameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        listener.disable();

        stopService(new Intent(this, OrientationListener.class));
        setUpComplete = false;
    }

    private void updateCalibrationStatus() {
        switch (OrientationListener.CALIBRATION_STATUS) {
        case OrientationListener.CALIBRATION_OK:
            this.btnCStatus.setImageResource(R.drawable.ic_sensorstatus_okay);
            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(3000);
            anim.setStartOffset(3000);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    btnCStatus.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    btnCStatus.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.btnCStatus.startAnimation(anim);
            this.btnCStatus.setClickable(false);
            break;
        case OrientationListener.CALIBRATION_BROKEN_ALL:
            this.btnCStatus.setImageResource(R.drawable.ic_sensorstatus_fail);
            this.btnCStatus.setVisibility(View.VISIBLE);
            this.btnCStatus.setClickable(true);
            break;
        case OrientationListener.CALIBRATION_BROKEN_ACCELEROMETER:
            this.btnCStatus
                    .setImageResource(R.drawable.ic_sensorstatus_warning);
            this.btnCStatus.setVisibility(View.VISIBLE);
            this.btnCStatus.setClickable(true);
            break;
        case OrientationListener.CALIBRATION_BROKEN_MAGNETOMETER:
            this.btnCStatus
                    .setImageResource(R.drawable.ic_sensorstatus_warning);
            this.btnCStatus.setVisibility(View.VISIBLE);
            this.btnCStatus.setClickable(true);
            break;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android
     * .content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        if (data == null || !data.getBooleanExtra(FINISH_TO_CAMERA, false)) {
            finishWorkflow(data);
        }
    }

    /**
     * Changes the mode of the camera to the given id.
     * 
     * @author tbrose
     * @param id
     *            {@code 0} for single, {@code 1} for gallery
     */
    private void switchMode(int id) {
        if (id == MODE_SINGLE) {
            mSwipeListManager.swipeFromLeft();
        } else if (id == MODE_GALLERY) {
            mSwipeListManager.swipeFromRight();
        } else {
            // If the mode is not single and not gallery, break here.
            return;
        }
        pictureHandler.setGallery(id == MODE_GALLERY);
        currentMappingMode = id;
    }

    /* ********************************************************** *
     * ********************************************************** *
     * **********************************************************
     */

    /**
     * Set the camera-action listener to the given image-button.
     * 
     * @author tbrose
     * @param button
     *            The image-button to use.
     */
    private void setListener(ImageButton button) {
        pictureHandler = new CapturePictureHandler(CameraActivity.this,
                cameraPreview);
        pictureHandler.setGallery(currentMappingMode == MODE_GALLERY);
        pictureHandler.setGalleryCallback(new Callback<Exception>() {
            @Override
            public void callback(final Exception e) {
                CameraActivity.this.galleryCallback(e);
            }

            @Override
            public int interval() {
                return 1;
            }
        });
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OrientationListener.CALIBRATION_STATUS == OrientationListener.CALIBRATION_OK
                        || !getWarning() || ignore) {
                    // After photo is taken, disable button for clicking twice
                    btnCapture.setEnabled(false);
                    mCamera.takePicture(shutterCallback, null, pictureHandler);
                } else {
                    showCalibrationDialog();
                }
            }
        });

        button.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (OrientationListener.CALIBRATION_STATUS == OrientationListener.CALIBRATION_OK
                        || !getWarning() || ignore) {
                    // After photo is taken, disable button for clicking twice
                    btnCapture.setEnabled(false);

                    mAutoFocusCrossHair.showStart();
                    mAutoFocusCrossHair.doAnimation();
                    mCamera.autoFocus(new AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                mAutoFocusCrossHair.success();
                                if (cameraAssistView != null
                                        && !cameraAssistView.isSkylook()) {
                                    mCamera.takePicture(shutterCallback, null,
                                            pictureHandler);

                                } else {
                                    mAutoFocusCrossHair.fail();
                                    btnCapture.setEnabled(true);
                                }
                            } else {
                                mAutoFocusCrossHair.fail();
                                btnCapture.setEnabled(true);
                            }
                        }
                    });
                } else {
                    showCalibrationDialog();
                }
                return true;

            }
        });
    }

    /**
     * This method is the callback for the gallery saving action.
     * 
     * @author tbrose
     * @param e
     *            The exception from the gallery or {@code null} if the
     *            opperation succeeds.
     */
    private void galleryCallback(final Exception e) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (e == null) {
                    CameraActivity.this.onGallerySuccess();
                } else {
                    Toast.makeText(CameraActivity.this,
                            e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    btnCapture.setEnabled(true);
                }
            }
        });
    }

    /**
     * This method is the callback for the success-animation of the gallery
     * saving action.
     * 
     * @author tbrose
     */
    protected void onGallerySuccess() {
        mCallbackView.animate().alpha(1).setDuration(VIBRATION_DURATION)
                .setInterpolator(new TimeInterpolator() {
                    private TimeInterpolator ti = new AccelerateDecelerateInterpolator();

                    @Override
                    public float getInterpolation(float input) {
                        return 2f * (0.5f - Math.abs(-ti
                                .getInterpolation(input) + 0.5f));
                    }
                }).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mCallbackView.setVisibility(View.VISIBLE);
                        Log.i(TAG, "starting success animation");
                        mCamera.startPreview();
                    }
                }).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mCallbackView.setVisibility(View.INVISIBLE);
                        mCallbackView.setAlpha(0);
                        btnCapture.setEnabled(true);
                        Log.i(TAG, "ending success animation");
                    }
                }).start();
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
     * Define method to check calibration status.
     * 
     * @param view
     *            current view used this method
     */
    public void onClickCalibrationStatus(View view) {
        showCalibrationDialog();
    }

    /**
     * This method shows an AlertDialog if the phone need recalibration.
     */
    private void showCalibrationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        switch (OrientationListener.CALIBRATION_STATUS) {
        case OrientationListener.CALIBRATION_OK:
            alertDialogBuilder.setTitle(R.string.goodSensorCalibrationTitle);
            break;
        case OrientationListener.CALIBRATION_BROKEN_ALL:
            alertDialogBuilder.setTitle(R.string.badSensorCalibrationTitle);
            break;
        case OrientationListener.CALIBRATION_BROKEN_ACCELEROMETER:
            alertDialogBuilder
                    .setTitle(R.string.badAcceleometerCalibrationTitle);
            break;
        case OrientationListener.CALIBRATION_BROKEN_MAGNETOMETER:
            alertDialogBuilder
                    .setTitle(R.string.badMagnetometerCalibrationTitle);
            break;
        }
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.badSensorCalibration)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setWarning(true);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(R.string.alwaysIgnore,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setWarning(false);
                                dialog.cancel();
                            }
                        })
                .setNeutralButton(R.string.ignore,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ignore = true;
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private boolean getWarning() {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        return (prefs.getBoolean("sensor_warning", true));
    }

    private void setWarning(boolean show) {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("sensor_warning", show).commit();
    }

    /**
     * This method update the Camera View Assist for drawing the horizontal line
     * on sensor change.
     */
    public void updateCameraAssistView() {

        if (orientationListener != null) {
            final Camera.Parameters params = mCamera.getParameters();
            final float horizontalViewAngle =
                    (float) Math.toRadians(params.getVerticalViewAngle());
            final float verticalViewAngle =
                    (float) Math.toRadians(params.getHorizontalViewAngle());
            cameraAssistView.setInformations(horizontalViewAngle,
                    verticalViewAngle,
                    orientationListener.getDeviceOrientation());
            cameraAssistView.invalidate();
            // disable the camerabutton when the camera looks to the sky
            if (!cameraAssistView.isSkylook()) {
                btnCapture.setVisibility(View.VISIBLE);
            } else {
                btnCapture.setVisibility(View.GONE);
            }
        }

    }

    /** Defines callbacks for the orientation service, passed to bindService() */
    private ServiceConnection orientationListenerConnection =
            new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className,
                        IBinder service) {

                    // LocalService instance
                    LocalBinder binder = (LocalBinder) service;
                    orientationListener = binder.getService();
                    orientationBound = true;
                    HorizonListener horizonListener =
                            new OrientationListener.HorizonListener() {

                                @Override
                                public void makeHorizon(boolean state) {
                                    updateCameraAssistView();
                                }

                            };
                    orientationListener.setHorizonListener(horizonListener);
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                    orientationBound = false;
                }
            };

}
