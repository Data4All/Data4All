package io.github.data4all;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	Bitmap bmp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		((Button) findViewById(R.id.btnGallary))
				.setOnClickListener(new OnClickListener() {
					@Override
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

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				if (bmp != null && !bmp.isRecycled()) {
					bmp = null;
				}

				bmp = BitmapFactory.decodeFile(filePath);
				image.setBackgroundResource(0);
				image.setImageBitmap(bmp);
			} else {
				Log.d("Status:", "Photopicker canceled");
			}
		}
	}
}
