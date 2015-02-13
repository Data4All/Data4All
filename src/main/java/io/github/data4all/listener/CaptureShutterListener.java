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
package io.github.data4all.listener;

import io.github.data4all.activity.CameraActivity;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.logger.Log;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * A Listener for the camera trigger.
 * 
 * @author sbollen
 *
 */
public class CaptureShutterListener implements OnClickListener {

    // Camera Object
    private Camera camera;

    private ShutterCallback shutterCallback = new ShutterCallback() {

        public void onShutter() {
            Log.i(getClass().getSimpleName(),
                    "make a sound to notify the picture taken.");
        }
    };

    private PictureCallback pictureCallback = new PictureCallback() {

        public void onPictureTaken(byte[] raw, Camera camera) {
            Log.i(getClass().getSimpleName(), "compress jpeg");
        }
    };

    /**
     * constructor.
     * 
     * @param c
     *            the used camera
     */
    public CaptureShutterListener(Camera c) {
        this.camera = c;
    }

    @Override
    public void onClick(View v) {
        // Get the actual context
        final CameraActivity myContext = (CameraActivity) v.getContext();
        // Take a picture with the handled data
        camera.takePicture(shutterCallback, pictureCallback,
                new CapturePictureHandler(myContext));
    }

}
