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
package io.github.data4all.view;

import io.github.data4all.logger.Log;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * PreviewClass for camera.
 * 
 * This class serves as Previewclass for the camera . This class creates the
 * preview with all associated views and handles the control of the camera in
 * cooperation with the {@link CameraActivity}.
 * 
 * @author Andre Koch
 * @CreationDate 09.02.2015
 * @LastUpdate 12.02.2015
 * @version 1.1
 * 
 */

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

	private static final String TAG = CameraPreview.class.getSimpleName();

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Size mPreviewSize;
	private Size mPhotoSize;
	private List<Size> mSupportedPreviewSizes;
	private List<Size> mSupportedPictureSizes;
	private List<String> mSupportedFlashModes;
	private List<String> mAutoFocus;
	private Camera.Parameters params;

	public static int containerWidth = 0;
	public static int containerHeight = 0;

	private Context context;

    private int mPreviewWidth;

    private int mPreviewHeight;

	public CameraPreview(Context context) {
		super(context);
		this.init(context);
	}

	/**
	 * 
	 * @param context
	 * @param attributeSet
	 */
	public CameraPreview(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.init(context);

	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context);
	}

	private void init(Context context) {

		final SurfaceView mSurfaceView = new SurfaceView(context);
		addView(mSurfaceView);
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "onMeasure is called");

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

		setMeasuredDimension(width, height);

		if (containerWidth < width || containerHeight < height) {
			containerWidth = width;
			containerHeight = height;
		}

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
					containerWidth, containerHeight);
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout is called");

		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;
            mPreviewWidth = height;
            mPreviewHeight = width;

			child.layout(0, 0, width, height);
		}
	}

	public void setCamera(Camera camera) {
		Log.d(TAG, "setCamera is called");

		mCamera = camera;
		if (mCamera != null) {

			// get a group of supported preview size
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();

			mSupportedPictureSizes = mCamera.getParameters()
					.getSupportedPictureSizes();

			mSupportedFlashModes = mCamera.getParameters()
					.getSupportedFlashModes();

			mAutoFocus = mCamera.getParameters().getSupportedFocusModes();

			requestLayout();
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated is called");
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);

				// retrieve the parameters of cameras
				params = mCamera.getParameters();
				
				Log.d("SIZE", "w: " + mPreviewSize.width + " h: " + mPreviewSize.height);
                Log.d("SIZE", "w: " + mPreviewWidth + " h: " + mPreviewHeight);

				params.setPreviewSize(mPreviewWidth, mPreviewHeight);

				mPhotoSize = getOptimalSize(mSupportedPictureSizes, mPreviewSize.width, mPreviewSize.height);

				params.setPictureSize(mPhotoSize.width, mPhotoSize.height);
				
				params.setRotation(90);

				// set the picture type for taking photo
				params.setPictureFormat(ImageFormat.JPEG);
				params.setJpegQuality(10);
				params.setZoom(0);

				setFocusModes(params);

				setFlashModes(params);

				mCamera.setParameters(params);
			}
		} catch (IOException ex) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", ex);
		}

	}

	private Camera.Parameters setFlashModes(Camera.Parameters params) {
		Log.d(TAG, "setFlashModes is called");

		if (mSupportedFlashModes != null
				&& mSupportedFlashModes
						.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
			params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

		}
		return params;
	}

	private Camera.Parameters setFocusModes(Camera.Parameters params) {
		Log.d(TAG, "setFocusModes is called");

		if (mAutoFocus
				.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		} else if (mAutoFocus.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		return params;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.d(TAG, "surfaceChanged is called");

		mCamera.stopPreview();

		if (mCamera == null) {
			Log.e(TAG, " mCamera is null");
			return;
		}

		this.setCameraDisplayOrientation();

		mCamera.startPreview();

	}

	/*
	 * Set the Camera Display Orientation when the view changed
	 */
	private void setCameraDisplayOrientation() {

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
		default:
			degrees = 0;
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

		params.setRotation(info.orientation);

		mCamera.setParameters(params);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		Log.d(TAG, "surfaceDestroyed is called");

		if (mCamera != null) {
			mCamera.stopPreview();
		}

	}

	/*
	 * @Function: get optimal picture size according to camera view angles
	 */
	private static Size getOptimalSize(List<Size> sizes, int w, int h) {
	    final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		// calculate the ratio of preview display
		double targetRatio = ((double) w) / ((double) h);
		// if no supported preview sizes, return null
		if (sizes == null) {
			return null;
		}
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		// Set target Height based on the
		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = ((double) size.width) / (double) (size.height);

			//Log.d(TAG, "currentSize width:" + size.width);
			//Log.d(TAG, "currentSize heigth:" + size.height);

			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			}
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			Log.d(TAG, "optimalSize is null");
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		//Log.d(TAG, "optimalSize width:" + optimalSize.width);
		//Log.d(TAG, "optimalSize heigth:" + optimalSize.height);

		return optimalSize;
	}

}