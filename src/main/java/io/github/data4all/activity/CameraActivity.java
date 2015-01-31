package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.view.CameraPreview;
import io.github.data4all.view.CaptureCameraSurfaceView;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This class serves as acitivity for the camera . This class creates the Camera
 * with all associated views and handles the control of the camera in
 * cooperation with the CameraPreview Class
 * 
 * @author: Andre Koch
 */

public class CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private SensorManager sensorManager = null;
	private int deviceHeight;
	private ImageButton btnCapture;

	private OnClickListener btnCaptureOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamera.takePicture(shutterCallback, null,
					new CapturePictureHandler(getApplicationContext()));
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_camera);

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.noCamSupported), Toast.LENGTH_LONG)
					.show();
			// will close the app if the device does't have camera
			finish();
		}

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		deviceHeight = display.getHeight();

		btnCapture = (ImageButton) findViewById(R.id.btnCapture);
		btnCapture.setOnClickListener(btnCaptureOnClickListener);

		// Getting the sensor service.
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		createCamera();
	}

	private void createCamera() {
		// Create an instance of Camera
		if (mCamera == null) {
            mCamera = getCameraInstance();
        }

		// Create our Preview view and set it as the content of our activity.
		if (mPreview == null) {
			mPreview = new CameraPreview(this, mCamera);
		}

		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

		// Calculating the width of the preview so it is proportional.
		float widthFloat = (float) (deviceHeight) * 4 / 3;
		int width = Math.round(widthFloat);

		// Resizing the LinearLayout so we can make a proportional preview. This
		// approach is not 100% perfect because on devices with a really small
		// screen the the image will still be distorted - there is place for
		// improvment.
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				width, deviceHeight);
		preview.setLayoutParams(layoutParams);

		// Adding the camera preview
		preview.addView(mPreview);

	}

	/**
	 * This method looks whether the device has a camera and then returns a
	 * boolean.
	 * 
	 * @return boolean true if device has a camera, false otherwise
	 */
	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Creating the camera
		createCamera();

	}

	@Override
	protected void onPause() {
		

		// release the camera immediately on pause event
		releaseCamera();

		// removing the inserted view - so when we come back to the app we
		// won't have the views on top of each other.
		
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.removeAllViews();
		
		
		if (mCamera != null) {
	        mCamera.setPreviewCallback(null);
	        mPreview.getHolder().removeCallback(mPreview);
	        mCamera.release();
	    }

		super.onPause();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mPreview.setCamera(null);
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	/**
	 * A safe way to get an instance of the Camera object.
	 */
	public static Camera getCameraInstance() {
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
