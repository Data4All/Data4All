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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Surface;
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
public class ShowPictureActivity extends BasicActivity {

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
    private ImageButton undo;
    private ImageButton redo;

    // the current TransformationBean and device orientation when the picture
    // was taken
    private TransformationParamBean transformBean;
    private DeviceOrientation currentOrientation;

    /**
     * public standard constructor.<br\>
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
        });

        if (getIntent().hasExtra("file_path")) {
            this.setBackground(Uri.fromFile((File) getIntent().getExtras().get(
                    "file_path")));
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
    }

    /**
     * OnClick method to finish the current drawing.<br\>
     * 
     * * @param view current view used this method
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
     * 
     * @author vkochno
     */
    private void setBackground(Uri selectedImage) {
        // try to convert a image to a bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), selectedImage);
            final Display d = getWindowManager().getDefaultDisplay();
            if (d.getRotation() == Surface.ROTATION_0) {
                // get the display size
                final Point size = new Point();
                d.getSize(size);
                final int width = size.x;
                final int height = size.y;

                // scaled bitmap
                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                        height, width, true);

                // create a matrix object
                final Matrix matrix = new Matrix();
                // clockwise by 90 degrees
                matrix.postRotate(90);

                // create a new bitmap from the original using the matrix to
                // transform the result
                bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                        matrix, true);
            }

            // set the imageview bitmap-resource
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e(this.getClass().toString(), "ERROR, no file found" + e);
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "ERROR, file is no image" + e);
        }
    }
}
