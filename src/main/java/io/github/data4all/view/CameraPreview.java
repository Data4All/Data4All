package io.github.data4all.view;

import io.github.data4all.logger.Log;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * This class serves as Previewclass for the camera . This class creates the
 * preview with all associated views and handles the control of the camera in
 * cooperation with the CameraActivity.
 * 
 * @author: Andre Koch
 */

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

	private final static String TAG = "CameraPreview";

	private static SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private List<Size> mSupportedPreviewSizes;
	private static Camera mCamera;
	private static Context context;
	private Size mPreviewSize;
	private boolean previewIsRunning;

	public CameraPreview(Context context) {
		super(context);

		mSurfaceView = new SurfaceView(context);
		addView(mSurfaceView);

		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);

	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			requestLayout();
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

	private static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		Log.d(TAG, "start to calculate optimal Preview Size");
		final double ASPECT_TOLERANCE = 0.1;
		final double MAX_DOWNSIZE = 1.5;

		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			double downsize = (double) size.width / w;
			if (downsize > MAX_DOWNSIZE) {
				// if the preview is a lot larger than our display surface
				// ignore it
				// reason - on some phones there is not enough heap available to
				// show the larger preview sizes
				continue;
			}
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		// keep the max_downsize requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				double downsize = (double) size.width / w;
				if (downsize > MAX_DOWNSIZE) {
					continue;
				}
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		// everything else failed, just take the closest match
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
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		requestLayout();

		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}

	// safe call to start the preview
	// if this is called in onResume, the surface might not have been created
	// yet
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

	/*
	 * Set the Camera Display Orientation when the Surface is changed
	 */
	public static void setCameraDisplayOrientation() {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(0, info);
		WindowManager winManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int rotation = winManager.getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height
						/ previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);
			}
		}
	}

}