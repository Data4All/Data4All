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

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public void onClickPoint(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        if (getIntent().hasExtra("file_path")) {
            Log.d("Status:", "intent is there");
            // intent.putExtra("file_path", (String)
            // getIntent().getExtras().get("file_path"));
            startActivity(intent);
        } else {
            // TODO: nachricht an nutzer falls kein file_path vorhanden
        }
    }

    public void onClickPath(View view) {
        // Kabloey
    }

    public void onClickArea(View view) {
        // Kabloey
    }

    public void onClickBuilding(View view) {
        // Kabloey
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
