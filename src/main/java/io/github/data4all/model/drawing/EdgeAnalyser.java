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
    
    /**
     * Detects all edges in the given bitmap.<br/>
     * The brighter a pixel is the more different the pixel is to its neighbors.
     * 
     * @param input The image bitmap to analyze the edges
     * @return A image providing all edges of the input bitmap
     */
    private static Bitmap edgeDetection(Bitmap input) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), conf);
        for(int x = input.getWidth() - 1; x >= 0; x--) {
            for(int y = input.getHeight() - 1; y >= 0; y--) {
                if(x == 0 || y == 0) {
                    result.setPixel(x, y, 0xff000000);
                } else {
                    int rgb1 = input.getPixel(x, y);
                    int rgb2 = input.getPixel(x-1, y);
                    int rgb3 = input.getPixel(x, y-1);
                    rgb1 = (rgb1&0xff) + ((rgb1>>8)&0xff) + ((rgb1>>16)&0xff);
                    rgb2 = (rgb2&0xff) + ((rgb2>>8)&0xff) + ((rgb2>>16)&0xff);
                    rgb3 = (rgb3&0xff) + ((rgb3>>8)&0xff) + ((rgb3>>16)&0xff);

                    int dif = (int) Math.min(Math.hypot(rgb1-rgb2, rgb1-rgb3), 256);
                    int rgb4 = (dif << 16) | (dif << 8) | dif;

                    result.setPixel(x,y, 0xff000000 | rgb4);
                }
            }
        }
        return result;
    }
}
