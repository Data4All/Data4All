package io.github.data4all.view;

import io.github.data4all.logger.Log;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class serves as Previewclass for the camera . This class creates the
 * preview with all associated views and handles the control of the camera in
 * cooperation with the CameraActivity.
 * 
 * @author: Andre Koch
 */

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private final String TAG = "CameraPreview";
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private boolean previewIsRunning;

	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		this.mCamera = camera;
		this.mHolder = this.getHolder();
		this.mHolder.addCallback(this);
		this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			requestLayout();

			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();

			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				// set Camera parameters
				mCamera.setParameters(params);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
					height);
		}
	}


	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			this.mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}

	}
	
	
	
	

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// make any resize, rotate or reformatting changes here

		// start preview with new settings
		try {
			if (mCamera != null) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
				requestLayout();

				mCamera.setParameters(parameters);
				mCamera.startPreview();
			}

		} catch (Exception e) {
			Log.d("DG_DEBUG",
					"Error starting camera preview: " + e.getMessage());
		}
	}
	
	
	
	public void surfaceDestroyed(SurfaceHolder holder) {
        myStopPreview();
        mCamera.release();
        mCamera = null;
    }
	
	
	// safe call to start the preview
	   // if this is called in onResume, the surface might not have been created yet
	   // so check that the camera has been set up too.
	   public void myStartPreview() {
	       if (!previewIsRunning && (mCamera != null)) {
	           mCamera.startPreview();
	           previewIsRunning = true;
	       }
	   }

	   // same for stopping the preview
	   public void myStopPreview() {
	       if (previewIsRunning && (mCamera != null)) {
	           mCamera.stopPreview();
	           previewIsRunning = false;
	       }
	   }

}