/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.CornerAnalyser;
import io.github.data4all.model.drawing.RedoUndo.UndoRedoListener;
import io.github.data4all.util.PointToCoordsTransformUtil;
import io.github.data4all.view.TouchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Activity to set a new Layer-Backgroundimage
 * 
 * @author vkochno
 *
 */
public class ShowPictureActivity extends BasicActivity {

    private TouchView touchView;
    private ImageView imageView;
    private Intent intent;
    private static final String TYPE = "TYPE_DEF";
    private static final int POINT = 1;
    private static final int BUILDING = 3;
    private static final int WAY = 2;
    private static final int AREA = 4;
    private static final String OSM_ELEMENT = "OSM_ELEMENT";
    private ImageButton undo;
    private ImageButton redo;

    Bitmap bitmap;

    // the current TransformationBean and device orientation when the picture
    // was taken
    private TransformationParamBean transformBean;
    private DeviceOrientation currentOrientation;

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
        redo = (ImageButton) findViewById(R.id.redobtn);
        
        

        touchView.setUndoRedoListener(new UndoRedoListener() {

            @Override
            public void canUndo(boolean state) {
                undo.setEnabled(state);
            }

            @Override
            public void canRedo(boolean state) {
                redo.setEnabled(state);
            }
        });

        if (getIntent().hasExtra("file_path")) {
            File image = (File) getIntent().getExtras().get("file_path");
            setBackground(Uri.fromFile(image));
            
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            
            touchView.setCatchPoints(new CornerAnalyser(image,size.x, size.y).analyze());
            
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

        onClickBuilding(null);
    }

    public void onClickOkay(View view) {

        // create an osm element from the given data and pass it to the next
        // activity
        OsmElement osmElement = touchView.create();
        intent.putExtra(OSM_ELEMENT, osmElement);
        startActivity(intent);
    }

    public void onClickPoint(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.POINT);
        touchView.invalidate();
        intent.putExtra(TYPE, POINT);
    }

    public void onClickPath(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.WAY);
        touchView.invalidate();
        intent.putExtra(TYPE, WAY);
    }

    public void onClickArea(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.AREA);
        touchView.invalidate();
        intent.putExtra(TYPE, AREA);

    }

    public void onClickBuilding(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.BUILDING);
        touchView.invalidate();
        intent.putExtra(TYPE, BUILDING);
    }

    public void onClickRedo(View view) {
        touchView.redo();
        touchView.invalidate();
    }

    public void onClickUndo(View view) {
        touchView.undo();
        touchView.invalidate();
    }

    public void SetRedoEnable(boolean enabled) {
        this.redo.setEnabled(enabled);
    }

    public void SetUndoEnable(boolean enabled) {
        this.undo.setEnabled(enabled);
    }

    /**
     * Get a Uri of a Image and set this to local layout as background
     * 
     * @param selectedImage
     */
    private void setBackground(Uri selectedImage) {

        try { // try to convert a image to a bitmap
            bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), selectedImage);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e(this.getClass().toString(), "ERROR, no file found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "ERROR, file is no image");
            e.printStackTrace();
        }
    }

}
