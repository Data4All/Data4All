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
package io.github.data4all.util;

import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * This class uses the orientation of the phone, the pixel of the drawn points
 * and the camera parameters to calculate the distance between the phone and the
 * object in a 2D system. After that the location of the point in world
 * coordinates is calculated.
 * 
 * @author burghardt
 * @version 0.1
 *
 */
public class PointToCoordsTransformUtil {
    /** Logger tag for this class. */
    private static final String TAG = "PointToWorldCoords";
    /** height of the device from the ground. */
    private double height;
    /** object of the TransFormationParamBean. */
    private TransformationParamBean tps;
    /** object of the deviceOrientation. */
    private DeviceOrientation deviceOrientation;
    /**
     * 2 Vectors for the x- and the y-axe
     */
    private static final double[] xaxe = { 1, 0, 0, };
    private static final double[] yaxe = { 0, 1, 0, };

    public PointToCoordsTransformUtil() {
    }

    /**
     * Constructor, which set some necessary data.
     * 
     * @param tps
     *            object of TranformParamBean
     * @param deviceOrientation
     *            object of DeviceOrientation
     */
    public PointToCoordsTransformUtil(TransformationParamBean tps,
            DeviceOrientation deviceOrientation) {
        this.tps = tps;
        this.deviceOrientation = deviceOrientation;
    }

    /**
     * Calls transform with saved informations.
     * 
     * @param points
     *            List of Point
     * @return rotation List of Node
     */
    public List<Node> transform(List<Point> points) {
        return this.transform(tps, deviceOrientation, points);
    }

