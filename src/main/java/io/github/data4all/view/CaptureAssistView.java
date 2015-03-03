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
import io.github.data4all.model.drawing.Point;
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
	private Point coordinateLeft;
	private Point coordinateRight;
	private int mMeasuredWidth;
	private int mMeasuredHeight;
	private boolean skylook;

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
		
		Log.d(TAG, "pooint");
		
		this.mMeasuredWidth = getMeasuredWidth();
		this.mMeasuredHeight = getMeasuredHeight();
		this.coordinateLeft = new Point(0, 0);
		this.coordinateRight = new Point(0, 0);
		this.skylook = false;
		
		Resources r = this.getResources();

		cameraStopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cameraStopPaint.setColor(r.getColor(R.color.camera_stop_cross));
		cameraStopPaint.setStyle(Paint.Style.STROKE);
		cameraStopPaint.setStrokeWidth(6);

		invalidRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		invalidRegionPaint.setColor(r.getColor(R.color.invalid_region));
		invalidRegionPaint.setStyle(Paint.Style.FILL);

	}

	public void setInformations(ReturnValues returnValues) {
		Log.d(TAG, "set informations");		
		this.coordinateLeft = returnValues.getPoint1();
		this.coordinateRight = returnValues.getPoint2();
		this.skylook = returnValues.isSkylook();
		
		Log.i(TAG, "DEBUGDEVICE WIDTH  : "+mMeasuredWidth);
		Log.i(TAG, "DEBUGDEVICE HEIGHT : "+mMeasuredHeight);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDrawCalled");
		if (!skylook) {

			Path path = getPath();

			canvas.drawPath(path, invalidRegionPaint);
		} else {
			canvas.drawLine(mMeasuredWidth / 2 - mMeasuredWidth / 12,
					mMeasuredHeight / 2 - mMeasuredHeight / 8, mMeasuredWidth
							/ 2 + mMeasuredWidth / 12, mMeasuredHeight / 2
							+ mMeasuredHeight / 8, cameraStopPaint);

			canvas.drawLine(mMeasuredWidth / 2 + mMeasuredWidth / 12,
					mMeasuredHeight / 2 - mMeasuredHeight / 8, mMeasuredWidth
							/ 2 - mMeasuredWidth / 12, mMeasuredHeight / 2
							+ mMeasuredHeight / 8, cameraStopPaint);
		}

		canvas.restore();

	}

	private Path getPath() {

		Path path = new Path();
		path.moveTo(0, 0);
		Log.i(TAG, "DEBUG LEFT X  : "+coordinateLeft.getX());
		Log.i(TAG, "DEBUG LEFT Y  : "+coordinateLeft.getY());
		Log.i(TAG, "DEBUG RIGHT X : "+coordinateRight.getX());
		Log.i(TAG, "DEBUG RIGHT Y : "+coordinateRight.getY());
		
		path.lineTo(coordinateLeft.getX(), coordinateLeft.getY());
		path.lineTo(coordinateRight.getX(), coordinateRight.getY());
		if (coordinateRight.getX() != mMeasuredWidth || coordinateRight.getY() != 0) {
			path.lineTo(mMeasuredWidth, 0);
		}
		path.lineTo(0, 0);
		return path;

	}

}