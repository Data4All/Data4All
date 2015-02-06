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
package io.github.data4all.model.drawing;

import io.github.data4all.logger.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import unseen.labs.face.harris.HarrisFast;
import unseen.labs.face.harris.Corner;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Analyzes the corners of the given picture and returns the analyzed points,
 * where the DrawingMotion can be catch to.
 * 
 * @author tbrose
 *
 */
public class CornerAnalyser implements Analyzer<List<Point>> {

    /** The gaussian filter parameter */
    private static final double FILTER_SIGMAR = 1.2;

    /** The parameter for the harris measure formula */
    private static final double FILTER_K = 0.06;

    /** The minimum distance between two corners */
    private static final int FILTER_DISTANCE = 10;

    /** The height of the device screen */
    private final int screenHeight;

    /** The width of the device screen */
    private final int screenWidth;

    /** The height of the bitmap to analyze */
    private final int bmpHeight;

    /** The width of the bitmap to analyze */
    private final int bmpWidth;

    /** The analyzation-tool for corner analysis */
    private final HarrisFast image;

    /**
     * Setup constructor for the {@code analyze()} method.
     * 
     * @param image
     *            The image to be analyzed
     */
    public CornerAnalyser(File image, int screenHeight, int screenWidth) {
        if (image != null) {
            this.screenHeight = screenHeight;
            this.screenWidth = screenWidth;

            // Read in the image
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bm =
                    BitmapFactory.decodeFile(image.getAbsolutePath(), options);

            // Convert into the grayscale image and setup the analyzation tool
            this.bmpHeight = bm.getHeight();
            this.bmpWidth = bm.getWidth();
            this.image = new HarrisFast(toGrayscale(bm), bmpWidth, bmpHeight);
        } else {
            throw new IllegalArgumentException("image is null");
        }
    }

    /**
     * Converts the given Bitmap into a two dimensional grayscale matrix.
     * 
     * @param bmpOriginal
     *            The bitmap to convert
     * @return The grayscale matrix
     */
    private static int[][] toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        final int[][] gray = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gray[x][y] = rgb2gray(bmpOriginal.getPixel(x, y));
            }
        }
        return gray;
    }

    /**
     * Converts a given ARGB-Color to a gray-value [0-255].
     * 
     * @param argb
     *            The color to convert
     * @return
     */
    private static int rgb2gray(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }

    /**
     * Returns the analyzed points, where the DrawingMotion can be catch to.
     */
    @Override
    public List<Point> analyze() {
        // Specify parameters above
        image.filter(FILTER_SIGMAR, FILTER_K, FILTER_DISTANCE);
        List<Point> result = new ArrayList<Point>(image.corners.size());
        for (Corner c : image.corners) {
            Point p = scalePoint(c.x, c.y);
            result.add(p);
            Log.d(getClass().getSimpleName(),
                    "Point: [" + p.getX() + "|" + p.getY() + "]");
        }
        return result;
    }

    private Point scalePoint(float x, float y) {
        return new Point(x / bmpWidth * screenWidth, y / bmpHeight
                * screenHeight);
    }
}
