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

import io.github.data4all.Data4AllApplication;
import io.github.data4all.R;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;
import io.github.data4all.util.HorizonCalculationUtil;
import io.github.data4all.util.Optimizer;
import io.github.data4all.util.HorizonCalculationUtil.ReturnValues;
import io.github.data4all.util.PointToCoordsTransformUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
    private Paint textPaint;
    private Paint poiPaint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int augMinTextSize = 20;
    private int augMaxTextSize = 100;
    private float horizontalViewAngle;
    private float verticalViewAngle;
    private float horizondegree = 87.5f;
    private float maxDistance = 100;
    private DeviceOrientation deviceOrientation;
    private boolean skylook;
    private boolean visible;
    private boolean informationSet;
    private List<Point> points = new ArrayList<Point>();
    private Bitmap bitmap;
    private List<AbstractDataElement> dataElements;
    private TransformationParamBean tps;
    private PointToCoordsTransformUtil util;
    private double rotateDegree;
    private Bitmap poiBitmap;
    private HorizonCalculationUtil horizonCalculationUtil;

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
     *          AttributeSet
     * @param defStyle
     *          defStyle
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
        this.dataElements = db.getAllDataElements();
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

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setShadowLayer(1.0f, 1.0f, 1.0f, Color.WHITE);

        poiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        poiPaint.setColor(Color.BLUE);
        poiPaint.setStyle(Paint.Style.FILL);
        poiPaint.setTextAlign(Align.CENTER);

        BitmapDrawable bitmapDraw = (BitmapDrawable) r
                .getDrawable(R.drawable.ic_setpoint_blue);
        poiBitmap = bitmapDraw.getBitmap();

        this.tps = new TransformationParamBean(getDeviceHeight(),
                horizontalViewAngle, verticalViewAngle, mMeasuredWidth,
                mMeasuredHeight, Optimizer.currentBestLoc());

        util = new PointToCoordsTransformUtil();
        horizonCalculationUtil = new HorizonCalculationUtil();
    }

    /**
     * This method is called to get the information for calculating the
     * drawings.
     * 
     * @param horizontalViewAngle
     *          maxCameraAngle
     * @param verticalViewAngle
     *          maxCameraAngle
     * @param deviceOrientation
     *          the deviceOrientation
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
            this.rotateDegree = returnValues.getRotateDegree();
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
        // Draws the augmented Reality including the Infotext
        if (informationSet && getAugmented()) {
            tps.setPhotoHeight(mMeasuredHeight);
            tps.setPhotoWidth(mMeasuredWidth);
            Point center = null;
            float distance = maxDistance + 1;
            Boolean isWay = false;
            for (AbstractDataElement iter : dataElements) {
                // Check if it's an instance of a PolyElement
                if (iter instanceof PolyElement) {
                    PolyElement poly = (PolyElement) iter;
                    // Draw Mapped Object on Canvas
                    if (poly.getType() == PolyElementType.AREA
                            || poly.getType() == PolyElementType.BUILDING) {
                        this.points = util.calculateNodesToPoint(
                                poly.getNodes(), tps, deviceOrientation);
                        center = getCenter(points);
                        distance = (float) util.calculateDistance(tps,
                                deviceOrientation, center);
                        if (distance < maxDistance) {
                            Path path = getPath();
                            canvas.drawPath(path, paint);
                        }
                    } else {
                        isWay = true;
                    }
                } else {
                    // Draw PointOfInterest
                    Node node = (Node) iter;
                    center = util.calculateNodeToPoint(node, tps,
                            deviceOrientation);
                    distance = (float) util.calculateDistance(tps,
                            deviceOrientation, center);
                    if (distance < maxDistance) {
                        canvas.rotate((float) Math.toDegrees(rotateDegree),
                                center.getX(), center.getY());
                        // Resize BitMap for different distances
                        float scale = (float) (1.2 / (distance / 6 + 1) + 0.1);
                        bitmap = Bitmap.createScaledBitmap(poiBitmap,
                                (int) (scale * poiBitmap.getWidth()),
                                (int) (scale * poiBitmap.getHeight()), true);
                        canvas.drawBitmap(bitmap, center.getX(), center.getY(),
                                poiPaint);
                        canvas.rotate((float) Math.toDegrees(-rotateDegree),
                                center.getX(), center.getY());
                    }
                }
                // Write InfoText
                if (!isWay && distance < maxDistance) {
                    String value = "";
                    // get String for Infotext
                    if (!iter.getTags().keySet().isEmpty()
                            && !iter.getTags().values().isEmpty()) {
                        final Tag tag = (Tag) iter.getTags().keySet().toArray()[0];
                        value = tag.getNamedValue(Data4AllApplication.context,
                                iter.getTags().get(tag));
                    }
                    // alter TextSize for different distances
                    textPaint
                            .setTextSize(((augMaxTextSize - augMinTextSize) / (distance / 4 + 1))
                                    + augMinTextSize);
                    canvas.rotate((float) Math.toDegrees(rotateDegree),
                            center.getX(), center.getY());
                    canvas.drawText(value, center.getX(), center.getY(),
                            textPaint);
                    canvas.rotate((float) -Math.toDegrees(rotateDegree),
                            center.getX(), center.getY());
                }
                isWay = false;
            }
        }
        canvas.restore();
    }

    /**
     * calculates the Center of a list of Points
     * 
     * @param points
     *            a List of Points
     * @return a Point
     */
    public static Point getCenter(List<Point> points) {
        int i = 0;
        float x = 0;
        float y = 0;
        for (Point iter : points) {
            x += iter.getX();
            y += iter.getY();
            i += 1;
        }
        return new Point(x / i, y / i);
    }

    protected boolean getAugmented() {
        PreferenceManager.setDefaultValues(getContext(), R.xml.settings, false);
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        return prefs.getBoolean("augmented_reality", false);
    }

    /**
     * Reads the height of the device in condition of the bodyheight from the
     * preferences.
     * 
     * @author tbrose
     * 
     *         If the preference is empty or not set the default value is
     *         stored.
     * 
     * @return The height of the device or {@code 0} if the preference is not
     *         set or empty
     */
    private double getDeviceHeight() {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        final Resources res = getContext().getResources();
        final String key = res.getString(R.string.pref_bodyheight_key);
        final String height = prefs.getString(key, null);
        if (TextUtils.isEmpty(height)) {
            final int defaultValue = res
                    .getInteger(R.integer.pref_bodyheight_default);
            // Save the default value
            prefs.edit().putString(key, "" + defaultValue).commit();
            return (defaultValue - 30) / 100.0;
        } else {
            final double bodyHeight = Integer.parseInt(height);
            return (bodyHeight - 30) / 100.0;
        }
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
     * @author vkochno & burghardt
     * 
     * @param point
     *            point to be testet
     * @return result true if the point is in the red marked area
     */
    public boolean overHorizont(Point point) {
        if (point.getX() < 0 || point.getX() > mMeasuredWidth
                || point.getY() < 0 || point.getY() > mMeasuredHeight) {
            return true;
        }
        if (bitmap == null) {
            this.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(this.getDrawingCache());
            this.setDrawingCacheEnabled(false);
        }
        if (bitmap.getPixel((int) point.getX(), (int) point.getY()) == Color.TRANSPARENT
                || bitmap.getPixel((int) point.getX(), (int) point.getY()) == paint
                        .getColor()) {
            return false;
        }
        return true;
    }

    public boolean isSkylook() {
        return skylook;
    }

}