package io.github.data4all.activity;

import io.github.data4all.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity to set a new Layer-Backgroundimage
 * 
 * @author vkochno
 *
 */
public class ShowPictureActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		if (getIntent().hasExtra("file_path")) {
			setBackground(Uri.fromFile((File) getIntent().getExtras().get(
					"file_path")));
		} else {
			Log.e(this.getClass().toString(), "ERROR, no file found in intent");

		}
	}

	/**
	 * Get a Uri of a Image and set this to local layout as background
	 * 
	 * @param selectedImage
	 */
	private void setBackground(Uri selectedImage) {
		Resources res = getResources();
		Bitmap bitmap;
		try { // try to convert a image to a bitmap
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), selectedImage);
			BitmapDrawable bd = new BitmapDrawable(res, bitmap);
			View view = findViewById(R.id.LinearLayout);
			view.setBackground(bd);
		} catch (FileNotFoundException e) {
			Log.e(this.getClass().toString(), "ERROR, no file found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(this.getClass().toString(), "ERROR, file is no image");
			e.printStackTrace();
		}
	}
}
