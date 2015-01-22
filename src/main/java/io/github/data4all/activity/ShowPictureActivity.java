package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.drawing.AreaMotionInterpreter;
import io.github.data4all.model.drawing.BuildingMotionInterpreter;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.model.drawing.PointMotionInterpreter;
import io.github.data4all.model.drawing.WayMotionInterpreter;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.util.PointToCoordsTransformUtil;
import io.github.data4all.view.TouchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
    private Button undo;
	private Button redo;

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
        undo = (Button) findViewById(R.id.undobtn);
        redo = (Button)findViewById(R.id.redobtn);

        if (getIntent().hasExtra("file_path")) {
            setBackground(Uri.fromFile((File) getIntent().getExtras().get(
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
        
        transformBean.setPhotoWidth(touchView.getWidth());
        transformBean.setPhotoHeight(touchView.getHeight());
        
        // set a new PointToCoordsTransformUtil to the touchView which includes
        // the deviceOrientation, current Location, camera angle, photo size and
        // height
        touchView.setTransformUtil(new PointToCoordsTransformUtil(
                transformBean, currentOrientation));
    }

    public void onClickOkay(View view) {
        //create an osm element from the given data and pass it to the next activity
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
    
    public void SetRedoEnable(boolean enabled){
    	redo.setEnabled(enabled);
    }
    
    public void SetUndoEnable(boolean enabled){
    	undo.setEnabled(enabled);
    }
    
    public boolean onLongClick(View v,MotionEvent event){
    	touchView.deletePoint(new Point(event.getX(),event.getY()));
		return true;
    }

    /**
     * Get a Uri of a Image and set this to local layout as background
     * 
     * @param selectedImage
     */
    private void setBackground(Uri selectedImage) {
		Bitmap bitmap;
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
    
    private void disableButtons(){
		findViewById(R.id.imageButton1).setVisibility(View.GONE);;
		findViewById(R.id.imageButton2).setVisibility(View.GONE);
		findViewById(R.id.imageButton3).setVisibility(View.GONE);
		findViewById(R.id.button1).setVisibility(View.GONE);
		findViewById(R.id.button2).setVisibility(View.GONE);
		
	}
}
