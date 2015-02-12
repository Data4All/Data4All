package io.github.data4all.view;

import io.github.data4all.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CaptureAssistView extends View {

	private int previewWidth;
	private double percentage;

	private Paint cameraCrossPaint;
	private Paint cameraStopPaint;
	private Paint invalidRegionPaint;

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

	public void setInvalidatedRegionPrecentage(double _precentage) {
		this.percentage = _precentage;
	}

	public void setPreviewWidth(int _previewWidth) {
		this.previewWidth = _previewWidth;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int mMeasuredWidth = getMeasuredWidth();
		int mMeasuredHeight = getMeasuredHeight();

		int offWidth = mMeasuredWidth - previewWidth;

		if (percentage > 0.0) {

			RectF invalidateRect = new RectF(offWidth / 2, 0, mMeasuredWidth
					- offWidth / 2,
					new Double(mMeasuredHeight * percentage).intValue());
			canvas.drawRect(invalidateRect, invalidRegionPaint);
		}

		if (percentage < 1) {

			canvas.drawLine(mMeasuredWidth / 2 - mMeasuredWidth / 12,
					mMeasuredHeight / 2, mMeasuredWidth / 2 + mMeasuredWidth
							/ 12, mMeasuredHeight / 2, cameraCrossPaint);

			canvas.drawLine(mMeasuredWidth / 2, mMeasuredHeight / 2
					- mMeasuredWidth / 12, mMeasuredWidth / 2, mMeasuredHeight
					/ 2 + mMeasuredWidth / 12, cameraCrossPaint);

			RectF rollOval = new RectF((mMeasuredWidth / 2) - mMeasuredWidth
					/ 16, (mMeasuredHeight / 2) - mMeasuredWidth / 16,
					(mMeasuredWidth / 2) + mMeasuredWidth / 16,
					(mMeasuredHeight / 2) + mMeasuredWidth / 16);

			canvas.drawRect(rollOval, cameraCrossPaint);
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
}