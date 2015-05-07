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
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.listener.ButtonAnimationListener;
import io.github.data4all.listener.ButtonRotationListener;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.RedoUndo.UndoRedoListener;
import io.github.data4all.util.Gallery;
import io.github.data4all.util.PointToCoordsTransformUtil;
import io.github.data4all.view.CaptureAssistView;
import io.github.data4all.view.TouchView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

/**
 * Activity to set a ImageView and use the TouchView to draw.<br\>
 * 
 * @version 1.1
 * 
 * @author vkochno
 * @author tbrose (v 1.1)
 */

public class ShowPictureActivity extends AbstractActivity {

	private static final String TAG = ShowPictureActivity.class.getSimpleName();

	private CaptureAssistView cameraAssistView;

	/** Name of the DeviceOrientation to give to the next activity */
	public static final String CURRENT_ORIENTATION_EXTRA = "current_orientation";

	/** Name of the TransformationParamBean to give to the next activity */
	public static final String TRANSFORM_BEAN_EXTRA = "transform_bean";

	/** The name of the extra info for the preview size in the intent */
	public static final String SIZE_EXTRA = "preview_size";

	/** The name of the extra info for the filepath in the intent */
	public static final String FILE_EXTRA = "file_path";

	private TouchView touchView;
	public static ImageView imageView;
	private Intent intent;
	private static final String TYPE = "TYPE_DEF";
	private static final String LOCATION = "LOCATION";
	private static final int POINT = 1;
	private static final int BUILDING = 3;
	private static final int WAY = 2;
	private static final int AREA = 4;
	private static final int SAMPLE_SIZE = 2;
	private int viewScale = 100;
	private float scale = 1f;
	private Matrix matrix = new Matrix();
	private static final String OSM_ELEMENT = "OSM_ELEMENT";
	private ImageButton undo;
	private ImageButton redo;
	private ImageButton ok;
	private ButtonAnimationListener btnAnimator;

	// the current TransformationBean and device orientation when the picture
	// was taken
	private TransformationParamBean transformBean;
	public static Matrix baseMatrix;
	private DeviceOrientation currentOrientation;

	private ButtonRotationListener listener;

	public static ScaleGestureDetector SGD;

	private List<View> buttons;

	/**
	 * public standard constructor.
	 */
	public ShowPictureActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		baseMatrix = new Matrix();
		baseMatrix.setValues(new float[] { SAMPLE_SIZE, 0, 0, 0, SAMPLE_SIZE,
				0, 0, 0, 1 });

		// remove title and status bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

