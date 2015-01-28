/**
 * Copyright (C) Data4All
 */
package io.github.data4all.model.drawing;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Analyzes the edges of the given picture and returns the analyzed points,
 * where the DrawingMotion can be catch to.
 * 
 * @author tbrose
 *
 */
public class EdgeAnalyser implements Analyzer<List<Point>> {

    private Bitmap image;

    public EdgeAnalyser(File image) {
        if (image != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            final String path = image.getAbsolutePath();
            this.image = BitmapFactory.decodeFile(path, options);
        } else {
            throw new IllegalArgumentException("image is null");
        }
    }

    /**
     * Returns the analyzed points, where the DrawingMotion can be catch to.
     */
    @Override
    public List<Point> analyze() {

        return null;
    }
}
