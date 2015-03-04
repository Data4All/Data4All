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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
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
 * @LastUpdate 12.02.2015
 * @version 1.0
 * 
 */

public class CaptureAssistView extends View {

    private Paint cameraCrossPaint;
    private Paint cameraStopPaint;
    private Paint invalidRegionPaint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private boolean skylook;
    private boolean visible;
    private List<Point> points = new ArrayList<Point>();

    HorizonCalculationUtil horizonCalculationUtil = new HorizonCalculationUtil();

    private static final String TAG = CaptureAssistView.class.getSimpleName();

    public CaptureAssistView(Context context) {
        super(context);
        initView();
    }

    public CaptureAssistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CaptureAssistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setFocusable(true);
        Log.d(TAG, "initViewIsCalled");

        // Sorry for this... but have to initialise to some Points.
        // TODO FIX ME
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

    }

    public void setInformations(float maxPitch, float maxRoll,
            DeviceOrientation deviceOrientation) {
        Log.d(TAG, "set informations");
        this.mMeasuredWidth = getMeasuredWidth();
        this.mMeasuredHeight = getMeasuredHeight();
        ReturnValues returnValues = horizonCalculationUtil
                .calcHorizontalPoints(maxPitch, maxRoll, mMeasuredWidth,
                        mMeasuredHeight, (float) Math.toRadians(70),
                        deviceOrientation);
        this.skylook = returnValues.isSkylook();
        this.visible = returnValues.isVisible();
        this.points = returnValues.getPoints();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDrawCalled");
        if (visible && !points.isEmpty()) {
            if (!skylook) {
                Path path = getPath();
                canvas.drawPath(path, invalidRegionPaint);
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

}