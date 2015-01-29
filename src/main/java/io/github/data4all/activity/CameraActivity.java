package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.util.CameraPreview;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
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

	private static final String TAG = "CamTestActivity";
	CameraPreview preview;
	ImageButton buttonClick;
	Camera camera;
	Activity act;
	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Start to create Camera @ CameraActivity");
		super.onCreate(savedInstanceState);
		ctx = this;
		act = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_camera);

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.noCamSupported), Toast.LENGTH_LONG)
					.show();
			// will close the app if the device does't have camera
			finish();
		}

		preview = new CameraPreview(this,
				(SurfaceView) findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);

		// Autofocus Message
		Toast.makeText(ctx, getString(R.string.CamWithAutoFocus),
				Toast.LENGTH_LONG).show();

		buttonClick = (ImageButton) findViewById(R.id.btnCaptureImage);

		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				camera.takePicture(shutterCallback, null,
						new CapturePictureHandler(getApplicationContext()));
			}
		});

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

	/**
	 * Helper method to access the camera returns null if it cannot get the
	 * camera or does not exist
	 * 
	 * @return
	 */
	private Camera getCameraInstance() {
		Camera camera = null;
		int numCams = Camera.getNumberOfCameras();
		if (numCams > 0) {
			try {
				camera = Camera.open(0);
			} catch (Exception e) {
				// cannot get camera or does not exist
			}
		}
		return camera;
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			camera = getCameraInstance();
			camera.startPreview();
			preview.setCamera(camera);
		} catch (RuntimeException ex) {
			Toast.makeText(ctx, getString(R.string.noCamSupported),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

}
