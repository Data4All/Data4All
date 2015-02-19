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
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * The View for the Camera Preview. Is a Surface and sets the layout e.g.
 * rotation
 * 
 * @author sbollen
 *
 */
public class CaptureCameraSurfaceView extends ViewGroup implements
        SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private static Camera mCamera;
    private Size mPreviewSize;
    private Size mPhotoSize;
    private static Context context;

    public static final int containerWidth = 0;
    public static final int containerHeight = 0;

    public CaptureCameraSurfaceView(Context context) {
        super(context);
        CaptureCameraSurfaceView.context = context;
        init(context);
    }

    public CaptureCameraSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        CaptureCameraSurfaceView.context = context;
        init(context);

    }

    public CaptureCameraSurfaceView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        CaptureCameraSurfaceView.context = context;
        init(context);
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
                final int scaledChildWidth =
                        previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight =
                        previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width,
                        (height + scaledChildHeight) / 2);
            }

        }
    }

    private void init(Context context) {
        final SurfaceView mSurfaceView = new SurfaceView(context);

        addView(mSurfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }

        mCamera = camera;

        if (mCamera != null) {
            requestLayout();

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }
    }

    /*
     * Set the Camera Display Orientation when the Surface is changed
     */
    public static void setCameraDisplayOrientation() {
        final Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        final WindowManager winManager =
                (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
        final int rotation = winManager.getDefaultDisplay().getRotation();

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
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);

                // Open Camera Instance and assign to the reference

                // retrieve the parameters of cameras
                final Camera.Parameters params = mCamera.getParameters();

                // set the picture size for taking photo
                final List<Size> sizes = params.getSupportedPictureSizes();
                // See which sizes the camera supports and choose one of those
                mPhotoSize = sizes.get(0);
                params.setPictureSize(mPhotoSize.width, mPhotoSize.height);

                // set the picture type for taking photo
                params.setPictureFormat(ImageFormat.JPEG);

            }
        } catch (IOException ex) {
            Log.e(getClass().getSimpleName(),
                    "IOException caused by setPreviewDisplay()", ex);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {

        setCameraDisplayOrientation();
        final Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(width, height);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }
}
