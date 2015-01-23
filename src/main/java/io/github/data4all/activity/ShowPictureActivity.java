package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.activity.TagActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.view.TouchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
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
public class ShowPictureActivity extends Activity {


    private TouchView touchView;
    private ImageView imageView;
    	private Intent tagIntent;
	private String type = "TYPE_DEF";
	private String point = "POINT";
	private String building = "BUILDING";
	private String way = "WAY";
	private String area = "AREA";
	private Button undo;
	private Button redo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picture);
        imageView = (ImageView) findViewById(R.id.imageView1);
        touchView = (TouchView) findViewById(R.id.touchView1);
        undo = (Button) findViewById(R.id.undobtn);
        redo = (Button)findViewById(R.id.redobtn);
tagIntent = new Intent(this,TagActivity.class);
       
        if (getIntent().hasExtra("file_path")) {
            setBackground(Uri.fromFile((File) getIntent().getExtras().get(
                    "file_path")));
        } else {
            Log.e(this.getClass().toString(), "ERROR, no file found in intent");
        }
    }

	public void onClickOkay(View view) {
		startActivity(tagIntent);
	}

    public void onClickPoint(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.POINT);
        touchView.invalidate();
        tagIntent.putExtra(type, point);
    }

    public void onClickPath(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.WAY);
        touchView.invalidate();
        tagIntent.putExtra(type, way);
    }

    public void onClickArea(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.AREA);
        touchView.invalidate();
        tagIntent.putExtra(type, area);
    }

    public void onClickBuilding(View view) {
        touchView.clearMotions();
        touchView.setInterpretationType(TouchView.InterpretationType.BUILDING);
        touchView.invalidate();
        tagIntent.putExtra(type, building);
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

}
