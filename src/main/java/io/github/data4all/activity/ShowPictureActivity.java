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
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.RedoUndo.UndoRedoListener;
import io.github.data4all.util.PointToCoordsTransformUtil;
import io.github.data4all.view.TouchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Activity to set a ImageView and use the TouchView to draw.<br\>
 * 
 * @author vkochno
 */

public class ShowPictureActivity extends AbstractActivity {

	private static final String TAG = ShowPictureActivity.class.getSimpleName();

	private TouchView touchView;
	private ImageView imageView;
	private Bitmap bitmap;
	private Intent intent;
	private static final String TYPE = "TYPE_DEF";
	private static final int POINT = 1;
	private static final int BUILDING = 3;
	private static final int WAY = 2;
	private static final int AREA = 4;
	private static final String OSM_ELEMENT = "OSM_ELEMENT";
	private int SCREEN_ORIENTATION;
	private ImageButton undo;
	private ImageButton redo;
	private ImageButton ok;

	// the current TransformationBean and device orientation when the picture
	// was taken
	private TransformationParamBean transformBean;
	private DeviceOrientation currentOrientation;

	/**
	 * public standard constructor.
	 */
	public ShowPictureActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_picture);
		imageView = (ImageView) findViewById(R.id.imageView1);
		touchView = (TouchView) findViewById(R.id.touchView1);
		intent = new Intent(this, MapPreviewActivity.class);
		undo = (ImageButton) findViewById(R.id.undobtn);
		undo.setVisibility(View.GONE);
		redo = (ImageButton) findViewById(R.id.redobtn);
		redo.setVisibility(View.GONE);
		ok = (ImageButton) findViewById(R.id.okbtn);
		ok.setVisibility(View.GONE);
		touchView.setUndoRedoListener(new UndoRedoListener() {
			@Override
			public void canUndo(boolean state) {
				undo.setEnabled(state);
				if (state) {
					undo.setVisibility(View.VISIBLE);
				} else {
					undo.setVisibility(View.GONE);
				}
			}

			@Override
			public void canRedo(boolean state) {
				redo.setEnabled(state);
				if (state) {
					redo.setVisibility(View.VISIBLE);
				} else {
					redo.setVisibility(View.GONE);
				}
			}
			@Override
		public void okUseable(boolean state){
			if(state){
				 ok.setVisibility(View.VISIBLE);
			} else{
				ok.setVisibility(View.INVISIBLE);
			}
		}
		});

		if (getIntent().hasExtra("SCREEN_ORIENTATION")) {
			SCREEN_ORIENTATION = getIntent().getExtras().getInt(
					"SCREEN_ORIENTATION");

			Log.i(TAG, "intent deliever :" + SCREEN_ORIENTATION);
			setRequestedOrientation(SCREEN_ORIENTATION);
		}

		if (getIntent().hasExtra("file_path")) {
			this.setBackground((String) getIntent().getExtras()
					.get("file_path"));

		} else {
			Log.e(this.getClass().toString(), "ERROR, no file found in intent");
		}

		if (getIntent().hasExtra("transform_bean")) {
			transformBean = getIntent().getExtras().getParcelable(
					"transform_bean");
		}

		if (getIntent().hasExtra("current_orientation")) {
			currentOrientation = getIntent().getExtras().getParcelable(
					"current_orientation");
		}

		// Set the display size as photo size to get a coordinate system for the
		// drawn points
		transformBean.setPhotoWidth(getBaseContext().getResources()
				.getDisplayMetrics().widthPixels);
		transformBean.setPhotoHeight(getBaseContext().getResources()
				.getDisplayMetrics().heightPixels);

		// set a new PointToCoordsTransformUtil in the touchView which includes
		// the deviceOrientation, current Location, camera angle, photo size and
		// height
		touchView.setTransformUtil(new PointToCoordsTransformUtil(
				transformBean, currentOrientation));
		this.onClickBuilding(null);
	}

	/**
	 * OnClick method to finish the current drawing.
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickOkay(View view) {
		// 0 or Rotation0 if portrait
		// 90 or Rotation1 if home-button to the right
		// 270 or Rotation3 if home-button to the left
		final int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getRotation();

		// create an abstract data element from the given data and pass it to
		// the next
		// activity
		final AbstractDataElement osmElement = touchView.create(rotation);
		intent.putExtra(OSM_ELEMENT, osmElement);
		startActivity(intent);
	}

	/**
	 * Define method to draw a point.<br\>
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickPoint(View view) {
		touchView.clearMotions();
		touchView.setInterpretationType(TouchView.InterpretationType.POINT);
		touchView.invalidate();
		intent.putExtra(TYPE, POINT);
	}

	/**
	 * Define method to draw a way.<br\>
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickPath(View view) {
		touchView.clearMotions();
		touchView.setInterpretationType(TouchView.InterpretationType.WAY);
		touchView.invalidate();
		intent.putExtra(TYPE, WAY);
	}

	/**
	 * Define method to draw a area.<br\>
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickArea(View view) {
		touchView.clearMotions();
		touchView.setInterpretationType(TouchView.InterpretationType.AREA);
		touchView.invalidate();
		intent.putExtra(TYPE, AREA);
	}

	/**
	 * Define method to draw a building.<br\>
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickBuilding(View view) {
		touchView.clearMotions();
		touchView.setInterpretationType(TouchView.InterpretationType.BUILDING);
		touchView.invalidate();
		intent.putExtra(TYPE, BUILDING);
	}

	/**
	 * Method to use the redo function.<br\>
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickRedo(View view) {
		touchView.redo();
		touchView.invalidate();
	}

	/**
	 * Method to use the undo function.<br\>
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickUndo(View view) {
		touchView.undo();
		touchView.invalidate();
	}

	/**
	 * Get a Uri of a Image and set this to local ImageView.<br\>
	 *
	 * @param selectedImage
	 *            Uri for the selected Image
	 */
	private void setBackground(String selectedImage) {
		// try to convert a image to a bitmap
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(),
					Uri.fromFile(new File(selectedImage)));

			if (SCREEN_ORIENTATION != 0) {
				bitmap = loadFromCamera(this,
						Uri.fromFile(new File(selectedImage)));
			}

			// set the imageview bitmap-resource
			imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			imageView.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			Log.e(this.getClass().toString(), "ERROR, no file found" + e);
		} catch (IOException e) {
			Log.e(this.getClass().toString(), "ERROR, file is no image" + e);
		}
	}

	private Bitmap loadFromCamera(Context context, Uri photoUri)
			throws FileNotFoundException, IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		AssetFileDescriptor fileDescriptor = null;
		fileDescriptor = getContentResolver().openAssetFileDescriptor(photoUri,
				"r");

		Bitmap photo = BitmapFactory.decodeFileDescriptor(
				fileDescriptor.getFileDescriptor(), null, options);
		if (photo != null) {			
			int rotation = getRotationFor(context, photoUri);
			photo = scaleAndRotate(photo, rotation);
			return photo;
		}
		return null;
	}

	

	private int getRotationFor(Context context, Uri photoUri) {
		if (SCREEN_ORIENTATION != 0) {
			// clockwise by 90 degrees
			if (SCREEN_ORIENTATION == 1) {
				return 90;
			} else {
				return 180;
			}
		} else {
			return 0;
		}
	}

	
	
	 private Bitmap scaleAndRotate(Bitmap bitmap, int rotation) {
	        DisplayMetrics metrics = getResources().getDisplayMetrics();
	        float scaleX = 1;
	        float scaleY = 1;
	        if(rotation == 90) {
	            scaleX = metrics.heightPixels * 1.0f / bitmap.getWidth();
	            scaleY = metrics.widthPixels * 1.0f / bitmap.getHeight();
	        } else if (rotation == 180) {
	            scaleY = metrics.widthPixels * 1.0f / bitmap.getWidth();
	            scaleX = metrics.heightPixels * 1.0f / bitmap.getHeight();   
	        }
	        
	        Log.d(TAG, "bmpx: " + bitmap.getWidth() + " bmpy: " + bitmap.getHeight());
	        Log.d(TAG, "scrx: " + metrics.widthPixels + " scry: " + metrics.heightPixels);
	        Log.d(TAG, "scalex: " + scaleX + " scaley: " + scaleY);
	        
	        Matrix matrix = new Matrix();
	        matrix.postScale(scaleX, scaleY);
	        matrix.postRotate(rotation);

	        Bitmap scaled =
	                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	                        bitmap.getHeight(), matrix, true);
	        return scaled;
	    }
}
