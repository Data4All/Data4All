/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

    /** Logger tag for this class */
    final static String TAG = "PointToWorldCoords";

    /** osmID for the node */
    private int osmID = -1;

    // TODO to remove with the new data models
    private static int osmVersion = 1;

    /** height of the device from the ground */
    private double height = 0;

    /** */
    private int xAxis = 0;

    /** */
    private int yAxis = 0;

    /** object of the TransFormationParamBean */
    private TransformationParamBean tps;

    /** object of the deviceOrientation */
    private DeviceOrientation deviceOrientation;

    public PointToCoordsTransformUtil() {
    }

    /**
     * Constructor, which set some necessary data.
     * 
     * @param object
     *            of TranformParamBean
     * @param object
     *            of DeviceOrientation
     */
    public PointToCoordsTransformUtil(TransformationParamBean tps,
            DeviceOrientation deviceOrientation) {
        this.tps = tps;
        this.deviceOrientation = deviceOrientation;
    }

    /**
     * Calls transform with saved informations.
     * 
     * @param List
     *            of Point
     * @return List of Node
     */
    public List<Node> transform(List<Point> points, int rotation) {
        return transform(tps, deviceOrientation, points, rotation);
    }

    /**
     * transforms a list of points in a list of Nodes.
     * 
     * @param object
     *            of TransformParamBean
     * @param object
     *            of DeviceOrientation
     * @param points
     *            list of points which have to be calculated
     * @param rotation
     *            rotation of the device
     * @return list of Node
     */
    public List<Node> transform(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, List<Point> points,
            int rotation) {

        List<Node> nodes = new ArrayList<Node>();
        this.height = tps.getHeight(); // get the set height

        Log.d(TAG, "TPS-DATA pic height: " + tps.getPhotoHeight() + " width: "
                + tps.getPhotoWidth() + " deviceHeight: " + tps.getHeight());

        for (Point point : points) {

            // change point coordinates to match the set coordinate system
            point = changePixelCoordSystem(point, rotation);

            Log.i(TAG, "Point X:" + point.getX() + " Y: " + point.getY());

            // calculates local coordinates in meter first
            double[] coord = calculateCoordFromPoint(tps, deviceOrientation,
                    point);
            Log.d(TAG, "Calculated local Coords:" + coord[0] + "  " + coord[1]);
            // transforms local coordinates in global GPS-coordinates set to
            // Node
            if (coord != null) {
                Node node = calculateGPSPoint(tps.getLocation(), coord);

                Log.d(TAG,
                        "Calculated Lat: " + node.getLat() + " Lon: "
                                + node.getLon());
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * opens calculate4thPoint with saved information.
     * 
     * @param points
     *            list of the first already drawn points
     * @param rotation
     *            rotation of the device at the state of drawing
     * @return the 4th point
     */
    public Point calculate4thPoint(List<Point> points, int rotation) {
        return calculate4thPoint(tps, deviceOrientation, points, rotation);
    }

    /**
     * calculates a 4th Point (for houses etc.) with 3 given Points.
     * 
     * @param tps
     *            object of the TransformationParamBean
     * @param deviceOrientation
     *            of the DeviceOrientation
     * @param points
     *            list of the first three already drawn points
     * @param rotation
     *            rotation of the device at the state of drawing
     * @return the 4th Point
     */
    public Point calculate4thPoint(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, List<Point> points,
            int rotation) {
        if (points.size() != 3) {
            return null;
        }
        final List<double[]> coords = new ArrayList<double[]>();
        this.height = tps.getHeight();
        for (Point point : points) {
            point = changePixelCoordSystem(point, rotation);
            coords.add(calculateCoordFromPoint(tps, deviceOrientation, point));
        }
        final double[] coord = add4Point(coords);
        return calculatePointFromCoords(tps, deviceOrientation, coord);
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
        double pixelpitch = calculateAngleFromPixel(point.getX(), xAxis,
                tps.getCameraMaxPitchAngle());
        // gets an angle for the point on the roll axis
        double pixelroll = -calculateAngleFromPixel(point.getY(), yAxis,
                tps.getCameraMaxRotationAngle());
        double pitch = -deviceOrientation.getPitch();
        double roll = -deviceOrientation.getRoll();

        double[] vector = new double[3];
        // without any rotation (faced to the ground and the north)
        vector[2] = -1;
        vector[1] = Math.tan(pixelpitch);
        vector[0] = Math.tan(pixelroll);
        // rotate around x-axis with pitch
        double[] vector2 = new double[3];
        vector2[0] = vector[0];
        vector2[1] = vector[1] * Math.cos(pitch) - vector[2] * Math.sin(pitch);
        vector2[2] = vector[1] * Math.sin(pitch) + vector[2] * Math.cos(pitch);
        // rotate around line through origin with pitch angle
        double[] finalVector = new double[3];
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
        // TODO remove when the horizon calculation is ready
        if (finalVector[2] >= 0) {
            return null;
        }

        // collides vector with the xy-plane
        double tempXX = finalVector[0] * (height / -finalVector[2]);
        double tempYY = finalVector[1] * (height / -finalVector[2]);
        final double[] coord = new double[3];
        // Rotate Vector with azimuth (z is fix))
        Log.d(TAG, "AZIMUTH: " + azimuth);
        coord[0] = ((tempXX * Math.cos(azimuth)) - (tempYY * Math.sin(azimuth)));
        coord[1] = ((tempXX * Math.sin(azimuth)) + (tempYY * Math.cos(azimuth)));
        coord[2] = 0;
        return coord;
    }

    /**
     * Calculates the fourth point in dependence of the first three points of
     * the given list.
     * 
     * @param coords
     *            list of coordinates of the first three points
     * @return the 4th Point
     */
    private double[] add4Point(List<double[]> coords) {
        final double[] a = coords.get(0);
        final double[] b = coords.get(1);
        final double[] c = coords.get(2);
        final double[] coord = new double[2];
        coord[0] = a[0] + (c[0] - b[0]);
        coord[1] = a[1] + (c[1] - b[1]);
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
        double percent = (2 * pixel - axis) / axis;
        double z = Math.sin(maxAngle / 2);
        double angle = Math.asin(z * percent);
        return angle;

    }

    /**
     * calculates the point from the coordinates in the local coordinate system.
     * e.g. for the camera (augmented reality)
     * 
     * @param tps
     *            object of the TransformationParamBean
     * @param deviceOrientation
     *            object of the DeviceOrientation
     * @param coord
     *            double[] with coordinates in a local system
     * @return calculated point
     */
    public Point calculatePointFromCoords(TransformationParamBean tps,
            DeviceOrientation deviceOrientation, double[] coord) {
        if (coord[2] == -1) {
            return null; // TODO check whether it is necessary
        }
        double pitch = deviceOrientation.getPitch();
        double roll = -deviceOrientation.getRoll();
        double azimuth = deviceOrientation.getAzimuth();
        // rotates the vector with azimuth
        double[] vector = new double[3];
        vector[0] = ((coord[0] * Math.cos(azimuth)) - (coord[1] * Math
                .sin(azimuth)));
        vector[1] = ((coord[0] * Math.sin(azimuth)) + (coord[1] * Math
                .cos(azimuth)));
        vector[2] = -tps.getHeight();
        // rotate around line through origin with pitch angle
        double[] vector2 = new double[3];
        vector2[0] = vector[0] * Math.cos(roll) - vector[1] * Math.sin(pitch)
                * Math.sin(roll) + vector[2] * Math.cos(pitch) * Math.sin(roll);
        vector2[1] = vector[0]
                * Math.sin(roll)
                * Math.sin(pitch)
                + vector[1]
                * (Math.cos(pitch) * Math.cos(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll)) + vector[2] * Math.cos(pitch)
                * Math.sin(pitch) * (1 - Math.cos(roll));
        vector2[2] = -vector[0]
                * Math.cos(pitch)
                * Math.sin(roll)
                + vector[1]
                * Math.sin(pitch)
                * Math.cos(pitch)
                * (1 - Math.cos(roll))
                + vector[2]
                * (Math.sin(pitch) * Math.sin(pitch) * (1 - Math.cos(roll)) + Math
                        .cos(roll));

        // rotate around x-axis with pitch
        double[] finalVector = new double[3];
        finalVector[0] = vector2[0];
        finalVector[1] = vector2[1] * Math.cos(-pitch) - vector2[2]
                * Math.sin(-pitch);
        finalVector[2] = vector2[1] * Math.sin(-pitch) + vector2[2]
                * Math.cos(-pitch);

        double x = Math.atan(-finalVector[1] / finalVector[2]);
        double y = Math.atan(finalVector[0] / finalVector[2]);
        x = Math.sin(x);
        x = x / Math.sin(tps.getCameraMaxPitchAngle() / 2);
        x = (xAxis / 2) + (x * (xAxis / 2));

        y = Math.sin(y);
        y = y / Math.sin(tps.getCameraMaxRotationAngle() / 2);
        y = (yAxis / 2) + (y * (yAxis / 2));
        /*
         * double percent = (2*pixel-width) / width; double z =
         * Math.sin(maxAngle/2); double angle = Math.asin(z * percent); return
         * angle;
         */

        // multiply with the width and height of the photo TODO what is this
        // for?
        float xx = (float) (Math.round(x));
        float yy = (float) (Math.round(y));
        Point point = new Point(xx, yy);
        return point;
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
        double radius = 6371004.0;
        double lat = Math.toRadians(location.getLatitude());
        double lon = Math.toRadians(location.getLongitude());

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
        double latLength = radius * 2 * Math.PI;
        // add to the current Longitude the distance of the coordinate
        double lat2 = lat + Math.toRadians((coord[1] * 360) / latLength);
        /*
         * if (lon2 > (Math.PI/4)){ lon2 = (Math.PI/2) - lon2; } if (lon2 <
         * (-Math.PI/4)){ lon2 = -(Math.PI/2) + lon2; }
         */
        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);

        // create a new Node with the latitude and longitude values
        Node node = new Node(osmID, osmVersion, lat2, lon2);
        osmID--;
        return node;
    }

    /**
     * Calculates the coordinate in a local coordinate system depending on the
     * current location from a given node.
     * 
     * @param location
     *            the current location of the device
     * @param node
     *            the given node
     * @return double[] with coordinates in a local system
     */
    public double[] calculateCoordFromGPS(Location location, Node node) {
        double radius = 6371004.0;
        double lat = Math.toRadians(node.getLat() - location.getLatitude());
        double lon = Math.toRadians(node.getLon() - location.getLongitude());
        double localLat = Math.toRadians(location.getLatitude());

        // calculate the Length of the current Latitude with the earth Radius
        double latLength = radius * Math.cos(localLat);

        double[] coord = new double[3];
        coord[0] = latLength * lat;

        double lonLength = radius;
        coord[1] = lonLength * lon;
        coord[2] = 0;

        return coord;
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
        // change the point value in dependency of the rotated coordinate
        // system for drawing
        if (rotation == 0) {
            Log.d(TAG, "Device orientation was portrait");
            // device was in portrait mode
            xAxis = tps.getPhotoHeight();
            yAxis = tps.getPhotoWidth();
            return new Point((tps.getPhotoHeight() - point.getY() + 1),
                    (tps.getPhotoWidth() - point.getX() + 1));
        } else if (rotation == 1) {
            Log.d(TAG, "Device orientation was landscape counter-clockwise");
            // device was in landscape mode and the home-button to the right
            xAxis = tps.getPhotoWidth();
            yAxis = tps.getPhotoHeight();
            return new Point((tps.getPhotoWidth() - point.getX() + 1),
                    (point.getY()));
        } else if (rotation == 3) {
            Log.d(TAG, "Device orientation was landscape clockwise");
            // device was in landscape mode and the home-button to the left
            xAxis = tps.getPhotoWidth();
            yAxis = tps.getPhotoHeight();
            return new Point((point.getX()), (tps.getPhotoHeight()
                    - point.getY() + 1));
        } else {
            Log.wtf(TAG, "No device orientation identifiable!!", null);
            xAxis = tps.getPhotoHeight();
            yAxis = tps.getPhotoWidth();
            return point;
        }
    }
}
