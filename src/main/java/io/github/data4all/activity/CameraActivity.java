/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.listener.CaptureShutterListener;
import io.github.data4all.logger.Log;
import io.github.data4all.service.OrientationListener;
import io.github.data4all.view.CaptureCameraSurfaceView;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * An Activity for the camera preview and taking a photo
 * 
 * @author: sbollen
 */

public class CameraActivity extends Activity {

    // Camera Preview View
    private CaptureCameraSurfaceView cameraPreview;
    // Camera Object
    private Camera mCamera;
    // Camera Trigger Button View Component
    private ImageButton btnTrigger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Inflate the UI layout
        setContentView(R.layout.activity_camera);
        
        // Initialize the UI components
        initUIComponents();
    }

    /*
     * Called when the Activity is no longer visible at all.
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Open the camera
        try {
            mCamera = Camera.open();
            Log.d(getClass().getSimpleName(), "CameraInstance:" + mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate the camera previews
        cameraPreview.setCamera(mCamera);
        mCamera.startPreview();
        
        // Start the Device Orientation listener
        startService(new Intent(this, OrientationListener.class));

        // Assign the camera trigger listener here, instead of being in
        // onCreated method.
        // we leave the camera initialize here
        btnTrigger.setOnClickListener(new CaptureShutterListener(mCamera));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Release the current camera
        if (mCamera != null) {
            cameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        stopService(new Intent(this, OrientationListener.class));
        
        btnTrigger.setOnClickListener(null);
    }

    /*
     * Set the trigger clickable if necessary
     */
    public void setCameraShutter(boolean flag) {
        if (btnTrigger != null)
            btnTrigger.setClickable(flag);
    }

    /*
     * Initialize the UI Components (trigger and cameraPreview)
     */
    private void initUIComponents() {
        // Retrieve the ImageButton of Camera Trigger
        btnTrigger = (ImageButton) findViewById(R.id.btnTrigger);

        // Retrieve the Camera Preview Component
        cameraPreview = (CaptureCameraSurfaceView) findViewById(R.id.cameraPreview);
    }
    
    
}
