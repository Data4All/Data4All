/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.view;

import java.util.ArrayList;
import java.util.List;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.util.HorizonCalculationUtil;
import io.github.data4all.util.HorizonCalculationUtil.ReturnValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * AssistView is a helper class to display the optimal picture shooting angle.
 * 
 * This class is drawing red lines into the {@link CameraPreview} and shows
 * unsuitable angles for taking a photo.
 * 
 * @author Andre Koch
 * @CreationDate 12.02.2015
 * @LastUpdate 05.03.2015
 * @version 1.3
 * 
 */

public class CaptureAssistView extends View {

    private Paint cameraStopPaint;
    private Paint invalidRegionPaint;
    private Paint paint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private float maxPitch, maxRoll;
    private DeviceOrientation deviceOrientation;
    private boolean skylook;
    private boolean visible;
    private boolean informationSet;
    private List<Point> points = new ArrayList<Point>();
    private List<Point> point = new ArrayList<Point>();

    HorizonCalculationUtil horizonCalculationUtil = new HorizonCalculationUtil();
    private Runnable finishInflateListener;

    private static final String TAG = CaptureAssistView.class.getSimpleName();

    /**
     * Default Constructor
     * 
     * @param context
     *            Contextclass for global information about an application
     *            environment
     */
    public CaptureAssistView(Context context) {
        super(context);
        initView();
    }

    /**
     * Default Constructor
     * 
     * @param context
     *            Contextclass for global information about an application
     *            environment
     * @param attrs
     * 
     */
    public CaptureAssistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * Default Constructor
     * 
     * @param context
     *            Contextclass for global information about an application
     *            environment
     * @param attrs
     * @param defStyle
     * 
     */
    public CaptureAssistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * This method initializes the variables on the first call and generates the
     * drawing layer for drawing the horizon line and the stop image when the
     * camera is too far up in the sky.
     */
    private void initView() {
        setFocusable(true);
        Log.d(TAG, "initViewIsCalled");

        // initialise variables for the first time.
        this.mMeasuredWidth = getMeasuredWidth();
        this.mMeasuredHeight = getMeasuredHeight();
        this.skylook = false;
        this.visible = true;

        Resources r = this.getResources();

        cameraStopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cameraStopPaint.setColor(r.getColor(R.color.camera_stop_cross));
        cameraStopPaint.setStyle(Paint.Style.STROKE);
        cameraStopPaint.setStrokeWidth(6);

        invalidRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidRegionPaint.setColor(r.getColor(R.color.invalid_region));
        invalidRegionPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setHinting(paint.HINTING_OFF);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

    }

    /**
     * This method is called to get the information for calculating the
     * drawings.
     * 
     * @param maxPitch
     * @param maxRoll
     * @param deviceOrientation
     */
    public void setInformations(float maxPitch, float maxRoll,
            DeviceOrientation deviceOrientation) {
        Log.d(TAG, "setInformationsIsCalled");
        this.maxPitch = maxPitch;
        this.maxRoll = maxRoll;
        this.deviceOrientation = deviceOrientation;
        this.informationSet = true;

        Log.d(TAG,
                "MP" + maxPitch + " MR " + maxRoll + " OR "
                        + deviceOrientation.getPitch() + "  "
                        + deviceOrientation.getRoll() + " MW+H "
                        + mMeasuredWidth + " " + mMeasuredHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDrawCalled");        

        this.mMeasuredWidth = getMeasuredWidth();
        this.mMeasuredHeight = getMeasuredHeight();
        if (informationSet) {
            ReturnValues returnValues = horizonCalculationUtil
                    .calcHorizontalPoints(maxPitch, maxRoll, mMeasuredWidth,
                            mMeasuredHeight, (float) Math.toRadians(85),
                            deviceOrientation);
            this.skylook = returnValues.isSkylook();
            this.visible = returnValues.isVisible();
            this.points = returnValues.getPoints();
            this.point = returnValues.getPoint();
        }
        
        if (visible && !points.isEmpty()) {
            if (!skylook) {
                Path path = getPath();
                canvas.drawPath(path, invalidRegionPaint);
                for (Point iter : point) {

                    // Log.d("TEST", "X: " + iter.getX() + "Y: " + iter.getY());
                    canvas.drawCircle(iter.getX(), iter.getY(), 20, paint);
                    paint.setColor(Color.BLACK);
                }

            } else {
                canvas.drawLine(mMeasuredWidth / 2 - mMeasuredWidth / 12,
                        mMeasuredHeight / 2 - mMeasuredHeight / 8,
                        mMeasuredWidth / 2 + mMeasuredWidth / 12,
                        mMeasuredHeight / 2 + mMeasuredHeight / 8,
                        cameraStopPaint);

                canvas.drawLine(mMeasuredWidth / 2 + mMeasuredWidth / 12,
                        mMeasuredHeight / 2 - mMeasuredHeight / 8,
                        mMeasuredWidth / 2 - mMeasuredWidth / 12,
                        mMeasuredHeight / 2 + mMeasuredHeight / 8,
                        cameraStopPaint);
            }
        }
        canvas.restore();

    }

    /**
     * This method draws the lines for the horizon line. everything is filled
     * from the top left corner and the upper right corner to the horizon line
     * with a defined color.
     * 
     * @return Path calculated horizon line.
     */
    private Path getPath() {
        Path path = new Path();
        boolean firstIter = true;
        if (!points.isEmpty()) {
            for (Point iter : points) {
                if (firstIter) {
                    path.moveTo(iter.getX(), iter.getY());
                    firstIter = false;
                } else {
                    path.lineTo(iter.getX(), iter.getY());
                }
            }
            path.lineTo(points.get(0).getX(), points.get(0).getY());
        }
        return path;

    }
    
    /**
     * Testing if a point is over the horizont (red marked area)
     * @param p point to be testet
     * @return result true if the point is in the red marked area
     * 
     * @author vkochno
     */
    public boolean overHorizont(Point point){
        Bitmap bitmap = null;
        this.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        if(bitmap.getPixel((int) point.getX(), (int) point.getY()) == Color.TRANSPARENT){
            return false;
        }
        return true; 
    }
    

    public void setOnFinishInflateListener(Runnable listener) {
        this.finishInflateListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (finishInflateListener != null) {
            finishInflateListener.run();
        }
    }

    public boolean isSkylook() {
        return skylook;
    }

    
    

}