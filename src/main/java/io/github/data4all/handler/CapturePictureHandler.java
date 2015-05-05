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
package io.github.data4all.handler;

import io.github.data4all.R;
import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.CameraActivity;
import io.github.data4all.activity.ShowPictureActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.util.Gallery;
import io.github.data4all.util.Optimizer;
import io.github.data4all.util.upload.Callback;
import io.github.data4all.view.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * PreviewClass for camera. This class serves as Previewclass for the camera .
 * This class creates the preview with all associated views and handles the
 * control of the camera in cooperation with the {@link CameraActivity}.
 * 
 * @author Andre Koch
 * @author tbrose (rearrangement and garbage-collecting)
 * @CreationDate 09.02.2015
 * @LastUpdate 21.02.2015
 * @version 1.2
 * 
 */

public class CapturePictureHandler implements PictureCallback {
    // The name of the extra info for the filepath in the intent
    public static final String FILE_EXTRA = "file_path";

    // The name of the extra info for the preview size in the intent
    public static final String SIZE_EXTRA = "preview_size";

    // Name of the TransformationParamBean to give to the next activity
    public static final String TRANSFORM_BEAN = "transform_bean";

    private static final String TAG = CapturePictureHandler.class
            .getSimpleName();

    // Actual Activity for the context
    private final AbstractActivity context;

    // The file into which the picture is saved
    private File photoFile;

    // The directory where the pictures are saved into
    private static final String DIRECTORY = "Data4all";

    // The fileformat of the saved picture
    private static final String FILE_FORMAT = ".jpeg";

    public static final String CURRENT_ORIENTATION = "current_orientation";

    private TransformationParamBean transformBean;

    private CameraPreview preview;

    private boolean gallery;

    private Gallery mGallery;

    private Callback<Exception> mGalleryCallback;

    /**
     * Default constructor.
     * 
     * @param context
     *            The Application context
     * @param preview
     *            The Camera Preview
     */
    public CapturePictureHandler(AbstractActivity context, CameraPreview preview) {
        this.context = context;
        this.preview = preview;
        mGallery = new Gallery(context);
    }

    /**
     * 
     * (non-Javadoc).
     * 
     * @see android.hardware.Camera.PictureCallback#onPictureTaken(byte[],
     *      android.hardware.Camera)
     */
    @Override
    public void onPictureTaken(final byte[] raw, Camera camera) {
        Log.d(TAG, "onPictureTaken is called");

        final Camera.Parameters params = camera.getParameters();

        final double horizontalViewAngle =
                Math.toRadians(params.getHorizontalViewAngle());
        final double verticalViewAngle =
                Math.toRadians(params.getVerticalViewAngle());
        final Size pictureSize = params.getPictureSize();
        final Location currentLocation = Optimizer.currentBestLoc();
        transformBean =
                new TransformationParamBean(this.getDeviceHeight(),
                        verticalViewAngle, horizontalViewAngle,
                        pictureSize.width, pictureSize.height, currentLocation);

        // Start a thread to save the Raw Image in JPEG into SDCard
        if (gallery) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mGallery.addImage(raw, transformBean,
                                Optimizer.currentDeviceOrientation(),
                                preview.getViewSize());
                    } catch (IOException e) {
                        mGalleryCallback.callback(e);
                    }
                    mGalleryCallback.callback(null);
                }
            }).start();
        } else {
            new SavePhotoTask(Optimizer.currentDeviceOrientation(),
                    preview.getViewSize()).execute(raw);
        }
    }

    /**
     * Reads the height of the device in condition of the bodyheight from the
     * preferences.
     * 
     * If the preference is empty or not set the default value is stored.
     * 
     * @author tbrose
     * @return The height of the device or {@code 0} if the preference is not
     *         set or empty
     */
    private double getDeviceHeight() {
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        final Resources res = context.getResources();
        final String key = res.getString(R.string.pref_bodyheight_key);
        final String height = prefs.getString(key, null);
        if (TextUtils.isEmpty(height)) {
            final int defaultValue =
                    res.getInteger(R.integer.pref_bodyheight_default);
            // Save the default value
            prefs.edit().putString(key, "" + defaultValue).commit();
            return (defaultValue - 30) / 100.0;
        } else {
            final double bodyHeight = Integer.parseInt(height);
            return (bodyHeight - 30) / 100.0;
        }
    }

    public static File getImageFolder() {
        return new File(Environment.getExternalStorageDirectory(), DIRECTORY);
    }

    private static File createFile() {
        // Create a new folder on the internal storage named Data4all
        final File folder = getImageFolder();
        if (!folder.exists()) {
            folder.mkdirs();
            Log.i(TAG, "Folder was created");
        }

        // Save the file to the folder in the internal storage
        final String name = System.currentTimeMillis() + FILE_FORMAT;
        return new File(folder, name);
    }

    public void setGallery(boolean gallery) {
        this.gallery = gallery;
    }

    /**
     * An inner Class for saving a picture in storage in a thread.
     */
    private class SavePhotoTask extends AsyncTask<byte[], String, String> {

        private DeviceOrientation deviceOrientation;
        private Point viewSize;

        /**
         * Default Constructor for saving photo task.
         * 
         * @param deviceOrientation
         *            the curretn device orientation
         * @param viewSize
         *            The current preview size
         */
        public SavePhotoTask(DeviceOrientation deviceOrientation, Point viewSize) {
            this.deviceOrientation = deviceOrientation;
            this.viewSize = viewSize;
        }

        @Override
        protected String doInBackground(byte[]... photoData) {
            try {
                photoFile = CapturePictureHandler.createFile();

                Log.d(TAG, "Picturepath:" + photoFile.getPath());

                final FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(photoData[0]);
                fos.flush();
                fos.close();

            } catch (IOException ex) {
                Log.e(TAG, "Fail to save picture", ex);
            }

            return "successful";
        }

        @Override
        protected void onPostExecute(String result) {
            if ("successful".equals(result)) {
                Log.d(TAG, "Picture successfully saved");

                ShowPictureActivity.startActivity(context, photoFile,
                        transformBean, deviceOrientation, viewSize, null);

            } else {
                Toast.makeText(context, "Failed on taking picture",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void setGalleryCallback(Callback<Exception> callback) {
        this.mGalleryCallback = callback;
    }
}
