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
	private CameraPreview mPreview;
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
			Toast.makeText(getApplicationContext(),
					getString(R.string.noCamSupported), Toast.LENGTH_LONG)
					.show();
			// will close the app if the device does't have camera
			finish();
		}

		btnCapture = (ImageButton) findViewById(R.id.btnCapture);
		btnCapture.setOnClickListener(btnCaptureOnClickListener);
				
		createCamera();
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

		// release the camera immediately on pause event
		Log.d(TAG, "release camera");
		releaseCamera();

	}

	private void createCamera() {
		Log.i(TAG, "createCamera is called");
		
		// Create an instance of Camera
		Log.d(TAG, "try to get instance of camera or create new one");
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview =new CameraPreview(this,
				(SurfaceView) findViewById(R.id.surfaceView),mCamera);

		
		mPreview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		
		
		// Calculating the width of the preview so it is proportional.
		float widthFloat = (float) (deviceHeight) * 4 / 3;
		int width = Math.round(widthFloat);

		// Resizing the LinearLayout so we can make a proportional preview. This
		// approach is not 100% perfect because on devices with a really small
		// screen the the image will still be distorted - there is place for
		// improvment.
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				width, deviceHeight);
		mPreview.setLayoutParams(layoutParams);	
		
		((FrameLayout) findViewById(R.id.layout)).addView(mPreview);
		
		Log.d(TAG, "finish create Camera");

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
