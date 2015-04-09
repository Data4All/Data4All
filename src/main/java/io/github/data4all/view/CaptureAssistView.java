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
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.handler.LastChoiceHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.util.HorizonCalculationUtil;
import io.github.data4all.util.Optimizer;
import io.github.data4all.util.HorizonCalculationUtil.ReturnValues;
import io.github.data4all.util.PointToCoordsTransformUtil;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

/**
 * AssistView is a helper class to display the optimal picture shooting angle.
 * 
 * This class is drawing red lines into the {@link CameraPreview} and shows
 * unsuitable angles for taking a photo.
 * 
 * @author Andre Koch & burghardt
 * @CreationDate 12.02.2015
 * @LastUpdate 18.03.2015
 * @version 1.4
 * 
 */

public class CaptureAssistView extends View {

    private Paint cameraStopPaint;
    private Paint invalidRegionPaint;
    private Paint paint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private float horizontalViewAngle, verticalViewAngle;
    private float horizondegree = 86;
    private DeviceOrientation deviceOrientation;
    private boolean skylook;
    private boolean visible;
    private boolean informationSet;
    private List<Point> points = new ArrayList<Point>();
    private Bitmap bitmap;
    private List<PolyElement> polyElements;
    private TransformationParamBean tps;
    private PointToCoordsTransformUtil util;

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

        // add osmElements from the database to the map
        DataBaseHandler db = new DataBaseHandler(getContext());
        this.polyElements = db.getAllPolyElements();
        db.close();

        Resources r = this.getResources();

        cameraStopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cameraStopPaint.setColor(r.getColor(R.color.camera_stop_cross));
        cameraStopPaint.setStyle(Paint.Style.STROKE);
        cameraStopPaint.setStrokeWidth(6);

        invalidRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidRegionPaint.setColor(r.getColor(R.color.invalid_region));
        invalidRegionPaint.setStyle(Paint.Style.FILL);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setAlpha(64);
        paint.setStyle(Paint.Style.FILL);
        

        this.tps = new TransformationParamBean(1.5,
                horizontalViewAngle, verticalViewAngle, mMeasuredWidth,
                mMeasuredHeight, Optimizer.currentBestLoc());  
        
        util = new PointToCoordsTransformUtil();
    }

    /**
     * This method is called to get the information for calculating the
     * drawings.
     * 
     * @param horizontalViewAngle
     * @param maxRoll
     * @param deviceOrientation
     */
    public void setInformations(float horizontalViewAngle,
            float verticalViewAngle, DeviceOrientation deviceOrientation) {
        Log.d(TAG, "setInformationsIsCalled");
        this.horizontalViewAngle = horizontalViewAngle;
        this.verticalViewAngle = verticalViewAngle;
        this.deviceOrientation = deviceOrientation;
        this.tps.setMaxHorizontalViewAngle(verticalViewAngle);
        this.tps.setCameraMaxVerticalViewAngle(horizontalViewAngle);
        this.tps.setLocation(Optimizer.currentBestLoc());
        this.informationSet = true;
        Log.d(TAG, "MP" + horizontalViewAngle + " MR " + verticalViewAngle
                + " OR " + deviceOrientation.getPitch() + "  "
                + deviceOrientation.getRoll() + " MW+H " + mMeasuredWidth + " "
                + mMeasuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDrawCalled");
        // save the Size of the View
        this.mMeasuredWidth = getMeasuredWidth();
        this.mMeasuredHeight = getMeasuredHeight();
        // when the needed information have been set: calculate the horizon
        if (informationSet) {
            ReturnValues returnValues = horizonCalculationUtil
                    .calcHorizontalPoints(horizontalViewAngle,
                            verticalViewAngle, mMeasuredWidth, mMeasuredHeight,
                            (float) Math.toRadians(horizondegree),
                            deviceOrientation);            
            tps.setPhotoHeight(mMeasuredHeight);
            tps.setPhotoWidth(mMeasuredWidth);
          
            for (PolyElement iter : polyElements) {
                if (iter.getType() == PolyElementType.AREA
                        || iter.getType() == PolyElementType.BUILDING) {   
                    this.points = util.calculateNodesToPoint(iter.getNodes(), tps, deviceOrientation);
                }
                Path path = getPath();
                canvas.drawPath(path, paint);
            }
                   
            this.skylook = returnValues.isSkylook();
            this.visible = returnValues.isVisible();
            this.points = returnValues.getPoints();
        }
        // if the horizon is visible and there are points do draw the horizon
        if (visible && !points.isEmpty()) {
            // if less then 50% of the display is over the horizon: draw horizon
            if (!skylook) {
                Path path = getPath();
                canvas.drawPath(path, invalidRegionPaint);
            } else {
                // draw a big X
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
     * 
     * @param point
     *            point to be testet
     * @return result true if the point is in the red marked area
     * 
     * @author vkochno & burghardt
     */
    public boolean overHorizont(Point point) {
        if (bitmap == null) {
            this.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(this.getDrawingCache());
            this.setDrawingCacheEnabled(false);
        }
        if (bitmap.getPixel((int) point.getX(), (int) point.getY()) == Color.TRANSPARENT) {
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