    /**
     * transforms a list of points in a list of Nodes.
     * 
     * @param tps
     *            object of TransformParamBean
     * @param deviceOrientation
     *            object of DeviceOrientation
     * @param points
     *            list of points which have to be calculated
     * @return list of Node
     */
    public List<Node> transform(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, List<Point> points) {
        this.tps = tps;

        Log.i(TAG, tps.toString());
        Log.i(TAG, deviceOrientation.toString());
        final List<Node> nodes = new ArrayList<Node>();
        Log.d(TAG,
                "Orientation: "
                        + Math.toDegrees(deviceOrientation.getAzimuth())
                        + " ; " + Math.toDegrees(deviceOrientation.getPitch())
                        + " ; " + Math.toDegrees(deviceOrientation.getRoll()));
        Log.d(TAG, tps.toString());

        // get the set height
        this.height = tps.getHeight();
        Log.d(TAG, "TPS-DATA pic height: " + tps.getPhotoHeight() + " width: "
                + tps.getPhotoWidth() + " deviceHeight: " + tps.getHeight());
        for (Point point : points) {
            // point = new Point(xAxis - point.getY(), yAxis - point.getX());
            Log.i(TAG, "Point X:" + point.getX() + " Y: " + point.getY());

            // calculates local coordinates in meter first
            final double[] coord = this.calculateCoordFromPoint(tps,
                    deviceOrientation, point);
            Log.d(TAG, "Calculated local Coords:" + coord[0] + "  " + coord[1]);
            // transforms local coordinates in global GPS-coordinates set to.
            // Node.
            if (coord != null) {
                final Node node = MathUtil.calculateGPSPoint(tps.getLocation(),
                        coord);
                Log.d(TAG,
                        "Calculated Lat: " + node.getLat() + " Lon: "
                                + node.getLon());
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * calculates with 3 Points and the given deviceorientation and tps
     * informations the 4th point of a building
     * 
     * @param tps
     *            object of TransformParamBean
     * @param deviceOrientation
     *            object of DeviceOrientation
     * @param points
     *            list of points which have to be calculated
     * @return a point
     */
    public Point fourthBuildingPoint(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, List<Point> points) {
        this.deviceOrientation = deviceOrientation;
        this.tps = tps;
        return fourthBuildingPoint(points);
    }

    /**
     * calculates with 3 Points and the given deviceorientation and tps
     * informations the 4th point of a building
     * 
     * @param points
     *            list of exact 3 points to calculate the 4th
     * @return list of Node
     */
    public Point fourthBuildingPoint(List<Point> points) {
        final List<double[]> coords = new ArrayList<double[]>();
        // float saveAzimuth = deviceOrientation.getAzimuth();
        // deviceOrientation.setAzimuth(0);
        Log.d(TAG, "TPS-DATA pic height: " + tps.getPhotoHeight() + " width: "
                + tps.getPhotoWidth() + " deviceHeight: " + tps.getHeight());
        if (points.size() != 3) {
            Log.w(TAG,
                    "The given amount of Points was not 3, can not calculate 4th building point");
            return null;
        } else {
            for (Point point : points) {
                // change point coordinates to match the set coordinate system.
                Log.i(TAG, "Point X:" + point.getX() + " Y: " + point.getY());
                // calculates local coordinates in meter first
                final double[] coord = this.calculateCoordFromPoint(tps,
                        deviceOrientation, point);
                Log.d(TAG, "Calculated local Coords:" + coord[0] + "  "
                        + coord[1]);
                coords.add(coord);
            }
        }
        final double[] coord = MathUtil.calcFourthCoord(coords);
        return coordToPixel(coord);
    }

    /**
     * Calculates a coordinate to a Pixel
     * 
     * @param coord
     *            A geoPoint transfered to a local coordinate System
     * @return a Point a Pixel to draw on the device
     */
    public Point coordToPixel(double[] coord) {
        double[] vector = new double[3];
        final double azimuth = deviceOrientation.getAzimuth();
        vector[0] = ((coord[0] * Math.cos(azimuth)) - (coord[1] * Math
                .sin(azimuth)));
        vector[1] = ((coord[0] * Math.sin(azimuth)) + (coord[1] * Math
                .cos(azimuth)));
        vector[2] = -tps.getHeight();
        // Rotate to the device coordinat-system
        final double pitch = deviceOrientation.getPitch();
        final double[] vector2 = MathUtil.rotate(vector, xaxe, pitch);
        final double roll = deviceOrientation.getRoll();
        final double[] vector3 = MathUtil.rotate(vector2, yaxe, -roll);

        if (vector3[2] >= 0) {
            final double length = Math.sqrt(vector3[0] * vector3[0]
                    + vector3[1] * vector3[1] + vector3[2] * vector3[2]);
            double multi = 1 - vector3[2] / length;
            multi = multi * multi
                    * (tps.getPhotoHeight() + tps.getPhotoWidth());
            multi = multi * multi;
            return new Point((float) (vector3[0] * multi),
                    (float) (-vector3[1] * multi));
        }

        // calculate the pitch- and roll-angles.
        final double horizonPitch = Math.atan(vector3[1] / vector3[2]);
        final double horizonRoll = -Math.atan(vector3[0] / vector3[2]);

        // calculate a point on the horizont vertical to the mid of the display.
        Log.i(TAG, tps.toString());
        final float x = tps.getPhotoWidth()
                / 2
                + MathUtil.calculatePixelFromAngle(horizonRoll,
                        tps.getPhotoWidth(),
                        tps.getCameraMaxVerticalViewAngle());
        final float y = tps.getPhotoHeight()
                / 2
                + MathUtil.calculatePixelFromAngle(horizonPitch,
                        tps.getPhotoHeight(),
                        tps.getCameraMaxHorizontalViewAngle());
        return new Point(x, y);
    }

    /**
     * Transfers GPSPoints to Points on the Display
     * 
     * @param nodes
     *            a GeoPoint
     * @param tps
     *            Transformationparambean
     * @param deviceOrientation
     *            the Orientation of the device
     * @return List of Points a List of Points to draw on Device
     */
    public List<Point> calculateNodesToPoint(List<Node> nodes,
            TransformationParamBean tps, DeviceOrientation deviceOrientation) {
        final List<Point> points = new ArrayList<Point>();
        for (Node node : nodes) {
            points.add(calculateNodeToPoint(node, tps, deviceOrientation));
        }
        return points;
    }

    /**
     * Transfers GPSPoints to Points on the Display
     * 
     * @param nodes
     *            a GeoPoint
     * @param tps
     *            Transformationparambean
     * @param deviceOrientation
     *            the Orientation of the device
     * @return List of Points a List of Points to draw on Device
     */
    public Point calculateNodeToPoint(Node node, TransformationParamBean tps,
            DeviceOrientation deviceOrientation) {
        this.tps = tps;
        this.deviceOrientation = deviceOrientation;
        final double[] coord = MathUtil.calculateCoordFromGPS(
                tps.getLocation(), node);
        return coordToPixel(coord);

    }

    /**
     * calculates local coordinates for a point with the orientation of the
     * device, the pixel and information of the camera.
     * 
     * @param tps
     *            object of TransformParamBean
     * @param deviceOrientationobject
     *            of DeviceOrientation
     * @param point
     *            object of Point
     * @return double[] with coordinates in a local system
     */
    public double[] calculateCoordFromPoint(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, Point point) {

        this.height = tps.getHeight();
        final double azimuth = -deviceOrientation.getAzimuth();
        // gets an angle for the point on the pitch axis
        final double pixelpitch = -MathUtil.calculateAngleFromPixel(
                point.getY(), tps.getPhotoHeight(),
                tps.getCameraMaxHorizontalViewAngle());
        // gets an angle for the point on the roll axis
        final double pixelroll = MathUtil.calculateAngleFromPixel(point.getX(),
                tps.getPhotoWidth(), tps.getCameraMaxVerticalViewAngle());
        final double pitch = -deviceOrientation.getPitch();
        final double roll = deviceOrientation.getRoll();

        final double[] vector = new double[3];
        // without any rotation (faced to the ground and the north)
        vector[2] = -1;
        vector[1] = Math.tan(pixelpitch);
        vector[0] = Math.tan(pixelroll);
        // rotate around x-axis with pitch
        final double[] vector2 = new double[3];
        vector2[0] = vector[0];
        vector2[1] = vector[1] * Math.cos(pitch) - vector[2] * Math.sin(pitch);
        vector2[2] = vector[1] * Math.sin(pitch) + vector[2] * Math.cos(pitch);
        // rotate around line through origin with pitch angle
        final double[] finalVector = new double[3];
        finalVector[0] = vector2[0] * Math.cos(roll) - vector2[1]
                * Math.sin(pitch) * Math.sin(roll) + vector2[2]
                * Math.cos(pitch) * Math.sin(roll);
        finalVector[1] = vector2[0]
                * Math.sin(roll)
                * Math.sin(pitch)
                + vector2[1]
                * (Math.cos(pitch) * Math.cos(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll)) + vector2[2] * Math.cos(pitch)
                * Math.sin(pitch) * (1 - Math.cos(roll));
        finalVector[2] = -vector2[0]
                * Math.cos(pitch)
                * Math.sin(roll)
                + vector2[1]
                * Math.sin(pitch)
                * Math.cos(pitch)
                * (1 - Math.cos(roll))
                + vector2[2]
                * (Math.sin(pitch) * Math.sin(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll));
        // returns null, if the vector points to the sky
        if (finalVector[2] >= 0) {
            Log.wtf(TAG,
                    "Vector is directed to the sky, cannot calculate coords",
                    null);
            return new double[3];
        }
        // collides vector with the xy-plane
        final double tempXX = finalVector[0] * (height / -finalVector[2]);
        final double tempYY = finalVector[1] * (height / -finalVector[2]);
        final double[] coord = new double[3];
        Log.i(TAG, "Coord: " + tempXX + " , " + tempYY);
        // Rotate Vector with azimuth (z is fix))
        Log.d(TAG, "AZIMUTH: " + azimuth);
        coord[0] = ((tempXX * Math.cos(azimuth)) - (tempYY * Math.sin(azimuth)));
        coord[1] = ((tempXX * Math.sin(azimuth)) + (tempYY * Math.cos(azimuth)));
        coord[2] = 0;
        Log.i(TAG, "Coordinates: " + coord[0] + " ; " + coord[1]);

        return coord;
    }

    public double calculateDistance(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, Point point) {
        double[] coord = calculateCoordFromPoint(tps, deviceOrientation, point);
        return Math.sqrt(coord[0] * coord[0] + coord[1] * coord[1]);
    }

}
