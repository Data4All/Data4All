package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.activity.TagActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.util.PointToCoordsTransformUtil;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

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
    private String osmElem = "OSM_ELEMENT";

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
        tagIntent = new Intent(this, TagActivity.class);

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
        tagIntent.putExtra(osmElem, osmElement);
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
            int display_mode = getResources().getConfiguration().orientation;
            Matrix matrix = new Matrix();
            if (display_mode == 1) {
                matrix.setRotate(90);
            }

            Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Log.e(this.getClass().toString(), "ROTATION:");
            imageView.setImageBitmap(adjustedBitmap);
        } catch (FileNotFoundException e) {
            Log.e(this.getClass().toString(), "ERROR, no file found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "ERROR, file is no image");
            e.printStackTrace();
        }
    }

}
