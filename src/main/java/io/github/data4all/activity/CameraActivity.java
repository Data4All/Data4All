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
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
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
	private CameraPreview preview;
	private int deviceHeight;
	private ImageButton btnCapture;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate is called");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_camera);

		// Checking camera availability
		Log.d(TAG, "check if device support Camera");
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.noCamSupported), Toast.LENGTH_LONG)
					.show();
			// will close the app if the device does't have camera
			finish();
		}

		
		preview = new CameraPreview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		 
		btnCapture = (ImageButton) findViewById(R.id.btnCapture);
		btnCapture.setOnClickListener(btnCaptureOnClickListener);

		Log.d(TAG,"ready with onCreate");
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
			mCamera = getCameraInstance();
			preview.setCamera(mCamera);
		}

	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause is called");
		super.onPause();

		// release the camera immediately on pause event
		Log.d(TAG, "release camera");

        if (mCamera != null) {
            preview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }

	}

	

	/**
	 * This method looks whether the device has a camera and then returns a
	 * boolean.
	 * 
	 * @return boolean true if device has a camera, false otherwise
	 */
	
	private boolean isDeviceSupportCamera() {
		Log.d(TAG,"look if device has camera");
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			return false;
		}
	}

	private void releaseCamera() {
		Log.d(TAG,"release Camera is called");
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	/**
	 * A safe way to get an instance of the Camera object.
	 */
	public static Camera getCameraInstance() {
		Log.d(TAG,"get camera instance is called");
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {

		}
		return camera;
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

}
