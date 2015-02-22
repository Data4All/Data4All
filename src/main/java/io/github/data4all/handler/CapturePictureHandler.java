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
import io.github.data4all.activity.ShowPictureActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.util.Optimizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * PreviewClass for camera. This class serves as Previewclass for the camera .
 * This class creates the preview with all associated views and handles the
 * control of the camera in cooperation with the {@link CameraActivity}.
 * 
 * @author Andre Koch
 * @CreationDate 09.02.2015
 * @LastUpdate 12.02.2015
 * @version 1.1
 * 
 */

public class CapturePictureHandler implements PictureCallback {

	private static final String TAG = CapturePictureHandler.class
			.getSimpleName();

	// Actual Activity for the context
	private final Context context;

	// The file into which the picture is saved
	private File photoFile;

	// The directory where the pictures are saved into
	private static final String DIRECTORY = "/Data4all";

	// The fileformat of the saved picture
	private static final String FILE_FORMAT = ".jpeg";

	// The name of the extra info for the filepath in the intent for the new
	private static final String FILEPATH = "file_path";

	// Name and object of the DeviceOrientation to give to the next activity
	private static final String DEVICE_ORIENTATION = "current_orientation";

	private DeviceOrientation currentOrientation;

	private static final String SCREEN_ORIENTATION = "SCREEN_ORIENTATION";
	// Name and object of the TransformationParamBean to give to the next

	// activity
	private static final String TRANSFORM_BEAN = "transform_bean";

	private TransformationParamBean transformBean;

	/**
	 * Default constructor.
	 * 
	 * @param context
	 *            The Application context
	 */
	public CapturePictureHandler(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * (non-Javadoc).
	 * 
	 * @see android.hardware.Camera.PictureCallback#onPictureTaken(byte[],
	 *      android.hardware.Camera)
	 */
	@Override
	public void onPictureTaken(byte[] raw, Camera camera) {
		Log.d(TAG, "onPictureTaken is called");

		final Camera.Parameters params = camera.getParameters();

		final double horizontalViewAngle = Math.toRadians(params
				.getHorizontalViewAngle());
		final double verticalViewAngle = Math.toRadians(params
				.getVerticalViewAngle());
		final Size pictureSize = params.getPictureSize();
		final Location currentLocation = Optimizer.currentBestLoc();
		transformBean = new TransformationParamBean(this.getDeviceHeight(),
				horizontalViewAngle, verticalViewAngle, pictureSize.width,
				pictureSize.height, currentLocation);

		currentOrientation = Optimizer.currentDeviceOrientation();

		// Start a thread to save the Raw Image in JPEG into SDCard
		new SavePhotoTask(params).execute(raw);
	}

	/**
	 * Reads the height of the device in condition of the bodyheight from the
	 * preferences.
	 * 
	 * If the preference is empty or not set the default value is stored.
	 * 
	 * @return The height of the device or {@code 0} if the preference is not
	 *         set or empty
	 */
	private double getDeviceHeight() {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		final Resources res = context.getResources();
		final String key = res.getString(R.string.pref_bodyheight_key);
		final String height = prefs.getString(key, null);
		if (TextUtils.isEmpty(height)) {
			final int defaultValue = res
					.getInteger(R.integer.pref_bodyheight_default);
			// Save the default value
			prefs.edit().putString(key, "" + defaultValue).commit();
			return (defaultValue - 20) / 100.0;
		} else {
			final double bodyHeight = Integer.parseInt(height);
			return (bodyHeight - 20) / 100.0;
		}
	}

	/**
	 * @Description: An inner Class for saving a picture in storage in a thread.
	 */
	class SavePhotoTask extends AsyncTask<byte[], String, String> {

		private Camera.Parameters params;

		/**
		 * Default Constructor for saving photo task.
		 * 
		 * @param params
		 *            camera params
		 */
		public SavePhotoTask(Camera.Parameters params) {
			this.params = params;
		}

		@Override
		protected String doInBackground(byte[]... photoData) {
			try {
				photoFile = CapturePictureHandler.this.createFile();

				Log.d(TAG, "Picturepath:" + photoFile.getPath());

				final FileOutputStream fos = new FileOutputStream(
						photoFile.getPath());
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
			if (result.equals("successful")) {
				Log.d(TAG, "Picture successfully saved");

				final Intent intent = new Intent();
				intent.setClass(context, ShowPictureActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(FILEPATH, photoFile.getPath());
				intent.putExtra(DEVICE_ORIENTATION, currentOrientation);
				intent.putExtra(TRANSFORM_BEAN, transformBean);
				intent.putExtra(SCREEN_ORIENTATION, getOrientationForRotation());

				context.startActivity(intent);

			} else {
				Toast.makeText(context, "Failed on taking picture",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public int getOrientationForRotation() {
		final Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(0, info);

		final WindowManager winManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		final int rotation = winManager.getDefaultDisplay().getRotation();

		int requestedOrientation = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			break;
		case Surface.ROTATION_90:
			requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			break;
		case Surface.ROTATION_180:
			requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
			break;
		case Surface.ROTATION_270:
			requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			break;
		default:
			requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			break;
		}

		return requestedOrientation;
	}

	private File createFile() {

		// Create a new folder on the internal storage named Data4all
		final File folder = new File(Environment.getExternalStorageDirectory()
				+ DIRECTORY);
		if (!folder.exists()) {
			Log.i(TAG, "Folder was created");
			folder.mkdir();
			Log.i(TAG, "Folder was created");
		}

		// Save the file to the folder in the internal storage
		return new File(Environment.getExternalStorageDirectory() + DIRECTORY,
				System.currentTimeMillis() + FILE_FORMAT);
	}
}
