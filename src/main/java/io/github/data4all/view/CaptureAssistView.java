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

import io.github.data4all.R;
import io.github.data4all.logger.Log;
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
    private float coordinateLeftX;
    private float coordinateLeftY;
    private float coordinateRightX;
    private float coordinateRightY;

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

        Resources r = this.getResources();
        cameraCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cameraCrossPaint.setColor(r.getColor(R.color.camera_cross));
        cameraCrossPaint.setStyle(Paint.Style.STROKE);
        cameraCrossPaint.setStrokeWidth(4);

        cameraStopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cameraStopPaint.setColor(r.getColor(R.color.camera_stop_cross));
        cameraStopPaint.setStyle(Paint.Style.STROKE);
        cameraStopPaint.setStrokeWidth(6);

        invalidRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidRegionPaint.setColor(r.getColor(R.color.invalid_region));
        invalidRegionPaint.setStyle(Paint.Style.FILL);

    }

    public void setPoints(float leftX, float leftY, float rightX, float rightY) {
        this.coordinateLeftX = leftX;
        this.coordinateLeftY = leftY;
        this.coordinateRightX = rightX;
        this.coordinateRightY = rightY;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Path path = getPath();

        canvas.drawPath(path, invalidRegionPaint);

        canvas.restore();

    }

    private Path getPath() {
        int mMeasuredWidth = getMeasuredWidth();

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(coordinateLeftX, coordinateLeftY);
        path.lineTo(coordinateRightX, coordinateRightY);
        if (coordinateRightX != mMeasuredWidth || coordinateRightY != 0) {
            path.lineTo(mMeasuredWidth, 0);
        }
        path.lineTo(0, 0);
        return path;

    }

}