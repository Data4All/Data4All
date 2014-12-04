package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.R.id;
import io.github.data4all.R.layout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ShowPictureActivity extends Activity {
	Button button;
	ImageView image;
	LinearLayout layout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		if(getIntent().hasExtra("file_path")){
			Log.d("Status:", "intent is there");
			setBackground(Uri.fromFile((File) getIntent().getExtras().get("file_path")));
		}
		((Button) findViewById(R.id.btnGallary))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						openGallery();
					}
				});
	}

	private void openGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (data != null && resultCode == RESULT_OK) {

				Uri selectedImage = data.getData();
				setBackground(selectedImage);
		        
			} else {
				Log.d("Status:", "Photopicker canceled");
			}
		}
	}
	
	private void setBackground(Uri selectedImage) {
		Resources res = getResources();
        Bitmap bitmap;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
			BitmapDrawable bd = new BitmapDrawable(res, bitmap);
	        View view = findViewById(R.id.LinearLayout);
	        view.setBackground(bd);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
