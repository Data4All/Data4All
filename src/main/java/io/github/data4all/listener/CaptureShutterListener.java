package io.github.data4all.listener;

import io.github.data4all.activity.CameraActivity;
import io.github.data4all.handler.CapturePictureHandler;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import io.github.data4all.logger.Log;
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

    public CaptureShutterListener(Camera c) {
        this.camera = c;
    }

    public void onClick(View v) {
        // Get the actual context
        CameraActivity myContext = (CameraActivity) v.getContext();
        // Take a picture with the handled data
        camera.takePicture(shutterCallback, pictureCallback,
                new CapturePictureHandler(myContext));
    }

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

}
