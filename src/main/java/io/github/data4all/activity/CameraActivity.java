package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.util.CameraPreview;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * An Activity for the camera preview and taking a photo
 * 
 * @author: sbollen
 */

public class CameraActivity extends Activity {

	// Logger Tag
	private static final String TAG = "CameraActivity";

	// Camera Preview View
	private CameraPreview mCameraPreview;

	// Camera Preview View
	private CapturePictureHandler mPicture;

	// Camera Object
	private Camera mCamera;

	private FrameLayout preview;

	// Camera action Button
	private Button btnCaptureImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Inflate the UI layout
		setContentView(R.layout.activity_camera);

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(), "@string/noCamSupported",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}

		initUIComponents();

	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * Helper method to access the camera returns null if it cannot get the
	 * camera or does not exist
	 * 
	 * @return
	 */
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera cam = null;
		try {
			cam = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return cam; // returns null if camera is unavailable
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
	protected void onResume() {

		super.onResume();
		try {
			mCamera = Camera.open();
			mCamera = getCameraInstance();
			mCamera.setPreviewCallback(null);
			mCameraPreview = new CameraPreview(this, mCamera);// set preview
			preview.addView(mCameraPreview);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	/*
	 * Initialize the UI Components (trigger and cameraPreview)
	 */
	private void initUIComponents() {

		// Create an instance of Camera
		mCamera = getCameraInstance();

		// initialise a new Preview
		mCameraPreview = new CameraPreview(this, mCamera);

		// create a new Layout
		preview = (FrameLayout) findViewById(R.id.camera_preview);

		// add camera to preview
		preview.addView(mCameraPreview);

		// Retrieve the ImageButton of Camera Trigger
		btnCaptureImage = (Button) findViewById(R.id.btnCaptureImage);
		btnCaptureImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				mCamera.takePicture(null, null, mPicture);
			}
		});

	}

}
