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

import android.location.Location;

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
    private final static String TAG = "PointToWorldCoords";
    /** osmID for the node. */
    private int osmID = -1;
    /** height of the device from the ground. */
    private double height;
    /** */
    private int xAxis;
    /** */
    private int yAxis;
    /** object of the TransFormationParamBean. */
    private TransformationParamBean tps;
    /** object of the deviceOrientation. */
    private DeviceOrientation deviceOrientation;

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
     * @param rotation
     *            rotation of the device
     * @return rotation List of Node
     */
    public List<Node> transform(List<Point> points, int rotation) {
        return this.transform(tps, deviceOrientation, points, rotation);
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
     * @param rotation
     *            rotation of the device
     * @return list of Node
     */
    public List<Node> transform(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, List<Point> points,
            int rotation) {
        this.tps = tps;
        final List<Node> nodes = new ArrayList<Node>();
        // get the set height
        this.height = tps.getHeight();
        Log.d(TAG, "TPS-DATA pic height: " + tps.getPhotoHeight() + " width: "
                + tps.getPhotoWidth() + " deviceHeight: " + tps.getHeight());
        for (Point point : points) {
            // change point coordinates to match the set coordinate system.
            point = this.changePixelCoordSystem(point, rotation);
            Log.i(TAG, "Point X:" + point.getX() + " Y: " + point.getY());
            // calculates local coordinates in meter first
            final double[] coord =
                    this.calculateCoordFromPoint(tps, deviceOrientation, point);
            Log.d(TAG, "Calculated local Coords:" + coord[0] + "  " + coord[1]);
            // transforms local coordinates in global GPS-coordinates set to.
            // Node.
            if (coord != null) {
                final Node node = calculateGPSPoint(tps.getLocation(), coord);
                Log.d(TAG,
                        "Calculated Lat: " + node.getLat() + " Lon: "
                                + node.getLon());
                nodes.add(node);
            }
        }
        return nodes;
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
        final double pixelpitch =
                this.calculateAngleFromPixel(point.getX(), xAxis,
                        tps.getCameraMaxPitchAngle());
        // gets an angle for the point on the roll axis
        final double pixelroll =
                -calculateAngleFromPixel(point.getY(), yAxis,
                        tps.getCameraMaxRotationAngle());
        final double pitch = -deviceOrientation.getPitch();
        final double roll = -deviceOrientation.getRoll();
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
        finalVector[0] =
                vector2[0] * Math.cos(roll) - vector2[1] * Math.sin(pitch)
                        * Math.sin(roll) + vector2[2] * Math.cos(pitch)
                        * Math.sin(roll);
        finalVector[1] =
                vector2[0]
                        * Math.sin(roll)
                        * Math.sin(pitch)
                        + vector2[1]
                        * (Math.cos(pitch) * Math.cos(pitch)
                                * (1 - Math.cos(roll)) + Math.cos(roll))
                        + vector2[2] * Math.cos(pitch) * Math.sin(pitch)
                        * (1 - Math.cos(roll));
        finalVector[2] =
                -vector2[0]
                        * Math.cos(pitch)
                        * Math.sin(roll)
                        + vector2[1]
                        * Math.sin(pitch)
                        * Math.cos(pitch)
                        * (1 - Math.cos(roll))
                        + vector2[2]
                        * (Math.sin(pitch) * Math.sin(pitch)
                                * (1 - Math.cos(roll)) + Math.cos(roll));
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
        // Rotate Vector with azimuth (z is fix))
        Log.d(TAG, "AZIMUTH: " + azimuth);
        coord[0] =
                ((tempXX * Math.cos(azimuth)) - (tempYY * Math.sin(azimuth)));
        coord[1] =
                ((tempXX * Math.sin(azimuth)) + (tempYY * Math.cos(azimuth)));
        coord[2] = 0;
        return coord;
    }

    /**
     * Calculates the angle altered by the given pixel.
     * 
     * @param pixel
     *            one coordinate of the pixel
     * @param axis
     *            the axis on which the angle is altered
     * @param maxAngle
     *            maximum camera angle
     * @return altered Angle
     */
    public double calculateAngleFromPixel(double pixel, double axis,
            double maxAngle) {
        if ((pixel - (axis / 2)) == 0) {
            return 0;
        }
        final double percent = (2 * pixel - axis) / axis;
        final double z = Math.sin(maxAngle / 2);
        final double angle = Math.asin(z * percent);
        return angle;
    }

    /**
     * calculates Node (with latitude and longitude) from coordinates in a local
     * system and the current Location.
     * 
     * @param location
     *            current location of the device
     * @param coord
     *            coordinate of the point in the local system
     * @return A Node with latitude and longitude
     */
    public Node calculateGPSPoint(Location location, double[] coord) {
        final double radius = 6371004.0;
        final double lat = Math.toRadians(location.getLatitude());
        final double lon = Math.toRadians(location.getLongitude());
        // calculate the length of the current latitude line with the earth
        // radius
        double lonLength = radius * Math.cos(lat);
        lonLength = lonLength * 2 * Math.PI;
        // add to the current latitude the distance of the coordinate
        double lon2 = lon + Math.toRadians((coord[0] * 360) / lonLength);
        // fix the skip from -PI to +PI for the longitude
        lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
        // calculate the length of the current longitude line with the earth
        // radius
        final double latLength = radius * 2 * Math.PI;
        // add to the current Longitude the distance of the coordinate
        double lat2 = lat + Math.toRadians((coord[1] * 360) / latLength);
        /*
         * if (lon2 > (Math.PI/4)){ lon2 = (Math.PI/2) - lon2; } if (lon2 <
         * (-Math.PI/4)){ lon2 = -(Math.PI/2) + lon2; }
         */
        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);
        // create a new Node with the latitude and longitude values
        final Node node = new Node(osmID, lat2, lon2);
        osmID--;
        return node;
    }

    /**
     * Changes the point coordinates on the device coordinate system depending
     * on the device rotation.
     * 
     * @author sbollen
     * @param point
     *            the drawn point
     * @param rotation
     *            the rotation of the device at the state of drawing
     * @return the new calculated point
     */
    public Point changePixelCoordSystem(Point point, int rotation) {
        Point returnpoint;
        if (point != null) {
            if (rotation == 0) {
                Log.d(TAG, "Device orientation was portrait");
                // device was in portrait mode
                xAxis = tps.getPhotoHeight();
                yAxis = tps.getPhotoWidth();
                returnpoint = new Point(xAxis - point.getY() + 1, yAxis - point.getX()
                        + 1);
            } else if (rotation == 1) {
                Log.d(TAG, "Device orientation was landscape counter-clockwise");
                // device was in landscape mode and the home-button to the right
                xAxis = tps.getPhotoWidth();
                yAxis = tps.getPhotoHeight();
                returnpoint = new Point(xAxis - point.getX() + 1, point.getY());
            } else if (rotation == 3) {
                Log.d(TAG, "Device orientation was landscape clockwise");
                // device was in landscape mode and the home-button to the left
                xAxis = tps.getPhotoWidth();
                yAxis = tps.getPhotoHeight();
                returnpoint = new Point(point.getX(), yAxis - point.getY() + 1);
            } else {
                Log.wtf(TAG, "No device orientation identifiable!!", null);
                xAxis = tps.getPhotoHeight();
                yAxis = tps.getPhotoWidth();
                returnpoint = point;
            }
            return returnpoint;
        } else {
            Log.d(TAG, "No point to change");
            return null;
        }
    }

    /**
     * @return Size of xAxis
     */
    public int getxAxis() {
        return xAxis;
    }

    /**
     * @param xAxis
     *            Size of xAxis
     */
    public void setxAxis(int xAxis) {
        this.xAxis = xAxis;
    }

    /**
     * @return Size of yAxis
     */
    public int getyAxis() {
        return yAxis;
    }

    /**
     * @param yAxis
     *            Size of yAxis
     */
    public void setyAxis(int yAxis) {
        this.yAxis = yAxis;
    }
}