		// It is important to call the super method after the window-features
		// are requested
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_picture);

		imageView = (ImageView) findViewById(R.id.imageView1);
		cameraAssistView = (CaptureAssistView) findViewById(R.id.cameraAssistView);
		touchView = (TouchView) findViewById(R.id.touchView1);

		intent = new Intent(this, MapPreviewActivity.class);
		undo = (ImageButton) findViewById(R.id.undobtn);
		undo.setVisibility(View.GONE);
		redo = (ImageButton) findViewById(R.id.redobtn);
		redo.setVisibility(View.GONE);
		ok = (ImageButton) findViewById(R.id.okbtn);
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
			public void okUseable(boolean state) {
				if (state) {
					ok.setVisibility(View.VISIBLE);
				} else {
					ok.setVisibility(View.GONE);
				}
			}
		});

		final Bundle extras = getIntent().getExtras();

		if (extras != null
				&& getIntent().hasExtra(CapturePictureHandler.FILE_EXTRA)) {
			this.setBackground((File) getIntent().getSerializableExtra(
					CapturePictureHandler.FILE_EXTRA));

		} else {
			Log.e(TAG, "ERROR, no file found in intent");
			this.finish();
		}

		if (extras != null
				&& getIntent().hasExtra(CapturePictureHandler.TRANSFORM_BEAN)) {
			transformBean = getIntent().getExtras().getParcelable(
					CapturePictureHandler.TRANSFORM_BEAN);
			intent.putExtra(LOCATION, transformBean.getLocation());
		}

		if (extras != null
				&& getIntent().hasExtra(
						CapturePictureHandler.CURRENT_ORIENTATION)) {
			currentOrientation = getIntent().getExtras().getParcelable(
					CapturePictureHandler.CURRENT_ORIENTATION);
		}

		if (getIntent().hasExtra(Gallery.GALLERY_ID_EXTRA)) {
			intent.putExtra(Gallery.GALLERY_ID_EXTRA,
					getIntent().getLongExtra(Gallery.GALLERY_ID_EXTRA, 0));
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


		// set the HorizontView
		cameraAssistView.setInformations(
				(float) transformBean.getCameraMaxVerticalViewAngle(),
				(float) transformBean.getCameraMaxHorizontalViewAngle(),
				currentOrientation);
		cameraAssistView.invalidate();

		touchView.setCameraAssistView(cameraAssistView);

		this.onClickArea(null);

		// Setup the rotation listener
		buttons = new ArrayList<View>();
		buttons.add(redo);
		buttons.add(undo);
		buttons.add(findViewById(R.id.imageButton1));
		buttons.add(findViewById(R.id.imageButton2));
		buttons.add(findViewById(R.id.imageButton3));
		buttons.add(findViewById(R.id.imageButton4));
		buttons.add(ok);
		buttons.add(findViewById(R.id.customImageButton1));
		listener = new ButtonRotationListener(this, buttons);
		btnAnimator = new ButtonAnimationListener(buttons);

		AbstractActivity.addNavBarMargin(getResources(),
				findViewById(R.id.layout_choose_interpreter));

	}

	@Override
	protected void onResume() {
		super.onResume();
		listener.enable();
	}

	@Override
	protected void onPause() {
		super.onPause();
		listener.disable();
	}

	/**
	 * OnClick method to finish the current drawing.
	 * 
	 * @param view
	 *            current view used this method
	 */
	public void onClickOkay(View view) {
		// first get sure that there is a valid location if
		if (transformBean.getLocation() == null) {
			final String text = getString(R.string.noLocationFound);
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
		} else if (currentOrientation == null) {
			final String text = getString(R.string.noSensorData);
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
		} else {
			// create an abstract data element from the given data and pass
			// it // to the next activity touchView.scaleDiv(scale);
			final AbstractDataElement osmElement = touchView.create();
			intent.putExtra(OSM_ELEMENT, osmElement);
			startActivityForResult(intent);
		}

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

	public void onClickAway(View view) {
		btnAnimator.onRotate();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android
	 * .content.Intent)
	 */
	@Override
	protected void onWorkflowFinished(Intent data) {
		finishWorkflow(data);
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

	public void onClickHide(View view) {

	}

	/**
	 * Get the file of an image and set this to local ImageView.
	 * 
	 * @param file
	 *            The file of the image to display
	 */
	private void setBackground(File file) {
		try {
			final Uri uri = Uri.fromFile(file);
			imageView.setImageBitmap(this.loadFromCamera(uri));

		} catch (IOException e) {
			Log.e(TAG, "Error while setBackground(File)", e);
		}
	}

	private Bitmap loadFromCamera(Uri photoUri) throws IOException {
		final AssetFileDescriptor fileDescriptor = getContentResolver()
				.openAssetFileDescriptor(photoUri, "r");

		final Options options = new Options();

		options.inSampleSize = SAMPLE_SIZE;

		final Bitmap photo = BitmapFactory.decodeFileDescriptor(
				fileDescriptor.getFileDescriptor(), null, options);
		if (photo != null) {
			return this.scaleAndRotate(photo);
		} else {
			return null;
		}
	}


	private Bitmap scaleAndRotate(Bitmap bitmap) {
		final Matrix matrix = new Matrix();
		final double scrAR = this.getScreenRation();

		// Setup the default 'createBitmap' parameters
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		int x = 0;
		int y = 0;

		Log.v("INIT_PARAMETER", "x: " + x + " y: " + y + " h: " + height
				+ " w: " + width);

		if (height < width) {
			matrix.postRotate(90);
			final double picAR = width / height;

			if (scrAR - picAR > 0.1) {
				// Reduce the height of the image
				final int newHeight = (int) (width / scrAR);
				y = (height - newHeight) / 2;
				height = newHeight;
			} else if (picAR - scrAR > 0.1) {
				// Reduce the width of the image
				final int newWidth = (int) (height / scrAR);
				x = (width - newWidth) / 2;
				width = newWidth;
			}
		} else {
			final double picAR = height / width;

			if (scrAR - picAR > 0.1) {
				// Reduce the width of the image
				final int newWidth = (int) (height / scrAR);
				x = (width - newWidth) / 2;
				width = newWidth;
			} else if (picAR - scrAR > 0.1) {
				// Reduce the height of the image
				final int newHeight = (int) (width / scrAR);
				y = (height - newHeight) / 2;
				height = newHeight;
			}
		}

		Log.v("NEW_PARAMETER", "x: " + x + " y: " + y + " h: " + height
				+ " w: " + width);

		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}

		return Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
	}

	private double getScreenRation() {
		final Point size = getIntent().getParcelableExtra(SIZE_EXTRA);
		Log.v("SCREEN_DIMENSION", "h:" + size.x + " w: " + size.y);
		return (1.0 * size.x) / size.y;
	}

	public static final void startActivity(AbstractActivity context,
			File photoFile, TransformationParamBean transformBean,
			DeviceOrientation deviceOrientation, Point viewSize, Bundle extras) {
		final Intent intent = new Intent(context, ShowPictureActivity.class);
		intent.putExtra(FILE_EXTRA, photoFile);
		intent.putExtra(TRANSFORM_BEAN_EXTRA, transformBean);
		intent.putExtra(CURRENT_ORIENTATION_EXTRA, deviceOrientation);
		intent.putExtra(SIZE_EXTRA, viewSize);
		if (extras != null) {
			intent.putExtras(extras);
		}
		context.startActivityForResult(intent);
	}

}
